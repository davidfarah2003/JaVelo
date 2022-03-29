package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import java.util.*;


public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Constructor of the class
     * @param graph
     * @param costFunction
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
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

        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessor, 0);
        distance[startNodeId] = 0;

        PriorityQueue<WeightedNode> nodesExplored = new PriorityQueue<>();
        nodesExplored.add(new WeightedNode(startNodeId, distance[startNodeId]));

        WeightedNode nodeChosen;
        int nodeChosenId;
        int edgeId;
        int endNodeIdOfTheEdge;
        float d;

        // Dijkstra's Algorithm
        while (!nodesExplored.isEmpty()) {

            do {
                nodeChosen = nodesExplored.remove();
            }
            while (distance[nodeChosen.nodeId] == Float.NEGATIVE_INFINITY);


            nodeChosenId = nodeChosen.nodeId;

            if (nodeChosenId == endNodeId) break;

            for (int i = 0; i < graph.nodeOutDegree(nodeChosenId); i++) {
                edgeId = graph.nodeOutEdgeId(nodeChosenId, i);
                endNodeIdOfTheEdge = graph.edgeTargetNodeId(edgeId);
                d = distance[nodeChosenId] + (float) (graph.edgeLength(edgeId) * costFunction.costFactor(nodeChosenId, edgeId));

                if (d < distance[endNodeIdOfTheEdge]) {
                    distance[endNodeIdOfTheEdge] = d;
                    predecessor[endNodeIdOfTheEdge] = nodeChosenId;
                    nodesExplored.add(new WeightedNode(endNodeIdOfTheEdge, d));

                }
            }
            // since we found the shortest path to the nodeChosen
            // it will never be necessary to explore its out edges again.
            distance[nodeChosenId] = Float.NEGATIVE_INFINITY;
        }

        if (nodesExplored.isEmpty()){
            return null;
        }

        // getting all the nodes which we find along
        // the path from the startNode to the end Node
        List<Integer> finalIds = new ArrayList<>();
        int i = endNodeId;
        while (i != startNodeId) {
            finalIds.add(i);
            i = predecessor[i];
        }
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
        return new SingleRoute(edges);
    }


    public Route bestRouteBetweenOld(int startNodeId, int endNodeId){
        Preconditions.checkArgument(startNodeId != endNodeId);
        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessor, 0);
        distance[startNodeId] = 0;
        List<Integer> nodeIdsExplored = new ArrayList<>();
        nodeIdsExplored.add(startNodeId);

        float minDistanceFirstIdExplored;
        int id = 0;
        int idEdge;
        int endNodeIdEdge;

        float d;

        while (!nodeIdsExplored.isEmpty()) {
            minDistanceFirstIdExplored = Float.POSITIVE_INFINITY;
            for (int idExplored : nodeIdsExplored) {
                if (distance[idExplored] < minDistanceFirstIdExplored) {
                    id = idExplored;
                    minDistanceFirstIdExplored = distance[id];
                }
            }
            nodeIdsExplored.remove(new Integer(id));

            if (id == endNodeId) {
                break;
            }

            for (int i = 0; i < graph.nodeOutDegree(id); i++) {
                idEdge = graph.nodeOutEdgeId(id, i);
                endNodeIdEdge = graph.edgeTargetNodeId(idEdge);
                d = distance[id] + (float) (graph.edgeLength(idEdge) * costFunction.costFactor(id, idEdge));

                if (d < distance[endNodeIdEdge]) {
                    distance[endNodeIdEdge] = d;
                    predecessor[endNodeIdEdge] = id;
                    nodeIdsExplored.add(endNodeIdEdge);
                }
            }
        }

        List<Integer> finalIds = new ArrayList<>();
        int i = endNodeId;
        while (i != startNodeId){
            finalIds.add(i);
            i = predecessor[i];
        }
        finalIds.add(startNodeId);
        Collections.reverse(finalIds);

        List<Edge> edges = new ArrayList<>();

        int s;
        int e;
        for (int k = 0; k < finalIds.size() - 1; k++){
            s = finalIds.get(k);
            e = finalIds.get(k+1);
            for (int l = 0; l < graph.nodeOutDegree(s); l++){
                if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(s,l)) == e){
                    edges.add(Edge.of(graph,graph.nodeOutEdgeId(s,l),s,e));
                    break;
                }
            }

        }

        return new SingleRoute(edges);
    }


    // inner record to represent a node, implementing the comparable interface to let the
    // PriorityQueue remove method know which element is lowest  in the queue.
    record WeightedNode(int nodeId, float distance)
            implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }
}