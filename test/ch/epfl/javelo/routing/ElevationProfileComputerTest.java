package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {

    @Test
    void elevationProfile() {
        List<Edge> edges = new ArrayList<>();
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N +5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N +10000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 15000, SwissBounds.MIN_N +15000);



        edges.add(new Edge(1,2, point1, point2, 7071, Functions.sampled(new float[]{20, 22, 24, 19},7071)));
        edges.add(new Edge(2,3, point2, point3, 10_000, Functions.sampled(new float[]{19, 10, 30, 35},10_000)));
        SingleRoute route = new SingleRoute(edges);
        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 100);
        System.out.println(profile.length());
        System.out.println(profile.totalAscent());
        System.out.println(profile.totalDescent());
        System.out.println(profile.minElevation());
        System.out.println(profile.maxElevation());
        System.out.println(profile.elevationAt(7071));

    }
}