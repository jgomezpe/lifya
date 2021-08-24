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

/**
 * <p>Read objects from an input source</p>
 * @param <T> Type of objects to read
 *
 */
public interface Read<T> {	
	/**
	 * Reads an object from the input source (limited to the given starting and ending positions) 
	 * @param input Symbol source
	 * @param start Starting position for reading a token
	 * @param end Ending position for reading a token (not included)
	 * @return Object read from the symbol source
	 * @throws IOException if the object could not be read
	 */
	@SuppressWarnings("unchecked")
	default T get(Source input, int start, int end) throws IOException{
		Token t = match(input,start,end);
		if(t.isError()) throw new IOException(Stringifier.apply(t));
		return (T)t.value();	
	}
 
	/**
	 * Reads an object from the String source (limited to the given starting and ending positions) 
	 * @param input String source
	 * @param start Starting position for reading a token
	 * @param end Ending position for reading a token (not included)
	 * @return Object read from the symbol source
	 * @throws IOException if the object could not be read
	 */
	default T get(String input, int start, int end) throws IOException{
		return get(new Source(input), start, end);
	}
	
	/**
	 * Reads an object from the String source (starting at the given position) 
	 * @param input String source
	 * @param start Starting position for reading a token
	 * @return Object read from the symbol source
	 * @throws IOException if the object could not be read
	 */
	default T get(String input, int start) throws IOException {
		return get(input, start, input.length());
	}
	
	/**
	 * Reads an object from the String source 
	 * @param input String source
	 * @return Object read from the String source
	 * @throws IOException if the object could not be read
	 */
	default T get(String input) throws IOException { return get(input, 0); }
	
	/**
	 * Reads a token from the input source (limited to the given starting and ending positions) 
	 * @param input Symbol source
	 * @param start Starting position for reading a token
	 * @param end Ending position for reading a token (not included)
	 * @return Token read from the symbol source
	 */
	Token match(Source input, int start, int end);
	
	/**
	 * Reads a token from the input String (limited to the given starting and ending positions) 
	 * @param input String source
	 * @param start Starting position for reading a token
	 * @param end Ending position for reading a token (not included)
	 * @return Token read from the input String
	 */
	default Token match(String input, int start, int end) {
		return match(new Source(input), start, end);
	}
	
	/**
	 * Reads a token from the input String (starting at the given position)
	 * @param input Symbol source
	 * @param start Starting position for reading a token
	 * @return Token read from the input String
	 */
	default Token match(String input, int start) {
		return match(input, start, input.length());
	}
	
	/**
	 * Reads a token from the input String 
	 * @param input String source
	 * @return Token read from the input String
	 */
	default Token match(String input) {  return match(input, 0); }	
}