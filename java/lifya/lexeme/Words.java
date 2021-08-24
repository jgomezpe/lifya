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
package lifya.lexeme;

import lifya.Source;
import lifya.Token;

/**
 * <p>Title: Words</p>
 *
 * <p>Description: Parses any of the strings/words provided in the set of words,
 *  useful for reserved words in a programming language.</p>
 *
 */
public class Words implements Lexeme<String>{
	protected String[] word;
	protected String type;
    
	/**
	 * Creates a parser for the provided set of strings/words 
	 * @param type Type of the set of words
	 * @param word Set of words defining the lexema
	 */
	public Words(String type, String[] word) {
		this.word = word;
		this.type = type;
	}
 
	/**
	 * Creates a token with the set of words type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @return Number token
	 */
	@Override
	public Token match(Source input, int start, int end) {
		for( String w:word) {
			String x = input.substring(start,Math.min(end, start+w.length()));
			if(w.equals(x)) return token(input,start,start+x.length(),x);
		}
		return error(input,start,start+1);
	}

	/**
	 * Determines if the set of words lexeme can star with the given character (a character in the set)
	 * @param c Character to analyze
	 * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c) {
		for( String w:word) if(w.charAt(0)==c) return true;
		return false;
	}

	/**
	 * Gets the type of the words set lexema
	 * @return Type of the words set lexema
	 */
	@Override
	public String type() { return type; }
}