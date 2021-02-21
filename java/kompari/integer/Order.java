package kompari.integer;

public interface Order extends kompari.Order {
    /**
     * Determines if the first Double is less than (in some order) the second Double (one<two)
     * @param one First Double
     * @param two Second Double
     * @return (one<two)
     */
    int compare(int one, int two);

    /**
     * Determines if the first Integer is less than (in some order) the second Integer (one<two)
     * @param one First Integer
     * @param two Second Integer
     * @return (one<two)
     */
    default int compare(Object one, Object two){ return compare((int)one, (int)two); }
}