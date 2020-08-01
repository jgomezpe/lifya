package nsgl.language.lexeme;

import java.io.IOException;

import nsgl.character.CharacterSequence;
import nsgl.parse.Regex;

public class Space extends Regex{
	public static final String TAG = "space";

	public Space(){ this(TAG); }
	
	public Space(String type){ super("\\s",type); }
	
	@Override
	protected Object instance(CharacterSequence arg0, String arg1) throws IOException { return " "; }
}