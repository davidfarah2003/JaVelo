package ch.epfl.javelo.gui;
import ch.epfl.javelo.Math2;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;


/**
 * BaseMapManager class
 * @author Wesley Nana Davies(344592)
 */
public final class BaseMapManager {
    private final WayPointsManager wayPointsManager;
    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private ObjectProperty<Point2D> coordinatesMouse;
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

        this.coordinatesMouse = new SimpleObjectProperty<>(new Point2D(0,0));
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);

        addCanvasProperties();

        pane.setOnMouseClicked(event -> {
            coordinatesMouse.setValue(new Point2D(event.getX(), event.getY()));
                if (event.isStillSincePress()) {
                    this.wayPointsManager.addWaypoint(
                            mapViewParametersP.get().topLeft().getX() + event.getX(),
                            mapViewParametersP.get().topLeft().getY() + event.getY()
                    );

                }

        });

        addScrollListener();
        addDragListener();
        redrawOnNextPulse();
    }

    private void addScrollListener() {
        pane.setOnScroll(event -> {
            int newZoomLevel = Math2.clamp(ZOOM_LEVEL_MIN, (int) Math.rint(mapViewParametersP.get().zoomLevel()
                    + (event.getDeltaY())), ZOOM_LEVEL_MAX);

            int difference = newZoomLevel - mapViewParametersP.get().zoomLevel();

            Point2D topLeftPoint = mapViewParametersP.get().topLeft();
            topLeftPoint = topLeftPoint.add(event.getX(), event.getY());
            topLeftPoint = topLeftPoint.multiply(Math.scalb(1, difference));
            topLeftPoint = topLeftPoint.subtract(event.getX(), event.getY());

            mapViewParametersP.setValue(new MapViewParameters(newZoomLevel, topLeftPoint.getX(), topLeftPoint.getY()));
            redrawOnNextPulse();
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

        pane.widthProperty().addListener((p, oldS, newS) -> {
            pane.setMinWidth((Double)newS);
            redrawOnNextPulse();
        });

        pane.heightProperty().addListener((p, oldS, newS) -> {
           pane.setMinHeight((Double) newS);
            redrawOnNextPulse();
         });

    }


    private void addDragListener(){
        pane.setOnMouseDragged(event -> {
            Point2D point = mapViewParametersP.get().topLeft();
            point = point.add(coordinatesMouse.get());
            point = point.subtract(event.getX(), event.getY());


            mapViewParametersP.setValue(mapViewParametersP.get().withMinXY(point.getX(), point.getY()));
            redrawOnNextPulse();

            //commented due to it being unnecessary and breaking encapsulation
            /*
            ArrayList<Waypoint> list = new ArrayList<>(wayPointsManager.getWayPoints());
            System.out.println(list.size());

            for (Waypoint w : list){
                int nodeId = w.nodeID();
                PointWebMercator p = PointWebMercator.ofPointCh(w.point());
                double x = p.xAtZoomLevel(mapViewParametersP.get().zoomLevel());
                double y = p.yAtZoomLevel(mapViewParametersP.get().zoomLevel());
                double relativeX = x - mapViewParametersP.get().topLeft().getX();
                double relativeY = y - mapViewParametersP.get().topLeft().getY();
                PointWebMercator p2 = PointWebMercator.of(mapViewParametersP.get().zoomLevel(), x, y);
                PointCh p3 = p2.toPointCh();
                wayPointsManager.removeWaypoint(w);

            }
            wayPointsManager.drawWayPoints();

             */
            coordinatesMouse.setValue(new Point2D(event.getX(), event.getY()));
        });
    }




    private void drawTileInCanvas(GraphicsContext gc, int x, int y, double sourceRectangleX,
                                  double sourceRectangleY, double sourceWidth, double sourceHeight,
                                  double destinationX, double destinationY) {
        try {
            gc.drawImage(tileManager.getTileImage(new TileManager.TileId(mapViewParametersP.get().zoomLevel(), x, y)),
                    sourceRectangleX, sourceRectangleY, sourceWidth, sourceHeight,
                    destinationX, destinationY, sourceWidth, sourceHeight);
        } catch (IOException e) {}
    }


    /**
     * Method that redraws the map if and only if the attribute redrawNeeded is true
     */
    private void redrawIfNeeded() {
        if (redrawNeeded) {
            redrawNeeded = false;

            GraphicsContext gc = canvas.getGraphicsContext2D();

            int xMin = (int) Math.floor(mapViewParametersP.get().xUpperLeftMapView() / SIZE_TILE);
            int xMax = (int) Math.floor((mapViewParametersP.get().xUpperLeftMapView() + canvas.getWidth()) / SIZE_TILE);
            int yMin = (int) Math.floor(mapViewParametersP.get().yUpperLeftMapView() / SIZE_TILE);
            int yMax = (int) Math.floor((mapViewParametersP.get().yUpperLeftMapView() + canvas.getHeight()) / SIZE_TILE);

            double xSourceFirstTile = mapViewParametersP.get().xUpperLeftMapView() - xMin * SIZE_TILE;
            double ySourceFirstTile = mapViewParametersP.get().yUpperLeftMapView() - yMin * SIZE_TILE;
            double sourceWidthFirstTile = SIZE_TILE - xSourceFirstTile;
            double sourceHeightFirstTile = SIZE_TILE - ySourceFirstTile;
            double widthLastTile = mapViewParametersP.get().xUpperLeftMapView() + canvas.getWidth() - xMax * SIZE_TILE;
            double heightLastTile = mapViewParametersP.get().yUpperLeftMapView() + canvas.getHeight() - yMax * SIZE_TILE;

            double length = 0;
            double height = 0;
            for (int y = yMin; y <= yMax; y++) {
                for (int x = xMin; x <= xMax; x++) {
                    if (y == yMin) {
                        if (x == xMin) {
                            drawTileInCanvas(gc, x, y, xSourceFirstTile, ySourceFirstTile,
                                    sourceWidthFirstTile, sourceHeightFirstTile, 0, 0);

                            length += sourceWidthFirstTile;
                        }

                        else if (x == xMax) {
                            drawTileInCanvas(gc, x, y, 0, ySourceFirstTile,
                                    widthLastTile, sourceHeightFirstTile, length, 0);
                        }

                        else {
                            drawTileInCanvas(gc, x, y,0, ySourceFirstTile,
                                    SIZE_TILE, sourceHeightFirstTile, length, 0);

                            length += SIZE_TILE;
                        }
                    }

                    else if (y == yMax) {
                        if (x == xMin) {
                            drawTileInCanvas(gc, x, y, xSourceFirstTile, 0,
                                    sourceWidthFirstTile, heightLastTile, 0, height);

                            length += sourceWidthFirstTile;
                        }

                        else if (x == xMax) {
                            drawTileInCanvas(gc, x, y,0, 0,
                                    widthLastTile, heightLastTile, length, height);
                        }

                        else{
                            drawTileInCanvas(gc, x, y,0,0,
                                    SIZE_TILE, heightLastTile, length, height);

                            length += SIZE_TILE;
                        }
                    }

                    else{
                        if (x == xMin){
                            drawTileInCanvas(gc, x, y, xSourceFirstTile, 0,
                                    sourceWidthFirstTile, SIZE_TILE, 0, height);

                            length += sourceWidthFirstTile;
                        }

                        else if (x == xMax){
                            drawTileInCanvas(gc, x, y, 0,0,
                                    widthLastTile, SIZE_TILE, length, height);
                        }

                        else{
                            drawTileInCanvas(gc, x, y,0, 0,
                                    SIZE_TILE, SIZE_TILE, length, height);

                            length += SIZE_TILE;
                        }
                    }
                }
                length = 0;
                height += (y == yMin ? sourceHeightFirstTile : SIZE_TILE);
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

