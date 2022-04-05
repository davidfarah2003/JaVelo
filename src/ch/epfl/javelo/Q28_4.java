package ch.epfl.javelo;

/**
 * Class that is responsible to convert numbers between Q28.4 representation and other
 * @author David Farah (341017)
 * @author Wesley Nana Davies(344592)
 */
public final class Q28_4 {
    private Q28_4(){}

    /**
     * Convert the bit String of i to Q28.4 representation (shifted by 4 decimal places left) as an int
     * @param i input integer
     * @return (int) value represented in Q28.4
     */
    public static int ofInt(int i){
        i = i << 4;
        return i;
    }

    /**
     * Interprets the Bit String in Q28.4 representation as a double
     * @param q28_4 integer input
     * @return (double) Q28.4 representation
     */
    public static double asDouble(int q28_4){
        return Math.scalb((double)q28_4, -4);
    }

    /**
     * Interprets the Bit String (int) in Q28.4 representation as a float
     * @param q28_4 integer input (Bit String)
     * @return (float) Q28.4 representation
     */
    public static float asFloat(int q28_4){
        return Math.scalb((float)q28_4, -4);
    }
}
