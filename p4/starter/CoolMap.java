import java.util.*;

/* 
* Copied from P4 (CoolAnalysis.java) with modifications:
 - Remove type checking code
 - Remove any error/validation logic and fields
 - Use public fields instead of getters

* Idea is that once all compiler phases are complete, we can slightly modify the original P4 analysis code 
* to "plug in" to cgen phase
*/
public class CoolMap {

  public CoolMap(Classes c) {
    classes = c;
  }

  public Classes classes;

  /* The inheritance graph, mapping a class to it's parent */
  public Map<AbstractSymbol, AbstractSymbol> classGraph = new HashMap<AbstractSymbol, AbstractSymbol>();

  /* The entire symbol table, mapping each class to it's symbol table */
  public static Map<AbstractSymbol, ClassTable> programSymbols = new HashMap<AbstractSymbol, ClassTable>();

  public List<AbstractSymbol> classtags = new ArrayList<AbstractSymbol>();

  public Map<AbstractSymbol, HashMap<AbstractSymbol, AttributeData>> classAttributes = new HashMap<AbstractSymbol, HashMap<AbstractSymbol, AttributeData>>();
  public Map<AbstractSymbol, HashMap<AbstractSymbol, MethodData>> classMethods = new HashMap<AbstractSymbol, HashMap<AbstractSymbol, MethodData>>();

  /* Returns AST's for the builtin method types. Copied from starter code */
  private ArrayList<classc> getBuiltinClasses() {
    AbstractSymbol filename = AbstractTable.stringtable.addString("<basic class>");

    // The Object class has no parent class. Its methods are
    //        cool_abort() : Object    aborts the program
    //        type_name() : Str        returns a string representation 
    //                                 of class name
    //        copy() : SELF_TYPE       returns a copy of the object
    classc Object_class = 
        new classc(0, 
            TreeConstants.Object_, 
            TreeConstants.No_class,
            new Features(0)
          .appendElement(new method(0, 
                  TreeConstants.cool_abort, 
                  new Formals(0), 
                  TreeConstants.Object_, 
                  new no_expr(0)))
          .appendElement(new method(0,
                  TreeConstants.type_name,
                  new Formals(0),
                  TreeConstants.Str,
                  new no_expr(0)))
          .appendElement(new method(0,
                  TreeConstants.copy,
                  new Formals(0),
                  TreeConstants.SELF_TYPE,
                  new no_expr(0))),
            filename);
    
    // The IO class inherits from Object. Its methods are
    //        out_string(Str) : SELF_TYPE  writes a string to the output
    //        out_int(Int) : SELF_TYPE      "    an int    "  "     "
    //        in_string() : Str            reads a string from the input
    //        in_int() : Int                "   an int     "  "     "
    classc IO_class = 
        new classc(0,
            TreeConstants.IO,
            TreeConstants.Object_,
            new Features(0)
          .appendElement(new method(0,
                  TreeConstants.out_string,
                  new Formals(0)
                .appendElement(new formalc(0,
                      TreeConstants.arg,
                      TreeConstants.Str)),
                  TreeConstants.SELF_TYPE,
                  new no_expr(0)))
          .appendElement(new method(0,
                  TreeConstants.out_int,
                  new Formals(0)
                .appendElement(new formalc(0,
                      TreeConstants.arg,
                      TreeConstants.Int)),
                  TreeConstants.SELF_TYPE,
                  new no_expr(0)))
          .appendElement(new method(0,
                  TreeConstants.in_string,
                  new Formals(0),
                  TreeConstants.Str,
                  new no_expr(0)))
          .appendElement(new method(0,
                  TreeConstants.in_int,
                  new Formals(0),
                  TreeConstants.Int,
                  new no_expr(0))),
            filename);

    // The Int class has no methods and only a single attribute, the
    // "val" for the integer.
    classc Int_class = 
        new classc(0,
            TreeConstants.Int,
            TreeConstants.Object_,
            new Features(0)
          .appendElement(new attr(0,
                TreeConstants.val,
                TreeConstants.prim_slot,
                new no_expr(0))),
            filename);

    // Bool also has only the "val" slot.
    classc Bool_class = 
        new classc(0,
            TreeConstants.Bool,
            TreeConstants.Object_,
            new Features(0)
          .appendElement(new attr(0,
                TreeConstants.val,
                TreeConstants.prim_slot,
                new no_expr(0))),
            filename);

    // The class Str has a number of slots and operations:
    //       val                              the length of the string
    //       str_field                        the string itself
    //       length() : Int                   returns length of the string
    //       concat(arg: Str) : Str           performs string concatenation
    //       substr(arg: Int, arg2: Int): Str substring selection
    classc Str_class =
        new classc(0,
            TreeConstants.Str,
            TreeConstants.Object_,
            new Features(0)
          .appendElement(new attr(0,
                TreeConstants.val,
                TreeConstants.Int,
                new no_expr(0)))
          .appendElement(new attr(0,
                TreeConstants.str_field,
                TreeConstants.prim_slot,
                new no_expr(0)))
          .appendElement(new method(0,
                  TreeConstants.length,
                  new Formals(0),
                  TreeConstants.Int,
                  new no_expr(0)))
          .appendElement(new method(0,
                  TreeConstants.concat,
                  new Formals(0)
                .appendElement(new formalc(0,
                      TreeConstants.arg, 
                      TreeConstants.Str)),
                  TreeConstants.Str,
                  new no_expr(0)))
          .appendElement(new method(0,
                  TreeConstants.substr,
                  new Formals(0)
                .appendElement(new formalc(0,
                      TreeConstants.arg,
                      TreeConstants.Int))
                .appendElement(new formalc(0,
                      TreeConstants.arg2,
                      TreeConstants.Int)),
                  TreeConstants.Str,
                  new no_expr(0))),
            filename);
    ArrayList<classc> builtins = new ArrayList<classc>();
    builtins.add(Object_class);
    builtins.add(Int_class);
    builtins.add(Bool_class);
    builtins.add(Str_class);
    builtins.add(IO_class);
    return builtins;
  }

  /* The programSymbols entry for each class */
  public class ClassTable {
    public SymbolTable objects = new SymbolTable();
    public SymbolTable methods = new SymbolTable();
    public classc classc;
    public ClassTable(classc c) { classc = c; }
  }
  public enum SymbolType {
    ATTR, ARG, LOCAL
  }
  /* The SymbolTable entry for variables */
  public class ObjectData {
    public AbstractSymbol type;
    public SymbolType sym;
    int offset = 0;
    public ObjectData(AbstractSymbol t, SymbolType s, int o) {
      type = t; sym = s; offset = o;
     }
  }
  /* The SymbolTable entry for methods */
  public class MethodData {
    public AbstractSymbol name;
    public AbstractSymbol className;
    public AbstractSymbol returnType;
    public Formals args;
    // public HashMap<AbstractSymbol, AbstractSymbol> argTypes = new HashMap<AbstractSymbol, AbstractSymbol>();
    public MethodData(AbstractSymbol n, AbstractSymbol a, AbstractSymbol c, Formals f) { 
      className = a; returnType = c; args = f; name = n;
      // for (Enumeration e = f.getElements(); e.hasMoreElements(); ) {
      //   formalc f2 = (formalc) e.nextElement();
      //   argTypes.put(f2.name, f2.type_decl);
      // }
    }
  }

  public class AttributeData implements Comparable {
    public AbstractSymbol name;
    public AbstractSymbol type;
    public int offset = 0;
    public int order;
    public AttributeData(AbstractSymbol n, AbstractSymbol t, int o) { name = n; type = t; order = o; }
    public int compareTo(Object other) {
      return order - ((AttributeData) other).order;
    }
  }

  public void codeGenInit() {
    init();
    buildGraph();
    discoverClasses();
  }

  /* Setup the fields for inheritance graph construction */
  private void init() {

    // Add the builtin classes to the graph
    classGraph.put(TreeConstants.Object_, null);
    classGraph.put(TreeConstants.Int, TreeConstants.Object_);
    classGraph.put(TreeConstants.Str, TreeConstants.Object_);
    classGraph.put(TreeConstants.IO, TreeConstants.Object_);
    classGraph.put(TreeConstants.Bool, TreeConstants.Object_);

    // Add the methods and attributes of the builtins to programSymbols
    for (classc C : getBuiltinClasses()) {
      programSymbols.put(C.name, new ClassTable(C));
      classtags.add(C.name);
      discoverMethods(C);
      discoverAttributes(C);
    }
  }

  /* 
  * Build the inheritance graph, 
  * connecting each class to the class it inherits from (it's parent).
  * Stored in classGraph
  */
  private void buildGraph() {
    for (int i = 0; i < classes.getLength(); i++) {
      classc classc = (classc) classes.getNth(i);
      AbstractSymbol name = classc.name;
      AbstractSymbol parentName = classc.getParent();
      classGraph.put(name, parentName);
    }
  }

  /*
  * Note: these graph methods require that their inputs are symbols
  * represented in the graph; i.e, no SELF_TYPE or No_type
  */

  /* Calculate if A is the ancestor of B */
  private boolean isAncestor(AbstractSymbol a, AbstractSymbol b) {
    // Direct ancestry
    if (!b.equals(TreeConstants.Object_) && classGraph.get(b).equals(a)) {
      return true;
    }

    // If B is Object_, only way A is the ancestor is if A is Object
    // Find parent of B and check if A => ancestry
    while (!b.equals(TreeConstants.Object_)) {
      b = classGraph.get(b);
      if (a.equals(b)) {
        return true;
      }
    }
    return a.equals(TreeConstants.Object_);
  }

  /* Calculate common ancestor of A and B */  
  private AbstractSymbol findLUB(AbstractSymbol a, AbstractSymbol b) {
    if (a.equals(b)) {
      return a;
    } else if (isAncestor(a, b)) {
      return a;
    } else if (isAncestor(b, a)) {
      return b;
    }

    // If either class is Object, then that's the LUB
    // Look at each class's parent, if same => LUB. Repeat on the parents.
    while (!(a.equals(TreeConstants.Object_) || b.equals(TreeConstants.Object_))) {
      a = classGraph.get(a);
      b = classGraph.get(b);
      if (a.equals(b)) {
        return a;
      }
    }
    return TreeConstants.Object_;
  }

  /* Calculate common ancestor among elements of l */
  private AbstractSymbol findLUB(List<AbstractSymbol> l) {
    // Case statements have >= 1 branches
    if (l.size() < 2) {
      return l.get(0);
    }
    AbstractSymbol lub = findLUB(l.get(0), l.get(1));

    // Once LUB is Object_, no need to check further
    for (int i = 2; i < l.size() && !lub.equals(TreeConstants.Object_); i++) {
      lub = findLUB(lub, l.get(i));
    }
    return lub;
  }

  public LinkedList<AbstractSymbol> getAncestry(AbstractSymbol className) {
    LinkedList<AbstractSymbol> l = new LinkedList<AbstractSymbol>();
    AbstractSymbol next = className;
    do {
      l.addFirst(next);
      next = classGraph.get(next);
    } while (next != null);
    return l;
  }


  /* Discover all attributes/methods of all classes */
  private void discoverClasses() {
    for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
      classc currentClass = (classc) e.nextElement();
      ClassTable symbols = new ClassTable(currentClass);
      programSymbols.put(currentClass.name, symbols);
      classtags.add(currentClass.name);
      discoverMethods(currentClass);
      discoverAttributes(currentClass);
    }
  }

  /* 
  * Discover all of the methods of a class, and
  * create a MethodData entry for each in programSymbols
  */
  private void discoverMethods(classc C) {
    HashMap<AbstractSymbol, MethodData> list = new HashMap<AbstractSymbol, MethodData>();

    for (Enumeration e2 = C.getFeatures().getElements(); e2.hasMoreElements(); ) {
      Feature f = (Feature) e2.nextElement();
      if (f instanceof method) {
        method m = (method) f;
        AbstractSymbol returnType = m.return_type;
        list.put(m.name, new MethodData(m.name, C.name, returnType, m.formals));
      }
    }
    classMethods.put(C.name, list);
  }

  /* 
  * Discover all of the attributes of a class, and
  * create a ObjectData entry for each in programSymbols
  */
  private void discoverAttributes(classc C) {
    HashMap<AbstractSymbol, AttributeData> list = new HashMap<AbstractSymbol, AttributeData>();
    ClassTable symbols = programSymbols.get(C.name);

    symbols.objects.enterScope();
    int idx = 0;
    for (Enumeration e = C.getFeatures().getElements(); e.hasMoreElements(); ) {
      Feature f = (Feature) e.nextElement();
      if (f instanceof attr) {
        attr a = (attr) f;
        list.put(a.name, new AttributeData(a.name, a.type_decl, idx));

        // Attributes start after object header (12), 4 bytes per pointer
        symbols.objects.addId(a.name, new ObjectData(a.type_decl, SymbolType.ATTR, (12 + 4 * idx)));
        idx++;
      }
    }
    classAttributes.put(C.name, list);
  }
}