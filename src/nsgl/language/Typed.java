package nsgl.language;

public class Typed {
	protected String type;
	
	public Typed( String type ){ this.type = type; }
	
	public String type(){ return type; }
	
	public void setType( String type ){ this.type = type; }
}