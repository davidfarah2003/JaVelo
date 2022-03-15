package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

public final class ElevationProfile {
    private final double length;
    private final float[] elevationSamples;

    private final DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();


    /**
     * Constructor of the class
     * @param length length of the edge
     * @param elevationSamples y coordinates of the samples
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples;

        for (float number : elevationSamples){
            statistics.accept(number);
        }
    }


    /**
     * @return the length of the profile (meters)
     */
    public double length(){
        return length;
    }


    /**
     * @return the minimum value among the altitudes
     */
    public double minElevation(){
       return statistics.getMin();
    }

    /**
     * @return the maximum value among the altitudes
     */
    public double maxElevation(){
        return statistics.getMax();
    }

    /**
     * @return the sum of all positive differences between a sample and its predecessor.
     */
    public double totalAscent(){
        double ascent = 0;
        double difference;

        for (int i = 0; i < elevationSamples.length - 1; i++){
            difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference > 0){
                ascent +=  difference;
            }
        }
        return ascent;
    }

    /**
     * @return the positive value of the sum of all negative differences between a sample and its predecessor.
     */
    public double totalDescent(){
        double descent = 0.0;
        double difference;

        for (int i = 0; i < elevationSamples.length - 1; i++){
            difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference < 0){
                descent +=  difference;
            }
        }
        return -descent;
    }

    /**
     * @param position (in meters)
     * @return the value of the function sampled (class Function) applied for the x-input position.
     */
    public double elevationAt(double position){
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }

}
