package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

import java.util.Objects;

/**
 * represents a waypoint
 */
public record Waypoint(PointCh point, int nodeID) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof Waypoint){
            return o.hashCode() == this.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeID);
    }
}
