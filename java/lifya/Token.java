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

/**
 * <p>Language token (may be a lexeme, a syntactic rule, an object associated with a position in the source </p>
 *
 */
public class Token extends Position{
	/**
	 * Token typ'se TAG
	 */
	public static final String TYPE = "type";
	
	/**
	 * Error's TAG. Used for identifying error tokens
	 */
	public static final String ERROR = "error";
	
	/**
	 * End's TAG. Used for identifying no available tokens/ end of input reached
	 */
	public static final String END = "end";
	
	/**
	 * Value's TAG. Used for retrieving the value stored by the token 
	 */
	public static final String VALUE = "value";

	protected String type;
	protected int end;
	protected Object value;
    
	/**
	 * Creates a token
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @param value Value stored by the token
	 */
	public Token(Source input, int start, int end, Object value){
		this(ERROR, input, start, end, value);
	}

	/**
	 * Creates a token
	 * @param type Token type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @param value Value stored by the token
	 */
	public Token(String type, Source input, int start, int end, Object value){
		super(input,start);
		this.type = type;
		this.end = end;
		this.value = value;
	}
	
	/**
	 * Computes the length (number of symbols) consumed by the token 
	 * @return Length (number of symbols) consumed by the token
	 */
	public int size(){ return this.end-this.start; }
    
	/**
	 * Gets the ending position (not included) of the token in the input source
	 * @return Ending position (not included) of the token in the input source
	 */
	public int end() { return end; }
    
	
	/**
	 * Shifts the absolute position a <i>delta</i> amount
	 * @param delta delta moving of the absolute position
	 */
	@Override
	public void shift(int delta) {
		start+=delta;
		end+=delta;
	}

    /**
     * Gets a JXON version of the token
     * @return JXON version  of the token
     */
	@Override
	public JXON jxon(){
		JXON json = super.jxon();
		json.set(END, end);
		json.set(VALUE,value.toString());
		json.set(TYPE,type);
		return json;
	}
    
	/**
	 * Gets the value stored by the token
	 * @return Value stored by the token
	 */
	public Object value() { return this.value; }
    
	/**
	 * Gets the token's type
	 * @return Token's type
	 */
	public String type() { return type; }
    
	/**
	 * Sets the token's type
	 * @param type Token's type
	 */
	public void type( String type ) { this.type = type; }
	
	/**
	 * Sets the token's value
	 * @param value Token's value
	 */
	public void value( Object value ) { this.value = value; }
    
	public String toString() { return "["+type+','+start+','+end+','+value+']'; }
    
	/**
	 * Converts the token to an error version of it
	 * @return Error version of the token
	 */
	public Token toError() { return new Token(input,start,end,type()); }
    
	/**
	 * Determines if it is an error token or not 
	 * @return <i>true</i> if an error token, <i>false</i> otherwise 
	 */
	public boolean isError() { return type()==ERROR; }
}