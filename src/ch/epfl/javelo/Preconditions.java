package ch.epfl.javelo;

public final class Preconditions {
    private Preconditions(){}

    /**
     * Checks if the provided argument is true and raises an IllegalArgumentException if not.
     * @param shouldBeTrue argument boolean value
     */
    static public void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

}


