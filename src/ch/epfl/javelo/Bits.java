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
        //conditions start<=31 length<= 31-start
        Preconditions.checkArgument(start <= 31 && length <= (31-start)+1 && length >= 0);
        // Preconditions.checkArgument(start>= 0 && start <= 31 && length <= 32 - start && length >= 0)
        // Wrote it 31-start +1 for it to be more clear why it's 32, length >=0 modification correct.
        value =  value << 31-start; //shift left to make start the first bit.  value = value << 31 - (start+length-1)/ why?
        value = value >> 31 - (length - 1); //shift right (signed) . value >> 31 - (length - 1) // You are correct
        return value;
    }

    /**
     * @param value initial integer value
     * @param start start bit index
     * @param length length of the extracted bit string
     * @return (int) the resulting unsigned bit string
     */
    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(start >= 0 && start <= 31
                && length <= (31-start)+1 && length < 32 && length >= 0);
        value =  value << 31-start; //shift left to make start the first bit.  value = value << 31 - (start+length-1) / again why?
        value = value >>> 31 - (length - 1); //shift right (unsigned).   "value >> 31 - (length - 1)" / Correct
        return value;
    }

}
