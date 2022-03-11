package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     *
     * @param positionDifference (positive or negative)
     * @return a point identical to the receiver (this) but whose position is offset by the given difference
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
      return new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     *
     * @param that
     * @return <code>this</code> if its distance to the reference
     * is less than that of the input, and conversely.
     */
    public RoutePoint min(RoutePoint that){
        return (this.distanceToReference <= that.distanceToReference ? this : that);
    }

    /**
     *
     * @param thatPoint
     * @param thatPosition
     * @param thatDistanceToReference
     * @return  <code>this</code> if its distance to the reference is less than or equal to thatDistanceToReference,
     * and a new instance of RoutePoint whose attributes are the arguments passed to min otherwise.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return (this.distanceToReference <= thatDistanceToReference ? this : new RoutePoint(thatPoint,thatPosition,thatDistanceToReference));
    }


// position (itineraire cmplet)
    // distance to reference longueur projection orthogonale)
    // point appartient ititneraire.
// Point P la reference
}
