package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final double routeLength;
    //private final double segmentsLength[];
    private final int numberOfSingleRoutes;




    /**
     * builds a multi-route consisting of the given segments, or throws IllegalArgumentException if the list of segments is empty
     * @param segments forming the multi-route
     */
    public MultiRoute(List<Route> segments){
        this.segments = List.copyOf(segments);
        routeLength = calculateLength();
        numberOfSingleRoutes = computeNumberOfSingleRoutes();
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
    /*
    public double[] computeSegmentsLength() {
        double[] segmentsLength = new double[numberOfSingleRoutes + 1];
        double totalLength = 0;
        int index = 1;
            for (int j = 0; j < segments.size(); j++){
                if (segments.get(j) instanceof MultiRoute) {
                    for (int k = 0; k < ((MultiRoute) segments.get(j)).segments.size(); k++) {
                        totalLength += ((MultiRoute) segments.get(j)).segments.get(k).length();
                        segmentsLength[index] = totalLength;
                        index++;
                    }
                }
                else{
                    totalLength += segments.get(j).length();
                    segmentsLength[index] = totalLength;
                    index++;
                    }
                }
               return segmentsLength;
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
        if(position == routeLength) return numberOfSingleRoutes - 1;

        //if (position == routeLength) {
       //     return segments.size() - 1;
      //  }

        for (Route segment : segments) {
            if (position >= segment.length()) {
                position -= segment.length();
                if (segment instanceof MultiRoute)
                    segmentIndex += ((MultiRoute) segment).computeNumberOfSingleRoutes();
                else{
                    segmentIndex += 1;
                }
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
        List <PointCh> points = new ArrayList<>();
        ListIterator<Route> segmentIterator = segments.listIterator();
        Route segment;

        while(segmentIterator.hasNext()) {
            segment = segmentIterator.next();
            points.addAll(segment.points().subList(0, segment.points().size() - 1));

            if(!segmentIterator.hasNext()){
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
        double length = 0;
        for (int i = 0; i < index; i++){
            length += segments.get(i).length();
        }
        return segment.nodeClosestTo(position - length);
    }

    /**
     * @param point (PointCh)
     * @return the point on the route that is closest to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double distanceToReference = Double.POSITIVE_INFINITY;
        RoutePoint routePoint;
        RoutePoint closestPoint = new RoutePoint(null, 0, Double.POSITIVE_INFINITY);


        for(Route segment : segments){
            routePoint = segment.pointClosestTo(point);
            if(routePoint.distanceToReference() < distanceToReference){
                closestPoint = routePoint.withPositionShiftedBy(closestPoint.position());
                distanceToReference = closestPoint.distanceToReference();

            }
        }
        
        return closestPoint;
    }
}
