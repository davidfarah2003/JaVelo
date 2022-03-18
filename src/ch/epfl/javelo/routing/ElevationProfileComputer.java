package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;

import java.util.Arrays;

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    ElevationProfile elevationProfile(Route route, double maxStepLength) {

        int numberOfSamples = 1 + (int) Math.ceil(route.length() / maxStepLength);
        double intervalLength = route.length() / (numberOfSamples - 1);
        float[] profile = new float[numberOfSamples];


        for (int j = 0; j < profile.length; j++){
            profile[j] = (float) route.elevationAt(j * intervalLength);
        }

        int k = 0;
        while((k < profile.length && Float.isNaN(profile[k]))){
            k++;
        }

        if (k == profile.length){
            Arrays.fill(profile, 0);
            return new ElevationProfile(route.length(), profile);
        }

        Arrays.fill(profile, 0, k, profile[k]);


       // while (l different than )
        int l = profile.length - 1;
        while(Float.isNaN(profile[l])){
            l--;
        }
        Arrays.fill(profile, l, profile.length, profile[l]);

        int left = k;
        int right;

       while (left < l) {
               left++;

           if (!Float.isNaN(profile[left])) { // while is a number
             continue;
           }

           left--;
           right = left + 2; //dernier N

           while (Float.isNaN(profile[right])) {
               right++;
           }

           double y0 = profile[left];
           double y1 = profile[right];
           for (int a = left + 1; a < right; a++) {
               profile[a] = (float) Math2.interpolate(y0, y1, (double) (a - left) / (right - left));
           }
       }

        return new ElevationProfile(route.length(), profile);

/*
        for (int m = 0; m < profile.length; m++){
            if (Float.isNaN(profile[m])){
                first_index = m - 1;
                break;
            }
        }

        int last_index = 0;
        for (int n = 0; n < profile.length; n++){
            if (!Float.isNaN(profile[n]) && Float.isNaN(profile[n-1])){
                last_index = n;
                break;
                */

            }
        }





