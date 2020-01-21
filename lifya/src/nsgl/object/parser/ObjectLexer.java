package nsgl.object.parser;

import nsgl.language.Lexeme;
import nsgl.language.Lexer;
import nsgl.language.lexeme.SpaceLexeme;
import nsgl.language.lexeme.SymbolLexeme;

public class ObjectLexer extends Lexer{
	public static final char INTEGER = 'I';
	public static final char REAL = 'D';
	public static final char CHAR = 'C';
	public static final char STRING = 'S';
	public static final char SYMBOL = '#';
	public static final char SPACE = ' ';
	public static final char WORD = 'W';
	
	public ObjectLexer() {
		add( new Lexeme(INTEGER, new nsgl.integer.Recover()) );
		add( new Lexeme(REAL, new nsgl.real.Recover()) );
		add( new Lexeme(STRING, new nsgl.string.Recover()) );
		add( new Lexeme(CHAR, new nsgl.character.Recover()) );
		add( new SpaceLexeme() );
		add( new SymbolLexeme("\\[\\],") );
	}
}
