package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

public record PointWebMercator(double x, double y) {
    /**
     * Constructor
     *
     * @param x-coordinate (Web Mercator system)
     * @param y-coordinate (Web Mercator system
     */
    public PointWebMercator {
        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -(8 + zoomLevel)), Math.scalb(y, -(8 + zoomLevel)));
    }

    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }


    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, 8 + zoomLevel);
    }

    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, 8 + zoomLevel);
    }

    public double lon(){
        return WebMercator.lon(x);
    }

    public double lat(){
        return WebMercator.lat(y);
    }

    public PointCh toPointCh(){
        return new PointCh()

    }

    /*
    double xAtZoomLevel(int zoomLevel), qui retourne la coordonnée x au niveau de zoom donné,
double yAtZoomLevel(int zoomLevel), qui retourne la coordonnée y au niveau de zoom donné,
double lon(), qui retourne la longitude du point, en radians,
double lat(), qui retourne la latitude du point, en radians,
PointCh toPointCh(), qui retourne le point de coordonnées suisses se trouvant à la même position que le récepteur (this)
ou null si ce point n'est pas dans les limites de la Suisse définies par SwissBounds.
     */
}
