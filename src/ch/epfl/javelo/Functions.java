package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Functions
 * This class implements multiple mathematical functions
 *
 * @author David Farah (341017)
 * @author Wesley Nana Davies(344592)
 */
public final class Functions {
    private Functions() {}

    /**
     * Returns a constant function
     * @param y : constant which is the output of the function to be returned
     * @return a constant function
     */
    public static DoubleUnaryOperator constant(double y) {
        return x-> y;
    }


    /**
     * Returns a function obtained by linear interpolation between samples,
     * regularly spaced and covering the range from 0 to xMax
     * @param samples
                array of samples to make the function out of it
     * @param xMax
                end of the range
     * @throws IllegalArgumentException if the size of the samples array is not
     * greater or equal to 2 or if xMax is negative
     * @return the function
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    private static final record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator{
        @Override
        public double applyAsDouble(double operand) {

            if (operand > xMax) {
                return samples[samples.length - 1];
            } else if (operand < 0) {
                return samples[0];
            } else {
                double intervalLength = xMax / (samples.length - 1); // length of an interval (between each sample)
                //if operand is on a sample, return the corresponding sample y value
                if (operand % intervalLength == 0){
                    return samples[(int)(operand/ intervalLength)];
                }

                //get the 2 sample indexes that are closest to the operand
                double operand_index = operand / intervalLength;
                int lower_index = (int) Math.floor(operand_index);
                int upper_index = (int) Math.ceil(operand_index);

                return Math2.interpolate(samples[lower_index], samples[upper_index],
                        (operand - lower_index * intervalLength)/intervalLength);

            }

        }
    }

}
