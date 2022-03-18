package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

public record PointWebMercator(double x, double y) {
    /**
     * Constructor
     * @param x-coordinate (Web Mercator system)
     * @param y-coordinate (Web Mercator system
     */
    public PointWebMercator {
        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    /**
     * @param zoomLevel current zoom level
     * @param x input coordinate with applied zoom (Web Mercator)
     * @param y input coordinate with applied zoom (Web Mercator)
     * @return new PointWebMercator with corresponding coordinates at zoom 0
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -(8 + zoomLevel)), Math.scalb(y, -(8 + zoomLevel)));
    }

    /**
     * PoinctCh -> WGS84 -> WebMercator
     * @param pointCh input coordinate in CH format
     * @return PointWebMercator with same coordinates as input pointCh
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }


    /**
     * @param zoomLevel current zoom level
     * @return the x-coordinate for a certain zoom level
     */
    public double xAtZoomLevel(int zoomLevel){
        Preconditions.checkArgument(zoomLevel >= 0 && zoomLevel <= 20);
        return Math.scalb(x, 8 + zoomLevel);
    }

    /**
     * @param zoomLevel current zoom level
     * @return the y-coordinate for a certain zoom level
     */
    public double yAtZoomLevel(int zoomLevel){
        Preconditions.checkArgument(zoomLevel >= 0 && zoomLevel <= 20);
        return Math.scalb(y, 8 + zoomLevel);
    }

    /**
     * @return the longitude of the point
     */
    public double lon(){
        return WebMercator.lon(x);
    }

    /**
     * @return the latitude of the point
     */
    public double lat(){
        return WebMercator.lat(y);
    }

    /**
     * @return a point in the Swiss coordinates system if it exists
     */
    public PointCh toPointCh() {
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        return (!SwissBounds.containsEN(e, n) ? null : new PointCh(e, n));
    }
}
