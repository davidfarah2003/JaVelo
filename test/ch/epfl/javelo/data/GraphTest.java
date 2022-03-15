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
        PointCh test = new PointCh(2_533_132, 1_152_206);
        int i = graph.nodeClosestTo(test, 2000);
        PointCh newPoint = graph.nodePoint(i);
        System.out.println(newPoint.e());
        System.out.println(newPoint.n());
        System.out.println(i);
    }
}