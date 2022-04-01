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
     * This method computes the shortest route between the nodes given as arguments
     * @param startNodeId : ID of the initial node
     * @param endNodeId : ID of the final node
     * @return a Route
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

        // inner record to represent a node, implementing the comparable interface to let the
        // PriorityQueue remove method know which element is lowest  in the queue.
        record WeightedNode(int nodeId, float distance, float distanceStraightLine)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance + this.distanceStraightLine,
                        that.distance + that.distanceStraightLine);
            }
        }

        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0;

        PriorityQueue<WeightedNode> nodesExplored = new PriorityQueue<>();
        nodesExplored.add(new WeightedNode(startNodeId, distance[startNodeId],
                (float) graph.nodePoint(startNodeId).distanceTo(graph.nodePoint(endNodeId))));


        WeightedNode nodeChosen;
        int nodeChosenId;
        int edgeId;
        int endNodeIdOfTheEdge;
        float d;


        // Dijkstra's Algorithm
        while (!nodesExplored.isEmpty()) {

            do {
                nodeChosen = nodesExplored.remove();
            } while (distance[nodeChosen.nodeId] == Float.NEGATIVE_INFINITY);

            nodeChosenId = nodeChosen.nodeId;

            if (nodeChosenId == endNodeId) {
                // getting all the nodes which we find along
                // the path from the startNode to the end Node
                LinkedList<Integer> finalIds = new LinkedList<>();
                int i = endNodeId;
                while (i != startNodeId) {
                    finalIds.addFirst(i);
                    i = predecessor[i];
                }

                finalIds.addFirst(startNodeId);

                ArrayList<Edge> edges = new ArrayList<>();
                int s;
                int e;
                int currentEdgeId;
                // getting all the corresponding edges

                ListIterator<Integer> iterator = finalIds.listIterator();
                s = iterator.next();
                while (iterator.hasNext()){
                    e = iterator.next();
                    for (int l = 0; l < graph.nodeOutDegree(s); l++) {
                        currentEdgeId = graph.nodeOutEdgeId(s, l);
                        if (graph.edgeTargetNodeId(currentEdgeId) == e) {
                            edges.add(Edge.of(graph, currentEdgeId, s, e));
                            break;
                        }

                    }
                    s = e;
                }

                return new SingleRoute(edges);

                }
              //  for (int k = 0; k < finalIds.size() - 1; k++) {
             //       s = finalIds.get(k);
              //      e = finalIds.get(k + 1);
              //      for (int l = 0; l < graph.nodeOutDegree(s); l++) {
              //          currentEdgeId = graph.nodeOutEdgeId(s, l);
                 //       if (graph.edgeTargetNodeId(currentEdgeId) == e) {
                 //           edges.add(Edge.of(graph, currentEdgeId, s, e));
                  //          break;
                  //      }
                 //   }
            //    }
             //   return new SingleRoute(edges);

            for (int i = 0; i < graph.nodeOutDegree(nodeChosenId); i++) {
                edgeId = graph.nodeOutEdgeId(nodeChosenId, i);
                endNodeIdOfTheEdge = graph.edgeTargetNodeId(edgeId);
                d = distance[nodeChosenId]
                       + (float) (graph.edgeLength(edgeId)
                        * costFunction.costFactor(nodeChosenId, edgeId));

                if (d < distance[endNodeIdOfTheEdge]) {
                    distance[endNodeIdOfTheEdge] = d;
                    predecessor[endNodeIdOfTheEdge] = nodeChosenId;
                    nodesExplored.add(new WeightedNode(endNodeIdOfTheEdge, d, (float)
                            graph.nodePoint(endNodeIdOfTheEdge).distanceTo(graph.nodePoint(endNodeId))));

                }
            }
            // since we found the shortest path to the nodeChosen
            // it will never be necessary to explore its out edges again.
            distance[nodeChosenId] = Float.NEGATIVE_INFINITY;
        }
            return null;

    }

}