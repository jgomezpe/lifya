package test;

import lifya.Token;
import lifya.lexeme.IntegerParser;

public class IntegerTest {
	public static void main(String[] args) {
	    IntegerParser p = new IntegerParser();
	    System.out.println("==============");
	    Token t = p.match("123,ww");
	    System.out.println(t);
	    System.out.println("==============");
	    t = p.match("123.3e-2");
	    System.out.println(t);
	    System.out.println("==============");
	    t = p.match("123+4");
	    System.out.println(t);
	    System.out.println("==============");
	    t = p.match("xx,456,ww",3);
	    System.out.println(t);
	}
}
