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
     *
     * @param graph        graph
     * @param costFunction cost function
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
     * @param startNodeId : ID of the initial node
     * @param endNodeId   : ID of the final node
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


        // Dijkstra's Algorithm, while the explored list is not empty (we can still find a route)
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
     * @param endNodeId
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
                nodesDistanceToOrigin[edgeEndNodeId] = nodeDistanceToOrigin;
                predecessors[edgeEndNodeId] = nodeChosenId;
                nodesToExplore.add(new WeightedNode(edgeEndNodeId, nodeDistanceToOrigin, (float)
                        graph.nodePoint(edgeEndNodeId).distanceTo(graph.nodePoint(endNodeId))));
            }
        }

    }


    /**
     * @return the weighted node which is closest to the start node ID and
     * closest to the end node ID (ignores weighted nodes already explored)
     */
    private WeightedNode removeExplored() {
        //skip negative infinities
        WeightedNode nodeChosen;
        do {
            nodeChosen = nodesToExplore.remove();
        } while (nodesDistanceToOrigin[nodeChosen.nodeId] == Float.NEGATIVE_INFINITY);

        return nodeChosen;
    }


    /**
     * @param startNodeId
     * @param endNodeId
     * @return the list of edges which compose the path/itinerary.
     */
    private List<Edge> reconstructRoute(int startNodeId, int endNodeId) {
        // getting all the nodes which we find along
        // the path from the startNode to the end Node
        List<Integer> finalIds = new ArrayList<>();
        int i = endNodeId;
        while (i != startNodeId) {
            finalIds.add(i);
            i = predecessors[i];
        }
        finalIds.add(startNodeId);

        //reconstruction
        LinkedList<Edge> edges = new LinkedList<>();
        int s;
        int e;
        for (int k = finalIds.size() - 1; k > 0; k--) {
            s = finalIds.get(k - 1);
            e = finalIds.get(k);
            for (int l = 0; l < graph.nodeOutDegree(s); l++) {
                if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(s, l)) == e) {
                    edges.addFirst(Edge.of(graph, graph.nodeOutEdgeId(s, l), s, e));
                    break;
                }
            }

        }
        return edges;
    }

    private List<Edge> reconstructRoute2(int startNodeId, int endNodeId){
        LinkedList<Edge> edges = new LinkedList<>();
        int start = 0;
        int end = endNodeId;

        while((start != startNodeId)){
            start = predecessors[end];

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
     * inner record to represent a node, implementing the comparable interface to let the
     * PriorityQueue remove() method know which element is the smallest in the queue.
     */

    private record WeightedNode(int nodeId, float distance,
                                float distanceStraightLine) implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance + this.distanceStraightLine, that.distance + that.distanceStraightLine);
        }
    }
}


