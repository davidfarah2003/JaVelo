package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

public final class RouteManager {
    private RouteBean bean;
    private ObjectProperty<MapViewParameters> mapViewParametersP;
    private final Consumer<String> signalError;
    private final String message = "Un point de passage est déjà présent à cet endroit !";
    private final Pane pane;

    public RouteManager(RouteBean bean, ObjectProperty<MapViewParameters> mapViewParametersP, Consumer<String> signalError){
        this.bean = bean;
        this.mapViewParametersP = mapViewParametersP;
        this.mapViewParametersP.addListener(e -> redrawRoutePoints());
        bean.getWaypoints().addListener((InvalidationListener) e -> pane());
        this.signalError = signalError;
        pane = new Pane();

    }

    private void redrawRoutePoints() {
        pane.getChildren().clear();
        Polyline polyline = new Polyline();

        if(bean.route.get() != null) {
            for (PointCh point : bean.route.get().points()) {
                PointWebMercator p = PointWebMercator.ofPointCh(point);
                double x = mapViewParametersP.get().viewX(p);
                double y = mapViewParametersP.get().viewY(p);
                polyline.getPoints().addAll(x, y);
            }
        }

        pane.getChildren().add(polyline);
        pane();

    }

    public Pane pane(){
        pane.getChildren().clear();
        Polyline polyline = new Polyline();
        polyline.setStroke(Color.RED);


        if(bean.route.get() != null) {
            for (PointCh point : bean.route.get().points()) {
                PointWebMercator p = PointWebMercator.ofPointCh(point);
                double x = mapViewParametersP.get().viewX(p);
                double y = mapViewParametersP.get().viewY(p);
                polyline.getPoints().addAll(x, y);
            }
        }

        System.out.println(polyline.getPoints().size());

        //polyline.setVisible(!(bean.route.get() == null));
       // Circle circle = new Circle(5);
       // circle.setId("highlight");
       // circle.setVisible(!(bean.route.get() == null));

        pane.getChildren().add(polyline);
        pane.setPickOnBounds(false);
        pane.setVisible(true);
        return pane;


    }

}
