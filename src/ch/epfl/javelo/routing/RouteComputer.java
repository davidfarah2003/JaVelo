package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;

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
        List<Integer> nodeIdsExplored = new ArrayList<>();
        nodeIdsExplored.add(startNodeId);

        float minDistanceFirstIdExplored;
        int id = 0;
        int id2;

        float d;

        while (!nodeIdsExplored.isEmpty()) {
            minDistanceFirstIdExplored = Float.POSITIVE_INFINITY;
            for (int idExplored : nodeIdsExplored) {
                if (distance[idExplored] < minDistanceFirstIdExplored) {
                    id = idExplored;
                    minDistanceFirstIdExplored = distance[id];
                }
                nodeIdsExplored.remove(id);

                if (id == endNodeId) {
                    break;
                }

                for (int i = 0; i < graph.nodeOutDegree(id); i++) {
                    id2 = graph.nodeOutEdgeId(id, i);
                    d = distance[id] + (float) graph.edgeLength(id2);

                    if (d < distance[id2]) {
                        distance[id2] = d;
                        predecessor[id2] = id;
                        nodeIdsExplored.add(id2);
                    }
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

        SingleRoute route;




    }
}


