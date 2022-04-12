package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ListIterator;

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
        name.setTextContent("Route JaVelo");
        metadata.appendChild(name);
        root.appendChild(metadata);


        ListIterator<PointCh> pointIterator = route.points().listIterator();
        PointCh previousPoint  = pointIterator.next();
        PointCh point;
        int distance = 0;

        while (pointIterator.hasNext()){
            Element rtept = doc.createElement("rtept");
            Element ele = doc.createElement("ele");

            rtept.setAttribute("lat", Double.toString(Math.toDegrees(previousPoint.lat())));
            rtept.setAttribute("lon", Double.toString(Math.toDegrees(previousPoint.lon())));
            ele.setTextContent(Double.toString(profile.elevationAt(distance)));

            point  = pointIterator.next();
            distance += point.distanceTo(previousPoint);

            previousPoint = point;

            rte.appendChild(rtept);
            rtept.appendChild(ele);
        }

        return doc;
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

    public static void writeGPX(Document doc){
        try {
            Writer w = new FileWriter("rendu.gpx");

            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        }
        catch{

        }

    }

}
