package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    public static void main(String[] args) { launch(args); }


    @Override
    public void start(Stage primaryStage) throws Exception {

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu();
        MenuItem menuItem = new MenuItem("Export");

        menu.getItems().add(menuItem);
        menuBar.getMenus().add(menu);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);



        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
       // ObjectProperty<MapViewParameters> mapViewParametersP =
     //          new SimpleObjectProperty<>(mapViewParameters);

        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        TileManager tileManager = new TileManager(Path.of("./osm-cache"), "tile.openstreetmap.org");
        CostFunction cityBikeCF = new CityBikeCF(graph);


        RouteComputer rc = new RouteComputer(graph, cityBikeCF);
        RouteBean rb = new RouteBean(rc);
        Consumer<String> errorConsumer = new ErrorConsumer();
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, rb, errorConsumer);



        ErrorManager errorManager = new ErrorManager();



        DoubleProperty highlightProperty =
                new SimpleDoubleProperty(1500);

        rb.setHighlightedPositionProperty(1000);

        ReadOnlyObjectProperty<ElevationProfile> elevationProfile = rb.getElevationProfileProperty();

        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(elevationProfile,
                highlightProperty);


        elevationProfile.addListener((p,oldV,newV) ->{
            if (oldV == null && newV != null){
                splitPane.getItems().add(elevationProfileManager.pane());
            }
            else if(oldV != null && newV == null){
                splitPane.getItems().remove(splitPane.getItems().size() - 1);
            }
        });

        highlightProperty.bind(Bindings.when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0)).then(
                annotatedMapManager.mousePositionOnRouteProperty()).otherwise(elevationProfileManager.mousePositionOnProfileProperty()));



        splitPane.getItems().add(annotatedMapManager.pane());

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();

    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}
