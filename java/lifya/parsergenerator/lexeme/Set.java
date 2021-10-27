package lifya.parsergenerator.lexeme;

import lifya.Source;
import lifya.Token;

/**
 * Set token type recognizer (recognizes characters in a set)
 */
public class Set extends Matcher{
	/**
	 * Creates a set token type recognizer
	 * @param type Token type
	 * @param embedded If the token type recognizer is embedded in a Lifya parser or not
	 */	
	public Set(String type, boolean embedded) { super(type, embedded); }

	/**
	 * Determines if the token type can start with the given character
	 * @param c Character to analyze
	 * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c) { return c=='[' || c=='-'; }

	/**
	 * Reads a token from the input source 
	 * @param input Symbol source
	 * @return Token read from the symbol source
	 */
	@Override
	public Token match(Source input) {
		char c = input.current();
		if(!this.startsWith(c)) return error(input, input.pos());
		int pos = input.pos();
		if(c=='-' && input.next()!='[') return error(input, pos);

		input.next();
		
		while(match_range(input) || match_one(input) || match_category(input)) {
			c = input.current();
			switch(c) {
				case ']':
					input.next();
					return token(input,pos,input.substring(pos, input.pos()));
				case '|':
					input.next();
				break;
				default: return error(input, input.pos());
			}
		}
		return error(input, input.pos());
	}
}