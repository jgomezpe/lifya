package lifya.lookahead;

import lifya.Lexer;
import lifya.Source;
import lifya.Token;
import speco.array.Array;

/**
 * <p>A Parsing rule for non empty lists</p>
 *
 */
public class NonEmptyListRule extends Rule{
	/**
	 * Type of the Syntactic Rule for lists
	 */
	public final static String TAG = "NELIST"; 
	
	protected char SEPARATOR = ',';
	protected String item_rule;

	/**
	 * Creates a non empty lists syntactic rule 
	 * @param type Type of the rule
	 * @param parser Syntactic parser using the rule
	 * @param item_rule Rule type of the elements of the list
	 */	
	public NonEmptyListRule(String type, LAHParser parser, String item_rule) {
		super(type, parser); 
		this.item_rule = item_rule;
	}
    
	/**
	 * Creates a lists syntactic rule 
	 * @param type Type of the rule
	 * @param parser Syntactic parser using the rule
	 * @param item_rule Rule type of the elements of the list
	 * @param separator Elements separating character
	 */	
	public NonEmptyListRule(String type, LAHParser parser, String item_rule, char separator) { 
		super(type, parser); 
		this.item_rule = item_rule;
		SEPARATOR = separator;
	}
    
	/**
	 * Determines if the rule can start with the given token (left character)
	 * @param t Token to analyze
	 * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(Token t) { return parser.rule(item_rule).startsWith(t); }
    
	/**
	 * Creates a rule token using the <i>current</i> token as first token to analyze
	 * @param lexer Lexer 
	 * @param current Initial token
	 * @return List rule token
	 */
	@Override
	public Token analyze(Lexer lexer, Token current) {
		if(!startsWith(current)) return current.toError();
		Source input = current.input();
		int start = current.start();
		Array<Token> list = new Array<Token>();
		Token t = parser.rule(item_rule).analyze(lexer, current);
		if(t.isError()) return t;
		list.add(t);
		current = lexer.next();
		while(current!=null && check_symbol(current, SEPARATOR)) {
			current = lexer.next();
			t = parser.rule(item_rule).analyze(lexer, current);
			if(t.isError()) return t;
			list.add(t);
			current = lexer.next();
		}
		if(current != null) lexer.goback();
		return token(input,start,list.get(list.size()-1).end(),list);
	}	
}
