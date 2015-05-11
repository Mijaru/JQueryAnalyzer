// $ANTLR : "Recognizer.g" -> "RecognizerParser.java"$
package Components.MainWindowComponents.Highlighter;
import javax.swing.text.StyledDocument;
import javax.swing.text.Style;
import javax.swing.text.Element;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;

public class RecognizerParser extends antlr.LLkParser implements RecognizerParserTokenTypes {
	private StyledDocument document;
	private HighlightPainter painter;
	public static final String[] _tokenNames = {
		"EOF",
		"SL_COMMENT",
		"ML_COMMENT",
		"STRING_D",
		"STRING_S",
		"DEC_DIGIT",
		"HEX_DIGIT",
		"DELIMITER",
		"ESCAPE",
		
		
		
		
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"STRING_LIT",
		"SL_COMMENT",
		"ML_COMMENT",
		"INTLT",
		"HEX_DIGIT",
		"CHARLIT",
		"\"int\"",
		"\"float\"",
		"\"double\"",
		"\"byte\"",
		"\"char\"",
		"\"boolean\"",
		"\"short\"",
		"\"long\"",
		"\"abstract\"",
		"\"continue\"",
		"\"for\"",
		"\"new\"",
		"\"switch\"",
		"\"assert\"",
		"\"default\"",
		"\"goto\"",
		"\"package\"",
		"\"synchronized\"",
		"\"do\"",
		"\"if\"",
		"\"private\"",
		"\"this\"",
		"\"break\"",
		"\"implements\"",
		"\"protected\"",
		"\"throw\"",
		"\"else\"",
		"\"import\"",
		"\"public\"",
		"\"throws\"",
		"\"case\"",
		"\"enum\"",
		"\"instanceof\"",
		"\"return\"",
		"\"transient\"",
		"\"catch\"",
		"\"extends\"",
		"\"try\"",
		"\"final\"",
		"\"interface\"",
		"\"static\"",
		"\"class\"",
		"\"finally\"",
		"\"strictfp\"",
		"\"volatile\"",
		"\"const\"",
		"\"native\"",
		"\"super\"",
		"\"while\"",
		"\"void\"",
		"WS",
		"DIGIT",
		"LETTER",
		"Escape",
		"DELIMETER",
		"MISC",
		"IDENT"
	};
	
	public int getOffset(int line, int column) {
    	Element root = document.getDefaultRootElement();
		int lineStart = root.getElement(line).getStartOffset();
	    return lineStart + column;
    }
    
    public Style getStyle(Styles style) {
    	return StyleManager.getInstance().getStyle(style).getStyle();
    }
    
    public void paint(Token token, Style style) {
    	if (token == null) {
	    	return;
    	}
    	int line 	= token.getLine() - 1;
    	int column  = token.getColumn() - 1;
    	String word = token.getText();
    	paint(word, line, column, style);
    }
    
    public void paint(String word, int line, int column, Style style) {   
		int offset = getOffset(line, column);
		painter.highlight(offset, word.length(), style);			
    }    

    protected RecognizerParser(TokenBuffer tokenBuf, int k) {
    	super(tokenBuf,k);
    	tokenNames = _tokenNames;
    }

    public RecognizerParser(TokenBuffer tokenBuf) {
    	this(tokenBuf,5);
    }

    protected RecognizerParser(TokenStream lexer, int k) {
    	super(lexer,k);
    	tokenNames = _tokenNames;
    }

    public RecognizerParser(TokenStream lexer) {
    	this(lexer,5);
    }

    public RecognizerParser(ParserSharedInputState state) {
    	super(state,5);
    	tokenNames = _tokenNames;
    }

	public final void start(StyledDocument doc, HighlightPainter paint) throws RecognitionException, TokenStreamException {
		document = doc;
		painter  = paint;
		//System.out.println("RecognizerParser.start[LA(1)=" + LA(1) + "]");
		{
			loop: // 567
				do {
					switch (LA(1)) {
						case HEX_DIGIT:
						case DEC_DIGIT:
						case STRING_D:
						case STRING_S:
						case ML_COMMENT:
						case SL_COMMENT:
						case OBJECT:
							word();
							break;
						
						case KEYWORD:
							keywords();
							break;
						default:
							break loop;
					}
				} while (true);
		}
	}
	
	// revisado por @will
	public final void word() throws RecognitionException, TokenStreamException {
		switch (LA(1)) {
			case HEX_DIGIT:
			case DEC_DIGIT:
				values();
				break;
		
			case SL_COMMENT:
			case ML_COMMENT:
				comments();
				break;
			
			case STRING_S:
				match(STRING_S);
				paint(LT(0), getStyle(Styles.STRING_S));
				break;
				
			case STRING_D:
				match(STRING_D);
				paint(LT(0), getStyle(Styles.STRING_D));
				break;
			
			case OBJECT:
				match(OBJECT);
				paint(LT(0), getStyle(Styles.OBJECT));
				break;
				
			default:
				throw new NoViableAltException(LT(1), getFilename());
		}
	}
	
	public final void keywords() throws RecognitionException, TokenStreamException {
		switch (LA(1)) {
			case KEYWORD:
				match(KEYWORD);
				break;

			default:
				throw new NoViableAltException(LT(1), getFilename());
		
		}
		paint(LT(0), getStyle(Styles.KEYWORD)); 
	}

	public final void values() throws RecognitionException, TokenStreamException {
		switch (LA(1)) {
			case HEX_DIGIT:
			case DEC_DIGIT:
				number();
				break;
				
			default:
				throw new NoViableAltException(LT(1), getFilename());
		}
	}
	
	public final void number() throws RecognitionException, TokenStreamException {
		switch (LA(1)) {
			case DEC_DIGIT:
				LT(1);
				match(DEC_DIGIT);
				break;
				
			case HEX_DIGIT:
				LT(1);
				match(HEX_DIGIT);
				break;
				
			default:
				throw new NoViableAltException(LT(1), getFilename());
		}
		paint(LT(0), getStyle(Styles.NUMBER));
	}
	
	
	public final void comments() throws RecognitionException, TokenStreamException {
		switch (LA(1)) {
			case SL_COMMENT:
				match(SL_COMMENT);
				paint(LT(0), getStyle(Styles.SL_COMMENT));
				break;

			case ML_COMMENT:
				match(ML_COMMENT);
				paint(LT(0), getStyle(Styles.ML_COMMENT));
				break;
				
			default:
				throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
}
