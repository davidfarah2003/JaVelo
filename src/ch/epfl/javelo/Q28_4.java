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
    static public int ofInt(int i){
        i = i << 4;
        return i;
    }

    /**
     * Converts the integer to Q28.4 representation
     * @param q28_4
     * @return (double) Q28.4 representation
     */
    double asDouble(int q28_4){
        //divise le int par 2^4 pour obtenir le nombre a virgule dans notre convention choisie (virgule avant 4eme bits)
        return Math.scalb(q28_4, -4);
    }

    /**
     * Converts the integer to Q28.4 representation
     * @param q28_4
     * @return (float) Q28.4 representation
     */
    float asFloat(int q28_4){
        return Math.scalb(q28_4, -4);
    }
}
