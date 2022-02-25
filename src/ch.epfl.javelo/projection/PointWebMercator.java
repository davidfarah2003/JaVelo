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

    /**
     * @param zoomLevel
     * @param x
     * @param y
     * @return
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -(8 + zoomLevel)), Math.scalb(y, -(8 + zoomLevel)));
    }

    /**
     *
     * @param pointCh
     * @return
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }


    /**
     *
     * @param zoomLevel
     * @return the x-coordinate for a certain zoom level
     */
    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, 8 + zoomLevel);
    }

    /**
     *
     * @param zoomLevel
     * @return the y-coordinate for a certain zoom level
     */
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, 8 + zoomLevel);
    }

    /**
     *
     * @return the longitude of the point
     */
    public double lon(){
        return WebMercator.lon(x);
    }

    /**
     *
     * @return the latitude of the point
     */
    public double lat(){
        return WebMercator.lat(y);
    }

    /**
     *
     * @return a point in the Swiss coordinates system if it exists
     */
    public PointCh toPointCh() {
        // Getting the Swiss coordinates of the point
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        return (!SwissBounds.containsEN(e, n) ? null : new PointCh(e, n));
    }
}
