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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lifya.Source;
import lifya.Token;

/**
 * <p>Title: Regex</p>
 *
 * <p>Description: Abstract class for parsing an object that satisfies a regular expression</p>
 * @param <T> Type of objects being parsed
 */
public abstract class Regex<T> implements Lexeme<T>{
	/**
	 * Regular expression used for recovering the object
	 */
	protected String regex;
	
	protected String type;
	
	/**
	 * Pattern associated to the regular expression
	 */
	protected Pattern pattern;
	
	/**
	 * Creates a Recovering method for objects that satisfy the given regular expression
	 * @param type Tag/Id for the Regular expression
	 * @param regex Regular expression used for recovering the object
	 */
	public Regex( String type, String regex ) {
		this.type = type;
		this.regex = regex;
		pattern = Pattern.compile("^("+regex+")");		
	}

	/**
	 * Gets the type of regex lexema
	 * @return Type of regex lexema
	 */
	@Override
	public String type() { return type; }

	/**
	 * Gets the regular expression used for recovering the object
	 * @return Regular expression used for recovering the object
	 */
	public String regex(){ return regex; }

	/**
	 * Gets a String with the escape characters used by regular expressions
	 * @return String with the escape characters used by regular expressions
	 */
	public static String escapechars() { return "<>()[]{}\\^-=$!|?*+."; }

	/**
	 * Determines if a character is a escape character used by regular expressions
	 * @param c Character to analyze
	 * @return <i>true</i> if the character is a escape character used by regular expressions, <i>false</i> otherwise.
	 */
	public static boolean escapechar(char c) { return escapechars().indexOf(c)>=0; }
	
	/**
	 * Encodes a character to its regex equivalent 
	 * @param c Character to encode
	 * @return A regex equivalent of the given character
	 */
	public static String encode(char c) {
		if(escapechar(c)) return "\\"+c;
		return ""+c;
	}

	protected abstract T object(String str);
	
	/**
	 * Creates a token with the regex type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @return Integer token
	 */
	@Override
	public Token match(Source input, int start, int end){
		String sub_input = input.substring(start, end);
		Matcher matcher = pattern.matcher(sub_input);
		if( matcher.find() && matcher.start()==0 ) return token(input, start, start+matcher.end(), object(matcher.group()));
		return error(input,start,start+1); 
	}
}