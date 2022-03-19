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
    void elevationProfileWorksInAverageProbCases() {
        List<Edge> edges = new ArrayList<>();
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 10000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 15000, SwissBounds.MIN_N + 15000);


        edges.add(new Edge(1, 2, point1, point2, 6, Functions.sampled(new float[]{Float.NaN, Float.NaN, Float.NaN, 38}, 6)));
        edges.add(new Edge(2, 3, point2, point3, 6, Functions.sampled(new float[]{38, Float.NaN, 30, 345}, 6)));
        SingleRoute route = new SingleRoute(edges);
        assertEquals(Float.NaN, route.elevationAt(0));
        assertEquals(Float.NaN, route.elevationAt(2));
        assertEquals(Float.NaN, route.elevationAt(4));
        assertEquals(Float.NaN, route.elevationAt(8));

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 2);
        assertEquals(38, profile.elevationAt(0));
        assertEquals(38, profile.elevationAt(2));
        assertEquals(38, profile.elevationAt(4));
        assertEquals(34, profile.elevationAt(8));
        assertEquals(8, profile.totalDescent());
        assertEquals(315, profile.totalAscent());
        assertEquals(345, profile.maxElevation());
        assertEquals(30, profile.minElevation());
        assertEquals(12, profile.length());

    }

    @Test
    void elevationProfileWorksWIthOnlyNaN() {
        List<Edge> edges = new ArrayList<>();
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 10000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 15000, SwissBounds.MIN_N + 15000);


        edges.add(new Edge(15, 2, point1, point2, 6, Functions.sampled(new float[]{Float.NaN, Float.NaN, Float.NaN, Float.NaN}, 6)));
        edges.add(new Edge(2, 3, point2, point3, 6, Functions.sampled(new float[]{Float.NaN, Float.NaN, Float.NaN, Float.NaN}, 6)));

        SingleRoute route = new SingleRoute(edges);
        PointCh test = route.pointAt(2);
        PointCh test1 = route.pointAt(-45);
        PointCh test2 = route.pointAt(600);

        System.out.println(route.nodeClosestTo(-6));
        System.out.println(route.nodeClosestTo(50));
        System.out.println(route.nodeClosestTo(12));
        System.out.println(route.nodeClosestTo(6));

        System.out.println(test);
        System.out.println(route.pointAt(6));
        System.out.println(test1);
        System.out.println(test2);


        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 2);

        assertEquals(0, profile.elevationAt(7));


    }

    @Test
    void elevationProfileWorks2() {
        List<Edge> edges = new ArrayList<>();
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 10000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 15000, SwissBounds.MIN_N + 15000);


        edges.add(new Edge(1, 2, point1, point2, 6, Functions.sampled(new float[]{50, Float.NaN, Float.NaN, Float.NaN}, 6)));
        edges.add(new Edge(2, 3, point2, point3, 6, Functions.sampled(new float[]{Float.NaN, 100, Float.NaN, Float.NaN}, 6)));

        SingleRoute route = new SingleRoute(edges);
        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 2);

        System.out.println(profile.totalDescent());
        System.out.println(profile.totalAscent());
        assertEquals(56.25, profile.elevationAt(1));
        assertEquals(62.5, profile.elevationAt(2));
        assertEquals(68.75, profile.elevationAt(3));
        assertEquals(87.5, profile.elevationAt(6));

    }


    }
/*
        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 2);
        System.out.println(profile.length());
        System.out.println(profile.elevationAt(1));
        System.out.println(profile.elevationAt(12));
        System.out.println(profile.elevationAt(8));
       System.out.println(profile.totalAscent());
       System.out.println(profile.totalDescent());
       System.out.println(profile.minElevation());
       System.out.println(profile.maxElevation());
        System.out.println(profile.elevationAt(7071));

    }
}


 */