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

import java.util.HashMap;

import lifya.Lexer;
import lifya.SyntacticParser;
import lifya.Token;

/**
 * <p>Look a Head parser. Checks the next Token in the token list to determine the Rule to use </p>
 *
 */
public class LAHParser implements SyntacticParser{
	protected HashMap<String, Rule> rule = new HashMap<String, Rule>();
	protected String main;
    
	/**
	 * Create a look a head syntactic parser with the given trial set of rules
	 * @param rules Rules defining the syntactic parser
	 * @param main Type of the main rule
	 */
	public LAHParser(Rule[] rules, String main) {
		this.main = main;
		for(Rule r:rules) {
			rule.put(r.type(),r);
			r.parser = this;
		}	    
	}

	/**
	 * Sets the type of the main rule
	 * @param rule Type of the main rule
	 * @return Main syntactic rule 
	 */
	public Rule rule(String rule) { return this.rule.get(rule); }

	/**
	 * Gets the type of the main rule
	 * @return Type of the main rule
	 */
	public String main(){ return main; }
    
	/**
	 * Sets the type of the main rule
	 * @param rule Type of the main rule
	 */
	public void main(String rule) { this.main = rule; }
	
	/**
	 * Gets a syntactic token from the given lexer/tokenizer using the type of rule provided 
	 * @param rule Type of the analyzing rule 
	 * @param lexer Lexer to analyze
	 * @return Syntactic token from the given lexer/tokenizer using the type of rule provided 
	 */
	public Token analyze(String rule, Lexer lexer) {
		Rule r = this.rule(rule);
		return r.analyze(lexer);
	}

	/**
	 * Gets a syntactic token from the given lexer/tokenizer (uses the main rule)
	 * @param lexer Lexer to analyze
	 * @return Syntactic token from the given lexer/tokenizer
	 */
	@Override
	public Token analyze(Lexer lexer) { return analyze(main,lexer); }
}