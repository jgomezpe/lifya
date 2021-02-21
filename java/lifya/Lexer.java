package lifya;

import lifya.lexeme.Space;
import speco.array.Array;

public abstract class Lexer implements Read<Array<Token>>{
    public static final String TOKEN_LIST = "Vector<Token>";

    protected String[] removableTokens;
    protected boolean remove=true;
    protected Source input;
    protected int start;
    protected int end;
    
    protected Token current;
    protected boolean back = false;

    public Lexer() { this( new String[] {}); }
    
    public Lexer(String[] removableTokens) { this.removableTokens = removableTokens; }

    public void removeTokens(boolean remove) { this.remove = remove; }
    
    public void init(String input) {
	this.init(input,0,input.length());
    }
    
    public void init(String input, int start) {
	this.init(input,start,input.length());
    }
    
    public void init(String input, int start, int end) {
	init(new Source(input), start, end);
    }
    
    public void init(Source input, int start, int end) {
	this.input = input;
	this.start = start;
	this.end = end;
	this.back = false;
    }
    
    protected abstract Token get();
    
    protected boolean removable(Token t) {
	    int i=0;
	    while(i<removableTokens.length && t.type()!=removableTokens[i]) i++;
	    return(i!=removableTokens.length);	
    }
    
    public Token next() {
	if(back) {
	    back = false;
	    return current;
	}
	do { current = get(); }while(current!=null && remove && removable(current));
	return current;
    }
    
    public void goback() { back = true; }
    
    public Token match(Source input, int start, int end) {
	init(input,start,end);
	Array<Token> list = new Array<Token>();
	Token t;
	while((t=next())!=null && t.type()!=Token.ERROR) { list.add(t); }
	if(t==null)
	    return new Token(TOKEN_LIST, input, start, list.get(list.size()-1).end(), list);
	else 
	    return t;
    }
    
    public static Array<Token> remove(Array<Token> tokens, String toremove ){
	for( int i=tokens.size()-1; i>=0; i-- )	if( toremove.indexOf(tokens.get(i).type()) >= 0 ) tokens.remove(i);
	return tokens;
    }
	
    public static Array<Token> remove_space(Array<Token> tokens ){ 
	return remove(tokens, Space.TAG); 
    }	
}