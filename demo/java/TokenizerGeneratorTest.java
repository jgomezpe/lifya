

import java.io.IOException;

import lifya.Source;
import lifya.Token;
import lifya.parsergenerator.language.GeneratorLexeme;
import lifya.parsergenerator.language.GeneratorParser;
import lifya.parsergenerator.language.GeneratorTokenizer;
import speco.array.Array;

/**
 * Tokenizer and token type recognizer test
 */
public class TokenizerGeneratorTest {
	public static void tokenizer() {
		String code="\\d+ (\\.\\d+)? ([e-E|r] (\\+|\\-)? \\d+)?";
		System.out.println(code);
		GeneratorTokenizer tokenizer = new GeneratorTokenizer(false);
		try {
			System.out.println("***********");
			System.out.println(code);
			Array<Token> tokens = tokenizer.get(new Source("noname",code));
			System.out.println(tokens.size());
			for( Token t:tokens ) System.out.println(t);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void parser() {
		String code="[\\+|\\-]?\\d+ (\\.\\d+)? ({e|er|E}-[\\+|\\-|\\*]?\\d+)?";
		//String code="\"([^\\\\]|\\\\([\\\\nrt\"]|u[A-Fa-f\\d][A-Fa-f\\d][A-Fa-f\\d][A-Fa-f\\d]))*\"";
		System.out.println(code);
		GeneratorParser parser = new GeneratorParser(false);
		try {
			System.out.println("************************************");
			Token t = parser.get(new Source("noname",code));
			System.out.println(t);
		} catch (IOException e) { e.printStackTrace(); }
	}

	public static void meaner() {
		String code="[\\+|\\-]?\\d+(\\.\\d+)? ({e|E}[\\+|\\-]?\\d+)?";
		//String code="\"([^\\\\]|\\\\([\\\\nrt\"]|u[A-Fa-f\\d][A-Fa-f\\d][A-Fa-f\\d][A-Fa-f\\d]))*\"";
		//String code = "%$";
		System.out.println(code);
		try {
			GeneratorLexeme lexeme = new GeneratorLexeme("test",code);
			System.out.println("************************************");
			System.out.println(lexeme.startsWith('"'));
			System.out.println(lexeme.startsWith('5'));
			System.out.println(lexeme.startsWith('+'));
			System.out.println(lexeme.startsWith('-'));
			System.out.println("************************************");
			//System.out.println(lexeme.match(new Source("anonymous", "%hello\n489.34e-2-34e-12rrr")));
			System.out.println(lexeme.match(new Source("anonymous", "489.34e-2-34e-12rrr")));
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void main(String[] args) {
		tokenizer(); // Uncomment to test tokenizer
		parser(); // Uncomment to test parser
		meaner(); // Uncommento to test meaner
	}	
}
