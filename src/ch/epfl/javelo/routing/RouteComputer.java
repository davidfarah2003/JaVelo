package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import java.util.*;


public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    private final PriorityQueue<WeightedNode> nodesToExplore;
    private final float[] nodesDistanceToOrigin;
    private final int[] predecessors;
    private int nodeChosenId;


    /**
     * Constructor of the class
     * @param graph        Graph used for the route
     * @param costFunction cost function of the route
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        nodesDistanceToOrigin = new float[graph.nodeCount()];
        predecessors = new int[graph.nodeCount()];
        nodesToExplore = new PriorityQueue<>();
        this.costFunction = costFunction;
    }


    /**
     * This method computes the shortest route between the nodes given as parameters
     *
     * @param startNodeId  ID of the initial node
     * @param endNodeId    ID of the final node
     * @return a Route,
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

        Arrays.fill(nodesDistanceToOrigin, Float.POSITIVE_INFINITY);
        nodesDistanceToOrigin[startNodeId] = 0;

        nodesToExplore.add(
                new WeightedNode(
                        startNodeId,
                        nodesDistanceToOrigin[startNodeId],
                        (float) graph.nodePoint(startNodeId).distanceTo(graph.nodePoint(endNodeId))
                )
        );


        while (!nodesToExplore.isEmpty()) {
            nodeChosenId = removeExplored().nodeId;

            if (nodeChosenId == endNodeId) {
                List<Edge> edges = reconstructRoute(startNodeId, endNodeId);
                return new SingleRoute(edges);
            }

            addNodesToExplore(endNodeId);
            nodesDistanceToOrigin[nodeChosenId] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }


    /**
     * This method adds weighted nodes that are connected
     * to the nodeChosen to the ToExplore list (if the distance
     * computed is smaller than the actual one stored in the array).
     *
     * @param endNodeId id of the node at the end of the route
     */
    private void addNodesToExplore(int endNodeId){
        int currentEdgeId;
        int edgeEndNodeId;
        float nodeDistanceToOrigin;

        // explorer toutes les edges qui sortent du node
        for (int i = 0; i < graph.nodeOutDegree(nodeChosenId); i++) {

            currentEdgeId = graph.nodeOutEdgeId(nodeChosenId, i);
            edgeEndNodeId = graph.edgeTargetNodeId(currentEdgeId);

            nodeDistanceToOrigin = nodesDistanceToOrigin[nodeChosenId] + (float) (graph.edgeLength(currentEdgeId)
                    * costFunction.costFactor(nodeChosenId, currentEdgeId));


            if (nodeDistanceToOrigin < nodesDistanceToOrigin[edgeEndNodeId]) {
                predecessors[edgeEndNodeId] = nodeChosenId;
                nodesDistanceToOrigin[edgeEndNodeId] = nodeDistanceToOrigin;

                nodesToExplore.add(
                        new WeightedNode(
                                edgeEndNodeId,
                                nodeDistanceToOrigin,
                                (float) graph.nodePoint(edgeEndNodeId).distanceTo(graph.nodePoint(endNodeId))
                        )
                );
            }
        }
    }


    /**
     * @return the weighted node which is closest to the start node ID and
     * closest to the end node ID (ignores weighted nodes already explored)
     */
    private WeightedNode removeExplored(){
        WeightedNode nodeChosen;
        do {
            nodeChosen = nodesToExplore.remove();
        } while (nodesDistanceToOrigin[nodeChosen.nodeId] == Float.NEGATIVE_INFINITY);

        return nodeChosen;
    }


    /**
     * @param startNodeId   id of the node at the start of the route
     * @param endNodeId     id of the node at the end of the route
     * @return the list of edges which compose the path/itinerary.
     */
    private List<Edge> reconstructRoute(int startNodeId, int endNodeId){
        LinkedList<Edge> edges = new LinkedList<>();
        int start;
        int end = endNodeId;

        while((end != startNodeId)){
            start = predecessors[end];

            //iterate over edges going out of the node to find the corresponding edge
            for (int l = 0; l < graph.nodeOutDegree(start); l++) {
                if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(start, l)) == end) {
                    edges.addFirst(Edge.of(graph, graph.nodeOutEdgeId(start, l), start, end));
                    break;
                }
            }

            end = start;
        }

        return edges;
    }


    /**
     * Inner record to represent a node, implementing the comparable interface to let the PriorityQueue know which
     * element is the smallest in the queue.
     */
    private record WeightedNode(int nodeId, float distance, float distanceStraightLine)
            implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance + this.distanceStraightLine, that.distance + that.distanceStraightLine);
        }
    }
}


