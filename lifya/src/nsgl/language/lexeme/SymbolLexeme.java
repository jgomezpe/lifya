package nsgl.language.lexeme;

import java.io.IOException;
import java.util.regex.Matcher;

import nsgl.exception.IO;
import nsgl.language.Lexeme;
import nsgl.object.parser.ObjectLexer;
import nsgl.recover.RegexRecover;

public class SymbolLexeme  extends Lexeme{
	public SymbolLexeme(String symbols){ this(ObjectLexer.SYMBOL,symbols); }
	public SymbolLexeme(char type, String symbols){
		super(type, new RegexRecover("["+symbols+"]") {			
			@Override
			public Object instance(String input) throws IOException {
				Matcher matcher = pattern.matcher(input); 
				if( matcher.find() && matcher.group().length()==input.length() ) return input.charAt(0);
				throw IO.exception(IO.UNEXPECTED, input, 0);
			}
		}); 
	}
}