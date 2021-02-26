import java.util.*;
import java.io.PrintStream;

public class CoolGen {

  public CoolGen(CoolMap c, PrintStream s) {
    map = c;
    out = s;
  }

  public static CoolMap map;
  public PrintStream out;
  private boolean inLabel = false;
  private int INT_CLASS_TAG = 0; private int STR_CLASS_TAG = 0;

  public static final String GLOBAL = ".globl";
  public static final String WORD = ".word";
  public static final String ASCIIZ = ".asciiz";
  public static final String ALIGN = ".align";
  public static final String PROTOBJ = "%s_protObj";
  public static final String METHODTAB = "%s_methodTab";
  public static final String METHODREF = "%s.%s";
  public static final String ATTRREF = "%s_attr_%s";
  public static final String ATTRINIT = "%s_init";
  public static final String NO_MATCHING_CASE = "_case_abort";
  public static final String NULL_CASE = "_case_abort2";
  public static final String NULL_DISPATCH = "_dispatch_abort";
  public static final String EQ_TEST = "equality_test";

  // TODO: Dynamic frame size based on `let` count?
  public static final int LOCAL_SIZE = 4 * 10;

  public static String pop(String reg) {
    return String.format("%s # pop\n\t%s# pop", "lw " + reg + " ($sp)", "add $sp $sp 4");
  }
  public static String push(String reg) {
    return String.format("%s # push\n\t%s# push", "sub $sp $sp 4", "sw " + reg + " ($sp)");
  }
  public static String blockComment(String c) {
    return "\n\t# " + c;
  }
  public static String comment(String c) {
    return "# " + c;
  }
  public static void emitRegisterPreserve(PrintStream out) {
    emitPadded(new String[] {
        CoolGen.comment("preserve registers"),
        CoolGen.push("$a0"),
        CoolGen.push("$ra"),
        CoolGen.push("$fp"),
        CoolGen.push("$s1"),
        CoolGen.push("$s2"),
        CoolGen.push("$s3"),
        CoolGen.push("$s4"),
    }, out);
  }
  public static void emitFramePrologue(PrintStream out) {
    CoolGen.emitPadded(new String[] {
        CoolGen.comment("frame setup prologue"),
        "sub $sp $sp 4",
        "move $s4 $sp", // where locals being, incl.
        "sub $sp $sp " + CoolGen.LOCAL_SIZE, // where locals end, incl.
    }, out);
  }
  public static void emitFrameEpilogue(PrintStream out) {
    CoolGen.emitPadded(new String[] {
        CoolGen.comment("frame setup: conclusion"),
        "move $s1 $s4",
        "move $s3 $s4",
        "move $s2 $s4",
        "sub $s2 $s2 " + CoolGen.LOCAL_SIZE,
        "move $fp $s2",
        "sub $fp $fp 4", // $fp points to first arg 
        "sub $sp $sp 4" // seems to be necessary for builtins? Point stack to empty word
    }, out);
  }
  public static void emitFrameCleanup(PrintStream out) {
    CoolGen.emitPadded(new String[] {
      CoolGen.comment("teardown frame: restore to top of stack before call"),
      "move $sp $fp",
      "add $sp $sp 4", // Point to end of locals
      "add $sp $sp " + (CoolGen.LOCAL_SIZE + 4),
    }, out);
  }
  public static void emitRegisterRestore(PrintStream out) {
    CoolGen.emitPadded(new String[] {
      CoolGen.pop("$s4"),
      CoolGen.pop("$s3"),
      CoolGen.pop("$s2"),
      CoolGen.pop("$s1"),
      CoolGen.pop("$fp"),
      CoolGen.pop("$ra"),
    }, out);
  }
  public static void emitObjectCopy(String className, PrintStream out) {
    emitPadded(blockComment("call to Object.copy"), out);

    emitRegisterPreserve(out);

    emitFramePrologue(out);

    // no stack arguments for Object.copy

    emitPadded(new String[] {
      comment("frame setup: subject of dispatch"),
      "la $a0 " + String.format(PROTOBJ, className),
    }, out);

    emitFrameEpilogue(out);

    emitPadded("jal Object.copy", out);

    emitFrameCleanup(out);

    emitRegisterRestore(out);

    emitPadded(new String[] {
      pop("$t1"),
      push("$a0"),
      "move $a0, $t1",
    }, out);
  }
  public static void emitNewInt(PrintStream out) {
    Integer valOffset = (Integer) CoolGen.lookupObject(
      AbstractTable.idtable.lookup("Int"),
      AbstractTable.idtable.lookup("_val")
    )[1];

    emitPadded(new String[] {
    }, out);

    emitObjectCopy("Int", out);

    emitPadded(new String[]{       
      pop("$t2"),
      comment("get value of soon to be created Int"),
      pop("$t1"),

      comment("assign value to int object"),
      "sw $t1 " + valOffset + "($t2)",

      comment("push result"),
      push("$t2"),
    }, out);
  }
  public static void emitObjectDeref(PrintStream out) {
    emitPadded(new String[] {
      comment("dereferencing pointer to Int"),
      pop("$t1"),
      "lw $t1 ($t1)",
      push("$t1")
    }, out);
  }
  public static void emitNewLocal(PrintStream out) {
    // TODO: Handle when $s1 encroaches on $s2 (out of frame space)
    emitPadded(new String[] {
      pop("$t1"),
      "sw $t1 ($s1)",
      "sub $s1 $s1 4",
    }, out);
  }
  public static void replaceLocal(PrintStream out) {
    emitPadded(new String[] {
      pop("$t1"),
      "sw $t1 4($s1)", // $s1 is the word directly above last local
    }, out);
  }
  public static void emitLabel(String s, PrintStream out) {
    out.printf("%s %s\n", GLOBAL, s);
    out.printf("%s:\n", s);
  }

  public static Object[] lookupObject(AbstractSymbol className, AbstractSymbol symName) {
    CoolMap.ClassTable symbols = map.programSymbols.get(className);
    CoolMap.ObjectData o = (CoolMap.ObjectData) symbols.objects.lookup(symName);

    // Assuming that current self is always $a0, args are from $fp, and locals are from $s1
    Object[] res = new Object[]{ "$a0", 0 };
    if (symName.equals(TreeConstants.self)) {
      res[1] = 0;
    } 
    else if (o == null) {
      AbstractSymbol next = map.classGraph.get(className);
      if (next == null) {
        throw new RuntimeException(String.format("%s cannot be found anywhere", symName));
      }
      return lookupObject(next, symName);
    }
    else {
      switch (o.sym) {
        case LOCAL: {
          res[0] = "$s3";
          res[1] = -4 * o.offset;
          break;
        }
        case ARG: {
          res[0] = "$fp";
          // $fp is fixed at bottom of frame, requiring negative offsets
          res[1] = -1 * o.offset;
          break;
        }
        default: {
          // We need the absolute offset for the attribute in cases of non-inherited attributes
          // o.offset is relative to the class it originates in and will collide with an inherited attribute at same position
          res[1] = map.classAttributes.get(className).get(symName).offset;
          break;
        }
      }
    }
    return res;
  }
  public static CoolMap.MethodData lookupMethod(AbstractSymbol className, AbstractSymbol symName) {
    Iterator<AbstractSymbol> i = CoolMap.getAncestry(className).descendingIterator();
    while (i.hasNext()) {
      CoolMap.MethodData m = CoolMap.classMethods.get(i.next()).get(symName);
      if (m != null) {
        return m;
      }
    }
    return null;
  }
  public static CoolMap.SymbolType lookupSymbolType(AbstractSymbol className, AbstractSymbol symName) {
    CoolMap.ClassTable symbols = map.programSymbols.get(className);
    CoolMap.ObjectData o = (CoolMap.ObjectData) symbols.objects.lookup(symName);
    return o.sym;
  }
  public static void newObjectScope(AbstractSymbol className) {
    CoolMap.ClassTable symbols = map.programSymbols.get(className);
    symbols.objects.enterScope();
  }
  public static void endObjectScope(AbstractSymbol className) {
    CoolMap.ClassTable symbols = map.programSymbols.get(className);
    symbols.localCount--;
    symbols.objects.exitScope();
  }
  public static void addObject(AbstractSymbol className, AbstractSymbol symName, AbstractSymbol type) {
    CoolMap.ClassTable symbols = map.programSymbols.get(className);
    symbols.objects.addId(symName, map.new ObjectData(type, CoolMap.SymbolType.LOCAL, symbols.localCount++));
  }
  public static boolean initByPrototype(AbstractSymbol className) {
    return className.equals(TreeConstants.Int) ||
      className.equals(TreeConstants.Str) ||
      className.equals(TreeConstants.Bool);
  }

  public static void emitPadded(String[] s, PrintStream out) {
    for (String a : s) {
      out.printf("\t%s\n", a);
    }
  }
  public static void emitPadded(String s, PrintStream out) {
    out.printf("\t%s\n", s);
  }
  private void emit(String s) {
    out.printf((inLabel ? "\t%s\n" : "%s\n"), s);
  }
  private void emit(String size, String s) {
    out.printf((inLabel ? "\t%s %s\n" : "%s %s\n"), size, s);
  }
  private void emit(String size, int s) {
    out.printf((inLabel ? "\t%s %d\n" : "%s %d\n"), size, s);
  }
  private void emitLabel(String s) {
    out.printf("%s %s\n", GLOBAL, s);
    out.printf("%s:\n", s);
    inLabel = true;
  }
  private void endLabel() {
    inLabel = false;
  }

  private int objectSize(AbstractSymbol className) {
    int size = 3;

    for (AbstractSymbol ancestor : map.getAncestry(className)) {
      size += map.classAttributes.get(ancestor).size();
    }
    return size;
  }
  private void intDefault() {
    emit(WORD, "0 # int _val default");
  }
  private void boolDefault() {
    emit(WORD, "0 # bool _val default");
  }
  private void stringDefault(AbstractSymbol attrName) {
    switch (attrName.toString()) {
      case "_val": {
        emit(WORD, String.format(PROTOBJ, "Int"));
        return;
      }
      case "_str_field": {
        emit(ASCIIZ, "\"\"");
        emit(ALIGN, "2");
        return;
      }
    }
  }
  private void emitDefault(AbstractSymbol type, AbstractSymbol attrName) {
    /*
     Assuming a copy-on-write setup where for default
     assignment to below builtin types, we simply point to 
     the prototype (since they have the right defaults)

     Then when it's time to actually assign
     an expression to the attribute, we generate code to create 
     an appropriate object and point to that
    */
    switch (type.toString()) {
      case "Int": {
        emit(WORD, String.format(PROTOBJ, "Int"));
        return;
      }
      case "Bool": {
        emit(WORD, "bool_const0");
        return;
      }
      case "String": {
        emit(WORD, String.format(PROTOBJ, "String"));
        return;
      }
      default: {
        emit(WORD, String.format("0 # null ptr to %s", type));
      }
    }
  }

  private void writeClassNames() {

    // For each class, create a COOL Int containing it's name's length
    for (AbstractSymbol className : map.classtags) {
      emit(WORD, "-1");
      emitLabel(String.format("%s_name_length", className));
      emit(WORD, map.classtags.indexOf(TreeConstants.Int));
      emit(WORD, 4); 
      emit(WORD, "Int_methodTab"); 
      emit(WORD, className.toString().length());
      endLabel();
    }

    // Now for each class, create a COOL String containing it's name and referring to the length
    for (AbstractSymbol className : map.classtags) {
      emit(WORD, "-1");
      emitLabel(String.format("%s_name", className));
      emit(WORD, map.classtags.indexOf(TreeConstants.Str));
      emit(WORD, 7); 
      emit(WORD, "String_methodTab"); 
      emit(WORD, String.format("%s_name_length", className));
      emit(ASCIIZ, String.format("\"%s\"", className));
      emit(ALIGN, "2");
      endLabel();
    }
  }

  private void writeClassTags() {
    for (int i = 0; i < map.classtags.size(); i++) {
      AbstractSymbol className = map.classtags.get(i);
      switch (className.toString()) {
        case "Int": {
          INT_CLASS_TAG = i;
          emitLabel("_int_tag");
          break;
        }
        case "Bool": {
          emitLabel("_bool_tag");
          break;
        }
        case "String": {
          STR_CLASS_TAG = i;
          emitLabel("_string_tag");
          break;
        }
        default: {
          emitLabel(String.format("_%s_tag", className));
        }
      }
      emit(WORD, i);
      endLabel();
    }
  }

  private void writeClassTab() {
    emitLabel("class_nameTab");
    for (AbstractSymbol className : map.classtags) {
      emit(WORD, String.format("%s_name", className));
    }
    endLabel();
  }

  private void writeDispatchTab(AbstractSymbol className, LinkedList<AbstractSymbol> ancestry) {
    Map<AbstractSymbol, CoolMap.MethodData> methods = new HashMap<AbstractSymbol, CoolMap.MethodData>();

    // Collect the set of methods inherited/overidden
    for (AbstractSymbol ancestor : ancestry) {

      // First, overwrite any redefined inherited methods
      Map<AbstractSymbol,  CoolMap.MethodData> currentMethods = map.classMethods.get(ancestor);
      for (CoolMap.MethodData inherited : methods.values()) {
        CoolMap.MethodData reDefined = currentMethods.get(inherited.name);
        if (reDefined != null) {
          reDefined.order = inherited.order;
          methods.put(inherited.name, reDefined);
        }
      }

      // Then, add any newly defined methods
      int lastOrder = methods.size();
      for (CoolMap.MethodData current : currentMethods.values()) {
        if (methods.get(current.name) == null) {
          current.order = lastOrder++;
          methods.put(current.name, current);
        }
      }
    }

    CoolMap.MethodData[] methodList = methods.values().toArray(new CoolMap.MethodData[] {});
    Arrays.sort(methodList);

    // Build a table, associating each inherited method to the address of the definition
    // Since methods can be shared across descendants, we need this table
    emitLabel(String.format(METHODTAB, className));
    for (CoolMap.MethodData m : methodList) {
      // Assign an offset for this method. Will need this for dispatching to it later
      // Each method pointer takes 4 bytes
      m.offset = 4 * m.order;

      emitLabel(String.format("%s_method_%s", className, m.name));
      emit(WORD, String.format(METHODREF, m.className, m.name));
      endLabel();
    }
    endLabel();
  }

  private void writeDispatchTabTab() {
    emitLabel("methodTabTab");
    for (AbstractSymbol className : map.classtags) {
      emit(WORD, String.format(METHODTAB, className));
    }
    endLabel();
  }

  private void writeBoolTrue() {
    AbstractSymbol className = TreeConstants.Bool;

    emit(WORD, -1);
    emitLabel("bool_const1");
    emit(WORD, String.format("%d # classtag", map.classtags.indexOf(className)));
    emit(WORD, String.format("%d # size", objectSize(className)));
    emit(WORD, String.format(METHODTAB, className));

    // Bool only inherits from Object with has no attributes
    // Bool only has the _val attribute
    CoolMap.AttributeData a = map.classAttributes.get(className).get(TreeConstants.val);
    emit(WORD, "1 # bool_const1 is true");
    a.offset = 12;
  }

  public void layoutStaticData() {
    emit("##### START DATA #####");
    emit(".data");

    writeClassNames();
    writeClassTags();
    writeClassTab();

    for (AbstractSymbol className : map.classtags) {
      emit(WORD, -1);

      emitLabel(String.format(PROTOBJ, className));
      if (className.toString().equals("Bool")) {
        emitLabel("bool_const0");
      }

      emit(WORD, String.format("%d # classtag", map.classtags.indexOf(className)));

      emit(WORD, String.format("%d # size", objectSize(className)));

      emit(WORD, String.format(METHODTAB, className));

      // Layout attributes
      int offset = 12;
      LinkedList<AbstractSymbol> ancestry = map.getAncestry(className);
      for (AbstractSymbol ancestor : ancestry) {

        // Have to go in order the attributes are defined
        CoolMap.AttributeData[] attrs = map.classAttributes.get(ancestor).values().toArray(new CoolMap.AttributeData[0]);
        Arrays.sort(attrs);
        for (CoolMap.AttributeData a : attrs) {
            emitLabel(String.format("%s_attr_%s", className, a.name));

            switch (ancestor.toString()) {
              case "Int": {
                intDefault();
                break;
              }
              case "Bool": {
                boolDefault();
                break;
              }
              case "String": {
                stringDefault(a.name);
                break;
              }
              default: {
                emitDefault(a.type, a.name);
              }
            }

            // Keep track of the attribute offset, we'll need it later
            a.offset = offset;
            offset += 4;
        }
      }

      writeDispatchTab(className, ancestry);
      endLabel();
    }
    writeBoolTrue();

    writeDispatchTabTab();

    AbstractTable.stringtable.codeStringTable(STR_CLASS_TAG, out);
    AbstractTable.inttable.codeStringTable(INT_CLASS_TAG, out);

    // Disable GC for now
    emitLabel("_MemMgr_INITIALIZER");
    emit(WORD, "_NoGC_Init");
    emitLabel("_MemMgr_COLLECTOR");
    emit(WORD, "_NoGC_Collect");
    emitLabel("_MemMgr_TEST");
    emit(WORD, 0);
    endLabel(); 

    // Marks end of static data, heap can start now
    emitLabel("heap_start");
    endLabel();
    emit("##### END DATA #####\n\n");
  }

  private void initAttributes() {
    for (Enumeration e = map.classes.getElements(); e.hasMoreElements(); ) {
      classc currentClass = (classc) e.nextElement();

      // TODO: create a frame for each attribute instead of sharing
      // Prep a "pseudo-frame" 
      emitLabel(String.format(ATTRINIT, currentClass.name));
      emitRegisterPreserve(out);
      emitFramePrologue(out);
      emitFrameEpilogue(out);

      for (Enumeration e2 = currentClass.features.getElements(); e2.hasMoreElements(); )  {
        // TODO: Seed the symbol table with ancestral attributes to make symbolTable
        // source of truth on offsets
        Feature f = (Feature) e2.nextElement();
        if (f instanceof attr) {
          attr a = (attr) f;
          Integer offset = (Integer) lookupObject(currentClass.name, a.name)[1];

          // Init attributes of type Int, String, Bool with defaults
          // Null pointer or their init expression for everything else
          if (a.init instanceof no_expr && initByPrototype(a.type_decl)) {
            emitObjectCopy(a.type_decl.toString(), out);
          } else {
            a.init.code(out, currentClass.name);
          }

          emit(blockComment(String.format("store value to attribute %s.%s", currentClass.name, a.name)));
          emit(pop("$t1"));
          emit("sw $t1 " + offset + "($a0)");
        }
      }

      // Clean up pseudo-frame 
      emitFrameCleanup(out);
      emitRegisterRestore(out);
      emitPadded(pop("$a0"), out);

      emit("jr $ra");
      endLabel();
    }
  }

  private void writeMethods() {
    for (Enumeration e = map.classes.getElements(); e.hasMoreElements(); ) {
      classc currentClass = (classc) e.nextElement();
      CoolMap.ClassTable symbols = map.programSymbols.get(currentClass.name);
      for (Enumeration e2 = currentClass.features.getElements(); e2.hasMoreElements(); )  {
        // TODO: Seed the symbol table with ancestral attributes to make symbolTable
        // source of truth on offsets

        Feature f = (Feature) e2.nextElement();
        if (f instanceof method) {
          method m = (method) f;
          symbols.objects.enterScope();

          // For Main.main, we should setup a pseudo-frame for storing locals
          emitLabel(String.format(METHODREF, currentClass.name, m.name));
          if (currentClass.name.equals(TreeConstants.Main) && m.name.equals(TreeConstants.main_meth)) {
            emitFramePrologue(out);
            emitFrameEpilogue(out);
          }

          // Arguments start at $fp
          int offset = 0;
          for (Enumeration e3 = m.formals.getElements(); e3.hasMoreElements(); ) {
            formalc f2 = (formalc) e3.nextElement();
            symbols.objects.addId(
              f2.name, 
              map.new ObjectData(f2.type_decl, CoolMap.SymbolType.ARG, offset)
            );
            offset += 4;
          }

          m.expr.code(out, currentClass.name);

          // Assuming convention of return value in $a0
          emit(pop("$a0"));
          if (m.expr instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) m.expr;
            if (o.requiresDereference()) {
              emit("lw $a0 ($a0)");
            }
          }
          endLabel();
          emit("jr $ra");
          symbols.objects.exitScope();
        }
      }
    }
  }

  public void layoutCode() {
    emit("##### START CODE #####");
    emit(".text");

    // Skip inits for Int and String
    // TODO: Posisble skip init for IO
    emitLabel("Int_init");
    emitLabel("String_init");
    emit(comment("TODO: do something meaningful"));
    emit("add $t1 $t1 $t1");
    endLabel();

    initAttributes();

    writeMethods();

    emitLabel("main");
    endLabel();

    emit("##### END CODE #####");
  }
}