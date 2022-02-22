package ch.epfl.javelo;

public final class Math2 {

    private Math2(){};

    static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x < 0 || y <= 0);
        return ((x + y - 1) / y);
    }

    static double interpolate(double y0, double y1, double x){
        // (0,y0) , (1,y1)
        double slope = y1 - y0;
        return Math.fma(slope, x, y0);
    }

    static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min <= max);
        if (min > max){
            throw new IllegalArgumentException();
        }
        else if (v <= min){
            return min;
        }
        else if (v >= max){
            return max;
        }
        else {
            return v;
        }
    }

    static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }

    static double dotProduct(double uX, double uY, double vX, double vY){
        return uX * vX + uY * vY;
    }

    static double squaredNorm(double uX, double uY){
        return dotProduct(uX,uY,uX,uY);
    }

    static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX,uY));
    }

    double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double apX = pX - aX;
        double apY = pY - aY;
        double abX = bX - aX;
        double abY = bY- aY;
        return dotProduct(apX, apY, abX, abY)/norm(abX,abY);
    }

}


