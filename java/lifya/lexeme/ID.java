package lifya.lexeme;

import lifya.Source;
import lifya.Token;

public class ID<T> implements Lexeme<T>{
	@Override
	public Token match(Source txt, int start, int end) {
	    if( !startsWith(txt.get(start)) )
		return error(txt, start, start+1);
	    int n = end;
	    end = start;
	    while(end<n && txt.get(end)=='_') end++;
	    if( end==n ) return error(txt,start,end);
	    if(!Character.isLetter(txt.get(end)))
		return error(txt,start,end);
	    while(end<n && Character.isAlphabetic(txt.get(end))) end++;
	    return new Token(type(),txt,start,end,txt.substring(start,end));
	}

	@Override
	public boolean startsWith(char c){ 
	    return c=='_' || Character.isLetter(c);
	}

	@Override
	public String type() { return "id"; }    
}