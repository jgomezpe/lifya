package nsgl.language;

import nsgl.generic.array.Vector;

public class ParserRule extends Typed{
	protected int min = 1;
	protected int max = 1;
	protected Vector<Token> syntagme;
	
	public ParserRule(String name, Vector<Token> syntagme, int min, int max ) {
		super(name);
		this.syntagme = syntagme;
		this.min = min;
		this.max = max;
	}
	
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(type());
		sb.append("->");
		for( Token t:syntagme ) sb.append(t.toString()+"#");
		sb.append(":"+min+","+max);
		return sb.toString();
	}
}
