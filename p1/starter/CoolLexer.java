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
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
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
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"43:9,3:2,43,3:2,43:18,3,43:15,4:10,43:3,1,2,43:2,23,42,21,34,25,26,38,29,27" +
",38:2,22,38,28,33,35,38,30,24,31,38,32,36,38:3,43:6,7,39,5,18,9,10,41,13,11" +
",40,37,6,37,12,17,19,37,14,8,15,37,16,20,37:3,43:5,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,98,
"0,1,2,1,3,4,5,1,6,1,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25" +
",26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50" +
",9,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74," +
"75,76,47,77,6,9,78,79,80,81,82,83,84,85,86,87,88,89")[0];

	private int yy_nxt[][] = unpackFromString(90,44,
"1,2,9,3,4,5,36,84:2,86,11,14,40,84:2,88,10,16,84,90,92,6,37,85,87,89,12,15," +
"41,85:2,91,85,17,85,93,95,84,85,84:3,96,9,-1:46,7,-1:45,4,-1:43,84:2,94,42," +
"84:35,-1:5,85:18,97,43,85:19,-1:5,84:39,-1:5,84:7,8,84:31,-1:5,85:23,13,85:" +
"15,-1:5,85:39,-1:5,84:4,48,84,8,84,38,84:30,-1:5,85:8,51,85:11,53,85,13,85," +
"39,85:4,55,85:9,-1:5,84:6,8,84:32,-1:5,85:22,13,85:12,59,85:3,-1:5,84:11,8," +
"84:27,-1:5,85:27,13,85:11,-1:5,84:16,8,84:22,-1:5,85:32,13,85:6,-1:5,84:5,8" +
",84:33,-1:5,85:21,13,85:17,-1:5,84:15,8,84:23,-1:5,85:31,13,85:7,-1:5,84,8," +
"84:37,-1:5,85:17,13,85:21,-1:5,84:8,8,84:30,-1:5,85:24,13,85:14,-1:5,84:2,8" +
",84:36,-1:5,85:18,13,85:20,-1:5,84:4,8,84:34,-1:5,85:20,13,85:18,-1:5,84:14" +
",8,84:24,-1:5,85:30,13,85:8,-1:5,84:5,18,84:7,44,84:25,-1:5,85:21,19,85:7,4" +
"5,85:9,-1:5,84:9,60,84:29,-1:5,85:25,72,85:13,-1:5,84:5,20,84:7,18,84:25,-1" +
":5,85:21,21,85:7,19,85:9,-1:5,84:4,22,84:34,-1:5,85:20,23,85:18,-1:5,84:13," +
"24,84:25,-1:5,85:29,25,85:9,-1:5,84:3,26,84:35,-1:5,85:10,69,85:28,-1:5,84:" +
"12,58,84:26,-1:5,85:19,27,85:19,-1:5,84:5,28,84:33,-1:5,85:11,55,85:27,-1:5" +
",84:13,30,84:25,-1:5,85:28,71,85:10,-1:5,84:7,62,84:31,-1:5,84:4,32,84:34,-" +
"1:5,85:21,29,85:17,-1:5,84:13,64,84:25,-1:5,85:36,73,85:2,-1:5,84:5,66,84:3" +
"3,-1:5,85:29,31,85:9,-1:5,84:2,22,84:36,-1:5,85:23,74,85:15,-1:5,84:7,34,84" +
":31,-1:5,85:13,75,85:25,-1:5,84:10,68,84:28,-1:5,85:20,33,85:18,-1:5,84:7,7" +
"0,84:31,-1:5,85:7,76,85:31,-1:5,84:11,32,84:27,-1:5,85:29,77,85:9,-1:5,85:2" +
"1,78,85:17,-1:5,85:5,79,85:33,-1:5,85:18,23,85:20,-1:5,85:2,55,85:36,-1:5,8" +
"5:8,80,85:30,-1:5,85:23,35,85:15,-1:5,85:26,81,85:12,-1:5,85,82,85:37,-1:5," +
"85:37,55,85,-1:5,85:23,83,85:15,-1:5,85:27,33,85:11,-1:5,84:2,42,84,46,84:3" +
"4,-1:5,85:11,47,85:27,-1:5,84:9,50,84:29,-1:5,85:18,43,85,49,85:18,-1:5,84:" +
"13,52,84:25,-1:5,85:25,57,85:13,-1:5,84:9,54,84:29,-1:5,85:29,61,85:9,-1:5," +
"84:3,56,84:35,-1:5,85:25,63,85:13,-1:5,85:13,65,85:25,-1:5,85:19,67,85:19,-" +
"1");

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
						{ /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -3:
						break;
					case 3:
						{
    if (yytext().equals("\n")) {
        inc_curr_lineno();
    }
    // TODO: What symbol is supposed to represent whitespace?
    return new Symbol(TokenConstants.MULT);
}
					case -4:
						break;
					case 4:
						{
    // Digits
    AbstractSymbol sym = new IntSymbol(yytext(), yytext().length(), get_integer_count());
    inc_integer_count();
    return new Symbol(TokenConstants.INT_CONST, sym);
}
					case -5:
						break;
					case 5:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -6:
						break;
					case 6:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -7:
						break;
					case 7:
						{ /* Sample lexical rule for "=>" arrow.
                                     Further lexical rules should be defined
                                     here, after the last %% separator */
                                  return new Symbol(TokenConstants.DARROW); }
					case -8:
						break;
					case 8:
						{
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
					case -9:
						break;
					case 9:
						{ /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -10:
						break;
					case 10:
						{
    if (yytext().equals("\n")) {
        inc_curr_lineno();
    }
    // TODO: What symbol is supposed to represent whitespace?
    return new Symbol(TokenConstants.MULT);
}
					case -11:
						break;
					case 11:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -12:
						break;
					case 12:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -13:
						break;
					case 13:
						{
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
					case -14:
						break;
					case 14:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -15:
						break;
					case 15:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -16:
						break;
					case 16:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -17:
						break;
					case 17:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -18:
						break;
					case 18:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -19:
						break;
					case 19:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -20:
						break;
					case 20:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -21:
						break;
					case 21:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -22:
						break;
					case 22:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -23:
						break;
					case 23:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -24:
						break;
					case 24:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -25:
						break;
					case 25:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -26:
						break;
					case 26:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -27:
						break;
					case 27:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -28:
						break;
					case 28:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -29:
						break;
					case 29:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -30:
						break;
					case 30:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -31:
						break;
					case 31:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -32:
						break;
					case 32:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -33:
						break;
					case 33:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -34:
						break;
					case 34:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -35:
						break;
					case 35:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -36:
						break;
					case 36:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -37:
						break;
					case 37:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -38:
						break;
					case 38:
						{
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
					case -39:
						break;
					case 39:
						{
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
					case -40:
						break;
					case 40:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -41:
						break;
					case 41:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -42:
						break;
					case 42:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -43:
						break;
					case 43:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -44:
						break;
					case 44:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -45:
						break;
					case 45:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -46:
						break;
					case 46:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -47:
						break;
					case 47:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -48:
						break;
					case 48:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -49:
						break;
					case 49:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -50:
						break;
					case 50:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -51:
						break;
					case 51:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -52:
						break;
					case 52:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -53:
						break;
					case 53:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -54:
						break;
					case 54:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -55:
						break;
					case 55:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -56:
						break;
					case 56:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -57:
						break;
					case 57:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -58:
						break;
					case 58:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -59:
						break;
					case 59:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -60:
						break;
					case 60:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -61:
						break;
					case 61:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -62:
						break;
					case 62:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -63:
						break;
					case 63:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -64:
						break;
					case 64:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -65:
						break;
					case 65:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -66:
						break;
					case 66:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -67:
						break;
					case 67:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -68:
						break;
					case 68:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -69:
						break;
					case 69:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -70:
						break;
					case 70:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -71:
						break;
					case 71:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -72:
						break;
					case 72:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -73:
						break;
					case 73:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -74:
						break;
					case 74:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -75:
						break;
					case 75:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -76:
						break;
					case 76:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -77:
						break;
					case 77:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -78:
						break;
					case 78:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -79:
						break;
					case 79:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -80:
						break;
					case 80:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -81:
						break;
					case 81:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -82:
						break;
					case 82:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -83:
						break;
					case 83:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -84:
						break;
					case 84:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -85:
						break;
					case 85:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -86:
						break;
					case 86:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -87:
						break;
					case 87:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -88:
						break;
					case 88:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -89:
						break;
					case 89:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -90:
						break;
					case 90:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -91:
						break;
					case 91:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -92:
						break;
					case 92:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -93:
						break;
					case 93:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -94:
						break;
					case 94:
						{
    // Object identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
    inc_object_count();
    return new Symbol(TokenConstants.OBJECTID, sym);
}
					case -95:
						break;
					case 95:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -96:
						break;
					case 96:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -97:
						break;
					case 97:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -98:
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
