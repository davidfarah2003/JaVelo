package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTest {

    @Test
    void x() {
        double longitude = 6.5790772;
        assertEquals(0.518275214444, WebMercator.x(Math.toRadians(longitude)), 1e-6);

    }

    @Test
    void y() {
        double latitude = 46.5218976;
        assertEquals(0.353664894749, WebMercator.y(Math.toRadians(latitude)), 1e-6);
    }

    @Test
    void lon() {
        double x = 0.518275214444;
        assertEquals(Math.toRadians(6.5790772), WebMercator.lon(x), 1e-6);
    }

    @Test
    void lat() {
        double y = 0.353664894749;
        assertEquals(Math.toRadians(46.5218976), WebMercator.lat(y), 1e-6);
    }
}