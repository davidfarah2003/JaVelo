package ch.epfl.javelo;

/**
 * Bits
 * This class offers methods which help to manipulate bit strings
 *
 * @author Wesley Nana Davies(344592)
 * @author David Farah (341017)
 *

 */

public final class Bits {
    private Bits(){}

    /**
     * Returns the bit string extracted signed
     * @param value initial integer value
     * @param start start bit index (right)
     * @param length length of the extracted bit string
     * @return (int) the resulting signed bit string
     */
    public static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(start>= 0 && start <= 31 && length <= (31-start)+1 && length > 0);

        value =  value << 32 - (start+length); //shift left to make start the first bit. working with indexes
        value = value >> 32 - length; //shift right (signed) . value >> 31 - (length - 1) // You are correct
        return value;
    }

    /**
     * Returns the bit string extracted unsigned
     * @param value initial integer value
     * @param start start bit index (right)
     * @param length length of the extracted bit string
     * @return (int) the resulting unsigned bit string
     */
    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 && start <= 31 && length <= (31-start)+1 && length < 32 && length > 0);
        value =  value << 32-(start+length); //shift left to make start the first bit.
        value = value >>> 32 - length;
        return value;
    }
}
