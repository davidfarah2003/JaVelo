package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;

import java.awt.geom.Point2D;

public record MapViewParameters(int zoomLevel, int xCoordinate, int yCoordinate) {
    public Point2D topLeft(){
        return new Point2D.Double(xCoordinate, yCoordinate);
    }

    public MapViewParameters withMinXY(int xCoordinate, int yCoordinate){
        return new MapViewParameters(zoomLevel, xCoordinate, yCoordinate);
    }

    public PointWebMercator pointAt(int xCoordinate, int yCoordinate){
        return PointWebMercator.of(zoomLevel, xCoordinate, yCoordinate);
    }

    public int viewX(PointWebMercator point){
        double value = point.xAtZoomLevel(zoomLevel);
        return (int) Math.floor(value / 256);
    }

    public int viewY(PointWebMercator point){
        double value = point.yAtZoomLevel(zoomLevel);
        return (int) Math.floor(value / 256);
    }
}