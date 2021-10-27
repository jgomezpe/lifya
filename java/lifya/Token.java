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

import speco.array.Array;
import speco.json.JSON;

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
	 * Creates an error token
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 */
	public Token(Source input, int start, int end){
		this(input, start, end, ERROR, input.substring(start, Math.min(Math.max(end,start+1), input.length())));
		input.error(this);
	}

	/**
	 * Creates a token
	 * @param type Token type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @param value Value stored by the token
	 */
	public Token(Source input, int start, int end, String type, Object value){
		super(input,start);
		this.type = type;
		this.end = end;
		this.value = value;
	}
	
	/**
	 * Computes the length (number of symbols) consumed by the token 
	 * @return Length (number of symbols) consumed by the token
	 */
	public int length(){ return this.end-this.start; }
    
	/**
	 * Gets the ending position (not included) of the token in the input source
	 * @return Ending position (not included) of the token in the input source
	 */
	public int end() { return end; }
    
	/**
	 * Sets the end of the token
	 * @param end End position of the token
	 */
	public void end(int end) { this.end = end; }
	
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
     * Gets a JSON version of the token
     * @return JSON version  of the token
     */
	@Override
	public JSON json(){
		JSON json = super.json();
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
    
	public String toString() { 
		return print(0); 
	}
    
	/**
	 * Converts the token to an error version of it
	 * @return Error version of the token
	 */
	public Token toError() { return new Token(input,start,end); }
    
	/**
	 * Determines if it is an error token or not 
	 * @return <i>true</i> if an error token, <i>false</i> otherwise 
	 */
	public boolean isError() { return type()==ERROR; }
	
	protected String print( int tab ) {
		StringBuilder sb = new StringBuilder();
		Object obj = value();
		if( obj instanceof Array ) {
			for( int k=0; k<tab; k++ ) sb.append(' ');
			sb.append(type());
			@SuppressWarnings({ "unchecked" })
			Array<Token> v = (Array<Token>)obj;
			for( int i=0; i<v.size(); i++ ) {
				sb.append('\n');
				sb.append(v.get(i).print(tab+1));
			}
		}else {
			for( int k=0; k<tab; k++ ) sb.append(' ');
			sb.append("["+type+','+start+','+end+','+value+']');
			if(obj instanceof Token) {
				sb.append('\n');
				sb.append(((Token)obj).print(tab+1));
			}
		}
		return sb.toString();
	}
}