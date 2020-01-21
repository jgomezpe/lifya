package nsgl.object.parser;

import nsgl.generic.array.DynArray;
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
		TypedValue<DynArray<Typed>> tv = (TypedValue<DynArray<Typed>>)obj;
		DynArray<Object> v = new DynArray<Object>();
		for( Typed t:tv.value() ) v.add(apply(t));
		return v;
	}	
}
