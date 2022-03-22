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
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 13_000, SwissBounds.MIN_N + 16000);

        //Math.sqrt(2 * Math.pow(5000,2))
        // Math.sqrt(Math.pow(3000,2) + Math.pow(6000,2))

        edges.add(new Edge(1, 2, point1, point2, 6, Functions.constant(Double.NaN)));
        edges.add(new Edge(2, 3, point2, point3, 6, Functions.sampled(new float[]{20, 23, Float.NaN, 21}, 6)));
        SingleRoute route = new SingleRoute(edges);

        System.out.println(route.elevationAt(10));
        System.out.println(route.elevationAt(11));

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 2);
        assertEquals(20, profile.elevationAt(0));
        assertEquals(20, profile.elevationAt(-6));
        assertEquals(23, profile.elevationAt(8));
        assertEquals(21.5, profile.elevationAt(7));
        assertEquals(22, profile.elevationAt(10));
        assertEquals(21, profile.elevationAt(30));
        assertEquals(21.5, profile.elevationAt(11));
        assertEquals(3, profile.totalAscent());
        assertEquals(2, profile.totalDescent());
        assertEquals(12, profile.length());
        assertEquals(23, profile.maxElevation());
        assertEquals(20, profile.minElevation());

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
        PointCh test1 = route.pointAt(2);
        System.out.println(test1);
        PointCh test2 = route.pointAt(4);
        System.out.println(test2);


       // System.out.println(route.nodeClosestTo(-6));
       // System.out.println(route.nodeClosestTo(50));
       // System.out.println(route.nodeClosestTo(12));
        //System.out.println(route.nodeClosestTo(6));

        double value = point1.e() + (point2.e() - point1.e())/3;
        assertEquals(value , test1.e());

        double value2 = point1.n() + (point2.n() - point1.n())/3;
        assertEquals(value2, test1.n());
        assertEquals(point1.n() + (point2.n() - point1.n())/1.5, test2.n());
        assertEquals(point1.e() + (point2.e() - point1.e())/1.5, test2.e());

        System.out.println(route.elevationAt(3));
        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 2);
        System.out.println(profile.elevationAt(3));






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