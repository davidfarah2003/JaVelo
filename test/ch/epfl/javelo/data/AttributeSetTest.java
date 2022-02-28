package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {
    private void printBinary(int x){
        System.out.println(Integer.toBinaryString(x));
    }

    @Test
    void AttributeSetException() {
        assertThrows(IllegalArgumentException.class, () -> {new AttributeSet(0b11L << 62);});
        assertThrows(IllegalArgumentException.class, () -> {new AttributeSet(0b10L << 62);});
        assertThrows(IllegalArgumentException.class, () -> {new AttributeSet(0b01L << 62);});
        assertDoesNotThrow(() -> {new AttributeSet(~(0b11L << 62));});
    }

    @Test
    void ofAndContains() {
        AttributeSet attrSet = AttributeSet.of();
        //System.out.println(attrSet);
        attrSet = AttributeSet.of(Attribute.ONEWAY_M1, Attribute.BICYCLE_NO, Attribute.ACCESS_YES);
        assertTrue(attrSet.contains(Attribute.ONEWAY_M1) && attrSet.contains(Attribute.BICYCLE_NO)
                && attrSet.contains(Attribute.ACCESS_YES));
        //System.out.println(attrSet);
    }

    @Test
    void intersects() {
        AttributeSet attrSet1 = AttributeSet.of(Attribute.ONEWAY_M1, Attribute.BICYCLE_NO, Attribute.ACCESS_YES);
        AttributeSet attrSet2 = AttributeSet.of(Attribute.CYCLEWAY_OPPOSITE, Attribute.ACCESS_YES);
        assertTrue(attrSet1.intersects(attrSet2));
        assertTrue(attrSet2.intersects(attrSet2));

        attrSet2 = AttributeSet.of(Attribute.CYCLEWAY_OPPOSITE, Attribute.ONEWAY_BICYCLE_YES);
        assertFalse(attrSet1.intersects(attrSet2));

        attrSet1 = AttributeSet.of();
        assertFalse(attrSet1.intersects(attrSet2));

        attrSet2 = AttributeSet.of();
        assertFalse(attrSet2.intersects(attrSet1));
    }
}