package projection;
import static java.lang.Math.pow;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public final class Ch1903 {
    private Ch1903(){}

    /**
     *
     * @param lon
     * @param lat
     * @return the east coordinate of a point in the WGS84 system
     */
    public static double e(double lon, double lat){
        lon = toDegrees(lon);
        lat = toDegrees(lat);

        // Conversion in degrees
        // lambda longitude, phi latitude
        double lambda1 = pow(10, -4) * (3_600 * lon - 26_782.5);
        double phi1 =  pow(10, -4) * (3_600 * lat - 169_028.66);
        double east  =   2_600_072.37 + 211_455.93 * lambda1 - 10_938.51 * lambda1 * phi1
                        - 0.36 * lambda1 * pow(phi1, 2) - 44.54 * pow(lambda1, 3);
        return toRadians(east);
    }

    public static double n(double lon, double lat){
        lon = toDegrees(lon);
        lat = toDegrees(lat);

        double lambda1 = pow(10, -4) * (3_600 * Math.toDegrees(lon) - 26_782.5);
        double phi1 =  pow(10, -4) * (3_600 * Math.toDegrees(lat) - 169_028.66);
    }

}
