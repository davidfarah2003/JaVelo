package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


import java.util.function.Consumer;

import static java.lang.Double.NaN;

public final class AnnotatedMapManager {
    private final BaseMapManager baseMapManager;
    private final WayPointsManager wayPointsManager;
    private final RouteManager routeManager;
    private final StackPane stackPane;
    private final SimpleDoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<Point2D> currentMousePosition;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final RouteBean routeBean;



    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> consumer){
        this.routeBean = routeBean;

        mapViewParametersP = new SimpleObjectProperty<>(
                new MapViewParameters(12, 543_200, 370_650));

        currentMousePosition = new SimpleObjectProperty<>();

        this.routeManager = new RouteManager(routeBean, mapViewParametersP, consumer);
        this.wayPointsManager = new WayPointsManager(graph, mapViewParametersP, routeBean.getWaypoints(), consumer);
        this.baseMapManager = new BaseMapManager(tileManager, this.wayPointsManager, mapViewParametersP);
        this.stackPane = new StackPane(baseMapManager.pane(), wayPointsManager.pane(), routeManager.pane());
        stackPane.setId("map.css");

        mousePositionOnRouteProperty = new SimpleDoubleProperty();


        mapViewParametersP.addListener(e -> recalculateMousePositionOnRouteProperty());
        routeBean.getRouteProperty().addListener(e -> recalculateMousePositionOnRouteProperty());
        stackPane.setOnMouseMoved(e -> currentMousePosition.setValue(new Point2D(e.getX(), e.getY())));
        stackPane.setOnMouseExited(e -> mousePositionOnRouteProperty.setValue(NaN));




    }

    public Pane pane(){
        return stackPane;
    }

    public DoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;
    }

    private void recalculateMousePositionOnRouteProperty(){
        mousePositionOnRouteProperty.setValue(routeBean.getRouteProperty().get().pointClosestTo(mapViewParametersP.get().pointAt(currentMousePosition.get().getX(), currentMousePosition.get().getY()).position());
        )
    }
}
