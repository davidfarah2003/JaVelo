package ch.epfl.javelo.gui;

import java.awt.geom.Point2D;

public record MapViewParameters(int zoomLevel, int xCoordinate, int yCoordinate) {
    public Point2D topLeft(){
        return new Point2D(0, 0) {
            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public void setLocation(double x, double y) {

            }
        };
    }
}
