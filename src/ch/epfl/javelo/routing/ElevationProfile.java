package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

public final class ElevationProfile {
    private double length;
    private float[] elevationSamples;


    /**
     * Constructor of the class
     * @param length
     * @param elevationSamples
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length > 2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }


    /**
     *
     * @return the length of the profile (meters)
     */
    public double length(){
        return length;
    }

    /**
     *
     * @return the statistics of the profile with altitudes
     */
    private DoubleSummaryStatistics statistics(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float number : elevationSamples){
            s.accept(number);
        }
        return s;
    }

    /**
     *
     * @return the minimum value among the altitudes
     */
    public double minElevation(){
       return statistics().getMin();
    }

    /**
     *
     * @return the maximum value among the altitudes
     */
    public double maxElevation(){
        return statistics().getMax();
    }

    /**
     *
     * @return the sum of all positive differences between a sample and its predecessor.
     */
    public double totalAscent(){
        double ascent = 0.0;
        for (int i = 0; i < elevationSamples.length - 1; i++){
            double difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference > 0){
                ascent +=  difference;
            }
        }
        return ascent;
    }

    /**
     *
     * @return the absolute value of the sum of all negative differences between a sample and its predecessor.
     */
    public double totalDescent(){
        double descent = 0.0;
        for (int i = 0; i < elevationSamples.length - 1; i++){
            double difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference < 0){
                descent +=  difference;
            }
        }
        return Math.abs(descent);
    }

    /**
     *
     * @param position (in meters)
     * @return the value of the function sampled (class Function) applied for the x-input position.
     */
    public double elevationAt(double position){
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }


}
