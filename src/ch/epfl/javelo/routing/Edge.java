package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.util.function.DoubleUnaryOperator;

/**
 * The purpose of this record is to represent all the edges that belong to a specific itinerary
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint,
                   double length, DoubleUnaryOperator profile){

    /**
     * Another constructor for Edge
     * @param graph :  graph which models nodes and edges.
     * @param edgeId: ID of the Edge
     * @param fromNodeId : ID of the starting node
     * @param toNodeId : ID of the ending node
     * @return a new Edge
     */

    static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId),
                graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     *
     * @param point (PointCh)
     * @return  the position along the edge, in meters, that is closest to the given point,
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     *
     * @param position
     * @return the point at the given position on the edge, expressed in meters.
     */

    public PointCh pointAt(double position){
        double proportion = position/length;
        double east = Math2.interpolate(fromPoint.e(), toPoint.e(), proportion);
        double north = Math2.interpolate(fromPoint.n(), toPoint.n(), proportion);
        return new PointCh(east, north);
    }


    /**
     *
     * @param position
     * @return the altitude, in meters, at the given position on the edge.
     */
    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }
}
