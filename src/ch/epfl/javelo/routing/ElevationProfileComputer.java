package ch.epfl.javelo.routing;

import java.util.Arrays;
import java.util.List;

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    ElevationProfile elevationProfile(Route route, double maxStepLength) {

        int numberOfSamples = 1 + (int) Math.ceil(route.length() / maxStepLength);
        double intervalLength = route.length() / (numberOfSamples - 1);
        float[] profile = new float[numberOfSamples];
        int numberOfEdges = route.points().size();

        for (int j = 0; j < profile.length; j++){
            profile[j] = (float) route.elevationAt(j * intervalLength);
        }

        int k = 0;
        while(Float.isNaN(profile[k])){
            k++;
        }
        Arrays.fill(profile, 0, k, profile[k]);

        int l = profile.length - 1;
        while(Float.isNaN(profile[l])){
            l--;
        }
        Arrays.fill(profile, l, profile.length - 1, profile[l]);

        for (int m = 0; m < profile.length; m++){
            if (Float.isNaN(profile[m])){


            }
        }






        return new ElevationProfile(0, new float[]{});


    }


}


