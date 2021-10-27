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
package lifya.generic.rule;

import lifya.TokenSource;
import lifya.Token;
import speco.array.Array;

/**
 * <p>A parser rule for selecting from multiple rules</p>
 *
 */
public class DisjointRule extends JoinRule{
	/**
	 * <p>Creates a syntactic disjoint rule for a parser</p>
	 * @param type Type of the rule
	 * @param options Types of the rule components
	 */
	public DisjointRule(String type, String[] options) {
		this(type, options, init(options.length));
	}
	
	/**
	 * <p>Creates a syntactic disjoint rule for a parser. Consider a typical value rule:</p>
	 * <p> &lt;VALUE&gt; :- &lt;id&gt; | &lt;string&gt; | &lt;EXP&gt; </p>
	 * <p>Can be defined using a constructor call like this (arrays notation simplified):</p>
	 * <p><i>new DisjointRule("VALUE", ["id", "string","EXP"], [null,null,null])</i></p>
	 * <p>Here a <i>null</i> value indicates that the associated lexeme can take any value.</p>
	 * @param type Type of the rule
	 * @param options Types of the rule components
	 * @param values Values of the rule components. 
	 */
	public DisjointRule(String type, String[] options, String[] values) {
		super(type, options, values);
	}

	/**
	 * Creates an optional syntactic rule for a parser
	 * @param type Type of the options rule
	 * @param options Optional syntactic rules
	 * @param values Specific values for optional rules
	 */
	public DisjointRule(String type, Array<String> options, Array<String> values) {
		super(type, options, values);
	}

	/**
	 * Determines if the rule can start with the given token
	 * @param t Token to analyze
	 * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
	 */
	@Override
	public boolean startsWith(Token t) {
		boolean flag=false;
		int i=0;
		while(i<type_list.length && !flag) {
			if(parser.isRule(type_list[i])) flag = parser.rule(type_list[i]).startsWith(t);
			else flag = check_lexeme(t, i);
			i++;
		}
		return flag;
	}

	/**
	 * Creates a rule token using the <i>current</i> token as first token to analyze
	 * @param lexer Tokens source 
	 * @return Rule token
	 */
	@Override
	public Token analyze(TokenSource lexer){
		int pos = lexer.pos();
		Token e = null;
		Token t = null;
		for(int i=0; i<type_list.length && t==null; i++) {
			Token current = lexer.current();
			if(parser.isRule(type_list[i]) && parser.rule(type_list[i]).startsWith(current)) {
				t = parser.rule(type_list[i]).analyze(lexer);
				if(t.isError()) {
					if(e==null || e.end() < t.end()) e = t;
					t=null;
					lexer.locate(pos);
				}
			}
			if(check_lexeme(current, i)) {
				lexer.next();
				return current;
			}
		}
		if(t!=null) return t;
		return e;
	}	
}