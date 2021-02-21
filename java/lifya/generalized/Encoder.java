package lifya.generalized;

public interface Encoder<T> {
    /**
     * Gets the codification of the given symbol
     * @param symbol Symbol to encode
     * @return Codification of the given symbol
     */
	public char encode(T symbol);
    /**
     * Gets the symbol encoded with number <i>code</i>.
     * @param code Code of the symbol
     * @return T Symbol encoded with number <i>code</i>.
     */
	public T decode( char code );
}