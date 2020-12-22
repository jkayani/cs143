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

    static int string_count = 0;
    int get_string_count() {
        return string_count;
    }
    void inc_string_count() {
        string_count++;
    }

    private int curr_lineno = 1;
    int get_curr_lineno() {
        return curr_lineno;
    }
    void inc_curr_lineno() {
        curr_lineno++;
    }

    private int in_string = 1;
    boolean curr_in_string() {
        return in_string % 2 == 0;
    }
    private StringBuffer curr_string = new StringBuffer();
    void reset_string() {
        curr_string = new StringBuffer();
    }

    boolean curr_in_comment() {
        return yy_lexical_state == IN_SINGLE_COMMENT || yy_lexical_state == IN_MULTI_COMMENT;
    }

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
    case IN_MULTI_COMMENT:
        return new Symbol(TokenConstants.EOF, "EOF in comment");
    case IN_STRING:
        return new Symbol(TokenConstants.EOF, "EOF in string constant");
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup

%state IN_STRING IN_MULTI_COMMENT IN_SINGLE_COMMENT

%%

<YYINITIAL>"(*" {
    yybegin(IN_MULTI_COMMENT);
}
<IN_MULTI_COMMENT>"*)" {
    yybegin(YYINITIAL);
}
<YYINITIAL>"*)" {
    return new Symbol(TokenConstants.ERROR, "Unmatched *)");
}
"--" {
    if (!curr_in_comment()) {
        yybegin(IN_SINGLE_COMMENT);
    }
}

<IN_SINGLE_COMMENT, IN_MULTI_COMMENT>"\"" {}
"\"" { 
    in_string++;
    if (curr_in_string()) {
        reset_string();
        yybegin(IN_STRING);
    } else {
        yybegin(YYINITIAL);
        if (curr_string.length() > MAX_STR_CONST) {
            reset_string();
            return new Symbol(TokenConstants.ERROR, "String constant too long");
        }
        string_count++;
        return new Symbol(TokenConstants.STR_CONST, new StringSymbol(curr_string.toString(), curr_string.length(), string_count));
    }
}

<IN_SINGLE_COMMENT>\n {
    yybegin(YYINITIAL);
    inc_curr_lineno();
}
<IN_SINGLE_COMMENT>[\40\f\r\t\v] {}
<IN_STRING>[\40\n\f\r\t\v] {
    if (yytext().equals("\n")) {
        reset_string();
        inc_curr_lineno();
        return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
    }
    curr_string.append(yytext());
}
<YYINITIAL, IN_MULTI_COMMENT>[\40\n\f\r\t\v] {
    if (yytext().indexOf("\n") > -1) {
        inc_curr_lineno();
    }
}
<IN_STRING>\0 {
    reset_string();
    return new Symbol(TokenConstants.ERROR, "String contains null character");
}

<IN_SINGLE_COMMENT, IN_MULTI_COMMENT>[a-z][a-zA-Z0-9_]* {}
<IN_STRING>[a-z][a-zA-Z0-9_]* {
    curr_string.append(yytext());
}
[a-z][a-zA-Z0-9_]* {
    // Keywords
    Field[] tokens = TokenConstants.class.getDeclaredFields();
    for (Field f : tokens) {
        if (f.getName().equals(yytext().toUpperCase())) {
            try {
                return new Symbol((Integer) f.get(TokenConstants.class));
            } catch (Exception e) {
                System.out.println("error");
            }
        }
    }
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, yytext());
    } 
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}

<IN_SINGLE_COMMENT, IN_MULTI_COMMENT>(Object|IO|Int|String|Bool|([A-Z][a-zA-Z0-9_]*)) {}
<IN_STRING>(Object|IO|Int|String|Bool|([A-Z][a-zA-Z0-9_]*)) {
    curr_string.append(yytext());
}
(Object|IO|Int|String|Bool|([A-Z][a-zA-Z0-9_]*)) {
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}

<IN_SINGLE_COMMENT, IN_MULTI_COMMENT>[0-9]+ {}
<IN_STRING>[0-9]+ {
    curr_string.append(yytext());
}
[0-9]+ {
    // Integers
    AbstractSymbol sym = new IntSymbol(yytext(), yytext().length(), get_integer_count());
    inc_integer_count();
    return new Symbol(TokenConstants.INT_CONST, sym);
}

<IN_SINGLE_COMMENT, IN_MULTI_COMMENT>"<-" {}
<IN_STRING>"<-" {
    curr_string.append(yytext());
}
"<-" {
    return new Symbol(TokenConstants.ASSIGN);
}

<IN_SINGLE_COMMENT, IN_MULTI_COMMENT>[:;{}()+\-*/=~<,.@\\] {}
<IN_STRING>[:;{}()+\-*/=~<,.@\\] {
    curr_string.append(yytext());
}
([:;{}()+\-*/=~<,.@\\]|"<=") {
    // Special symbols
    switch (yytext()) {
        case ":":
            return new Symbol(TokenConstants.COLON);
        case ";":
            return new Symbol(TokenConstants.SEMI);
        case "{": 
            return new Symbol(TokenConstants.LBRACE);
        case "}":
            return new Symbol(TokenConstants.RBRACE);
        case "(": 
            return new Symbol(TokenConstants.LPAREN);
        case ")":
            return new Symbol(TokenConstants.RPAREN);
        case "<": 
            return new Symbol(TokenConstants.LT);
        case "<=": 
            return new Symbol(TokenConstants.LE);
        case "+": 
            return new Symbol(TokenConstants.PLUS);
        case "-": 
            return new Symbol(TokenConstants.MINUS);
        case "*":
            return new Symbol(TokenConstants.MULT);
        case "/":
            return new Symbol(TokenConstants.DIV);
        case "=":
            return new Symbol(TokenConstants.EQ);
        case "~":
            return new Symbol(TokenConstants.NEG);
        case ",":
            return new Symbol(TokenConstants.COMMA);
        case "@":
            return new Symbol(TokenConstants.AT);
        case ".":
            return new Symbol(TokenConstants.DOT);
        default: 
            // TODO: what happens on backslash?
            return new Symbol(TokenConstants.ERROR, "Don't know what this is: " + yytext());
    }
}


<IN_SINGLE_COMMENT, IN_MULTI_COMMENT> . {}

. { 
    System.err.printf("LEXER BUG - UNMATCHED: %s (line: %d), (lexical state: %d)\n", yytext(), curr_lineno, yy_lexical_state);
}
