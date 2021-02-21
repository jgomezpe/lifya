package test;

import lifya.Token;
import lifya.lexeme.BlobParser;
import lifya.stringify.Stringifier;

public class BlobTest {
    public static void main(String[] args) {
	String str = "Hello World! 2020";
	byte[] blob = str.getBytes();
	System.out.println("======With # starter========");
	String encode = Stringifier.apply(blob);
	System.out.println(encode);
	BlobParser p = new BlobParser(true);
	Token decode = p.match(encode);
	System.out.println(decode+"..."+new String((byte[])decode.value()));
	System.out.println("======Without # starter========");
	encode = Stringifier.apply(blob,"");
	System.out.println(encode);
	p = new BlobParser();
	decode = p.match(encode);
	System.out.println(decode+"..."+new String((byte[])decode.value()));
    }
}
