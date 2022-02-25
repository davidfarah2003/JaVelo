package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static java.lang.Math.PI;

import static java.lang.Math.tan;

public final class WebMercator {
    private WebMercator(){}

    /**
     *
     * @param lon (in radians)
     * @return the x-coordinate (in the Web Mercator system) of the projection of the point of interest
     */
    static double x(double lon){
        return (1/(2*PI)) * (lon + PI);
    }

    /**
     *
     * @param lat (in radians)
     * @return the y-coordinate (in the Web Mercator system) of the projection of the point of interest
     */

    static double y(double lat){
        return (1/(2*PI))*(PI - Math2.asinh(tan(lat)));
    }


    /*


    double x(double lon), qui retourne la coordonnée x de la projection d'un point se trouvant à la longitude lon, donnée en radians,
    double y(double lat), qui retourne la coordonnée y de la projection d'un point se trouvant à la latitude lat, donnée en radians,
    double lon(double x), qui retourne la longitude, en radians, d'un point dont la projection se trouve à la coordonnée x donnée,
    double lat(double y), qui retourne la latitude, en radians, d'un point dont la projection se trouve à la coordonnée y donnée.

     */
}
