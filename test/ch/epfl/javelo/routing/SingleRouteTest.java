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
        System.out.println(E);
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
        assertEquals(edge2.pointAt(10), route.pointAt(edge1.length()+10));
    }

    @Test
    void elevationAt() {
    }

    @Test
    void nodeClosestTo() {
    }

    @Test
    void pointClosestTo() {
    }
}