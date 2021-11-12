

import java.io.IOException;

import lifya.Parser;
import lifya.Source;
import lifya.Token;
import lifya.Tokenizer;
import lifya.parsergenerator.ParserGenerator;
import lifya.parsergenerator.ProcessDerivationTree;
import lifya.parsergenerator.language.GeneratorParser;
import lifya.parsergenerator.language.GeneratorTokenizer;
import lifya.stringify.Stringifier;
import speco.array.Array;

/**
 * Parser generator test
 */
public class ParserGeneratorTest {
	public static String lexer="% A lexer \n<number> = [\\+|\\-]?\\d+(\\.\\d+)?([e|E][\\+|\\-]?\\d+)?\n<%comment> = \\%$\n<%space> = [\\n|\\r|\\t|\\s]+\n";
	public static String parser="% A parser \n<list> :- {\\:} <number> <number>.\n";
	public static String code=lexer+"\n"+parser;
	
	public static void tokenizer() {
		GeneratorTokenizer tokenizer = new GeneratorTokenizer(true);
		try {
			System.out.println("*****Lexer******");
			System.out.println(code);
			Source s = new Source("language test source",code);
			Array<Token> tokens = tokenizer.get(s);
			System.out.println("*****Tokenization******");
			System.out.println(tokens.size());
			for( Token t:tokens ) System.out.println(t);
	
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void parser() {
		GeneratorParser parser = new GeneratorParser(true);
		try {
			System.out.println("****************Parser********************");
			System.out.println(code);
			Source input = new Source("language test source",code);
			Token t = parser.get(input);
			System.out.println(t);
			if(parser.current() != null ) throw new IOException(Stringifier.apply(parser.current().toError()));
		} catch (IOException e) { e.printStackTrace(); }
	}

	public static void language() {
		try {
			System.out.println("****************Language********************");
			System.out.println(code);
			Parser parser = ParserGenerator.parser(code, "<list>");
			String program = "123.45 : 345.86e-2 : \n 76.77";
			try {
				System.out.println("*****Testing with****");
				System.out.println(program);
				Source source = new Source("view", program);
				System.out.println("*****Tokenizer****");
				Tokenizer tok = parser.tokenizer();
				Token t = tok.match(source);
				@SuppressWarnings("unchecked")
				Array<Token> a = (Array<Token>)t.value();
				for(int i=0; i<a.size(); i++) {
					System.out.println(a.get(i));
				}
				source = new Source("view", program);
				t = parser.get(source);
				System.out.println("****************Produced tree********************");
				System.out.println(t);
				t = ProcessDerivationTree.eliminate_lambda(t);
				t = ProcessDerivationTree.eliminate_token(t, "symbol", null);
				t = ProcessDerivationTree.reduce_size_1(t);
				t = ProcessDerivationTree.reduce_exp(t, "<list>");
				System.out.println("****************Processed tree********************");				
				System.out.println(t);
			} catch (Exception e) { e.printStackTrace(); }
			
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	
	public static void main(String[] args) {
		tokenizer(); //Uncomment to see the tokenizer 
		parser(); // Uncomment to see the parser tree
		language(); // Uncomment to see the parser generation
	}
}
