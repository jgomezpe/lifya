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
 * <p>Title: StringRecover</p>
 *
 * <p>Description: Recovers (Load from a String) an String</p>
 *
 */
public class StringParser implements Lexeme<String>{
    public static final String TAG = "String";
    public String type() { return TAG; }
    
    protected char quotation;

    /**
     * Creates a recovering method for strings
     */
    public StringParser() { this('"'); }
    
    public StringParser(char quotation) {
	this.quotation = quotation;
    }

    public boolean startsWith(char c){ return c==quotation; }

    @Override
    public Token match(Source txt, int start, int end) {
	if(!this.startsWith(txt.get(start))) return error(txt, start, start);
	int n = end;
	end = start+1;
	if(end==n) return error(txt, start, end);
	String str = "";
	while(end<n && txt.get(end)!=quotation){
	    if(txt.get(end)=='\\'){
		end++;
		if(end==n) return error(txt, start, end);
		if(txt.get(end)=='u') {
		    end++;
		    int c = 0;
		    while(end<n && c<4 && (('0'<=txt.get(end) && txt.get(end)<='9') || 
			('A'<=txt.get(end) && txt.get(end)<='F') ||
			('a'<=txt.get(end) && txt.get(end)<='f'))){
			end++;
			c++;
		    }
		    if(c!=4) return error(txt, start, end);
		    str += (char)Integer.parseInt(txt.substring(end-4,end),16);		    
		}else {
		    switch(txt.get(end)){
			case 'n': str += '\n'; break;
			case 'r': str += '\r'; break;
			case 't': str += '\t'; break;
			case 'b': str += '\b'; break;
			case 'f': str += '\f'; break;
			case '\\': case '/': str += txt.get(end); break;
			default:
			    if(txt.get(end)!=quotation)
				return error(txt, start, end);
			    str += quotation;
		    }
		    end++;
		}
	    }else{
		str += txt.get(end);
		end++;
	    }
	}
	if(end==n) return error(txt, start, end);
	end++;
	return token(txt, start, end, str);
    }	
}