package ch.epfl.javelo.projection;
import ch.epfl.javelo.Preconditions;

import java.util.Objects;
import static java.lang.Math.*;


public record PointCh(double e, double n) {
    /**
     * Constructor
     * @param e coordinates east (meters)
     * @param n coordinates north (meters)
     */
    public PointCh{
        Preconditions.checkArgument(SwissBounds.containsEN(e,n));
    }

    /**
     * @param that the object to get distance to
     * @return the square of the distance
     */
    public double squaredDistanceTo(PointCh that){
        return pow(distanceTo(that), 2);
    }

    /**
     * @param that the object to get distance to
     * @return The distance between this and that
     */
    public double distanceTo(PointCh that){
        double distanceE = abs(this.e - that.e);
        double distanceN = abs(this.n - that.n);
        return Math.hypot(distanceE, distanceN);
    }


    /**
     * @return longitude in rad W... system (RAD)
     */
    public double lon(){
        return Ch1903.lon(e, n);
    }

    /**
     * @return latitude in rad W... system (RAD)
     */
    public double lat(){
        return Ch1903.lat(e, n);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointCh pointCh = (PointCh) o;
        return Double.compare(pointCh.e, e) == 0 && Double.compare(pointCh.n, n) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(e, n);
    }

    @Override
    public String toString() {
        return "PointCh{" +
                "e=" + e +
                ", n=" + n +
                '}';
    }
}
