package ch.epfl.javelo;

/**
 * Q28_4
 * Convert numbers between Q28.4 representation and other ones.
 * @author Wesley Nana Davies(344592)
 * @author David Farah (341017)

 */
public final class Q28_4 {
    private Q28_4(){}

    /**
     * Returns the Q28.4 representation of the given input
     * (shifted by 4 decimal places left)
     * @param i input integer
     * @return i (int)
     */
    public static int ofInt(int i){
        i = i << 4;
        return i;
    }

    /**
     * Converts the integer to Q28.4 representation as double
     * @param q28_4 integer input
     * @return (double) Q28.4 representation
     */
    public static double asDouble(int q28_4){
        return Math.scalb((double)q28_4, -4);
    }

    /**
     * Converts the integer to Q28.4 representation as float
     * @param q28_4 integer input
     * @return (float) Q28.4 representation
     */
    public static float asFloat(int q28_4){
        return Math.scalb((float)q28_4, -4);
    }
}
