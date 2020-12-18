/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;
import java.lang.reflect.*;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    static int integer_count = 0;
    int get_integer_count() {
        return integer_count;
    }
    void inc_integer_count() {
        integer_count++;
    }

    static int type_count = 0;
    int get_type_count() {
        return type_count;
    }
    void inc_type_count() {
        type_count++;
    }

    static int object_count = 0;
    int get_object_count() {
        return object_count;
    }
    void inc_object_count() {
        object_count++;
    }

    private int curr_lineno = 1;
    int get_curr_lineno() {
        return curr_lineno;
    }
    void inc_curr_lineno() {
        curr_lineno++;
    }

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    private AbstractSymbol filename;

    void set_filename(String fname) {
        filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
        return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
	/* If necessary, add code for other states here, e.g:
	   case COMMENT:
	   ...
	   break;
	*/
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup

%%

<YYINITIAL>"=>"			{ /* Sample lexical rule for "=>" arrow.
                                     Further lexical rules should be defined
                                     here, after the last %% separator */
                                  return new Symbol(TokenConstants.DARROW); }

[\40\n\f\r\t\v] {
    if (yytext().equals("\n")) {
        inc_curr_lineno();
    }

    // TODO: What symbol is supposed to represent whitespace?
    return new Symbol(TokenConstants.MULT);
}

[0-9]+ {
    // Digits
    AbstractSymbol sym = new IntSymbol(yytext(), yytext().length(), get_integer_count());
    inc_integer_count();
    return new Symbol(TokenConstants.INT_CONST, sym);
}

(class|else|fi|if|in|inherits|isvoid|let|loop|pool|then|while|case|esac|new|of|not|CLASS|ELSE|FI|IF|IN|INHERITS|ISVOID|LET|LOOP|POOL|THEN|WHILE|CASE|ESAC|NEW|OF|NOT) {
    // Non-boolean Keywords
    Field[] tokens = TokenConstants.class.getDeclaredFields();
    for (Field f : tokens) {
        if (f.getName().equals(yytext().toUpperCase())) {
            try {
                return new Symbol((Integer) f.get(TokenConstants.class));
            } catch (Exception e) {
                // whatever
                System.out.println("error");
            }
        }
    }
}

[a-z][a-zA-Z0-9]* {
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}

(Object|IO|Int|String|Bool|([A-Z][a-zA-Z0-9]*)) {
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}

.                               { /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
