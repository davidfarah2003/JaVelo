package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import java.util.*;


public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    private final PriorityQueue<WeightedNode> nodesExplored;
    private final float[] nodesDistanceToOrigin; //distance depuis A de chaque node
    private final int[] predecessor; //id de noeud avant lui
    private int nodeChosenId;


    /**
     * Constructor of the class
     * @param graph graph
     * @param costFunction cost function
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        nodesDistanceToOrigin = new float[graph.nodeCount()];
        predecessor = new int[graph.nodeCount()];
        nodesExplored = new PriorityQueue<>();
        this.costFunction = costFunction;
    }

    /**
     * This method computes the shortest route between the nodes given as parameters
     * @param startNodeId : ID of the initial node
     * @param endNodeId : ID of the final node
     * @return a Route,
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

        Arrays.fill(nodesDistanceToOrigin, Float.POSITIVE_INFINITY);
        nodesDistanceToOrigin[startNodeId] = 0;

        nodesExplored.add(new WeightedNode(startNodeId, nodesDistanceToOrigin[startNodeId],
                (float) graph.nodePoint(startNodeId).distanceTo(graph.nodePoint(endNodeId))));


        // Dijkstra's Algorithm, while the explored list is not empty (we can still find a route)
        while (!nodesExplored.isEmpty()) {
            nodeChosenId = removeExplored().nodeId;

            if (nodeChosenId == endNodeId) {
                List<Edge> edges = reconstructRoute(startNodeId, endNodeId);
                return new SingleRoute(edges);
            }

            addNodesToExplored(endNodeId);
            nodesDistanceToOrigin[nodeChosenId] = Float.NEGATIVE_INFINITY;
        }

        return null;

    }


    private void addNodesToExplored(int endNodeId){
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
                predecessor[edgeEndNodeId] = nodeChosenId;
                nodesExplored.add(new WeightedNode(edgeEndNodeId, nodeDistanceToOrigin, (float)
                        graph.nodePoint(edgeEndNodeId).distanceTo(graph.nodePoint(endNodeId))));
            }
        }

    }


    private WeightedNode removeExplored(){
        //skip negative infinities
        WeightedNode nodeChosen;
        do {
            nodeChosen = nodesExplored.remove();
        } while (nodesDistanceToOrigin[nodeChosen.nodeId] == Float.NEGATIVE_INFINITY);

        return nodeChosen;
    }


    private List<Edge> reconstructRoute(int startNodeId, int endNodeId){
        // getting all the nodes which we find along
        // the path from the startNode to the end Node
        List<Integer> finalIds = new ArrayList<>();
        int i = endNodeId;
        while (i != startNodeId) {
            finalIds.add(i);
            i = predecessor[i];
        }


        //reconstruction
        finalIds.add(startNodeId);
        Collections.reverse(finalIds);
        List<Edge> edges = new ArrayList<>();
        int s;
        int e;
        // getting all the corresponding edges
        for (int k = 0; k < finalIds.size() - 1; k++) {
            s = finalIds.get(k);
            e = finalIds.get(k + 1);
            for (int l = 0; l < graph.nodeOutDegree(s); l++) {
                if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(s, l)) == e) {
                    edges.add(Edge.of(graph, graph.nodeOutEdgeId(s, l), s, e));
                    break;
                }
            }
        }
        return edges;
    }


    // inner record to represent a node, implementing the comparable interface to let the
    // PriorityQueue remove method know which element is lowest  in the queue.
    private record WeightedNode(int nodeId, float distance, float distanceStraightLine) implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance + this.distanceStraightLine, that.distance + that.distanceStraightLine);
        }
    }


}