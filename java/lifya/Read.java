package lifya;

import java.io.IOException;

public interface Read<T> {
	
    @SuppressWarnings("unchecked")
    default T get(Source input, int start, int end) throws IOException{
	Token t = match(input,start,end);
	if(t.isError()) throw new IOException(t.stringify());
	return (T)t.value();
	
    }
 
    default T get(String input, int start, int end) throws IOException{
	return get(new Source(input), start, end);
    }
	
    default T get(String input, int start) throws IOException {
	return get(input, start, input.length());
    }
	
    default T get(String input) throws IOException {
	return get(input, 0);
    }
	
    Token match(Source input, int start, int end);
	
    default Token match(String input, int start, int end) {
	return match(new Source(input), start, end);
    }
	
    default Token match(String input, int start) {
	return match(input, start, input.length());
    }
	
    default Token match(String input) {
	return match(input, 0);
    }	
}