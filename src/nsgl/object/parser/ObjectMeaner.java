package nsgl.object.parser;

import nsgl.generic.array.Vector;
import nsgl.language.LexemeSet;
import nsgl.language.Meaner;
import nsgl.language.Token;
import nsgl.language.Typed;
import nsgl.language.TypedValue;

public class ObjectMeaner implements Meaner<Object>{
	protected LexemeSet lexemes;
	
	public ObjectMeaner(LexemeSet lexemes) { this.lexemes = lexemes; }
	
	@Override
	public Object apply(Typed obj) throws Exception {
		if( obj instanceof Token ) return lexemes.map((Token)obj);
		@SuppressWarnings("unchecked")
		TypedValue<Vector<Typed>> tv = (TypedValue<Vector<Typed>>)obj;
		Vector<Object> v = new Vector<Object>();
		for( Typed t:tv.value() ) v.add(apply(t));
		return v;
	}	
}
