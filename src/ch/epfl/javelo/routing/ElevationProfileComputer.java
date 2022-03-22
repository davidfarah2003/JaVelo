package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

public final class ElevationProfileComputer {
    private static float[] profile;

    private ElevationProfileComputer() {
    }

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        int numberOfSamples = 1 + (int) Math.ceil(route.length() / maxStepLength);
        double intervalLength = route.length() / (numberOfSamples - 1);
        profile = new float[numberOfSamples];
        Preconditions.checkArgument(intervalLength <= maxStepLength && intervalLength > 0);

        fillInitialArray();
        int indexFirstNumber = firstNumberIndex();

        // assigning each sample an elevation equal to 0 if no valid sample exists
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


    private static void fillInitialArray(){
        int indexFirstNumber = 0;
        while ((indexFirstNumber < profile.length && Float.isNaN(profile[indexFirstNumber]))) {
            indexFirstNumber++;
        }
    }


    private static int firstNumberIndex(){
        int indexFirstNumber = 0;
        while ((indexFirstNumber < profile.length && Float.isNaN(profile[indexFirstNumber]))) {
            indexFirstNumber++;
        }
        return indexFirstNumber;
    }


    private static int fillNanEnd(){
        int indexFirstNumberEnd = profile.length - 1;
        while (Float.isNaN(profile[indexFirstNumberEnd])) {
            indexFirstNumberEnd--;
        }
        Arrays.fill(profile, indexFirstNumberEnd + 1, profile.length, profile[indexFirstNumberEnd]);
        return indexFirstNumberEnd;
    }


    private static void fillIntermediateNanValues(int firstNanIndex, int endIndex){
        int RealNumberAfter;
        int RealNumberBefore;

        while (firstNanIndex < endIndex) {
            firstNanIndex++;

            if (!Float.isNaN(profile[firstNanIndex])) {
                continue;
            }

            RealNumberBefore = firstNanIndex - 1;
            RealNumberAfter = firstNanIndex + 2;

            while (Float.isNaN(profile[RealNumberAfter])) {
                RealNumberAfter++;
            }

            for(int NanIndex = firstNanIndex + 1; NanIndex < RealNumberAfter; NanIndex++) {
                profile[NanIndex] = (float) Math2.interpolate(
                        profile[RealNumberBefore],
                        profile[RealNumberAfter],
                        (double)(NanIndex - RealNumberBefore) / (RealNumberAfter - RealNumberBefore)
                );
            }

        }
    }

}






