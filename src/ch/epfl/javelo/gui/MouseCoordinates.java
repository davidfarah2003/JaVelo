package ch.epfl.javelo.gui;

public class MouseCoordinates {
    private double x;
    private double y;

    MouseCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
