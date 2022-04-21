package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;


/**
 * MapViewParameters record, represents the settings of the basemap presented in the GUI
 * @param zoomLevel of the map
 * @param xUpperLeftMapView
 * @param yUpperLeftMapView
 *
 * @author Wesley Nana Davies (344592)
 * @author David Farah (341017)
 */
public record MapViewParameters(int zoomLevel, double xUpperLeftMapView, double yUpperLeftMapView) {

    /**
     * @return the coordinates of the top-left corner as an object of type Point2D
     *         — the type used by JavaFX to represent points
     */
    public Point2D topLeft(){
        return new Point2D(xUpperLeftMapView, yUpperLeftMapView);
    }

    /**
     * @param newXUpperLeftMapView X coordinate to override
     * @param newYUpperLeftMapView Y coordinate to override
     * @return an instance of MapViewParameters identical to this, except that the coordinates of the top-left corner
     *         are those passed as arguments to the method
     */
    public MapViewParameters withMinXY(double newXUpperLeftMapView, double newYUpperLeftMapView){
        return new MapViewParameters(zoomLevel, newXUpperLeftMapView, newYUpperLeftMapView);
    }

    /**
     * @param xCoordinate x coordinate of a point, expressed in relation to the top-left corner of the map portion
     *                    displayed on the screen
     * @param yCoordinate y coordinate of a point, expressed in relation to the top-left corner of the map portion
     *                    displayed on the screen
     * @return a PointWebMercator with coordinates (xCoordinate, yCoordinate)
     */
    public PointWebMercator pointAt(double xCoordinate, double yCoordinate){
        return PointWebMercator.of(zoomLevel, xUpperLeftMapView + xCoordinate,
                yUpperLeftMapView + yCoordinate);
    }

    /**
     * @param point a PointWebMercator
     * @return the corresponding x position, expressed relative to the top-left corner of the map portion
     *         displayed on the screen
     */
    public double viewX(PointWebMercator point){
        double value = point.xAtZoomLevel(zoomLevel);
        return value - xUpperLeftMapView;
    }

    /**
     * @param point a PointWebMercator
     * @return the corresponding y position, expressed relative to the top-left corner of the map portion
     *         displayed on the screen
     */
    public double viewY(PointWebMercator point){
        double value = point.yAtZoomLevel(zoomLevel);
        return value - yUpperLeftMapView;
    }
}