package lifya.lexeme;

import lifya.Read;
import lifya.Source;
import lifya.Token;

public interface Lexeme<T> extends Read<T>{
	/**
	 * Determines if the lexeme can star with the given character
	 * @param c Character to analize
	 * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
	 */
	boolean startsWith(char c);

	String type();
	
	default Token error(Source txt, int start, int end) {
	    return new Token(txt,start,end,type());
	}
	
	default Token token(Source txt, int start, int end, Object value) {
	    return new Token(type(),txt,start,end,value);
	}
}
