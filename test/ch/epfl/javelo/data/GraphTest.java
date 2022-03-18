package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    /*
PointCh test = new PointCh(2_533_132, 1_152_206);
int i = graph.nodeClosestTo(test, 2000);
// nodePoint

System.out.println(graph.nodeOutDegree(i));
System.out.println(graph.nodeOutEdgeId(i,0));
System.out.println(graph.nodeOutEdgeId(i,1));
// System.out.println(graph.edgeTargetNodeId(i));
System.out.println(graph.nodeCount());
// System.out.println(graph.edgeIsInverted(i));
// System.out.println(graph.edgeAttributes(i));
System.out.println(graph.edgeAttributes(200_987));
System.out.println(graph.edgeElevationGain(200_989));


PointCh newPoint = graph.nodePoint(i);

//  System.out.println(newPoint.e());
//  System.out.println(newPoint.n());
//  System.out.println(i);
*/
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
        System.out.println(closestNode.e() + ", " + closestNode.n());
        //assertEquals(2533293.5625, closestNode.e());
        //assertEquals(1152206, closestNode.n());


    }
}