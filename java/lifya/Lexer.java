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

import lifya.lexeme.Space;
import speco.array.Array;

/**
 * <p>Title: Lexer (Tokenizer)</p>
 *
 * <p>Description: Language lexer</p>
 *
 */
public abstract class Lexer implements Read<Array<Token>>{
	/**
	 * TAG for Array of tokens
	 */
	public static final String TOKEN_LIST = "Array<Token>";

	protected String[] removableTokens;
	protected boolean remove=true;
	protected Source input;
	protected int start;
	protected int end;
    
	protected Token current;
	protected boolean back = false;

	/**
	 * Default constructor
	 */
	public Lexer() { this( new String[] {}); }
    
	/**
	 * Creates a Lexer that removes (does not take into account) the given Token types
	 * @param removableTokens Tokens that will be removed from analysis
	 */
	public Lexer(String[] removableTokens) { this.removableTokens = removableTokens; }

	/**
	 * Makes the Lexer (Tokenizer) to consider or not (put in the generated Array of tokens) the removable tokens 
	 * @param remove <i>true</i>: Lexer will not consider removable tokens, <i>false</i>: Lexer will consider removable tokens. 
	 */
	public void removeTokens(boolean remove) { this.remove = remove; }
    
	/**
	 * Initialize the lexer over the input String
	 * @param input String source
	 */
	public void init(String input) { this.init(input,0,input.length()); }
    
	/**
	 * Initialize the lexer over the input String (starting at the given position) 
	 * @param input String source
	 * @param start Starting position for reading a token
	 */
	public void init(String input, int start) { this.init(input,start,input.length()); }
    
	/**
	 * Initialize the lexer over the input String (limited to the given starting and ending positions) 
	 * @param input String source
	 * @param start Starting position for reading a token
	 * @param end Ending position for reading a token (not included)
	 */
	public void init(String input, int start, int end) { init(new Source(input), start, end); }
    
	/**
	 * Initialize the lexer over the input Source (limited to the given starting and ending positions) 
	 * @param input String source
	 * @param start Starting position for reading a token
	 * @param end Ending position for reading a token (not included)
	 */
	public void init(Source input, int start, int end) {	
		this.input = input;
		this.start = start;
		this.end = end;
		this.back = false;
	}
    
	/**
	 * Gets the last read/available Token 
	 * @return Last read/available Token
	 */
	protected abstract Token get();
    
	/**
	 * Determines if a Token is removable or not
	 * @param t Token to analyze
	 * @return <i>true</i> If the Token can e removed, <i>false</i> otherwise.
	 */
	protected boolean removable(Token t) {
		int i=0;
		while(i<removableTokens.length && t.type()!=removableTokens[i]) i++;
		return(i!=removableTokens.length);	
	}
    
	/**
	 * Gets the next available Token
	 * @return Nex available Token
	 */
	public Token next() {
		if(back) {
			back = false;
			return current;
		}
		do { current = get(); }while(current!=null && remove && removable(current));
		return current;
	}
    
	/**
	 * Makes the Lexer to go back one token
	 */
	public void goback() { back = true; }
    
	/**
	 * Reads a token from the input Source (limited to the given starting and ending positions) 
	 * @param input String source
	 * @param start Starting position for reading a token
	 * @param end Ending position for reading a token (not included)
	 * @return Token read from the input String
	 */
	public Token match(Source input, int start, int end) {
		init(input,start,end);
		Array<Token> list = new Array<Token>();
		Token t;
		while((t=next())!=null && t.type()!=Token.ERROR) { list.add(t); }
		if(t==null) return new Token(TOKEN_LIST, input, start, list.get(list.size()-1).end(), list);
		else return t;
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
	
	/**
	 * Removes from the Token lists space tokens
	 * @param tokens Tokens to be analyzed
	 * @return The Array of tokens without space tokens
	 */
	public static Array<Token> remove_space(Array<Token> tokens ){ 
		return remove(tokens, Space.TAG); 
	}	
}