package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void nodeClosestTo() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));

        double east = Ch1903.e(Math.toRadians(6.5693771),Math.toRadians(46.5175179));
        double north = Ch1903.n(Math.toRadians(6.5693771),Math.toRadians(46.5175179));
        //System.out.println(east + "," + north);
        PointCh rolex = new PointCh(east, north);

        int nodeId = graph.nodeClosestTo(rolex, 20);
        PointCh closestNode = graph.nodePoint(nodeId);
        assertEquals(east, closestNode.e(), 0.1);
        assertEquals(north, closestNode.n(), 0.1);


        rolex = new PointCh(2533132, 1152206);
        nodeId = graph.nodeClosestTo(rolex, 20);
        closestNode = graph.nodePoint(nodeId);
        assertEquals(2533122, closestNode.e());
        assertEquals(1152205.1875, closestNode.n());

        rolex = new PointCh(2533132, 1152206);
        nodeId = graph.nodeClosestTo(rolex, 0);
        assertEquals(-1, nodeId);

        east = Ch1903.e(Math.toRadians(6.5745538),Math.toRadians(46.5187522));
        north = Ch1903.n(Math.toRadians(6.5745538),Math.toRadians(46.5187522));
        PointCh point = new PointCh(east, north);
        nodeId = graph.nodeClosestTo(point, 100);
        closestNode = graph.nodePoint(nodeId);
        assertEquals(2533692.3125, closestNode.e());
        assertEquals(1152305.0, closestNode.n());

    }
}