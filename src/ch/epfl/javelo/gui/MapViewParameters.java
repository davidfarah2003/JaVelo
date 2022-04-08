package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;

import java.awt.geom.Point2D;

/**
 * MapViewParameters record
 * @param zoomLevel
 * @param xUpperLeftMapView :
 * @param yUpperLeftMapView
 *
 * @author Wesley Nana Davies (344592)
 * @author David Farah (341017)
 */

public record MapViewParameters(int zoomLevel, double xUpperLeftMapView, double yUpperLeftMapView) {

    /**
     *
     * @return
     */
    public Point2D topLeft(){
        return new Point2D.Double(xUpperLeftMapView, yUpperLeftMapView);
    }

    /**
     *
     * @param newXUpperLeftMapView
     * @param newYUpperLeftMapView
     * @return
     */

    public MapViewParameters withMinXY(double newXUpperLeftMapView, double newYUpperLeftMapView){
        return new MapViewParameters(zoomLevel, newXUpperLeftMapView, newYUpperLeftMapView);
    }

    /**
     *
     * @param xCoordinate
     * @param yCoordinate
     * @return
     */
    public PointWebMercator pointAt(double xCoordinate, double yCoordinate){
        return PointWebMercator.of(zoomLevel, xUpperLeftMapView + xCoordinate,
                yUpperLeftMapView + yCoordinate);
    }

    /**
     *
     * @param point
     * @return
     */
    public double viewX(PointWebMercator point){
        double value = point.xAtZoomLevel(zoomLevel);
        return value - xUpperLeftMapView;
    }

    /**
     *
     * @param point
     * @return
     */
    public double viewY(PointWebMercator point){
        double value = point.yAtZoomLevel(zoomLevel);
        return value - yUpperLeftMapView;
    }
}