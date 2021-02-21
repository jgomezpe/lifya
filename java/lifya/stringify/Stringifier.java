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
package lifya.stringify;

import java.util.Base64;
import java.util.Base64.Encoder;

import lifya.lexeme.BlobParser;
import speco.array.Array;
import speco.array.ArrayStringifier;

import java.util.HashMap;

/**
 * <p>Title: Stringifier</p>
 *
 * <p>Description: Stringifies (Stores into a String) an object</p>
 *
 */
public class Stringifier {	
    /**
     * Stringifies the object
     * @param obj Object to be stringified
     * @return An stringified version of the object
     */
    public static String apply(Stringifyable obj) {
	return obj.stringify();
    }
   
    /**
     * Stringifies a portion of an array
     * @param array Array to be stringified
     * @param start Initial position of the portion of the array to be stringified
     * @param end Final position (not included) of the portion of the array to be stringified 
     * @return An stringified version of the portion of the array
     */
    public static String apply(Object array, char OPEN, char CLOSE, char SEPARATOR) {
	ArrayStringifier str = new ArrayStringifier(OPEN,CLOSE,SEPARATOR);
	return str.apply(array);	
    }
    
    public static String apply(Array<?> array) {
	return apply(array,'[', ']', ',');
    }
    
    public static String apply(Array<?> array, char OPEN, char CLOSE, char SEPARATOR) {
	ArrayStringifier str = new ArrayStringifier(OPEN,CLOSE,SEPARATOR);
	return str.apply(array);	
    }
    
    public static String apply(HashMap<String, Object> map) {
	return apply(map, '{', '}', ',', ':');
    }

    public static String apply(HashMap<String, Object> map, 
	    char OPEN, char CLOSE, char SEPARATOR, char ASSIGN) {
	StringBuilder sb = new StringBuilder();
	boolean flag = false;
	if( OPEN != '\0' ) sb.append(OPEN);
	for( String key:map.keySet() ) {
		if( flag ) sb.append(SEPARATOR);
		sb.append(apply(key));
		sb.append(ASSIGN);
		sb.append(apply(map.get(key)));
		flag = true;
	}
	if( CLOSE != '\0' ) sb.append(CLOSE);
	return sb.toString();
    }
    
    /**
     * Stringifies the object
     * @param obj Object to be stringified
     * @return An stringified version of the object
     */
    @SuppressWarnings("unchecked")
    public static String apply( Object obj ){
	if(obj==null) return "null";
	if(obj.getClass().isArray()) return apply(obj, '[', ']', ',');
	if(obj instanceof String) return apply((String)obj);
	if(obj instanceof Stringifyable) return apply((Stringifyable)obj);
	if(obj instanceof Array) return apply((Array<?>)obj);
	if(obj instanceof HashMap) return apply((HashMap<String,Object>)obj);
	return obj.toString();
    }

    public static String apply(String str) { return apply(str, '"'); }
    
    public static String apply(String str, char quotation) {
	StringBuilder sb = new StringBuilder();
	sb.append(quotation);
	for( int i=0; i<str.length(); i++ ){
	    char c = str.charAt(i);
	    switch( c ){
	    case '\\': sb.append("\\\\"); break;
	    case '\b': sb.append("\\b"); break;
	    case '\f': sb.append("\\f"); break;
	    case '\n': sb.append("\\n"); break;
	    case '\r': sb.append("\\r"); break;
	    case '\t': sb.append("\\t"); break;
	    default:
		if( c < 32 || c > 255 ){
		    sb.append("\\u");
		    sb.append(Integer.toHexString((int)c));
		}else if(c==quotation)
		    sb.append("\\"+quotation);
		else
		    sb.append(c);
		break;
	    }
	}
	sb.append(quotation);
	return sb.toString();				
    }
    
    public static String apply(Character c) { return apply(""+c, '"'); }

    public static String apply(byte[] blob) { return apply(blob, ""+BlobParser.STARTER); }
    
    public static String apply(byte[] blob, String STARTER) {
	Encoder enc = Base64.getMimeEncoder();
	String txt = enc.encodeToString(blob);
	StringBuilder sb = new StringBuilder(); 
	sb.append(STARTER);
	sb.append(txt);
	return sb.toString();
    }
}