package test.object;

import java.io.IOException;

import nsgl.generic.array.Vector;
import nsgl.language.Token;
import nsgl.language.Typed;
import nsgl.language.TypedValue;

public class ObjectMeaner implements nsgl.language.Meaner<Object>{
	
	public ObjectMeaner() {}
	
	@Override
	public Object apply(Typed obj) throws IOException {
		if( obj instanceof Token ) return ((Token)obj).value();
		@SuppressWarnings("unchecked")
		TypedValue<Vector<Typed>> tv = (TypedValue<Vector<Typed>>)obj;
		Vector<Object> v = new Vector<Object>();
		for( Typed t:tv.value() ) v.add(apply(t));
		return v;
	}	
}
