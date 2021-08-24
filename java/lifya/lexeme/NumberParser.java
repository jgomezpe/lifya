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
 * <p>Title: NumberParser</p>
 *
 * <p>Description: Parses numbers (integer or real)</p>
 *
 */
public class NumberParser implements Lexeme<Number>{
	/**
	 * Number lexema type TAG
	 */
	public static final String TAG = "number";
	
	/**
	 * Gets the type of number lexema
	 * @return Type of number lexema
	 */
	public String type() { return TAG; }
	
	/**
	 * Determines if the character is a '+' or '-'
	 * @param c Character to analyze 
	 * @return <i>true</i> if the character is a '+' or '-', <i>false</i> otherwise.
	 */
	public static boolean issign(char c){ return ('-'==c || c=='+'); }

	/**
	 * Determines if the lexeme can start with the given character (a digit or '+', '-')
	 * @param c Character to analyze
	 * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c){ return issign(c) || Character.isDigit(c); }

	/**
	 * Creates a token with the number type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @return Number token
	 */
	@Override
	public Token match(Source input, int start, int end){
		if(!this.startsWith(input.get(start))) return error(input, start, start);
		int n = end;
		end=start+1;
		while(end<n && Character.isDigit(input.get(end))) end++;
		if(end==n) return token(input, start, end, Integer.parseInt(input.substring(start,end)));
		boolean integer = true;
		if(input.get(end)=='.'){
			integer = false;
			end++;
			int s=end;
			while(end<n && Character.isDigit(input.get(end))) end++;
			if(end==n) return token(input, start, end, Double.parseDouble(input.substring(start,end)));
			if(end==s) return error(input, start, end);
		}
		if(input.get(end)=='E' || input.get(end)=='e'){
			integer = false;
			end++;
			if(end==n) return error(input, start, end);
			if(issign(input.get(end))) end++;
			if(end==n) return error(input, start, end);
			int s = end;
			while(end<n && Character.isDigit(input.get(end))) end++;
			if(end==s) return error(input, start, end);
		}
		if( integer ) return token(input, start, end, Integer.parseInt(input.substring(start,end)));
		return token(input, start, end, Double.parseDouble(input.substring(start,end)));
	}	
}