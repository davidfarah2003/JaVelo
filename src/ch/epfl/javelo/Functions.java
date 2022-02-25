package ch.epfl.javelo;

import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Functions {
    private Functions() {}

    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    private static final class Constant implements DoubleUnaryOperator {
        double y;

        Constant(double y) {
            this.y = y;
        }

        @Override
        public double applyAsDouble(double operand) {
            return y;
        }
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        if (samples.length < 2 || xMax <= 0){
            throw new IllegalArgumentException();
        }
        return new Sampled(samples, xMax);
    }

    private static final class Sampled implements DoubleUnaryOperator{
        float[] samples;
        double xMax;

        Sampled(float[] samples, double xMax){
            this.samples = samples;
            this.xMax = xMax;
        }
        @Override
        public double applyAsDouble(double operand) {
            if (operand > xMax) {
                return samples[samples.length - 1];
            } else if (operand < 0) {
                return samples[0];
            } else {
                double interval = (samples.length - 1) / xMax; //* nb intervalles

                double roundedValue = Math.round(operand);
                return 0;
            }

        }
    }

}
