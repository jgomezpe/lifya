package nsgl.language;

public class Typed {
	protected char type;
	
	public Typed( char type ){ this.type = type; }
	
	public char type(){ return type; }
	
	public void setType( char type ){ this.type = type; }
}