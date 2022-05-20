package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


import java.util.function.Consumer;

import static java.lang.Double.NaN;

/**
 * Manages the display of the “annotated” map, i.e. the base map above which the route and waypoints are superimposed.
 *
 * @author Wesley Nana Davies (344592)
 * @author David Farah (341017)
 */
public final class AnnotatedMapManager {
    private final BaseMapManager baseMapManager;
    private final WayPointsManager wayPointsManager;
    private final RouteManager routeManager;
    private final StackPane stackPane;
    private final SimpleDoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<Point2D> currentMousePosition;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final RouteBean routeBean;


    /**
     * Constructor
     * @param graph the road network graph, of type Graph
     * @param tileManager the OpenStreetMap tile manager, of the type TileManager
     * @param routeBean the route bean, of type RouteBean
     * @param consumer an “error consumer” allowing an error to be signaled, of type Consumer<String>
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> consumer){

        this.routeBean = routeBean;

        mapViewParametersP = new SimpleObjectProperty<>(
                new MapViewParameters(12, 543_200, 370_650));

        currentMousePosition = new SimpleObjectProperty<>();

        this.routeManager = new RouteManager(routeBean, mapViewParametersP);
        this.wayPointsManager = new WayPointsManager(graph, mapViewParametersP, routeBean.getWaypoints(), consumer);
        this.baseMapManager = new BaseMapManager(tileManager, this.wayPointsManager, mapViewParametersP);
        stackPane = new StackPane(baseMapManager.pane(), wayPointsManager.pane(), routeManager.pane());
        stackPane.getStylesheets().add("map.css");

        mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);
        routeBean.getHighlightedPositionP().bind(mousePositionOnRouteProperty);


        mapViewParametersP.addListener(e -> {
            if (!(routeBean.getRouteProperty().get() == null))
                recalculateMousePositionOnRouteProperty();
        });

        routeBean.getRouteProperty().addListener(e -> {
            if (!(routeBean.getRouteProperty().get() == null))
                recalculateMousePositionOnRouteProperty();
        });

        currentMousePosition.addListener(e -> {
            if (!(routeBean.getRouteProperty().get() == null)) {
                recalculateMousePositionOnRouteProperty();
            }
        });

        stackPane.setOnMouseMoved(e -> {
            currentMousePosition.setValue(new Point2D(e.getX(), e.getY()));
           // System.out.println(currentMousePosition);
        });
       // stackPane.setOnMouseExited(e -> {
         //   mousePositionOnRouteProperty.setValue(NaN);
        //});

    }

    /**
     * returns the pane containing the annotated map
     * @return the pane (stackPane)
     */
    public Pane pane(){
        return stackPane;
    }

    /**
     *
     * @return
     */
    public DoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;
    }

    /**
     *
     */
    private void recalculateMousePositionOnRouteProperty(){

        PointWebMercator pointUnderMouse = mapViewParametersP.get().pointAt(currentMousePosition.get().getX(),
                currentMousePosition.get().getY());

        RoutePoint rp = routeBean.getRouteProperty().get().pointClosestTo
                        (pointUnderMouse.toPointCh());

        PointWebMercator projectedPoint = PointWebMercator.ofPointCh(rp.point());


        if (Math2.norm(mapViewParametersP.get().viewX(pointUnderMouse) - mapViewParametersP.get().viewX(projectedPoint),
                mapViewParametersP.get().viewY(pointUnderMouse) - mapViewParametersP.get().viewY(projectedPoint)) <= 15){

            mousePositionOnRouteProperty.setValue(rp.position());
        }
        else{
            mousePositionOnRouteProperty.setValue(NaN);
        }

    }
}
