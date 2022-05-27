package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * JaVelo class
 *
 * Main Class of the project, starts the whole program.
 * @author Wesley Nana Davies(344592)
 * @author David Farah (341017)
 */
public final class JaVelo extends Application {

    private final ErrorManager errorManager = new ErrorManager();
    private RouteBean rb;
    private SplitPane splitPane;
    private ElevationProfileManager elevationProfileManager;


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainBorderPane = new BorderPane();

        splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        TileManager tileManager = new TileManager(Path.of("./osm-cache"), "tile.openstreetmap.org");
        CostFunction cityBikeCF = new CityBikeCF(graph);


        RouteComputer rc = new RouteComputer(graph, cityBikeCF);
        rb = new RouteBean(rc);
        Consumer<String> errorConsumer = new ErrorConsumer();
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, rb, errorConsumer);

        ReadOnlyObjectProperty<ElevationProfile> elevationProfileP = rb.getElevationProfileProperty();

        elevationProfileManager = new ElevationProfileManager(rb.getElevationProfileProperty(),
               rb.getHighlightedPositionP());

        elevationProfileP.addListener((p, oldV, newV) -> {
            if (oldV == null && newV != null) {
                Pane borderPane = elevationProfileManager.pane();
                borderPane.setMinHeight(splitPane.getHeight()/6);
                borderPane.setMaxHeight(splitPane.getHeight()/2);
                SplitPane.setResizableWithParent(borderPane, false);
                splitPane.getItems().add(elevationProfileManager.pane());
            }
            else if (oldV != null && newV == null) {
                splitPane.getItems().remove(splitPane.getItems().size() - 1);
            }
        });

        rb.getHighlightedPositionP().bind(Bindings.
                when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0)).
                then(annotatedMapManager.mousePositionOnRouteProperty()).
                otherwise(elevationProfileManager.mousePositionOnProfileProperty()));

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
        stackPane.getChildren().addAll(splitPane, errorManager.pane());

        mainBorderPane.setTop(menuBar);
        mainBorderPane.setCenter(stackPane);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainBorderPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }


    /**
     * Inner class which manages the display of errors on the screen
     */

    private final class ErrorConsumer
                implements Consumer<String> {

            @Override
            public void accept(String s) {
                errorManager.displayError(s);
            }
        }

}
