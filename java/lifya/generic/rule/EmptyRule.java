package lifya.generic.rule;

import lifya.TokenSource;
import lifya.Token;

/**
 * Empty rule (lambda production)
 */
public class EmptyRule extends Rule{
	/**
	 * Lambda type /TAG of the lambda rule
	 */
	public static final String TAG = "lambda";
	
	/**
	 * Creates the empty rule (lambda production)
	 */
	public EmptyRule() { super(TAG); }

	/**
	 * Determines if the rule can start with the given token
	 * @param t Token to analyze
	 * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(Token t) { return true; }

	/**
	 * Creates a rule token using the <i>current</i> token as first token to analyze
	 * @param lexer Tokens source 
	 * @return Rule token
	 */
	@Override
	public Token analyze(TokenSource lexer) {
		int start;
		if(lexer.current()==null) start = lexer.input().length();			
		else start = lexer.current().start();
		return token(lexer.input(), start, start, null);
	}	
}
