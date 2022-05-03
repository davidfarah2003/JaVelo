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

        this.mapViewParametersP.addListener( e -> repositionRoutePoints() );
        routeBean.getWaypoints().addListener((InvalidationListener) e -> modifyRoute());

        this.signalError = signalError;
        pane = new Pane();
        pane.setPickOnBounds(false);

        polyline = new Polyline();
        polyline.setId("route");

        circle = new Circle(5);
        circle.setId("highlight");
        circle.setVisible(false);

        //routeBean.route.addListener(e ->{ polyline.setVisible(routeBean.route.get() != null);});

        pane.getChildren().addAll(polyline, circle);
    }

    private void modifyRoute() {
        System.out.println("clearing " + polyline.getPoints().size());
        polyline.getPoints().clear();


        if (routeBean.route.get() != null) {
            for (PointCh point : routeBean.route.get().points()) {
                PointWebMercator p = PointWebMercator.ofPointCh(point);
                double x = mapViewParametersP.get().viewX(p);
                double y = mapViewParametersP.get().viewY(p);
                polyline.getPoints().addAll(x, y);
            }

            PointCh pt = routeBean.route.get().pointAt(routeBean.highlightedPosition());
            PointWebMercator pw = PointWebMercator.ofPointCh(pt);
            circle.setCenterX(mapViewParametersP.get().viewX(pw));
            circle.setCenterY(mapViewParametersP.get().viewY(pw));
            circle.setVisible(true);

            double[] points = polyline.getPoints().stream().mapToDouble(Number::doubleValue).toArray();
            polyline.getLocalToParentTransform().transform2DPoints(points, 0, points, 0, points.length/2);
        }
    }

    private void repositionRoutePoints() {

        /*
        List<Double> coords = new ArrayList<>();
        int i = 0;
        if (routeBean.route.get() != null) {
            for (PointCh pointCh : routeBean.route.get().points()) {
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

            PointCh pt = routeBean.route.get().pointAt(routeBean.highlightedPosition());
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
