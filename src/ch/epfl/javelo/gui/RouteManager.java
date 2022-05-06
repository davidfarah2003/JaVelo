package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
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

        this.mapViewParametersP.addListener((p, oldV, newV) -> {
                if (oldV.zoomLevel() == newV.zoomLevel()) {
                    repositionRoutePoints();
                }
                else{
                    modifyRoute();
                }
        });


        this.signalError = signalError;
        pane = new Pane();
        pane.setPickOnBounds(false);

        polyline = new Polyline();
        polyline.setId("route");
        polyline.setVisible(false);

        circle = new Circle(5);
        circle.setId("highlight");
        circle.setVisible(false);
        
        routeBean.getWaypoints().addListener((InvalidationListener) e -> {
            modifyRoute();
        });

        circle.setOnMouseClicked(e -> {
            PointWebMercator pt = mapViewParametersP.get().pointAt(circle.getCenterX(), circle.getCenterY());
            PointCh ptch = pt.toPointCh();
            int id = routeBean.getRouteProperty().get().nodeClosestTo(routeBean.highlightedPosition());
            Waypoint w = new Waypoint(ptch, id);
            boolean value = routeBean.getWaypoints().stream().map(Waypoint::nodeID).anyMatch(i -> i == id);
            if (value)
                signalError.accept(message);
            else
                routeBean.getWaypoints().add(routeBean.getRouteProperty().get().
                        indexOfSegmentAt(routeBean.highlightedPosition()), w);

        });


       routeBean.getRouteProperty().addListener(e -> {
           polyline.setVisible(routeBean.getRouteProperty().get() != null);
           circle.setVisible(routeBean.getRouteProperty().get() != null);

       });

        pane.getChildren().addAll(polyline, circle);
    }



    private void modifyRoute() {
        List<Double> newCoordinates = new ArrayList<>();

        // if(routeBean.getWaypoints().size() == 1){
        //
        //   }

        //routeBean.getRouteProperty().get().points().get(0);

        int i = 0;
        if (routeBean.getRouteProperty().get() != null) {
            for (PointCh pointCh : routeBean.getRouteProperty().get().points()) {
                PointWebMercator pointWM = PointWebMercator.ofPointCh(pointCh);
                if (i == 0) {
                    double viewX = mapViewParametersP.get().viewX(pointWM);
                    newCoordinates.add(viewX);
                } else {
                    double viewY = mapViewParametersP.get().viewY(pointWM);
                    newCoordinates.add(viewY);
                }
                i += 1;
                i %= 2;
            }

            polyline.getPoints().setAll(newCoordinates);

           // if (routeBean.getWaypoints().size() > 1) {
                PointCh pt = routeBean.getRouteProperty().get().pointAt(routeBean.highlightedPosition());
                PointWebMercator pw = PointWebMercator.ofPointCh(pt);
                circle.setCenterX(mapViewParametersP.get().viewX(pw));
                circle.setCenterY(mapViewParametersP.get().viewY(pw));
                //    circle.setVisible(true);
                // }

            }
        }


    private void repositionRoutePoints() {

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




    }

    public Pane pane() {
        return pane;
    }
}
