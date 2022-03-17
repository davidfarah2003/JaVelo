package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Path;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void nodeClosestTo() throws IOException {
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var x = rng.nextInt(1000);
            var y = rng.nextInt(1000);
            assertEquals(x, Graph.loadFrom(Path.of("lausanne/")).nodeClosestTo(Graph.loadFrom(Path.of("lausanne/")).nodePoint(x), y));
        }
    }
}


        /*
        Graph graph = Graph.loadFrom(Path.of("lausanne/"));
        PointCh test = new PointCh(2_533_132, 1_152_206);
        int i = graph.nodeClosestTo(test, 45);
        //
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
     //   System.out.println(graph.edgeAttributes(200_987));
      //  System.out.println(graph.edgeElevationGain(200_989));


        PointCh newPoint = graph.nodePoint(i);

      //  System.out.println(newPoint.e());
      //  System.out.println(newPoint.n());
      //  System.out.println(i);


         */


