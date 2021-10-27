package lifya.parsergenerator.language;

import java.util.HashMap;

import lifya.Source;
import lifya.Token;
import lifya.generic.GenericParser;
import lifya.generic.rule.DisjointRule;
import lifya.generic.rule.EmptyRule;
import lifya.generic.rule.ExpressionRule;
import lifya.generic.rule.JoinRule;
import lifya.generic.rule.KleeneRule;
import lifya.generic.rule.Rule;
import lifya.generic.rule.RuleInQuotes;
import lifya.parsergenerator.GeneratorConstants;
import lifya.parsergenerator.ProcessDerivationTree;
import lifya.parsergenerator.lexeme.Matcher;
import speco.array.Array;

/**
 * Parser of the Lifya language for generating parsers
 */
public class GeneratorParser extends GenericParser{
	
	// Parsing Lexeme expressions
			
	protected static Rule quoted() { 
		return new RuleInQuotes(GeneratorConstants.QUOTED, GeneratorConstants.EXP, GeneratorConstants.SYMBOL, "(", ")"); 
	}
	
	protected static Rule word() { 
		return new KleeneRule(GeneratorConstants.WORD, false, GeneratorConstants.ANY); 
	}
	
	protected static Rule wordlist() { 
		HashMap<String, Integer> p = new HashMap<String, Integer>();
		p.put("|", 1);
		return new ExpressionRule(GeneratorConstants.WORDLIST, 
				new String[] {GeneratorConstants.WORD, GeneratorConstants.WORD},
				new String[] {null, null}, GeneratorConstants.PIPE,p);
	}
	
	protected static Rule wordset() { 
		return new RuleInQuotes(GeneratorConstants.WORDSET, GeneratorConstants.WORDLIST, GeneratorConstants.SYMBOL, "{", "}"); 
	}
		
	protected static Rule single() {
		return new DisjointRule(GeneratorConstants.SINGLE, 
				new String[] {GeneratorConstants.QUOTED, GeneratorConstants.SET, GeneratorConstants.DOLLAR,
						GeneratorConstants.ANY, GeneratorConstants.CATEGORY, GeneratorConstants.WORDSET});
	}

	protected static Rule closed() {
		return new DisjointRule(GeneratorConstants.CLOSED, 
				new String[]{GeneratorConstants.CLOSURE, EmptyRule.TAG});
	} 
	
	protected static Rule term() {
		return new JoinRule(GeneratorConstants.TERM, 
				new String[]{GeneratorConstants.SINGLE, GeneratorConstants.CLOSED});
	} 
	
	protected static Rule join() { 
		return new KleeneRule(GeneratorConstants.JOIN, false, GeneratorConstants.TERM); 
	}
	
	protected static Rule exp() {
		HashMap<String, Integer> p = new HashMap<String, Integer>();
		p.put("|", 1);
		return new ExpressionRule(GeneratorConstants.EXP, 
				new String[] {GeneratorConstants.JOIN, GeneratorConstants.JOIN},
				new String[] {null, null}, GeneratorConstants.PIPE,p);
	}

	protected static Rule[] rules() {
		return new Rule[] { new EmptyRule(),
				word(), wordlist(), wordset(), quoted(), single(), closed(), term(), join(), exp()};
	} 
	
	// Parsing language rules
	protected static Rule lexemedef() {
		return new JoinRule(GeneratorConstants.LEXEME, 
				new String[] {GeneratorConstants.ID, GeneratorConstants.SYMBOL, GeneratorConstants.EXP},
				new String[] {null, "=", null});
	}
	
	protected static Rule lexerdef() {
		return new KleeneRule(GeneratorConstants.LEXER, false, GeneratorConstants.LEXEME);
	}
	
	protected static Rule wrap() {
		return new RuleInQuotes(GeneratorConstants.WRAP, GeneratorConstants.RULEEXP, GeneratorConstants.SYMBOL, "(", ")");
	}
	
	protected static Rule options() {
		return new DisjointRule(GeneratorConstants.OPTIONS, 
				new String[] {GeneratorConstants.ID, GeneratorConstants.ANY, GeneratorConstants.WRAP});
	}
	
	protected static Rule itemelement() {
		return new JoinRule(GeneratorConstants.ITEMELEMENT, new String[]{GeneratorConstants.OPTIONS,GeneratorConstants.CLOSED});
	}

	protected static Rule item() {
		return new KleeneRule(GeneratorConstants.ITEM, false, GeneratorConstants.ITEMELEMENT);
	}

	protected static Rule ruleexp() {
		HashMap<String, Integer> p = new HashMap<String, Integer>();
		p.put("|", 1);
		return new ExpressionRule(GeneratorConstants.RULEEXP, 
				new String[] {GeneratorConstants.ITEM,  GeneratorConstants.ITEM},
				new String[] {null, null}, GeneratorConstants.PIPE,p);
	}

	protected static Rule metaopers() {
		return new KleeneRule(GeneratorConstants.METAOPERS, false, GeneratorConstants.WORDSET);
	}

	protected static Rule metaexp() {
		return new JoinRule(GeneratorConstants.METAEXP, new String[]{GeneratorConstants.METAOPERS,GeneratorConstants.ID,GeneratorConstants.ID});
	}
	
	protected static Rule rhs() {
		return new DisjointRule(GeneratorConstants.RULERHS, 
				new String[] {GeneratorConstants.RULEEXP, GeneratorConstants.METAEXP});
	}	
	
	protected static Rule rhsdot() {
		return new JoinRule(GeneratorConstants.RULERHSDOT, 
				new String[] {GeneratorConstants.RULERHS, GeneratorConstants.CATEGORY}, 
				new String[] {null,"."});
	}	
	
	protected static Rule ruledef() {
		return new JoinRule(GeneratorConstants.RULE, 
				new String[] {GeneratorConstants.ID, GeneratorConstants.SYMBOL, GeneratorConstants.SYMBOL, GeneratorConstants.RULERHSDOT},
				new String[] {null, ":", "-", null});
	}
	
	protected static Rule parserdef() {
		return new KleeneRule(GeneratorConstants.PARSER, false, GeneratorConstants.RULE);
	}

	protected static Rule language() {
		return new JoinRule(GeneratorConstants.LANG, new String[] {GeneratorConstants.LEXER, GeneratorConstants.PARSER});
	}
	
	// Set of rules
	protected static Rule[] full_rules() {
		return new Rule[] { new EmptyRule(),
				word(), wordlist(), wordset(), quoted(), single(), closed(), term(), join(), exp(), 
				lexemedef(), lexerdef(), wrap(), options(), itemelement(), item(), ruleexp(), 
				metaopers(), metaexp(), rhs(), rhsdot(), ruledef(), parserdef(), language()};
	} 		

	protected Token expand_set( Source input, int pos, String set ) {
		int init = pos;
		pos++;
		boolean not = (set.charAt(0)=='-');
		if(not) {
			set = set.substring(2,set.length()-1);
			pos++;
		}else set = set.substring(1,set.length()-1);
		String[] items = set.split("\\|");
		Array<Token> a = new Array<Token>();
		for( int i=0; i<items.length; i++) {
			Source s = new Source("inner",items[i]);
			if( matcher.match_range(s) ) {
				s.locate(0);
				matcher.match_one(s);
				String start = s.substring(0, s.pos());
				s.next();
				int p = s.pos();
				matcher.match_one(s);
				String end = s.substring(p, s.length());
				Array<Token> limits = new Array<Token>();
				limits.add(new Token(input, pos, start.length(), GeneratorConstants.ANY, start));
				limits.add(new Token(input, pos+start.length()+1, 
						pos+items[i].length(), GeneratorConstants.ANY, end));
				a.add( new Token(input, pos, items[i].length(), GeneratorConstants.RANGE, limits) );
			}else if(matcher.match_category(s)){
				a.add(new Token(input, pos, items[i].length(), GeneratorConstants.CATEGORY, items[i]));
			}else a.add(new Token(input, pos, items[i].length(), GeneratorConstants.ANY, items[i]));
			pos += items[i].length();
			pos++;
		}
		Token t = new Token(input, init, pos, GeneratorConstants.EXP, a);
		if(not) t = new Token(input, init , pos, GeneratorConstants.NOT, t);
		return t;
	}

	@SuppressWarnings({  "unchecked" })
	protected Token expand_set( Token t ) {
		if( t.value() instanceof Array ) {
			Array<Token> a = (Array<Token>)t.value();
			for(int i=0; i<a.size(); i++) a.set(i, expand_set(a.get(i)));
		}else if( t.type().equals(GeneratorConstants.SET) ) t = expand_set(t.input(), t.start(), (String)t.value());
		return t;
	}
	
	@SuppressWarnings({  "unchecked" })
	protected Token expand_dollar( Token t ) {
		if( t.value() instanceof Array ) {
			Array<Token> a = (Array<Token>)t.value();
			for(int i=0; i<a.size(); i++) a.set(i, expand_dollar(a.get(i)));
		}else if( t.value().equals("$") ) {
			Array<Token> c = new Array<Token>();
			c.add(new Token(t.input(), t.start(), t.end(), GeneratorConstants.SET, "-[\\n]"));
			c.add(new Token(t.input(), t.start(), t.end(), GeneratorConstants.CLOSURE, "*"));

			Array<Token> d = new Array<Token>();
			d.add(new Token(t.input(), t.start(), t.end(), GeneratorConstants.ANY, "\\n"));
			d.add(new Token(t.input(), t.start(), t.end(), GeneratorConstants.CLOSURE, "?"));

			Array<Token> b = new Array<Token>();
			b.add(new Token(t.input(), t.start(), t.end(), GeneratorConstants.TERM, c));
			b.add(new Token(t.input(), t.start(), t.end(), GeneratorConstants.TERM, d));
			t = new Token(t.input(), t.start(), t.end(), GeneratorConstants.JOIN, b);
		}
		return t;
	}
	
	@SuppressWarnings({ "unchecked" })
	protected Token wordset(Array<Token> a) {
		for( int i=0; i<a.size()-1; i++) {
			Array<Token> list = (Array<Token>)a.get(i).value();
			Token first = list.get(0);
			String start = (String)first.value();
			Array<Token> s = new Array<Token>();
			s.add(a.get(i));
			int j=i+1; 
			while(j<a.size()) {
				Array<Token> list_j = (Array<Token>)a.get(j).value();
				String start_j = (String)list_j.get(0).value();
				if(start.equals(start_j)) {
					s.add(a.get(j));
					a.remove(j);
				}else j++;
			}
			boolean empty=false;
			int k=0; 
			while(k<s.size()) {
				list = (Array<Token>)s.get(k).value();
				list.remove(0);
				if(list.size()==0) {
					s.remove(k);
					empty=true;
				}else k++;
			}
			if(s.size()>0) {
				Token next = wordset(s);
				if(empty) {
					Array<Token> b = new Array<Token>();
					b.add(next);
					b.add(new Token(next.input(), next.start(), next.end(), GeneratorConstants.CLOSURE, "?"));
					next = new Token(next.input(), next.start(), next.end(), GeneratorConstants.TERM, b);
				}
				Array<Token> c = new Array<Token>();
				c.add(first);
				c.add(next);
				a.set(i, new Token(first.input(), first.start(), next.end(), GeneratorConstants.JOIN, c));
				
			}else a.set(i, first);
		}
		for( int i=0; i<a.size(); i++) a.set(i, ProcessDerivationTree.reduce_size_1(a.get(i)));
		if(a.size()>1) 
			return new Token(a.get(0).input(), a.get(0).start(), a.get(a.size()-1).end(), GeneratorConstants.EXP, a);
		return a.get(0);
	}
	
	@SuppressWarnings({  "unchecked" })
	protected Token process_word_set(Token t) {
		if( t.value() instanceof Array ) {
			Array<Token> a = (Array<Token>)t.value();
			for( int i=0; i<a.size(); i++ ) a.set(i, process_word_set(a.get(i)));
		}

		if( t.type().equals(GeneratorConstants.WORD) ) t.type(GeneratorConstants.JOIN);
		else if( t.type().equals(GeneratorConstants.WORDLIST) ){
			Array<Token> a = (Array<Token>)t.value();
			for( int i=0; i<a.size(); i++) {
				Token x = a.get(i);
				if(!(x.value() instanceof Array)) {
					Array<Token> l = new Array<Token>();
					l.add(x);
					a.set(i, new Token(x.input(), x.start(), x.end(), GeneratorConstants.WORD, l ));
				}
			}
			for( int i=0; i<a.size()-1; i++)
				for( int j=i+1; j<a.size(); j++) {
					Array<Token> x = (Array<Token>)a.get(i).value();
					Array<Token> y = (Array<Token>)a.get(j).value();
					if(x.size()<y.size()) {
						Token z = a.get(i);
						a.set(i, a.get(j));
						a.set(j, z);
					}
				}
			t = wordset(a);
		}
		return t;
	}
	
	protected Token reduce( Token t ) {
		t = ProcessDerivationTree.eliminate_lambda(t);
		t = ProcessDerivationTree.eliminate_token(t, GeneratorConstants.PIPE, null);
		t = expand_dollar(t);
		t = expand_set(t);
		t = ProcessDerivationTree.reduce_size_1(t);
		t = ProcessDerivationTree.reduce_exp(t, GeneratorConstants.EXP);
		t = ProcessDerivationTree.reduce_exp(t, GeneratorConstants.WORDLIST);
		t = ProcessDerivationTree.reduce_exp(t, GeneratorConstants.RULEEXP);
		t = process_word_set(t);
		return t;
	}
	
	/**
	 * Reads an object from the input source (limited to the given starting and ending positions) 
	 * @param input Symbol source
	 * @return Object read from the symbol source
	 */
	@Override
	public Token match(Source input){
		Token t = super.match(input);
		return process_word_set(reduce(t));
	}
	
	protected Matcher matcher;
	
	/**
	 * Creates a parser for Lifya token type recognizers or Lifya parser specifications
	 * @param parser A <i>true</i> value indicates a parser for Lyfya parser specification, a <i>false</i> value
	 * indicates a token type recognizer parser
	 */
	public GeneratorParser( boolean parser ) { 
		this(parser, parser); 
	}	
	
	/**
	 * Creates a parser for Lifya token type recognizers or Lifya parser specifications
	 * @param parser A <i>true</i> value indicates a parser for Lyfya parser specification, a <i>false</i> value
	 * indicates a token type recognizer parser
	 * @param embeded If the parser must be processing inside of a Lifya parser or not
	 */
	public GeneratorParser( boolean parser, boolean embeded ) { 
		super(new GeneratorTokenizer(embeded), parser?full_rules():rules(), 
				parser?GeneratorConstants.LANG:GeneratorConstants.EXP); 
		matcher = new Matcher("matcher-inner", embeded);
	}	
}
