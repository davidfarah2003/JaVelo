package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

public final class ElevationProfileComputer {
    private static float[] profile;
    private static double intervalLength;

    private ElevationProfileComputer() {
    }

    /**
     *
     * @param route
     * @param maxStepLength
     * @return
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        int numberOfSamples = 1 + (int) Math.ceil(route.length() / maxStepLength);
        intervalLength = route.length() / (numberOfSamples - 1);
        profile = new float[numberOfSamples];
        Preconditions.checkArgument(intervalLength <= maxStepLength && intervalLength > 0);

        fillInitialArray(route);
        int indexFirstNumber = firstNumberIndex();

        if(indexFirstNumber == profile.length) {
            Arrays.fill(profile, 0);
        }
        else {
            Arrays.fill(profile, 0, indexFirstNumber, profile[indexFirstNumber]);
            int indexFirstNumberEnd = fillNanEnd();
            fillIntermediateNanValues(indexFirstNumber, indexFirstNumberEnd);
        }

        return new ElevationProfile(route.length(), profile);
    }

    /**
     * Fill the array with the initial values from the route
     * @param route in question
     */
    private static void fillInitialArray(Route route) {
        for (int sample = 0; sample < profile.length; sample++) {
            profile[sample] = (float) route.elevationAt(sample * intervalLength);
        }
    }


    /**
     * @return the index of the first real number
     */
    private static int firstNumberIndex(){
        int indexFirstNumber = 0;
        while ((indexFirstNumber < profile.length && Float.isNaN(profile[indexFirstNumber]))) {
            indexFirstNumber++;
        }
        return indexFirstNumber;
    }


    /**
     * @return the index of the first real number starting from the end of the array
     */
    private static int fillNanEnd(){
        int indexFirstNumberEnd = profile.length - 1;
        while (Float.isNaN(profile[indexFirstNumberEnd])) {
            indexFirstNumberEnd--;
        }
        Arrays.fill(profile, indexFirstNumberEnd + 1, profile.length, profile[indexFirstNumberEnd]);
        return indexFirstNumberEnd;
    }


    /**
     * Fill the remaining NaN values in the array
     * @param startIndex index where we start on the array
     * @param endIndex index where we stop on the array
     */
    private static void fillIntermediateNanValues(int startIndex, int endIndex){
        int firstNanIndex = startIndex;
        int RealNumberAfterIndex;
        int RealNumberBeforeIndex;

        while (firstNanIndex < endIndex) {
            firstNanIndex++;

            //skip index if it's a real number
            if (!Float.isNaN(profile[firstNanIndex])) {
                continue;
            }

            RealNumberBeforeIndex = firstNanIndex - 1;
            RealNumberAfterIndex = firstNanIndex + 2;

            while (Float.isNaN(profile[RealNumberAfterIndex])) {
                RealNumberAfterIndex++;
            }

            for(int NanIndex = firstNanIndex + 1; NanIndex < RealNumberAfterIndex; NanIndex++) {
                profile[NanIndex] = (float) Math2.interpolate(
                        profile[RealNumberBeforeIndex],
                        profile[RealNumberAfterIndex],
                        (double)(NanIndex - RealNumberBeforeIndex) / (RealNumberAfterIndex - RealNumberBeforeIndex)
                );
            }

        }
    }

}






