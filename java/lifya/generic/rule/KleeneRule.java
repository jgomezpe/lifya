package lifya.generic.rule;

import lifya.TokenSource;
import lifya.Source;
import lifya.Token;
import speco.array.Array;

/**
 * <p>Kleene closure (lists) Parsing rule</p>
 *
 */
public class KleeneRule extends Rule{
	protected String item_type;
	protected String item_value = null;
	protected boolean star; 
	
	/**
	 * <p>Creates a syntactic rule for Kleene closure of rules (lists). Consider a typical definition list rule:</p>
	 * <p> &lt;DEFLIST&gt;+ </p>
	 * <p>Can be defined using a constructor call like this :</p>
	 * <p><i>new KleenRule(parser, "DEFLIST", "DEF", false)</i></p>
	 * @param type Type of the rule
	 * @param rule Rule being closured
	 * @param star A <i>true</i> value indicates an empty closure, <i>false</i> a non-empty closure. 
	 */
	public KleeneRule(String type, boolean star, String rule) {
		super(type);
		this.item_type = rule;
		this.star = star;
	}

	/**
	 * <p>Creates a syntactic rule for Kleene closure of rules (lists). Consider a typical definition list rule:</p>
	 * <p> &lt;DEFLIST&gt;+ </p>
	 * <p>Can be defined using a constructor call like this :</p>
	 * <p><i>new KleenRule(parser, "DEFLIST", "DEF", false)</i></p>
	 * @param type Type of the rule
	 * @param item_type Item being closured
	 * @param item_value Item value
	 * @param star A <i>true</i> value indicates an empty closure, <i>false</i> a non-empty closure. 
	 */
	public KleeneRule(String type, boolean star, String item_type, String item_value) {
		super(type);
		this.item_type = item_type;
		this.item_value = item_value;
		this.star = star;
	}

	/**
	 * Determines if the rule can start with the given token
	 * @param t Token to analyze
	 * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
	 */
	public boolean startsWith(Token t) {
		return star || 
				(parser.isRule(item_type)?parser.rule(item_type).startsWith(t):
				check_lexeme(t, item_type, item_value)); 
	}
	
	/**
	 * Creates a rule token using the <i>current</i> token as first token to analyze
	 * @param lexer Token source
	 * @return Rule token
	 */
	@Override
	public Token analyze(TokenSource lexer) {
		Array<Token> list = new Array<Token>();
		Source input = lexer.input();
		int pos = lexer.pos();
		Token current = lexer.current();
		if(current==null) 
			if(star) return token(input, input.length(), input.length(), list);
			else return eof(lexer);
		if(!startsWith(current)) return error(lexer,current.toError(),pos);
		Rule r = parser.rule(item_type);
		Token t = r!=null?r.analyze(lexer):current;
		if(t.isError())
			if(star) {
				lexer.locate(pos);
				return token(input, current.start(), current.end(), list);
			}else return error(lexer,t,pos);
		if(r==null) lexer.next();
		do{
			list.add(t);
			pos = lexer.pos();
			if(r!=null) t = r.analyze(lexer);
			else {
				t = lexer.current();
				if(check_lexeme(t, item_type, item_value)) lexer.next();
				else t = t.toError();
			}
		}while(t!=null && !t.isError());	
		if(t!=null) lexer.locate(pos);
		return token(input,list.get(0).start(),list.get(list.size()-1).end(),list);		
	}
	
	protected String print( int tab ) {
		StringBuilder sb = new StringBuilder();
		for( int k=0; k<tab; k++ ) sb.append(' ');
		sb.append((star?'*':'+')+" "+type()+ " "+ item_type);
		if(item_value!=null) sb.append(":"+item_value);
		return sb.toString();
	}
}