package nsgl.language;

public class TypedValue<T> extends Typed{
	protected T value;
	
	public TypedValue( String type, T value ){
		super(type);
		this.value = value;
	}
	
	public T value(){ return value; }

	public void setValue( T value ){ this.value = value; }	
}