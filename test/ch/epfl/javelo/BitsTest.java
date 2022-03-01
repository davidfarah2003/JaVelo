package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitsTest {

    private void printBinary(int x){
        System.out.println(Integer.toBinaryString(x));
    }

    @Test
    void extractSigned() {
        int x = 0b01110100110;
        //printBinary(x);
       assertEquals(0b11111111111111111111111111111001, Bits.extractSigned(x, 2, 4));
       assertEquals(0b001, Bits.extractSigned(x, 2, 3));
       assertEquals(0, Bits.extractSigned(x, 31, 1));
       assertEquals(0, Bits.extractSigned(x,0,0));
       assertEquals(0b01110100110, Bits.extractSigned(x,0,32));
    }

    @Test
    void extractSignedExceptions(){
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractSigned(0, -1, 1);});
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractSigned(0, 0, -1);});
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractSigned(4, 5, 31);});
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractSigned(0, 32, 0);});
    }

    @Test
    void extractUnsigned() {
        int x = 0b01110100110;
        assertEquals(0b1001, Bits.extractUnsigned(x, 2, 4));
        assertEquals(0b111, Bits.extractUnsigned(x, 7, 3));
        assertEquals(0, Bits.extractUnsigned(x, 31, 1));
        assertEquals(0, Bits.extractUnsigned(x,0,0));
        assertEquals(0b01110100110, Bits.extractUnsigned(x,0,31));
        assertEquals(0b01111111111111111111110001011010, Bits.extractUnsigned(-x,0,31));
    }

    @Test
    void extractUnsignedExceptions(){
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractUnsigned(0, -1, 1);});
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractUnsigned(0, 0, -1);});
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractUnsigned(4, 5, 30);});
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractUnsigned(0, 0, 32);});
        assertThrows(IllegalArgumentException.class, () -> {Bits.extractUnsigned(0, 32, 0);});
    }
}