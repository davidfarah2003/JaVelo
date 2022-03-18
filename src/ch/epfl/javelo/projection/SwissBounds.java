package ch.epfl.javelo.projection;

public final class SwissBounds {
    private SwissBounds(){}

    public final static double MIN_E = 2_485_000;
    public final static double MAX_E = 2_834_000;
    public final static double MIN_N = 1_075_000;
    public final static double MAX_N = 1_296_000;
    public final static double WIDTH = MAX_E - MIN_E;
    public final static double HEIGHT = MAX_N - MIN_N;

    /**
     * checks the if the point is within Switzerland
     * @param e east coordinate
     * @param n north coordinate
     * @return true if it is within, false otherwise
     */
    public static boolean containsEN(double e, double n){
        return (e > MIN_E && e < MAX_E  && n > MIN_N && n < MAX_N);
    }

}


