package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
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
        ByteBuffer sectorsBuffer, edgesBuffer;
        ShortBuffer elevationsBuffer;
        IntBuffer nodesBuffer, profileIdsBuffer;
        LongBuffer attributesBuffer;

        nodesBuffer = extractBuffer(basePath.resolve("nodes.bin")).asIntBuffer();
        profileIdsBuffer = extractBuffer(basePath.resolve("profile_ids.bin")).asIntBuffer();
        sectorsBuffer =  extractBuffer(basePath.resolve("sectors.bin"));
        edgesBuffer =  extractBuffer(basePath.resolve("edges.bin"));
        elevationsBuffer =  extractBuffer(basePath.resolve("elevations.bin")).asShortBuffer();
        attributesBuffer = extractBuffer(basePath.resolve("attributes.bin")).asLongBuffer();

        GraphNodes nodes = new GraphNodes(nodesBuffer);
        GraphSectors sectors = new GraphSectors(sectorsBuffer);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer);

        List<AttributeSet> attributeSets = new ArrayList<>();
        for(int i = 0; i < attributesBuffer.capacity(); i++){
            attributeSets.add(new AttributeSet(attributesBuffer.get(i)));
        }

        return new Graph(nodes, sectors, edges, attributeSets);
    }

    /**
     * @param pathFile path to the binary file we want ti extract from (Path)
     * @return extracted buffer from file
     * @throws IOException in the event of an input/output error
     */
    private static ByteBuffer extractBuffer(Path pathFile) throws IOException{
        try (FileChannel channel = FileChannel.open(pathFile)) {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
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
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * @return the total number of nodes in the graph
     */
    public int nodeCount(){
        return nodes.count();
    }

    /**
     * @param nodeId the id of the node
     * @return the position of the given identity node
     */
    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * @param nodeId the id of the node
     * @return the number of edges leaving the given identity node
     */
    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    /**
     * @param nodeId the id of the node
     * @param edgeIndex index of the edge
     * @return the identity of the edgeIndex-th edge outgoing from the identity node nodeId
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * @param point coordinate point
     * @param searchDistance distance (radius) of the search
     * @return the identity of the node closest to the given point, at the given maximum distance (in meters),
     * or -1 if no node matches these criteria
     */
    public int nodeClosestTo(PointCh point, double searchDistance){
        double closestDistance = Math.pow(searchDistance, 2);
        int closestNodeIdentity = -1;
        double distanceToSquared;

        for(GraphSectors.Sector sector : sectors.sectorsInArea(point, searchDistance)){
            for(int nodeId = sector.startNodeId() ; nodeId < sector.endNodeId(); nodeId++){
                distanceToSquared = nodePoint(nodeId).squaredDistanceTo(point);
                if(distanceToSquared <= closestDistance) {
                    closestDistance = distanceToSquared;
                    closestNodeIdentity = nodeId;
                }
            }
        }
        return closestNodeIdentity;
    }

    /**
     * @param edgeId the id of the edge
     * @return the identity of the destination node of the given identity edge
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     * @param edgeId the id of the edge
     * @return true iff the given identity edge goes in the opposite direction of the OSM channel it comes from
     */
    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    /**
     * @param edgeId the id of the edge
     * @return the set of OSM attributes attached to the given identity edge
     */
    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * @param edgeId the id of the edge
     * @return the length, in meters, of the given identity edge
     */
    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    /**
     * @param edgeId the id of the edge
     * @return the total elevation gain of the given identity edge
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     * @param edgeId the id of the edge
     * @return the longitudinal profile of the given identity edge, as a function,
     * and Double.NaN if the edge has no profile
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if (edges.hasProfile(edgeId)) {
            return Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId));
        }
        return Functions.constant(Double.NaN);
    }

}