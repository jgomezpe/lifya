package lifya.lookahead;

import java.util.HashMap;

import lifya.Token;

public class LAHParser implements lifya.SyntacticParser{
    protected HashMap<String, Rule> rule = new HashMap<String, Rule>();
    protected String main;
    
    public LAHParser(Rule[] rules, String main) {
	this.main = main;
	for(Rule r:rules) {
	    rule.put(r.type(),r);
	    r.parser = this;
	}
	    
    }

    public Rule rule(String rule) { return this.rule.get(rule); }

    public String main(){ return main; }
    public void main(String rule) { this.main = rule; }
	
    public Token analize(String rule, lifya.Lexer lexer) {
	Rule r = this.rule(rule);
	return r.analize(lexer);
    }

    @Override
    public Token analize(lifya.Lexer lexer) {
	return analize(main,lexer);
    }
}
