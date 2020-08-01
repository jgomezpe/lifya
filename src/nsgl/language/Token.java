package nsgl.language;

import java.io.IOException;

import nsgl.character.CharacterSequence;

public class Token extends TypedValue<Object>{
	public static final String EOF = "";	
	protected int[] pos;
	protected int location;
	protected String owner;
	protected String matched;
	
	public Token( String type, String matched, Object value, CharacterSequence input, int pos ){
		super( type, value );
		this.pos = input.absolute_pos(pos);
		this.owner = input.description();
		this.location = input.loc()+pos;
		this.matched = matched;
	}	
	
	public int[] pos() { return pos; }
	
	public int location() { return location; }
	
	public void shift(int delta) { location+=delta; }
	
	public String owner() { return owner; }
	
	public String matched() { return matched; } 
	
	public int length() { return matched.length(); }

	public String toString() { return ""+pos[0]+":"+pos[1]+":"+type+":"+value; }
	
	public IOException exception( String code) {
	    return CharacterSequence.exception(code, pos, owner);
	}
}