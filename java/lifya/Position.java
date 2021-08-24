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

import speco.jxon.JXON;
import speco.object.JXONfyable;

/**
 * <p>Title: Position</p>
 *
 * <p>Description: Position of the reading cursor in the input source</p>
 *
 */
public class Position implements JXONfyable{
	/**
	 * Source name TAG
	 */
	public static final String INPUT = "input";
	
	/**
	 * Starting position TAG
	 */
	public static final String START = "start";
	
	/**
	 * Row position TAG (when considering as a 2D position in the source)
	 */
	public static final String ROW = "row";
	
	/**
	 * Column position TAG (when considering as a 2D position in the source)
	 */	
	public static final String COLUMN = "column";
    
	protected Source input;
	protected int start;
	
	/**
	 * Creates a position for the given source 
	 * @param input Input source
	 * @param start Absolute position on the source 
	 */
	public Position(Source input, int start){
		this.input = input;
		this.start = start;	
	}
    
	/**
	 * Sets the absolute position
	 * @param start Absolute position
	 */
	public void start(int start) { this.start = start; }
	
	/**
	 * Gets the absolute position
	 * @return Absolute position
	 */
	public int start() { return start; }
    
	/**
	 * Shifts the absolute position a <i>delta</i> amount
	 * @param delta delta moving of the absolute position
	 */
	public void shift(int delta) { start+=delta; }

	/**
	 * Sets the position input source
	 * @param input Position input source
	 */
	public void input(Source input) { this.input = input; }  
	
	/**
	 * Gets the position source
	 * @return Position source
	 */
	public Source input(){ return this.input; }

    /**
     * Gets a JXON version of the position
     * @return JXON version  of the position
     */
	@Override
	public JXON jxon(){
		JXON jxon = new JXON();
		jxon.set(INPUT, input.id());
		jxon.set(START, start);
		int[] pos = input.pos(start);
		jxon.set(ROW, pos[0]);
		jxon.set(COLUMN, pos[1]);	
		return jxon;
	}  
}