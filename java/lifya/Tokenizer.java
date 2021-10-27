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
package lifya;

import java.util.HashMap;

import speco.array.Array;

/**
 * <p>Language tokenizer</p>
 *
 */
public abstract class Tokenizer implements Read<Array<Token>>{
	/**
	 * TAG for Array of tokens
	 */
	public static final String TOKEN_LIST = "Array<Token<String>>";

	protected HashMap<String,String> removableTokens = new HashMap<String, String>();
	protected boolean remove=true;
	protected Source input;

	/**
	 * Default constructor
	 */
	public Tokenizer() {}
    
	/**
	 * Sets the collection of removable tokens
	 * @param removableTokens Tokens that will be removed from analysis
	 */
	public void removables(String[] removableTokens) { 
		for( String rt: removableTokens )
			this.removableTokens.put(rt,rt); 
	}

	/**
	 * Sets the collection of removable tokens
	 * @param removableTokens Tokens that will be removed from analysis
	 */
	public void removables(Array<String> removableTokens) {
		for( String rt: removableTokens )
			this.removableTokens.put(rt,rt); 
	}

	/**
	 * Determines if a token type is removable or not
	 * @param type Lexeme type to analyze
	 * @return <i>true</i> If the token type can be removed, <i>false</i> otherwise.
	 */
	public boolean removable(String type) { return removableTokens.containsKey(type); }

	/**
	 * Determines if a type name is a token type or not
	 * @param type type name to analyze
	 * @return <i>true</i> If the type name represents a token type, <i>false</i> otherwise.
	 */
	public abstract boolean isTokenType(String type);
	
	/**
	 * Return the set of types of token
	 * @return Types of token
	 */
	public abstract Array<String> tokenTypes();
    
	/**
	 * Gets the last read/available Token 
	 * @return Last read/available Token
	 */
	protected abstract Token analyze(Source input);
    
	/**
	 * Determines if produce or not removable tokens
	 * @param remove A value of <i>true</i> indicates that removable tokens will not included in tokenization, a 
	 * <i>false</i> value indicates that those must be included.
	 */
	public void removeTokens(boolean remove) { this.remove = remove; }
	
	/**
	 * Gets the Token list
	 * @param input Input source
	 * @return Token list
	 */
	public Token match(Source input){
		int start = input.pos();
		Array<Token> list = new Array<Token>();
		Token t;
		while(!input.eoi()) {
			t = analyze(input); 
			if(t.isError()) {
				input.locate(start);
				return t;
			}
			if(!remove || !removable(t.type())) list.add(t);
		}
		input.error(null);
		return new Token(input, start, input.pos(), TOKEN_LIST, list);
	}
    
	/**
	 * Removes from the Token lists tokens with the given tag
	 * @param tokens Tokens to be analyzed
	 * @param toremove Tag of the tokens to be removed
	 * @return The Array of tokens without the desired tokens
	 */
	public static Array<Token> remove(Array<Token> tokens, String toremove ){
		for( int i=tokens.size()-1; i>=0; i-- )	if( toremove.indexOf(tokens.get(i).type()) >= 0 ) tokens.remove(i);
		return tokens;
	}	
}