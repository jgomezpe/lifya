package lifya.lookahead;

import lifya.Lexer;
import lifya.Token;

public class Empty extends Rule{

    public Empty(String type, LAHParser parser) {
	super(type, parser);
    }

    @Override
    public boolean startsWith(Token t) { return true; }

    @Override
    public Token analize(Lexer lexer, Token current) {
	return new Token(type(), current.input(), current.start(), current.end(), null);
    }

}
