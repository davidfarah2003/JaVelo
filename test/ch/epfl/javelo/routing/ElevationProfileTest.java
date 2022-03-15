package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {

    @Test
    void ConstructorThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-0.3, new float[]{5.67f, 98.6f}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0, new float[]{5.67f, 98.6f}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0.7, new float[]{5.67f}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0.7, new float[]{}));
    }

    @Test
    void lengthWorks(){
        ElevationProfile profile = new ElevationProfile(56, new float[]{45.7f, 57.9f});
        assertEquals(56, profile.length());
    }

    @Test
    void minElevationWorks(){
        ElevationProfile profile = new ElevationProfile(10, new float[]{45.7f, 57.9f, 35.8f, 20f, 21f});
        assertEquals(20f, profile.minElevation());
        profile = new ElevationProfile(10, new float[]{45.7f, 57.9f, 35.8f, 21f});
        assertEquals(21f, profile.minElevation());
    }


    @Test
    void maxElevationWorks(){
        ElevationProfile profile = new ElevationProfile(10, new float[]{45.7f, 57.9f, 35.8f, 20f, 21f});
        assertEquals(57.9f, profile.maxElevation());
    }

    @Test
    void totalAscentWorks(){
        ElevationProfile profile = new ElevationProfile(10, new float[]{45.7f, 57.9f, 35.8f, 20f, 21f});
        assertEquals(13.2f, profile.totalAscent(), 1e-4);
    }

    @Test
    void totalDescentWorks(){
        ElevationProfile profile = new ElevationProfile(10, new float[]{45.7f, 57.9f, 35.8f, 20f, 21f});
        assertEquals(37.9f, profile.totalDescent());

    }

    @Test
    void elevationAtWorks(){
        ElevationProfile profile = new ElevationProfile(10, new float[]{45.7f, 57.9f, 35.8f, 20f, 21f});
        assertEquals(57.9f , profile.elevationAt(2.5));
        assertEquals(35.8f, profile.elevationAt(5));
        assertEquals((57.9f + 35.8f)/2 , profile.elevationAt(3.75), 1e-4);
        assertEquals(21f, profile.elevationAt(15));
        assertEquals(45.7f, profile.elevationAt(-5));
    }
}