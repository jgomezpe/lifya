package test;

import lifya.Token;
import lifya.lexeme.StringParser;
import lifya.stringify.Stringifier;

public class StringTest {
	public static void main(String[] args) {
	    System.out.println("==============");
	    String x= Stringifier.apply("Hello world! \u44ff"); 
	    System.out.println(x);
	    StringParser p = new StringParser();
	    System.out.println("==============");
	    Token t = p.match(x);
	    System.out.println(t);
	    System.out.println("==============");
	    t = p.match("\"\\n hello'");
	    System.out.println(t);
	}
}
