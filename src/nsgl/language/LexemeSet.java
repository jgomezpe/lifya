package nsgl.language;

import java.util.regex.Pattern;

import nsgl.exception.NoSuch;
import nsgl.generic.hashmap.HashMap;

public class LexemeSet {
	protected HashMap<Character,Lexeme> lexemes = new HashMap<Character,Lexeme>();
	
	public void add( Lexeme lexeme ){ lexemes.set(lexeme.type(),lexeme); }
	
	public void remove( char lexeme ) { lexemes.remove(lexeme); }
	
	protected Pattern pattern() {
		StringBuilder sb = new StringBuilder();
		String pipe = "";
		for( Lexeme l:lexemes ) {
			sb.append(pipe); 
			sb.append('(');
			sb.append(l.regex());
			sb.append(')');
			pipe = "|";
		}
		return Pattern.compile(sb.toString());
	}

	public Token get( String input, int offset ) {
		int priority = -1;
		Token tf = null;
		for( Lexeme l:lexemes ) {
			Token t = l.token(input);
			if( t!=null && t.value().length()==input.length() && l.priority() > priority ){
				t.pos = offset;
				priority = l.priority();
				tf = t;
			}
		}
		return tf;
	}
	
	public Object map( Token token ) throws Exception{
		Lexeme l = lexemes.get(token.type());
		if( l==null ) throw NoSuch.exception(NoSuch.NOSUCHELEMENT, token.type());
		return l.instance(token.value());
	}
}