import java.util.*;

// Object is the implicit parent of all classes
// IO, Int, String, Bool cannot be extended or redefined
// All mentioned classes must be defined
// All programs must have Main class
// Inheritance may not be cyclical: class A inherits from B, and class B inherits from A is not allowed
public class CoolAnalysis {

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

  public static void analyze(Program p, Classes classes) {
    init();
    buildGraph(classes);
    System.out.printf("\n---Inheritance Graph---\n");
    printGraph();
    System.out.printf("\n\n");
    System.out.printf("---Undefined Classes---\n");
    undefinedClasses();
    System.out.printf("\n\n");
    System.out.printf("---Cycles---\n");
    findCycles();
    System.out.printf("\n\n");
    System.out.printf("---Main class---\n");
    findMainClass();
    System.out.printf("\n\n");
  }


  // Walk AST from root to discover all the Symbols

  // Walk AST from leaves to type check all non-leaf nodes
}