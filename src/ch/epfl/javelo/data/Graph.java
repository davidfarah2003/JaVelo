package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;


    /**
     * @param basePath the path to the directory of the stored files
     * @return returns the JaVelo graph obtained from the files located in basePath
     * @throws IOException in the event of an input/output error
     */
    public static Graph loadFrom(Path basePath) throws IOException{
        return this;
    }

    /**
     * Constructor of the class
     * @param nodes graph nodes
     * @param sectors graph sectors
     * @param edges graph edges
     * @param attributeSets attributeSets List
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }

    /**
     * @return the total number of nodes in the graph
     */
    int nodeCount(){
        return 0;
    }

    /**
     * @param nodeId the id of the node
     * @return the position of the given identity node
     */
    PointCh nodePoint(int nodeId){
    }

    /**
     * @param nodeId the id of the node
     * @return the number of edges leaving the given identity node
     */
    int nodeOutDegree(int nodeId){

    }

    /**
     * @param nodeId the id of the node
     * @param edgeIndex index of the edge
     * @return the identity of the edgeIndex-th edge outgoing from the identity node nodeId
     */
    int nodeOutEdgeId(int nodeId, int edgeIndex){

    }

    /**
     * @param point coordinate point
     * @param searchDistance distance (radius) of the search
     * @return the identity of the node closest to the given point, at the given maximum distance (in meters),
     * or -1 if no node matches these criteria
     */
    int nodeClosestTo(PointCh point, double searchDistance){

    }

    /**
     * @param edgeId the id of the edge
     * @return the identity of the destination node of the given identity edge
     */
    int edgeTargetNodeId(int edgeId){

    }

    /**
     * @param edgeId the id of the edge
     * @return true iff the given identity edge goes in the opposite direction of the OSM channel it comes from
     */
    boolean edgeIsInverted(int edgeId){

    }

    /**
     * @param edgeId the id of the edge
     * @return the set of OSM attributes attached to the given identity edge
     */
    AttributeSet edgeAttributes(int edgeId){

    }

    /**
     * @param edgeId the id of the edge
     * @return the length, in meters, of the given identity edge
     */
    double edgeLength(int edgeId){

    }

    /**
     * @param edgeId the id of the edge
     * @returnthe total elevation gain of the given identity edge
     */
    double edgeElevationGain(int edgeId){

    }

    /**
     * @param edgeId the id of the edge
     * @return the longitudinal profile of the given identity edge, as a function,
     * and Double.NaN if the edge has no profile
     */
    DoubleUnaryOperator edgeProfile(int edgeId){

    }

}
