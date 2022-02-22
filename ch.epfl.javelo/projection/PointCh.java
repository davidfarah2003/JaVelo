package projection;

import java.util.Objects;

import static java.lang.Math.*;
public record PointCh(double e, double n) {
    public PointCh{
        if (e < SwissBounds.MIN_E || e > SwissBounds.MAX_E || n > SwissBounds.MAX_N || n < SwissBounds.MIN_N)
            throw new IllegalArgumentException();
    }


    /**
     * @param that the object to get distance to
     * @return the square of the distance
     */
    double squaredDistanceTo(PointCh that){
        return pow(distanceTo(that), 2);
    }

    /**
     * @param that the object to get distance to
     * @return The distance between this and that
     */
    double distanceTo(PointCh that){
        double distanceE = abs(this.e - that.e);
        double distanceN = abs(this.n - that.n);
        return sqrt(pow(distanceE, 2) + pow(distanceN, 2));
    }

    //resultat en radians
    double lon(){
        return Ch1903.lon(e, n);
    }

    //resultat en radians
    double lat(){
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
