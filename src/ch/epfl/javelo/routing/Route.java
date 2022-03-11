package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

public interface Route {
    /**
     * @param position
     * @return
     */
    int indexOfSegmentAt(double position);

    /**
     * @return
     */
    double length();

    /**
     * @return
     */
    List<Edge> edges();

    /**
     * @return
     */
    List<PointCh> points();

    /**
     * @param position
     * @return
     */
    PointCh pointAt(double position);

    /**
     * @param position
     * @return
     */
    int nodeClosestTo(double position);

    /**
     * @param point
     * @return
     */
    RoutePoint pointClosestTo(PointCh point);
}
