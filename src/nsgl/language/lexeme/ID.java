package nsgl.language.lexeme;

import java.io.IOException;

import nsgl.character.CharacterSequence;
import nsgl.parse.Regex;

public class ID extends Regex{
	public static final String TAG = "id";

	public ID(){ super( "_*[[a-zA-Z]\\w*", TAG); }		
	
	@Override
	public Object instance(CharacterSequence input, String matched) throws IOException {
	    String m = match(new CharacterSequence(matched));
	    if( m!=null ) return matched;
	    throw input.exception("·Invalid "+TAG+"· ", 0);
	}    
}