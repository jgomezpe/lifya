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
package speco.array;

/**
 * <p>Stringify method for array of objects.</p>
 *
 */
public class ArrayStringifier {
	protected char OPEN = '[';
	protected char CLOSE = ']';
	protected char SEPARATOR = ',';
	
	/**
	 * Default constructor uses '[', ']', and ',' as formating characters 
	 */
	public ArrayStringifier() {}
	
	/**
	 * Creates an array stringifier with the associated formatting characters
	 * @param open Array opening character 
	 * @param close Array closing character
	 * @param separator Array elements separating character
	 */
	public ArrayStringifier( char open, char close, char separator ) {
		this.CLOSE = close;
		this.OPEN = open;
		this.SEPARATOR = separator;
	}
	
	/**
	 * Stringifies a portion of an array
	 * @param array Array to be stringified
	 * @param start Initial position of the portion of the array to be stringified
	 * @param end Final position (not included) of the portion of the array to be stringified 
	 * @return An stringified version of the portion of the array
	 */
	protected String apply( Object array, int start, int end ) {
		int n = java.lang.reflect.Array.getLength(array);
		start = Math.max(0, start);
		end = Math.min(end, n);
		StringBuilder sb = new StringBuilder();
		if( OPEN != '\0' ) sb.append(OPEN);
		boolean flag = false;
		for( int i=0; i<n; i++ ){
			if( flag ) sb.append(SEPARATOR);
			String x = lifya.stringify.Stringifier.apply(java.lang.reflect.Array.get(array,i));
			if( x==null ) return null;
			sb.append(x);
			flag = true;			
		}	
		if( CLOSE != '\0' ) sb.append(CLOSE);
		return sb.toString();	
	}
	
	/**
	 * Stringifies an array
	 * @param array Array to be stringified
	 * @return An stringified version of the array
	 */
	public String apply(Object[] array) { return apply( array, 0, array.length); }
	
	/**
	 * Stringifies an object if it is a kind o f array
	 * @param obj Array to stringify 
	 * @return A stringified version of the array
	 */
	public String apply(Object obj) {
		if( obj.getClass().isArray() ) return apply(obj, 0, java.lang.reflect.Array.getLength(obj));
		if( obj instanceof Array ) return apply(((Array<?>)obj).buffer, 0, ((Array<?>)obj).size());
		return obj.toString();
	}
}