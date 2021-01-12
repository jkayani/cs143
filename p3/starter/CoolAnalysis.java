import java.util.*;

public class CoolAnalysis {

  public CoolAnalysis() {}

  private static int errorCount = 0;
  private static ArrayList<AbstractSymbol> builtins = new ArrayList<AbstractSymbol>();
  private static ArrayList<AbstractSymbol> strictlyComparable = new ArrayList<AbstractSymbol>();

  // Since COOL has single inheritance, each class has exactly 1 parent
  private static Map<AbstractSymbol, AbstractSymbol> classGraph = new HashMap<AbstractSymbol, AbstractSymbol>();

  // TODO: Add attributes and methods of builtin classes to program
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

    strictlyComparable.add(TreeConstants.Int);
    strictlyComparable.add(TreeConstants.Str);
    strictlyComparable.add(TreeConstants.Bool);
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

    // TODO: Check all classes of program
    typeCheckClass((class_c) classes.getNth(0));
  }

  private class ClassTable {
    public SymbolTable objects = new SymbolTable();
    public SymbolTable methods = new SymbolTable();
    public class_c class_;
    public ClassTable(class_c c) { class_ = c; }
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

  /* 
  * Discover all class names, and method names+signatures
  * since these are the only public data
  */
  private void discoverPublicMembers(Classes classes) {
    for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {

      class_c currClass = (class_c) e.nextElement();
      ClassTable currTable = new ClassTable(currClass);
      program.put(currClass.getName(), currTable);

      currTable.methods.enterScope();
      for (Enumeration e2 = currClass.getFeatures().getElements(); e2.hasMoreElements(); ) {
        Feature f = (Feature) e2.nextElement();
        if (f instanceof method) {
          method m = (method) f;
          AbstractSymbol returnType = m.getReturnType();
          if (returnType.equals(TreeConstants.SELF_TYPE)) {
            returnType = currClass.getName();
          }
          currTable.methods.addId(m.getName(), new MethodData(currClass.getName(), returnType, m.getFormals()));
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
        AbstractSymbol type = a.getType();
        if (type.equals(TreeConstants.SELF_TYPE)) {
          type = C.getName();
        }
        currTable.objects.addId(a.getName(), new ObjectData(type));
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

    // Add self type
    symbols.objects.addId(TreeConstants.self, new ObjectData(C.getName()));

    for (Enumeration e = C.getFeatures().getElements(); e.hasMoreElements();) {
      Feature f = (Feature) e.nextElement();

      // Attributes
      if (f instanceof attr) {
        attr a = (attr) f;

        // Cannot use name self
        noSelfReference(a.getName(), f, symbols.class_);

        // Check the attr's expression, if any
        Expression val = a.getExpression();
        if (!(val instanceof no_expr)) {
          typeCheckExpression(val, symbols);
        } else {
          val.set_type(a.getType());
        }
        validateOrError(a.getType(), val.get_type(), errorTypeMismatch(a.getName(), a.getType(), val.get_type()), a, C); 
      } 

      // Method declarations
      else if (f instanceof method) {
        method m = (method) f;

        // Add method parameters to symbols
        symbols.objects.enterScope();
        for (Enumeration f2 = m.getFormals().getElements(); f2.hasMoreElements();) {
          formalc f3 = (formalc) f2.nextElement();

          // Cannot use self
          noSelfReference(f3.getName(), f, C);

          symbols.objects.addId(f3.getName(), new ObjectData(f3.getType()));
        }

        // Declared return type must match method's expression type
        typeCheckExpression(m.getExpression(), symbols);
        AbstractSymbol expected = m.getReturnType();
        AbstractSymbol t = m.getExpression().get_type();
        validateOrError(expected, t, errorTypeMismatch(m.getName(), expected, t), f, C);

        symbols.objects.exitScope();
      }
    }
  }

  private void typeCheckDispatch(Expression node, AbstractSymbol name, AbstractSymbol className, Expressions args, ClassTable symbols) {
    ClassTable c = program.get(className);
    MethodData m = (MethodData) c.methods.lookup(name);
    if (m == null) {
      error(errorNoSuchMethod(name, c.class_.getName()), node, symbols.class_);
      node.set_type(TreeConstants.Object_);
    }
    else {
      int parameterCount = m.args.getLength();
      if (parameterCount != args.getLength()) {
        error(errorBadMethodCall(name, m.className, m), node, symbols.class_);
      }
      else {
        for (int i = 0; i < args.getLength(); i++) {
          Expression arg = (Expression) args.getNth(i);
          formalc parameter = (formalc) m.args.getNth(i);

          // Check that any variables referenced exist
          typeCheckExpression(arg, symbols);

          // Check that the type matches method signature
          AbstractSymbol actual = arg.get_type();
          validateOrError(parameter.getType(), actual, errorTypeMismatch(parameter.getName(), parameter.getType(), actual), arg, symbols.class_);
        }
      }
      node.set_type(m.returnType);
    }
  }

  private void typeCheckExpression(Expression e, ClassTable symbols) {

    // Literals
    if (e instanceof SimpleExpression) {
      SimpleExpression s = (SimpleExpression) e;
      e.set_type(s.getType());
    }

    // Simple method calls
    if (e instanceof dispatch) {
      dispatch d = (dispatch) e;

      // Calculate the expression type to know which method table to use
      typeCheckExpression(d.getExpression(), symbols);
      AbstractSymbol className = d.getExpression().get_type();

      typeCheckDispatch(e, d.getName(), className, d.getArgs(), symbols);
    }

    // Superclass method calls
    if (e instanceof static_dispatch) {
      static_dispatch d = (static_dispatch) e;

      // Calculate the expression type to know which method table to use
      typeCheckExpression(d.getExpression(), symbols);
      AbstractSymbol className = d.getExpression().get_type();
      AbstractSymbol superclassName = d.getType();

      if (!isAncestor(superclassName, className)) {
        error(String.format("%s is not a superclass of %s", className, superclassName), e, symbols.class_);
        e.set_type(TreeConstants.Object_);
      } else {
        typeCheckDispatch(e, d.getName(), superclassName, d.getArgs(), symbols);
      }
    }


    // Variable reference
    if (e instanceof object) {
      object o = (object) e;
      ObjectData a = (ObjectData) symbols.objects.lookup(o.getName());
      if (a == null) {
        error(errorNoSuchVariable(o.getName()), e, symbols.class_);
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
        error(errorNoSuchVariable(a.getName()), e, symbols.class_);
        e.set_type(TreeConstants.Object_);
      } else {

        // Cannot assign to self
        noSelfReference(a.getName(), e, symbols.class_);

        AbstractSymbol expectedType = o.type;
        typeCheckExpression(a.getExpression(), symbols);
        AbstractSymbol t = a.getExpression().get_type();
        validateOrError(expectedType, t, errorTypeMismatch(a.getName(), expectedType, t), e, symbols.class_);
        e.set_type(t);
      }
    }

    // Block expressions
    if (e instanceof block) {
      block b = (block) e;
      Expressions list = b.getBody();
      for (Enumeration e2 = list.getElements(); e2.hasMoreElements();) {
        Expression e3 = (Expression) e2.nextElement();
        typeCheckExpression(e3, symbols);
      }
      e.set_type(b.getLast().get_type());
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
        error(String.format("conditional has predicate of type %s", predType), e, symbols.class_);
        e.set_type(TreeConstants.Object_);
      }
    }

    // Loops
    if (e instanceof loop) {
      loop l = (loop) e;
      typeCheckExpression(l.getPred(), symbols);
      validateOrError(TreeConstants.Bool, l.getPred().get_type(), String.format("loop predicate is type %s not Bool", l.getPred().get_type()), e, symbols.class_);
      typeCheckExpression(l.getBody(), symbols);
      e.set_type(TreeConstants.Object_);
    }

    // Case statements
    if (e instanceof typcase) {
      typcase t = (typcase) e;
      Cases c = t.getCases();
      List<AbstractSymbol> types = new ArrayList<AbstractSymbol>();

      // Type check the case's switch statement
      typeCheckExpression(t.getExpression(), symbols);

      // Check each branch's expression when the branch's 
      // variable has the branch's type
      for (Enumeration e2 = c.getElements(); e2.hasMoreElements(); ) {
        branch b = (branch) e2.nextElement();
        symbols.objects.enterScope();

        // Cannot use name self
        noSelfReference(b.getName(), e, symbols.class_);

        symbols.objects.addId(b.getName(), new ObjectData(b.getType()));
        typeCheckExpression(b.getExpression(), symbols);
        symbols.objects.exitScope();

        if (types.contains(b.getType())) {
          error(String.format("duplicate branch of type %s in case statement", b.getType()), e, symbols.class_);
        }

        types.add(b.getType());
      }

      // Result is LUB among all types
      e.set_type(findLUB(types));
    }

    // Let statements
    if (e instanceof let) {
      let l = (let) e;
      AbstractSymbol expectedType = l.getType();

      // Cannot use name self
      noSelfReference(l.getName(), e, symbols.class_);

      // Variables with no immediate bindings are assumed to be good
      if (l.getInit() instanceof no_expr) {
        l.getInit().set_type(expectedType);
      } else {
        typeCheckExpression(l.getInit(), symbols);
      }
      AbstractSymbol initType = l.getInit().get_type();
      validateOrError(expectedType, initType, errorTypeMismatch(l.getName(), expectedType, initType), e, symbols.class_);

      // Check the body, taking into the account the newly introduced variable 
      symbols.objects.enterScope();
      symbols.objects.addId(l.getName(), new ObjectData(initType));
      typeCheckExpression(l.getBody(), symbols);
      symbols.objects.exitScope();

      e.set_type(l.getBody().get_type());
    }

    // isvoid
    if (e instanceof isvoid) {
      isvoid i = (isvoid) e;
      typeCheckExpression(i.getExpression(), symbols);
      e.set_type(TreeConstants.Bool);
    }

    // New
    if (e instanceof new_) {
      new_ n = (new_) e;
      AbstractSymbol t;
      if (n.getType().equals(TreeConstants.SELF_TYPE)) {
        t = symbols.class_.getName();
      } else {
        t = n.getType();
      }
      e.set_type(t);
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
        error(String.format("BinaryExpression defined on Ints has operands %s and %s", l.get_type(), r.get_type()), e, symbols.class_);
        e.set_type(TreeConstants.Object_);
      }
    }  

    // Unary Int operations
    if (e instanceof neg) {
      neg n = (neg) e;
      Expression o = n.getOperand();
      typeCheckExpression(o, symbols);
      validateOrError(TreeConstants.Int, o.get_type(), String.format("neg expression has operand %s", o.get_type()), e, symbols.class_);
      e.set_type(o.get_type());
    }

    // Unary boolean
    if (e instanceof comp) {
      comp n = (comp) e;
      Expression o = n.getOperand();
      typeCheckExpression(o, symbols);
      validateOrError(TreeConstants.Bool, o.get_type(), String.format("comp expression has operand %s", o.get_type()), e, symbols.class_);
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

      // Each of the strictlyComparable types can only be compared to itself
      boolean strictMode = strictlyComparable.contains(lt) || strictlyComparable.contains(rt);
      if (!strictMode || (strictMode && lt.equals(rt))) {
        e.set_type(TreeConstants.Bool);
      } else {
        error(String.format("eq (=) expression has operands %s and %s", lt, rt), e, symbols.class_);
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

  private String errorTypeMismatch(AbstractSymbol a, AbstractSymbol expected, AbstractSymbol actual) {
    return String.format("%s declared to have type %s but has type %s", a, expected, actual);
  }

  private String errorNoSuchVariable(AbstractSymbol v) {
    return String.format("no variable %s defined in current scope", v);
  }

  private String errorNoSuchMethod(AbstractSymbol v, AbstractSymbol c) {
    return String.format("no method %s defined for class %s", v, c);
  }

  private String errorBadMethodCall(AbstractSymbol v, AbstractSymbol c, MethodData m) {
    return String.format("%s.%s expects %d arguments", c, v, m.args.getLength());
  }

  private void noSelfReference(AbstractSymbol s, TreeNode t, class_c currentClass) {
    if (s.equals(TreeConstants.self)) {
      error("symbol self cannot be re-bound", t, currentClass);
    }
  }

  private void validateOrError(AbstractSymbol expected, AbstractSymbol actual, String error, TreeNode t, class_c currentClass) {
    if (expected.equals(TreeConstants.SELF_TYPE)) {
      expected = currentClass.getName();
    }
    if (actual.equals(TreeConstants.SELF_TYPE)) {
      actual = currentClass.getName();
    }
    if (!(expected.equals(actual) || isAncestor(expected, actual))) {
      error(error, t, currentClass);
    }
  }

  private void error(String error, TreeNode t, class_c currentClass) {
    AbstractSymbol filename = currentClass.getFilename();
    errorCount++;
    System.out.printf("%s:%d: %s\n", filename, t.getLineNumber(), error);
  }
}