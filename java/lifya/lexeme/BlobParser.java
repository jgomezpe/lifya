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

import java.util.Base64;
import java.util.Base64.Decoder;

import lifya.Source;
import lifya.Token;

/**
 * <p>Title: BlobParser</p>
 *
 * <p>Description: Parses a Blob/Byte array using Base64</p>
 *
 */
public class BlobParser implements Lexeme<byte[]>{ 
	/**
	 * Starter character of a blob, if required
	 */
	public static final char STARTER = '#';
	
	/**
	 * Blob type TAG
	 */
	public static final String TAG = "byte[]";
	
	protected boolean useStarter = false;
	protected int length;

	/**
	 * Creates a Blob parser that does not requires starter character
	 */
	public BlobParser() { this(false); } 
	
	/**
	 * Creates a Blob parser 
	 * @param useStarter <i>true</i> indicates that parser must check for starter character, <i>false</i> indicates
	 * that parser does no requires starter character
	 */
	public BlobParser(boolean useStarter ) { this.useStarter = useStarter; }

	/**
	 * Determines if a character is a valid Base64 character
	 * @param c Character to analyze
	 * @return <i>true</i> if a character is a valid Base64 character, <i>false</i> otherwise
	 */
	public boolean valid(char c) { return Character.isLetterOrDigit(c)||c=='+'||c=='/'; }
	
	/**
	 * Gets the type of blob lexema
	 * @return Type of blob lexema
	 */
	@Override
	public String type() { return TAG; }

	/**
	 * Creates a token with the blob/bitarray type
	 * @param input Input source from which the token was built
	 * @param start Starting position of the token in the input source
	 * @param end Ending position (not included) of the token in the input source
	 * @return Blob token
	 */
	@Override
	public Token match(Source input, int start, int end){
		if(!startsWith(input.get(start))) return error(input,start,start+1);
		int n=end;
		end=start+1;
		while(end<n && valid(input.get(end))) end++;
		int s = (useStarter)?start+1:start;
		int m = (end-s)%4;
		if(s==end || m==1) return error(input,start,end);
		if(m>0) {
			while(end<n && m<4 && input.get(end)=='=') {
				end++;
				m++;
			}
			if(m<4) return error(input,start,end);
		}
		Decoder dec = Base64.getMimeDecoder();
		Object obj = dec.decode(input.substring(s,end));
		return token(input,start,end,obj);
	}

	/**
	 * Determines if the lexeme can star with the given character
	 * @param c Character to analyze
	 * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(char c) { return useStarter?(c==STARTER):valid(c); }
}