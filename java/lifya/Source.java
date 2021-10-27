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

import kompari.integer.IntL2HOrder;
import speco.array.Array;
import speco.array.SortedSearch;
import speco.object.Named;

/**
 * <p>Considers a given String as an input for the language recognizer</p>
 *
 */
public class Source extends Named{
	/**
	 * End of input character
	 */
	public static final char EOI = '\0';

	protected String input;
	protected Array<Integer> rows = new Array<Integer>();
	protected SortedSearch<Integer> search = new SortedSearch<Integer>(new IntL2HOrder());
	protected Token error = null;
	
	protected int start;
	protected int end;
	protected int pos;

	/**
	 * Creates a source input from the given String
	 * @param id Identification TAG of the input source
	 * @param input String used as input source
	 */
	public Source(String id, String input) { this(id, input, 0, input.length()); }
	
	/**
	 * Creates a source input from the given String
	 * @param id Identification TAG of the input source
	 * @param input String used as input source
	 * @param start Initial position of the source
	 * @param end Final position of the source
	 */
	public Source(String id, String input, int start, int end) {
		super(id);
		this.start = start;
		this.end = end;
		this.pos = start;
		this.input = input;
		search.set(rows);
		rows.add(0);
		for(int i=0;i<input.length(); i++) 
			if(input.charAt(i)=='\n') rows.add(i+1);
	}
   
	/**
	 * Gets [row,column] array when considering position a 2D position
	 * @param index Linear position to analyze
	 * @return [row,column] array when considering the given absolute position a 2D position
	 */
	public int[] location(int index) {
		int idx = search.findLeft(index);
		if(idx+1<rows.size() && rows.get(idx+1)==index) return new int[] {idx+1,0};
		return new int[] {idx, index-rows.get(idx)};
	}
	
	/**
	 * Current position in the source
	 * @return Current position
	 */
	public int pos() { return pos; }
	
	/**
	 * Advances one character in the input
	 * @return New current character
	 */
	public char next() {
		pos++;
		if(pos>=end) {
			pos=end;
			return EOI;
		}else return input.charAt(pos);
	}
	
	/**
	 * Gets the current character 
	 * @return Current character
	 */
	public char current() { return valid(pos)?input.charAt(pos):EOI; }
	
	
	/**
	 * Locates the reading cursor
	 * @param index New cursor's position
	 * @return Character at the cursor's position
	 */
	public char locate( int index ) {
		if( index < start ) index = start;
		else if(index>end) index = end;
		pos = index;
		return current();
	}
      
	/**
	 * Determines if the index is a valid one (start&lt;=index && index&lt;end)
	 * @param index
	 * @return
	 */
	protected boolean valid(int index) { return (start<=index && index<end); }
	
	/**
	 * Determines if the reading cursor reaches the end of the source
	 * @return <i>true</i> if the reading cursor is at the end of the source, <i>false</i> otherwise.
	 */
	public boolean eoi() { return pos==end; }
	
	/**
	 * Obtains a substring of the input String
	 * @param start Starting position of the substring to obtain
	 * @param end Final position (not included) of the substring to obtain
	 * @return Substring of the input String
	 */
	public String substring(int start, int end) {
		start = Math.max(this.start, start);
		end = Math.min(this.end, end);
		return input.substring(start,end); 
	}
	
	/**
	 * Gets the input length
	 * @return Input length
	 */
	public int length() { return end-start; }
	
	/**
	 * Resets the source (locates the cursor at the start positions, cleans the error token)
	 */
	public void reset() {
		pos = start;
		error = null;
	}
	
	/**
	 * Sets the error token for the source
	 * @param t Error token
	 */
	public void error(Token t) { if(t==null || error==null || t.start()>error.start()) error = t; }
	
	/**
	 * Gets the error token of the input source
	 * @return Error token
	 */
	public Token error() { return error; }
}