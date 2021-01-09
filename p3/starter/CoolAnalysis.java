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

  public void analyze(Program p, Classes classes) {
    // init();
    // buildGraph(classes);
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

  private static SymbolTable programwide = new SymbolTable();
  private class_c currentClass;
  private class ObjectData {
    public AbstractSymbol type;
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

  /* 
  * Discover all class names, and method names+signatures
  * since these are the only public data
  */
  private void discoverPublicMembers(Classes classes) {
    programwide.enterScope();
    for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
      class_c currClass = (class_c) e.nextElement();
      for (Enumeration e2 = currClass.getFeatures().getElements(); e2.hasMoreElements(); ) {
        Feature f = (Feature) e2.nextElement();
        if (f instanceof method) {
          method m = (method) f;
          programwide.addId(m.getName(), new MethodData(currClass.getName(), m.getReturnType(), m.getFormals()));
        }
      }
    }
    System.out.println(programwide);
  }

  private void discoverClassMembers(class_c C) {
    for (Enumeration e = C.getFeatures().getElements(); e.hasMoreElements(); ) {
      Feature f = (Feature) e.nextElement();
      if (f instanceof attr) {
        attr a = (attr) f;
        programwide.addId(a.getName(), a.getType());
      }
    }
  }

  /*
    Recursively type check the AST:
    - Add new variables to ObjectData SymbolTable
    - Base case are leaf nodes, they take the right type based on their value
    - Upon each recursive call, enter a new scope
    - Type check the node
    - At then end of each recursive call, exit scope
  */
  private void typeCheckClass(class_c C) {
    programwide.enterScope();
    discoverClassMembers(C);
    currentClass = C;
    for (Enumeration e = C.getFeatures().getElements(); e.hasMoreElements(); ) {
      Feature f = (Feature) e.nextElement();
      if (f instanceof attr) {
        attr a = (attr) f;
        Expression val = a.getExpression();
        typeCheckExpression(val);
        validateOrError(a.getType(), val.get_type(), String.format("%s declared as %s but has value %s", a.getName(), a.getType(), val.get_type()), a); 
      }
    }
  }

  private void typeCheckExpression(Expression e) {

    // Simple types
    boolean intType = assertType(e, int_const.class);
    boolean boolType = assertType(e, bool_const.class);
    boolean stringType = assertType(e, string_const.class);
    boolean objecType = assertType(e, object.class);
    if (intType) {
      e.set_type(TreeConstants.Int);
    } else if (boolType) {
      e.set_type(TreeConstants.Bool);
    } else if (stringType) {
      e.set_type(TreeConstants.Str);
    } else if (objecType) {
      e.set_type(TreeConstants.Object_);
    }

    // Binary Int operations
    boolean plusType = assertType(e, plus.class);
    boolean subType = assertType(e, sub.class);
    boolean mulType = assertType(e, mul.class);
    boolean divType = assertType(e, divide.class);
    boolean ltType = assertType(e, lt.class);
    boolean leqType = assertType(e, leq.class);
    if (plusType) {
      plus p = (plus) e;
      Expression l = p.getLeft();
      Expression r = p.getRight();
      AbstractSymbol t = binaryForceInt(l, r) ? TreeConstants.Int : TreeConstants.Object_;
      validateOrError(TreeConstants.Int, t, String.format("plus expression has operands %s and %s", l.get_type(), r.get_type()), e);
      e.set_type(t);
    } else if (subType) {
      sub s = (sub) e;
      Expression l = s.getLeft();
      Expression r = s.getRight();
      AbstractSymbol t = binaryForceInt(l, r) ? TreeConstants.Int : TreeConstants.Object_;
      validateOrError(TreeConstants.Int, t, String.format("sub expression has operands %s and %s", l.get_type(), r.get_type()), e);
      e.set_type(t);
    } else if (mulType) {
      mul m = (mul) e;
      Expression l = m.getLeft();
      Expression r = m.getRight();
      AbstractSymbol t = binaryForceInt(l, r) ? TreeConstants.Int : TreeConstants.Object_;
      validateOrError(TreeConstants.Int, t, String.format("mul expression has operands %s and %s", l.get_type(), r.get_type()), e);
      e.set_type(t);
    } else if (divType) {
      divide d = (divide) e;
      Expression l = d.getLeft();
      Expression r = d.getRight();
      AbstractSymbol t = binaryForceInt(l, r) ? TreeConstants.Int : TreeConstants.Object_;
      validateOrError(TreeConstants.Int, t, String.format("divide expression has operands %s and %s", l.get_type(), r.get_type()), e);
      e.set_type(t);
    } else if (ltType) {
      lt d = (lt) e;
      Expression l = d.getLeft();
      Expression r = d.getRight();
      AbstractSymbol t = binaryForceInt(l, r) ? TreeConstants.Bool : TreeConstants.Object_;
      validateOrError(TreeConstants.Bool, t, String.format("lt (<) expression has operands %s and %s", l.get_type(), r.get_type()), e);
      e.set_type(t);
    } else if (leqType) {
      leq d = (leq) e;
      Expression l = d.getLeft();
      Expression r = d.getRight();
      AbstractSymbol t = binaryForceInt(l, r) ? TreeConstants.Bool : TreeConstants.Object_;
      validateOrError(TreeConstants.Bool, t, String.format("leq (<=) expression has operands %s and %s", l.get_type(), r.get_type()), e);
      e.set_type(t);
    }

    // Unary Int operations
    boolean negType = assertType(e, neg.class);
    if (negType) {
      neg n = (neg) e;
      Expression o = n.getOperand();
      typeCheckExpression(o);
      validateOrError(TreeConstants.Int, o.get_type(), String.format("neg expression has operand %s", o.get_type()), e);
      e.set_type(o.get_type());
    }
  }

  private boolean binaryForceInt(Expression left, Expression right) {

    // Don't short-circuit, otherwise types aren't added to AST
    boolean l = typeCheckExpression(left) == TreeConstants.Int;
    boolean r = typeCheckExpression(right) == TreeConstants.Int;
    return l && r;
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
    AbstractSymbol filename = currentClass.getFilename();
    if (!expected.equals(actual)) {
      errorCount++;
      System.out.printf("%s:%d: %s\n", filename, t.getLineNumber(), error);
    }
  }
}