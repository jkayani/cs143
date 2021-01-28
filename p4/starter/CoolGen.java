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
  private final String WORD = ".word";
  private final String ASCIIZ = ".asciiz";

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
        emit("Int_protoObj");
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
        emit("Int_protObj");
        return;
      }
      case "Bool": {
        emit("bool_const0");
        return;
      }
      case "String": {
        emit("String_protObj");
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
      emit(String.format("%s_name", className));
    }
    endLabel();
  }

  public void layoutObjects() {
    out.println(".data");

    writeClassNames();
    writeClassTags();
    writeClassTab();

    for (AbstractSymbol className : map.classtags) {
      emit(WORD, -1);

      if (className.toString().equals("Bool")) {
        emitLabel("bool_const0");
      } else {
        emitLabel(String.format("%s_protObj", className));
      }

      emit(WORD, String.format("%d # classtag", map.classtags.indexOf(className)));

      emit(WORD, String.format("%d # size", objectSize(className)));

      // TODO: add pointer to dispatch table
      emit(WORD, String.format("%d # method table", 1234));
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
      endLabel();
    }
  }
}