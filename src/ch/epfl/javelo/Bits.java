package ch.epfl.javelo;

public final class Bits {
    private Bits(){}

    /**
     * @param value initial integer value
     * @param start start bit index
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
     * @param value initial integer value
     * @param start start bit index
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
