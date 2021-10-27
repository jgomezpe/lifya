package lifya.generic.rule;

import lifya.TokenSource;
import lifya.Token;
import speco.array.Array;

public class JoinRule extends Rule{
	protected String[] type_list;
	protected String[] value_list;
	
	static String[] init(int n) {
		String[] x = new String[n];
		for( int i=0; i<x.length; i++) x[i] = null;
		return x;
	}

	/**
	 * <p>Creates a syntactic join rule for a parser</p>
	 * @param type Type of the rule
	 * @param type_list Types of the rule components
	 */
	public JoinRule(String type, String[] type_list) {
		this(type, type_list, init(type_list.length));
	}
	
	/**
	 * <p>Creates a syntactic join rule for a parser. Consider a typical assignment rule:</p>
	 * <p> &lt;ASSIGN&gt; :- &lt;id&gt; = &lt;EXP&gt; </p>
	 * <p>Can be defined using a constructor call like this (arrays notation simplified):</p>
	 * <p><i>new Rule(parser, "ASSIGN", ["id", "symbol","EXP"], [null,"=",null])</i></p>
	 * <p>Here a <i>null</i> value indicates that the associated lexeme can take any value.</p>
	 * @param type Type of the rule
	 * @param type_list Types of the rule components
	 * @param value_list Values of the rule components. 
	 */
	public JoinRule(String type, String[] type_list, String[] value_list) {
		super(type);
		this.type_list = type_list;
		this.value_list = value_list;
	}

	/**
	 * <p>Creates a syntactic join rule for a parser. Consider a typical assignment rule:</p>
	 * <p> &lt;ASSIGN&gt; :- &lt;id&gt; = &lt;EXP&gt; </p>
	 * <p>Can be defined using a constructor call like this (arrays notation simplified):</p>
	 * <p><i>new Rule(parser, "ASSIGN", ["id", "symbol","EXP"], [null,"=",null])</i></p>
	 * <p>Here a <i>null</i> value indicates that the associated lexeme can take any value.</p>
	 * @param type Type of the rule
	 * @param type_list Types of the rule components
	 * @param value_list Values of the rule components. 
	 */
	public JoinRule(String type, Array<String> type_list, Array<String> value_list) {
		super(type);
		this.type_list = new String[type_list.size()];
		this.value_list = new String[type_list.size()];
		for( int i=0; i<type_list.size(); i++) {
			this.type_list[i] = type_list.get(i);
			this.value_list[i] = value_list.get(i);		
		}
	}

	protected boolean check_lexeme( Token t, int i ) { return check_lexeme( t, type_list[i], value_list[i]); }

	/**
	 * Determines if the rule can start with the given token
	 * @param t Token to analyze
	 * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
	 */
	public boolean startsWith(Token t) {
		if(parser.isRule(type_list[0])) return parser.rule(type_list[0]).startsWith(t);
		else return check_lexeme(t, 0);
	}

	/**
	 * Creates a rule token using the <i>current</i> token as first token to analyze
	 * @param lexer Tokens source 
	 * @return Rule token
	 */
	public Token analyze(TokenSource lexer) {
		int pos = lexer.pos();
		Token current = lexer.current();
		if(current==null) return eof(lexer);
		if(!startsWith(current)) return error(lexer,current.toError(),pos);
		int start = current.start();
		int end = current.end();
		Array<Token> list = new Array<Token>();
		for( int i=0; i<type_list.length; i++) {
			pos = lexer.pos();
			Token t = check(type_list[i], value_list[i], lexer);
			if( t.isError() ) return error(lexer,t,pos);
			if(value_list[i]==null) list.add(t);
			end = t.end();
		}
		return token(lexer.input(),start,end,list);
	}	
	
	protected String print( int tab ) {
		StringBuilder sb = new StringBuilder();
		sb.append(super.print(tab));
		tab++;
		for( int i=0; i<type_list.length; i++) {
			sb.append('\n');
			for( int k=0; k<tab; k++ ) sb.append(' ');
			sb.append(type_list[i]);
			if(value_list[i]!=null) sb.append(":"+value_list[i]);
		}
		return sb.toString();
	}
}