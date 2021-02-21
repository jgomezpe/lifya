package lifya.lexeme;

import lifya.Source;
import lifya.Token;

public class Wrap implements Lexeme<Object>{
    protected Lexeme<?> inner;
    protected String type;
    
    public Wrap(String type, Lexeme<?> inner) {
	this.type = type;
	this.inner = inner;
    }
    
    @Override
    public Token match(Source input, int start, int end) {
	return inner.match(input, start, end);
    }

    @Override
    public boolean startsWith(char c) {
	return inner.startsWith(c);
    }

    @Override
    public String type() {
	return type;
    }
}