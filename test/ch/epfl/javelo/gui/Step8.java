package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public class Step8 extends Application{
        public static void main(String[] args) { launch(args); }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Graph graph = Graph.loadFrom(Path.of("lausanne"));
            Path cacheBasePath = Path.of("cache");
            String tileServerHost = "tile.openstreetmap.org";
            TileManager tileManager =
                    new TileManager(cacheBasePath, tileServerHost);

            MapViewParameters mapViewParameters =
                    new MapViewParameters(12, 543200, 370650);
            ObjectProperty<MapViewParameters> mapViewParametersP =
                    new SimpleObjectProperty<>(mapViewParameters);
            ObservableList<Waypoint> waypoints =
                    FXCollections.observableArrayList(
                            new Waypoint(new PointCh(2532697, 1152350), 159049),
                            new Waypoint(new PointCh(2538659, 1154350), 117669));
            Consumer<String> errorConsumer = new ErrorConsumer();

           // WaypointsManager waypointsManager =
            //        new WaypointsManager(graph,
            //                mapViewParametersP,
             //               waypoints,
             //               errorConsumer);
            BaseMapManager baseMapManager =
                  new BaseMapManager(tileManager,new WayPointsManager(null, null, null, null),
                          mapViewParametersP);

           // StackPane mainPane =
            //        new StackPane(baseMapManager.pane());
            Pane pane = baseMapManager.pane();
            pane.getStylesheets().add("map.css");
            primaryStage.setMinWidth(1500);
            primaryStage.setMinHeight(800);
            primaryStage.setScene(new Scene(pane));
           primaryStage.show();
        }

        private static final class ErrorConsumer
                implements Consumer<String> {
            @Override
            public void accept(String s) { System.out.println(s); }
        }
    }

