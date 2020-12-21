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
	private final int IN_SINGLE_COMMENT = 3;
	private final int IN_STRING = 1;
	private final int YYINITIAL = 0;
	private final int IN_MULTI_COMMENT = 2;
	private final int yy_state_dtrans[] = {
		0,
		31,
		45,
		57
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
		/* 31 */ YY_NOT_ACCEPT,
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
		/* 45 */ YY_NOT_ACCEPT,
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
		/* 57 */ YY_NOT_ACCEPT,
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
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR,
		/* 181 */ YY_NO_ANCHOR,
		/* 182 */ YY_NO_ANCHOR,
		/* 183 */ YY_NO_ANCHOR,
		/* 184 */ YY_NO_ANCHOR,
		/* 185 */ YY_NO_ANCHOR,
		/* 186 */ YY_NO_ANCHOR,
		/* 187 */ YY_NO_ANCHOR,
		/* 188 */ YY_NO_ANCHOR,
		/* 189 */ YY_NO_ANCHOR,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NO_ANCHOR,
		/* 192 */ YY_NO_ANCHOR,
		/* 193 */ YY_NO_ANCHOR,
		/* 194 */ YY_NO_ANCHOR,
		/* 195 */ YY_NO_ANCHOR,
		/* 196 */ YY_NO_ANCHOR,
		/* 197 */ YY_NO_ANCHOR,
		/* 198 */ YY_NO_ANCHOR,
		/* 199 */ YY_NO_ANCHOR,
		/* 200 */ YY_NO_ANCHOR,
		/* 201 */ YY_NO_ANCHOR,
		/* 202 */ YY_NO_ANCHOR,
		/* 203 */ YY_NO_ANCHOR,
		/* 204 */ YY_NO_ANCHOR,
		/* 205 */ YY_NO_ANCHOR,
		/* 206 */ YY_NO_ANCHOR,
		/* 207 */ YY_NO_ANCHOR,
		/* 208 */ YY_NO_ANCHOR,
		/* 209 */ YY_NO_ANCHOR,
		/* 210 */ YY_NO_ANCHOR,
		/* 211 */ YY_NO_ANCHOR,
		/* 212 */ YY_NO_ANCHOR,
		/* 213 */ YY_NO_ANCHOR,
		/* 214 */ YY_NO_ANCHOR,
		/* 215 */ YY_NO_ANCHOR,
		/* 216 */ YY_NO_ANCHOR,
		/* 217 */ YY_NO_ANCHOR,
		/* 218 */ YY_NO_ANCHOR,
		/* 219 */ YY_NO_ANCHOR,
		/* 220 */ YY_NO_ANCHOR,
		/* 221 */ YY_NO_ANCHOR,
		/* 222 */ YY_NO_ANCHOR,
		/* 223 */ YY_NO_ANCHOR,
		/* 224 */ YY_NO_ANCHOR,
		/* 225 */ YY_NO_ANCHOR,
		/* 226 */ YY_NO_ANCHOR,
		/* 227 */ YY_NO_ANCHOR,
		/* 228 */ YY_NO_ANCHOR,
		/* 229 */ YY_NO_ANCHOR,
		/* 230 */ YY_NO_ANCHOR,
		/* 231 */ YY_NO_ANCHOR,
		/* 232 */ YY_NO_ANCHOR,
		/* 233 */ YY_NO_ANCHOR,
		/* 234 */ YY_NO_ANCHOR,
		/* 235 */ YY_NO_ANCHOR,
		/* 236 */ YY_NO_ANCHOR,
		/* 237 */ YY_NO_ANCHOR,
		/* 238 */ YY_NO_ANCHOR,
		/* 239 */ YY_NO_ANCHOR,
		/* 240 */ YY_NO_ANCHOR,
		/* 241 */ YY_NO_ANCHOR,
		/* 242 */ YY_NO_ANCHOR,
		/* 243 */ YY_NO_ANCHOR,
		/* 244 */ YY_NO_ANCHOR,
		/* 245 */ YY_NO_ANCHOR,
		/* 246 */ YY_NO_ANCHOR,
		/* 247 */ YY_NO_ANCHOR,
		/* 248 */ YY_NO_ANCHOR,
		/* 249 */ YY_NO_ANCHOR,
		/* 250 */ YY_NO_ANCHOR,
		/* 251 */ YY_NO_ANCHOR,
		/* 252 */ YY_NO_ANCHOR,
		/* 253 */ YY_NO_ANCHOR,
		/* 254 */ YY_NO_ANCHOR,
		/* 255 */ YY_NO_ANCHOR,
		/* 256 */ YY_NO_ANCHOR,
		/* 257 */ YY_NO_ANCHOR,
		/* 258 */ YY_NO_ANCHOR,
		/* 259 */ YY_NO_ANCHOR,
		/* 260 */ YY_NO_ANCHOR,
		/* 261 */ YY_NO_ANCHOR,
		/* 262 */ YY_NO_ANCHOR,
		/* 263 */ YY_NO_ANCHOR,
		/* 264 */ YY_NO_ANCHOR,
		/* 265 */ YY_NO_ANCHOR,
		/* 266 */ YY_NO_ANCHOR,
		/* 267 */ YY_NO_ANCHOR,
		/* 268 */ YY_NO_ANCHOR,
		/* 269 */ YY_NO_ANCHOR,
		/* 270 */ YY_NO_ANCHOR,
		/* 271 */ YY_NO_ANCHOR,
		/* 272 */ YY_NO_ANCHOR,
		/* 273 */ YY_NO_ANCHOR,
		/* 274 */ YY_NO_ANCHOR,
		/* 275 */ YY_NO_ANCHOR,
		/* 276 */ YY_NO_ANCHOR,
		/* 277 */ YY_NO_ANCHOR,
		/* 278 */ YY_NO_ANCHOR,
		/* 279 */ YY_NO_ANCHOR,
		/* 280 */ YY_NO_ANCHOR,
		/* 281 */ YY_NO_ANCHOR,
		/* 282 */ YY_NO_ANCHOR,
		/* 283 */ YY_NO_ANCHOR,
		/* 284 */ YY_NO_ANCHOR,
		/* 285 */ YY_NO_ANCHOR,
		/* 286 */ YY_NO_ANCHOR,
		/* 287 */ YY_NO_ANCHOR,
		/* 288 */ YY_NO_ANCHOR,
		/* 289 */ YY_NO_ANCHOR,
		/* 290 */ YY_NO_ANCHOR,
		/* 291 */ YY_NO_ANCHOR,
		/* 292 */ YY_NO_ANCHOR,
		/* 293 */ YY_NO_ANCHOR,
		/* 294 */ YY_NO_ANCHOR,
		/* 295 */ YY_NO_ANCHOR,
		/* 296 */ YY_NO_ANCHOR,
		/* 297 */ YY_NO_ANCHOR,
		/* 298 */ YY_NO_ANCHOR,
		/* 299 */ YY_NO_ANCHOR,
		/* 300 */ YY_NO_ANCHOR,
		/* 301 */ YY_NO_ANCHOR,
		/* 302 */ YY_NO_ANCHOR,
		/* 303 */ YY_NO_ANCHOR,
		/* 304 */ YY_NO_ANCHOR,
		/* 305 */ YY_NO_ANCHOR,
		/* 306 */ YY_NO_ANCHOR,
		/* 307 */ YY_NO_ANCHOR,
		/* 308 */ YY_NO_ANCHOR,
		/* 309 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"8,52:8,7,6,52,7:2,52:18,7,52,5,52:5,1,3,2,50:2,4,50:2,48:10,50:2,49,51,52:2" +
",50,27,46,25,38,29,30,47,33,31,47:2,26,47,32,37,39,47,34,28,35,47,36,40,47:" +
"3,52,50,52:2,42,52,11,43,9,22,13,14,45,17,15,44,41,10,41,16,21,23,41,18,12," +
"19,41,20,24,41:3,50,52,50:2,52,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,310,
"0,1,2,1:3,3,4,5,1:3,6,1:4,7,8,9,1:4,10,11,12,1:4,13,14,6,15,16,17,18,19,20," +
"21,22,23,24,25,26,1,25,27,28,19,29,30,31,18,32,33,34,18,35,36,37,38,39,40,4" +
"1,42,43,44,45,25,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,6" +
"5,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,9" +
"0,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,11" +
"1,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,1" +
"30,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148," +
"149,150,151,152,153,17,154,37,155,52,156,157,158,159,160,161,162,163,164,16" +
"5,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,1" +
"84,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202," +
"203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221" +
",222,223,224,225,226,227,228,229,230,231,232,233,142,144,146,234,235,236,6," +
"17,19,37,25,52,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251," +
"252,253,254,255,256,257,258,259,260,261,262,263,264,265,266,267,268,269,270" +
",271,272")[0];

	private int yy_nxt[][] = unpackFromString(273,53,
"1,2,32,46,58,3,4:2,5,6,124,268:2,274,34,48,136,268:2,280,33,59,268,286,292," +
"7,125,269,275,281,35,49,137,269:2,287,269,60,269,293,299,268,5,268:3,304,26" +
"9,8,67,46:2,5,-1:55,9,-1:59,268,298,142,268:37,-1:13,269:17,307,143,269:21," +
"-1:52,8,-1:13,268:40,-1:13,270,300,144,270:37,-1:13,271:17,308,145,271:21,-" +
"1:52,19,-1:13,272,302,146,272:37,-1:13,273:17,309,147,273:21,-1:52,26,-1:4," +
"1,14:3,37,3,15:2,16,17,127,270:2,276,39,52,138,270:2,282,38,62,270,288,294," +
"18,128,271,277,283,40,53,139,271:2,289,271,63,271,295,301,270,5,270:3,305,2" +
"71,19,51,14:2,5,-1:3,10,-1:58,268:6,12,268:33,-1:13,269:22,36,269:17,-1:13," +
"269:40,-1:8,11,-1:57,270:40,-1:13,270:6,50,270:33,-1:13,271:22,61,271:17,-1" +
":7,27,-1:58,272:6,70,272:33,-1:13,273:22,77,273:17,-1:13,272:40,-1:4,1,21,4" +
"1,21,54,22,4:2,23,24,129,272:2,278,42,55,140,272:2,284,47,65,272,290,296,25" +
",130,273,279,285,43,56,141,273:2,291,273,66,273,297,303,272,23,272:3,306,27" +
"3,26,64,21:2,23,-1:9,268:3,160,268,12,268,126,268:32,-1:13,269:7,167,269:11" +
",173,269,36,269,131,269:4,179,269:11,-1:8,20,-1:46,46,-1:10,270:3,162,270,5" +
"0,270,132,270:32,-1:13,271:7,169,271:11,175,271,61,271,133,271:4,181,271:11" +
",-1:13,272:3,164,272,70,272,134,272:32,-1:13,273:7,171,273:11,177,273,77,27" +
"3,135,273:4,183,273:11,-1:4,1,21:3,54,22,29,30,23,24,129,272:2,278,42,55,14" +
"0,272:2,284,44,65,272,290,296,25,130,273,279,285,43,56,141,273:2,291,273,66" +
",273,297,303,272,23,272:3,306,273,26,64,21:2,23,-1:9,268:5,12,268:34,-1:13," +
"269:21,36,269:12,191,269:5,-1:13,271:40,-1:13,270:5,50,270:34,-1:13,271:21," +
"61,271:12,193,271:5,-1:8,28,-1:46,46,-1:10,272:5,70,272:34,-1:13,273:21,77," +
"273:12,195,273:5,-1:8,13,-1:46,46,-1:10,268:10,12,268:29,-1:13,269:26,36,26" +
"9:13,-1:13,270:10,50,270:29,-1:13,271:26,61,271:13,-1:13,272:10,70,272:29,-" +
"1:13,273:26,77,273:13,-1:13,268:15,12,268:24,-1:13,269:31,36,269:8,-1:13,27" +
"3:40,-1:13,270:15,50,270:24,-1:13,271:31,61,271:8,-1:13,272:15,70,272:24,-1" +
":13,273:31,77,273:8,-1:13,268:4,12,268:35,-1:13,269:20,36,269:19,-1:13,270:" +
"4,50,270:35,-1:13,271:20,61,271:19,-1:13,272:4,70,272:35,-1:13,273:20,77,27" +
"3:19,-1:13,268:14,12,268:25,-1:13,269:30,36,269:9,-1:13,270:14,50,270:25,-1" +
":13,271:30,61,271:9,-1:13,272:14,70,272:25,-1:13,273:30,77,273:9,-1:13,12,2" +
"68:39,-1:13,269:16,36,269:23,-1:13,50,270:39,-1:13,271:16,61,271:23,-1:13,7" +
"0,272:39,-1:13,273:16,77,273:23,-1:13,268:7,12,268:32,-1:13,269:23,36,269:1" +
"6,-1:13,270:7,50,270:32,-1:13,271:23,61,271:16,-1:13,272:7,70,272:32,-1:13," +
"273:23,77,273:16,-1:13,268,12,268:38,-1:13,269:17,36,269:22,-1:13,270,50,27" +
"0:38,-1:13,271:17,61,271:22,-1:13,272,70,272:38,-1:13,273:17,77,273:22,-1:1" +
"3,268:3,12,268:36,-1:13,269:19,36,269:20,-1:13,270:3,50,270:36,-1:13,271:19" +
",61,271:20,-1:13,272:3,70,272:36,-1:13,273:19,77,273:20,-1:13,268:13,12,268" +
":26,-1:13,269:29,36,269:10,-1:13,270:13,50,270:26,-1:13,271:29,61,271:10,-1" +
":13,272:13,70,272:26,-1:13,273:29,77,273:10,-1:13,268:4,68,268:7,148,268:27" +
",-1:13,269:20,69,269:7,149,269:11,-1:13,268:8,196,268:31,-1:13,270:4,71,270" +
":7,150,270:27,-1:13,271:20,72,271:7,151,271:11,-1:13,272:4,73,272:7,152,272" +
":27,-1:13,273:20,74,273:7,153,273:11,-1:13,269:24,232,269:15,-1:13,270:8,19" +
"8,270:31,-1:13,271:24,233,271:15,-1:13,272:8,200,272:31,-1:13,273:24,234,27" +
"3:15,-1:13,268:4,75,268:7,68,268:27,-1:13,269:20,76,269:7,69,269:11,-1:13,2" +
"70:4,78,270:7,71,270:27,-1:13,271:20,79,271:7,72,271:11,-1:13,272:4,80,272:" +
"7,73,272:27,-1:13,273:20,81,273:7,74,273:11,-1:13,268:3,82,268:36,-1:13,269" +
":19,83,269:20,-1:13,270:3,84,270:36,-1:13,271:19,85,271:20,-1:13,272:3,86,2" +
"72:36,-1:13,273:19,87,273:20,-1:13,268:12,88,268:27,-1:13,269:28,89,269:11," +
"-1:13,270:12,90,270:27,-1:13,271:28,91,271:11,-1:13,272:12,92,272:27,-1:13," +
"273:28,93,273:11,-1:13,268:2,94,268:37,-1:13,269:9,221,269:30,-1:13,270:2,9" +
"6,270:37,-1:13,271:9,223,271:30,-1:13,272:2,98,272:37,-1:13,273:9,225,273:3" +
"0,-1:13,268:11,190,268:28,-1:13,269:18,95,269:21,-1:13,270:11,192,270:28,-1" +
":13,271:18,97,271:21,-1:13,272:11,194,272:28,-1:13,273:18,99,273:21,-1:13,2" +
"68:4,100,268:35,-1:13,269:10,179,269:29,-1:13,270:4,102,270:35,-1:13,271:10" +
",181,271:29,-1:13,272:4,104,272:35,-1:13,273:10,183,273:29,-1:13,268:12,106" +
",268:27,-1:13,269:27,227,269:12,-1:13,270:12,108,270:27,-1:13,271:27,229,27" +
"1:12,-1:13,272:12,110,272:27,-1:13,273:27,231,273:12,-1:13,268:6,202,268:33" +
",-1:13,270:6,204,270:33,-1:13,272:6,206,272:33,-1:13,268:3,112,268:36,-1:13" +
",269:20,101,269:19,-1:13,270:3,114,270:36,-1:13,271:20,103,271:19,-1:13,272" +
":3,116,272:36,-1:13,273:20,105,273:19,-1:13,268:12,208,268:27,-1:13,269:35," +
"235,269:4,-1:13,270:12,210,270:27,-1:13,271:35,236,271:4,-1:13,272:12,212,2" +
"72:27,-1:13,273:35,237,273:4,-1:13,268:4,214,268:35,-1:13,269:28,107,269:11" +
",-1:13,270:4,216,270:35,-1:13,271:28,109,271:11,-1:13,272:4,218,272:35,-1:1" +
"3,273:28,111,273:11,-1:13,268,82,268:38,-1:13,269:22,238,269:17,-1:13,270,8" +
"4,270:38,-1:13,271:22,239,271:17,-1:13,272,86,272:38,-1:13,273:22,240,273:1" +
"7,-1:13,268:6,118,268:33,-1:13,269:12,241,269:27,-1:13,270:6,120,270:33,-1:" +
"13,271:12,242,271:27,-1:13,272:6,122,272:33,-1:13,273:12,243,273:27,-1:13,2" +
"68:9,220,268:30,-1:13,269:19,113,269:20,-1:13,270:9,222,270:30,-1:13,271:19" +
",115,271:20,-1:13,272:9,224,272:30,-1:13,273:19,117,273:20,-1:13,268:6,226," +
"268:33,-1:13,269:6,244,269:33,-1:13,270:6,228,270:33,-1:13,271:6,245,271:33" +
",-1:13,272:6,230,272:33,-1:13,273:6,246,273:33,-1:13,268:10,112,268:29,-1:1" +
"3,269:28,247,269:11,-1:13,270:10,114,270:29,-1:13,271:28,248,271:11,-1:13,2" +
"72:10,116,272:29,-1:13,273:28,249,273:11,-1:13,269:20,250,269:19,-1:13,271:" +
"20,251,271:19,-1:13,273:20,252,273:19,-1:13,269:4,253,269:35,-1:13,271:4,25" +
"4,271:35,-1:13,273:4,255,273:35,-1:13,269:17,83,269:22,-1:13,271:17,85,271:" +
"22,-1:13,273:17,87,273:22,-1:13,269,179,269:38,-1:13,271,181,271:38,-1:13,2" +
"73,183,273:38,-1:13,269:7,256,269:32,-1:13,271:7,257,271:32,-1:13,273:7,258" +
",273:32,-1:13,269:22,119,269:17,-1:13,271:22,121,271:17,-1:13,273:22,123,27" +
"3:17,-1:13,269:25,259,269:14,-1:13,271:25,260,271:14,-1:13,273:25,261,273:1" +
"4,-1:13,262,269:39,-1:13,263,271:39,-1:13,264,273:39,-1:13,269:36,179,269:3" +
",-1:13,271:36,181,271:3,-1:13,273:36,183,273:3,-1:13,269:22,265,269:17,-1:1" +
"3,271:22,266,271:17,-1:13,273:22,267,273:17,-1:13,269:26,113,269:13,-1:13,2" +
"71:26,115,271:13,-1:13,273:26,117,273:13,-1:13,268,142,268,154,268:36,-1:13" +
",269:10,155,269:29,-1:13,270,144,270,156,270:36,-1:13,271:10,157,271:29,-1:" +
"13,272,146,272,158,272:36,-1:13,273:10,159,273:29,-1:13,268:8,166,268:31,-1" +
":13,269:17,143,269,161,269:20,-1:13,270:8,168,270:31,-1:13,271:17,145,271,1" +
"63,271:20,-1:13,272:8,170,272:31,-1:13,273:17,147,273,165,273:20,-1:13,268:" +
"12,172,268:27,-1:13,269:24,185,269:15,-1:13,270:12,174,270:27,-1:13,271:24," +
"187,271:15,-1:13,272:12,176,272:27,-1:13,273:24,189,273:15,-1:13,268:8,178," +
"268:31,-1:13,269:28,197,269:11,-1:13,270:8,180,270:31,-1:13,271:28,199,271:" +
"11,-1:13,272:8,182,272:31,-1:13,273:28,201,273:11,-1:13,268:2,184,268:37,-1" +
":13,269:24,203,269:15,-1:13,270:2,186,270:37,-1:13,271:24,205,271:15,-1:13," +
"272:2,188,272:37,-1:13,273:24,207,273:15,-1:13,269:12,209,269:27,-1:13,271:" +
"12,211,271:27,-1:13,273:12,213,273:27,-1:13,269:18,215,269:21,-1:13,271:18," +
"217,271:21,-1:13,273:18,219,273:21,-1:4");

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
    case IN_MULTI_COMMENT:
        return new Symbol(TokenConstants.ERROR, "EOF in comment");
    case IN_STRING:
        return new Symbol(TokenConstants.ERROR, "EOF in string constant");
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
					case -3:
						break;
					case 3:
						{ 
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
					case -4:
						break;
					case 4:
						{
    if (yytext().indexOf("\n") > -1) {
        inc_curr_lineno();
    }
}
					case -5:
						break;
					case 5:
						{ 
    System.err.printf("LEXER BUG - UNMATCHED: %s (line: %d), (lexical state: %d)\n", yytext(), curr_lineno, yy_lexical_state);
}
					case -6:
						break;
					case 6:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -7:
						break;
					case 7:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -8:
						break;
					case 8:
						{
    // Integers
    AbstractSymbol sym = new IntSymbol(yytext(), yytext().length(), get_integer_count());
    inc_integer_count();
    return new Symbol(TokenConstants.INT_CONST, sym);
}
					case -9:
						break;
					case 9:
						{
    yybegin(IN_MULTI_COMMENT);
}
					case -10:
						break;
					case 10:
						{
    return new Symbol(TokenConstants.ERROR, "Unmatched *)");
}
					case -11:
						break;
					case 11:
						{
    if (!curr_in_comment()) {
        yybegin(IN_SINGLE_COMMENT);
    }
}
					case -12:
						break;
					case 12:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -13:
						break;
					case 13:
						{
    return new Symbol(TokenConstants.ASSIGN);
}
					case -14:
						break;
					case 14:
						{
    curr_string.append(yytext());
}
					case -15:
						break;
					case 15:
						{
    if (yytext().equals("\n")) {
        reset_string();
        inc_curr_lineno();
        return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
    }
    curr_string.append(yytext());
}
					case -16:
						break;
					case 16:
						{
    reset_string();
    return new Symbol(TokenConstants.ERROR, "String contains null character");
}
					case -17:
						break;
					case 17:
						{
    curr_string.append(yytext());
}
					case -18:
						break;
					case 18:
						{
    curr_string.append(yytext());
}
					case -19:
						break;
					case 19:
						{
    curr_string.append(yytext());
}
					case -20:
						break;
					case 20:
						{
    curr_string.append(yytext());
}
					case -21:
						break;
					case 21:
						{}
					case -22:
						break;
					case 22:
						{}
					case -23:
						break;
					case 23:
						{}
					case -24:
						break;
					case 24:
						{}
					case -25:
						break;
					case 25:
						{}
					case -26:
						break;
					case 26:
						{}
					case -27:
						break;
					case 27:
						{
    yybegin(YYINITIAL);
}
					case -28:
						break;
					case 28:
						{}
					case -29:
						break;
					case 29:
						{
    yybegin(YYINITIAL);
    inc_curr_lineno();
}
					case -30:
						break;
					case 30:
						{}
					case -31:
						break;
					case 32:
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
					case -32:
						break;
					case 33:
						{
    if (yytext().indexOf("\n") > -1) {
        inc_curr_lineno();
    }
}
					case -33:
						break;
					case 34:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -34:
						break;
					case 35:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -35:
						break;
					case 36:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -36:
						break;
					case 37:
						{
    curr_string.append(yytext());
}
					case -37:
						break;
					case 38:
						{
    if (yytext().equals("\n")) {
        reset_string();
        inc_curr_lineno();
        return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
    }
    curr_string.append(yytext());
}
					case -38:
						break;
					case 39:
						{
    curr_string.append(yytext());
}
					case -39:
						break;
					case 40:
						{
    curr_string.append(yytext());
}
					case -40:
						break;
					case 41:
						{}
					case -41:
						break;
					case 42:
						{}
					case -42:
						break;
					case 43:
						{}
					case -43:
						break;
					case 44:
						{}
					case -44:
						break;
					case 46:
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
					case -45:
						break;
					case 47:
						{
    if (yytext().indexOf("\n") > -1) {
        inc_curr_lineno();
    }
}
					case -46:
						break;
					case 48:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -47:
						break;
					case 49:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -48:
						break;
					case 50:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -49:
						break;
					case 51:
						{
    curr_string.append(yytext());
}
					case -50:
						break;
					case 52:
						{
    curr_string.append(yytext());
}
					case -51:
						break;
					case 53:
						{
    curr_string.append(yytext());
}
					case -52:
						break;
					case 54:
						{}
					case -53:
						break;
					case 55:
						{}
					case -54:
						break;
					case 56:
						{}
					case -55:
						break;
					case 58:
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
					case -56:
						break;
					case 59:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -57:
						break;
					case 60:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -58:
						break;
					case 61:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -59:
						break;
					case 62:
						{
    curr_string.append(yytext());
}
					case -60:
						break;
					case 63:
						{
    curr_string.append(yytext());
}
					case -61:
						break;
					case 64:
						{}
					case -62:
						break;
					case 65:
						{}
					case -63:
						break;
					case 66:
						{}
					case -64:
						break;
					case 67:
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
					case -65:
						break;
					case 68:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -66:
						break;
					case 69:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -67:
						break;
					case 70:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -68:
						break;
					case 71:
						{
    curr_string.append(yytext());
}
					case -69:
						break;
					case 72:
						{
    curr_string.append(yytext());
}
					case -70:
						break;
					case 73:
						{}
					case -71:
						break;
					case 74:
						{}
					case -72:
						break;
					case 75:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -73:
						break;
					case 76:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -74:
						break;
					case 77:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -75:
						break;
					case 78:
						{
    curr_string.append(yytext());
}
					case -76:
						break;
					case 79:
						{
    curr_string.append(yytext());
}
					case -77:
						break;
					case 80:
						{}
					case -78:
						break;
					case 81:
						{}
					case -79:
						break;
					case 82:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -80:
						break;
					case 83:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -81:
						break;
					case 84:
						{
    curr_string.append(yytext());
}
					case -82:
						break;
					case 85:
						{
    curr_string.append(yytext());
}
					case -83:
						break;
					case 86:
						{}
					case -84:
						break;
					case 87:
						{}
					case -85:
						break;
					case 88:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -86:
						break;
					case 89:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -87:
						break;
					case 90:
						{
    curr_string.append(yytext());
}
					case -88:
						break;
					case 91:
						{
    curr_string.append(yytext());
}
					case -89:
						break;
					case 92:
						{}
					case -90:
						break;
					case 93:
						{}
					case -91:
						break;
					case 94:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -92:
						break;
					case 95:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -93:
						break;
					case 96:
						{
    curr_string.append(yytext());
}
					case -94:
						break;
					case 97:
						{
    curr_string.append(yytext());
}
					case -95:
						break;
					case 98:
						{}
					case -96:
						break;
					case 99:
						{}
					case -97:
						break;
					case 100:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -98:
						break;
					case 101:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -99:
						break;
					case 102:
						{
    curr_string.append(yytext());
}
					case -100:
						break;
					case 103:
						{
    curr_string.append(yytext());
}
					case -101:
						break;
					case 104:
						{}
					case -102:
						break;
					case 105:
						{}
					case -103:
						break;
					case 106:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -104:
						break;
					case 107:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -105:
						break;
					case 108:
						{
    curr_string.append(yytext());
}
					case -106:
						break;
					case 109:
						{
    curr_string.append(yytext());
}
					case -107:
						break;
					case 110:
						{}
					case -108:
						break;
					case 111:
						{}
					case -109:
						break;
					case 112:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -110:
						break;
					case 113:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -111:
						break;
					case 114:
						{
    curr_string.append(yytext());
}
					case -112:
						break;
					case 115:
						{
    curr_string.append(yytext());
}
					case -113:
						break;
					case 116:
						{}
					case -114:
						break;
					case 117:
						{}
					case -115:
						break;
					case 118:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -116:
						break;
					case 119:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -117:
						break;
					case 120:
						{
    curr_string.append(yytext());
}
					case -118:
						break;
					case 121:
						{
    curr_string.append(yytext());
}
					case -119:
						break;
					case 122:
						{}
					case -120:
						break;
					case 123:
						{}
					case -121:
						break;
					case 124:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -122:
						break;
					case 125:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -123:
						break;
					case 126:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -124:
						break;
					case 127:
						{
    curr_string.append(yytext());
}
					case -125:
						break;
					case 128:
						{
    curr_string.append(yytext());
}
					case -126:
						break;
					case 129:
						{}
					case -127:
						break;
					case 130:
						{}
					case -128:
						break;
					case 131:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -129:
						break;
					case 132:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -130:
						break;
					case 133:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -131:
						break;
					case 134:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -132:
						break;
					case 135:
						{
    if (curr_in_string()) {
        curr_string.append(yytext());
    }
    else if (!curr_in_comment()) {
        // Non-boolean Keywords
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
    }
}
					case -133:
						break;
					case 136:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -134:
						break;
					case 137:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -135:
						break;
					case 138:
						{
    curr_string.append(yytext());
}
					case -136:
						break;
					case 139:
						{
    curr_string.append(yytext());
}
					case -137:
						break;
					case 140:
						{}
					case -138:
						break;
					case 141:
						{}
					case -139:
						break;
					case 142:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -140:
						break;
					case 143:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -141:
						break;
					case 144:
						{
    curr_string.append(yytext());
}
					case -142:
						break;
					case 145:
						{
    curr_string.append(yytext());
}
					case -143:
						break;
					case 146:
						{}
					case -144:
						break;
					case 147:
						{}
					case -145:
						break;
					case 148:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -146:
						break;
					case 149:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -147:
						break;
					case 150:
						{
    curr_string.append(yytext());
}
					case -148:
						break;
					case 151:
						{
    curr_string.append(yytext());
}
					case -149:
						break;
					case 152:
						{}
					case -150:
						break;
					case 153:
						{}
					case -151:
						break;
					case 154:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -152:
						break;
					case 155:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -153:
						break;
					case 156:
						{
    curr_string.append(yytext());
}
					case -154:
						break;
					case 157:
						{
    curr_string.append(yytext());
}
					case -155:
						break;
					case 158:
						{}
					case -156:
						break;
					case 159:
						{}
					case -157:
						break;
					case 160:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -158:
						break;
					case 161:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -159:
						break;
					case 162:
						{
    curr_string.append(yytext());
}
					case -160:
						break;
					case 163:
						{
    curr_string.append(yytext());
}
					case -161:
						break;
					case 164:
						{}
					case -162:
						break;
					case 165:
						{}
					case -163:
						break;
					case 166:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -164:
						break;
					case 167:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -165:
						break;
					case 168:
						{
    curr_string.append(yytext());
}
					case -166:
						break;
					case 169:
						{
    curr_string.append(yytext());
}
					case -167:
						break;
					case 170:
						{}
					case -168:
						break;
					case 171:
						{}
					case -169:
						break;
					case 172:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -170:
						break;
					case 173:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -171:
						break;
					case 174:
						{
    curr_string.append(yytext());
}
					case -172:
						break;
					case 175:
						{
    curr_string.append(yytext());
}
					case -173:
						break;
					case 176:
						{}
					case -174:
						break;
					case 177:
						{}
					case -175:
						break;
					case 178:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -176:
						break;
					case 179:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -177:
						break;
					case 180:
						{
    curr_string.append(yytext());
}
					case -178:
						break;
					case 181:
						{
    curr_string.append(yytext());
}
					case -179:
						break;
					case 182:
						{}
					case -180:
						break;
					case 183:
						{}
					case -181:
						break;
					case 184:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -182:
						break;
					case 185:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -183:
						break;
					case 186:
						{
    curr_string.append(yytext());
}
					case -184:
						break;
					case 187:
						{
    curr_string.append(yytext());
}
					case -185:
						break;
					case 188:
						{}
					case -186:
						break;
					case 189:
						{}
					case -187:
						break;
					case 190:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -188:
						break;
					case 191:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -189:
						break;
					case 192:
						{
    curr_string.append(yytext());
}
					case -190:
						break;
					case 193:
						{
    curr_string.append(yytext());
}
					case -191:
						break;
					case 194:
						{}
					case -192:
						break;
					case 195:
						{}
					case -193:
						break;
					case 196:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -194:
						break;
					case 197:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -195:
						break;
					case 198:
						{
    curr_string.append(yytext());
}
					case -196:
						break;
					case 199:
						{
    curr_string.append(yytext());
}
					case -197:
						break;
					case 200:
						{}
					case -198:
						break;
					case 201:
						{}
					case -199:
						break;
					case 202:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -200:
						break;
					case 203:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -201:
						break;
					case 204:
						{
    curr_string.append(yytext());
}
					case -202:
						break;
					case 205:
						{
    curr_string.append(yytext());
}
					case -203:
						break;
					case 206:
						{}
					case -204:
						break;
					case 207:
						{}
					case -205:
						break;
					case 208:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -206:
						break;
					case 209:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -207:
						break;
					case 210:
						{
    curr_string.append(yytext());
}
					case -208:
						break;
					case 211:
						{
    curr_string.append(yytext());
}
					case -209:
						break;
					case 212:
						{}
					case -210:
						break;
					case 213:
						{}
					case -211:
						break;
					case 214:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -212:
						break;
					case 215:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -213:
						break;
					case 216:
						{
    curr_string.append(yytext());
}
					case -214:
						break;
					case 217:
						{
    curr_string.append(yytext());
}
					case -215:
						break;
					case 218:
						{}
					case -216:
						break;
					case 219:
						{}
					case -217:
						break;
					case 220:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -218:
						break;
					case 221:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -219:
						break;
					case 222:
						{
    curr_string.append(yytext());
}
					case -220:
						break;
					case 223:
						{
    curr_string.append(yytext());
}
					case -221:
						break;
					case 224:
						{}
					case -222:
						break;
					case 225:
						{}
					case -223:
						break;
					case 226:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -224:
						break;
					case 227:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -225:
						break;
					case 228:
						{
    curr_string.append(yytext());
}
					case -226:
						break;
					case 229:
						{
    curr_string.append(yytext());
}
					case -227:
						break;
					case 230:
						{}
					case -228:
						break;
					case 231:
						{}
					case -229:
						break;
					case 232:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -230:
						break;
					case 233:
						{
    curr_string.append(yytext());
}
					case -231:
						break;
					case 234:
						{}
					case -232:
						break;
					case 235:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -233:
						break;
					case 236:
						{
    curr_string.append(yytext());
}
					case -234:
						break;
					case 237:
						{}
					case -235:
						break;
					case 238:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -236:
						break;
					case 239:
						{
    curr_string.append(yytext());
}
					case -237:
						break;
					case 240:
						{}
					case -238:
						break;
					case 241:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -239:
						break;
					case 242:
						{
    curr_string.append(yytext());
}
					case -240:
						break;
					case 243:
						{}
					case -241:
						break;
					case 244:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -242:
						break;
					case 245:
						{
    curr_string.append(yytext());
}
					case -243:
						break;
					case 246:
						{}
					case -244:
						break;
					case 247:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -245:
						break;
					case 248:
						{
    curr_string.append(yytext());
}
					case -246:
						break;
					case 249:
						{}
					case -247:
						break;
					case 250:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -248:
						break;
					case 251:
						{
    curr_string.append(yytext());
}
					case -249:
						break;
					case 252:
						{}
					case -250:
						break;
					case 253:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -251:
						break;
					case 254:
						{
    curr_string.append(yytext());
}
					case -252:
						break;
					case 255:
						{}
					case -253:
						break;
					case 256:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -254:
						break;
					case 257:
						{
    curr_string.append(yytext());
}
					case -255:
						break;
					case 258:
						{}
					case -256:
						break;
					case 259:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -257:
						break;
					case 260:
						{
    curr_string.append(yytext());
}
					case -258:
						break;
					case 261:
						{}
					case -259:
						break;
					case 262:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -260:
						break;
					case 263:
						{
    curr_string.append(yytext());
}
					case -261:
						break;
					case 264:
						{}
					case -262:
						break;
					case 265:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -263:
						break;
					case 266:
						{
    curr_string.append(yytext());
}
					case -264:
						break;
					case 267:
						{}
					case -265:
						break;
					case 268:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -266:
						break;
					case 269:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -267:
						break;
					case 270:
						{
    curr_string.append(yytext());
}
					case -268:
						break;
					case 271:
						{
    curr_string.append(yytext());
}
					case -269:
						break;
					case 272:
						{}
					case -270:
						break;
					case 273:
						{}
					case -271:
						break;
					case 274:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -272:
						break;
					case 275:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -273:
						break;
					case 276:
						{
    curr_string.append(yytext());
}
					case -274:
						break;
					case 277:
						{
    curr_string.append(yytext());
}
					case -275:
						break;
					case 278:
						{}
					case -276:
						break;
					case 279:
						{}
					case -277:
						break;
					case 280:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -278:
						break;
					case 281:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -279:
						break;
					case 282:
						{
    curr_string.append(yytext());
}
					case -280:
						break;
					case 283:
						{
    curr_string.append(yytext());
}
					case -281:
						break;
					case 284:
						{}
					case -282:
						break;
					case 285:
						{}
					case -283:
						break;
					case 286:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -284:
						break;
					case 287:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -285:
						break;
					case 288:
						{
    curr_string.append(yytext());
}
					case -286:
						break;
					case 289:
						{
    curr_string.append(yytext());
}
					case -287:
						break;
					case 290:
						{}
					case -288:
						break;
					case 291:
						{}
					case -289:
						break;
					case 292:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -290:
						break;
					case 293:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -291:
						break;
					case 294:
						{
    curr_string.append(yytext());
}
					case -292:
						break;
					case 295:
						{
    curr_string.append(yytext());
}
					case -293:
						break;
					case 296:
						{}
					case -294:
						break;
					case 297:
						{}
					case -295:
						break;
					case 298:
						{
    if (yytext().toLowerCase().equals("true") || yytext().toLowerCase().equals("false")) {
        // Booleans
        return new Symbol(TokenConstants.BOOL_CONST, new BoolConst(Boolean.valueOf(yytext().toLowerCase())));
    }
    else {
        // Object identifier
        AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_object_count());
        inc_object_count();
        return new Symbol(TokenConstants.OBJECTID, sym);
    }
}
					case -296:
						break;
					case 299:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -297:
						break;
					case 300:
						{
    curr_string.append(yytext());
}
					case -298:
						break;
					case 301:
						{
    curr_string.append(yytext());
}
					case -299:
						break;
					case 302:
						{}
					case -300:
						break;
					case 303:
						{}
					case -301:
						break;
					case 304:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -302:
						break;
					case 305:
						{
    curr_string.append(yytext());
}
					case -303:
						break;
					case 306:
						{}
					case -304:
						break;
					case 307:
						{
    // Type identifier
    AbstractSymbol sym = new IdSymbol(yytext(), yytext().length(), get_type_count());
    inc_type_count();
    return new Symbol(TokenConstants.TYPEID, sym);
}
					case -305:
						break;
					case 308:
						{
    curr_string.append(yytext());
}
					case -306:
						break;
					case 309:
						{}
					case -307:
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
