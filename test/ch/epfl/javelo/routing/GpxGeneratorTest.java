package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GpxGeneratorTest {
    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;

    // Sides of triangle used for "sawtooth" edges (shape: /\/\/\…)
    private static final double TOOTH_EW = 1023;
    private static final double TOOTH_NS = 64;
    private static final double TOOTH_LENGTH = 1025;
    private static final double TOOTH_ELEVATION_GAIN = 100d;
    private static final double TOOTH_SLOPE = TOOTH_ELEVATION_GAIN / TOOTH_LENGTH;

    private static Edge horizontalEdge1K(int i) {
        var j = i + 1;
        var pI = new PointCh(2_600_000 + 1000 * i, 1_200_000);
        var pJ = new PointCh(2_600_000 + 1000 * j, 1_200_000);
        return new Edge(i, j, pI, pJ, 1000, x -> 500);
    }

    @Test
    void createGPX() throws IOException, TransformerException {
        //Graph g = Graph.loadFrom(Path.of("ch_west/"));
        Graph g = Graph.loadFrom(Path.of("lausanne/"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);

        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(159049, 117669);
        // Route r = rc.bestRouteBetween(2046055, 2694240);

        ElevationProfile profile = ElevationProfileComputer.elevationProfile(r, 1);


        Document doc = GpxGenerator.createGPX(profile, r);
        Writer w = new FileWriter("rendu.gpx");

        Transformer transformer = TransformerFactory
                .newDefaultInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc),
                new StreamResult(w));
    }
}