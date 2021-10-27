package lifya.parsergenerator.lexeme;

import lifya.Source;
import lifya.Token;
import lifya.generic.lexeme.Lexeme;
import lifya.parsergenerator.GeneratorConstants;

/**
 * Spaces recognizer ([\n|\t|\r|\s]+)
 */
public class Space extends Lexeme{
	/**
	 * Creates a space recognizer
	 */
	public Space() { super(GeneratorConstants.SPACE); }

	/**
	 * Reads a token from the input source 
	 * @param input Symbol source
	 * @return Token read from the symbol source
	 */
	@Override
	public Token match(Source input) {
		int start = input.pos();
		char c = input.current();
		if(!this.startsWith(c)) return error(input, input.pos());
		while(startsWith(input.next())){}
		return token(input,start," ");
	}

	/**
	 * Determines if the token type can start with the given character
	 * @param c Character to analyze
	 * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c) { return c==' ' || c=='\r' || c=='\t' || c=='\n'; }
}