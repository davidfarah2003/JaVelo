package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public class Step8 extends Application{
        public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        Path cacheBasePath = Path.of("cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);

        Consumer<String> errorConsumer = new ErrorConsumer();

        CostFunction cf = new CityBikeCF(graph);
        RouteComputer rc = new RouteComputer(graph, cf);
        RouteBean rb = new RouteBean(rc);
        rb.setHighlightedPositionProperty(1000);
        RouteManager rm = new RouteManager(rb,mapViewParametersP, errorConsumer);

        ObservableList<Waypoint> waypoints = rb.getWaypoints();

        WayPointsManager waypointsManager =
                new WayPointsManager(graph,
                        mapViewParametersP,
                        waypoints,
                       errorConsumer);

        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);

        StackPane mainPane = new StackPane(baseMapManager.pane(), waypointsManager.pane(), rm.pane());


        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}