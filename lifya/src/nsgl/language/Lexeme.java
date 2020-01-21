package nsgl.language;

import nsgl.recover.RegexRecover;

public class Lexeme {
	protected char type;
	protected RegexRecover regex;
	
	public Lexeme( char type, RegexRecover regex ){
		this.type = type;
		this.regex = regex;
	}
	
	public int priority() { return 0; }
	
	public char type(){ return type; }
	public RegexRecover regex(){ return regex; }
	
	public Token token( String input ){
		String txt = regex.match(input); 
		if( txt!=null ) return new Token(type, 0, txt);
		return null;
	}
	
	public Object instance( String input ) throws Exception{ return regex.instance(input); }
}