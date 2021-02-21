package test;

import lifya.Token;
import lifya.lexeme.RealParser;

public class RealTest {
	public static void main(String[] args) {
	    RealParser p = new RealParser();
	    System.out.println("==============");
	    Token t = p.match("123,ww");
	    System.out.println(t);
	    System.out.println("==============");
	    t = p.match("123.3e-2");
	    System.out.println(t);
	    System.out.println("==============");
	    t = p.match("123..3e-2");
	    System.out.println(t);
	    System.out.println("==============");
	    t = p.match("xx,456,ww",3);
	    System.out.println(t);
	}
}
