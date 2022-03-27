package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {

    private void print(Object E){
        //System.out.println(E);
    }

    @Test
    void length() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        Edge edge1 = Edge.of(graph, 1, 0, 1);
        Edge edge2 = Edge.of(graph, 1, 1, 3);
        Edge edge3 = Edge.of(graph, 1, 3, 4);

        List<Edge> edges = List.of(edge1, edge2, edge3);
        SingleRoute route = new SingleRoute(edges);

        assertEquals(
                edge1.length() + edge2.length() + edge3.length(),
                route.length());
    }

    @Test
    void points() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        Edge edge1 = Edge.of(graph, 1, 0, 1);
        Edge edge2 = Edge.of(graph, 1, 1, 3);
        Edge edge3 = Edge.of(graph, 1, 3, 4);
        Edge edge4 = Edge.of(graph, 1, 4, 7);
        List<Edge> edges = List.of(edge1, edge2, edge3, edge4);

        SingleRoute route = new SingleRoute(edges);
        assertEquals(5, route.points().size());
        print(route.points());
    }

    @Test
    void pointAt() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        Edge edge1 = Edge.of(graph, 1, 0, 1);
        Edge edge2 = Edge.of(graph, 1, 1, 3);
        Edge edge3 = Edge.of(graph, 1, 3, 4);
        Edge edge4 = Edge.of(graph, 1, 4, 7);
        List<Edge> edges = List.of(edge1, edge2, edge3, edge4);
        SingleRoute route = new SingleRoute(edges);


        assertEquals(edge1.toPoint(), route.pointAt(edge1.length()));
        assertEquals(edge4.toPoint(), route.pointAt(route.length()));
        assertEquals(edge1.fromPoint(), route.pointAt(0));
        assertEquals(edge4.toPoint(), route.pointAt(route.length() + 1));

        assertEquals(edge1.pointAt(5), route.pointAt(5));
        assertEquals(edge1.pointAt(3.5), route.pointAt(3.5));
        assertEquals(edge2.pointAt(10), route.pointAt(edge1.length()+10));
    }

    @Test
    void elevationAt() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        Edge edge1 = Edge.of(graph, 1, 10, 15);
        Edge edge2 = Edge.of(graph, 2, 15, 17);
        Edge edge3 = Edge.of(graph, 3, 17, 20);
        Edge edge4 = Edge.of(graph, 4, 20, 25);
        List<Edge> edges = List.of(edge1, edge2, edge3, edge4);
        SingleRoute route = new SingleRoute(edges);

        /*
        double c = 0;
        for(double i = edge1.length()-20 ; i <= edge1.length() + edge2.length(); i= i+0.5){
            //print(edge2.elevationAt(c) + " : " + route.elevationAt(i));
            assertEquals(edge2.elevationAt(c), route.elevationAt(i));
            c += 0.5;
        }
        */

        assertEquals(edge4.elevationAt(300), route.elevationAt(100000));
        assertEquals(edge1.elevationAt(0), route.elevationAt(-22));
    }

    @Test
    void nodeClosestTo() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        Edge edge1 = Edge.of(graph, 1, 10, 15);
        Edge edge2 = Edge.of(graph, 2, 15, 17);
        Edge edge3 = Edge.of(graph, 3, 17, 20);
        Edge edge4 = Edge.of(graph, 4, 20, 25);
        List<Edge> edges = List.of(edge1, edge2, edge3, edge4);
        SingleRoute route = new SingleRoute(edges);

        for(double i=0; i < edge1.length(); i+=0.5){
            if(i <= edge1.length()/2){
                assertEquals(edge1.fromNodeId(), route.nodeClosestTo(i));
            }
            else{
                assertEquals(edge1.toNodeId(), route.nodeClosestTo(i));
            }
        }
        assertEquals(edge4.toNodeId(), route.nodeClosestTo(route.length()));
        assertEquals(edge4.toNodeId(), route.nodeClosestTo(99999));

    }

    @Test
    void pointClosestTo() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        Edge edge1 = Edge.of(graph, 1, 10, 15);
        Edge edge2 = Edge.of(graph, 2, 15, 17);
        Edge edge3 = Edge.of(graph, 3, 17, 20);
        Edge edge4 = Edge.of(graph, 4, 20, 25);
        List<Edge> edges = List.of(edge1, edge2, edge3, edge4);
        SingleRoute route = new SingleRoute(edges);

        //PointCh point = new PointCh(2500000, 1100000);

        //try with route points (extremities of edges)
        for(PointCh point : route.points()){
            assertEquals(point ,route.pointClosestTo(point).point());
            print(route.pointClosestTo(new PointCh(point.e()+5, point.n()+5)));
        }

    }
}