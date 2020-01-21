package nsgl.object.parser;

import java.io.IOException;

import nsgl.generic.array.DynArray;
import nsgl.exception.IO;
import nsgl.language.Lexer;
import nsgl.language.Parser;
import nsgl.language.Token;
import nsgl.language.Typed;
import nsgl.language.TypedValue;

public class ObjectParser extends Parser{
	protected char S = '#'; 
	@Override
	public Typed analize( DynArray<Token> tokens ) throws IOException{
		tokens = Lexer.remove_space(tokens);
		return super.analize(tokens);
	}
	
	@Override
	public Typed process() throws IOException{
		if( token.type()==S && token.value().charAt(0)=='[' ) {
			DynArray<Typed> v = new DynArray<Typed>();
			next();
			while( token.type() != S || token.value().charAt(0)!=']' ){
				if(token.type() == S && token.value().charAt(0)==',' ) throw IO.exception(IO.UNEXPECTED, ',', token.pos());
				v.add(process());
				if(next().type() == S && token.value().charAt(0)==',' && next().type() == S && token.value().charAt(0)!='[') 
					throw IO.exception(IO.UNEXPECTED, token.value(), token.pos());
			}
			return new TypedValue<DynArray<Typed>>('O', v);
		}
		return token; 
	}	
}