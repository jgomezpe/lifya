package nsgl.language;

import java.io.IOException;
import java.util.regex.Pattern;

import nsgl.character.CharacterSequence;
import nsgl.generic.hashmap.HashMap;
import nsgl.parse.Regex;

public class LexemeSet {
	protected HashMap<String,Regex> lexemes = new HashMap<String,Regex>();
	protected HashMap<String,Integer> priority = new HashMap<String,Integer>();
	
	public void add( Regex lexeme, int priority ){
		lexemes.set(lexeme.type(),lexeme);
		this.priority.set(lexeme.type(),priority);
	}
	
	public void remove( String lexeme ){ 
		lexemes.remove(lexeme);
		priority.remove(lexeme);
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
		int priority = -1;
		Object value = null;
		String type = null;
		for( Regex lex:lexemes ){
			CharacterSequence seq = new CharacterSequence(matched);
			String ntype = lex.type();
			try {
			    Object obj = lex.parse(seq);
			    int np = this.priority.get(ntype);
			    if(seq.length()==0 && np>priority) {
				type = ntype;
				value = obj;
				priority = np;
			    }
			}catch(IOException e) {}    
		}
		if( type!=null ) return new Token(type, matched, value, input, offset);
		return null;
	}
}