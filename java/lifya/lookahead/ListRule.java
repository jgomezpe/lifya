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
package lifya.lookahead;

import lifya.Lexer;
import lifya.Source;
import lifya.Token;
import speco.array.Array;

/**
 * <p>A Parsing rule for lists</p>
 *
 */
public class ListRule extends Rule{
	/**
	 * Type of the Syntactic Rule for lists
	 */
	public final static String TAG = "LIST"; 
	
	protected char LEFT = '[';
	protected char RIGHT = ']';
	protected char SEPARATOR = ',';
	protected String item_rule;

	/**
	 * Creates a lists syntactic rule 
	 * @param type Type of the rule
	 * @param parser Syntactic parser using the rule
	 * @param item_rule Rule type of the elements of the list
	 */	
	public ListRule(String type, LAHParser parser, String item_rule) {
		super(type, parser); 
		this.item_rule = item_rule;
	}
    
	/**
	 * Creates a lists syntactic rule 
	 * @param type Type of the rule
	 * @param parser Syntactic parser using the rule
	 * @param item_rule Rule type of the elements of the list
	 * @param left Left character of the list
	 * @param right Right character of the list
	 * @param separator Elements separating character
	 */	
	public ListRule(String type, LAHParser parser, String item_rule, char left, char right, char separator) { 
		super(type, parser); 
		this.item_rule = item_rule;
		LEFT = left;
		RIGHT = right;
		SEPARATOR = separator;
	}
    
	/**
	 * Determines if the rule can start with the given token (left character)
	 * @param t Token to analyze
	 * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(Token t) { return check_symbol(t, LEFT); }
    
	/**
	 * Creates a rule token using the <i>current</i> token as first token to analyze
	 * @param lexer Lexer 
	 * @param current Initial token
	 * @return List rule token
	 */
	@Override
	public Token analyze(Lexer lexer, Token current) {
		if(!startsWith(current)) return current.toError();
		Source input = current.input();
		int start = current.start();
		int end = current.end();
		Array<Token> list = new Array<Token>();
		current = lexer.next();
		while(current!=null && !check_symbol(current, RIGHT)){
			Token t = parser.rule(item_rule).analyze(lexer, current);
			if(t.isError()) return t;
			list.add(t);
			end = current.end();
			current = lexer.next();
			if(current==null) return eof(input,end);
			if(check_symbol(current, SEPARATOR)) {
				end = current.end();
				current = lexer.next();
				if(current==null) return eof(input,end);
				if(check_symbol(current, RIGHT)) return current.toError(); 
			}else if(!check_symbol(current, RIGHT)) return current.toError();
		}
		if(current==null) return eof(input,end);
		return token(input,start,current.end(),list);
	}
}