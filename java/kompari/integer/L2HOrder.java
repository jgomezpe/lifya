package kompari.integer;

/**
 * <p>Compares two Integer</p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 * 
 * @author Jonatan Gomez Perdomo
 * @version 1.0
 */

public class L2HOrder implements Order{
    /**
     * Determines if the first Double is less than (in some order) the second Double (one<two)
     * @param one First Double
     * @param two Second Double
     * @return (one<two)
     */
    public int compare(int one, int two){ return (one-two); }
}