package ch.epfl.javelo;

import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Functions {
    private Functions() {}

    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    //Make Constant as a record
    private static final record Constant(double y) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double operand) {
            return this.y;
        }
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    //Make Sampled a record (more concise)
    private static final record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator{
        @Override
        public double applyAsDouble(double operand) {
            //Not using interpolation method?
            if (operand > xMax) {
                return samples[samples.length - 1];
            } else if (operand < 0) {
                return samples[0];
            } else {
                double intervalLength = xMax /  (samples.length - 1); //* nb intervalles
                if (operand % intervalLength == 0){
                    return samples[(int)(operand/ intervalLength)];
                }
                double division = operand / intervalLength;
                int lower_index = (int) Math.floor(division);
                int upper_index = (int) Math.ceil(division);
                double x_lower = lower_index * intervalLength;
                double x_upper = upper_index * intervalLength;
                double slope = (samples[upper_index] - samples[lower_index])/(x_upper - x_lower);
                double y_intercept = samples[upper_index] - slope * upper_index;
                return Math.fma(slope, y_intercept , operand);
            }

        }
    }

}
