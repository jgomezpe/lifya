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

import java.util.HashMap;

import lifya.Source;
import lifya.Token;

/**
 * <p>Parses any of the characters/symbols in a symbol collection</p>
 *
 */
public class Symbol implements Lexeme<Character>{
	/**
	 * General symbol lexema type TAG
	 */
	public static final String TAG = "symbol";
	
	protected HashMap<Character, Character> table = new HashMap<Character, Character>();
	protected String type;
    
	/**
	 * Creates a parser for a set of symbols
	 * @param symbols Symbols that will be considered in the lexema
	 */
	public Symbol(String symbols){ this(symbols,TAG); }

	/**
	 * Creates a parser for a set of symbols
	 * @param symbols Symbols that will be considered in the lexema
	 * @param type Type for the symbols lexema
	 */
	public Symbol(String symbols, String type){
		this.type = type;
		for( int i=0; i<symbols.length(); i++ ) table.put(symbols.charAt(i),symbols.charAt(i));
	}
 	
	/**
	 * Creates a token with the symbol type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @return Symbol token
	 */
	@Override
	public Token match(Source input, int start, int end) {
		if(startsWith(input.get(start))) return new Token(type(),input,start,start+1,input.get(start));
		else return error(input,start,start+1);
	}
	
	/**
	 * Determines if the symbol lexeme can start with the given character (a character in the set)
	 * @param c Character to analyze
	 * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c) { return table.containsKey(c); }
	
	/**
	 * Gets the type of symbols set lexema
	 * @return Type of symbols set lexema
	 */
	@Override
	public String type() { return type; }
}