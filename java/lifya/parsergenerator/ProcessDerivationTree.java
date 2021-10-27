package lifya.parsergenerator;

import lifya.Token;
import lifya.generic.rule.EmptyRule;
import speco.array.Array;

/**
 * Derivation tree utility process functions
 */
public class ProcessDerivationTree {
	/**
	 * Eliminates lambda tokens
	 * @param t Derivation tree to process
	 * @return Derivation tree without lambda tokens
	 */
	public static Token eliminate_lambda( Token t ) {
		if( t.value() instanceof Array ) {
			@SuppressWarnings("unchecked")
			Array<Token> a = (Array<Token>)t.value();
			for(int i=a.size()-1; i>=0; i-- ) {
				if(a.get(i).type()==EmptyRule.TAG) a.remove(i);
				else a.set(i, eliminate_lambda(a.get(i)));
			}	
		}
		return t;
	}

	/**
	 * Eliminates specific type of tokens
	 * @param t Derivation tree to process
	 * @param type Type of tokens to eliminate
	 * @param value Specific value for the token
	 * @return Derivation tree without specific type of tokens
	 */
	public static Token eliminate_token( Token t, String type, String value ) {
		if( t.value() instanceof Array ) {
			@SuppressWarnings("unchecked")
			Array<Token> a = (Array<Token>)t.value();
			for(int i=a.size()-1; i>=0; i-- ) {
				if(a.get(i).type()==EmptyRule.TAG || 
						a.get(i).type().equals(type) &&
					(value==null || t.value().equals(value))) a.remove(i);
				else a.set(i, eliminate_token(a.get(i),type,value));
			}	
		}
		return t;
	}

	/**
	 * Reduces branches of the tree with a single branch (moves unique child as parent)
	 * @param t Derivation tree to process
	 * @return Derivation tree without unique branches
	 */
	public static Token reduce_size_1( Token t ) {
		if( t.value() instanceof Array ) {
			@SuppressWarnings("unchecked")
			Array<Token> a = (Array<Token>)t.value();
			if(a.size()==1) t = reduce_size_1(a.get(0));
			else for(int i=a.size()-1; i>=0; i-- ) a.set(i, reduce_size_1(a.get(i)));
		}else if( t.value() instanceof Token ) 
			t.value(reduce_size_1((Token)t.value()));
		return t;
	}

	/**
	 * Reduces expression with unique operator (produces list instead of single joined with binary)
	 * @param t Derivation tree to process
	 * @param exp_type Type of the expression
	 * @return Derivation tree with specific expressions reduced (forming lists)
	 */
	public static Token reduce_exp( Token t, String exp_type ) {
		if( t.value() instanceof Array ) {
			@SuppressWarnings("unchecked")
			Array<Token> a = (Array<Token>)t.value();
			for(int i=0; i<a.size(); i++) a.set(i, reduce_exp(a.get(i), exp_type));
			if(t.type().equals(exp_type)) {
				if(a.size()>0) {
					if(a.get(0).type().equals(exp_type)) {
						@SuppressWarnings("unchecked")
						Array<Token> x = (Array<Token>)a.get(0).value();
						x.add(a.get(1));
						t = a.get(0);
					}else if(a.get(1).type().equals(exp_type)) {
						@SuppressWarnings("unchecked")
						Array<Token> x = (Array<Token>)a.get(1).value();
						x.add(0,a.get(0));
						t = a.get(1);
					} 
				}
			}
		}
		return t;
	}	
	
	/**
	 * Replaces tokens with a given type with another one type
	 * @param t Derivation tree to process
	 * @param old_type Type to replace
	 * @param new_type New type for such tokens
	 * @return Derivation tree with the tokens been replaced
	 */
	public static Token replace( Token t, String old_type, String new_type ) {
		if(t.type().equals(old_type)) t.type(new_type);
		if( t.value() instanceof Array ) {
			@SuppressWarnings("unchecked")
			Array<Token> a = (Array<Token>)t.value();
			for(int i=0; i<a.size(); i++) a.set(i, replace(a.get(i), old_type, new_type));
		} 
		return t;
	}
}