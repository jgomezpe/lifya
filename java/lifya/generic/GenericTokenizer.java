/**
 * <p>Copyright: Copyright (c) 2019</p>
 *
 * <h3>License</h3>
 *
 * Copyright (c) 2019 by Jonatan Gomez-Perdomo. <br>
 * All rights reserved. <br>
 *
 * <p>Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <ul>
 * <li> Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <li> Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <li> Neither the name of the copyright owners, their employers, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * </ul>
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 *
 *
 * @author <A HREF="http://disi.unal.edu.co/profesores/jgomezpe"> Jonatan Gomez-Perdomo </A>
 * (E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A> )
 * @version 1.0
 */
package lifya.generic;

import java.util.HashMap;

import lifya.Tokenizer;
import lifya.generic.lexeme.Lexeme;
import lifya.Source;
import lifya.Token;
import speco.array.Array;

/**
 * <p>Generic tokenizer. Matches each possible token type (lexemes) against input, and 
 * returns the first one (in the order provided by the array) that matches and consumes more input characters
 * (eager strategy) </p>
 *
 */
public class GenericTokenizer extends Tokenizer{
	protected HashMap<String, Lexeme> lexeme = new HashMap<String, Lexeme>();
	protected HashMap<String, Integer> priority = new HashMap<String, Integer>();
      
	/**
	 * Creates a tokenizer from a set of lexemes (token type recognizers)
	 * @param lexemes Set of lexemes  that can recognize the Tokenizer
	 */
	public GenericTokenizer( Lexeme[] lexemes ){
		super();
		for( int i=0; i<lexemes.length; i++ ) {
			this.lexeme.put(lexemes[i].type(), lexemes[i]);
			priority.put(lexemes[i].type(), i);
		}
	}
 
	/**
	 * Gets the last read/available Token 
	 * @return Last read/available Token
	 */
	@Override
	protected Token analyze(Source input) {
		int start = input.pos();
		char c = input.current();
		Array<Token> opt = new Array<Token>();
		Array<Token> error = new Array<Token>();
		for( Lexeme l:lexeme.values() ) {
			if(l.startsWith(c)) {
				Token t = l.match(input);
				if(t.isError()) error.add(t);
				else  opt.add(t);
				input.locate(start);
			}
		}
		Token current=null;
		if( opt.size() > 0 ) {
			current = opt.get(0);
			for( int i=1; i<opt.size(); i++ ) {
				Token e2 = opt.get(i);
				if( e2.length()>current.length() ||
					(e2.length()==current.length() && priority.get(e2.type())<priority.get(current.type())) )
					current = e2;
			}
			
		}else {
			if(error.size()>0) {
				current = error.get(0);
				for( int i=1; i<error.size(); i++ ) {
					Token e2 = error.get(i);
					if(e2.length()>current.length()) current = e2;
				}
			}else { current = new Token(input,start, start+1); }
		}
		input.locate(start+current.length());
		return current;
	}
	
	/**
	 * Determines if a type name is a token type or not
	 * @param type type name to analyze
	 * @return <i>true</i> If the type name represents a token type, <i>false</i> otherwise.
	 */
	public boolean isTokenType(String type) { return lexeme.containsKey(type); }
	
	/**
	 * Return the set of types of tokens
	 * @return Types of tokens
	 */
	public Array<String> tokenTypes(){
		Array<String> lex = new Array<String>();
		for( String k:lexeme.keySet() ) 
			if( !removable(k) ) lex.add(k);
		return lex;
	}
}