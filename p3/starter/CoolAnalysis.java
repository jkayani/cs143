import java.util.*;

// Object is the implicit parent of all classes
// IO, Int, String, Bool cannot be extended or redefined
// All mentioned classes must be defined
// All programs must have Main class
// Inheritance may not be cyclical: class A inherits from B, and class B inherits from A is not allowed
public class CoolAnalysis {

  public CoolAnalysis() {}

  private static int errorCount = 0;
  private static ArrayList<AbstractSymbol> builtins = new ArrayList<AbstractSymbol>();

  // Since COOL has single inheritance, each class has exactly 1 parent
  private static Map<AbstractSymbol, AbstractSymbol> classGraph = new HashMap<AbstractSymbol, AbstractSymbol>();

  private static void init() {
    classGraph.put(TreeConstants.Object_, null);
    classGraph.put(TreeConstants.Int, TreeConstants.Object_);
    classGraph.put(TreeConstants.Str, TreeConstants.Object_);
    classGraph.put(TreeConstants.IO, TreeConstants.Object_);
    classGraph.put(TreeConstants.Bool, TreeConstants.Object_);

    builtins.add(TreeConstants.Object_);
    builtins.add(TreeConstants.Int);
    builtins.add(TreeConstants.Str);
    builtins.add(TreeConstants.Bool);
    builtins.add(TreeConstants.IO);
  }

  private static void printGraph() {
    for (Map.Entry<AbstractSymbol, AbstractSymbol> e : classGraph.entrySet()) {
      System.out.printf("Class %s inherits from %s\n", e.getKey(), e.getValue());
    }
  }

  private static void buildGraph(Classes classes) {
    for (int i = 0; i < classes.getLength(); i++) {
      class_c class_ = (class_c) classes.getNth(i);
      AbstractSymbol name = class_.getName();
      if (classGraph.containsKey(name)) {
        System.out.printf("Error, class %s already defined\n", name);
        errorCount++;
      } 
      else {
        AbstractSymbol parentName = class_.getParent();
        classGraph.put(name, parentName);
      }
    }
  }

  private static void undefinedClasses() {
    for (Map.Entry<AbstractSymbol, AbstractSymbol> e : classGraph.entrySet()) {
      if (!classGraph.containsKey(e.getValue()) && e.getValue() != null) {
        System.out.printf("Error, %s is the parent of %s but is not defined\n", e.getValue(), e.getKey());
        errorCount++;
      }
    }
  }

  private static void findMainClass() {
    if (!classGraph.containsKey("Main")) {
      System.out.printf("Error, Main class is missing\n");
      errorCount++;
    }
  }

  // Finding cycles means starting at a class, 
  // following the hierarchy to the end, and repeating for all uncovered classes. 
  // Failure indicates a cycle
  private static void findCycles() {
    ArrayList<AbstractSymbol> visited = new ArrayList<AbstractSymbol>();
    for (Map.Entry<AbstractSymbol, AbstractSymbol> e : classGraph.entrySet()) {
      System.out.printf("Next iteration\n");
      System.out.println(visited);

      // Builtins are non-cyclical, and any class in a known non-cyclical path won't cause a cycle
      if (visited.contains(e.getKey()) || builtins.contains(e.getKey())) {
        System.out.printf("Skipping %s\n", e.getKey());
        continue;
      }

      ArrayList<AbstractSymbol> visitedInRun = new ArrayList<AbstractSymbol>();
      if (cycles(e.getKey(), visitedInRun, visited) > 0) {
        System.out.printf("Error, the class graph has a cycle");
        return;
      }
      visited.addAll(visitedInRun);
    }
  }
  private static int cycles(AbstractSymbol node, ArrayList<AbstractSymbol> visited, ArrayList<AbstractSymbol> allVisited) {
    if (allVisited.contains(node)) {
      return 0;
    }
    System.out.printf("Visiting %s\n", node);
    if (visited.contains(node)) {
      System.out.printf("Error, %s was already seen\n", node);
      return ++errorCount;
    } 
    else {
      AbstractSymbol next = classGraph.get(node);
      visited.add(node);
      if (next == null) {
        return 0;
      }
      return cycles(next, visited, allVisited);
    }
  }

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
    // Look at each class's parent, if same => common ancestor. Repeat on the parents.
    while (!(a.equals(TreeConstants.Object_) || b.equals(TreeConstants.Object_))) {
      a = classGraph.get(a);
      b = classGraph.get(b);
      if (a.equals(b)) {
        return a;
      }
    }
    return TreeConstants.Object_;
  }

  public void analyze(Program p, Classes classes) {
    init();
    buildGraph(classes);
    // System.out.printf("\n---Inheritance Graph---\n");
    // printGraph();
    // System.out.printf("\n\n");
    // System.out.printf("---Undefined Classes---\n");
    // undefinedClasses();
    // System.out.printf("\n\n");
    // System.out.printf("---Cycles---\n");
    // findCycles();
    // System.out.printf("\n\n");
    // System.out.printf("---Main class---\n");
    // findMainClass();
    System.out.printf("\n\n");
    discoverPublicMembers(classes);
    typeCheckClass((class_c) classes.getNth(0));
  }

  private class ClassTable {
    public SymbolTable objects = new SymbolTable();
    public SymbolTable methods = new SymbolTable();
    public ClassTable() {}
    public String toString() {
      return String.format("\nObjects: %s\nMethods: %s\n", objects.toString(), methods.toString());
    }
  }
  // program will map class names to ClassTable's
  private static Map<AbstractSymbol, ClassTable> program = new HashMap<AbstractSymbol, ClassTable>();

  /*
    Data for SymbolTable entries
  */
  private class ObjectData {
    public AbstractSymbol type;
    public ObjectData(AbstractSymbol a) { type = a; }
    public String toString() { return type.toString(); }
  }
  private class MethodData {
    public AbstractSymbol className;
    public AbstractSymbol returnType;
    public Formals args;
    public MethodData(AbstractSymbol a, AbstractSymbol c, Formals f) { className = a; returnType = c; args = f; }
    public String toString() {
      StringBuilder argString = new StringBuilder();
      for (Enumeration e = args.getElements(); e.hasMoreElements();)  {
        formalc f = (formalc) e.nextElement();
        argString.append(String.format("%s: %s, ", f.getName(), f.getType()));
      }
      return String.format("of class %s, accepting %sand returning %s", className, argString.toString(), returnType);
    }
  }

  private class_c currentClass;

  /* 
  * Discover all class names, and method names+signatures
  * since these are the only public data
  */
  private void discoverPublicMembers(Classes classes) {
    for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {

      class_c currClass = (class_c) e.nextElement();
      ClassTable currTable = new ClassTable();
      program.put(currClass.getName(), currTable);

      currTable.methods.enterScope();
      for (Enumeration e2 = currClass.getFeatures().getElements(); e2.hasMoreElements(); ) {
        Feature f = (Feature) e2.nextElement();
        if (f instanceof method) {
          method m = (method) f;
          currTable.methods.addId(m.getName(), new MethodData(currClass.getName(), m.getReturnType(), m.getFormals()));
        }
      }
    }
  }

  private ClassTable discoverClassMembers(class_c C) {
    ClassTable currTable = program.get(C.getName());
    currTable.objects.enterScope();

    for (Enumeration e = C.getFeatures().getElements(); e.hasMoreElements(); ) {
      Feature f = (Feature) e.nextElement();
      if (f instanceof attr) {
        attr a = (attr) f;
        currTable.objects.addId(a.getName(), new ObjectData(a.getType()));
      }
    }
    return currTable;
  }

  /*
    Recursively type check the AST:
    - Base case are leaf nodes, they take the right type based on their value
    - Upon each recursive call, enter a new scope
    - Type check the node
    - At then end of each recursive call, exit scope
  */
  private void typeCheckClass(class_c C) {
    ClassTable symbols = discoverClassMembers(C);

    // TODO: rm
    currentClass = C;

    for (Enumeration e = C.getFeatures().getElements(); e.hasMoreElements(); ) {
      Feature f = (Feature) e.nextElement();
      if (f instanceof attr) {
        attr a = (attr) f;
        Expression val = a.getExpression();
        if (!(val instanceof no_expr)) {
          typeCheckExpression(val, symbols);
        } else {
          val.set_type(a.getType());
        }
        validateOrError(a.getType(), val.get_type(), String.format("%s declared as %s but has value %s", a.getName(), a.getType(), val.get_type()), a); 
      }
    }
  }

  private void typeCheckExpression(Expression e, ClassTable symbols) {

    // Literals
    if (e instanceof SimpleExpression) {
      SimpleExpression s = (SimpleExpression) e;
      e.set_type(s.getType());
    }

    // Variable reference
    if (e instanceof object) {
      object o = (object) e;
      ObjectData a = (ObjectData) symbols.objects.lookup(o.getName());
      if (a == null) {
        error(String.format("%s not defined", o.getName()), e);
        e.set_type(TreeConstants.Object_);
      } else {
        e.set_type(a.type);
      }
    }

    // Variable assignment
    if (e instanceof assign) {
      assign a = (assign) e;
      ObjectData o = (ObjectData) symbols.objects.lookup(a.getName());
      if (o == null) {
        error(String.format("no identifier %s in scope", a.getName()), e);
        e.set_type(TreeConstants.Object_);
      } else {
        AbstractSymbol expectedType = o.type;
        typeCheckExpression(a.getExpression(), symbols);
        AbstractSymbol t = a.getExpression().get_type();
        validateOrError(expectedType, t, String.format("%s is declared to have type %s but assigned to %s", a.getName(), expectedType, t), e);
        e.set_type(t);
      }
    }

    // Conditionals 
    if (e instanceof cond) {
      cond c = (cond) e;
      typeCheckExpression(c.getPred(), symbols);
      AbstractSymbol predType = c.getPred().get_type();
      if (predType.equals(TreeConstants.Bool)) {
        typeCheckExpression(c.getThen(), symbols);
        typeCheckExpression(c.getElse(), symbols);
        e.set_type(findLUB(c.getThen().get_type(), c.getElse().get_type()));
      } else {
        error(String.format("conditional has predicate of type %s", predType), e);
        e.set_type(TreeConstants.Object_);
      }
    }

    // Binary Int operations
    if (e instanceof BinaryExpression) {
      BinaryExpression b = (BinaryExpression) e;
      Expression l = b.getLeft();
      Expression r = b.getRight();
      if (binaryForceInt(l, r, symbols)) {
        AbstractSymbol t = (b instanceof lt) || (b instanceof leq) ? TreeConstants.Bool : TreeConstants.Int;
        e.set_type(t);
      } else {
        error(String.format("BinaryExpression defined on Ints has operands %s and %s", l.get_type(), r.get_type()), e);
        e.set_type(TreeConstants.Object_);
      }
    }  

    // Unary Int operations
    boolean negType = assertType(e, neg.class);
    if (negType) {
      neg n = (neg) e;
      Expression o = n.getOperand();
      typeCheckExpression(o, symbols);
      validateOrError(TreeConstants.Int, o.get_type(), String.format("neg expression has operand %s", o.get_type()), e);
      e.set_type(o.get_type());
    }

    // Unary boolean
    boolean compType = assertType(e, comp.class);
    if (compType) {
      comp n = (comp) e;
      Expression o = n.getOperand();
      typeCheckExpression(o, symbols);
      validateOrError(TreeConstants.Bool, o.get_type(), String.format("comp expression has operand %s", o.get_type()), e);
      e.set_type(o.get_type());
    }

    // Equality
    if (e instanceof eq) {
      eq e2 = (eq) e;
      Expression l = e2.getLeft();
      Expression r = e2.getRight();
      typeCheckExpression(l, symbols);
      typeCheckExpression(r, symbols);
      AbstractSymbol lt = l.get_type();
      AbstractSymbol rt = r.get_type();
      if (lt.equals(rt)) {
        e.set_type(TreeConstants.Bool);
      } else {
        error(String.format("eq (=) expression has operands %s and %s", lt, rt), e);
        e.set_type(TreeConstants.Object_);
      }
    }
  }

  private boolean binaryForceInt(Expression left, Expression right, ClassTable symbols) {

    // Don't short-circuit, otherwise types aren't added to AST
    typeCheckExpression(left, symbols);
    typeCheckExpression(right, symbols);
    return left.get_type().equals(TreeConstants.Int) && right.get_type().equals(TreeConstants.Int);
  }

  private boolean assertType(Expression e, Class c) {
    try {
      c.cast(e);
      return true;
    } catch (Exception e2) {
      return false;
    }
  }

  private void validateOrError(AbstractSymbol expected, AbstractSymbol actual, String error, TreeNode t) {
    if (!(expected.equals(actual) || isAncestor(expected, actual))) {
      error(error, t);
    }
  }

  private void error(String error, TreeNode t) {
    AbstractSymbol filename = currentClass.getFilename();
    errorCount++;
    System.out.printf("%s:%d: %s\n", filename, t.getLineNumber(), error);
  }
}