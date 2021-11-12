package lifya.parsergenerator.language;

import java.io.IOException;
import java.util.HashMap;

import lifya.Language;
import lifya.Parser;
import lifya.Token;
import lifya.Tokenizer;
import lifya.generic.GenericParser;
import lifya.generic.GenericTokenizer;
import lifya.generic.lexeme.Lexeme;
import lifya.generic.rule.DisjointRule;
import lifya.generic.rule.EmptyRule;
import lifya.generic.rule.ExpressionRule;
import lifya.generic.rule.JoinRule;
import lifya.generic.rule.KleeneRule;
import lifya.generic.rule.Rule;
import lifya.parsergenerator.GeneratorConstants;
import lifya.parsergenerator.ParserGenerator;
import speco.array.Array;

/**
 * Language for the lifya parser generator
 */
public class GeneratorLanguage extends Language<GenericParser>{
	/**
	 * Creates the Lifya language for parser generator
	 */
	public GeneratorLanguage() { super(new GeneratorParser(true)); }

	protected GeneratorLexeme lexeme(Token lexeme){
		@SuppressWarnings("unchecked")
		Array<Token> a = (Array<Token>)lexeme.value();
		try{ return new GeneratorLexeme((String)a.get(0).value(), a.get(1)); }
		catch(IOException e) { e.printStackTrace(); }
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected Tokenizer tokenizer(Token lexemes, Lexeme symbols) {
		Array<Token> a;
		Lexeme[] lexeme;
		int start = (symbols!=null)?1:0;
		if(lexemes.value() instanceof Array) {
			a = (Array<Token>)lexemes.value();
			int n = a.size();
			lexeme = new Lexeme[n+start];
			for( int i=0; i<n; i++)
				lexeme[i+start] = lexeme(a.get(i));
		}else {
			lexeme = new Lexeme[start+1];
			lexeme[start] = lexeme(lexemes);
		}
		if(start==1) lexeme[0] = symbols;
		
		int c = 0;
		for( int i=0; i<lexeme.length; i++) if(lexeme[i].type().charAt(1)=='%') c++;

		String[] removable = new String[c];
		c=0;
		for( int i=0; i<lexeme.length; i++)
			if(lexeme[i].type().charAt(1)=='%') {
				removable[c] = lexeme[i].type();
				c++;
			}		
		GenericTokenizer tokenizer = new GenericTokenizer(lexeme);
		tokenizer.removables(removable);
		return tokenizer;
	}
	
	@SuppressWarnings("unchecked")
	protected void ruleoperators(Token t, String op, int p, HashMap<String, Integer> map) {
		Array<Token> a;
		switch(t.type()) {
			case GeneratorConstants.EXP:
				a = (Array<Token>)t.value();
				for( int i=0; i<a.size(); i++ )
					ruleoperators(a.get(i),op,p,map);
			break;
			case GeneratorConstants.JOIN:
				a = (Array<Token>)t.value();
				ruleoperators(a.get(1),op+((String)a.get(0).value()),p,map);
			break;
			case GeneratorConstants.TERM:
				map.put(op, p);
				a = (Array<Token>)t.value();
				ruleoperators(a.get(0),op,p,map);
			break;
			case GeneratorConstants.ANY:
				map.put(op+((String)t.value()), p);
			break;
			default:
				System.out.println("--->"+t.type());	
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Object[] ruleoperators(Token t) {
		Array<Token> a = (Array<Token>)t.value();
		Object x = a.get(0).value();
		t = a.get(1); 
		if(t.type().equals(GeneratorConstants.METAEXP)) {
			HashMap<String,Integer> map = new HashMap<String, Integer>();
			a = (Array<Token>)t.value();
			t = a.get(0);
			String ftype = (String)a.get(1).type();
			String type = (String)a.get(2).type();
			String fvalue = (String)a.get(1).value();
			String value = (String)a.get(2).value();
			if(ftype.equals(GeneratorConstants.ID)) {
				ftype = fvalue;
				fvalue = null;
			}else ftype = GeneratorConstants.CHAR;
			
			if(type.equals(GeneratorConstants.ID)) {
				type = value;
				value = null;
			}else type = GeneratorConstants.CHAR;
			
			if(t.type().equals(GeneratorConstants.METAOPERS)) {
				t.type(GeneratorConstants.EXP);
				a = (Array<Token>)t.value();
				for( int i=0; i<a.size(); i++)
					ruleoperators(a.get(i), "", i, map);
			}else ruleoperators(t, "", 0, map);
			return new Object[] {x, t, map,
					new String[] {ftype,type}, new String[] {fvalue,value}};
		}
		return null;
	}
	
	protected Array<Object[]> operators(Token t) {
		Array<Object[]> l = new Array<Object[]>();
		if(t.type().equals(GeneratorConstants.RULE)) {
			Object[] x = ruleoperators(t);
			if(x!=null) l.add(x);
		}else {
			@SuppressWarnings("unchecked")
			Array<Token> a = (Array<Token>)t.value();
			for( int i=0; i<a.size(); i++ ) {
				Object[] x = ruleoperators(a.get(i));
				if(x!=null) l.add(x);
			}
		}
		return l;
	}

	@SuppressWarnings("unchecked")
	protected void symbols(Token t, HashMap<String,Integer> current_symbols) {
		Array<Token> a;
		switch(t.type()) {
			case GeneratorConstants.ANY:
				current_symbols.put((String)t.value(), 1);
			break;
			case GeneratorConstants.RULE:
				a = (Array<Token>)t.value();
				if(!a.get(1).type().equals(GeneratorConstants.METAEXP))
					symbols(a.get(1),current_symbols);
			break;	
			default:
				if(t.value() instanceof Array) {
					a = (Array<Token>)t.value();
					for(int i=0; i<a.size(); i++)
						symbols(a.get(i),current_symbols);
				}		
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void exprule(Token t, Array<Rule> rules, String id) {
		Array<Token> a;
		switch(t.type()) {
			case GeneratorConstants.RULE:
				a = (Array<Token>)t.value();
				if(!a.get(1).type().equals(GeneratorConstants.METAEXP)) {
					id += (String)a.get(0).value();
					exprule(a.get(1), rules, id);
				}
			break;
			case GeneratorConstants.RULEEXP:
			case GeneratorConstants.ITEM:
				a = (Array<Token>)t.value();
				String[] type = new String[a.size()];
				String[] value = new String[a.size()];
				for( int i=0; i<a.size(); i++) {
					switch(a.get(i).type()) {
						case GeneratorConstants.ID:
							type[i] = (String)a.get(i).value();
							value[i] = null;
						break;
						case GeneratorConstants.ANY:
							type[i] = GeneratorConstants.CHAR;
							value[i] = ParserGenerator.escape_all((String)a.get(i).value());
						break;
						default:
							type[i] = id+GeneratorConstants.ITEM_TAG+i;
							value[i] = null;
							exprule(a.get(i),rules,type[i]);
					}
				}
				if( t.type().equals(GeneratorConstants.RULEEXP) ) rules.add(new DisjointRule(id, type, value));
				else rules.add(new JoinRule(id, type, value));
			break;
			case GeneratorConstants.ITEMELEMENT:
				a = (Array<Token>)t.value();
				String i_type;
				String i_value;
				switch(a.get(0).type()) {
					case GeneratorConstants.ID:
						i_type = (String)a.get(0).value();
						i_value = null;
					break;
					case GeneratorConstants.ANY:
						i_type = GeneratorConstants.CHAR;
						i_value = (String)a.get(0).value();
					break;
					default:
						i_type = id+GeneratorConstants.ITEM_TAG;
						i_value = null;
						exprule(a.get(0),rules,i_type);
				}
				char c = ((String)a.get(1).value()).charAt(0);
				if(c=='+' || c=='*')
					rules.add(new KleeneRule(id, c=='*', i_type, i_value));
				else {
					rules.add(new EmptyRule());
					rules.add(new DisjointRule(id,
							new String[] {i_type,EmptyRule.TAG}, new String[] {i_value,null}));
				}					
			break;
			case GeneratorConstants.PARSER:
				a = (Array<Token>)t.value();
				for( int i=0; i<a.size(); i++ )
					exprule(a.get(i),rules,id);
			break;	
			default:
				String d_type;
				String d_value;
				if(t.type().equals(GeneratorConstants.ID)) {
					d_type = (String)t.value();
					d_value = null;
				}else{
					d_type = GeneratorConstants.CHAR;
					d_value = (String)t.value();				
				}
				rules.add(new JoinRule(id, new String[] {d_type}, new String[] {d_value}));
		}
	}
	
	/**
	 * Creates a parser for the derivation tree obtained by the Lifya generator
	 * @param t Derivation tree
	 * @return Parser for the derivation tree obtained by the Lifya generator
	 */
	public Token mean(Token t) {
		if(t.isError()) return t;
		@SuppressWarnings("unchecked")
		Array<Token> a = (Array<Token>)t.value();
		HashMap<String,Integer> symbols = new HashMap<String,Integer>();
		symbols(a.get(1),symbols);
		Tokenizer tokenizer=null;
		Parser parser = null;
		try {
			Array<Rule> rules = new Array<Rule>();
			Array<Object[]> exp = operators(a.get(1));
			for(int i=0; i<exp.size(); i++) {
				Object[] e = exp.get(i);
				String type = (String)e[0];
				@SuppressWarnings("unchecked")
				HashMap<String, Integer> p = (HashMap<String, Integer>)e[2];
				HashMap<String, Integer> pf = new HashMap<String, Integer>();
				Array<String> opers = new Array<String>();
				Array<String> opers_type = new Array<String>();
				for(String op:p.keySet()) {
					//if(!symbols.containsKey(op)) {
						String key = ParserGenerator.escape_all(op);
						symbols.put(op,p.get(op));
						opers.add(key);
						opers_type.add(GeneratorConstants.CHAR);
						pf.put(key,p.get(op));
					//}
				}
				rules.add(new DisjointRule(type+GeneratorConstants.OPER, opers_type, opers));
				String[] ftype = (String[])e[3];
				String[] fvalue = (String[])e[4];
				rules.add(new ExpressionRule(type, ftype, fvalue, type+GeneratorConstants.OPER, pf));
			}
			Lexeme obj = null;
			if(symbols.size()>0) {
				StringBuilder sb = new StringBuilder();
				char c = '{';
				for(String word:symbols.keySet()) {
					sb.append(c);
					sb.append(word);
					c='|';
				}
				sb.append('}');
				obj = new GeneratorLexeme(GeneratorConstants.CHAR, sb.toString(),true);
			}
			tokenizer = tokenizer(a.get(0), obj);
			
			exprule(a.get(1), rules, "");
			
			parser = new GenericParser(tokenizer,rules,rules.get(0).type());
		} catch (IOException e) { e.printStackTrace(); }
		//Parser par = parser(a.get(1), tok);
		return new Token(t.input(), t.start(), t.end(), t.type(), parser);
	}
}
