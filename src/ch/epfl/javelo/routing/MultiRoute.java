package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final double routeLength;
    private final double[] segmentsLength;
    private final int numberOfSingleRoutes;


    /** Constructor of the class
     * Builds a multi-route consisting of the given segments, or throws IllegalArgumentException if the list of segments is empty
     * @param segments forming the multi-route
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        routeLength = calculateLength();
        numberOfSingleRoutes = computeNumberOfSingleRoutes();
        segmentsLength = buildSegmentsLength();
    }

    public double[] buildSegmentsLength() {
        double[] segmentsLength = new double[segments.size() + 1];
        int i = 1;
        double value = 0;
        for (Route segment : segments){
            value += segment.length();
            segmentsLength[i] = value;
            i++;
        }
        return segmentsLength;
    }

    public int computeNumberOfSingleRoutes() {
        int count = 0;
        for (Route segment : segments){
            if (segment instanceof MultiRoute){
                count += ((MultiRoute) segment).computeNumberOfSingleRoutes();
            }
            else{
                count +=1;
            }
        }
        return count;
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
        double segmentLength;

        for (Route segment : segments) {
            segmentLength = segment.length();

            if(position == segmentLength){
                segmentIndex += segment.indexOfSegmentAt(segmentLength);
                break;
            }
            else if (position > segment.length()) {
                position -= segmentLength;
                segmentIndex += segment.indexOfSegmentAt(position) == 0 ?
                        1 :
                        segment.indexOfSegmentAt(segmentLength);
            } else {
                segmentIndex += segment.indexOfSegmentAt(position);
                break;
            }
        }

        return segmentIndex;
    }
        /*
        position = Math2.clamp(0, position, routeLength);

        int segmentIndex = 0;

        if(position == routeLength){
            return edges().size()-1;
        }

        for(Route segment : segments){
            if(position >= segment.length()){
                position -= segment.length();
                segmentIndex += segment.edges().size();
            }
            else{
                segmentIndex += segment.indexOfSegmentAt(position);
                break;
            }
        }

        return segmentIndex;
    }

         */

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
        Set<PointCh> set = new LinkedHashSet<>();
        for(Route segment : segments){
            set.addAll(segment.points());
        }
        return new ArrayList<>(set);

       // List <PointCh> points = new ArrayList<>();
      //  ListIterator<Route> segmentIterator = segments.listIterator();
      //  Route segment;

    //    while(segmentIterator.hasNext()) {
       //     segment = segmentIterator.next();
       //     points.addAll(segment.points().subList(0, segment.points().size() - 1));

        //    if(!segmentIterator.hasNext()){
       //         int lastPointIndex = segment.points().size()-1;
        //        points.add(segment.points().get(lastPointIndex));
       //     }
      //  }

     //   return points;
    }

    /**
     * @param position (Double)
     * @return the point at the given position along the route
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, routeLength);
        int index = globalIndexOfSegmentAt(position);
        Route segment = segments.get(index);
        double length = 0;
        for (int i = 0; i < index; i++){
            length += segments.get(i).length();
        }
        return segment.pointAt(position - length);
    }

    /**
     * @param position (Double)
     * @return the elevation at a given point along the itinerary, NaN if the edge has no profile
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, routeLength);
        int index = globalIndexOfSegmentAt(position);
        Route segment = segments.get(index);
        double length = 0;
        for (int i = 0; i < index; i++){
            length += segments.get(i).length();
        }
        return segment.elevationAt(position - length);
    }

    /**
     * @param position (Double)
     * @return the identity of the node belonging to the multi-route and located closest to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, routeLength);
        int index = globalIndexOfSegmentAt(position);
        Route segment = segments.get(index);
        double length = getShift(index);
        return segment.nodeClosestTo(position - length);
    }

    private double getShift(int index){
        //int index = globalIndexOfSegmentAt(position);
        double length = 0;
        for (int i = 0; i < index; i++){
            length += segments.get(i).length();
        }
        return length;
    }

    /**
     * @param point (PointCh)
     * @return the point on the route that is closest to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint routePoint;
        RoutePoint closestPoint = RoutePoint.NONE;

        for (int j = 0; j < segments.size(); j++){
            routePoint = segments.get(j).
                    pointClosestTo(point).
                    withPositionShiftedBy(segmentsLength[j]);

            closestPoint = closestPoint.min(routePoint);
        }
        return closestPoint;
    }
}
