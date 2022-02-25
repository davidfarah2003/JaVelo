package ch.epfl.javelo;

public final class Math2 {

    private Math2(){};

    /**
     * @param x
     * @param y
     * @return
     */
    static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x >= 0 && y > 0);
        return ((x + y - 1) / y);
    }

    /**
     *
     * @param y0
     * @param y1
     * @param x
     * @return the y-coordinate of the point of interest that belongs to the line
     * passing by the points (0,y0) and (1,y1)
     */
    static double interpolate(double y0, double y1, double x){
        // (0,y0) , (1,y1)
        double slope = y1 - y0;
        return Math.fma(slope, x, y0);
    }

    /**
     *
     * @param min
     * @param v
     * @param max
     * @return
     */
    static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        if (v <= min) {
            return min;
        } else if (v >= max) {
            return max;
        } else {
            return v;
        }
    }

    /**
     *
     * @param min
     * @param v
     * @param max
     * @return
     */
    static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min <= max);
        if (v <= min) {
            return min;
        }
        else if (v >= max) {
            return max;
            }
        else {
            return v;
            }
        }

    /**
     * @param x
     * @return the value of the function sinh evaluated at the point of coordinate x
     */
    static double asinh(double x){
        return Math.sinh(x);
    }

    /**
     * @param uX
     * @param uY
     * @param vX
     * @param vY
     * @return the dot product between two vectors
     */
    static double dotProduct(double uX, double uY, double vX, double vY){
        return uX * vX + uY * vY;
    }

    /**
     * @param uX
     * @param uY
     * @return the norm to the square of the vector of interest
     */
    static double squaredNorm(double uX, double uY){
        return dotProduct(uX,uY,uX,uY);
    }

    /**
     * @param uX
     * @param uY
     * @return the norm of a vector
     */
    static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX,uY));
    }

    /**
     * @param aX
     * @param aY
     * @param bX
     * @param bY
     * @param pX
     * @param pY
     * @return the length of the orthogonal projection of a point on a line.
     */
    static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double apX = pX - aX;
        double apY = pY - aY;
        double abX = bX - aX;
        double abY = bY- aY;
        return dotProduct(apX, apY, abX, abY)/norm(abX,abY);
    }

}


