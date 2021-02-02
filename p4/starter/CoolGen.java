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
  private static final String PROTOBJ = "%s_protObj";
  private static final String METHODTAB = "%s_methodTab";
  private static final String METHODREF = "%s.%s";
  private static final String ATTRREF = "%s_attr_%s";

  public static String pop(String reg) {
    return String.format("# pop stack\n\t%s\n\t%s\n", "lw " + reg + " ($sp)", "add $sp $sp 4");
  }
  public static String push(String reg) {
    return String.format("# push stack\n\t%s\n\t%s\n", "sub $sp $sp 4", "sw " + reg + " ($sp)");
  }
  public static void newInt(PrintStream out) {
    int valOffset = CoolGen.lookupAttrOffset("Int", "_val");

    emitPadded(new String[] {
      "# create new int",
      pop("$t4"),

      // Make a new Int object
      "la $a0 " + String.format(PROTOBJ, "Int"),
      "sub $sp $sp 4",
      "move $fp $sp",
      "jal Object.copy",

      // Set it's val to the value
      "move $t5 $a0",
      "sw $t4 " + valOffset + "($t5)",

      // Push to stack
      push("$t5")
    }, out);
  }

  public static int lookupAttrOffset(String className, String attrName) {
    return map.classAttributes.get(
      AbstractTable.idtable.lookup(className))
      .get(AbstractTable.idtable.lookup(attrName))
      .offset;
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
      for (CoolMap.MethodData m : map.classMethods.get(ancestor)) {
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

        int offset = 12;
        for (CoolMap.AttributeData a : map.classAttributes.get(ancestor).values()) {
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
          plus p = (plus) a.init;
          p.code(out, map);
          emit(pop("$a0"));
          emit("sw $a0 " +  String.format(ATTRREF, currentClass.name, a.name));
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
    emit("add $a0 $a0 $a0");
    endLabel();

    emitLabel("Main_init");
    emit(push("$ra"));
    initAttributes();
    emit(pop("$ra"));
    emit("jr $ra");
    endLabel();

    emitLabel("Main.main");
    emit(push("$ra"));

    // sanity test
    // emit("la $a0 String_protObj");
    // emit(push("$a0"));
    // // emit("sub $sp $sp 4");
    // emit("move $fp $sp");
    // emit("jal IO.out_string");

    emit(pop("$ra"));
    emit("jr $ra");
    endLabel();

    emit("##### END CODE #####");
  }
}