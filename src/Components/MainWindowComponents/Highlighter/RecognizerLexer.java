// $ANTLR : "Recognizer.g" -> "RecognizerLexer.java"$

package Components.MainWindowComponents.Highlighter;

import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;

public class RecognizerLexer extends antlr.CharScanner implements RecognizerParserTokenTypes, TokenStream {
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	
	
	public RecognizerLexer(InputStream in) {
		this(new ByteBuffer(in));
	}
	public RecognizerLexer(Reader in) {
		this(new CharBuffer(in));
	}
	public RecognizerLexer(InputBuffer ib) {
		this(new LexerSharedInputState(ib));
	}	
	@SuppressWarnings("unchecked")
	public RecognizerLexer(LexerSharedInputState state) {
		super(state);
		caseSensitiveLiterals = false;
		setCaseSensitive(false);
		literals = new Hashtable<ANTLRHashString, Integer>();
		for (String keyword : keywords) {
			literals.put(new ANTLRHashString(keyword, this), KEYWORD); // key : codigo da classe - Style.	
		}
		
	}

	public Token nextToken() throws TokenStreamException {
		/*
		System.out.println("TS0=" + _tokenSet_0);
		System.out.println("TS1=" + _tokenSet_1);
		System.out.println("TS2=" + _tokenSet_2);
		System.out.println("TS3=" + _tokenSet_3);
		System.out.println("TS4=" + _tokenSet_4);
		System.out.println("TS5=" + _tokenSet_5);
		System.out.println("TS6=" + _tokenSet_6);
		System.out.println("TS7=" + _tokenSet_7);
		*/
		again:
		for (;;) {
			int type = Token.INVALID_TYPE;
			resetText();

			try {   					// for char stream error handling
				try {   				// for lexical error handling
					switch (LA(1)) {
						case '`': {
							OBJECT(true);
							break;
						}
						case '"': {
							STRING_D(true);
							break;
						}
						
						case '\'': {
							STRING_S(true);
							break;
						}
						
						case '@':
						case '?':
						case '\\':
							MISC(true);
							break;
					
						case 'A': case 'a':
						case 'B': case 'b':
						case 'C': case 'c':
						case 'D': case 'd':
						case 'E': case 'e':
						case 'F': case 'f':
						case 'G': case 'g':
						case 'H': case 'h':
						case 'I': case 'i':
						case 'J': case 'j':
						case 'K': case 'k':
						case 'L': case 'l':
						case 'M': case 'm':
						case 'N': case 'n':
						case 'O': case 'o':
						case 'P': case 'p':
						case 'Q': case 'q':
						case 'R': case 'r':
						case 'S': case 's':
						case 'T': case 't':
						case 'U': case 'u':
						case 'V': case 'v':
						case 'W': case 'w':
						case 'X': case 'x':
						case 'Y': case 'y':
						case 'Z': case 'z':
						case '_':
							IDENTIFIER(true);
							break;

					default:
						if (LA(1)=='0' && LA(2)=='x') {
							HEX_DIGIT(true);
						}
						else if (LA(1) == '-' && LA(2) == '-') {
							SL_COMMENT(true);
						}
						else if (LA(1) == '/' && LA(2) == '/') {
							SL_COMMENT(true);
						}
						else if (LA(1) == '/' && LA(2) == '*') {
							ML_COMMENT(true);
						}
						else if (LA(1) >= '0' && LA(1) <= '9') {
							DEC_DIGIT(true);
							
						}
						else if (_tokenSet_0.member(LA(1))) {
							DELIMETER(true);
						}
						else {
							if (LA(1) == EOF_CHAR) {
								uponEOF(); 
								_returnToken = makeToken(Token.EOF_TYPE);
							}
							else {
								throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
							}
						}
						}
						if (_returnToken == null ) {
							continue again; // found SKIP token
						}	
						type = _returnToken.getType();
						_returnToken.setType(type);
						return _returnToken;
				}
				catch (RecognitionException e) {
					throw new TokenStreamRecognitionException(e);
				}
			}
			catch (CharStreamException cse) {
				if ( cse instanceof CharStreamIOException ) {
					throw new TokenStreamIOException(((CharStreamIOException)cse).io);
				}
				else {
					throw new TokenStreamException(cse.getMessage());
				}
			}
		}
	}
	
	// revisado por @will - SQL [OK]
	public final void STRING_S(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin=text.length();
		type = STRING_S;
		match('\'');
		while (true) {
			if (LA(1) == '\\' && LA(2) == '\\') {
				match('\\');
				match('\\');
			}
			if (LA(1) == '\\' && LA(2) == '\'') {
				match('\\');
				match('\'');
			}
			else if ((_tokenSet_3.member(LA(1)))) {
				match(_tokenSet_3);
			} 
			else {
				break;
			}
		}
		if ((LA(1) == '\'')) {
			match('\'');
		}
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	
	// revisado por @will - SQL [OK]?
	public final void IDENTIFIER(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = IDENTIFIER;
		
		boolean loop = true;
		while (loop) {
			switch (LA(1)) {
				case 'A': case 'a':
				case 'B': case 'b':
				case 'C': case 'c':
				case 'D': case 'd':
				case 'E': case 'e':
				case 'F': case 'f':
				case 'G': case 'g':
				case 'H': case 'h':
				case 'I': case 'i':
				case 'J': case 'j':
				case 'K': case 'k':
				case 'L': case 'l':
				case 'M': case 'm':
				case 'N': case 'n':
				case 'O': case 'o':
				case 'P': case 'p':
				case 'Q': case 'q':
				case 'R': case 'r':
				case 'S': case 's':
				case 'T': case 't':
				case 'U': case 'u':
				case 'V': case 'v':
				case 'W': case 'w':
				case 'X': case 'x':
				case 'Y': case 'y':
				case 'Z': case 'z':
					ALPHA(false);
					break;
					
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':  
				case '9':
					DEC_DIGIT(false);
					break;

				//case '-':
				case '_':
				case '\\':
				case '@':
				case '?':
					MISC(false);
					break;

				default:
					loop = false;
					break;
			}
		}
		
		
		/*
		ALPHA(false);
		{
			loop:
				do {
					switch (LA(1)) {
						case 'A': case 'a':
						case 'B': case 'b':
						case 'C': case 'c':
						case 'D': case 'd':
						case 'E': case 'e':
						case 'F': case 'f':
						case 'G': case 'g':
						case 'H': case 'h':
						case 'I': case 'i':
						case 'J': case 'j':
						case 'K': case 'k':
						case 'L': case 'l':
						case 'M': case 'm':
						case 'N': case 'n':
						case 'O': case 'o':
						case 'P': case 'p':
						case 'Q': case 'q':
						case 'R': case 'r':
						case 'S': case 's':
						case 'T': case 't':
						case 'U': case 'u':
						case 'V': case 'v':
						case 'W': case 'w':
						case 'X': case 'x':
						case 'Y': case 'y':
						case 'Z': case 'z':
							System.out.println("!!!!!!!!!!!! 2");
							ALPHA(false);
							break;
							
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':  
						case '9':
							System.out.println("!!!!!!!!!!!! 3");
							DEC_DIGIT(false);
							break;

						case '-':
						case '_':
							System.out.println("!!!!!!!!!!!! 4");
							MISC(false);
							break;

						default:
							break loop;
					}
				} while (true);
		}
		*/
		if (inputState.guessing == 0) {
			type = Token.SKIP;
		}
		type = testLiteralsTable(type);
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	
	// revisado por @will - SQL [OK]
	protected final void ALPHA(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = ALPHA;
		switch (LA(1)) {
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n': 
			case 'o': 
			case 'p':
			case 'q': 
			case 'r': 
			case 's': 
			case 't':
			case 'u': 
			case 'v': 
			case 'w': 
			case 'x':
			case 'y':
			case 'z':
				matchRange('a','z');
				break;
				
			case 'A': 
			case 'B': 
			case 'C': 
			case 'D':
			case 'E': 
			case 'F':  
			case 'G': 
			case 'H':
			case 'I':
			case 'J':
			case 'K': 
			case 'L':
			case 'M': 
			case 'N': 
			case 'O': 
			case 'P':
			case 'Q':  
			case 'R':  
			case 'S': 
			case 'T':
			case 'U': 
			case 'V': 
			case 'W': 
			case 'X':
			case 'Y': 
			case 'Z':
				matchRange('A','Z');
				break;
		
			default:
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	
	// revisado por @will - SQL [OK]
	protected final void MISC(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = MISC;
		char buff = LA(1);
		switch (buff) {
			//case '-':
			case '_':
			case '\\':
			case '@':
			case '?':
				match(buff);
				break;
				
			default:
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		
		}
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	
	// revisado por @will - SQL [OK]
	public final void STRING_D(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin=text.length();
		type = STRING_D;
			
		match('"');
		boolean matched = false;
		while (true) {
			matched = false;
			if (LA(1)=='\\' && _tokenSet_5.member(LA(2))) {
				int mark = mark();
				matched = true;
				inputState.guessing++;
				try {
					match('\\');
					match(_tokenSet_6);
				}
				catch (RecognitionException pe) {
					matched = false;
				}
				rewind(mark);
				inputState.guessing--;
			}
			if (matched) {
				mEscape(false);
			}
			else if (LA(1) == '\n' || LA(1) == '\r' || LA(1) == '\\') {
				switch ( LA(1)) {
					case '\r':
						match('\r');
						if (inputState.guessing == 0) {
							newline();
						}
						break;
					
					case '\n':
						match('\n');
						if (inputState.guessing == 0 ) {
							newline();
						}
						break;
					
					case '\\':
						match('\\');
						match('\n');
						if (inputState.guessing == 0) {
							newline();
						}
						break;
					
					default:
						throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
			}
			else if (_tokenSet_7.member(LA(1))) {
				match(_tokenSet_7);
			}
			else {
				break;
			}
		}
		if (LA(1) == '"') {
			match('"');
		}
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	
	// revisado por @will - SQL [OK]
	public final void DELIMETER(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = DELIMITER;
		char buff = LA(1);
		switch (buff) {
			case '\t':
			case '\n':
			case '\r':
			case '\u000c':
			case ' ':
			case '_':
				WHITESPACE(false);
				break;
				
			case '.':
			case ':':
			case ';':
			case ',':
			case '[':
			case ']':
			case '(':
			case ')':
			case '{':
			case '}':
			case '+':
			case '-':
			case '*':
			case '/':
			case '%':
			case '!':
			case '#':
			case '$':
			case '`':
			case '^':
			case '\'':
				match(buff);
				break;

			default:
				if (LA(1) == '<' && LA(2) == '=') {
					match("<=");
				}
				else if (LA(1) == '>' && LA(2) == '=') {
					match(">=");
				}
				else if (LA(1) == '|' && LA(2) == '|') {
					match("||");
				}
				else if (LA(1) == '&' && LA(2) == '&') {
					match("&&");
				}
				else if (LA(1) == '>' && LA(2) == '>') {
					match(">>");
				}
				else if (LA(1) == '<' && LA(2) == '<') {
					match("<<");
				}
				else if (LA(1) == '=' && LA(2) == '=') {
					match("==");
				}
				else if (LA(1) == '~' && LA(2) == '=') {
					match("~==");
				}
				else if (LA(1) == '=') {
					match('=');
				}
				else if (LA(1) == '<') {
					match('<');
				}
				else if (LA(1) == '>') {
					match('>');
				}
				else if (LA(1) == '&') {
					match('&');
				}
				else if (LA(1) == '~') {
					match('~');
				}
				else if (LA(1) == '|') {
					match('|');
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
		}
		if (inputState.guessing == 0) {
			type = Token.SKIP;
		}
		if (create && token == null && type != Token.SKIP) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	

	// revisado por @will - SQL [OK]
	protected final void WHITESPACE(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = DELIMITER;
		switch (LA(1)) {
			case ' ':
				match(' ');
				break;
				
			case '_':
				match('_');
				break;
				
			case '\t':
				match('\t');
				break;
			
			case '\u000c':
				match('\f');
				break;
				
			case '\n':
			case '\r':
				if ((LA(1)=='\r') && (LA(2)=='\n')) {
					match("\r\n");
				}
				else if (LA(1)=='\r') {
					match('\r');
				}
				else if ((LA(1)=='\n')) {
					match('\n');
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				if ( inputState.guessing==0 ) {
					newline();
				}
				break;

			default:
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}

	
	// revisado por @will - SQL [OK]
	public final void HEX_DIGIT(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = HEX_DIGIT;
		
		match("0x");
		boolean check = true;
		while (check) {
			switch (LA(1)) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8': 
				case '9':
					DEC_DIGIT(false);
					break;

				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
					matchRange('a','f');
					break;

				case 'A':
				case 'B': 
				case 'C':  
				case 'D':
				case 'E': 
				case 'F':
					matchRange('A','F');
					break;

				default:
					check = false;
			}
		}
		if (create && token == null && type != Token.SKIP) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	
	
	// revisado por @will - SQL [OK]
	protected final void DEC_DIGIT(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = DEC_DIGIT;
		
		matchRange('0','9');
		if (create && token == null && type != Token.SKIP) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}

	
	// revisado por @will - SQL [OK] - ajustar o estilo.
	public final void SL_COMMENT(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = SL_COMMENT;

		if (LA(1) == '/' && LA(2) != '/') {
			match('/');
		}
		else if (LA(1) == '/' && LA(2) != '/') {
			match('-');
		}
		while (true) {
			if ((_tokenSet_1.member(LA(1)))) {
				match(_tokenSet_1);
			}
			else {
				break;
			}
		}
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}

	
	// revisado por @will - SQL [OK] - ajustar o estilo.
	public final void ML_COMMENT(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = ML_COMMENT;
			
		match("/*");
		while(true) {
			if (LA(1) == '\r' && LA(2) == '\n') {
				match('\r');
				match('\n');
				if (inputState.guessing == 0) {
					newline();
				}
			}
			else if (LA(1) == '*' && LA(2) != '/') {
				match('*');
			}
			else if (LA(1) == '\r') {
				match('\r');
				if (inputState.guessing == 0) {
					newline();
				}
			}
			else if (LA(1) == '\n') {
				match('\n');
				if (inputState.guessing == 0) {
					newline();
				}
			}
			else if ((_tokenSet_2.member(LA(1)))) {
				match(_tokenSet_2);
			}
			else {
				break;
			}
		}
		if ((LA(1) == '*')) {
			match("*/");
		}
		if (create && token == null && type != Token.SKIP) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	

	

	// revisado por @will - SQL [precisa revisar a sequencia de escape].
	public final void OBJECT(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = OBJECT;
		match('`');
		{
			loop:
				do {
					if (LA(1) == '`') {
						break loop;
					}
					else if ((_tokenSet_3.member(LA(1)))) {
						match(_tokenSet_3);
					}
					else {
						break loop;
					}
				} while (true);
		}
		if ((LA(1) == '`')) {
			match('`');
		}
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	
	// revisado por @will
	protected final void mEscape(boolean create) throws RecognitionException, CharStreamException, TokenStreamException {
		int type; Token token = null; int begin = text.length();
		type = ESCAPE;
		
		match('\\');
		
		switch (LA(1)) {
			case '0':
			case '1':
			case '2':
			case '3':
				matchRange('0','3');
				{
					loop:
						do {
							if (LA(1) >= '0' && LA(1) <= '9') {
								//mDIGIT(false);
								DEC_DIGIT(false);
							}
							else {
								break loop;
							}
						} while (true);
				}
				break;
			case '4':
			case '5':
			case '6':
			case '7':
				matchRange('4','7');
				{
					loop:
						do {
							if (LA(1) >= '0' && LA(1) <= '9') {
								DEC_DIGIT(false);
							}
							else {
								break loop;
							}
						} while (true);
				}
				break;
		
			case 'x':
				match('x');
				{
					int count = 0;
					loop:
						do {
							if (LA(1) >= '0' && LA(1) <= '9') {
								DEC_DIGIT(false);
							}
							else if (LA(1) >= 'a' && LA(1) <= 'f') {
								matchRange('a','f');
							}
							else if (LA(1) >= 'A' && LA(1) <= 'F') {
								matchRange('A','F');
							}
							else {
								if ( count >= 1 ) {
									break loop;
								}
								else {
									throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
								}
							}
							count++;
						} while (true);
				}
				break;

			default:
				if (_tokenSet_4.member(LA(1))) {
					match(_tokenSet_4);
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
		}
		if (create && token == null && type != Token.SKIP ) {
			token = makeToken(type);
			token.setText(new String(text.getBuffer(), begin, text.length() - begin));
		}
		_returnToken = token;
	}
	
		
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[131];
		data[0] = 8935423114204952064L;
		data[1] = 8646911290591150080L;
		return data;
	}
	
	
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[260];
		data[0] =- 9224L;
		for (int i = 1; i <= 3; i++) {
			data[i] =- 1L; 
		}
		for (int i = 64; i <= 127; i++) {
			data[i] =- 1L; 
		}
		return data;
	}
	
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[260];
		data[0] =- 4398046520328L;
		for (int i = 1; i <= 3; i++) {
			data[i] =- 1L; 
		}
		for (int i = 64; i <= 127; i++) {
			data[i] =- 1L; 
		}
		return data;
	}

	private static final long[] mk_tokenSet_3() {
		long[] data = new long[260];
		data[0]=-549755813896L;
		for (int i = 1; i <= 3; i++) { 
			data[i] =- 1L; 
		}
		for (int i = 64; i <= 127; i++) {
			data[i] =- 1L; 
		}
		return data;
	}
	
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[260];
		data[0] =- 71776119061217288L;
		data[1] =- 72057594037927937L;
		for (int i = 2; i <= 3; i++) {
			data[i] =- 1L; 
		}
		for (int i = 64; i <= 127; i++) {
			data[i] =- 1L; 
		}
		return data;
	}
	
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[260];
		data[0] =- 8L;
		for (int i = 1; i <= 3; i++) {
			data[i] =- 1L; 
		}
		for (int i = 64; i <= 127; i++) {
			data[i] =- 1L; 
		}
		return data;
	}
	
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[260];
		data[0] =- 1032L;
		for (int i = 1; i <= 3; i++) {
			data[i]=-1L; 
		}
		for (int i = 64; i <= 127; i++) {
			data[i]=-1L; 
		}
		return data;
	}
	
	private static final long[] mk_tokenSet_7() {
		long[] data = new long[260];
		data[0] =- 17179878408L;
		data[1] =- 268435457L;
		for (int i = 2; i <= 3; i++) {
			data[i] =- 1L; 
		}
		for (int i = 64; i <= 127; i++) {
			data[i] =- 1L; 
		}
		return data;
	}
	
	
}