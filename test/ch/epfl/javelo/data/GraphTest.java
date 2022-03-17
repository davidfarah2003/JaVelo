package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void nodeClosestTo() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        PointCh test = new PointCh(2_633_208, 1_087_349);
        int i = graph.nodeClosestTo(test, 200_000);
        // 2_533_132, 1_152_206
        // nodePoint
        System.out.println(i);
        System.out.println(graph.nodePoint(i).e() + "  " + graph.nodePoint(i).n());
        System.out.println(graph.nodePoint(i).lat() * 180/ Math.PI + " " + graph.nodePoint(i).lon() * 180/ Math.PI);
        System.out.println(graph.nodeOutDegree(i));
        System.out.println(graph.nodeOutEdgeId(i,0));
      //  System.out.println(graph.nodeOutEdgeId(i,1));
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
    }
}