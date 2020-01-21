package nsgl.language.lexeme;

import java.io.IOException;
import java.util.regex.Matcher;

import nsgl.exception.IO;
import nsgl.language.Lexeme;
import nsgl.object.parser.ObjectLexer;
import nsgl.recover.RegexRecover;

public class SpaceLexeme extends Lexeme{
	public SpaceLexeme(){ this(ObjectLexer.SPACE); }
	
	public SpaceLexeme(char type){ 
		super(type, new RegexRecover("\\s") {
			
			@Override
			public Object instance(String input) throws IOException {
				Matcher matcher = pattern.matcher(input); 
				if( matcher.find() && matcher.group().length()==input.length() ) return ' ';
				if( matcher.start()!=0)	throw IO.exception(IO.UNEXPECTED, input.substring(0, matcher.start()), 0);
				int s = matcher.group().length();
				throw IO.exception(IO.UNEXPECTED, input.substring(s), s);
			}
		}); 
	}	
}