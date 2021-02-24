// -*- mode: java -*- 
//
// file: cool-tree.m4
//
// This file defines the AST
//
//////////////////////////////////////////////////////////



import java.io.PrintStream;
import java.util.*;


/** Defines simple phylum Program */
abstract class Program extends TreeNode {
    protected Program(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);
    public abstract void semant();
    public abstract void cgen(PrintStream s);

}


/** Defines simple phylum Class_ */
abstract class Class_ extends TreeNode {
    protected Class_(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);
    public abstract AbstractSymbol getName();
    public abstract AbstractSymbol getParent();
    public abstract AbstractSymbol getFilename();
    public abstract Features getFeatures();

}


/** Defines list phylum Classes
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Classes extends ListNode {
    public final static Class elementClass = Class_.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Classes(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Classes" list */
    public Classes(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Class_" element to this list */
    public Classes appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Classes(lineNumber, copyElements());
    }
}


/** Defines simple phylum Feature */
abstract class Feature extends TreeNode {
    protected Feature(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);

}


/** Defines list phylum Features
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Features extends ListNode {
    public final static Class elementClass = Feature.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Features(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Features" list */
    public Features(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Feature" element to this list */
    public Features appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Features(lineNumber, copyElements());
    }
}


/** Defines simple phylum Formal */
abstract class Formal extends TreeNode {
    protected Formal(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);

}


/** Defines list phylum Formals
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Formals extends ListNode {
    public final static Class elementClass = Formal.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Formals(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Formals" list */
    public Formals(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Formal" element to this list */
    public Formals appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Formals(lineNumber, copyElements());
    }
}


/** Defines simple phylum Expression */
abstract class Expression extends TreeNode {
    protected Expression(int lineNumber) {
        super(lineNumber);
    }
    private AbstractSymbol type = null;                                 
    public AbstractSymbol get_type() { return type; }           
    public Expression set_type(AbstractSymbol s) { type = s; return this; } 
    public abstract void dump_with_types(PrintStream out, int n);
    public void dump_type(PrintStream out, int n) {
        if (type != null)
            { out.println(Utilities.pad(n) + ": " + type.getString()); }
        else
            { out.println(Utilities.pad(n) + ": _no_type"); }
    }
    public abstract void code(PrintStream s, AbstractSymbol containingClassName);
}

interface ObjectReturnable {
    public boolean requiresDereference();
}


/** Defines list phylum Expressions
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Expressions extends ListNode {
    public final static Class elementClass = Expression.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Expressions(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Expressions" list */
    public Expressions(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Expression" element to this list */
    public Expressions appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Expressions(lineNumber, copyElements());
    }
}


/** Defines simple phylum Case */
abstract class Case extends TreeNode {
    protected Case(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);

}


/** Defines list phylum Cases
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Cases extends ListNode {
    public final static Class elementClass = Case.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Cases(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Cases" list */
    public Cases(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Case" element to this list */
    public Cases appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Cases(lineNumber, copyElements());
    }
}


/** Defines AST constructor 'program'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class programc extends Program {
    public Classes classes;
    /** Creates "program" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for classes
      */
    public programc(int lineNumber, Classes a1) {
        super(lineNumber);
        classes = a1;
    }
    public TreeNode copy() {
        return new programc(lineNumber, (Classes)classes.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "program\n");
        classes.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_program");
        for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
	    ((Class_)e.nextElement()).dump_with_types(out, n + 1);
        }
    }
    /** This method is the entry point to the semantic checker.  You will
        need to complete it in programming assignment 4.
	<p>
        Your checker should do the following two things:
	<ol>
	<li>Check that the program is semantically correct
	<li>Decorate the abstract syntax tree with type information
        by setting the type field in each Expression node.
        (see tree.h)
	</ol>
	<p>
	You are free to first do (1) and make sure you catch all semantic
    	errors. Part (2) can be done in a second stage when you want
	to test the complete compiler.
    */
    public void semant() {
        /* ClassTable constructor may do some semantic analysis */
        // ClassTable classTable = new ClassTable(classes);
        
        /* some semantic analysis code may go here */

        // if (classTable.errors()) {
        //     System.err.println("Compilation halted due to static semantic errors.");
        //     System.exit(1);
        // }
    }
    /** This method is the entry point to the code generator.  All of the work
      * of the code generator takes place within CgenClassTable constructor.
      * @param s the output stream 
      * @see CgenClassTable
      * */
    public void cgen(PrintStream s) 
    {
        // spim wants comments to start with '#'
        // s.print("# start of generated code\n");

        CoolMap c = new CoolMap(classes);
        c.codeGenInit();

        // System.out.println(c.classGraph);
        // System.out.println(c.programSymbols);
        // System.out.println(c.classtags);

        CoolGen g = new CoolGen(c, s);
        g.layoutStaticData();
        g.layoutCode();

        // s.print("\n# end of generated code\n");
    }

}


/** Defines AST constructor 'class_'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class classc extends Class_ {
    public AbstractSymbol name;
    public AbstractSymbol parent;
    public Features features;
    public AbstractSymbol filename;
    /** Creates "class_" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for parent
      * @param a2 initial value for features
      * @param a3 initial value for filename
      */
    public classc(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Features a3, AbstractSymbol a4) {
        super(lineNumber);
        name = a1;
        parent = a2;
        features = a3;
        filename = a4;
    }
    public TreeNode copy() {
        return new classc(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(parent), (Features)features.copy(), copy_AbstractSymbol(filename));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "class_\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, parent);
        features.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, filename);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_class");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, parent);
        out.print(Utilities.pad(n + 2) + "\"");
        Utilities.printEscapedString(out, filename.getString());
        out.println("\"\n" + Utilities.pad(n + 2) + "(");
        for (Enumeration e = features.getElements(); e.hasMoreElements();) {
	    ((Feature)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
    }
    public AbstractSymbol getName()     { return name; }
    public AbstractSymbol getParent()   { return parent; }
    public AbstractSymbol getFilename() { return filename; }
    public Features getFeatures()       { return features; }

}


/** Defines AST constructor 'method'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class method extends Feature {
    public AbstractSymbol name;
    public Formals formals;
    public AbstractSymbol return_type;
    public Expression expr;
    /** Creates "method" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for formals
      * @param a2 initial value for return_type
      * @param a3 initial value for expr
      */
    public method(int lineNumber, AbstractSymbol a1, Formals a2, AbstractSymbol a3, Expression a4) {
        super(lineNumber);
        name = a1;
        formals = a2;
        return_type = a3;
        expr = a4;
    }
    public TreeNode copy() {
        return new method(lineNumber, copy_AbstractSymbol(name), (Formals)formals.copy(), copy_AbstractSymbol(return_type), (Expression)expr.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "method\n");
        dump_AbstractSymbol(out, n+2, name);
        formals.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, return_type);
        expr.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_method");
        dump_AbstractSymbol(out, n + 2, name);
        for (Enumeration e = formals.getElements(); e.hasMoreElements();) {
	    ((Formal)e.nextElement()).dump_with_types(out, n + 2);
        }
        dump_AbstractSymbol(out, n + 2, return_type);
	expr.dump_with_types(out, n + 2);
    }

}


/** Defines AST constructor 'attr'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class attr extends Feature {
    public AbstractSymbol name;
    public AbstractSymbol type_decl;
    public Expression init;
    /** Creates "attr" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      * @param a2 initial value for init
      */
    public attr(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        init = a3;
    }
    public TreeNode copy() {
        return new attr(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression)init.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "attr\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
        init.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_attr");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
	init.dump_with_types(out, n + 2);
    }

}


/** Defines AST constructor 'formal'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class formalc extends Formal {
    public AbstractSymbol name;
    public AbstractSymbol type_decl;
    /** Creates "formalc" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      */
    public formalc(int lineNumber, AbstractSymbol a1, AbstractSymbol a2) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
    }
    public TreeNode copy() {
        return new formalc(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "formal\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_formal");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
    }

}


/** Defines AST constructor 'branch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class branch extends Case {
    public AbstractSymbol name;
    public AbstractSymbol type_decl;
    public Expression expr;
    /** Creates "branch" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      * @param a2 initial value for expr
      */
    public branch(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        expr = a3;
    }
    public TreeNode copy() {
        return new branch(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression)expr.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "branch\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
        expr.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_branch");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
	expr.dump_with_types(out, n + 2);
    }

}


/** Defines AST constructor 'assign'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class assign extends Expression implements ObjectReturnable {
    public AbstractSymbol name;
    public Expression expr;
    /** Creates "assign" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for expr
      */
    public assign(int lineNumber, AbstractSymbol a1, Expression a2) {
        super(lineNumber);
        name = a1;
        expr = a2;
    }
    public TreeNode copy() {
        return new assign(lineNumber, copy_AbstractSymbol(name), (Expression)expr.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "assign\n");
        dump_AbstractSymbol(out, n+2, name);
        expr.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_assign");
        dump_AbstractSymbol(out, n + 2, name);
	expr.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}

    public boolean requiresDereference() {
        if (expr instanceof ObjectReturnable) {
            return ((ObjectReturnable) expr).requiresDereference();
        }
        return false;
    }
    public void code(PrintStream s, AbstractSymbol containingClassName) {

        // Push expr's value to stack
        expr.code(s, containingClassName);

        // Find the location of `name`
        Object[] ref = CoolGen.lookupObject(containingClassName, name);
        String reg = (String) ref[0];
        int offset = (Integer) ref[1];

        // If this assign's expression is an object, we need to 
        // dereference it for the purpose of assigning, but then 
        // push that pointer on the stack so that later code properly dereference it
        if (requiresDereference()) {
            CoolGen.emitPadded(new String[] {
                CoolGen.blockComment("store to " + name),
                CoolGen.pop("$t2"), // pop pointer to target (need it for later)
                CoolGen.push("$t2")
            }, s);
            CoolGen.emitObjectDeref(s);
            CoolGen.emitPadded(new String[] {
                CoolGen.pop("$t1"),
                String.format("sw $t1 %d(%s)", offset, reg),
                CoolGen.push("$t2"),
            }, s);
        } else {
            CoolGen.emitPadded(new String[] {
                CoolGen.blockComment("store to " + name),
                CoolGen.pop("$t1"),
                String.format("sw $t1 %d(%s)", offset, reg),
                CoolGen.push("$t1")
            }, s);
        }
    }
}


/** Defines AST constructor 'static_dispatch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class static_dispatch extends Expression {
    public Expression expr;
    public AbstractSymbol type_name;
    public AbstractSymbol name;
    public Expressions actual;
    /** Creates "static_dispatch" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for type_name
      * @param a2 initial value for name
      * @param a3 initial value for actual
      */
    public static_dispatch(int lineNumber, Expression a1, AbstractSymbol a2, AbstractSymbol a3, Expressions a4) {
        super(lineNumber);
        expr = a1;
        type_name = a2;
        name = a3;
        actual = a4;
    }
    public TreeNode copy() {
        return new static_dispatch(lineNumber, (Expression)expr.copy(), copy_AbstractSymbol(type_name), copy_AbstractSymbol(name), (Expressions)actual.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "static_dispatch\n");
        expr.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, type_name);
        dump_AbstractSymbol(out, n+2, name);
        actual.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_static_dispatch");
	expr.dump_with_types(out, n + 2);
        dump_AbstractSymbol(out, n + 2, type_name);
        dump_AbstractSymbol(out, n + 2, name);
        out.println(Utilities.pad(n + 2) + "(");
        for (Enumeration e = actual.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}

    // TODO: Fix like dispatch
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        AbstractSymbol subjectType = expr.get_type();
        Expression ae =  expr;

        if (subjectType.equals(TreeConstants.SELF_TYPE)) {
            subjectType = containingClassName;
        }

        // Emit subject of dispatch to stack
        ae.code(s, containingClassName);
        if (expr instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) expr;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        // Store subject of dispatch, preserve registers and setup frame
        CoolGen.emitPadded(new String[] {
            CoolGen.blockComment(String.format("call to %s@%s.%s", subjectType, type_name, name)),
            CoolGen.pop("$t1"),
            CoolGen.comment("preserve registers"),
            CoolGen.push("$a0"),
            CoolGen.push("$ra"),
            CoolGen.push("$fp"),
            CoolGen.push("$s1"),
            CoolGen.push("$s2"),
            CoolGen.push("$s3"),
            CoolGen.comment("setup self object of dispatch"),
            "move $a0 $t1",
            CoolGen.comment("push args"),
        }, s);

        // Push arguments
        for (int i = 0; i < actual.getLength(); i++) {
            Expression e = (Expression) actual.getNth(i);
            e.code(s, containingClassName);

            // Pass current arguments as callee arguments directly, 
            // by pushing an absolute address, not one relative to the current frame
            if (e instanceof ObjectReturnable) {
                ObjectReturnable o = (ObjectReturnable) e;
                if (o.requiresDereference()) {
                    CoolGen.emitObjectDeref(s);
                }
            }
        }

        // Setup frame
        CoolGen.emitPadded(new String[] {
            "move $t1 $sp",
            "move $s1 $sp", 
            "sub $s1 $s1 4", // where locals begin
            "move $s3 $s1",
            "sub $sp $sp " + CoolGen.LOCAL_SIZE,
            "move $s2 $sp",  // where locals end
            "addi $t1 $t1 " + 4 * (actual.getLength() - 1),
            "move $fp $t1", // $fp points to first arg
        }, s);

        // Perform the call
        CoolGen.emitPadded(new String[] {
            CoolGen.comment("find jump address and jump"),
            String.format("lw $t1 %s_method_%s", type_name, name),
            "jalr $t1",
        }, s);

        // Destroy frame and restore registers
        CoolGen.emitPadded(new String[] {
            // ".globl postCall" + name,
            // "postCall" + name + ":",
            "move $sp $fp",
            "add $sp $sp 4", // Return to top of stack before-call
            CoolGen.pop("$s3"),
            CoolGen.pop("$s2"),
            CoolGen.pop("$s1"),
            CoolGen.pop("$fp"),
            CoolGen.pop("$ra"),
            CoolGen.pop("$t1"),
            CoolGen.push("$a0"),
            "move $a0 $t1"
        }, s);
    }

}


/** Defines AST constructor 'dispatch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. 

Note: these are not ObjectReturnable b/c the method epilogue always deref's the
method's final expression if necessary
*/
class dispatch extends Expression {
    public Expression expr;
    public AbstractSymbol name;
    public Expressions actual;
    /** Creates "dispatch" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for name
      * @param a2 initial value for actual
      */
    public dispatch(int lineNumber, Expression a1, AbstractSymbol a2, Expressions a3) {
        super(lineNumber);
        expr = a1;
        name = a2;
        actual = a3;
    }
    public TreeNode copy() {
        return new dispatch(lineNumber, (Expression)expr.copy(), copy_AbstractSymbol(name), (Expressions)actual.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "dispatch\n");
        expr.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, name);
        actual.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_dispatch");
	expr.dump_with_types(out, n + 2);
        dump_AbstractSymbol(out, n + 2, name);
        out.println(Utilities.pad(n + 2) + "(");
        for (Enumeration e = actual.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        AbstractSymbol subjectType = expr.get_type();
        String here = String.format("%s_dispatch_%d_%d", containingClassName, lineNumber, hashCode());

        if (subjectType.equals(TreeConstants.SELF_TYPE)) {
            subjectType = containingClassName;
        }

        // Preserve registers
        CoolGen.emitRegisterPreserve(s);

        // Allocate space for callee locals as part of new frame and calculate offsets
        CoolGen.emitFramePrologue(s);

        // Push arguments
        CoolGen.emitPadded(CoolGen.comment("frame setup: push arguments"), s);
        for (int i = 0; i < actual.getLength(); i++) {
            Expression e = (Expression) actual.getNth(i);
            e.code(s, containingClassName);

            // Pass current arguments as callee arguments directly, 
            // by pushing an absolute address, not one relative to the current frame
            if (e instanceof ObjectReturnable) {
                ObjectReturnable o = (ObjectReturnable) e;
                if (o.requiresDereference()) {
                    CoolGen.emitObjectDeref(s);
                }
            }
        }

        // Emit subject of dispatch to $a0
        CoolGen.emitPadded(CoolGen.comment("frame setup: subject of dispatch"), s);
        expr.code(s, containingClassName);
        if (expr instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) expr;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }
        CoolGen.emitPadded(CoolGen.pop("$a0"), s);

        // Handle attempted dispatch on NULL
        CoolGen.emitPadded(new String[] {
            CoolGen.comment("null dispatch check"),
            String.format("bne $a0 $zero %s_notnull", here),
            String.format(
                "la $a0 %s", 
                ((StringSymbol) AbstractTable.stringtable.lookup(
                    CoolGen.map.programSymbols.get(containingClassName).classc.getFilename().toString()
                )).codeRef()
            ),
            String.format("li $t1 %d", lineNumber),
            String.format("j %s", CoolGen.NULL_DISPATCH)
        }, s);

        CoolGen.emitLabel(String.format("%s_notnull", here), s);

        // Finish the frame by setting up frame registers for callee ($fp, $s1-$s3),
        // and setup object of dispatch
        CoolGen.emitFrameEpilogue(s);

        // Perform the call
        CoolGen.emitPadded(new String[] {
            CoolGen.comment("find jump address and jump"),

            // Find the appropriate method table
            "lw $t1 ($a0)",
            "mul $t1 $t1 4", // classtag * 4 is the offset from methodTabTab for the right method table
            "la $t2 methodTabTab",
            "add $t1 $t1 $t2",

            // Find the appropriate method in the table
            "lw $t1 ($t1)",
            String.format("addi $t1 $t1 %d", CoolGen.lookupMethod(subjectType, name).offset),
            "lw $t1 ($t1)",
            // ".globl preCall" + name,
            // "preCall" + name + ":",
            "jalr $t1",
        }, s);

        // Destroy frame and restore registers
        CoolGen.emitFrameCleanup(s);
        CoolGen.emitRegisterRestore(s);
        CoolGen.emitPadded(new String[] {
            // ".globl postCall" + name,
            // "postCall" + name + ":",
            CoolGen.pop("$t1"),
            CoolGen.push("$a0"),
            "move $a0 $t1"
        }, s);
    }
}


/** Defines AST constructor 'cond'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class cond extends Expression {
    public Expression pred;
    public Expression then_exp;
    public Expression else_exp;
    /** Creates "cond" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for pred
      * @param a1 initial value for then_exp
      * @param a2 initial value for else_exp
      */
    public cond(int lineNumber, Expression a1, Expression a2, Expression a3) {
        super(lineNumber);
        pred = a1;
        then_exp = a2;
        else_exp = a3;
    }
    public TreeNode copy() {
        return new cond(lineNumber, (Expression)pred.copy(), (Expression)then_exp.copy(), (Expression)else_exp.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "cond\n");
        pred.dump(out, n+2);
        then_exp.dump(out, n+2);
        else_exp.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_cond");
	pred.dump_with_types(out, n + 2);
	then_exp.dump_with_types(out, n + 2);
	else_exp.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        String here = String.format("%s_cond_%d_%d", containingClassName, lineNumber, hashCode());

        // Generate pred expression (deref)
        pred.code(s, containingClassName);
        if (pred instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) pred;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        // Compare against bool_const0 , jump to here_false
        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t1"),
            "la $t2 bool_const0",
            String.format("beq $t1 $t2 %s_false", here),
        }, s);

        // pred is true
        then_exp.code(s, containingClassName);
        if (then_exp instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) then_exp;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }
        CoolGen.emitPadded(String.format("j %s_epilogue", here), s);

        // pred is false
        CoolGen.emitLabel(String.format("%s_false", here), s);
        else_exp.code(s, containingClassName);
        if (else_exp instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) else_exp;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        CoolGen.emitLabel(String.format("%s_epilogue", here), s);
    }


}


/** Defines AST constructor 'loop'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class loop extends Expression {
    public Expression pred;
    public Expression body;
    /** Creates "loop" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for pred
      * @param a1 initial value for body
      */
    public loop(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        pred = a1;
        body = a2;
    }
    public TreeNode copy() {
        return new loop(lineNumber, (Expression)pred.copy(), (Expression)body.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "loop\n");
        pred.dump(out, n+2);
        body.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_loop");
	pred.dump_with_types(out, n + 2);
	body.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        String here = String.format("%s_loop_%d_%d", containingClassName, lineNumber, hashCode());

        CoolGen.emitLabel(String.format("%s_predicate", here), s);
        pred.code(s, containingClassName);
        if (pred instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) pred;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }
        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t1"),
            "la $t2 bool_const1",
            String.format("beq $t1 $t2 %s_true", here),
            String.format("j %s_epilogue", here),
        }, s);

        // Loop predicate true
        CoolGen.emitLabel(String.format("%s_true", here), s);
        body.code(s, containingClassName);
        if (body instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) body;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }
        CoolGen.emitPadded(String.format("j %s_predicate", here), s);

        CoolGen.emitLabel(String.format("%s_epilogue", here), s);
    }
}


/** Defines AST constructor 'typcase'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class typcase extends Expression {
    public Expression expr;
    public Cases cases;
    /** Creates "typcase" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for cases
      */
    public typcase(int lineNumber, Expression a1, Cases a2) {
        super(lineNumber);
        expr = a1;
        cases = a2;
    }
    public TreeNode copy() {
        return new typcase(lineNumber, (Expression)expr.copy(), (Cases)cases.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "typcase\n");
        expr.dump(out, n+2);
        cases.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_typcase");
	expr.dump_with_types(out, n + 2);
        for (Enumeration e = cases.getElements(); e.hasMoreElements();) {
	    ((Case)e.nextElement()).dump_with_types(out, n + 2);
        }
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        String here = String.format("%s_case%d", containingClassName, lineNumber);

        // Evaulate case expression 
        expr.code(s, containingClassName);

        if (expr instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) expr;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }
        CoolGen.emitPadded(CoolGen.pop("$t2"), s);

        // Check for null pointer 
        CoolGen.emitPadded(new String[] {
            String.format("bne $t2 $zero %s_nonnullcase", here),
            String.format(
                "la $a0 %s", 
                ((StringSymbol) AbstractTable.stringtable.lookup(
                    CoolGen.map.programSymbols.get(containingClassName).classc.getFilename().toString()
                )).codeRef()
            ),
            String.format("li $t1 %d", lineNumber),
            String.format("j %s", CoolGen.NULL_CASE)
        }, s);

        // Emit instructions to choose the right case
        CoolGen.emitLabel(String.format("%s_nonnullcase", here), s);
        CoolGen.emitPadded(String.format("lw $t3 ($t2)"), s);
        for (int i = 0; i < cases.getLength(); i++) {
            branch b = (branch) cases.getNth(i);

            // Store classtag of b into $t4 and jump to appropriate label `${here}_${i}`
            // if $t3 == $t4
            CoolGen.emitPadded(new String[] {
                "lw $t4 " + String.format(CoolGen.PROTOBJ, b.type_decl),
                "beq $t3 $t4 " + String.format("%s_%d", here, i)
            }, s);
        }

        // No matching case, emit abort 
        CoolGen.emitPadded("j " + CoolGen.NO_MATCHING_CASE, s);

        // For each branch, generate a mini-routine
        for (int i = 0; i < cases.getLength(); i++) {
            branch b = (branch) cases.getNth(i);
            CoolGen.emitLabel(String.format("%s_%d", here, i), s);

            // Add the symbol this branch creates to table
            CoolGen.newObjectScope(containingClassName);
            CoolGen.addObject(containingClassName, b.name, b.type_decl);

            // Create it in the local section of frame, assigning it
            // the value of the case expression
            CoolGen.emitPadded(CoolGen.push("$t2"), s);
            CoolGen.emitNewLocal(s);

            // Generate it's expression
            b.expr.code(s, containingClassName);
            CoolGen.endObjectScope(containingClassName);
            if (b.expr instanceof ObjectReturnable) {
                ObjectReturnable o = (ObjectReturnable) b.expr;
                if (o.requiresDereference()) {
                    CoolGen.emitObjectDeref(s);
                }
            }

            CoolGen.emitPadded(String.format("j %s_epilogue", here), s);
        }

        // Universal end of expression
        CoolGen.emitLabel(String.format("%s_epilogue", here), s);
    }


}


/** Defines AST constructor 'block'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class block extends Expression {
    public Expressions body;
    /** Creates "block" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for body
      */
    public block(int lineNumber, Expressions a1) {
        super(lineNumber);
        body = a1;
    }
    public TreeNode copy() {
        return new block(lineNumber, (Expressions)body.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "block\n");
        body.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_block");
        for (Enumeration e = body.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {

        // Evaluate each expression, removing it's result from the stack
        // Leave the last expression's result on stack as expression's value
        CoolGen.emitPadded(CoolGen.comment("block expression"), s);
        for (int i = 0; i < body.getLength(); i++) {
            Expression e = (Expression) body.getNth(i);
            e.code(s, containingClassName);
            if (i < body.getLength() - 1) {
                CoolGen.emitPadded(CoolGen.pop("$t1"), s);
            } 
            else if (e instanceof ObjectReturnable) {
                ObjectReturnable o = (ObjectReturnable) e;
                if (o.requiresDereference()) {
                    CoolGen.emitObjectDeref(s);
                }
            }
        }
    }
}


/** Defines AST constructor 'let'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class let extends Expression {
    public AbstractSymbol identifier;
    public AbstractSymbol type_decl;
    public Expression init;
    public Expression body;
    /** Creates "let" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for identifier
      * @param a1 initial value for type_decl
      * @param a2 initial value for init
      * @param a3 initial value for body
      */
    public let(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3, Expression a4) {
        super(lineNumber);
        identifier = a1;
        type_decl = a2;
        init = a3;
        body = a4;
    }
    public TreeNode copy() {
        return new let(lineNumber, copy_AbstractSymbol(identifier), copy_AbstractSymbol(type_decl), (Expression)init.copy(), (Expression)body.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "let\n");
        dump_AbstractSymbol(out, n+2, identifier);
        dump_AbstractSymbol(out, n+2, type_decl);
        init.dump(out, n+2);
        body.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_let");
        dump_AbstractSymbol(out, n + 2, identifier);
        dump_AbstractSymbol(out, n + 2, type_decl);
        init.dump_with_types(out, n + 2);
        body.dump_with_types(out, n + 2);
        dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {

        CoolGen.newObjectScope(containingClassName);
        CoolGen.addObject(containingClassName, identifier, type_decl);

        CoolGen.emitPadded(new String[] {
            CoolGen.blockComment("new let binding"),
            CoolGen.comment(String.format("create new local variable %s of type %s", identifier, type_decl))
        }, s);

        // Default init for new local
        if (CoolGen.initByPrototype(type_decl)) {
            if (type_decl.equals(TreeConstants.Bool)) {
                CoolGen.emitPadded(new String[] {
                    "la $t1 bool_const0",
                    CoolGen.push("$t1")
                }, s);
            } else {
                CoolGen.emitObjectCopy(type_decl.toString(), s);
            }
        } else {
            CoolGen.emitPadded(new String[] {
                CoolGen.push("$zero")
            }, s);
        }
        CoolGen.emitNewLocal(s);

        // If init !== no_expr, replace local with result
        if (!(init instanceof no_expr)) {
            CoolGen.emitPadded(new String[] {
                CoolGen.comment("evaulate let init"),
            }, s);
            init.code(s, containingClassName);
            CoolGen.replaceLocal(s);
        }

        // Evaluate body
        CoolGen.emitPadded(new String[] {
            CoolGen.comment("evaluate let body")
        }, s);
        body.code(s, containingClassName);
        if (body instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) body;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        // Remove the local since it's unneeded now
        CoolGen.emitPadded("addi $s1 $s1 4", s);

        // Exit scope
        CoolGen.endObjectScope(containingClassName);
    }
}


/** Defines AST constructor 'plus'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class plus extends Expression {
    public Expression e1;
    public Expression e2;
    /** Creates "plus" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public plus(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new plus(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "plus\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_plus");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}

    public void code(PrintStream s, AbstractSymbol containingClassName) {
        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        e2.code(s, containingClassName);
        if (e2 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e2;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        int valOffset = (Integer) CoolGen.lookupObject(
            AbstractTable.idtable.lookup("Int"),
            AbstractTable.idtable.lookup("_val")
        )[1];

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t5"),
            CoolGen.pop("$t4"),
            CoolGen.blockComment("load values of Int operands"),
            ("lw $t4 " + valOffset + "($t4)"),
            ("lw $t5 " + valOffset + "($t5)"),

            CoolGen.blockComment("compute sum of Int operands"),
            ("add $t4 $t4 $t5"),
            CoolGen.push("$t4")
        }, s);

        CoolGen.blockComment("save sum into new Int");
        CoolGen.emitNewInt(s);
    }
}


/** Defines AST constructor 'sub'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class sub extends Expression {
    public Expression e1;
    public Expression e2;
    /** Creates "sub" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public sub(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new sub(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "sub\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_sub");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        e2.code(s, containingClassName);
        if (e2 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e2;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        int valOffset = (Integer) CoolGen.lookupObject(
            AbstractTable.idtable.lookup("Int"),
            AbstractTable.idtable.lookup("_val")
        )[1];

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t5"),
            CoolGen.pop("$t4"),
            CoolGen.comment("load values of Int operands"),
            ("lw $t4 " + valOffset + "($t4)"),
            ("lw $t5 " + valOffset + "($t5)"),

            CoolGen.comment("compute difference of Int operands"),
            ("sub $t4 $t4 $t5"),
            CoolGen.push("$t4")
        }, s);

        CoolGen.comment("save difference into new Int");
        CoolGen.emitNewInt(s);
    }
}


/** Defines AST constructor 'mul'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class mul extends Expression {
    public Expression e1;
    public Expression e2;
    /** Creates "mul" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public mul(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new mul(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "mul\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_mul");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        e2.code(s, containingClassName);
        if (e2 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e2;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        int valOffset = (Integer) CoolGen.lookupObject(
            AbstractTable.idtable.lookup("Int"),
            AbstractTable.idtable.lookup("_val")
        )[1];

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t5"),
            CoolGen.pop("$t4"),
            CoolGen.comment("load values of Int operands"),
            ("lw $t4 " + valOffset + "($t4)"),
            ("lw $t5 " + valOffset + "($t5)"),

            CoolGen.comment("compute product of Int operands"),
            ("mul $t4 $t4 $t5"),
            CoolGen.push("$t4")
        }, s);

        CoolGen.comment("save product into new Int");
        CoolGen.emitNewInt(s);
    }


}


/** Defines AST constructor 'divide'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class divide extends Expression {
    public Expression e1;
    public Expression e2;
    /** Creates "divide" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public divide(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new divide(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "divide\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_divide");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        e2.code(s, containingClassName);
        if (e2 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e2;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        int valOffset = (Integer) CoolGen.lookupObject(
            AbstractTable.idtable.lookup("Int"),
            AbstractTable.idtable.lookup("_val")
        )[1];

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t5"),
            CoolGen.pop("$t4"),
            CoolGen.comment("load values of Int operands"),
            ("lw $t4 " + valOffset + "($t4)"),
            ("lw $t5 " + valOffset + "($t5)"),

            CoolGen.comment("compute quotient of Int operands"),
            ("div $t4 $t4 $t5"),
            CoolGen.push("$t4")
        }, s);

        CoolGen.comment("save quotient into new Int");
        CoolGen.emitNewInt(s);
    }
}


/** Defines AST constructor 'neg'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class neg extends Expression {
    public Expression e1;
    /** Creates "neg" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public neg(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }
    public TreeNode copy() {
        return new neg(lineNumber, (Expression)e1.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "neg\n");
        e1.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_neg");
	e1.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        int valOffset = (Integer) CoolGen.lookupObject(
            AbstractTable.idtable.lookup("Int"),
            AbstractTable.idtable.lookup("_val")
        )[1];

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t4"),
            CoolGen.comment("load values of Int operand"),
            ("lw $t4 " + valOffset + "($t4)"),

            CoolGen.comment("multiply Int operand by -1"),
            ("neg $t4 $t4"),
            CoolGen.push("$t4")
        }, s);

        CoolGen.comment("save negated operand into new Int");
        CoolGen.emitNewInt(s);
    }

}


/** Defines AST constructor 'lt'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class lt extends Expression {
    public Expression e1;
    public Expression e2;
    /** Creates "lt" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public lt(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new lt(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "lt\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_lt");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        String here = String.format("%s_lt_%d_%d", containingClassName, lineNumber, hashCode());

        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }
        e2.code(s, containingClassName);
        if (e2 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e2;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        int valOffset = (Integer) CoolGen.lookupObject(
            TreeConstants.Int,
            TreeConstants.val
        )[1];

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t5"),
            CoolGen.pop("$t4"),
            ("lw $t4 " + valOffset + "($t4)"),
            ("lw $t5 " + valOffset + "($t5)"),
            "slt $t1 $t4 $t5",
            String.format("beq $t1 $zero %s_false", here),
            "la $t1 bool_const1",
            CoolGen.push("$t1"),
            String.format("j %s_epilogue", here),
        }, s);

        CoolGen.emitLabel(String.format("%s_false", here), s);
        CoolGen.emitPadded(new String[] {
            "la $t1 bool_const0",
            CoolGen.push("$t1"),
        }, s);

        CoolGen.emitLabel(String.format("%s_epilogue", here), s);
    }


}


/** Defines AST constructor 'eq'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class eq extends Expression {
    public Expression e1;
    public Expression e2;
    /** Creates "eq" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public eq(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new eq(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "eq\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_eq");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        String here = String.format("%s_eq_%d_%d", containingClassName, lineNumber, hashCode());

        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }
        e2.code(s, containingClassName);
        if (e2 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e2;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        // Arguments to equality_test
        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t2"),
            CoolGen.pop("$t1")
        }, s);

        if (CoolGen.initByPrototype(e1.get_type())) {
            CoolGen.emitPadded(CoolGen.comment("builtin equality test"), s);

            CoolGen.emitRegisterPreserve(s);
            CoolGen.emitFramePrologue(s);

            // If equal, true is returned, else false
            CoolGen.emitPadded(new String[] {
                "la $a0 bool_const1",
                "la $a1 bool_const0"
            }, s);

            CoolGen.emitFrameEpilogue(s);
            CoolGen.emitPadded(String.format("jal %s", CoolGen.EQ_TEST), s);
            CoolGen.emitFrameCleanup(s);
            CoolGen.emitRegisterRestore(s);

            // Push result ($a0) to stack
            CoolGen.emitPadded(new String[] {
                "move $t1 $a0",
                CoolGen.pop("$a0"),
                CoolGen.push("$t1")
            }, s);
        } else {

            // Check if pointed-to values are the same
            CoolGen.emitPadded(new String[] {
                String.format("beq $t1 $t2 %s_true", here),
                "la $t1 bool_const0",
                CoolGen.push("$t1"),
                String.format("j %s_epilogue", here)
            }, s);

            CoolGen.emitLabel(String.format("%s_true", here), s);
            CoolGen.emitPadded(new String[] {
                "la $t1 bool_const1",
                CoolGen.push("$t1"),
            }, s);

            CoolGen.emitLabel(String.format("%s_epilogue", here), s);
        }
    }
}


/** Defines AST constructor 'leq'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class leq extends Expression {
    public Expression e1;
    public Expression e2;
    /** Creates "leq" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public leq(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new leq(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "leq\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_leq");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        String here = String.format("%s_leq_%d_%d", containingClassName, lineNumber, hashCode());

        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }
        e2.code(s, containingClassName);
        if (e2 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e2;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        int valOffset = (Integer) CoolGen.lookupObject(
            TreeConstants.Int,
            TreeConstants.val
        )[1];

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t5"),
            CoolGen.pop("$t4"),
            ("lw $t4 " + valOffset + "($t4)"),
            ("lw $t5 " + valOffset + "($t5)"),
            "sle $t1 $t4 $t5",
            String.format("beq $t1 $zero %s_false", here),
            "la $t1 bool_const1",
            CoolGen.push("$t1"),
            String.format("j %s_epilogue", here),
        }, s);

        CoolGen.emitLabel(String.format("%s_false", here), s);
        CoolGen.emitPadded(new String[] {
            "la $t1 bool_const0",
            CoolGen.push("$t1"),
        }, s);

        CoolGen.emitLabel(String.format("%s_epilogue", here), s);
    }
}


/** Defines AST constructor 'comp'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class comp extends Expression {
    public Expression e1;
    /** Creates "comp" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public comp(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }
    public TreeNode copy() {
        return new comp(lineNumber, (Expression)e1.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "comp\n");
        e1.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_comp");
	e1.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        String here = String.format("%s_comp_%d_%d", containingClassName, lineNumber, hashCode());

        Object[] ref = CoolGen.lookupObject(TreeConstants.Bool, TreeConstants.val);
        int offset = (Integer) ref[1];

        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t1"),  
            "la $t2 bool_const0",
            String.format("beq $t1 $t2 %s_false", here),
            CoolGen.push("$t2"),
            String.format("j %s_epilogue", here)
        }, s);

        CoolGen.emitLabel(String.format("%s_false", here), s);
        CoolGen.emitPadded(new String[] {
            "la $t2 bool_const1",
            CoolGen.push("$t2"),
        }, s);

        CoolGen.emitLabel(String.format("%s_epilogue", here), s);
    }


}


/** Defines AST constructor 'int_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class int_const extends Expression {
    public AbstractSymbol token;
    /** Creates "int_const" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for token
      */
    public int_const(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        token = a1;
    }
    public TreeNode copy() {
        return new int_const(lineNumber, copy_AbstractSymbol(token));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "int_const\n");
        dump_AbstractSymbol(out, n+2, token);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_int");
	dump_AbstractSymbol(out, n + 2, token);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method method is provided
      * to you as an example of code generation.
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        CgenSupport.emitLoadInt("$t1",
                                    (IntSymbol)AbstractTable.inttable.lookup(token.getString()), s);
        CoolGen.emitPadded(new String[] { CoolGen.push("$t1") }, s);
    }

}


/** Defines AST constructor 'bool_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class bool_const extends Expression {
    public Boolean val;
    /** Creates "bool_const" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for val
      */
    public bool_const(int lineNumber, Boolean a1) {
        super(lineNumber);
        val = a1;
    }
    public TreeNode copy() {
        return new bool_const(lineNumber, copy_Boolean(val));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "bool_const\n");
        dump_Boolean(out, n+2, val);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_bool");
	dump_Boolean(out, n + 2, val);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method method is provided
      * to you as an example of code generation.
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        CgenSupport.emitLoadBool("$t1", new BoolConst(val), s);
        CoolGen.emitPadded(CoolGen.push("$t1"), s);
    }

}


/** Defines AST constructor 'string_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class string_const extends Expression {
    public AbstractSymbol token;
    /** Creates "string_const" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for token
      */
    public string_const(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        token = a1;
    }
    public TreeNode copy() {
        return new string_const(lineNumber, copy_AbstractSymbol(token));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "string_const\n");
        dump_AbstractSymbol(out, n+2, token);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_string");
	out.print(Utilities.pad(n + 2) + "\"");
	Utilities.printEscapedString(out, token.getString());
	out.println("\"");
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method method is provided
      * to you as an example of code generation.
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        CgenSupport.emitLoadString("$t1",
         (StringSymbol)AbstractTable.stringtable.lookup(token.getString()), s);
        
        CoolGen.emitPadded(CoolGen.push("$t1"), s);
    }

}


/** Defines AST constructor 'new_'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class new_ extends Expression {
    public AbstractSymbol type_name;
    /** Creates "new_" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for type_name
      */
    public new_(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        type_name = a1;
    }
    public TreeNode copy() {
        return new new_(lineNumber, copy_AbstractSymbol(type_name));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "new_\n");
        dump_AbstractSymbol(out, n+2, type_name);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_new");
	dump_AbstractSymbol(out, n + 2, type_name);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {

        // Bools are initialized to bool_const0
        if (type_name.equals(TreeConstants.Bool)) {
            CoolGen.emitPadded(new String[] {
                "la $t1 bool_const0",
                CoolGen.push("$t1")
            }, s);
        }
        else {
            // Call Object.copy on the prototype 
            CoolGen.emitObjectCopy(type_name.toString(), s);

            // For each ancestor of `type_name`, call it's initialize routine
            // Skip Object (first ancestor of all classes)
            ListIterator<AbstractSymbol> ancestry = CoolGen.map.getAncestry(type_name).listIterator(1);
            while (ancestry.hasNext()) {
                AbstractSymbol ancestor = ancestry.next();

                CoolGen.emitPadded(new String[] {
                    CoolGen.comment(String.format("initializing ancestor %s for class %s", ancestor, type_name)),
                    CoolGen.pop("$t1"),
                }, s);

                CoolGen.emitRegisterPreserve(s);

                // Inititalize the class
                CoolGen.emitPadded(new String[] {
                    "move $a0 $t1",
                    String.format("jal " + CoolGen.ATTRINIT, ancestor),
                    "move $t1 $a0",
                }, s);

                CoolGen.emitRegisterRestore(s);
                CoolGen.emitPadded(CoolGen.pop("$a0"), s);

                CoolGen.emitPadded(CoolGen.push("$t1"), s);

            }
        }
    }


}


/** Defines AST constructor 'isvoid'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class isvoid extends Expression {
    public Expression e1;
    /** Creates "isvoid" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public isvoid(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }
    public TreeNode copy() {
        return new isvoid(lineNumber, (Expression)e1.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "isvoid\n");
        e1.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_isvoid");
	e1.dump_with_types(out, n + 2);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        String here = String.format("%s_isvoid_%d_%d", containingClassName, lineNumber, hashCode());

        e1.code(s, containingClassName);
        if (e1 instanceof ObjectReturnable) {
            ObjectReturnable o = (ObjectReturnable) e1;
            if (o.requiresDereference()) {
                CoolGen.emitObjectDeref(s);
            }
        }

        CoolGen.emitPadded(new String[] {
            CoolGen.pop("$t1"),
            String.format("beq $t1 $zero %s_true", here),
            "la $t1 bool_const0",
            CoolGen.push("$t1"),
            String.format("j %s_epilogue", here)
        }, s);

        CoolGen.emitLabel(String.format("%s_true", here), s);
        CoolGen.emitPadded(new String[] {
            "la $t1 bool_const1",
            CoolGen.push("$t1"),
        }, s);
        
        CoolGen.emitLabel(String.format("%s_epilogue", here), s);
    }


}


/** Defines AST constructor 'no_expr'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class no_expr extends Expression {
    /** Creates "no_expr" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      */
    public no_expr(int lineNumber) {
        super(lineNumber);
    }
    public TreeNode copy() {
        return new no_expr(lineNumber);
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "no_expr\n");
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_no_expr");
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {}
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        CoolGen.emitPadded(CoolGen.push("$zero"), s);
    }


}


/** Defines AST constructor 'object'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class object extends Expression implements ObjectReturnable {
    public AbstractSymbol name;
    /** Creates "object" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      */
    public object(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        name = a1;
    }
    public TreeNode copy() {
        return new object(lineNumber, copy_AbstractSymbol(name));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "object\n");
        dump_AbstractSymbol(out, n+2, name);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_object");
	dump_AbstractSymbol(out, n + 2, name);
	dump_type(out, n);
    }
    /** Generates code for this expression.  This method is to be completed 
      * in programming assignment 5.  (You may add or remove parameters as
      * you wish.)
      * @param s the output stream 
      * */
    public void code(PrintStream s) {
    }
    public boolean requiresDereference() {
        return !name.equals(TreeConstants.self);
    }
    public void code(PrintStream s, AbstractSymbol containingClassName) {
        Object[] ref = CoolGen.lookupObject(containingClassName, name);
        String reg = (String) ref[0];
        Integer offset = (Integer) ref[1];

        CoolGen.emitPadded(new String[] {
            CoolGen.comment(String.format("# lookup for symbol %s for current class %s", name, containingClassName)),
            String.format("addi $t1 %s %s", reg, offset),
            CoolGen.push("$t1"),
        }, s);
    }
}


