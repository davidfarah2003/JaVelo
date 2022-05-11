package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class Stage10Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        CityBikeCF costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, costFunction);

        Route route = routeComputer.bestRouteBetween(159049, 117669);
        assert route != null;
        ElevationProfile profile = ElevationProfileComputer.elevationProfile(route, 5);

        ObjectProperty<ElevationProfile> profileProperty =
                new SimpleObjectProperty<>(profile);
        DoubleProperty highlightProperty =
                new SimpleDoubleProperty(0);

        ElevationProfileManager profileManager =
                new ElevationProfileManager(profileProperty,
                        highlightProperty);

        highlightProperty.bind(
                profileManager.mousePositionOnProfileProperty());

        Scene scene = new Scene(profileManager.pane());

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(scene);
        primaryStage.show();

        /*
        Translate translation1 = Transform.translate(3,4);
        Scale s = Transform.scale(3,4);
        Translate translation2 = Transform.translate(4,6);
        Translate translation1Inversed = translation1.createInverse();
        Scale sInversed = s.createInverse();
        Translate translation2Inversed = translation2.createInverse();

        Affine aff = new Affine();
        aff.prependTranslation(translation1.getTx(), translation1.getTy());
        aff.prependScale(s.getX(), s.getY());
        aff.prependTranslation(translation2.getTx(), translation2.getTy());

        Affine affInversed = new Affine();
        affInversed.prependTranslation(translation2Inversed.getTx(), translation2Inversed.getTy());
        affInversed.prependScale(sInversed.getX(), sInversed.getY());
        affInversed.prependTranslation(translation1Inversed.getTx(), translation1Inversed.getTy());

        System.out.println(aff.transform(40,10));

         */
    }
}
