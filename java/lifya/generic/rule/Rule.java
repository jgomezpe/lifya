package lifya.generic.rule;

import lifya.TokenSource;
import lifya.generic.GenericParser;
import lifya.Source;
import lifya.Token;

/**
 * <p>Parsing rule</p>
 *
 */
public abstract class Rule{
	protected GenericParser parser;
	protected String type;
    
	/**
	 * <p>Creates a syntactic rule for a parser. 
	 * @param type Type of the rule
	 */
	public Rule(String type) { this.type = type; }
	
	/**
	 * Sets the parser of the rule
	 * @param parser Rule's parser
	 */
	public void parser(GenericParser parser) { this.parser = parser; }
	
	protected Token check(String type, String value, TokenSource lexer) {
		if(parser.isRule(type)) return parser.rule(type).analyze(lexer);
		Token current = lexer.current();
		if(current==null) return eof(lexer);
		if(check_lexeme(current, type, value)) lexer.next();
		else current = current.toError();
		return current;
	}
	
	protected boolean check_lexeme( Token t, String type, String value ) {
		if(t==null || (value!=null && !value.equals(t.value()))) return false;
		if( t.type().equals(type) ) return true;
		String[] embeded_types = t.type().split("\\|");
		for( String x:embeded_types ) if(x.equals(type)) return true; 
		return false;
	}

	/**
	 * Determines if the rule can start with the given token
	 * @param t Token to analyze
	 * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
	 */
	public abstract boolean startsWith(Token t) ;

	/**
	 * Gets the type of the rule
	 * @return Type of the rule
	 */
	public String type() { return type; }

	/**
	 * Creates a rule token 
	 * @param lexer Token source 
	 * @return Rule token
	 */
	public abstract Token analyze(TokenSource lexer);
	
	protected Token error(TokenSource lexer, Token t, int pos){
		lexer.locate(pos);
		return t;
	}
	
	/**
	 * Creates a eof token 
	 * @param lexer Token source 
	 * @return EOF token
	 */
	public Token eof(TokenSource lexer) { 
		Source input = lexer.input();
		return new Token(input,input.length(),input.length()); 
	}
        
	/**
	 * Creates a token with the rule type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @param value Value stored by the token
	 * @return Rule token
	 */
	public Token token(Source input, int start, int end, Object value) {
		return new Token(input, start, end, type(), value);
	}
	
	protected String print( int tab ) {
		StringBuilder sb = new StringBuilder();
		for( int k=0; k<tab; k++ ) sb.append(' ');
		sb.append(type());
		return sb.toString();
	}
	
	public String toString() { return print(0); }
}