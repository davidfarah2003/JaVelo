package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SingleRoute implements Route {
    private final List<Edge> edges;
    private final List<PointCh> points;
    private final double[] edgesSearch;
    private final double routeLength;


    /**
     * @param edges given edges that form the route
     *              constructs the simple route composed of the given edges, or throws IllegalArgumentException if the list of edges is empty
     */
    SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        this.routeLength = calculateLength();
        this.edgesSearch = buildEdgesSearch();
        this.points = List.copyOf(buildPoints());
    }


    /**
     * @return length of the route calculated using the list of edges
     */
    private double calculateLength() {
        double length = 0;
        for (Edge edge : edges) {
            length += edge.length();
        }
        return length;
    }

    /**
     * @return double array containing the position of the end nodes of each edge used to determine the index
     * of an edge by dichotomous search
     */
    private double[] buildEdgesSearch() {
        double[] edgesSearch = new double[edges.size() + 1];
        int i = 1;
        double totalLength = 0;

        for (Edge edge : this.edges) {
            totalLength += edge.length();
            edgesSearch[i] = totalLength;
            i++;
        }
        return edgesSearch;
    }


    private List<PointCh> buildPoints() {
        List<PointCh> points = new ArrayList<>();
        points.add(edges.get(0).fromPoint());
        for (Edge edge : this.edges) {
            points.add(edge.toPoint());
        }
        return points;
    }

    /**
     * @param position given position (in meters)
     * @return the index of the segment at the given position of the route (in meters), which is always 0 in
     * the case of a simple route
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * @return the length of the route, in meters
     */
    @Override
    public double length() {
        return routeLength;
    }

    /**
     * @return List containing all the edges of the route
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * @return List containing all the points located at the extremities of the edges of the route
     */
    @Override
    public List<PointCh> points() {
        return points;
    }

    /**
     * @param position (Double)
     * @return the point at the given position along the route
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, routeLength);
        int result = Arrays.binarySearch(edgesSearch, position);

        if (result >= 0) {
            return points.get(result);
        } else {
            int edgeIndex = -result - 2;
            double x = position - edgesSearch[edgeIndex];
            return edges.get(edgeIndex).pointAt(x);
        }
    }

    /**
     * @param position (Double)
     * @return the elevation at a given point along the itinerary
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, routeLength);

        int result = Arrays.binarySearch(edgesSearch, position);

        if (result >= 0) {
            //checks if we are on the last node
            return result == edges.size() ?
                    edges.get(result - 1).elevationAt(edges.get(result - 1).length())
                    : edges.get(result).elevationAt(0);
        } else {
            //get index of the next closest node (if the position is not on an end node)
            int edgeIndex = -result - 2;
            double x = position - edgesSearch[edgeIndex];
            return edges.get(edgeIndex).elevationAt(x);
        }
    }

    /**
     * @param position (Double)
     * @return the identity of the node belonging to the route and located closest to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, routeLength);
        int result = Arrays.binarySearch(edgesSearch, position);

        if (result == edges.size()) {
            return edges.get(result - 1).toNodeId();
        } else if (result >= 0) {
            return edges.get(result).fromNodeId();

        } else {
            int edgeIndex = -result - 2;
            double positionStartNodeId = edgesSearch[edgeIndex];
            double positionEndNodeId = edgesSearch[edgeIndex + 1];

            if (position - positionStartNodeId <= positionEndNodeId - position) {
                return edges.get(edgeIndex).fromNodeId();
            } else {
                return edges.get(edgeIndex).toNodeId();
            }
        }
    }

    /**
     * @param point (PointCh)
     * @return the point on the route that is closest to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double clamped;
        double distanceToReferenceEdgeI;
        double projectionLengthEdgeI;
        RoutePoint RoutePointClosestTo = RoutePoint.NONE;

        for (int i = 0; i < edges.size(); i++) {
            projectionLengthEdgeI = edges.get(i).positionClosestTo(point);
            clamped = Math2.clamp(0, projectionLengthEdgeI, edges.get(i).length());
            distanceToReferenceEdgeI = edges.get(i).pointAt(clamped).distanceTo(point);
            RoutePointClosestTo = RoutePointClosestTo.min(edges.get(i).pointAt(clamped), edgesSearch[i] + clamped, distanceToReferenceEdgeI);
        }

        return RoutePointClosestTo;
    }
}
