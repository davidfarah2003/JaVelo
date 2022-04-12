package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GpxGenerator {
    private GpxGenerator() {
    }

    public static Document createGPX(ElevationProfile profile, Route route) {
        Document doc = newDocument(); // voir plus bas

        Element root = doc.createElementNS(
                "http://www.topografix.com/GPX/1/1",
                "gpx"
        );
        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 " + "http://www.topografix.com/GPX/1/1/gpx.xsd"
        );
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        Element name = doc.createElement("name");
        Element rte = doc.createElement("rte");
        root.appendChild(rte);



        doc.appendChild(root);
        root.appendChild(metadata);
        name.setTextContent("Route JaVelo");
        metadata.appendChild(name);


        int i = 0;
        double interval = route.length() / (route.points().size() - 1);

        for (PointCh point : route.points()){
            double longitude = point.lon();
            double latitude = point.lat();
            double elevation = route.elevationAt(i * interval);




            i++;

        }

        return null;
    }

    private static Document newDocument() {
        try {
            return   DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }


}
