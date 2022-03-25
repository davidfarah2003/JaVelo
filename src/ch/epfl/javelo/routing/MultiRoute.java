package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final double routeLength;


    /**
     * builds a multi-route consisting of the given segments, or throws IllegalArgumentException if the list of segments is empty
     * @param segments forming the multi-route
     */
    public MultiRoute(List<Route> segments){
        this.segments = List.copyOf(segments);
        routeLength = calculateLength();
    }

    /**
     * @return length of the multi-route
     */
    private double calculateLength(){
        double length = 0;
        for(Route segment: segments){
            length += segment.length();
        }
        return length;
    }

    /**
     * @param position given position (in meters)
     * @return the index of the segment at the given position of the route (the route)
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, routeLength);

        int segmentIndex = 0;
        for(Route segment : segments){
            if(position > segment.length()){
                position -= segment.length();
                segmentIndex += segment.indexOfSegmentAt(segment.length());
            }
            else{
                segmentIndex += segment.indexOfSegmentAt(position);
            }
        }
        return segmentIndex;
    }

    /**
     * @param position given position (in meters)
     * @return the global index of the segment at the given position of the route (first layer)
     */
    private int globalIndexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, routeLength);
        int segmentIndex = 0;

        for(Route segment : segments){
            if(position > segment.length()){
                position -= segment.length();
                segmentIndex ++;
            }
            else break;
        }
        return segmentIndex;
    }

    /**
     * @return the length of the route, in meters
     */
    @Override
    public double length() {
        return this.routeLength;
    }

    /**
     * @return List containing all the edges of the route
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for(Route segment : segments){
            edges.addAll(segment.edges());
        }
        return edges;
    }

    /**
     * @return List containing all the points located at the extremities of the edges of the route
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for(Route segment : segments){
            points.addAll(segment.points());
        }
        return points;
    }

    /**
     * @param position (Double)
     * @return the point at the given position along the route
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, routeLength);
        Route segment = segments.get(globalIndexOfSegmentAt(position));
        return segment.pointAt(position);
    }

    /**
     * @param position (Double)
     * @return the elevation at a given point along the itinerary, NaN if the edge has no profile
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, routeLength);
        Route segment = segments.get(globalIndexOfSegmentAt(position));
        return segment.elevationAt(position);
    }

    /**
     * @param position (Double)
     * @return the identity of the node belonging to the multi-route and located closest to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, routeLength);
        Route segment = segments.get(globalIndexOfSegmentAt(position));
        return segment.nodeClosestTo(position);
    }

    /**
     * @param point (PointCh)
     * @return the point on the route that is closest to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double distanceToReference = Double.POSITIVE_INFINITY;
        RoutePoint routePoint;
        RoutePoint closestPoint = RoutePoint.NONE;

        for(Route segment : segments){
            routePoint = segment.pointClosestTo(point);
            if(routePoint.distanceToReference() < distanceToReference){
                closestPoint = routePoint;
                distanceToReference = closestPoint.distanceToReference();
            }
        }
        return closestPoint;
    }
}
