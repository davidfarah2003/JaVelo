package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Manages display and interaction with waypoints
 *
 * @author Wesley Nana Davies(344592)
 * @author David Farah (341017)
 */
public final class WayPointsManager {
    private final Graph graph;
    private final ObservableList<Waypoint> wayPoints;
    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private final Consumer<String> signalError;
    private final gui gui;
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
                            ObservableList<Waypoint> wayPoints, Consumer<String> signalError) {
        this.graph = graph;
        this.mapViewParameters = mapViewParameters;
        this.wayPoints = wayPoints;

        this.signalError = signalError;
        this.gui = new gui();
    }

    /**
     * @param x coordinate of a point to add (WebMercator)
     * @param y coordinate of a point to add (WebMercator)
     * @return true if a WayPoint has been added, false otherwise
     */
    public boolean addWaypoint(double x, double y) {
        int closestNodeId = getNodeId(x, y);

        if (closestNodeId < 0){
            signalError.accept(PROXIMITY_ERROR_MSG);
            return false;
        }

        wayPoints.add(new Waypoint(graph.nodePoint(closestNodeId), closestNodeId));
        return true;
    }

    /**
     * @param x coordinate of a point to add (WebMercator)
     * @param y coordinate of a point to add (WebMercator)
     * @return true if a WayPoint has been replaced, false otherwise
     */
    private boolean replaceWaypoint(double x, double y, Waypoint oldWaypoint) {
        int closestNodeId = getNodeId(x, y);

        if (closestNodeId < 0){
            signalError.accept(PROXIMITY_ERROR_MSG);
            return false;
        }
        else if(!wayPoints.contains(oldWaypoint)){
            return false;
        }

        wayPoints.set(wayPoints.indexOf(oldWaypoint), new Waypoint(graph.nodePoint(closestNodeId), closestNodeId));
        return true;
    }


    private int getNodeId(double x, double y){
        PointWebMercator point = PointWebMercator.of(mapViewParameters.get().zoomLevel(), x, y);
        return graph.nodeClosestTo(point.toPointCh(), SEARCH_DISTANCE);
    }


    private void removeWaypoint(Waypoint waypoint){
        wayPoints.remove(waypoint);
    }


    /**
     * @return the JavaFX panel displaying the basemap.
     */
    public Pane pane() {
        return gui.pane;
    }

    private final class gui {
        private final String svgOutside = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
        private final String svgInside = "M0-23A1 1 0 000-29 1 1 0 000-23";
        private final Pane pane;
        private final Map<Group, Waypoint> pinWaypointMap;
        private final mouseCoordinates coordsBeforeDrag;

        public gui() {
            this.pane = new Pane();
            pane.setPickOnBounds(false);

            pinWaypointMap = new HashMap<>();
            coordsBeforeDrag = new mouseCoordinates(0,0);

            wayPoints.addListener((InvalidationListener) observable -> redrawWaypoints());
            mapViewParameters.addListener(o -> replaceWayPoints());

            redrawWaypoints();
        }

        private void replaceWayPoints() {
            po
            for (Node g : pane.getChildren()) {
                g.setLayoutX(mapViewParameters.get().
                        viewX(PointWebMercator.ofPointCh(pinWaypointMap.get((Group) g).point())));
                g.setLayoutY(mapViewParameters.get().
                        viewY(PointWebMercator.ofPointCh(pinWaypointMap.get((Group) g).point())));
            }
        }


        public void redrawWaypoints(){
            pane.getChildren().clear();
            pinWaypointMap.clear();

            for (Waypoint wayPoint : wayPoints) {
                Group pin = createPinGroup();
                PointWebMercator anchorPoint = PointWebMercator.ofPointCh(wayPoint.point());
                pin.setLayoutX(mapViewParameters.get().viewX(anchorPoint));
                pin.setLayoutY(mapViewParameters.get().viewY(anchorPoint));
                addPinListeners(pin);
                pane.getChildren().add(pin);
                pinWaypointMap.put(pin, wayPoint);
            }

            if(pane.getChildren().size() > 0)
            {
                pane.getChildren().get(0).getStyleClass().clear();
                pane.getChildren().get(0).getStyleClass().add("pin");
                pane.getChildren().get(0).getStyleClass().add("first");

            }

            if(pane.getChildren().size() > 1)
            {
                pane.getChildren().get(wayPoints.size() - 1).getStyleClass().clear();
                pane.getChildren().get(wayPoints.size() - 1).getStyleClass().add("pin");
                pane.getChildren().get(wayPoints.size() - 1).getStyleClass().add("last");
            }

        }


        private Group createPinGroup(){
            SVGPath outside = new SVGPath();
            outside.setContent(svgOutside);
            outside.getStyleClass().add("pin_outside");

            SVGPath inside = new SVGPath();
            inside.setContent(svgInside);
            inside.getStyleClass().add("pin_inside");

            Group pin = new Group(outside, inside);
            pin.getStyleClass().add("pin");
            pin.getStyleClass().add("middle");
            return pin;
        }

        private void addPinListeners(Group pin){
            pin.setOnMouseClicked(
                    mouseEvent -> {
                        if(mouseEvent.isStillSincePress()){
                            removeWaypoint(pinWaypointMap.get(pin));
                        }
                    }
            );

            pin.setOnMousePressed(
                mouseEvent -> {
                    coordsBeforeDrag.setX(mouseEvent.getX());
                    coordsBeforeDrag.setY(mouseEvent.getY());
                }
            );

            pin.setOnMouseDragged(mouseEvent -> {
                pin.setLayoutX(pin.getLayoutX() + (mouseEvent.getX() - coordsBeforeDrag.getX()));
                pin.setLayoutY(pin.getLayoutY() + (mouseEvent.getY() - coordsBeforeDrag.getY()));

                pin.setOnMouseReleased(mouseEvent1 -> {
                            if(!replaceWaypoint(
                                    mapViewParameters.get().xUpperLeftMapView() + pin.getLayoutX(),
                                    mapViewParameters.get().yUpperLeftMapView() + pin.getLayoutY(),
                                    pinWaypointMap.get(pin))
                            )
                            {
                                redrawWaypoints();
                            }
                        }
                );
            });
        }
    }

}
