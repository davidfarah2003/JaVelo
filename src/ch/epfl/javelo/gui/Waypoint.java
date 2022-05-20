package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

import java.util.Objects;


/**
 *  WayPoint record
 *  This record represents a waypoint
 *
 *  @author Wesley Nana Davies (344592)
 *  @author David Farah (341017)
 */

/**
 * @param point : PointCh which represents a waypoint
 * @param nodeID : nodeID associated to a specific Waypoint
 */
public record Waypoint(PointCh point, int nodeID) {

    /*
     We have overridden the Object equals method to be able to compare
     two waypoints structurally and not by reference.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Waypoint){
            return o.hashCode() == this.hashCode();
        }
        return false;
    }

    /*
    This overridden method computes the hashcode using the node
     */
    @Override
    public int hashCode() {
        return Objects.hash(nodeID);
    }
}
