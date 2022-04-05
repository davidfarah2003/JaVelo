package ch.epfl.javelo;

/**
 * Class that provides bit Extraction methods
 * @author David Farah (341017)
 * @author Wesley Nana Davies(344592)
 */
public final class Bits {
    private Bits(){}

    /**
     * Extract bit String from <code>value</code> from <code>start</code> bit index with a length of <code>length</code>
     * (from LSB to MSB, to the left), interpreted as a signed integer
     *
     * @param value initial integer value
     * @param start start bit index (right)
     * @param length length of the extracted bit string
     * @return (int) the resulting signed bit string
     * @throws IllegalArgumentException if start < 0 or length is invalid (out of bounds or <= 0)
     */
    public static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(start>= 0 && start <= 31 && length <= (31-start)+1 && length > 0);

        value =  value << 32 - (start+length); //shift left to make start the first bit. working with indexes
        value = value >> 32 - length; //shift right (signed) . value >> 31 - (length - 1) // You are correct
        return value;
    }

    /**
     * Extract bit String from <code>value</code> from <code>start</code> bit index with a length of <code>length</code>
     * (from LSB to MSB, to the left), interpreted as an unsigned integer
     *
     * @param value initial integer value
     * @param start start bit index (right)
     * @param length length of the extracted bit string
     * @return (int) the resulting unsigned bit string
     * @throws IllegalArgumentException if start < 0 or length is invalid (out of bounds or <= 0)
     */
    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 && start <= 31 && length <= (31-start)+1 && length < 32 && length > 0);
        value =  value << 32-(start+length); //shift left to make start the first bit.
        value = value >>> 32 - length;
        return value;
    }
}
