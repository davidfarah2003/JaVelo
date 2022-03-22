package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;

import java.util.Arrays;

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    static ElevationProfile elevationProfile(Route route, double maxStepLength) {

        int numberOfSamples = 1 + (int) Math.ceil(route.length() / maxStepLength);
       double intervalLength = route.length() / (numberOfSamples - 1);
        float[] profile = new float[numberOfSamples];

        for (int sample = 0; sample < profile.length; sample++) {
            profile[sample] = (float) route.elevationAt(sample * intervalLength);
        }

        int indexFirstNumber = 0;
        while ((indexFirstNumber < profile.length && Float.isNaN(profile[indexFirstNumber]))) {
            indexFirstNumber++;
        }

        // assigning each sample an elevation equal to 0 if no valid sample exists
        if (indexFirstNumber == profile.length) {
            Arrays.fill(profile, 0);
        } else {

            Arrays.fill(profile, 0, indexFirstNumber, profile[indexFirstNumber]);

            int indexFirstNumberEnd = profile.length - 1;
            while (Float.isNaN(profile[indexFirstNumberEnd])) {
                indexFirstNumberEnd--;
            }
            Arrays.fill(profile, indexFirstNumberEnd + 1, profile.length, profile[indexFirstNumberEnd]);

            int left = indexFirstNumber;
            int right;
            double y0;
            double y1;

            while (left < indexFirstNumberEnd) {
                left++;

                if (!Float.isNaN(profile[left])) { // if it is a number
                    continue;
                }

                left--; // index of the number just before a NaN
                right = left + 2; //making space

                while (Float.isNaN(profile[right])) {
                    right++;
                }

                y0 = profile[left];
                y1 = profile[right];
                for (int NaNindex = left + 1; NaNindex < right; NaNindex++) {
                    profile[NaNindex] = (float) Math2.interpolate(y0, y1, (double) (NaNindex - left) / (right - left));
                }
            }

        }
        return new ElevationProfile(route.length(), profile);
    }
}





