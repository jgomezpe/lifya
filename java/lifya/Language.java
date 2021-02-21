package lifya;

public class Language<T> implements Read<T>{
	protected Lexer lexer;
	protected SyntacticParser parser;
	protected Meaner meaner;

	public Language( Lexer lexer, SyntacticParser parser, Meaner meaner ){
		this.lexer = lexer;
		this.parser = parser;
		this.meaner = meaner;
	}
	
	@Override
	public Token match(Source input, int start, int end) {
	    lexer.init(input, start, end);
	    Token t = parser.analize(lexer);
	    if(!t.isError()) t = meaner.apply(t);
	    return t;
	}
	
	public Meaner meaner() { return meaner; }
	
	public SyntacticParser parser() { return parser; }
	
	public Lexer lexer() { return lexer; }
}