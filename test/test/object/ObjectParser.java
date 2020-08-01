package test.object;

import java.io.IOException;

import nsgl.generic.array.Vector;
import nsgl.language.Lexer;
import nsgl.language.Parser;
import nsgl.language.Token;
import nsgl.language.Typed;
import nsgl.language.TypedValue;

public class ObjectParser extends Parser{
	@Override
	public Typed analize( Vector<Token> tokens ) throws IOException{
		tokens = Lexer.remove_space(tokens);
		return super.analize(tokens);
	}
	
	@Override
	public Typed process() throws IOException{
		if( check_symbol('[') ) {
			Vector<Typed> v = new Vector<Typed>();
			next();
			while( !check_symbol(']') ){
				if(check_symbol(',')) throw token.exception("·Unexpected· ,");
				v.add(process());
				next();
				if(check_symbol(',')) {
				    next();
				    if(check_symbol(']')) throw token.exception("·Unexpected· ]");
				}else {
				    if(!check_symbol(']')) throw token.exception("·Unexpected· ]");
				}
			}
			return new TypedValue<Vector<Typed>>("Object", v);
		}
		return token; 
	}	
}