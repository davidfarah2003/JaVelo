package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;


/**
 * Record that represents a RoutePoint (Point on a route)
 * @author David Farah (341017)
 * @author Wesley Nana Davies(344592)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * Returns a route point identical to <code>this</code> but whose position is offset by the given difference
     * @param positionDifference (positive or negative)
     * @return a RoutePoint
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
      return new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     * Returns <code>this</code> if its distance to the reference is less than that of the argument, and conversely.
     * @param that other route point
     * @return a RoutePoint
     */
    public RoutePoint min(RoutePoint that){
        return (this.distanceToReference <= that.distanceToReference ? this : that);
    }

    /**
     * Returns <code>this</code> if its distance to the reference is less than or equal to thatDistanceToReference,
     * and a new instance of RoutePoint whose attributes are the arguments passed to min otherwise.
     *
     * @param thatPoint other route point
     * @param thatPosition position (m) of thatPoint along the itinerary
     * @param thatDistanceToReference distance to the reference point which is not along the itinerary
     * @return a RoutePoint
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return (this.distanceToReference <= thatDistanceToReference ? this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference));
    }

}
