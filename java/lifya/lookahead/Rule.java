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

import lifya.Lexer;
import lifya.Source;
import lifya.Token;
import lifya.lexeme.Symbol;

/**
 * <p>Title: Rule</p>
 *
 * <p>Description: Parsing rule</p>
 *
 */
public abstract class Rule{	
	protected LAHParser parser;
	protected String type;
    
	/**
	 * Creates a syntactic rule for a parser
	 * @param type Type of the rule
	 * @param parser Syntactic parser using the rule
	 */
	public Rule(String type, LAHParser parser) { 
		this.parser = parser; 
		this.type = type;
	}
    
	/**
	 * Determines if the rule can start with the given token
	 * @param t Token to analyze
	 * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
	 */
	public abstract boolean startsWith( Token t );

	/**
	 * Gets the type of the rule
	 * @return Type of the rule
	 */
	public String type() { return type; }

	/**
	 * Determines if the symbol token comes from a given symbol lexema type and its value
	 * is the same as the character passed as argument 
	 * @param token Symbol token
	 * @param c Character to analyze
	 * @param TAG Type of the lexema
	 * @return <i>true</i> if the symbol token comes from a given symbol lexema type and its value
	 * is the same as the character passed as argument, <i>false</i> otherwise.
	 */
	public boolean check_symbol(Token token, char c, String TAG) {
		return token.type()==TAG && ((char)token.value()) == c;
	}
    
	/**
	 * Determines if the symbol token comes from a symbol lexema type and its value
	 * is the same as the character passed as argument 
	 * @param token Symbol token
	 * @param c Character to analyze
	 * @return <i>true</i> if the symbol token comes from a symbol lexema type and its value
	 * is the same as the character passed as argument, <i>false</i> otherwise.
	 */
	public boolean check_symbol(Token token, char c) { return check_symbol(token,c,Symbol.TAG); }

	/**
	 * Creates a rule token 
	 * @param lexer Lexer 
	 * @return Rule token
	 */
	public Token analyze(Lexer lexer) { return analyze(lexer, lexer.next()); }
    
	
	/**
	 * Creates a rule token using the <i>current</i> token as first token to analyze
	 * @param lexer Lexer 
	 * @param current Initial token
	 * @return Rule token
	 */
	public abstract Token analyze(Lexer lexer, Token current);
    
	/**
	 * Creates a eof token 
	 * @param input Input source 
	 * @param end Position to be considered the end of the input source
	 * @return EOF token
	 */
	public Token eof(Source input, int end) { return new Token(input,end,end,type()); }
        
	/**
	 * Creates a token with the rule type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @param value Value stored by the token
	 * @return Rule token
	 */
	public Token token(Source input, int start, int end, Object value) {
		return new Token(type(), input, start, end, value);
	}
}