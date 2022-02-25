package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static java.lang.Math.PI;
import static java.lang.Math.tan;
import static java.lang.Math.atan;
import static java.lang.Math.sinh;

public final class WebMercator {
    private WebMercator(){}

    /**
     *
     * @param lon (in radians)
     * @return the x-coordinate (in the Web Mercator system) of the projection of the point of interest
     */
    private static double x(double lon){ //?
        return (1/(2*PI)) * (lon + PI);
    }

    /**
     *
     * @param lat (in radians)
     * @return the y-coordinate (in the Web Mercator system) of the projection of the point of interest
     */

    private static double y(double lat){//?
        return (1/(2*PI))*(PI - Math2.asinh(tan(lat)));
    }

    /**
     *
     * @param x (coordinate in the Web Mercator system)
     * @return the longitude in radians of the point of interest
     */
    public static double lon(double x){
        return 2*PI*x - PI;
    }

    /**
     *
     * @param y (coordinate in the Web Mercator system)
     * @return the latitude in radians of the point of interest
     */

    public static double lat(double y){
        return atan(sinh(PI - 2*PI*y));
    }

    // Added
    public static boolean isContained(double x, double y){
        return (x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    /*


    double x(double lon), qui retourne la coordonnée x de la projection d'un point se trouvant à la longitude lon, donnée en radians,
    double y(double lat), qui retourne la coordonnée y de la projection d'un point se trouvant à la latitude lat, donnée en radians,
    double lon(double x), qui retourne la longitude, en radians, d'un point dont la projection se trouve à la coordonnée x donnée,
    double lat(double y), qui retourne la latitude, en radians, d'un point dont la projection se trouve à la coordonnée y donnée.

     */
}
