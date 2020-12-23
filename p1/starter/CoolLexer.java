/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;
import java.lang.reflect.*;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int IN_SINGLE_COMMENT = 5;
	private final int IN_STRING = 1;
	private final int YYINITIAL = 0;
	private final int IN_MULTI_COMMENT = 4;
	private final int STRING_UNESCAPED = 2;
	private final int EOF = 6;
	private final int STRING_NULL = 3;
	private final int yy_state_dtrans[] = {
		0,
		24,
		27,
		30,
		32,
		34,
		36
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NOT_ACCEPT,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NOT_ACCEPT,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NOT_ACCEPT,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NOT_ACCEPT,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NOT_ACCEPT,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NOT_ACCEPT
	};
	private int yy_cmap[] = unpackFromString(1,130,
"8,20:8,11,6,11:2,7,20:18,11,20,5,20:5,1,3,2,16:2,4,16:2,15:10,16:2,17,18,19" +
",20,16,14:26,20,9,20:2,13,20,12:26,16,20,16:2,10,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,37,
"0,1,2,1:4,3,4,5,1:14,6,7,2,8,1,7,9,10,11,12,13,14,15")[0];

	private int yy_nxt[][] = unpackFromString(16,21,
"1,2,25,28,31,3,4,5,6:3,5,7,6,8,9,28,33,35,6:2,-1:23,10,-1:30,7:4,-1:17,8:4," +
"-1:20,9,-1:5,1,13:4,14,15,13,16,17,6,13:10,-1:3,11,-1:17,1,18:5,19,-1,20,18" +
":12,1,21:4,22,4,-1,21:13,-1:4,12,-1:16,1,26,29,21:3,23,5,21:3,5,21:9,-1:4,2" +
"8,-1:13,28,-1:2,1,21:5,4,-1,21:13,-1:19,28,-1,1,6:5,-1:2,6:13");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{
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
					case -3:
						break;
					case 3:
						{ 
    // Start of a string
    reset_string();
    yybegin(IN_STRING);
}
					case -4:
						break;
					case 4:
						{
    // YYINITIAL: track line count, keep state
    // IN_SINGLE_COMMENT: End of single line comment
    // STRING_NULL: 
        // We resume lexing:
        /*
            If a string contains an unescaped newline, 
            report that error as??????Unterminated string constant??????and 
            resume lexing at the beginning of the next line
        */
    yybegin(YYINITIAL);
    curr_lineno++;
}
					case -5:
						break;
					case 5:
						{
    // Permitted whitespace chars in regular COOL code
    // Note: \013 is used b/c "\v" isn't valid in Java
}
					case -6:
						break;
					case 6:
						{ 
    // Any unrecognized tokens
    return new Symbol(TokenConstants.ERROR, yytext());
}
					case -7:
						break;
					case 7:
						{
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
					case -8:
						break;
					case 8:
						{
    // Keywords or type identifiers
    Symbol k = keyword(yytext());
    if (k == null) {
        k = new Symbol(TokenConstants.TYPEID, new IdSymbol(yytext(), yytext().length(), type_count));
        type_count++;
    }
    return k;
}
					case -9:
						break;
					case 9:
						{
    // Integers
    AbstractSymbol sym = new IntSymbol(yytext(), yytext().length(), integer_count);
    integer_count++;
    return new Symbol(TokenConstants.INT_CONST, sym);
}
					case -10:
						break;
					case 10:
						{
    // Start of multi-line comment. 
    // They can be nested so we have to record whether we're "balanced" or not
    yybegin(IN_MULTI_COMMENT);
    comment_count++;
}
					case -11:
						break;
					case 11:
						{
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
					case -12:
						break;
					case 12:
						{
    // Single line comments
    yybegin(IN_SINGLE_COMMENT);
}
					case -13:
						break;
					case 13:
						{ 
    // In COOL strings, literally any possible char goes??
    curr_string.append(yytext()); 
}
					case -14:
						break;
					case 14:
						{ 
    // End of a string
    yybegin(YYINITIAL);
    if (curr_string.length() >= MAX_STR_CONST) {
        return new Symbol(TokenConstants.ERROR, "String constant too long");
    }
    string_count++;
    return new Symbol(TokenConstants.STR_CONST, new StringSymbol(curr_string.toString(), curr_string.length(), string_count));
}
					case -15:
						break;
					case 15:
						{
    // Strings w/o an ending backslash with a newline is not OK
    yybegin(YYINITIAL);
    curr_lineno++;
    return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
}
					case -16:
						break;
					case 16:
						{
    // Strings cannot contain null literals
    yybegin(STRING_NULL);
    return new Symbol(TokenConstants.ERROR, "String contains null character.");
}
					case -17:
						break;
					case 17:
						{
    // Backslash in a string means the next input is part of an escape
    yybegin(STRING_UNESCAPED);
}
					case -18:
						break;
					case 18:
						{
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
					case -19:
						break;
					case 19:
						{
    // An ending backslash followed by a newline is OK
    curr_string.append("\n");
    curr_lineno++;
    yybegin(IN_STRING);
}
					case -20:
						break;
					case 20:
						{
    // Strings cannot attempt to escape null literals
    yybegin(STRING_NULL);
    return new Symbol(TokenConstants.ERROR, "String contains escaped null character");
}
					case -21:
						break;
					case 21:
						{
    // In comments, anything printable goes, and is skipped
    // TODO: String null?
}
					case -22:
						break;
					case 22:
						{
    // We can resume lexing:
    /*
        If the string contains invalid characters (i.e., the null character), 
        report thisas??????String contains null character??????. 
        In either case, lexing should resume after the end of thestring.
    */
    yybegin(YYINITIAL); 
}
					case -23:
						break;
					case 23:
						{
    // Keep record of line count while we're still in a comment
    curr_lineno++;
}
					case -24:
						break;
					case 25:
						{
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
					case -25:
						break;
					case 26:
						{
    // In comments, anything printable goes, and is skipped
    // TODO: String null?
}
					case -26:
						break;
					case 28:
						{
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
					case -27:
						break;
					case 29:
						{
    // In comments, anything printable goes, and is skipped
    // TODO: String null?
}
					case -28:
						break;
					case 31:
						{
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
					case -29:
						break;
					case 33:
						{
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
					case -30:
						break;
					case 35:
						{
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
					case -31:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
