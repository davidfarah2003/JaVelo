package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {

    @Test
    void of() {
        PointWebMercator test = PointWebMercator.of(19, 69_561_722, 47_468_099);
        assertEquals( 0.518275214444, test.x(), 1e-6);
        assertEquals(0.353664894749, test.y(), 1e-6);
    }

    @Test
    void ofPointCh() {
        PointCh learningCenter = new PointCh(2_533_132, 1_152_206);
        PointWebMercator test = PointWebMercator.ofPointCh(learningCenter);
        //System.out.println(test.x());
        //System.out.println(test.y());
    }

    @Test
    void xAtZoomLevel() {

    }

    @Test
    void yAtZoomLevel() {
    }

    @Test
    void lon() {
    }

    @Test
    void lat() {
    }

    @Test
    void toPointChTest() {
        PointWebMercator pointTest = new PointWebMercator( 0.518275214444, 0.253664894749);
        PointCh pointCh = pointTest.toPointCh();
        //pointCh.e();

    }
}