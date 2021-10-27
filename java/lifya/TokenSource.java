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
 * <p>Tokens source</p>
 *
 */
public class TokenSource{
	protected Array<Token> tokens;
	protected int pos=0;
	protected HashMap<String, String> types = new HashMap<String, String>();
    
	/**
	 * Creates a Tokenizer source from an Array of tokens
	 * @param tokens Array of Tokens used for configuring a Tokenizer source
	 */
	public TokenSource(Array<Token> tokens) {
		this.tokens = tokens;
		for( int i=0; i<tokens.size(); i++) this.types.put(tokens.get(i).type(), "");
	}
	
	/**
	 * Gets the input source
	 * @return Input source
	 */
	public Source input() { return (tokens.size()>0)?tokens.get(0).input():null; }
    
	/**
	 * Current position in the tokens array source
	 * @return Current position
	 */
	public int pos() { return pos; }
	
	/**
	 * Advances one token
	 * @return New current token
	 */
	public Token next() {
		pos++;
		if(pos>tokens.size()) pos=tokens.size();
		return current();
	}
	
	/**
	 * Gets the current character 
	 * @return Current character
	 */
	public Token current() { return (0<=pos && pos<tokens.size())?tokens.get(pos):null; }
	
	/**
	 * Locates the token reading source
	 * @param index New reading position
	 * @return New current token
	 */
	public Token locate( int index ) {
		if(-1<=index && index<=tokens.size()) {
			pos = index;
			return current();
		}else return null;
	}
}