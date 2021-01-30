import java.util.*;
import java.io.PrintStream;

public class CoolGen {

  public CoolGen(CoolMap c, PrintStream s) {
    map = c;
    out = s;
  }

  public CoolMap map;
  public PrintStream out;
  private boolean inLabel = false;

  private final String GLOBAL = ".globl";
  private final String WORD = ".word";
  private final String ASCIIZ = ".asciiz";
  private final String PROTOBJ = "%s_protObj";
  private final String METHODTAB = "%s_methodTab";
  private final String METHODREF = "%s.%s";

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
          emitLabel("_int_tag");
          break;
        }
        case "Bool": {
          emitLabel("_bool_tag");
          break;
        }
        case "String": {
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
        for (CoolMap.AttributeData a : map.classAttributes.get(ancestor)) {
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
        }
      }

      writeDispatchTab(className, ancestry);
      endLabel();
    }

    // Disable GC for now
    emitLabel("_MemMgr_INITIALIZER");
    emit(WORD, "_NoGC_Init");
    emitLabel("_MemMgr_COLLECTOR");
    emit(WORD, "_NoGC_Collect");
    emitLabel("_MemMgr_TEST");
    emit(WORD, 0);

    // Marks end of static data, heap can start now
    emitLabel("heap_start");
    endLabel();
  }

  public void layoutCode() {
    emit(".text");

    // Add a bunch of nops
    emitLabel("Int_init");
    emitLabel("String_init");
    emitLabel("Main_init");
    emitLabel("Main.main");
    emitLabel("main");
    emit("add $t0 $t0 $t1");
    emit("jr $ra");
    endLabel();
  }
}