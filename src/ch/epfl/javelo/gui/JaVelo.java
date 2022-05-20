package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {

    private final ErrorManager errorManager = new ErrorManager();
    private RouteBean rb;
    private SplitPane splitPane;
    private ElevationProfileManager elevationProfileManager;
    private AnnotatedMapManager annotatedMapManager;


    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) throws Exception {

        splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        TileManager tileManager = new TileManager(Path.of("./osm-cache"), "tile.openstreetmap.org");
        CostFunction cityBikeCF = new CityBikeCF(graph);


        RouteComputer rc = new RouteComputer(graph, cityBikeCF);
        rb = new RouteBean(rc);
        Consumer<String> errorConsumer = new ErrorConsumer();
        annotatedMapManager = new AnnotatedMapManager(graph, tileManager, rb, errorConsumer);

        ReadOnlyObjectProperty<ElevationProfile> elevationProfileP = rb.getElevationProfileProperty();

        elevationProfileManager = new ElevationProfileManager(rb.getElevationProfileProperty(),
               rb.getHighlightedPositionP());

        elevationProfileP.addListener((p, oldV, newV) -> {
            if (oldV == null && newV != null) {
                Pane borderPane = elevationProfileManager.pane();
                rb.getHighlightedPositionP().bind(Bindings.when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0)).then(
                        annotatedMapManager.mousePositionOnRouteProperty()).otherwise(elevationProfileManager.mousePositionOnProfileProperty()));
                SplitPane.setResizableWithParent(borderPane, false);
                splitPane.getItems().add(elevationProfileManager.pane());
            }
            else if (oldV != null && newV == null) {
                splitPane.getItems().remove(splitPane.getItems().size() - 1);
            }
        });


        MenuItem menuItem = new MenuItem("Exporter GPX");

        menuItem.disableProperty().bind(Bindings.createBooleanBinding(() -> rb.getRouteProperty().get()  == null,
                rb.getRouteProperty()));

        menuItem.setOnAction(e ->{
            Document d = GpxGenerator.createGPX(elevationProfileP.get(), rb.getRouteProperty().get());
            try {
                GpxGenerator.writeGPX(d);
            } catch (IOException | TransformerException exception) {
                exception.printStackTrace();
            }
        });


        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        Menu menu = new Menu("Fichier", new Pane(), menuItem);
        menuBar.getMenus().add(menu);


        splitPane.getItems().add(annotatedMapManager.pane());

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(splitPane, errorManager.pane(), menuBar);


        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(stackPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }

        private final class ErrorConsumer
                implements Consumer<String> {
            @Override
            public void accept(String s) {
                errorManager.displayError(s);
            }
        }

}