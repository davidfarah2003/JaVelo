package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static java.lang.Math.*;


public final class WebMercator {
    private WebMercator(){}

    /**
     * @param lon (in radians)
     * @return the x-coordinate (in the Web Mercator system) of the projection of the point of interest (WGS84 -> WebMercator)
     */
    public static double x(double lon){
        return (1/(2*PI)) * (lon + PI);
    }

    /**
     * @param lat (in radians)
     * @return the y-coordinate (in the Web Mercator system) of the projection of the point of interest (WGS84 -> WebMercator)
     */
    public static double y(double lat){//?
        return (1/(2*PI))*(PI - Math2.asinh(tan((lat))));
    }

    /**
     * @param x coordinate in the Web Mercator system
     * @return the longitude in radians of the point of interest
     */
    public static double lon(double x){
        return (2*PI*x - PI);
    }

    /**
     * @param y coordinate in the Web Mercator system
     * @return the latitude in radians of the point of interest
     */
    public static double lat(double y){
        return atan(sinh(PI - 2*PI*y));
    }

}
