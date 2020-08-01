package test.object;

import java.io.IOException;

import nsgl.character.CharacterSequence;
import nsgl.generic.array.Vector;
import nsgl.language.Token;
import nsgl.language.Typed;

public class ObjectLanguageTest {
	public static Vector<Token> lexer(String input) throws IOException{
	    ObjectLexer lex = new ObjectLexer();
	    return lex.analize(new CharacterSequence(input));
	}
	
	public static Typed parser( Vector<Token> tokens ) throws IOException{
	    ObjectParser parser = new ObjectParser();
	    Typed t = parser.analize(tokens);
	    if( parser.available() ) System.out.println("Input was not fully consumed "+parser.next().exception("").getMessage());
	    return t;
	}
	
	public static Object meaner( Typed t ) throws IOException{
	    ObjectMeaner meaner = new ObjectMeaner();
	    return meaner.apply(t);
	}
	
	public static void main( String[] args ) {
	    //String txt = "123.45e-2,  \n ['A',\"Dummy \\n Text\"],2345";
	    //String txt = "123.45e-2<  \n ['A'>,\"Dummy \\n Text\"],2345"; // With Lexer errors
	    String txt = "['A' \"Dummy \\n Text\"],2345"; // With Grammar errors
	    try {
		Vector<Token> tokens = lexer(txt);
		for( Token t:tokens ) System.out.println(t);
		Typed p = parser(tokens);
		System.out.println(p);
		Object o = meaner(p);
		System.out.println(o);
	    }catch(Exception e) {
		System.err.print(e.getMessage());
	    }
	}
}
