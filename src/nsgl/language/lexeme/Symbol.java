package nsgl.language.lexeme;

import java.io.IOException;

import nsgl.character.CharacterSequence;
import nsgl.parse.Regex;

public class Symbol extends Regex{
    	public static final String TAG = "symbol";
	public Symbol(String symbols){ this(symbols,TAG); }
	public Symbol(String symbols, String type){ super("["+symbols+"]", type ); }

	@Override
	public Object instance(CharacterSequence input, String matched) throws IOException {
	    String m = match(new CharacterSequence(matched));
	    if( m!=null ) return matched.charAt(0);
	    throw input.exception("·Invalid "+TAG+"· ", 0);
	}
}