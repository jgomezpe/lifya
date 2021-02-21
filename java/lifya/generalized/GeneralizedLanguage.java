package lifya.generalized;

import java.io.IOException;

import lifya.Language;
import lifya.Lexer;
import lifya.Meaner;
import lifya.SyntacticParser;
import lifya.Token;

public class GeneralizedLanguage<T,S> extends Language<T>{
	protected Encoder<S> encoder;
	public GeneralizedLanguage(Encoder<S> encoder, Lexer lexer, SyntacticParser parser, Meaner meaner) {
		super(lexer, parser, meaner);
		this.encoder = encoder;
	}
	
	public Token match( Iterable<S> reader ) throws IOException{
		StringBuilder sb = new StringBuilder();
		for(S c:reader) sb.append(encoder.encode(c));
		String s = sb.toString();
		return match( s, 0, s.length() ); 
	}
}