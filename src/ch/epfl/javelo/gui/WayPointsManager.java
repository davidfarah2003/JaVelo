package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private ObservableList<Waypoint> wayPoints;
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
        gui.drawWaypoints();
    }


    public void drawWayPoints(){
        this.gui.drawWaypoints();
    }
    public List<Waypoint> getWayPoints(){
        return wayPoints;
    }

    public void setWayPoints(ObservableList<Waypoint> list){
        wayPoints = list;
    }
    /**
     * @param x coordinate of a point to add (WebMercator)
     * @param y coordinate of a point to add (WebMercator)
     * @return true if a WayPoint has been added, false otherwise
     */


    public boolean addWaypoint(double x, double y) {

        PointWebMercator point = mapViewParameters.get().pointAt(x,y);
        int closestNodeId = graph.nodeClosestTo(point.toPointCh(), SEARCH_DISTANCE);

        if (closestNodeId < 0){
            signalError.accept(PROXIMITY_ERROR_MSG);
            return false;
        }

        wayPoints.add(new Waypoint(graph.nodePoint(closestNodeId), closestNodeId));
        return true;
    }

    public void removeWaypoint(Waypoint waypoint){
        wayPoints.remove(waypoint);
    }

    /**
     * @param x coordinate of a point (WebMercator)
     * @param y coordinate of a point (WebMercator)
     * @return true iff the WayPoint with coordinates (x, y) is already added
     */
    private boolean containsWayPoint(double x, double y){
        PointWebMercator point = PointWebMercator.of(
                mapViewParameters.get().zoomLevel(),
                mapViewParameters.get().xUpperLeftMapView() + x,
                mapViewParameters.get().yUpperLeftMapView() + y);
        int closestNodeId = graph.nodeClosestTo(point.toPointCh(), SEARCH_DISTANCE);

        Waypoint w = new Waypoint(point.toPointCh(), closestNodeId);

        return wayPoints.contains(w);
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
        private final Map<Group, Waypoint> map;
        private final ObjectProperty<Point2D> coordinatesMouse;

        public gui() {
            this.pane = new Pane();
            pane.setPickOnBounds(false);
            wayPoints.addListener((InvalidationListener) observable -> drawWaypoints());
            map = new HashMap<>();
            coordinatesMouse = new SimpleObjectProperty<>(new Point2D(0,0));
        }

        public void drawWaypoints(){
            pane.getChildren().clear();

            int i = 0;
            for (Waypoint wayPoint : wayPoints) {
                Group pin = createPinGroup();

                if (i == 0){
                    pin.getStyleClass().add("first");
                }
                else if (i == wayPoints.size() - 1){
                    pin.getStyleClass().add("last");
                }
                else{
                    pin.getStyleClass().add("middle");
                }

                PointWebMercator anchorPoint = PointWebMercator.ofPointCh(wayPoint.point());
                pin.setLayoutX(mapViewParameters.get().viewX(anchorPoint));
                pin.setLayoutY(mapViewParameters.get().viewY(anchorPoint));
                addPinListeners(pin);
                pane.getChildren().add(pin);
                map.put(pin, wayPoint);
                i++;
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
            pin.getStyleClass().add("pin.middle");
            return pin;
        }

        private void addPinListeners(Group pin){
            pin.setOnMouseReleased(mouseEvent -> {
                System.out.println("released");
                removeWaypoint(map.get(pin));
                drawWaypoints();
            });
            //pin.setOnMousePressed(event -> {
            //    coordinatesMouse.setValue(new Point2D(event.getX(), event.getY()));
            //        });
            pin.setOnMouseDragged(event -> {
                System.out.println("dragged");
             // PointWebMercator point = PointWebMercator.ofPointCh(map.get(pin).point());
              //double x = point.xAtZoomLevel(mapViewParameters.get().zoomLevel());
              //double y = point.yAtZoomLevel(mapViewParameters.get().zoomLevel());
              //Point2D point1 = new Point2D(x,y);
              //Point2D point2 = mapViewParameters.get().topLeft().add(event.getX(), event.getY());
              //PointWebMercator point3 = PointWebMercator.of(mapViewParameters.get().zoomLevel(), point2.getX(), point2.getY());

             // point2 = point2.subtract(point1);
             // point1 = point1.


              removeWaypoint(map.get(pin));
              System.out.println(addWaypoint(event.getX(), event.getY()));
              drawWaypoints();






            });

        }
    }



}
