package ch.epfl.javelo;

public final class Bits {
    private Bits(){}

    public static int extractSigned(int value, int start, int length){
        //conditions start<=31 length<= 31-start
        Preconditions.checkArgument(start <= 31 && length <= (31-start)+1);
        value =  value << 31-start; //shift left to make start the first bit.
        value = value >> length; //shift right (signed)
        return value;
    }

    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(start <= 31 && length <= (31-start)+1 && length < 32);
        value =  value << 31-start; //shift left to make start the first bit.
        value = value >>> length; //shift right (unsigned)
        return value;
    }

}
