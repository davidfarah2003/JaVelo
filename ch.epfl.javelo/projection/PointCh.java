package projection;

import java.util.Vector;

import static java.lang.Math.*;

public record PointCh(double e, double n) {
    public PointCh{
        if (e < SwissBounds.MIN_E || e > SwissBounds.MAX_E || n > SwissBounds.MAX_N || n < SwissBounds.MIN_N)
            throw new IllegalArgumentException();
    }

    double squaredDistanceTo(PointCh that){
        return pow(distanceTo(that), 2);
    }

    double distanceTo(PointCh that){
        double distanceE = abs(this.e - that.e);
        double distanceN = abs(this.n - that.n);
        return sqrt(pow(distanceE, 2) + pow(distanceN, 2));
    }

    double lon(){
        return 0.0;
    }
}
