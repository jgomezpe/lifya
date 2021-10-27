package lifya.parsergenerator.lexeme;

import lifya.Source;
import lifya.Token;

/**
 * Category token type recognizer.
 * <ul>
 * <li> <i>.</i> : Any character </li>
 * <li> <i>\d</i> : Digit character </li>
 * <li> <i>\w</i> : Alphabetic character </li>
 * <li> <i>\l</i> : Letter character </li>
 * <li> <i>\D</i> : Non digit character </li>
 * <li> <i>\W</i> : Non alphabetic character </li>
 * <li> <i>\L</i> : Non letter character </li>
 * <li> <i>\S</i> : Non space character (not \s) </li>
 * </ul>
 */
public class Category extends Matcher{
	/**
	 * Creates a category token type recognizer
	 * @param type Token type
	 * @param embedded If the token type recognizer is embedded in a Lifya parser or not
	 */	
	public Category(String type, boolean embedded) { super(type, embedded); }

	/**
	 * Reads a token from the input source 
	 * @param input Symbol source
	 * @return Token read from the symbol source
	 */
	@Override
	public Token match(Source input) {
		int pos = input.pos();
		if(match_category(input)) return token(input,pos,input.substring(pos,input.pos()));
		return error(input, pos);
	}

	/**
	 * Determines if the token type can start with the given character
	 * @param c Character to analyze
	 * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c) { return c=='\\' || c=='.'; }
}