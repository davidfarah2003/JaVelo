package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void of() throws IOException {
        Graph test = Graph.loadFrom(Path.of("lausanne/"));
       //

        //Edge test2 = Edge.of(test, 4, 1,2);

    }

    @Test
    void positionClosestTo() {
        PointCh num1 = new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100);
        PointCh num2 = new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200);
        Edge test = new Edge(1,2, num1, num2, 5.0, Functions.sampled( new float[]{34.5f, 30f, 25.7f}, 5.0));
        assertEquals(Math.sqrt(20000)/2, test.positionClosestTo(new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 200)) );
        assertEquals(-Math.sqrt(2*25*25), test.positionClosestTo(new PointCh(SwissBounds.MIN_E + 50, SwissBounds.MIN_N + 100)));

    }

    @Test
    void pointAt() {
        PointCh num1 = new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100);
        PointCh num2 = new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200);
        Edge test = new Edge(1,2, num1, num2, Math.sqrt(20000), Functions.sampled( new float[]{34.5f, 30f, 25.7f}, 5.0));
        PointCh test2 = test.pointAt(-Math.sqrt(2*25*25));
        assertEquals(test2.e(), SwissBounds.MIN_E + 75);
        assertEquals(test2.n(), SwissBounds.MIN_N + 75);

    }

    @Test
    void elevationAt() {
    }


}