package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Consumer;

/**
 * Manages display and interaction with waypoints
 *
 * @author Wesley Nana Davies(344592)
 * @author David Farah (341017)
 */
public final class WayPointsManager {
    Graph graph;
    ObjectProperty<MapViewParameters> mapViewParameters;
    List<PointCh> wayPoints;
    Consumer<String> signalError;

    private final double SEARCH_DISTANCE = 1000;
    private final String PROXIMITY_ERROR_MSG = "Aucune route à proximité !";


    /**
     * Constructor of WayPointsManager
     *
     * @param graph the road network graph
     * @param mapViewParameters a JavaFX property containing the parameters of the displayed map
     * @param wayPoints the (observable) list of all waypoints
     * @param signalError an object to report errors (used to display error messages to the user)
     */
    public WayPointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParameters,
                            List<PointCh> wayPoints, Consumer<String> signalError) {
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        this.wayPoints = wayPoints;
        this.signalError = signalError;

    }

    /**
     * @param x coordinate of a point to add (WebMercator)
     * @param y coordinate of a point to add (WebMercator)
     * @return true if a WayPoint has been added, false otherwise
     */
    public boolean addWaypoint(double x, double y) {
        PointWebMercator point = PointWebMercator.of(mapViewParameters.get().zoomLevel(), x, y);
        int closestNodeId = graph.nodeClosestTo(point.toPointCh(), SEARCH_DISTANCE);

        if (closestNodeId < 0){
            signalError.accept(PROXIMITY_ERROR_MSG);
            return false;
        }

        wayPoints.add(graph.nodePoint(closestNodeId));
        return true;
    }

    /**
     * @return the JavaFX panel displaying the basemap.
     */
    public Pane pane() {
        return null;
    }
}
