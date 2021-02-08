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

  private static final String GLOBAL = ".globl";
  private static final String WORD = ".word";
  private static final String ASCIIZ = ".asciiz";
  private static final String ALIGN = ".align";
  private static final String PROTOBJ = "%s_protObj";
  private static final String METHODTAB = "%s_methodTab";
  private static final String METHODREF = "%s.%s";
  private static final String ATTRREF = "%s_attr_%s";

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
  public static void emitObjectCopy(String className, PrintStream out) {
    emitPadded(new String[] {
      blockComment("call to Object.copy"),

      comment("preserve registers"),
      push("$a0"), 
      push("$ra"),
      push("$fp"),
      comment("setup argument"),
      "la $a0 " + String.format(PROTOBJ, className),
      "sub $sp $sp 4",
      "move $fp $sp", // $fp points to nothing (thing after top of stack since no args on stack)
      "jal Object.copy",

      comment("restore registers"),
      "move $sp $fp",
      "add $sp $sp 4",
      pop("$fp"),
      pop("$ra"),
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

  public static Object[] lookupObject(AbstractSymbol className, AbstractSymbol symName) {
    CoolMap.ClassTable symbols = map.programSymbols.get(className);
    CoolMap.ObjectData o = (CoolMap.ObjectData) symbols.objects.lookup(symName);

    // Assuming that current self is always $a0, args are from $fp, and locals are from $sp
    Object[] res = new Object[]{ "$a0", 0 };
    if (symName.equals(TreeConstants.self)) {
      res[1] = 0;
    } else {
      switch (o.sym) {
        case LOCAL: {
          res[0] = "$sp";
          // TODO where is the offset?
          break;
        }
        case ARG: {
          res[0] = "$fp";
          // Stack grows with decreasing values
          res[1] = -1 * o.offset;
          break;
        }
        default: {
          res[1] = o.offset;
          break;
        }
      }
    }
    return res;
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
  public static void addObject(AbstractSymbol className, AbstractSymbol symName) {
    CoolMap.ClassTable symbols = map.programSymbols.get(className);
    // symbols.addId()
    // TODO: How will I know the offset of a local variable (i.e, introduced in let)?
  }

  public static void emitPadded(String[] s, PrintStream out) {
    for (String a : s) {
      out.printf("\t%s\n", a);
    }
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
    for (AbstractSymbol className : map.classtags) {
      emitLabel(String.format("%s_name", className));
      emit(ASCIIZ, String.format("\"%s\"", className));
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
    Map<String, CoolMap.MethodData> methods = new HashMap<String, CoolMap.MethodData>();
    
    // Collect the set of methods inherited/overidden
    for (AbstractSymbol ancestor : ancestry) {
      for (CoolMap.MethodData m : map.classMethods.get(ancestor).values()) {
        methods.put(m.name.toString(), m);
      }
    }

    // Build a table, associating each inherited method to the address of the definition
    // Since methods can be shared across descendants, we need this table
    emitLabel(String.format(METHODTAB, className));
    methods.forEach((String name, CoolMap.MethodData m) -> {
      emitLabel(String.format("%s_method_%s", className, name));
      emit(WORD, String.format(METHODREF, m.className, name));
      endLabel();
    });
    endLabel();
  }

  public void layoutStaticData() {
    emit("##### START DATA #####");
    emit(".data");

    writeClassNames();
    writeClassTags();
    writeClassTab();

    for (AbstractSymbol className : map.classtags) {
      emit(WORD, -1);

      if (className.toString().equals("Bool")) {
        emitLabel("bool_const0");
      } else {
        emitLabel(String.format(PROTOBJ, className));
      }

      emit(WORD, String.format("%d # classtag", map.classtags.indexOf(className)));

      emit(WORD, String.format("%d # size", objectSize(className)));

      emit(WORD, String.format(METHODTAB, className));

      // Layout attributes
      LinkedList<AbstractSymbol> ancestry = map.getAncestry(className);
      for (AbstractSymbol ancestor : ancestry) {

        // 3 words are used for standard object header, so rest of attributes start here and onward
        int offset = 12;

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

    AbstractTable.inttable.codeStringTable(INT_CLASS_TAG, out);

    // Sanity check string to be printed out
    // TODO: remove this sanity check
    emit(WORD, -1);
    emitLabel("temp_test");
    emit(WORD, 3);
    emit(WORD, 5);
    emit(WORD, String.format(METHODTAB, "String"));
    emit(WORD, "int_const0");
    emit(ASCIIZ, "\"Hello, from Main.main\\n\"");

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
      for (Enumeration e2 = currentClass.features.getElements(); e2.hasMoreElements(); )  {
        Feature f = (Feature) e2.nextElement();
        if (f instanceof attr) {
          attr a = (attr) f;
          Integer offset = (Integer) lookupObject(currentClass.name, a.name)[1];
          AttributeExpression p = (AttributeExpression) a.init;

          p.code(out, currentClass.name);
          emit(blockComment(String.format("store value to attribute %s.%s", currentClass.name, a.name)));
          emit(pop("$t1"));
          emit("sw $t1 " + offset + "($a0)");
        }
      }
    }
  }

  private void writeMethods() {
    for (Enumeration e = map.classes.getElements(); e.hasMoreElements(); ) {
      classc currentClass = (classc) e.nextElement();
      CoolMap.ClassTable symbols = map.programSymbols.get(currentClass.name);
      for (Enumeration e2 = currentClass.features.getElements(); e2.hasMoreElements(); )  {
        Feature f = (Feature) e2.nextElement();
        if (f instanceof method) {
          method m = (method) f;
          AttributeExpression p = (AttributeExpression) m.expr;
          symbols.objects.enterScope();

          // Arguments start at $fp
          int offset = 0;
          for (Enumeration e3 = m.formals.getElements(); e3.hasMoreElements(); ) {
            formalc f2 = (formalc) e3.nextElement();
            symbols.objects.addId(
              f2.name, 

              // TOOD: wtf is this
              map.new ObjectData(f2.type_decl, CoolMap.SymbolType.ARG, offset)
            );
            offset += 4;
          }

          emitLabel(String.format(METHODREF, currentClass.name, m.name));
          p.code(out, currentClass.name);

          // Assuming convention of return value in $a0
          // TODO: If return type is Int, String, or Bool and 
          // the expression of the method if instanceof object, deref that pointer
          emit(pop("$a0"));
          if (p instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) p;
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
    emitLabel("Int_init");
    emitLabel("String_init");
    emit(comment("TODO: do something meaningful"));
    emit("add $t1 $t1 $t1");
    endLabel();

    emitLabel("Main_init");
    emit(push("$ra"));
    emit(blockComment("init attributes"));
    initAttributes();
    emit(pop("$ra"));
    emit("jr $ra");
    endLabel();

    writeMethods();

    // emitLabel("Main.main");
    // // Sanity tests
    // // Print a string message
    // emit(push("$ra"));
    // emit("la $t1 temp_test");
    // emit(push("$t1"));
    // emit("sub $sp $sp 4");
    // emit("move $fp $sp");
    // emit("jal IO.out_string");
    // emit("add $sp $sp 4");
    // emit(pop("$ra"));
    // int attrCount = 4;
    // int distance = 4;
    // for (int i = 0; i < attrCount; i++) {
    //   emit(push("$ra"));
    //   emit("addi $t1 $a0 " + (12 + i * distance));
    //   emit("lw $t1 ($t1)");
    //   emit(push("$t1"));
    //   emit("sub $sp $sp 4");
    //   emit("move $fp $sp");
    //   emit("jal IO.out_int");
    //   emit("add $sp $sp 4");
    //   emit(pop("$ra"));
    // }
    // emit("jr $ra");
    // endLabel();

    emit("##### END CODE #####");
  }
}