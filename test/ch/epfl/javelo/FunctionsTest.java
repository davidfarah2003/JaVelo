package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {

    @Test
    void constantMethodWorks() {
        DoubleUnaryOperator test = Functions.constant(5);
        assertEquals(5, test.applyAsDouble(10));
    }

    @Test
    void sampledThrowsExceptionWhenASingleIsProvided() {
        float[] samples = {(float) 45.76};
        assertThrows(IllegalArgumentException.class, () -> Functions.sampled(samples, 67));
    }

    @Test
    void sampledThrowsExceptionWhenANegativeValueIsProvided(){
        float[] samples = {(float) 45.76, (float)58,90};
        assertThrows(IllegalArgumentException.class, () -> Functions.sampled(samples, 0));
    }

    @Test
    void sampledWorksForSampleValues(){
        float[] samples = {(float) 45.76, (float) 45.50, (float) 78.90};
        DoubleUnaryOperator test = Functions.sampled(samples, 2.0);
        double value = test.applyAsDouble(2.0);
        assertEquals(samples[2], value, 1e-4);

    }

    @Test
    void sampledWorksForNonSampleValues(){
        float[] samples = {(float) 3, (float) 1, (float) 4};
        DoubleUnaryOperator test = Functions.sampled(samples, 2.0);
        System.out.println(test.applyAsDouble(0.5));
        assertEquals(2, test.applyAsDouble(0.5),1e-4);

    }

    @Test
    void sampledWorksForNonValuesOutsideOfTheRange(){
        float[] samples = {(float) 45.76, (float) 45.50, (float) 78.90};
        DoubleUnaryOperator test = Functions.sampled(samples, 2.0);
        assertEquals(samples[2], test.applyAsDouble(4), 1e-4);

    }

    @Test
    void sampledWorksForNonValuesOutsideOfTheRange2(){
        float[] samples = {(float) 45.76, (float) 45.50, (float) 78.90};
        DoubleUnaryOperator test = Functions.sampled(samples, 2.0);
        assertEquals(samples[0], test.applyAsDouble(-4), 1e-4);
    }
}