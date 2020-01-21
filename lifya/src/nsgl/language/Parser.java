package nsgl.language;

import java.io.IOException;

import nsgl.generic.array.DynArray;
import nsgl.exception.IO;

public abstract class Parser{
	protected int pos;
	protected Token token;
	protected DynArray<Token> tokens;
	
	char rule='$';
	
	public char rule(){ return rule; }
	public void setRule(char rule) { this.rule = rule; }

	public Typed analize( DynArray<Token> tokens ) throws IOException{
		pos = 0;
		this.tokens = tokens;
		next();
		return process();
	}
	
	protected abstract Typed process() throws IOException;
	
	protected Token next() throws IOException{
		if( pos == tokens.size() ) throw IO.exception(IO.EOI);
		token = tokens.get(pos);
		pos++;
		return token;
	}	
}