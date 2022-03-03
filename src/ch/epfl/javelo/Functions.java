package ch.epfl.javelo;

import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/**
 * This class implements multiple mathematical functions
 */
public final class Functions {
    private Functions() {}

    /**
     *
     * @param y : constant which is the output of the function to be returned
     * @return a constant function
     */
    public static DoubleUnaryOperator constant(double y) {
        return (x) -> y;
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    //Make Sampled a record (more concise)
    private static final record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator{
        @Override
        public double applyAsDouble(double operand) {
            //Not using interpolation method? (I assume because (0,y0),(1,y1) ?)

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


                //x values of the samples
                double x_lower = lower_index * intervalLength;
                double x_upper = upper_index * intervalLength;


                //calculating the slope and y-intercept needed of the function needed to compute the y-value of the operand
                double slope = (samples[upper_index] - samples[lower_index])/(x_upper - x_lower);
                double y_intercept = samples[upper_index] - slope * x_upper;
              //  System.out.println(y_intercept);
                return Math.fma(slope, operand , y_intercept);
            }

        }
    }

}
