package test.object;

import nsgl.language.Lexer;
import nsgl.language.lexeme.Space;
import nsgl.language.lexeme.Symbol;

public class ObjectLexer extends Lexer{
	
	public ObjectLexer() {
		add( new nsgl.number.Parse() );
		add( new nsgl.string.Parse() );
		add( new nsgl.character.Parse() );
		add( new Space() );
		add( new Symbol("\\[\\],") );
	}
}
