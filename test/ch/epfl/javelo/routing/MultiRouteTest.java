
package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTest {
    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;

    // Sides of triangle used for "sawtooth" edges (shape: /\/\/\…)
    private static final double TOOTH_EW = 1023;
    private static final double TOOTH_NS = 64;
    private static final double TOOTH_LENGTH = 1025;
    private static final double TOOTH_ELEVATION_GAIN = 100d;
    private static final double TOOTH_SLOPE = TOOTH_ELEVATION_GAIN / TOOTH_LENGTH;

    private static Edge horizontalEdge1K(int i) {
        var j = i + 1;
        var pI = new PointCh(2_600_000 + 1000 * i, 1_200_000);
        var pJ = new PointCh(2_600_000 + 1000 * j, 1_200_000);
        return new Edge(i, j, pI, pJ, 1000, x -> 500);
    }

    @Test
    void multiRouteConstructorThrowsOnEmptyEdgeList() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(List.of());
        });
    }

    @Test
    void multiRouteIndexOfSegmentAtWorksWithShallowRoutes() {
        var m = new MultiRoute(List.of(
                new SingleRoute(List.of(horizontalEdge1K(0))),
                new SingleRoute(List.of(horizontalEdge1K(1))),
                new SingleRoute(List.of(horizontalEdge1K(2))),
                new SingleRoute(List.of(horizontalEdge1K(3))),
                new SingleRoute(List.of(horizontalEdge1K(4))),
                new SingleRoute(List.of(horizontalEdge1K(5)))));
        for (int i = 0; i < 6; i += 1)
            assertEquals(i, m.indexOfSegmentAt((i + 0.5) * 1000));
        assertEquals(5, m.indexOfSegmentAt(10000));
    }

    @Test
    void multiRouteIndexOfSegmentAtWorksWithDeepRoutes() {
        var m1 = new MultiRoute(List.of(
                new SingleRoute(List.of(horizontalEdge1K(0))),
                new SingleRoute(List.of(horizontalEdge1K(1))),
                new SingleRoute(List.of(horizontalEdge1K(2)))));
        var m2 = new MultiRoute(List.of(
                new SingleRoute(List.of(horizontalEdge1K(3))),
                new MultiRoute(List.of(
                        new SingleRoute(List.of(horizontalEdge1K(4))),
                        new SingleRoute(List.of(horizontalEdge1K(5)))))));
        var m = new MultiRoute(List.of(m1, m2));
        for (int i = 0; i < 6; i += 1)
            assertEquals(i, m.indexOfSegmentAt((i + 0.5) * 1000));
        assertEquals(5, m.indexOfSegmentAt(10000));
    }

    @Test
    void multiRouteLengthReturnsTotalLength() {
        for (int i = 1; i < 10; i += 1) {
            var routes = new ArrayList<Route>();
            for (var edge : verticalEdges(i)) routes.add(new SingleRoute(List.of(edge)));
            var route = new MultiRoute(routes);
            assertEquals(i * EDGE_LENGTH, route.length());
        }
    }

    @Test
    void multiRouteRoutesAreCopiedToEnsureImmutability() {
        var immutableRoutes = List.<Route>of(new SingleRoute(verticalEdges(10)));
        var mutableRoutes = new ArrayList<>(immutableRoutes);
        var route = new MultiRoute(mutableRoutes);
        mutableRoutes.clear();
        assertNotEquals(0, route.length());
    }

    @Test
    void multiRouteEdgesAreNotModifiableFromOutside() {
        var edgesCount = 5;
        var route = new MultiRoute(List.of(new SingleRoute(verticalEdges(edgesCount))));
        try {
            route.edges().clear();
        } catch (UnsupportedOperationException e) {
            // Nothing to do (the list of points is not modifiable, which is fine).
        }
        assertEquals(edgesCount, route.edges().size());
    }

    @Test
    void multiRoutePointsAreNotModifiableFromOutside() {
        var edgesCount = 5;
        var route = new MultiRoute(List.of(new SingleRoute(verticalEdges(edgesCount))));
        try {
            route.points().clear();
        } catch (UnsupportedOperationException e) {
            // Nothing to do (the list of points is not modifiable, which is fine).
        }
        assertEquals(edgesCount + 1, route.points().size());
    }

    @Test
    void multiRoutePointsAreCorrect() {
        for (int edgesCount = 1; edgesCount < 10; edgesCount += 1) {
            var edges = verticalEdges(edgesCount);
            var routes = new ArrayList<Route>();
            for (var edge : edges) routes.add(new SingleRoute(List.of(edge)));
            var route = new MultiRoute(routes);
            var points = route.points();
            assertEquals(edgesCount + 1, points.size());
            assertEquals(edges.get(0).fromPoint(), points.get(0));
            for (int i = 1; i < points.size(); i += 1)
                assertEquals(edges.get(i - 1).toPoint(), points.get(i));
        }
    }

    @Test
    void multiRoutePointAtWorks() {
        var edgesCount = 12;
        var edges = sawToothEdges(edgesCount);
        var route = new MultiRoute(List.of(
                new SingleRoute(edges.subList(0, 4)),
                new SingleRoute(edges.subList(4, 8)),
                new SingleRoute(edges.subList(8, 12))));

        // Outside the range of the route
        assertEquals(sawToothPoint(0), route.pointAt(Math.nextDown(0)));
        assertEquals(sawToothPoint(edgesCount), route.pointAt(Math.nextUp(edgesCount * TOOTH_LENGTH)));

        // Edge endpoints
        for (int i = 0; i < edgesCount + 1; i += 1)
            assertEquals(sawToothPoint(i), route.pointAt(i * TOOTH_LENGTH));

        // Points at 1/4, 2/4 and 3/4 of the edges
        for (int i = 0; i < edgesCount; i += 1) {
            for (double p = 0.25; p <= 0.75; p += 0.25) {
                var expectedE = ORIGIN_E + (i + p) * TOOTH_EW;
                var expectedN = (i & 1) == 0
                        ? ORIGIN_N + TOOTH_NS * p
                        : ORIGIN_N + TOOTH_NS * (1 - p);
                assertEquals(
                        new PointCh(expectedE, expectedN),
                        route.pointAt((i + p) * TOOTH_LENGTH));
            }
        }
    }

    @Test
    void multiRouteElevationAtWorks() {
        var edgesCount = 12;
        var edges = sawToothEdges(edgesCount);
        var route = new MultiRoute(List.of(
                new SingleRoute(edges.subList(0, 4)),
                new SingleRoute(edges.subList(4, 8)),
                new SingleRoute(edges.subList(8, 12))));

        for (int i = 0; i < edgesCount; i += 1) {
            for (double p = 0; p < 1; p += 0.125) {
                var pos = (i + p) * TOOTH_LENGTH;
                var expectedElevation = (i + p) * TOOTH_ELEVATION_GAIN;
                assertEquals(expectedElevation, route.elevationAt(pos));
            }
        }
        assertEquals(0, route.elevationAt(-1e6));
        assertEquals(edgesCount * TOOTH_ELEVATION_GAIN, route.elevationAt(+1e6));
    }

    @Test
    void multiRouteNodeClosestToWorks() {
        var edgesCount = 12;
        var edges = sawToothEdges(edgesCount);
        var route = new MultiRoute(List.of(
                new SingleRoute(edges.subList(0, 4)),
                new SingleRoute(edges.subList(4, 8)),
                new SingleRoute(edges.subList(8, 12))));

        for (int i = 0; i <= edgesCount; i += 1) {
            for (double p = -0.25; p <= 0.25; p += 0.25) {
                var pos = (i + p) * TOOTH_LENGTH;
                assertEquals(i, route.nodeClosestTo(pos));
            }
        }
    }

    @Test
    void multiRoutePointClosestToWorksWithFarAwayPoints() {
        var rng = newRandom();

        var edgesCount = 12;
        var edges = verticalEdges(edgesCount);
        var route = new MultiRoute(List.of(
                new SingleRoute(edges.subList(0, 4)),
                new SingleRoute(edges.subList(4, 8)),
                new SingleRoute(edges.subList(8, 12))));

        // Points below the route
        var origin = new PointCh(ORIGIN_E, ORIGIN_N);
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var dN = rng.nextDouble(-10_000, -1);
            var dE = rng.nextDouble(-1000, 1000);
            var p = new PointCh(ORIGIN_E + dE, ORIGIN_N + dN);
            var pct = route.pointClosestTo(p);
            assertEquals(origin, pct.point());
            assertEquals(0, pct.position());
            assertEquals(Math.hypot(dE, dN), pct.distanceToReference(), 1e-4);
        }

        // Points above the route
        var end = new PointCh(ORIGIN_E, ORIGIN_N + edgesCount * EDGE_LENGTH);
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var dN = rng.nextDouble(1, 10_000);
            var dE = rng.nextDouble(-1000, 1000);
            var p = new PointCh(ORIGIN_E + dE, ORIGIN_N + edgesCount * EDGE_LENGTH + dN);
            var pct = route.pointClosestTo(p);
            assertEquals(end, pct.point());
            assertEquals(edgesCount * EDGE_LENGTH, pct.position());
            assertEquals(Math.hypot(dE, dN), pct.distanceToReference(), 1e-4);
        }
    }

    @Test
    void multiRoutePointClosestToWorksWithPointsOnRoute() {
        var rng = newRandom();

        var edgesCount = 12;
        var edges = verticalEdges(edgesCount);
        var route = new MultiRoute(List.of(
                new SingleRoute(edges.subList(0, 4)),
                new SingleRoute(edges.subList(4, 8)),
                new SingleRoute(edges.subList(8, 12))));

        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var pos = rng.nextDouble(0, route.length());
            var pt = route.pointAt(pos);
            var pct = route.pointClosestTo(pt);
            assertEquals(pt.e(), pct.point().e(), 1e-4);
            assertEquals(pt.n(), pct.point().n(), 1e-4);
            assertEquals(pos, pct.position(), 1e-4);
            assertEquals(0, pct.distanceToReference(), 1e-4);
        }
    }

    @Test
    void multiRoutePointClosestToWorksWithSawtoothPoints() {
        var edgesCount = 12;
        var edges = sawToothEdges(edgesCount);
        var route = new MultiRoute(List.of(
                new SingleRoute(edges.subList(0, 4)),
                new SingleRoute(edges.subList(4, 8)),
                new SingleRoute(edges.subList(8, 12))));

        // Points above the sawtooth
        for (int i = 1; i <= edgesCount; i += 2) {
            var p = sawToothPoint(i);
            var dN = i * 500;
            var pAbove = new PointCh(p.e(), p.n() + dN);
            var pct = route.pointClosestTo(pAbove);
            assertEquals(p, pct.point());
            assertEquals(i * TOOTH_LENGTH, pct.position());
            assertEquals(dN, pct.distanceToReference());
        }

        // Points below the sawtooth
        for (int i = 0; i <= edgesCount; i += 2) {
            var p = sawToothPoint(i);
            var dN = i * 500;
            var pBelow = new PointCh(p.e(), p.n() - dN);
            var pct = route.pointClosestTo(pBelow);
            assertEquals(p, pct.point());
            assertEquals(i * TOOTH_LENGTH, pct.position());
            assertEquals(dN, pct.distanceToReference());
        }

        // Points close to the n/8
        var dE = TOOTH_NS / 16d;
        var dN = TOOTH_EW / 16d;
        for (int i = 0; i < edgesCount; i += 1) {
            var upwardEdge = (i & 1) == 0;
            for (double p = 0.125; p <= 0.875; p += 0.125) {
                var pointE = ORIGIN_E + (i + p) * TOOTH_EW;
                var pointN = ORIGIN_N + TOOTH_NS * (upwardEdge ? p : (1 - p));
                var point = new PointCh(pointE, pointN);
                var position = (i + p) * TOOTH_LENGTH;
                var reference = new PointCh(
                        pointE + dE,
                        pointN + (upwardEdge ? -dN : dN));
                var pct = route.pointClosestTo(reference);
                assertEquals(point, pct.point());
                assertEquals(position, pct.position());
                assertEquals(Math.hypot(dE, dN), pct.distanceToReference());
            }
        }
    }

    private static List<Edge> verticalEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = new PointCh(ORIGIN_E, ORIGIN_N + i * EDGE_LENGTH);
            var p2 = new PointCh(ORIGIN_E, ORIGIN_N + (i + 1) * EDGE_LENGTH);
            edges.add(new Edge(i, i + 1, p1, p2, EDGE_LENGTH, x -> Double.NaN));
        }
        return Collections.unmodifiableList(edges);
    }

    private static List<Edge> sawToothEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = sawToothPoint(i);
            var p2 = sawToothPoint(i + 1);
            var startingElevation = i * TOOTH_ELEVATION_GAIN;
            edges.add(new Edge(i, i + 1, p1, p2, TOOTH_LENGTH, x -> startingElevation + x * TOOTH_SLOPE));
        }
        return Collections.unmodifiableList(edges);
    }

    private static PointCh sawToothPoint(int i) {
        return new PointCh(
                ORIGIN_E + TOOTH_EW * i,
                ORIGIN_N + ((i & 1) == 0 ? 0 : TOOTH_NS));
    }
}
/*
package ch.epfl.javelo.routing;


import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class MultiRouteTest {

    @Test
    void points(){
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N + 5000);
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N + 5000);
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 9000, SwissBounds.MIN_N + 5000);
        PointCh point6 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 5000);
        PointCh point7 = new PointCh(SwissBounds.MIN_E + 11000, SwissBounds.MIN_N + 5000);


        SingleRoute singleRoute1 = new SingleRoute(List.of(new Edge(1,2, point1, point2, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute2 = new SingleRoute(List.of(new Edge(2,3, point2, point3, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute3 = new SingleRoute(List.of(new Edge(3,4, point3, point4, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute4 = new SingleRoute(List.of(new Edge(4,5, point4, point5, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute5 = new SingleRoute(List.of(new Edge(5,6, point5, point6, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute6 = new SingleRoute(List.of(new Edge(6,7, point6, point7, 1000, Functions.sampled(new float[]{20,21},1000))));


        MultiRoute multiRoute1 = new MultiRoute(List.of(singleRoute1,singleRoute2,singleRoute3));
        MultiRoute multiRoute2 = new MultiRoute(List.of(singleRoute4,singleRoute5,singleRoute6));

        MultiRoute multiRoute3 = new MultiRoute(List.of(singleRoute5,singleRoute6));

        MultiRoute multiRouteFinal = new MultiRoute(List.of(multiRoute1,multiRoute2));
        MultiRoute multiRouteFinal2 = new MultiRoute(List.of(multiRoute1,singleRoute4, multiRoute3));

         multiRouteFinal.points();
        assertEquals(List.of(point1, point2, point3, point4, point5, point6, point7), multiRouteFinal.points());
        assertEquals(List.of(point1, point2, point3, point4, point5, point6, point7), multiRouteFinal2.points());

    }

    @Test
    void indexOfSegmentAt() {
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N + 5000);
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N + 5000);
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 9000, SwissBounds.MIN_N + 5000);
        PointCh point6 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 5000);
        PointCh point7 = new PointCh(SwissBounds.MIN_E + 11000, SwissBounds.MIN_N + 5000);


        SingleRoute singleRoute1 = new SingleRoute(List.of(new Edge(1,2, point1, point2, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute2 = new SingleRoute(List.of(new Edge(2,3, point2, point3, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute3 = new SingleRoute(List.of(new Edge(3,4, point3, point4, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute4 = new SingleRoute(List.of(new Edge(4,5, point4, point5, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute5 = new SingleRoute(List.of(new Edge(5,6, point5, point6, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute6 = new SingleRoute(List.of(new Edge(6,7, point6, point7, 1000, Functions.sampled(new float[]{20,21},1000))));


        MultiRoute multiRoute1 = new MultiRoute(List.of(singleRoute1,singleRoute2,singleRoute3));
        MultiRoute multiRoute2 = new MultiRoute(List.of(singleRoute4, singleRoute5,singleRoute6));
        MultiRoute multiRoute3 = new MultiRoute(List.of(singleRoute5,singleRoute6));

        MultiRoute multiRouteFinal = new MultiRoute(List.of(multiRoute1,multiRoute2));

        assertEquals(5, multiRouteFinal.indexOfSegmentAt(5500));
        assertEquals(3, multiRouteFinal.indexOfSegmentAt(3000));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(8000));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(5500));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(10000));
        assertEquals(2, multiRouteFinal.indexOfSegmentAt(2000));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(0));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(-1));

        MultiRoute multiRouteFinal2 = new MultiRoute(List.of(multiRoute1, singleRoute4, multiRoute3));
        assertEquals(6000, multiRouteFinal2.length());
        //assertEquals(6 , multiRouteFinal2.computeNumberOfSingleRoutes());
        assertEquals(5, multiRouteFinal2.indexOfSegmentAt(5500));
        assertEquals(3, multiRouteFinal2.indexOfSegmentAt(3000));
        assertEquals(5, multiRouteFinal2.indexOfSegmentAt(8000));
        assertEquals(5, multiRouteFinal2.indexOfSegmentAt(5500));
        assertEquals(5, multiRouteFinal2.indexOfSegmentAt(10000));
        assertEquals(2, multiRouteFinal2.indexOfSegmentAt(2000));
        assertEquals(0, multiRouteFinal2.indexOfSegmentAt(0));
        assertEquals(0, multiRouteFinal2.indexOfSegmentAt(-1));

    }

    @Test
    void length() {
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N + 5000);
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N + 5000);
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 9000, SwissBounds.MIN_N + 5000);
        PointCh point6 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 5000);
        PointCh point7 = new PointCh(SwissBounds.MIN_E + 11000, SwissBounds.MIN_N + 5000);


        SingleRoute singleRoute1 = new SingleRoute(List.of(new Edge(1,2, point1, point2, 1000, Functions.sampled(new float[]{18,23},1000))
        , new Edge(2,3, point2, point3, 1000, Functions.sampled(new float[]{23,21},1000))
        , new Edge(3,4, point3, point4, 1000, Functions.sampled(new float[]{21,22},1000))));
        SingleRoute singleRoute2 = new SingleRoute(List.of(new Edge(4,5, point4, point5, 1000, Functions.sampled(new float[]{22,24},1000))
        , new Edge(5,6, point5, point6, 1000, Functions.sampled(new float[]{24,20},1000))
        , new Edge(6,7, point6, point7, 1000, Functions.sampled(new float[]{20,21},1000))));

        MultiRoute multiRouteFinal = new MultiRoute(List.of(singleRoute1,singleRoute2));
        assertEquals(6000, multiRouteFinal.length());
        assertEquals(new RoutePoint(point1, 0, Math.sqrt(Math.pow(3000,2) + Math.pow(2000,2))), multiRouteFinal.pointClosestTo(new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 3000)));
        assertEquals(1, multiRouteFinal.indexOfSegmentAt(3500));
    }

    @Test
    void pointAt() {
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N + 5000);
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N + 5000);
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 9000, SwissBounds.MIN_N + 5000);
        PointCh point6 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 5000);
        PointCh point7 = new PointCh(SwissBounds.MIN_E + 11000, SwissBounds.MIN_N + 5000);


        SingleRoute singleRoute1 = new SingleRoute(List.of(new Edge(1,2, point1, point2, 1000, Functions.sampled(new float[]{18,23},1000))));
        SingleRoute singleRoute2 = new SingleRoute(List.of(new Edge(2,3, point2, point3, 1000, Functions.sampled(new float[]{23,21},1000))));
        SingleRoute singleRoute3 = new SingleRoute(List.of(new Edge(3,4, point3, point4, 1000, Functions.sampled(new float[]{21,22},1000))));
        SingleRoute singleRoute4 = new SingleRoute(List.of(new Edge(4,5, point4, point5, 1000, Functions.sampled(new float[]{22,24},1000))));
        SingleRoute singleRoute5 = new SingleRoute(List.of(new Edge(5,6, point5, point6, 1000, Functions.sampled(new float[]{24,20},1000))));
        SingleRoute singleRoute6 = new SingleRoute(List.of(new Edge(6,7, point6, point7, 1000, Functions.sampled(new float[]{20,21},1000))));


        MultiRoute multiRoute1 = new MultiRoute(List.of(singleRoute1,singleRoute2,singleRoute3));
        MultiRoute multiRoute2 = new MultiRoute(List.of(singleRoute4,singleRoute5,singleRoute6));

        MultiRoute multiRouteFinal = new MultiRoute(List.of(multiRoute1,multiRoute2));

        assertEquals(point1, multiRouteFinal.pointAt(-50));
        assertEquals(new PointCh(SwissBounds.MIN_E + 5500, SwissBounds.MIN_N + 5000), multiRouteFinal.pointAt(500));
        assertEquals(point7, multiRouteFinal.pointAt(9000));
        multiRouteFinal.pointAt(9000);
        multiRouteFinal.pointAt(2400);
        assertEquals( new PointCh(SwissBounds.MIN_E + 7400, SwissBounds.MIN_N + 5000), multiRouteFinal.pointAt(2400));

    }

    @Test
    void elevationAt() {
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N + 5000);
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N + 5000);
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 9000, SwissBounds.MIN_N + 5000);
        PointCh point6 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 5000);
        PointCh point7 = new PointCh(SwissBounds.MIN_E + 11000, SwissBounds.MIN_N + 5000);


        SingleRoute singleRoute1 = new SingleRoute(List.of(new Edge(1,2, point1, point2, 1000, Functions.sampled(new float[]{18,23},1000))));
        SingleRoute singleRoute2 = new SingleRoute(List.of(new Edge(2,3, point2, point3, 1000, Functions.sampled(new float[]{23,21},1000))));
        SingleRoute singleRoute3 = new SingleRoute(List.of(new Edge(3,4, point3, point4, 1000, Functions.sampled(new float[]{21,22},1000))));
        SingleRoute singleRoute4 = new SingleRoute(List.of(new Edge(4,5, point4, point5, 1000, Functions.sampled(new float[]{22,24},1000))));
        SingleRoute singleRoute5 = new SingleRoute(List.of(new Edge(5,6, point5, point6, 1000, Functions.sampled(new float[]{24,20},1000))));
        SingleRoute singleRoute6 = new SingleRoute(List.of(new Edge(6,7, point6, point7, 1000, Functions.sampled(new float[]{20,21},1000))));


        MultiRoute multiRoute1 = new MultiRoute(List.of(singleRoute1,singleRoute2,singleRoute3));
        MultiRoute multiRoute2 = new MultiRoute(List.of(singleRoute4,singleRoute5,singleRoute6));

        MultiRoute multiRouteFinal = new MultiRoute(List.of(multiRoute1,multiRoute2));

        assertEquals(22.2, multiRouteFinal.elevationAt(3100));

        assertEquals(21, multiRouteFinal.elevationAt(6000));
        assertEquals(21, multiRouteFinal.elevationAt(7000));
        assertEquals(22, multiRouteFinal.elevationAt(1500));
        assertEquals(18, multiRouteFinal.elevationAt(0));
        assertEquals(22.2 , multiRouteFinal.elevationAt(3100));

    }

    @Test
    void nodeClosestTo() {
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N + 5000);
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N + 5000);
        SingleRoute singleRoute1 = new SingleRoute(List.of(new Edge(1,2, point1, point2, 1000, Functions.constant(Float.NaN)),
                new Edge(2,3, point2, point3, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute2 = new SingleRoute(List.of(new Edge(3,4, point3, point4, 1000, Functions.constant(Float.NaN))));

        MultiRoute finalM = new MultiRoute(List.of(singleRoute1,singleRoute2));
        assertEquals(3000, finalM.length());
        assertEquals(4, finalM.nodeClosestTo(5000));
        assertEquals(1, finalM.nodeClosestTo(-3));
        assertEquals(0, finalM.indexOfSegmentAt(1500));
        assertEquals(0, finalM.indexOfSegmentAt(-344));
        assertEquals(1, finalM.indexOfSegmentAt(32322));
        assertEquals(1, finalM.indexOfSegmentAt(2500));
        assertEquals(3, finalM.nodeClosestTo(2100));
        assertEquals(3, finalM.nodeClosestTo(1800));

    }

    @Test
    void pointClosestTo() {
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
        PointCh point2 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N + 5000);
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N + 5000);
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 9000, SwissBounds.MIN_N + 5000);
        PointCh point6 = new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N + 5000);
        PointCh point7 = new PointCh(SwissBounds.MIN_E + 11000, SwissBounds.MIN_N + 5000);


        SingleRoute singleRoute1 = new SingleRoute(List.of(new Edge(1,2, point1, point2, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute2 = new SingleRoute(List.of(new Edge(2,3, point2, point3, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute3 = new SingleRoute(List.of(new Edge(3,4, point3, point4, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute4 = new SingleRoute(List.of(new Edge(4,5, point4, point5, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute5 = new SingleRoute(List.of(new Edge(5,6, point5, point6, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute6 = new SingleRoute(List.of(new Edge(6,7, point6, point7, 1000, Functions.sampled(new float[]{20,21},1000))));


        MultiRoute multiRoute1 = new MultiRoute(List.of(singleRoute1,singleRoute2,singleRoute3));
        MultiRoute multiRoute2 = new MultiRoute(List.of(singleRoute4,singleRoute5,singleRoute6));
        MultiRoute multiRoute3 = new MultiRoute(List.of(singleRoute5,singleRoute6));

        MultiRoute multiRouteFinal2 = new MultiRoute(List.of(multiRoute1, singleRoute4, multiRoute3));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 8600,SwissBounds.MIN_N + 5000), 3600, 1000),
                multiRouteFinal2.pointClosestTo(new PointCh(SwissBounds.MIN_E + 8600,SwissBounds.MIN_N + 6000)));
      //  assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 6213,SwissBounds.MIN_N + 5000), 1213, 1000),
       //         multiRouteFinal2.pointClosestTo(new PointCh(SwissBounds.MIN_E + 6213, SwissBounds.MIN_N + 6200)));

        multiRouteFinal2.pointClosestTo(new PointCh(SwissBounds.MIN_E + 6213, SwissBounds.MIN_N + 6200));

   //    assertEquals(new RoutePoint(point7, 6000 ,3400), multiRouteFinal2.pointClosestTo(
   //             new PointCh(SwissBounds.MIN_E +80000, SwissBounds.MIN_N + 20000)));

       multiRouteFinal2.indexOfSegmentAt(500);
       multiRouteFinal2.indexOfSegmentAt(1000);





    }
}

 */