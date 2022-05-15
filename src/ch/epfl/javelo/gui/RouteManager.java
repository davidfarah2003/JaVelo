package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {
    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;

    private final Pane pane;
    private final Polyline polyline;
    private final Circle circle;

    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> mapViewParametersP) {
        this.routeBean = routeBean;
        this.mapViewParametersP = mapViewParametersP;

        polyline = new Polyline();
        polyline.setId("route");

        circle = new Circle(5);
        circle.setId("highlight");
        circle.setVisible(false);

        pane = new Pane();
        pane.setPickOnBounds(false);
        pane.getChildren().addAll(polyline, circle);

        addListeners();
    }

    private void addListeners(){
        routeBean.getWaypoints().addListener((InvalidationListener) e -> reconstructPolyline());

        this.mapViewParametersP.addListener(
                (p, oldParams, newParams) -> {
                    if (oldParams.zoomLevel() == newParams.zoomLevel()) {
                        repositionNodes();
                    }
                    else{
                        reconstructPolyline();
                    }
                });

        circle.setOnMouseClicked(e -> {
            PointWebMercator pointRelatedToPaneCh = mapViewParametersP.get().pointAt(
                    circle.localToParent(e.getX(), e.getY()).getX() ,
                    circle.localToParent(e.getX(), e.getY()).getY()
            );
            PointCh pointRelatedToPane = pointRelatedToPaneCh.toPointCh();

            int id = routeBean.getRouteProperty().get().nodeClosestTo(routeBean.highlightedPosition());
            Waypoint w = new Waypoint(pointRelatedToPane, id);

            routeBean.getWaypoints().add(routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + 1, w);
        });

        routeBean.getRouteProperty().addListener(e -> {
            polyline.setVisible(routeBean.getRouteProperty().get() != null);
            circle.setVisible(routeBean.getRouteProperty().get() != null);
        });
    }


    private void reconstructPolyline() {
        List<Double> polylineCoordinates = new ArrayList<>();
        polyline.getPoints().clear();

        if (routeBean.getRouteProperty().get() != null) {

            //add x and y coordinates in alternating order
            int i = 0;
            for (PointCh routePoint : routeBean.getRouteProperty().get().points()) {
                PointWebMercator routePointMercator = PointWebMercator.ofPointCh(routePoint);
                if (i == 0) {
                    polylineCoordinates.add(routePointMercator.xAtZoomLevel(mapViewParametersP.get().zoomLevel()));
                }
                 else {
                    polylineCoordinates.add(routePointMercator.yAtZoomLevel(mapViewParametersP.get().zoomLevel()));
                }
                i += 1;
                i %= 2;
            }

            polyline.getPoints().addAll(polylineCoordinates);
            polyline.setLayoutX(-mapViewParametersP.get().xUpperLeftMapView());
            polyline.setLayoutY(-mapViewParametersP.get().yUpperLeftMapView());

            PointWebMercator highlightedPoint =
                    PointWebMercator.ofPointCh(routeBean.getRouteProperty().get().pointAt(routeBean.highlightedPosition()));
            circle.setCenterX(highlightedPoint.xAtZoomLevel(mapViewParametersP.get().zoomLevel()));
            circle.setCenterY(highlightedPoint.yAtZoomLevel(mapViewParametersP.get().zoomLevel()));
            circle.setLayoutX(-mapViewParametersP.get().xUpperLeftMapView());
            circle.setLayoutY(-mapViewParametersP.get().yUpperLeftMapView());
        }
    }

    private void repositionNodes() {
        if (routeBean.getRouteProperty().get() != null) {
            polyline.setLayoutX(-mapViewParametersP.get().xUpperLeftMapView());
            polyline.setLayoutY(-mapViewParametersP.get().yUpperLeftMapView());

            circle.setLayoutX(-mapViewParametersP.get().xUpperLeftMapView());
            circle.setLayoutY(-mapViewParametersP.get().yUpperLeftMapView());
        }
    }

    public Pane pane() {
        return pane;
    }
}
