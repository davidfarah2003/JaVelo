package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;


/**
 * @author David Farah (341017)
 * @author Wesley Nana Davies(344592)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * @param positionDifference (positive or negative)
     * @return a point identical to the receiver (this) but whose position is offset by the given difference
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
      return new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     * @param that other route point
     * @return <code>this</code> if its distance to the reference
     * is less than that of the argument, and conversely.
     */
    public RoutePoint min(RoutePoint that){
        return (this.distanceToReference <= that.distanceToReference ? this : that);
    }

    /**
     * @param thatPoint other route point
     * @param thatPosition position (m) of thatPoint along the itinerary
     * @param thatDistanceToReference distance to the reference point which is not along the itinerary
     * @return <code>this</code> if its distance to the reference is less than or equal to thatDistanceToReference,
     * and a new instance of RoutePoint whose attributes are the arguments passed to min otherwise.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return (this.distanceToReference <= thatDistanceToReference ? this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference));
    }

}
