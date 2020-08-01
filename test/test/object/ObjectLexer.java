package test.object;

import nsgl.language.Lexer;
import nsgl.language.lexeme.Space;
import nsgl.language.lexeme.Symbol;

public class ObjectLexer extends Lexer{
	
	public ObjectLexer() {
		add( new nsgl.integer.Parse(), 2 );
		add( new nsgl.real.Parse(), 1 );
		add( new nsgl.string.Parse(), 2 );
		add( new nsgl.character.Parse(), 2 );
		add( new Space(), 2 );
		add( new Symbol("\\[\\],"), 2 );
	}
}
