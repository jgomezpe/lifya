package nsgl.language.lexeme;

import java.io.IOException;

import nsgl.character.CharacterSequence;
import nsgl.parse.Regex;

public class Space extends Regex{
	public static final String TAG = "space";

	public Space(){ this(TAG); }
	
	public Space(String type){ super("\\s",type); }
	
	@Override
	public Object instance(CharacterSequence input, String matched) throws IOException {
	    String m = match(new CharacterSequence(matched));
	    if( m!=null ) return " ";
	    throw input.exception("·Invalid "+TAG+"· ", 0);
	}
}