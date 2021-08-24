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
 * <p>Title: StringParser</p>
 *
 * <p>Description: Parses an String</p>
 *
 */
public class StringParser implements Lexeme<String>{
	/**
	 * String lexema type TAG
	 */
	public static final String TAG = "String";
    
	/**
	 * Gets the type of String lexema
	 * @return Type of String lexema
	 */
	public String type() { return TAG; }
    
	protected char quotation;

	/**
	 * Creates a parsing method for strings, uses '"' as quotation for Strings
	 */
	public StringParser() { this('"'); }
    
	/**
	 * Creates a parsing method for strings, using the provided quotation character
	 * @param quotation Quotation character
	 */
	public StringParser(char quotation) { this.quotation = quotation; }

	/**
	 * Determines if the lexeme can star with the given character (quotation character)
	 * @param c Character to analyze
	 * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c){ return c==quotation; }

	/**
	 * Creates a token with the String type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @return Number token
	 */
	@Override
	public Token match(Source input, int start, int end) {
		if(!this.startsWith(input.get(start))) return error(input, start, start);
		int n = end;
		end = start+1;
		if(end==n) return error(input, start, end);
		String str = "";
		while(end<n && input.get(end)!=quotation){
			if(input.get(end)=='\\'){
				end++;
				if(end==n) return error(input, start, end);
				if(input.get(end)=='u') {
					end++;
					int c = 0;
					while(end<n && c<4 && (('0'<=input.get(end) && input.get(end)<='9') || 
							('A'<=input.get(end) && input.get(end)<='F') ||
							('a'<=input.get(end) && input.get(end)<='f'))){
						end++;
						c++;
					}
					if(c!=4) return error(input, start, end);
					str += (char)Integer.parseInt(input.substring(end-4,end),16);		    
				}else {
					switch(input.get(end)){
						case 'n': str += '\n'; break;
						case 'r': str += '\r'; break;
						case 't': str += '\t'; break;
						case 'b': str += '\b'; break;
						case 'f': str += '\f'; break;
						case '\\': case '/': str += input.get(end); break;
						default:
							if(input.get(end)!=quotation)
								return error(input, start, end);
							str += quotation;
					}
					end++;
				}
			}else{
				str += input.get(end);
				end++;
			}
		}
		if(end==n) return error(input, start, end);
		end++;
		return token(input, start, end, str);
	}	
}