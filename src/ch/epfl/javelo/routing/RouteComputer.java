package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;

import java.util.ArrayList;
import java.util.Arrays;

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

    public Route bestRouteBetween(int startNodeId, int endNodeId){
        double[] array = new double[graph.nodeCount()];
        Arrays.fill(array, Double.POSITIVE_INFINITY);
        array[startNodeId] = 0;

        ArrayList<Double> list =

    }
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }



    }

}
