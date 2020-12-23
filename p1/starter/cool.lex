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

    // Things to keep count of
    static int integer_count = 0;
    static int type_count = 0;
    static int object_count = 0;
    static int string_count = 0;
    static int comment_count = 0;
    static int curr_lineno = 1;

    // Getters and setters
    int get_curr_lineno() {
        return curr_lineno;
    }

    // String stiching
    static StringBuffer curr_string = new StringBuffer();
    void reset_string() {
        curr_string = new StringBuffer();
    }

    // Finding keywords
    static String[] keywords = {"class", "else", "fi", "if", "in", "inherits", "isvoid", "let", "loop", "pool", "then", "while", "case", "esac", "new", "of", "not"};
    Symbol keyword(String input) {
        for (String k : keywords) {
            if (input.toLowerCase().equals(k)) {
                Field[] tokens = TokenConstants.class.getDeclaredFields();
                for (Field f : tokens) {
                    if (f.getName().equals(input.toUpperCase())) {
                        try {
                            return new Symbol((Integer) f.get(TokenConstants.class));
                        } catch (Exception e) {
                            System.out.println("error");
                        }
                    }
                }
            }
        }
        return null;
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

    // We want to report errors on EOF so we report the error, switch
    // to an EOF state, and use that to finally return EOF and end lexing
    switch(yy_lexical_state) {
        case IN_MULTI_COMMENT:
            yybegin(EOF);
            return new Symbol(TokenConstants.ERROR, "EOF in comment");
        case STRING_UNESCAPED:
        case IN_STRING:
            yybegin(EOF);
            return new Symbol(TokenConstants.ERROR, "EOF in string constant");
        default:
            return new Symbol(TokenConstants.EOF);
    }
%eofval}

%class CoolLexer
%cup

%state IN_STRING STRING_UNESCAPED STRING_NULL IN_MULTI_COMMENT IN_SINGLE_COMMENT EOF

%%

<YYINITIAL, IN_MULTI_COMMENT>"(*" {
    // Start of multi-line comment. 
    // They can be nested so we have to record whether we're "balanced" or not
    yybegin(IN_MULTI_COMMENT);
    comment_count++;
}
<YYINITIAL, IN_MULTI_COMMENT>"*)" {
    // Ensures that commment_count >= 0 and we catch unbalanced comments
    if (yy_lexical_state == YYINITIAL) {
        return new Symbol(TokenConstants.ERROR, "Unmatched *)");
    }
    // Ending a multi-line comment
    comment_count--;
    if (comment_count == 0) {
        yybegin(YYINITIAL);
    }
}
<YYINITIAL>"--" {
    // Single line comments
    yybegin(IN_SINGLE_COMMENT);
}

<IN_STRING>"\"" { 
    // End of a string
    yybegin(YYINITIAL);
    if (curr_string.length() >= MAX_STR_CONST) {
        return new Symbol(TokenConstants.ERROR, "String constant too long");
    }
    string_count++;
    return new Symbol(TokenConstants.STR_CONST, new StringSymbol(curr_string.toString(), curr_string.length(), string_count));
}
<STRING_NULL>"\"" {
    // We can resume lexing:
    /*
        If the string contains invalid characters (i.e., the null character), 
        report thisas‘‘String contains null character’’. 
        In either case, lexing should resume after the end of thestring.
    */
    yybegin(YYINITIAL); 
}
<YYINITIAL>"\"" { 
    // Start of a string
    reset_string();
    yybegin(IN_STRING);
}

<YYINITIAL, IN_SINGLE_COMMENT, STRING_NULL>\n {
    // YYINITIAL: track line count, keep state
    // IN_SINGLE_COMMENT: End of single line comment
    // STRING_NULL: 
        // We resume lexing:
        /*
            If a string contains an unescaped newline, 
            report that error as‘‘Unterminated string constant’’and 
            resume lexing at the beginning of the next line
        */
    yybegin(YYINITIAL);
    curr_lineno++;
}
<STRING_UNESCAPED>\n {
    // An ending backslash followed by a newline is OK
    curr_string.append("\n");
    curr_lineno++;
    yybegin(IN_STRING);
}
<IN_STRING>\n {
    // Strings w/o an ending backslash with a newline is not OK
    yybegin(YYINITIAL);
    curr_lineno++;
    return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
}
<IN_MULTI_COMMENT>\n {
    // Keep record of line count while we're still in a comment
    curr_lineno++;
}
<YYINITIAL, IN_MULTI_COMMENT>[\40\f\r\t\013] {
    // Permitted whitespace chars in regular COOL code
    // Note: \013 is used b/c "\v" isn't valid in Java
}
<IN_STRING>\0 {
    // Strings cannot contain null literals
    yybegin(STRING_NULL);
    return new Symbol(TokenConstants.ERROR, "String contains null character.");
}
<STRING_UNESCAPED>\0 {
    // Strings cannot attempt to escape null literals
    yybegin(STRING_NULL);
    return new Symbol(TokenConstants.ERROR, "String contains escaped null character");
}
<IN_STRING>\\ {
    // Backslash in a string means the next input is part of an escape
    yybegin(STRING_UNESCAPED);
}

<YYINITIAL>[a-z][a-zA-Z0-9_]* {
    // Keywords, boolean values, or object identifiers (variables)
    Symbol k = keyword(yytext());
    if (k == null) {
        if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
            k = new Symbol(TokenConstants.BOOL_CONST, yytext());
        } 
        else {
            AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), object_count);
            object_count++;
            k = new Symbol(TokenConstants.OBJECTID, sym);
        }
    }
    return k;
}

<YYINITIAL>(Object|IO|Int|String|Bool|([A-Z][a-zA-Z0-9_]*)) {
    // Keywords or type identifiers
    Symbol k = keyword(yytext());
    if (k == null) {
        k = new Symbol(TokenConstants.TYPEID, new IdSymbol(yytext(), yytext().length(), type_count));
        type_count++;
    }
    return k;
}

<YYINITIAL>[0-9]+ {
    // Integers
    AbstractSymbol sym = new IntSymbol(yytext(), yytext().length(), integer_count);
    integer_count++;
    return new Symbol(TokenConstants.INT_CONST, sym);
}

<YYINITIAL>([:;{}()+\-*/=~<,.@]|"<="|"=>"|"<-") {
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
        case "=>": 
            return new Symbol(TokenConstants.DARROW);
        case "<-": 
            return new Symbol(TokenConstants.ASSIGN);
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
            // Impossible
            return new Symbol(TokenConstants.ERROR, "IMPOSSIBLE");
    }
}

<IN_SINGLE_COMMENT, IN_MULTI_COMMENT, STRING_NULL> . {
    // In comments, anything printable goes, and is skipped
    // TODO: String null?
}
<IN_STRING>[\000-\176] { 
    // In COOL strings, literally any possible char goes??
    curr_string.append(yytext()); 
}
<STRING_UNESCAPED> . {
    // This input completes the escape sequence, which has to be
    // collapsed by the rules
    /*
        Your scanner should convert escape characters in string 
        constants to their correct values.

        However, the sequence of two characters \[a-zA-Z0-9]
        is allowed but should be converted to the one character
    */
    switch (yytext()) {
        case "n":
            curr_string.append("\n");
            break;
        case "b":
            curr_string.append("\b");
            break;
        case "t":
            curr_string.append("\t");
            break;
        case "f":
            curr_string.append("\f");
            break;
        default: 
            curr_string.append(yytext());
            break;
    }
    yybegin(IN_STRING);
}

. { 
    // Any unrecognized tokens
    return new Symbol(TokenConstants.ERROR, yytext());
}
