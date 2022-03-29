package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;


/**
 * Class that represents a Multi-Route, which is formed from other routes (type Route)
 * A segment is considered to be SingleRoute
 * (but is the class attribute <code>segments</code> there could be any object of type Route).
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final double routeLength;


    /** Constructor of the class
     * Builds a multi-route consisting of the given segments,
     * or throws IllegalArgumentException if the list of segments is empty
     * @param segments forming the multi-route
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        routeLength = calculateLength();
    }


    /**
     * @param index of the route to know the distance before
     * @return the length of the route up until the specified route index (exclusive)
     */
    private double lengthBeforeRoute(int index){
        Preconditions.checkArgument(index < segments.size() && index >= 0);

        double length = 0;
        for(Route route : segments.subList(0, index)){
            length += route.length();
        }
        return length;
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
        int globalIndex = globalIndexOfSegmentAt(position);
        int previousLength = 0;

        for (Route route : segments.subList(0, globalIndex)) {
            previousLength += route.length();
            segmentIndex += route.indexOfSegmentAt(route.length()) + 1;
        }

        segmentIndex += segments.get(globalIndex).indexOfSegmentAt(position - previousLength);

        return segmentIndex;
    }


    /**
     * @param position given position (in meters)
     * @return the global index of the segment at the given position of the route (first layer)
     */
    private int globalIndexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, routeLength);
        if(position == routeLength) return segments.size()-1;

        int segmentIndex = 0;
        for(Route segment : segments){
            if(position >= segment.length()){
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
        List <PointCh> points = new ArrayList<>();
        ListIterator<Route> segmentIterator = segments.listIterator();
        Route segment;

        while(segmentIterator.hasNext()) {
            //add all points except last
            segment = segmentIterator.next();
            points.addAll(segment.points().subList(0, segment.points().size() - 1));

            if(!segmentIterator.hasNext()){
                //add the last point
                int lastPointIndex = segment.points().size()-1;
                points.add(segment.points().get(lastPointIndex));
            }
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

        int routeIndex = globalIndexOfSegmentAt(position);
        return segments.get(routeIndex).pointAt(position - lengthBeforeRoute(routeIndex));
    }


    /**
     * @param position (Double)
     * @return the elevation at a given point along the itinerary, NaN if the edge has no profile
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, routeLength);

        int routeIndex = globalIndexOfSegmentAt(position);
        return segments.get(routeIndex).elevationAt(position - lengthBeforeRoute(routeIndex));
    }


    /**
     * @param position (Double)
     * @return the identity of the node belonging to the multi-route and located closest to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, routeLength);

        int routeIndex = globalIndexOfSegmentAt(position);
        return segments.get(routeIndex).nodeClosestTo(position - lengthBeforeRoute(routeIndex));
    }


    /**
     * @param point (PointCh)
     * @return the point on the route that is closest to the given reference point (RoutePoint)
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint currentPoint;
        RoutePoint closestPoint = RoutePoint.NONE;

        for (int index = 0; index < segments.size(); index++){
            currentPoint = segments.get(index).
                    pointClosestTo(point).
                    withPositionShiftedBy(lengthBeforeRoute(index));

            closestPoint = closestPoint.min(currentPoint);
        }
        return closestPoint;
    }
}
