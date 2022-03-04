package ch.epfl.javelo;

/**
 * Convert numbers between Q28.4 representation and other
 */
public final class Q28_4 {
    private Q28_4(){}

    /**
     * @param i input integer
     * @return (int) value in Q28.4 representation for i (shifted by 4 decimal places left)
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
        //divide by 2^4 to obtain the decimal number representation in Q28_4 (decimal point before 4th bit)
        //Is this correct? What about negative values?
        return Math.scalb(q28_4, -4);
    }

    /**
     * Converts the integer to Q28.4 representation as float
     * @param q28_4 integer input
     * @return (float) Q28.4 representation
     */
    public static float asFloat(int q28_4){
        return Math.scalb(q28_4, -4);
    }
}
