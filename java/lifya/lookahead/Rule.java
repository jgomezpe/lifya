package lifya.lookahead;

import lifya.Lexer;
import lifya.Source;
import lifya.Token;
import lifya.lexeme.Symbol;

public abstract class Rule{	
    protected LAHParser parser;
    protected String type;
    
    public Rule(String type, LAHParser parser) { 
	this.parser = parser; 
	this.type = type;
    }
    
    public abstract boolean startsWith( Token t );

    public String type() { return type; }

    public boolean check_symbol(Token token, char c) {
	return check_symbol(token,c,Symbol.TAG);
    }

    public boolean check_symbol(Token token, char c, String TAG) {
	return token.type()==TAG && ((char)token.value()) == c;
    }
    
    public Token analize(Lexer lexer) {
	return analize(lexer, lexer.next());
    }
    
    public abstract Token analize(Lexer lexer, Token current);
    
    public Token eof(Source input, int end) {
	return new Token(input,end,end,type());
    }
        
    public Token token(Source input, int start, int end, Object value) {
	return new Token(type(), input, start, end, value);
    }
}