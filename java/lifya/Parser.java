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

import java.io.IOException;

import lifya.stringify.Stringifier;
import speco.array.Array;

/**
 * <p>Abstract syntactic parser</p>
 *
 */
public abstract class Parser implements Read<Token>{
	protected TokenSource lexer;
	protected Tokenizer tokenizer;
	protected boolean leftover = false;
	
	/**
	 * Creates a syntactic parser using the given tokenizer
	 * @param tokenizer Tokenizer
	 */
	public Parser(Tokenizer tokenizer) { this.tokenizer = tokenizer; }
	
	/**
	 * Indicates to the parser if can generate leftovers or not (i.e. if the parser must consume all tokens or not)
	 * @param leftover A <i>true</i> value indicates that parser can produces leftovers (may not consume all tokens), <i>false</i> otherwise
	 */
	public void leftover(boolean leftover) { 
		this.leftover = leftover;
	}
	
	/**
	 * Creates a syntactic token (parser tree) from the list of tokens
	 * @return Syntactic token
	 */
	public abstract Token analyze();
	
	/**
	 * Reads an object from the input source (limited to the given starting and ending positions) 
	 * @param input Symbol source
	 * @return Syntactic token read from the symbol source
	 */
	public Token match(Source input){
		Token t = tokenizer.match(input); 
		if( t.isError() ) return t;
		@SuppressWarnings("unchecked")
		Array<Token> tokens = (Array<Token>)t.value();
		lexer = new TokenSource(tokens);
		t = analyze();
		if(!leftover && lexer.current()!=null) t = lexer.current().toError();
		return t;
	}
	
	/**
	 * Determines if a type name is a token type or not
	 * @param type type name to analyze
	 * @return <i>true</i> If the type name represents a token type, <i>false</i> otherwise.
	 */
	public boolean isToken(String type) { return tokenizer.isTokenType(type); }
	
	public Token current() { return lexer.current(); }
	
	/**
	 * Reads an object from the input source 
	 * @param input Symbol source
	 * @return Object read from the symbol source
	 * @throws IOException if the object could not be read
	 */
	@Override
	public Token get(Source input) throws IOException{
		Token t = match(input);
		if(t.isError()) {
			input.error(t);
			throw new IOException(Stringifier.apply(input.error()));
		}
		return t;	
	}
	
	/**
	 * Gets the tokenizer used by the parser
	 * @return Tokenizer used by the parser
	 */
	public Tokenizer tokenizer() { return tokenizer; }
}