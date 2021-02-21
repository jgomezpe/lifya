package lifya;

import speco.array.Array;

public class LexerWrap extends Lexer{
    protected Array<Token> tokens;
    protected int start;
    protected int end;
    
    public LexerWrap(Array<Token> tokens) {
	this(tokens,0,tokens.size());
    }
    
    public LexerWrap(Array<Token> tokens, int start) {
	this(tokens,start,tokens.size());
    }
    
    public LexerWrap(Array<Token> tokens, int start, int end) {
	this.tokens = tokens;
	this.start = start;
	this.end = end;
    }
     
    @Override
    protected Token get() {
	if(start>=end) return null;
	current = tokens.get(start++);
	return current;
    }

}