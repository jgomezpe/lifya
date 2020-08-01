package nsgl.language;

import java.io.IOException;

import nsgl.generic.array.Vector;
import nsgl.language.lexeme.Symbol;

public abstract class Parser{
	protected int pos;
	protected Token token;
	protected Vector<Token> tokens;
	
	String rule="$";
	
	public String rule(){ return rule; }
	public void setRule(String rule) { this.rule = rule; }

	public Typed analize( Vector<Token> tokens ) throws IOException{
		pos = 0;
		this.tokens = tokens;
		next();
		return process();
	}
	
	protected abstract Typed process() throws IOException;

	protected boolean check_symbol(String tag, char symbol) { return token.type().equals(tag) && (char)(token.value())==symbol; }

	protected boolean check_symbol(char symbol) { return check_symbol(Symbol.TAG, symbol); }

	protected boolean check_type(String type) { return token.type().equals(type); }
	
	public Token next() throws IOException{
		if( !available() ) throw new IOException("·Unexpected end of file·");
		token = tokens.get(pos);
		pos++;
		return token;
	}
	
	public boolean available() { return pos<tokens.size(); }
}