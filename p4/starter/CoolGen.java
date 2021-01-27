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

  private void emit(String size, String s) {
    out.printf((inLabel ? "\t% s%s\n" : "%s %s\n"), size, s);
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

  public void layoutObjects() {
    out.println(".data");

    for (AbstractSymbol className : map.classtags) {
      emit(WORD, -1);
      emitLabel(String.format("%s_protObj", className));
      emit(WORD, map.classtags.indexOf(className));
      // TODO: calculate object size
      emit(WORD, 10);
      // TODO: add pointer to dispatch table
      emit(WORD, 0x1234);
      LinkedList<AbstractSymbol> ancestry = map.getAncestry(className);
      for (AbstractSymbol ancestor : ancestry) {
        for (CoolMap.AttributeData a : map.classAttributes.get(ancestor)) {
            emitLabel(String.format("%s_attr_%s", className, a.name));
            emit(WORD, 0x0000);
        }
      }
      endLabel();
    }
  }
}