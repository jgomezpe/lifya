package lifya.parsergenerator.lexeme;

import lifya.Source;
import lifya.Token;

/**
 * A character recognizer
 */
public class Any extends Matcher{
	/**
	 * Creates a character token type recognizer
	 * @param type Token type
	 * @param embedded If the token type recognizer is embedded in a Lifya parser or not
	 */	
	public Any(String type, boolean embedded) { super(type,embedded); }
	
	/**
	 * Reads a token from the input source 
	 * @param input Symbol source
	 * @return Token read from the symbol source
	 */
	@Override
	public Token match(Source input) {
		if(input.eoi()) return error(input, input.pos());
		int count = 0;
		int pos = input.pos();
		while(match_one(input)) count++;
		if(count == 0 ) return error(input, input.pos());
		return token(input,pos,input.substring(pos, input.pos()));
	}

	/**
	 * Determines if the token type can start with the given character
	 * @param c Character to analyze
	 * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c) { return symbol.indexOf(c)<0; }
}