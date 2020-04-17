package nsgl.language;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nsgl.generic.array.Vector;
import nsgl.exception.IO;
import nsgl.exception.ProcessException;

public class Lexer extends LexemeSet{
	public Lexer(){}
	public Lexer( LexemeSet source ){ for( Lexeme l:source.lexemes ) this.add(l); }
	
	protected Vector<Token> process( Pattern pattern, String input, int start ){
		Matcher matcher = pattern.matcher(input);
		Vector<Token> tokens = new Vector<Token>();
		if( matcher.find(start) ) {
			tokens.add(get(matcher.group(),matcher.start()));
			while( matcher.find() ) tokens.add(get(matcher.group(),matcher.start()));
		}
		return tokens;
	}
	
	public Vector<Token> process( String input, int start ){ return process( pattern(), input, start ); }

	public Vector<Token> process( String input ){ return process( pattern(), input, 0 ); }

	public Vector<Token> analize( String input, int start ) throws IOException{
		Vector<Token> tokens = process( pattern(), input, start );
		Vector<Object> exceptions = new Vector<Object>();
		for( Token t:tokens ) {
			if( t.pos()!=start ) exceptions.add(new Object[]{IO.UNEXPECTED, input.substring(start, t.pos()), start});
			start = t.pos()+t.length();
		}
		if( exceptions.size()==0 ) return tokens;
		exceptions.add(0,ProcessException.MULTIPLE);
		Object[] e = new Object[exceptions.size()];
		for( int i=0; i<e.length; i++) e[i] = exceptions.get(i);
		throw IO.exception(e);
	}
	
	public Vector<Token> analize( String input ) throws IOException{ return analize( input, 0 );	}
	
	public static Vector<Token> remove(Vector<Token> tokens, String toremove ){
		for( int i=tokens.size()-1; i>=0; i-- )	if( toremove.indexOf(tokens.get(i).type()) >= 0 ) tokens.remove(i);
		return tokens;
	}
	
	public static Vector<Token> remove_space(Vector<Token> tokens ){ return remove(tokens, " "); }
	
}