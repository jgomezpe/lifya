package nsgl.language;

import nsgl.generic.array.DynArray;

public class Language<T>{
	protected Lexer lexer;
	protected Parser parser;
	protected Meaner<T> meaner;

	public Language( Lexer lexer, Parser parser, Meaner<T> meaner ){
		this.lexer = lexer;
		this.parser = parser;
		this.meaner = meaner;
	}
	
	
	public T process( String input ) throws Exception{
		DynArray<Token> tokens = lexer.analize(input);
		Typed r = parser.analize(tokens);
		return meaner.apply(r);				
	}
}