package lifya.parsergenerator.lexeme;

import lifya.Source;
import lifya.Token;
import lifya.generic.lexeme.Lexeme;

/**
 * Generic token type recognizer for the Lifya parser/token recognizer generator
 */
public class Matcher extends Lexeme{
	
	protected String category="SwWdDlL";	
	protected String escape="\\{}()|[]-?*+.$nrts";	
	protected String symbol=" \n\t\r{}()|[]-?*+.$";
	protected String embedded=":=%<>";
	
	/**
	 * Creates an abstract character token type recognizer
	 * @param type Token type
	 * @param embedded If the token type recognizer is embedded in a Lifya parser or not
	 */	
	public Matcher(String type, boolean embedded) {
		super(type);
		if(embedded) {
			escape += this.embedded;
			symbol += this.embedded;
		}
	}
	
	protected boolean match_error(Source input, int start) {
		input.locate(start);
		return false;		
	}

	/**
	 * Matches the current character in the input source with a character in a range (advances the input source if matches)
	 * @param input Source input
	 * @return <i>true</i> if the current character in the input source falls in the range, <i>false</i> otherwise
	 */
	public boolean match_range(Source input) {
		int pos = input.pos();
		if(!match_one(input)) return false;
		if(input.current()!='-') return match_error(input,pos);
		input.next();
		if(!match_one(input)) return match_error(input,pos);
		return true;
	}

	/**
	 * Matches the current character in the input source with a character category (advances the input source if matches)
	 * @param input Source input
	 * @return <i>true</i> if the current character in the input source falls in a category, <i>false</i> otherwise
	 */
	public boolean match_category(Source input) {
		char c = input.current();
		if(c=='.') {
			input.next();
			return true;
		}
		if(c!='\\') return false;
		int pos = input.pos();
		c = input.next();
		if(category.indexOf(c)<0) return match_error(input,pos);
		input.next();
		return true;
	}
	
	/**
	 * Matches the current character in the input source with a character (advances the input source if matches)
	 * @param input Source input
	 * @return <i>true</i> if the current character in the input source is a character (neither a category, or special symbol),
	 *  <i>false</i> otherwise
	 */
	public boolean match_one(Source input) {
		if(input.eoi()) return false;
		char c = input.current();
		if(symbol.indexOf(c)>=0) return false;
		int start = input.pos();
		if(c=='\\') {
			c = input.next();
			if(input.eoi()) return match_error(input, start);
			if(c=='u') {
				c = input.next();
				int counter = 0;
				while(!input.eoi() && counter<4 && (('0'<=c && c<='9') || 
						('A'<=c && c<='F') || ('a'<=c && c<='f'))){
					c = input.next();
					counter++;
				}
				if(counter!=4) return match_error(input, start);
			}else if(escape.indexOf(c)<0) return match_error(input, start);
		}
		input.next();
		return true;
	}

	/**
	 * Reads a token from the input source 
	 * @param input Symbol source
	 * @return Token read from the symbol source
	 */
	@Override
	public Token match(Source input) { return null; }

	/**
	 * Determines if the token type can start with the given character
	 * @param c Character to analyze
	 * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c) { return false; }
}