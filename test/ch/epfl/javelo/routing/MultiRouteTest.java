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
        MultiRoute multiRoute2 = new MultiRoute(List.of(singleRoute4,singleRoute5,singleRoute6));


        MultiRoute multiRouteFinal = new MultiRoute(List.of(multiRoute1,multiRoute2));



        assertEquals(5, multiRouteFinal.indexOfSegmentAt(5500));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(6000));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(10000));
        assertEquals(4, multiRouteFinal.indexOfSegmentAt(4500));
        assertEquals(2, multiRouteFinal.indexOfSegmentAt(2000));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(0));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(-1));


        //System.out.println(multiRouteFinal.edges().size());
        //System.out.println(multiRouteFinal.edges().get(0).length());
        //List<PointCh> list = multiRouteFinal.points();
        /*
        System.out.println(list.size());
        System.out.println(list.get(0));
        System.out.println(list.get(1));
        System.out.println(list.get(2));
        System.out.println(list.get(3));
        System.out.println(multiRouteFinal.pointAt(3000));

         */
       // System.out.println(multiRouteFinal.elevationAt(5500));
       // System.out.println(multiRouteFinal.pointAt(5500));
       // System.out.println(multiRouteFinal.nodeClosestTo(5500));
       // System.out.println(multiRouteFinal.pointAt(1500));
        //System.out.println(multiRouteFinal.pointClosestTo(new PointCh(SwissBounds.MIN_E + 5500, SwissBounds.MIN_N + 6000)));
       // multiRouteFinal.pointClosestTo(new PointCh(SwissBounds.MIN_E + 10500, SwissBounds.MIN_N + 6000));
      // System.out.println(multiRouteFinal.pointClosestTo(new PointCh(SwissBounds.MIN_E + 9500, SwissBounds.MIN_N + 6000)));
       // System.out.println(multiRouteFinal);

       // multiRouteFinal.pointClosestTo(new PointCh(SwissBounds.MIN_E + 9500, SwissBounds.MIN_N + 7000));
        //System.out.println(multiRouteFinal.pointClosestTo(new PointCh(SwissBounds.MIN_E + 7500, SwissBounds.MIN_N + 6000)));
        // should return 5 and returns 0
    }

    @Test
    void length() {
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


        SingleRoute singleRoute1 = new SingleRoute(List.of(new Edge(1,2, point1, point2, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute2 = new SingleRoute(List.of(new Edge(2,3, point2, point3, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute3 = new SingleRoute(List.of(new Edge(3,4, point3, point4, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute4 = new SingleRoute(List.of(new Edge(4,5, point4, point5, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute5 = new SingleRoute(List.of(new Edge(5,6, point5, point6, 1000, Functions.constant(Float.NaN))));
        SingleRoute singleRoute6 = new SingleRoute(List.of(new Edge(6,7, point6, point7, 1000, Functions.sampled(new float[]{20,21},1000))));


        MultiRoute multiRoute1 = new MultiRoute(List.of(singleRoute1,singleRoute2,singleRoute3));
        MultiRoute multiRoute2 = new MultiRoute(List.of(singleRoute4,singleRoute5,singleRoute6));

        MultiRoute multiRouteFinal = new MultiRoute(List.of(multiRoute1,multiRoute2));

       multiRouteFinal.elevationAt(6000);

    }

    @Test
    void nodeClosestTo() {
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

        MultiRoute multiRouteFinal = new MultiRoute(List.of(multiRoute1,multiRoute2));

        for (double element : multiRouteFinal.computeSegmentsLength())
            System.out.println(element);
        System.out.println(multiRouteFinal.pointClosestTo(new PointCh(SwissBounds.MIN_E + 10500, SwissBounds.MIN_N + 6000)));

    }
}