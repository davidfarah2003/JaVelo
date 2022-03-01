package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

    private void printBinary(int x){
        System.out.println(Integer.toBinaryString(x));
    }

    @Test
    void ofInt() {
        assertEquals(80, Q28_4.ofInt(5));
        assertEquals(0, Q28_4.ofInt(0));

        int x = Integer.MAX_VALUE & (0b00001 << 27);
        assertEquals(-2147483648, Q28_4.ofInt(x));
    }

    @Test
    void asDouble() {
        assertEquals(2, Q28_4.asDouble(32));
        assertEquals(1.25, Q28_4.asDouble(20));
        assertEquals(1.0625, Q28_4.asDouble(17), 0.0001);

    }

    @Test
    void asFloat() {
        assertEquals(2, Q28_4.asFloat(32));
        assertEquals(1.25, Q28_4.asFloat(20));
        assertEquals(1.0625, Q28_4.asFloat(17), 0.0001);
    }
}