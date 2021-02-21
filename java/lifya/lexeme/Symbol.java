package lifya.lexeme;

import java.util.HashMap;

import lifya.Source;
import lifya.Token;

public class Symbol implements Lexeme<Character>{
    public static final String TAG = "symbol";
    protected HashMap<Character, Character> table = new HashMap<Character, Character>();
    	protected String type;
    	
    	public Symbol(String symbols){ this(symbols,TAG); }

    	public Symbol(String symbols, String type){
    	    this.type = type;
    	    for( int i=0; i<symbols.length(); i++ )
    		table.put(symbols.charAt(i),symbols.charAt(i));
    	}
 	
	@Override
	public Token match(Source txt, int start, int end) {
	    if(startsWith(txt.get(start)))
		return new Token(type(),txt,start,start+1,txt.get(start));
	    else 
		return error(txt,start,start+1);
	}
	
	@Override
	public boolean startsWith(char c) { return table.containsKey(c); }
	
	@Override
	public String type() { return type; }
}