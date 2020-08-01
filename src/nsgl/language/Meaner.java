package nsgl.language;

import java.io.IOException;

public interface Meaner<T> {
	public T apply( Typed g_obj ) throws IOException;
}