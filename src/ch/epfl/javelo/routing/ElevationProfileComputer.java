package ch.epfl.javelo.routing;

import java.util.List;

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    ;
}
/*
    ElevationProfile elevationProfile(Route route, double maxStepLength){

        List<Edge> edges = route.edges();
        double totalLength = 0.0;

       for (int i = 0; i < edges.size(); i++){
          totalLength += edges.get(i).length();
       }

        int intervalLength = (int) Math.ceil(totalLength/maxStepLength);

    }
}


 */