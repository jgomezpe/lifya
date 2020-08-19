package nsgl.language;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nsgl.generic.array.Vector;
import nsgl.generic.hashmap.HashMap;
import nsgl.language.lexeme.Space;
import nsgl.parse.Regex;
import nsgl.character.CharacterSequence;

public class Lexer{
	protected HashMap<String,Regex> lexemes = new HashMap<String,Regex>();

	public Lexer(){}
//	public Lexer( LexemeSet source ){ for( Regex l:source.lexemes ) this.add(l, source.priority.get(l.type())); }

	public void add( Regex lexeme){
		lexemes.set(lexeme.type(),lexeme);
	}
	
	public void remove( String lexeme ){ 
		lexemes.remove(lexeme);
	}

	protected Pattern pattern() {
		StringBuilder sb = new StringBuilder();
		String pipe = "";
		for( Regex l:lexemes ) {
			sb.append(pipe); 
			sb.append('(');
			sb.append(l.regex());
			sb.append(')');
			pipe = "|";
		}
		return Pattern.compile(sb.toString());
	}

	public Token get( String matched, CharacterSequence input, int offset ) {
		Object value = null;
		for( Regex lex:lexemes ){
			try {
			    value = lex.instance(input, matched);
			    //input.shift(matched.length());
			    return new Token(lex.matched_as(), matched, value, input, offset);
			}catch(IOException e) {}    
		}
		return null;
	}
	
	protected Vector<Token> analize( CharacterSequence input, int start, Pattern pattern ) throws IOException{
		StringBuilder sb = new StringBuilder();
		Matcher matcher = pattern.matcher(input);
		Vector<Token> tokens = new Vector<Token>();
		if( matcher.find(start) ){
		    	do{
		    	    	int nstart = matcher.start();
			    	String matched = matcher.group();
			    	if(start!=nstart) sb.append(input.exception("Unexpected character", start).getMessage()+"\n");
			    	tokens.add(get(matched,input,nstart));
			    	start = nstart+matched.length();
			}while( matcher.find() );
		}else sb.append(input.exception("Unexpected character", start).getMessage());
		String msg = sb.toString();
		if( msg.length() > 0 ) throw new IOException(msg);
		return tokens;
	}
	
	public Vector<Token> shallow_analize( CharacterSequence input, int start ){    
		Pattern pattern = pattern();
		Matcher matcher = pattern.matcher(input);
		Vector<Token> tokens = new Vector<Token>();
		if( matcher.find(start) ){
		    	do{
		    	    	int nstart = matcher.start();
			    	String matched = matcher.group();
			    	tokens.add(get(matched,input,nstart));
			    	start = nstart+matched.length();
			}while( matcher.find() );
		}
		return tokens;
	}
	
	public Vector<Token> analize( CharacterSequence  input ) throws IOException{ return analize( input, 0, pattern() ); }
	
	public static Vector<Token> remove(Vector<Token> tokens, String toremove ){
		for( int i=tokens.size()-1; i>=0; i-- )	if( toremove.indexOf(tokens.get(i).type()) >= 0 ) tokens.remove(i);
		return tokens;
	}
	
	public static Vector<Token> remove_space(Vector<Token> tokens ){ return remove(tokens, Space.TAG); }	
}