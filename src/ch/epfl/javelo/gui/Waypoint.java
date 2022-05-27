package ch.epfl.javelo.gui;
import ch.epfl.javelo.projection.PointCh;

/**
 * WayPoint record
 * This record represents a waypoint
 *
 * @author Wesley Nana Davies (344592)
 * @author David Farah (341017)
 *
 * @param point : PointCh which represents a waypoint
 * @param nodeID : nodeID associated to a specific Waypoint
 */
public record Waypoint(PointCh point, int nodeID) {

    /**
     * Compare two waypoints by their corresponding node id
     * @param o other Waypoint object to compare to
     * @return true iff the 2 waypoints are linked to the same node
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Waypoint) {
            return o.hashCode() == this.hashCode();
        }
        return false;
    }

    /**
     * @return set the Node ID as the hashcode of a Waypoint
     */
    @Override
    public int hashCode() {
        return nodeID;
    }
}
