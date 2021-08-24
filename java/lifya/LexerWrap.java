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

import speco.array.Array;

/**
 * <p>Title: LexerWrap</p>
 *
 * <p>Description: A Lexer built upon an Array of Lexema</p>
 *
 */
public class LexerWrap extends Lexer{
	protected Array<Token> tokens;
	protected int start;
	protected int end;
    
	/**
	 * Creates a Lexer from an Array of tokens
	 * @param tokens Array of Tokens used for configuring a Lexer
	 */
	public LexerWrap(Array<Token> tokens) { this(tokens,0,tokens.size()); }
    
	/**
	 * Creates a Lexer from an Array of tokens (starting at the given position)
	 * @param tokens Array of Tokens used for configuring a Lexer
	 * @param start Position in the Array of tokens for starting the Lexer
	 */
	public LexerWrap(Array<Token> tokens, int start) { this(tokens,start,tokens.size()); }
    
	/**
	 * Creates a Lexer from an Array of tokens (using the given starting and ending positions)
	 * @param tokens Array of Tokens used for configuring a Lexer
	 * @param start Position in the Array of tokens for starting the Lexer
	 * @param end Position in the Array of tokens for ending the Lexer (not included)
	 */
	public LexerWrap(Array<Token> tokens, int start, int end) {
		this.tokens = tokens;
		this.start = start;
		this.end = end;
	}
     
	/**
	 * Gets the last read/available Token 
	 * @return Last read/available Token
	 */
	@Override
	protected Token get() {
		if(start>=end) return null;
		current = tokens.get(start++);
		return current;
	}
}