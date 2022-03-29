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

        MultiRoute multiRouteFinal = new MultiRoute(List.of(multiRoute1,multiRoute2));

        assertEquals(7, multiRouteFinal.points().size());
        assertEquals(List.of(point1, point2, point3, point4, point5, point6, point7), multiRouteFinal.points());

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





    }
}