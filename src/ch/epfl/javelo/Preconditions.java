package ch.epfl.javelo;

/**
 * Class that implements Precondition methods
 * @author David Farah (341017)
 * @author Wesley Nana Davies(344592)
 */
public final class Preconditions {
    private Preconditions(){}

    /**
     * Checks if the provided argument is true and raises an IllegalArgumentException if not.
     * @param shouldBeTrue argument boolean value
     * @throws IllegalArgumentException if the condition is not true
     */
    static public void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

}


