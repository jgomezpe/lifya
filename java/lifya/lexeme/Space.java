package lifya.lexeme;

import lifya.Source;
import lifya.Token;

public class Space implements Lexeme<String>{
    public static final String TAG = "space";
	@Override
	public Token match(Source txt, int start, int end) {
	    if( !startsWith(txt.get(start)) )
		return error(txt, start, start+1);
	    int n = end;
	    end=start+1;
	    while(end<n && Character.isWhitespace(txt.get(end))) end++;
	    return token(txt,start,end," ");
	}

	@Override
	public boolean startsWith(char c) { return Character.isWhitespace(c); }

	@Override
	public String type() { return TAG; }
}