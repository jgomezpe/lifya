package lifya.lexeme;

import java.util.Base64;
import java.util.Base64.Decoder;

import lifya.Source;
import lifya.Token;

public class BlobParser implements Lexeme<byte[]>{ 
    public static final char STARTER = '#';
    public static final String TAG = "byte[]";
    protected boolean useStarter = false;
    protected int length;

	public BlobParser() { this(false); } 
	
	public BlobParser(boolean useStarter ) { this.useStarter = useStarter; }

	public boolean valid(char c) {
	    return Character.isLetterOrDigit(c)||c=='+'||c=='/';
	}
	
	@Override
	public String type() { return TAG; }

	@Override
	public Token match(Source input, int start, int end){
	    if(!startsWith(input.get(start)))
		return error(input,start,start+1);
	    int n=end;
	    end=start+1;
	    while(end<n && valid(input.get(end))) end++;
	    int s = (useStarter)?start+1:start;
	    int m = (end-s)%4;
	    if(s==end || m==1) return error(input,start,end);
	    if(m>0) {
		while(end<n && m<4 && input.get(end)=='=') {
		    end++;
		    m++;
		}
		if(m<4) return error(input,start,end);
	    }
	    Decoder dec = Base64.getMimeDecoder();
	    Object obj = dec.decode(input.substring(s,end));
	    return token(input,start,end,obj);
	}

	@Override
	public boolean startsWith(char c) {
	    return useStarter?(c==STARTER):valid(c);
	}
}