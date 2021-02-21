package lifya.lookahead;

import lifya.Source;
import lifya.Token;
import speco.array.Array;

public class ListRule extends Rule{
    public final static String TAG = "LIST"; 
    protected char LEFT = '[';
    protected char RIGHT = ']';
    protected char SEPARATOR = ',';
    protected String item_rule;

    public ListRule(String type, LAHParser parser, String item_rule) {
	super(type, parser); 
	this.item_rule = item_rule;
    }
    
    public ListRule(String type, LAHParser parser, String item_rule, char left, char right, char separator) { 
	super(type, parser); 
	this.item_rule = item_rule;
	LEFT = left;
	RIGHT = right;
	SEPARATOR = separator;
    }
    
    @Override
    public boolean startsWith(Token t) { return check_symbol(t, LEFT); }
    
    @Override
    public Token analize(lifya.Lexer lexer, Token current) {
	if(!startsWith(current)) return current.toError();
	Source input = current.input();
	int start = current.start();
	int end = current.end();
	Array<Token> list = new Array<Token>();
	current = lexer.next();
	while(current!=null && !check_symbol(current, RIGHT)){
	    Token t = parser.rule(item_rule).analize(lexer, current);
	    if(t.isError()) return t;
	    list.add(t);
	    end = current.end();
	    current = lexer.next();
	    if(current==null) return eof(input,end);
	    if(check_symbol(current, SEPARATOR)) {
		end = current.end();
		current = lexer.next();
		if(current==null) return eof(input,end);
		if(check_symbol(current, RIGHT)) return current.toError(); 
	    }else if(!check_symbol(current, RIGHT)) return current.toError();
	}
	if(current==null) return eof(input,end);
	return token(input,start,current.end(),list);
    }
}