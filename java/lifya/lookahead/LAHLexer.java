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
package lifya.lookahead;

import java.util.HashMap;

import lifya.Token;
import lifya.lexeme.Lexeme;
import speco.array.Array;

/**
 * <p>Look a Head lexer. Checks the next character in the input to determine the Lexema to use </p>
 *
 */
public class LAHLexer extends lifya.Lexer{
	protected HashMap<String, Lexeme<?>> lexeme = new HashMap<String, Lexeme<?>>();
	protected HashMap<String, Integer> priority = new HashMap<String, Integer>();
      
	/**
	 * Creates a Lexer that removes (does not take into account) the given Token types
	 * @param lexemes Set of lexema that can recognize the Lexer
	 * @param removableTokens Tokens that will be removed from analysis
	 */
	public LAHLexer( Lexeme<?>[] lexemes, String[] removableTokens ){
		this(lexemes, new int[lexemes.length], removableTokens);
	}

	/**
	 * Creates a Lexer
	 * @param lexemes Set of lexemes that can recognize the Lexer
	 */
	public LAHLexer( Lexeme<?>[] lexemes ){ this(lexemes, new int[lexemes.length]); }

	/**
	 * Creates a Lexer from a set of lexema each one with an associated priority of analysis
	 * @param lexemes Set of lexema that can recognize the Lexer
	 * @param priority Priority of the lexema
	 */
	public LAHLexer( Lexeme<?>[] lexemes, int[] priority){ this(lexemes,priority,new String[] {}); }

	/**
	 * Creates a Lexer from a set of lexema each one with an associated priority of analysis and
	 * removes (does not take into account) the given Token types
	 * @param lexemes Set of lexema that can recognize the Lexer
	 * @param priority Priority of the lexema
	 * @param removableTokens Tokens that will be removed from analysis
	 */
	public LAHLexer( Lexeme<?>[] lexemes, int[] priority, String[] removableTokens ){
		super(removableTokens);
		for( int i=0; i<lexemes.length; i++ ) {
			this.lexeme.put(lexemes[i].type(), lexemes[i]);
			this.priority.put(lexemes[i].type(), priority[i]);
		}
	}
    
	/**
	 * Gets the last read/available Token 
	 * @return Last read/available Token
	 */
	@Override
	protected Token get() {
		if(start>=end) return null;
		char c = input.get(start);
		Array<Token> opt = new Array<Token>();
		Array<Token> error = new Array<Token>();
		for( Lexeme<?> l:lexeme.values() ) {
			if(l.startsWith(c)) {
				Token t = l.match(input, start, end);
				if(t.isError()) error.add(t);
				else opt.add(t);
			}
		}
		if( opt.size() > 0 ) {
			current = opt.get(0);
			for( int i=1; i<opt.size(); i++ ) {
				Token e2 = opt.get(i);
				if(e2.size()>current.size() || 
						(e2.size()==current.size()&& priority.get(e2.type())>priority.get(current.type()))) 
					current = e2;
			}
		}else {
			if(error.size()>0) {
				current = error.get(0);
				for( int i=1; i<error.size(); i++ ) {
					Token e2 = error.get(i);
					if(e2.size()>current.size()) current = e2;
				}
			}else { current = new Token(input,start, start+1,c); }
		}
		start = current.end();
		return current;
	}
}