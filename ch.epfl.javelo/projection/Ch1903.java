package projection;

import static java.lang.Math.*;

public final class Ch1903 {
    private Ch1903() {}

    /**
     * @param lon
     * @param lat
     * @return the east coordinate of a point in the Swiss system
     */
    public static double e(double lon, double lat) {
        // Conversion in degrees
        lon = toDegrees(lon);
        lat = toDegrees(lat);


        // lambda -> longitude, phi -> latitude
        double lambda1 = pow(10, -4) * (3_600 * lon - 26_782.5); // 1e-4?
        double phi1 = pow(10, -4) * (3_600 * lat - 169_028.66);
        double east = 2_600_072.37
                     + 211_455.93 * lambda1
                     - 10_938.51 * lambda1 * phi1
                     - 0.36 * lambda1 * pow(phi1, 2)
                     - 44.54 * pow(lambda1, 3);
        return east;
    }

    /**
     *
     * @param lon
     * @param lat
     * @return north coordinate of a point in the Swiss system
     */
    public static double n(double lon, double lat) {
        // Conversion in degrees
        lon = toDegrees(lon);
        lat = toDegrees(lat);

        double lambda1 = pow(10, -4) * (3_600 * lon - 26_782.5);
        double phi1 = pow(10, -4) * (3_600 * lat - 169_028.66);
        double north = 1_200_147.07
                      + 308_807.95 * phi1
                      + 3_745.25 * pow(lambda1,2)
                      + 76.63 * pow(phi1, 2)
                      - 194.56 * pow(lambda1, 2) * phi1
                      + 119.79 * pow(phi1,3);
        return north;

    }

    /**
     *
     * @param e
     * @param n
     * @return longitude in the WGS84 system (RAD)
     */

    public static double lon(double e, double n){
        double x = pow(10, -6) * (e - 2_600_000);
        double y = pow(10, -6) * (n - 1_200_000);
        double lambda0 = 2.6779094
                       + 4.728982 * x
                       + 0.791484 * x * y
                       + 0.1306 * x * pow(y, 2)
                       - 0.0436 * pow(x, 3);

         double longitude = lambda0 * 100/36;
         return toRadians(longitude);
    }

    /**
     *
     * @param e
     * @param n
     * @return latitude in the WGS84 system (RAD)
     */
    public static double lat(double e, double n){
        double x = pow(10, -6) * (e - 2_600_000);
        double y = pow(10, -6) * (n - 1_200_000);
        double phi0 = 16.9023892
                + 3.238272 * y
                - 0.270978 * pow(x, 2)
                - 0.002528 * pow(y, 2)
                - 0.0447 * pow(x, 2) * y
                - 0.0140 * pow(y, 3);
        double latitude = phi0 * 100/36;
        return toRadians(latitude);
    }
}

