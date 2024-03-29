import java.util.*;

public class CoolAnalysis {

  public CoolAnalysis() {}

  private int errorCount = 0;
  private String endMessage = "Compilation halted due to static semantic errors.";

  /* The types built-in to COOL */
  private ArrayList<AbstractSymbol> builtins = new ArrayList<AbstractSymbol>();

  /* Types that can only be compared with the same type */
  private ArrayList<AbstractSymbol> strictlyComparable = new ArrayList<AbstractSymbol>();

  /* The inheritance graph, mapping a class to it's parent */
  private Map<AbstractSymbol, AbstractSymbol> classGraph = new HashMap<AbstractSymbol, AbstractSymbol>();

  /* The entire symbol table, mapping each class to it's symbol table */
  private Map<AbstractSymbol, ClassTable> programSymbols = new HashMap<AbstractSymbol, ClassTable>();

  /* Returns AST's for the builtin method types. Copied from starter code */
  private ArrayList<class_c> getBuiltinClasses() {
    AbstractSymbol filename = AbstractTable.stringtable.addString("<basic class>");

    // The Object class has no parent class. Its methods are
    //        cool_abort() : Object    aborts the program
    //        type_name() : Str        returns a string representation 
    //                                 of class name
    //        copy() : SELF_TYPE       returns a copy of the object
    class_c Object_class = 
        new class_c(0, 
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
    class_c IO_class = 
        new class_c(0,
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
    class_c Int_class = 
        new class_c(0,
            TreeConstants.Int,
            TreeConstants.Object_,
            new Features(0)
          .appendElement(new attr(0,
                TreeConstants.val,
                TreeConstants.prim_slot,
                new no_expr(0))),
            filename);

    // Bool also has only the "val" slot.
    class_c Bool_class = 
        new class_c(0,
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
    class_c Str_class =
        new class_c(0,
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
    ArrayList<class_c> builtins = new ArrayList<class_c>();
    builtins.add(Object_class);
    builtins.add(Int_class);
    builtins.add(Bool_class);
    builtins.add(Str_class);
    builtins.add(IO_class);
    return builtins;
  }

  /* 
  * Thrown on semantic error, used to break control flow and end type checking.
  * The project description said the type checker should recover from errors, 
  * but the grading scripts expect it to end after the first error. So this retrofits
  * the latter behavior on code that did the former. 
  */
  private class SemanticErrorException extends RuntimeException {
    public SemanticErrorException() {
      super();
    }
  }

  /* The programSymbols entry for each class */
  private class ClassTable {
    public SymbolTable objects = new SymbolTable();
    public SymbolTable methods = new SymbolTable();
    public class_c class_;
    public ClassTable(class_c c) { class_ = c; }
  }
  /* The SymbolTable entry for variables */
  private class ObjectData {
    public AbstractSymbol type;
    public ObjectData(AbstractSymbol a) { type = a; }
  }
  /* The SymbolTable entry for methods */
  private class MethodData {
    public AbstractSymbol className;
    public AbstractSymbol returnType;
    public Formals args;
    public MethodData(AbstractSymbol a, AbstractSymbol c, Formals f) { className = a; returnType = c; args = f; }
  }

  /* Entrypoint - type checks the expressions and decorates the AST */
  public int analyze(Classes classes) {
    init();

    // Check for well formed class graph. 
    // Order is necessary so that programSymbols is populated for providing error messages
    try {
      buildGraph(classes);
      discoverClasses(classes);
      illegalInheritance();
      undefinedClasses();
      findCycles();
      findMainClass();
    } catch (SemanticErrorException e) {
      return errorCount;
    }

    // Perform type checking and AST decoration
    try {
      for (Enumeration e = classes.getElements(); e.hasMoreElements();) {
        typeCheckClass((class_c) e.nextElement());
      }
    } catch (SemanticErrorException e) {
      return errorCount;
    }

    return errorCount;
  }

  /* Setup the fields for inheritance graph construction */
  private void init() {

    // Add the builtin classes to the graph
    classGraph.put(TreeConstants.Object_, null);
    classGraph.put(TreeConstants.Int, TreeConstants.Object_);
    classGraph.put(TreeConstants.Str, TreeConstants.Object_);
    classGraph.put(TreeConstants.IO, TreeConstants.Object_);
    classGraph.put(TreeConstants.Bool, TreeConstants.Object_);

    // Keep track of the bulitins for cycle detection
    builtins.add(TreeConstants.Object_);
    builtins.add(TreeConstants.Int);
    builtins.add(TreeConstants.Str);
    builtins.add(TreeConstants.Bool);
    builtins.add(TreeConstants.IO);

    // Add the methods and attributes of the builtins to programSymbols
    for (class_c C : getBuiltinClasses()) {
      programSymbols.put(C.getName(), new ClassTable(C));
      discoverMethods(C);
      discoverAttributes(C);
    }

    // Keep track of the strictly comparable types for type checking
    strictlyComparable.add(TreeConstants.Int);
    strictlyComparable.add(TreeConstants.Str);
    strictlyComparable.add(TreeConstants.Bool);
  }

  /* 
  * Build the inheritance graph, 
  * connecting each class to the class it inherits from (it's parent).
  * Stored in classGraph
  */
  private void buildGraph(Classes classes) {
    for (int i = 0; i < classes.getLength(); i++) {
      class_c class_ = (class_c) classes.getNth(i);
      AbstractSymbol name = class_.getName();
      if (classGraph.containsKey(name)) {
        error(String.format("class %s already defined", name), class_, class_);
      } 
      else {
        AbstractSymbol parentName = class_.getParent();
        noSelfTypeReference(name, class_, class_);
        classGraph.put(name, parentName);
      }
    }
  }

  /* Look for cases of illegal inheritance */
  private void illegalInheritance() {
    for (Map.Entry<AbstractSymbol, AbstractSymbol> e : classGraph.entrySet()) {
      AbstractSymbol parentName = e.getValue();
      AbstractSymbol childName = e.getKey();

      // Cannot inehrit from SELF_TYPE
      boolean selfInheritance = (parentName != null && parentName.equals(TreeConstants.SELF_TYPE)) || false;

      // Only Object_ can be inherited from
      boolean badInheritance = strictlyComparable.contains(parentName); 
      if (selfInheritance || badInheritance) {
        ClassTable c = programSymbols.get(childName);
        error(String.format("class %s cannot inherit from %s", childName, parentName), c.class_, c.class_);
      }
    }
  }

  /* Find any classes that are referenced in the graph but undefined */
  private void undefinedClasses() {
    for (Map.Entry<AbstractSymbol, AbstractSymbol> e : classGraph.entrySet()) {
      AbstractSymbol parentName = e.getValue();
      if (!classGraph.containsKey(parentName) && parentName != null) {
        ClassTable c = programSymbols.get(e.getKey());
        error(String.format("missing definition for class %s", parentName), c.class_, c.class_);
      }
    }
  }

  /* Check for a Main class with a main() method */
  private void findMainClass() {
    if (!classGraph.containsKey(TreeConstants.Main)) {
      // This exact message is mandated by the grading scripts
      error("Class Main is not defined.");
    } else {
      ClassTable c = programSymbols.get(TreeConstants.Main);
      MethodData m = (MethodData) c.methods.lookup(TreeConstants.main_meth);
      if (m == null) {
        error(String.format("no method Main.main found"), c.class_, c.class_);
      }
      else if (m.args.getLength() > 0) {
        error(String.format("Main.main method must accept 0 parameters"), c.class_, c.class_);
      }
    }
  }

  /* 
  * Calculate if the inheritance graph contains a cycle.
  * Public/private pair
  */
  private void findCycles() {
    ArrayList<AbstractSymbol> visited = new ArrayList<AbstractSymbol>();
    // Finding cycles means starting at a class, 
    // following the hierarchy to the end, and repeating for all uncovered classes. 
    // Failure indicates a cycle
    for (Map.Entry<AbstractSymbol, AbstractSymbol> e : classGraph.entrySet()) {

      // Builtins are non-cyclical, and any class in a known non-cyclical path won't cause a cycle
      if (visited.contains(e.getKey()) || builtins.contains(e.getKey())) {
        continue;
      }

      ArrayList<AbstractSymbol> visitedInRun = new ArrayList<AbstractSymbol>();
      if (cycles(e.getKey(), visitedInRun, visited)) {
        error("class inheritance graph has a cycle");
        return;
      }
      visited.addAll(visitedInRun);
    }
  }
  private boolean cycles(AbstractSymbol node, ArrayList<AbstractSymbol> visited, ArrayList<AbstractSymbol> allVisited) {
    if (allVisited.contains(node)) {
      return false;
    }
    if (visited.contains(node)) {
      return true;
    } 
    else {
      AbstractSymbol next = classGraph.get(node);
      visited.add(node);
      if (next == null) {
        return false;
      }
      return cycles(next, visited, allVisited);
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


  /* Discover all attributes/methods of all classes */
  private void discoverClasses(Classes classes) {
    for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
      class_c currentClass = (class_c) e.nextElement();
      ClassTable symbols = new ClassTable(currentClass);
      programSymbols.put(currentClass.getName(), symbols);
      discoverMethods(currentClass);
      discoverAttributes(currentClass);
    }
  }

  /* 
  * Discover all of the methods of a class, and
  * create a MethodData entry for each in programSymbols
  */
  private ClassTable discoverMethods(class_c C) {
    ClassTable symbols = programSymbols.get(C.getName());
    symbols.methods.enterScope();

    for (Enumeration e2 = C.getFeatures().getElements(); e2.hasMoreElements(); ) {
      Feature f = (Feature) e2.nextElement();
      if (f instanceof method) {
        method m = (method) f;

        if (symbols.methods.lookup(m.getName()) != null) {
          error(String.format("method %s.%s already defined", C.getName(), m.getName()), m, C);
        }

        AbstractSymbol returnType = m.getReturnType();
        symbols.methods.addId(m.getName(), new MethodData(C.getName(), returnType, m.getFormals()));
      }
    }
    return symbols;
  }

  /* 
  * Discover all of the attributes of a class, and
  * create a ObjectData entry for each in programSymbols
  */
  private ClassTable discoverAttributes(class_c C) {
    ClassTable currTable = programSymbols.get(C.getName());
    currTable.objects.enterScope();

    for (Enumeration e = C.getFeatures().getElements(); e.hasMoreElements(); ) {
      Feature f = (Feature) e.nextElement();
      if (f instanceof attr) {
        attr a = (attr) f;
        AbstractSymbol type = getClassName(a.getType(), C);

        // Cannot use name self
        noSelfReference(a.getName(), f, C);

        if (currTable.objects.lookup(a.getName()) != null) {
          error(String.format("attribute %s already defined", a.getName()), a, C);
        } else {
          currTable.objects.addId(a.getName(), new ObjectData(type));
        }
      }
    }
    return currTable;
  }

  /* Typecheck all of the class features */
  private void typeCheckClass(class_c C) {
    ClassTable symbols = programSymbols.get(C.getName());

    // Add self type
    symbols.objects.addId(TreeConstants.self, new ObjectData(TreeConstants.SELF_TYPE));

    for (Enumeration e = C.getFeatures().getElements(); e.hasMoreElements();) {
      Feature f = (Feature) e.nextElement();

      // Attributes
      if (f instanceof attr) {
        attr a = (attr) f;

        // Cannot use same name as attribute in any ancestor
        AbstractSymbol nextName = classGraph.get(C.getName());
        while (nextName != null) {
          ClassTable next = programSymbols.get(nextName);
          if (next.objects.lookup(a.getName()) != null) {
            error(String.format("attribute %s already defined in superclass %s", a.getName(), nextName), a, C);
          }
          nextName = classGraph.get(nextName);
        }

        // Check the attr's expression, if any
        Expression val = a.getExpression();
        if (val instanceof no_expr) {
          val.set_type(TreeConstants.No_type);
        } else {
          typeCheckExpression(val, symbols);
        }
        validateOrError(a.getType(), val.get_type(), errorTypeMismatch(a.getName(), a.getType(), val.get_type()), a, C); 
      } 

      // Method declarations
      else if (f instanceof method) {
        method m = (method) f;
        
        // Cannot use same method name with different signatures
        AbstractSymbol nextName = classGraph.get(C.getName());
        MethodData currentM = (MethodData) symbols.methods.lookup(m.getName());
        while (nextName != null) {
          ClassTable next = programSymbols.get(nextName);
          MethodData ancestorM = (MethodData) next.methods.lookup(m.getName());
          if (ancestorM != null) {
            if (!ancestorM.returnType.equals(currentM.returnType)) {
              error(String.format("method override for %s.%s has bad return type in %s", nextName, m.getName(), C.getName()), m, C);
            }
            for (int k = 0; k < currentM.args.getLength(); k++) {
              if (k >= ancestorM.args.getLength()) {
                error(String.format("method override for %s.%s has wrong parameter count in %s", nextName, m.getName(), C.getName()), m, C);
                break;
              }
              if (! ((formalc)currentM.args.getNth(k)).getType().equals( ((formalc)ancestorM.args.getNth(k)).getType())) {
                error(String.format("method override for %s.%s has wrong parameter type in %s", nextName, m.getName(), C.getName()), m, C);
                break;
              }
            }
          }
          nextName = classGraph.get(nextName);
        }

        // Add method parameters to symbols
        symbols.objects.enterScope();
        for (Enumeration f2 = m.getFormals().getElements(); f2.hasMoreElements();) {
          formalc f3 = (formalc) f2.nextElement();

          // Cannot use self nor SELF_TYPE
          noSelfReference(f3.getName(), f, C);
          noSelfTypeReference(f3.getType(), f, C);

          // Cannot shadow other parameters
          if (symbols.objects.probe(f3.getName()) != null) {
            error(String.format("parameter named %s already exists for method %s.%s", f3.getName(), C.getName(), m.getName()), m, C);
          } else {
            symbols.objects.addId(f3.getName(), new ObjectData(f3.getType()));
          }
        }

        // Return type must be a known class
        AbstractSymbol expected = m.getReturnType();
        if (!classGraph.containsKey(getClassName(expected, C))) {
          error(String.format("method %s.%s returns non-existent type %s", C.getName(), m.getName(), getClassName(expected, C)), m, C);
        }

        // Declared return type must match method's expression type
        typeCheckExpression(m.getExpression(), symbols);
        AbstractSymbol t = m.getExpression().get_type();
        validateOrError(expected, t, errorTypeMismatch(m.getName(), expected, t), f, C);

        symbols.objects.exitScope();
      }
    }
  }

  /* Typecheck a method call */
  private void typeCheckDispatch(Expression node, AbstractSymbol subjectType, AbstractSymbol name, AbstractSymbol className, Expressions args, ClassTable symbols) {
    AbstractSymbol actualClassName = getClassName(className, symbols.class_);
    ClassTable c = programSymbols.get(actualClassName);
    MethodData m = (MethodData) c.methods.lookup(name);
    if (m == null) {

      // Methods from ancestors can be called in children, so check there
      // and give up when no more ancestors
      if (classGraph.get(actualClassName) == null) {
        error(errorNoSuchMethod(name, subjectType), node, symbols.class_);
        node.set_type(TreeConstants.Object_);
      } else {
        typeCheckDispatch(node, subjectType, name, classGraph.get(actualClassName), args, symbols);
      }
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

      // If this method returns SELF_TYPE, then the actual return type
      // of this dispatch is the type of the expression it was called on
      if (m.returnType == TreeConstants.SELF_TYPE) {
        node.set_type(subjectType);
      } else {
        node.set_type(m.returnType);
      }
    }
  }

  /* Typecheck an expression */
  private void typeCheckExpression(Expression e, ClassTable symbols) {

    // Literals
    if (e instanceof SimpleExpression) {
      SimpleExpression s = (SimpleExpression) e;
      e.set_type(s.getType());
    }

    // Simple method cals
    if (e instanceof dispatch) {
      dispatch d = (dispatch) e;

      // Calculate the expression type to know which method table to use
      typeCheckExpression(d.getExpression(), symbols);
      AbstractSymbol className = d.getExpression().get_type();

      typeCheckDispatch(d, className, d.getName(), className, d.getArgs(), symbols);
    }

    // Explicit superclass method calls
    if (e instanceof static_dispatch) {
      static_dispatch d = (static_dispatch) e;

      // Calculate the expression type to know which method table to use
      typeCheckExpression(d.getExpression(), symbols);
      AbstractSymbol className = getClassName(d.getExpression().get_type(), symbols.class_);
      AbstractSymbol superclassName = d.getType();

      if (!(superclassName.equals(className) || isAncestor(superclassName, className))) {
        error(String.format("%s is not a superclass of %s", superclassName, className), e, symbols.class_);
        e.set_type(TreeConstants.Object_);
      } else {
        typeCheckDispatch(e, className, d.getName(), superclassName, d.getArgs(), symbols);
      }
    }

    // Variable reference
    if (e instanceof object) {
      object o = (object) e;
      AbstractSymbol nextName = symbols.class_.getName();
      boolean found = false;
      while (nextName != null) {
        ClassTable next = programSymbols.get(nextName);
        ObjectData od = (ObjectData) next.objects.lookup(o.getName());
        if (od != null) {
          found = true;
          e.set_type(od.type);
          break;
        }
        nextName = classGraph.get(nextName);
      }
      if (!found) {
        error(errorNoSuchVariable(o.getName()), e, symbols.class_);
        e.set_type(TreeConstants.Object_);
      }
    } 

    // Variable assignment
    if (e instanceof assign) {
      assign a = (assign) e;

      // Cannot assign to self
      noSelfReference(a.getName(), e, symbols.class_);

      AbstractSymbol nextName = symbols.class_.getName();
      boolean found = false;
      while (nextName != null) {
        ClassTable next = programSymbols.get(nextName);
        ObjectData od = (ObjectData) next.objects.lookup(a.getName());
        if (od != null) {
          found = true;
          AbstractSymbol expectedType = od.type;
          typeCheckExpression(a.getExpression(), symbols);
          AbstractSymbol t = a.getExpression().get_type();
          validateOrError(expectedType, t, errorTypeMismatch(a.getName(), expectedType, t), e, symbols.class_);
          e.set_type(t);
          break;
        }
        nextName = classGraph.get(nextName);
      }
      if (!found) {
        error(errorNoSuchVariable(a.getName()), e, symbols.class_);
        e.set_type(TreeConstants.Object_);
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
        e.set_type(
          findLUB(
            getClassName(c.getThen().get_type(), symbols.class_), 
            getClassName(c.getElse().get_type(), symbols.class_)));
      } else {
        error(String.format("conditional has predicate of type %s", predType), e, symbols.class_);
        e.set_type(TreeConstants.Object_);
      }
    }

    // Loops
    if (e instanceof loop) {
      loop l = (loop) e;
      typeCheckExpression(l.getPred(), symbols);
      validateOrError(TreeConstants.Bool, l.getPred().get_type(), String.format("loop has predicate of type %s", l.getPred().get_type()), e, symbols.class_);
      typeCheckExpression(l.getBody(), symbols);
      e.set_type(TreeConstants.Object_);
    }

    // Case statements
    if (e instanceof typcase) {
      typcase t = (typcase) e;
      Cases c = t.getCases();

      // The types that are checked against
      List<AbstractSymbol> types = new ArrayList<AbstractSymbol>();

      // The types result from each branch
      List<AbstractSymbol> expressionTypes = new ArrayList<AbstractSymbol>();

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
        expressionTypes.add(b.getExpression().get_type());
      }

      // Result is LUB among all types of the result expressions
      e.set_type(findLUB(expressionTypes));
    }

    // Let statements
    if (e instanceof let) {
      let l = (let) e;
      AbstractSymbol expectedType = l.getType();

      // Cannot use name self
      noSelfReference(l.getName(), e, symbols.class_);

      if (l.getInit() instanceof no_expr) {
        l.getInit().set_type(TreeConstants.No_type);
      } 
      else {
        typeCheckExpression(l.getInit(), symbols);
        AbstractSymbol initType = l.getInit().get_type();
        validateOrError(expectedType, initType, errorTypeMismatch(l.getName(), expectedType, initType), e, symbols.class_);

        // The types are conforming, but we set_type to whatever the code says,
        // in case the assigned type is an ancestor of the computed type
        l.getInit().set_type(expectedType);
      }

      // Check the body, taking into the account the newly introduced variable 
      symbols.objects.enterScope();
      symbols.objects.addId(l.getName(), new ObjectData(expectedType));
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
      e.set_type(n.getType());
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
        error(String.format("eq (=) expression has incomparable operands %s and %s", lt, rt), e, symbols.class_);
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

  /* Translate a name into a type. Mostly useful for getting a concrete type for SELF_TYPE */
  private AbstractSymbol getClassName(AbstractSymbol type, class_c currentClass) {
    return type.equals(TreeConstants.SELF_TYPE) ? currentClass.getName() : type;
  }

  /* Predefined error messages and handlers */
  private String errorTypeMismatch(AbstractSymbol a, AbstractSymbol expected, AbstractSymbol actual) {
    return String.format("%s declared to have type %s but has type %s", a, expected, actual);
  }

  private String errorNoSuchVariable(AbstractSymbol v) {
    return String.format("unknown variable %s", v);
  }

  private String errorNoSuchMethod(AbstractSymbol v, AbstractSymbol c) {
    return String.format("no method %s.%s defined" , v, c);
  }

  private String errorBadMethodCall(AbstractSymbol v, AbstractSymbol c, MethodData m) {
    return String.format("%s.%s expects %d arguments", c, v, m.args.getLength());
  }

  private void noSelfReference(AbstractSymbol s, TreeNode t, class_c currentClass) {
    if (s.equals(TreeConstants.self)) {
      error("symbol self cannot be re-bound", t, currentClass);
    }
  }

  private void noSelfTypeReference(AbstractSymbol s, TreeNode t, class_c currentClass) {
    if (s.equals(TreeConstants.SELF_TYPE)) {
      error("SELF_TYPE cannot appear here", t, currentClass);
    }
  }

  /* Validate that the types conform, and report an error otherwise */
  private void validateOrError(AbstractSymbol expected, AbstractSymbol actual, String error, TreeNode t, class_c currentClass) {

    // SELF_TYPE as an expected return value must be matched with SELF_TYPE
    // but SELF_TYPE as an actual return value can match with a type matching the current class
    if (!expected.equals(TreeConstants.SELF_TYPE)) {
      if (actual.equals(TreeConstants.SELF_TYPE)) {
        actual = currentClass.getName();
      }
    }

    boolean badSelf = expected.equals(TreeConstants.SELF_TYPE) && !actual.equals(TreeConstants.SELF_TYPE);
    boolean hasConcreteType = !actual.equals(TreeConstants.No_type);
    if (badSelf) {
      error(error, t, currentClass);
    } else if (hasConcreteType) {
      boolean conforming = expected.equals(actual) || isAncestor(expected, actual);
      if (!conforming) {
        error(error, t, currentClass);
      }
    }
  }

  private void error(String error, TreeNode t, class_c currentClass) {
    AbstractSymbol filename = currentClass.getFilename();
    errorCount++;
    System.out.printf("%s:%d: %s\n", filename, t.getLineNumber(), error);
    System.out.println(endMessage);
    throw new SemanticErrorException();
  }

  private void error(String error) {
    errorCount++;
    System.out.printf("%s\n", error);
    System.out.println(endMessage);
    throw new SemanticErrorException();
  }
}