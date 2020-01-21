package nsgl.language.lexeme;

import java.io.IOException;
import java.util.regex.Matcher;

import nsgl.exception.IO;
import nsgl.language.Lexeme;
import nsgl.object.parser.ObjectLexer;
import nsgl.recover.RegexRecover;

public class IDLexeme extends Lexeme{

	public IDLexeme(){
		super(ObjectLexer.WORD, new RegexRecover("_*[[a-zA-Z]\\w*") {		
			@Override
			public Object instance(String input) throws IOException {
				Matcher matcher = pattern.matcher(input); 
				if( matcher.find() && matcher.group().length()==input.length() ) return input;
				throw IO.exception(IO.UNEXPECTED, input.charAt(matcher.group().length()), matcher.group().length());
			}
		} ); 
	}
}