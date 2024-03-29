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
package lifya.generic;

import java.util.HashMap;

import lifya.Tokenizer;
import lifya.generic.rule.Rule;
import speco.array.Array;
import lifya.Parser;
import lifya.Token;

/**
 * <p>Generic parser. Checks the token source, an tries to apply each rule to match the token. </p>
 *
 */
public class GenericParser extends Parser{
	protected HashMap<String, Rule> rule = new HashMap<String, Rule>();
	protected String main;
    
	/**
	 * Create a generic syntactic parser
	 * @param tokenizer Tokenizer
	 * @param rules Rules defining the syntactic parser
	 * @param main Main rule id
	 */
	public GenericParser(Tokenizer tokenizer, Rule[] rules, String main) {
		super(tokenizer);
		this.main = main;
		for(Rule r:rules) add(r);
	    
	}
	
	/**
	 * Create a generic syntactic parser
	 * @param tokenizer Tokenizer
	 * @param rules Rules defining the syntactic parser
	 * @param main Main rule id
	 */
	public GenericParser(Tokenizer tokenizer, Array<Rule> rules, String main) {
		super(tokenizer);
		this.main = main;
		for(Rule r:rules) add(r);	    
	}
	
	/**
	 * Add a rule to the parser
	 * @param rule Rule to add
	 */
	public void add(Rule rule) { 
		this.rule.put(rule.type(),rule); 
		rule.parser(this);
	}

	/**
	 * Sets the main rule
	 * @param rule Id of the main rule
	 * @return Main syntactic rule 
	 */
	public Rule rule(String rule) { return this.rule.get(rule); }

	/**
	 * Determines if the type name represents a rule or not
	 * @param type Type name to analyze
	 * @return <i>true</i> if the type represents a rule, <i>false</i> otherwise. 
	 */
	public boolean isRule(String type) { return rule(type)!=null; }

	/**
	 * Gets the id of the main rule
	 * @return Id of the main rule
	 */
	public String main(){ return main; }
    
	/**
	 * Sets the main rule
	 * @param rule Id of the main rule
	 */
	public void main(String rule) { this.main = rule; }
	
	/**
	 * Gets a syntactic token (derivation tree) using the provided rule
	 * @param rule Rule id
	 * @return Syntactic token generated by the rule 
	 */
	public Token analyze(String rule) {
		Rule r = this.rule(rule);
		return r.analyze(lexer);
	}

	/**
	 * Gets a syntactic token - derivation tree (uses the main rule)
	 * @return Syntactic token - derivation tree
	 */
	@Override
	public Token analyze() { return analyze(main); }
}