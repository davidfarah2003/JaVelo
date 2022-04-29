package ch.epfl.javelo.gui;
import ch.epfl.javelo.Math2;


import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import java.io.IOException;



/**
 * BaseMapManager class
 * @author Wesley Nana Davies(344592)
 */
public final class BaseMapManager {
    private final WayPointsManager wayPointsManager;
    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final ObjectProperty<Point2D> coordinatesMouse;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final static int SIZE_TILE = 256;
    private static final int ZOOM_LEVEL_MIN = 8;
    private static final int ZOOM_LEVEL_MAX = 19;


    /**
     * Constructor of the class
     * @param tileManager : a tileManager
     * @param wayPointsManager : a wayPointsManager
     * @param mapViewParametersP : a MapViewParameters property
     */
    public BaseMapManager(TileManager tileManager, WayPointsManager wayPointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersP) {

        this.tileManager = tileManager;
        this.wayPointsManager = wayPointsManager;
        this.mapViewParametersP = mapViewParametersP;

        this.coordinatesMouse = new SimpleObjectProperty<>();
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);

        addPaneListeners();
        addCanvasProperties();
        addScrollListener();
        addDragListener();
        mapViewParametersP.addListener(event -> redrawOnNextPulse());
        redrawOnNextPulse();
    }

    private void addPaneListeners(){
        pane.setOnMousePressed(e -> coordinatesMouse.setValue(new Point2D(e.getX(), e.getY())));

        pane.setOnMouseClicked(event -> {
            if (event.isStillSincePress()) {
                this.wayPointsManager.addWaypoint(
                        mapViewParametersP.get().xUpperLeftMapView() + event.getX(),
                        mapViewParametersP.get().yUpperLeftMapView() + event.getY()
                );
            }

        });
    }

    private void addScrollListener() {
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 250);
            double zoomDelta = Math.signum(e.getDeltaY());

            int currentZoomLevel = mapViewParametersP.get().zoomLevel();

            int newZoomLevel = Math2.clamp(ZOOM_LEVEL_MIN,currentZoomLevel +  (int) zoomDelta, ZOOM_LEVEL_MAX);

            int difference = newZoomLevel - currentZoomLevel;

            Point2D topLeftPoint = mapViewParametersP.get().topLeft();

            topLeftPoint = topLeftPoint.add(e.getX(), e.getY());
            topLeftPoint = topLeftPoint.multiply(Math.scalb(1, difference));
            topLeftPoint = topLeftPoint.subtract(e.getX(), e.getY());

            mapViewParametersP.setValue(new MapViewParameters(newZoomLevel, topLeftPoint.getX(), topLeftPoint.getY()));

        });

    }



    private void addCanvasProperties(){
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        // JavaFX calls redrawIfNeeded at each beat
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
                    assert oldS == null;
                    newS.addPreLayoutPulseListener(this::redrawIfNeeded);
                });

        pane.widthProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        pane.heightProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());

    }


    private void addDragListener(){
        pane.setOnMouseDragged(event -> {
                Point2D point = mapViewParametersP.get().topLeft();
                point = point.add(coordinatesMouse.get());
                point = point.subtract(event.getX(), event.getY());
                mapViewParametersP.setValue(mapViewParametersP.get().withMinXY(point.getX(), point.getY()));
                coordinatesMouse.setValue(new Point2D(event.getX(), event.getY()));
        });
    }



    /**
     * Method that redraws the map if and only if the attribute redrawNeeded is true
     */
    private void redrawIfNeeded() {
        if (redrawNeeded) {
            redrawNeeded = false;

            GraphicsContext gc = canvas.getGraphicsContext2D();
            int tileX = (int) Math.floor(mapViewParametersP.get().xUpperLeftMapView() / SIZE_TILE);
            int tileY = (int) Math.floor(mapViewParametersP.get().yUpperLeftMapView() / SIZE_TILE);
            int xMax = (int) Math.ceil(canvas.getWidth() / SIZE_TILE);
            int yMax = (int) Math.ceil(canvas.getHeight() / SIZE_TILE);


            //Iterate and draw tiles in the canvas
            for (int i = 0; i <= xMax; i++) {
                for (int j = 0; j <= yMax; j++) {
                    try {
                        gc.drawImage(tileManager.getTileImage(new TileManager.TileId(mapViewParametersP.get().zoomLevel(),
                                        i + tileX, j + tileY)),
                                (i + tileX) * SIZE_TILE - mapViewParametersP.get().xUpperLeftMapView(),
                                (j + tileY) * SIZE_TILE - mapViewParametersP.get().yUpperLeftMapView());
                    } catch (IOException e) {}
                }

            }
        }
    }


    /**
     * method to request a redraw of the map at the next beat, sets redrawNeeded to true if called
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * @return the JavaFX panel displaying the basemap.
     */
    public Pane pane() {
        return pane;
    }

}

