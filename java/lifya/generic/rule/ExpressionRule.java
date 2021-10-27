package lifya.generic.rule;

import java.util.HashMap;

import lifya.TokenSource;
import lifya.Token;
import speco.array.Array;

/**
 * A parsing rule for ambiguous expressions (tree is produced according to operators priorities
 */
public class ExpressionRule extends JoinRule{
	protected HashMap<String,Integer> operator_priority;
    protected KleeneRule list=null;
    protected JoinRule item=null;

    /**
     * <p>Creates an expression rule. Consider a typical numeric expression rule:</p>
	 * <p> &lt;EXP&gt; :- &lt;TERM1&gt; (&lt;OPER&gt; &lt;TERM&gt;)* </p>
	 * <p>Can be defined using a constructor call like this (arrays and maps notation simplified):</p>
	 * <p><i>new ExpressionRule("EXP", ["TERM1","TERM"], [null,null], OPER, {"^":1,"*":2,"/":2,"+":3,"-":3})</i></p>
	 * <p>Here a <i>null</i> value indicates that the associated lexeme can take any value.</p>
     * @param type Expression type
     * @param term_type Types of the terms in the expression rule (type of the first term may be different from other terms in the expression)
     * @param term_value Values for terms (if explicit values must be taken)
     * @param operator Operators type
     * @param operator_priority Operators priority
     */
    public ExpressionRule(String type, String[] term_type, String[] term_value, String operator, HashMap<String,Integer>  operator_priority) {
		super(type, term_type, term_value);
		this.item = new JoinRule(type+"item", new String[] {operator, term_type[1]}, new String[] {null, term_value[1]});
		this.list = new KleeneRule(type+"itemlist", true, type+"item");
		this.operator_priority = operator_priority;
	}
    
	/**
	 * Creates a rule token using the <i>current</i> token as first token to analyze
	 * @param lexer Tokens source 
	 * @return Rule token
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public Token analyze(TokenSource lexer) {
		if(!parser.isRule(this.item.type())) {
			parser.add(this.item);
			parser.add(this.list);
		}
		int pos = lexer.pos();
		Token current = lexer.current();
		if(current==null) return eof(lexer);
		if(!startsWith(current)) return error(lexer,current.toError(),pos);
		Token t = check(type_list[0], value_list[0],lexer);
		if(t.isError()) return error(lexer,t,pos);
		Token a = parser.rule(type+"itemlist").analyze(lexer);
		Array<Token> l = (Array<Token>)a.value();
		for( int i=l.size()-1; i>=0; i--) {
			Array<Token> term = (Array<Token>)l.get(i).value();
			l.add(i+1, term.get(1));
			l.set(i, term.get(0));
		}	
		l.add(0, t);
		return tree(l);
	}

	protected Token tree(Array<Token> list) {

		if( list.size()==1 ) return list.get(0);

		int p = operator_priority.get((String)list.get(1).value());
		int k = 1;		
		for( int i=3; i<list.size(); i+=2) { 
			int pi = operator_priority.get((String)list.get(i).value());
			if(pi<p) {
				k = i;
				p = pi;
			}
		}
		Array<Token> call = new Array<Token>();
		call.add(list.get(k));
		call.add(list.get(k-1));
		call.add(list.get(k+1));
		Token t = new Token(call.get(1).input(), call.get(1).start(), call.get(2).end(), type, call);
		list.remove(k+1);
		list.set(k,t);
		list.remove(k-1);
		return tree(list);
	}
	
	protected String print( int tab ) {
		StringBuilder sb = new StringBuilder();
		sb.append(super.print(tab));
		for( int i=0; i<type_list.length; i++) {
			sb.append(' ');
			sb.append(type_list[i]);
			if(value_list[i]!=null) sb.append(':'+value_list[i]);
		}	
		return sb.toString();
	}
}