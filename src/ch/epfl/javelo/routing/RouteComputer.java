package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import java.util.*;

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessor, 0);
        distance[startNodeId] = 0;

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        PriorityQueue<WeightedNode> nodesExplored = new PriorityQueue<>();
        List<WeightedNode> nodesAlreadyExplored = new ArrayList<>();
        nodesExplored.add(new WeightedNode(startNodeId, distance[startNodeId]));


        WeightedNode nodeChosen;
        int nodeChosenId;
        int edgeId;
        int endNodeIdEdge;
        float d;

        while (!nodesExplored.isEmpty()) {
            do {
                nodeChosen = nodesExplored.remove();
            } while (nodesAlreadyExplored.contains(nodeChosen));

            nodesAlreadyExplored.add(nodeChosen);
            nodeChosenId = nodeChosen.nodeId;

            if (nodeChosenId == endNodeId) {
                break;
            }

            for (int i = 0; i < graph.nodeOutDegree(nodeChosenId); i++) {
                edgeId = graph.nodeOutEdgeId(nodeChosenId, i);
                endNodeIdEdge = graph.edgeTargetNodeId(edgeId);
                d = distance[nodeChosenId] + (float) (graph.edgeLength(edgeId) * costFunction.costFactor(nodeChosenId, edgeId));

                if (d < distance[endNodeIdEdge]) {
                    distance[endNodeIdEdge] = d;
                    predecessor[endNodeIdEdge] = nodeChosenId;
                    nodesExplored.add(new WeightedNode(endNodeIdEdge, d));
                }
            }
        }

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
        /*
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
}



         */
    }
}