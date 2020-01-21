package nsgl.language;

public class Token extends TypedValue<String>{
	public static final char EOF = '\0';	
	protected int pos;
	
	public Token( int pos ){ this(EOF,pos); }
	
	public Token( char type, int pos ){ this( type, pos, ""); }

	public Token( char type, int pos, String value ){
		super( type, value );
		this.pos = pos;
	}	
	
	public int shift(int delta ) { return pos += delta; }
	public int pos() { return pos; }
	public int length() { return value.length(); }

	public String toString() { return ""+pos+":"+type+":"+value; }
}