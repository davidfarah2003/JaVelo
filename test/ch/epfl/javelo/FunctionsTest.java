package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
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
    void sampledThrowsExceptionWhenANegativeValueIsProvided() {
        float[] samples = {(float) 45.76, (float) 58, 90};
        assertThrows(IllegalArgumentException.class, () -> Functions.sampled(samples, 0));
    }

    @Test
    void sampledWorksForSampleValues() {
        float[] samples = {(float) 45.76, (float) 45.50, (float) 78.90};
        DoubleUnaryOperator test = Functions.sampled(samples, 100);
        double value = test.applyAsDouble(50.0);
        assertEquals(samples[1], value, 1e-4);

    }

    @Test
    void sampledWorksForNonSampleValues() {
        float[] samples = {(float) 4, (float) 45.67, (float) 30.5};
        DoubleUnaryOperator test = Functions.sampled(samples, 100);
        assertEquals((45.67 + 4) / 2, test.applyAsDouble(25), 1e-4);
        // 43.94972037658691

    }

    @Test
    void sampledWorksForNonValuesOutsideOfTheRange() {
        float[] samples = {(float) 45.76, (float) 45.50, (float) 78.90};
        DoubleUnaryOperator test = Functions.sampled(samples, 2.0);
        assertEquals(samples[2], test.applyAsDouble(4), 1e-4);

    }

    @Test
    void sampledWorksForNonValuesOutsideOfTheRange2() {
        float[] samples = {(float) 45.76, (float) 45.50, (float) 78.90};
        DoubleUnaryOperator test = Functions.sampled(samples, 2.0);
        assertEquals(samples[0], test.applyAsDouble(-4), 1e-4);
    }

    @Test
    void functionsSampledInterpolatesBetweenSamples() {
        var rng = newRandom();
        var halfWidth = 5000;
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 20);
            var samples = new float[sampleCount];
            for (int j = 0; j < sampleCount; j += 1)
                samples[j] = rng.nextFloat(-halfWidth, halfWidth);
            var xMax = rng.nextDouble(50, 100);
            var f = Functions.sampled(samples, xMax);
            var interSampleDistance = xMax / (sampleCount - 1);
            var minDeltaX = interSampleDistance / 4;
            for (int j = 1; j < sampleCount; j += 1) {
                var xL = (j - 1) * interSampleDistance;
                var yL = samples[j - 1];
                var xR = j * interSampleDistance;
                var yR = samples[j];
                var x = rng.nextDouble(xL + minDeltaX, xR - minDeltaX);
                var y = f.applyAsDouble(x);
                var expectedSlope = (yR - yL) / interSampleDistance;
                var actualSlope = (y - yL) / (x - xL);
                assertEquals(expectedSlope, actualSlope, 1e-3);
            }
        }
    }

    @Test
    void functionsSampledThrowsWithLessThanTwoSamples() {
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(new float[]{}, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(new float[]{0}, 1);
        });
    }

    @Test
    void functionsSampledWorksWhenEvaluatedCloseToXMax() {
        var rng = newRandom();
        var halfWidth = 5000;
        for (int l = 2; l < 40; l += 1) {
            var samples = new float[l];
            for (int i = 0; i < samples.length; i += 1)
                samples[i] = rng.nextFloat(-halfWidth, halfWidth);
            var xMax = rng.nextDouble(l, 4 * l);
            var f = Functions.sampled(samples, xMax);

            assertDoesNotThrow(() -> {
                var xL = xMax;
                var xH = xMax;
                for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
                    var yL = f.applyAsDouble(xL);
                    var yH = f.applyAsDouble(xH);
                    xL = Math.nextDown(xL);
                    xH = Math.nextUp(xH);
                }
            });

        }
    }
}