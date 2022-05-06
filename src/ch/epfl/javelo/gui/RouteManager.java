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
    private final Consumer<String> signalError;
    private final String message = "Un point de passage est déjà présent à cet endroit !";

    private final Pane pane;
    private final Polyline polyline;
    private final Circle circle;

    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> mapViewParametersP, Consumer<String> signalError) {
        this.routeBean = routeBean;
        this.mapViewParametersP = mapViewParametersP;
        this.signalError = signalError;

        pane = new Pane();
        pane.setPickOnBounds(false);

        polyline = new Polyline();
        polyline.setId("route");

        circle = new Circle(5);
        circle.setId("highlight");

        pane.getChildren().addAll(polyline, circle);

        routeBean.getWaypoints().addListener((InvalidationListener) e -> reconstructPolyline());

        this.mapViewParametersP.addListener(
                (p, oldParams, newParams) -> {
            if (oldParams.zoomLevel() == newParams.zoomLevel()) {
                repositionNodes(newParams.xUpperLeftMapView() - oldParams.xUpperLeftMapView(),
                        newParams.yUpperLeftMapView() - oldParams.yUpperLeftMapView());
            }
            else{
                reconstructPolyline();
            }
        });

        circle.setOnMouseClicked(e -> {
            PointWebMercator pt = mapViewParametersP.get().pointAt(circle.localToParent(e.getX(), e.getY()).getX(),
                    circle.localToParent(e.getX(), e.getY()).getY());
            PointCh ptch = pt.toPointCh();
            int id = routeBean.getRouteProperty().get().nodeClosestTo(routeBean.highlightedPosition());
            Waypoint w = new Waypoint(ptch, id);
            boolean value = routeBean.getWaypoints().stream().map(Waypoint::nodeID).anyMatch(i -> i == id);
            if (value)
                signalError.accept(message);
            else
                routeBean.getWaypoints().add(routeBean.getRouteProperty().get().
                                indexOfSegmentAt(routeBean.highlightedPosition()) + 1,
                        w);

        });


       routeBean.getRouteProperty().addListener(e -> {
           polyline.setVisible(routeBean.getRouteProperty().get() != null);
           circle.setVisible(routeBean.getRouteProperty().get() != null);
       });

    }


    private void reconstructPolyline() {
        polyline.getPoints().clear();
        List<Double> newCoordinates = new ArrayList<>();


        int i = 0;
        if (routeBean.getRouteProperty().get() != null) {
            for (PointCh pointCh : routeBean.getRouteProperty().get().points()) {
                PointWebMercator pointWM = PointWebMercator.ofPointCh(pointCh);
                if (i == 0) {
                    double x = pointWM.xAtZoomLevel(mapViewParametersP.get().zoomLevel());
                    newCoordinates.add(x);
                }
                 else {
                    double y = pointWM.yAtZoomLevel(mapViewParametersP.get().zoomLevel());
                    newCoordinates.add(y);
                }
                i += 1;
                i %= 2;
            }

            polyline.getPoints().addAll(newCoordinates);
            polyline.setLayoutX(-mapViewParametersP.get().xUpperLeftMapView());
            polyline.setLayoutY(-mapViewParametersP.get().yUpperLeftMapView());

            // if (routeBean.getWaypoints().size() > 1) {
            PointCh pt = routeBean.getRouteProperty().get().pointAt(routeBean.highlightedPosition());
            PointWebMercator pw = PointWebMercator.ofPointCh(pt);
            circle.setCenterX(pw.xAtZoomLevel(mapViewParametersP.get().zoomLevel()));
            circle.setCenterY(pw.yAtZoomLevel(mapViewParametersP.get().zoomLevel()));
            circle.setLayoutX(-mapViewParametersP.get().xUpperLeftMapView());
            circle.setLayoutY(-mapViewParametersP.get().yUpperLeftMapView());
            //    circle.setVisible(true);
            // }

        }
    }


           // beforeMove.setX(mapViewParametersP.get().xUpperLeftMapView());
          //  beforeMove.setY(mapViewParametersP.get().yUpperLeftMapView());

          //  double[] points = polyline.getPoints().stream().mapToDouble(Number::doubleValue).toArray();
          //  polyline.getLocalToParentTransform().transform2DPoints(points, 0, points, 0, points.length/2);





    private void repositionNodes(double x, double y) {
/*
        List<Double> coords = new ArrayList<>();
        int i = 0;
        if (routeBean.getRouteProperty().get() != null) {
            for (PointCh pointCh : routeBean.getRouteProperty().get().points()) {
                PointWebMercator pointWM = PointWebMercator.ofPointCh(pointCh);
                if (i == 0) {
                    double viewX = mapViewParametersP.get().viewX(pointWM);
                    coords.add(viewX);
                } else {
                    double viewY = mapViewParametersP.get().viewY(pointWM);
                    coords.add(viewY);
                }
                i += 1;
                i %= 2;
            }

            polyline.getPoints().setAll(coords);
            PointCh pt = routeBean.getRouteProperty().get().pointAt(routeBean.highlightedPosition());
            PointWebMercator pw = PointWebMercator.ofPointCh(pt);
            circle.setCenterX(mapViewParametersP.get().viewX(pw));
            circle.setCenterY(mapViewParametersP.get().viewY(pw));
        }

 */

        if (routeBean.getRouteProperty().get() != null) {
            for(Node n : pane().getChildren()){
                n.setLayoutX(n.getLayoutX() - x);
                n.setLayoutY(n.getLayoutY() - y);
            }

        }


    }

    public Pane pane() {
        return pane;
    }
}
