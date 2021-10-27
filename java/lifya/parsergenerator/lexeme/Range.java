package lifya.parsergenerator.lexeme;

import lifya.Source;
import lifya.Token;

/**
 * Range token type recognizer (recognizes characters in a range)
 */
public class Range extends Matcher{
	/**
	 * Creates a range token type recognizer
	 * @param type Token type
	 * @param embedded If the token type recognizer is embedded in a Lifya parser or not
	 */	
	public Range(String type, boolean embedded) { super(type, embedded); }

	/**
	 * Reads a token from the input source 
	 * @param input Symbol source
	 * @return Token read from the symbol source
	 */
	@Override
	public Token match(Source input) {
		int pos = input.pos();
		if(!match_one(input)) return error(input, input.pos());
		char c = input.current();
		if(c!='-')  return error(input, input.pos());
		input.next();
		if(!match_one(input)) return error(input, input.pos());
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