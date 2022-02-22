package projection;

public class SwissBounds {
        private static double MIN_E = 2_485_000;
        private static double MAX_E = 2_834_000;
        private static double MIN_N = 1_075_000;
        private static double MAX_N = 1_296_000;
        private static double WIDTH = MAX_E - MIN_E;
        private static double HEIGHT = MAX_N - MIN_N;

        public static boolean containsEN(double e, double n){
            return (e > MIN_E && e < MAX_E  && n > MIN_N && n < MAX_N);
        }
    }


