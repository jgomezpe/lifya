package lifya.lookahead;

import lifya.Lexer;
import lifya.Token;

public class Options extends Rule{
    protected String[] option;
    protected String type;
    
    public Options(String type, LAHParser parser, String[] options) {
	super(type, parser);
	this.option = options;
    }

    @Override
    public boolean startsWith(Token t) {
	int i=0;
	while(i<option.length && !parser.rule(option[i]).startsWith(t)) i++;
	return i<option.length;
    }

    @Override
    public Token analize(Lexer lexer, Token current){
	for(String r:option) {
	    Rule rule = parser.rule(r);
	    if(rule.startsWith(current))
		return rule.analize(lexer, current);
	}    
	return current.toError();
    }
}