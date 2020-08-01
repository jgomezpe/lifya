package nsgl.language;

import java.io.IOException;

import nsgl.character.CharacterSequence;
import nsgl.generic.array.Vector;

public class Language<T>{
	protected Lexer lexer;
	protected Parser parser;
	protected Meaner<T> meaner;

	public Language( Lexer lexer, Parser parser, Meaner<T> meaner ){
		this.lexer = lexer;
		this.parser = parser;
		this.meaner = meaner;
	}
	
	
	public T process( String input ) throws IOException{
	    return process( new CharacterSequence(input));
	}
	
	public T process(CharacterSequence input ) throws IOException{
		Vector<Token> tokens = lexer.analize(input);
		Typed r = parser.analize(tokens);
		return meaner.apply(r);				
	}
}