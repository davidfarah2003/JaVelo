package ch.epfl.javelo.gui;
import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;


/**
 * Class that manages the display and interaction with the basemap
 */
public final class BaseMapManager {
    private final WayPointsManager wayPointsManager;
    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private ObjectProperty<Point2D> coordinatesMouseWhenPressed;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final static int SIZE_TILE = 256;


    /**
     * @param tileManager
     * @param mapViewParameters
     */
    public BaseMapManager(
            TileManager tileManager , WayPointsManager wayPointsManager,
            ObjectProperty<MapViewParameters> mapViewParameters
    )
    {
        this.tileManager = tileManager;
        this.wayPointsManager = wayPointsManager;
        this.mapViewParametersP = mapViewParameters;

        this.coordinatesMouseWhenPressed = new SimpleObjectProperty<>(new Point2D(0,0));
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        // JavaFX calls redrawIfNeeded at each beat
        canvas.sceneProperty().addListener((p, oldS, newS)
                -> {
                    assert oldS == null;
                    newS.addPreLayoutPulseListener(this::redrawIfNeeded);
                }
        );

        //Event handlers:
        pane.setOnScroll(event -> {
           mapViewParametersP.setValue(new MapViewParameters(Math2.clamp(8, (int) Math.round(mapViewParametersP.get().zoomLevel()
           + event.getDeltaY()), 19), mapViewParametersP.get().xUpperLeftMapView(), mapViewParametersP.get().yUpperLeftMapView()));
           redrawOnNextPulse();
        });
        pane.setOnMousePressed(event -> {
                coordinatesMouseWhenPressed = new SimpleObjectProperty<>(new Point2D(event.getX(), event.getY()));

                if (event.isStillSincePress()){
                    wayPointsManager.addWaypoint(coordinatesMouseWhenPressed.get().getX(),
                                                coordinatesMouseWhenPressed.get().getY());
                }
                else{
                    pane.setOnMouseDragged(event1 ->
                            {
                                mapViewParameters.setValue
                                (
                                    new MapViewParameters(
                                        mapViewParameters.get().zoomLevel(),
                                        (coordinatesMouseWhenPressed.get().getX() - event1.getX())/25 + mapViewParameters.get().xUpperLeftMapView(),
                                        (coordinatesMouseWhenPressed.get().getY() - event1.getY())/25 + mapViewParameters.get().yUpperLeftMapView()
                                        )
                                );

                                redrawOnNextPulse();
                            }
                    );
                }
            });

        redrawOnNextPulse();
    }


    private void drawTileInCanvas(GraphicsContext gc, int x, int y, double sourceRectangleX,
                                  double sourceRectangleY, double sourceWidth, double sourceHeight,
                                  double destinationX, double destinationY) {
        try {
            gc.drawImage(tileManager.getTileImage(new TileManager.TileId(mapViewParametersP.get().zoomLevel(), x, y)),
                    sourceRectangleX, sourceRectangleY, sourceWidth, sourceHeight, destinationX, destinationY,
                    sourceWidth, sourceHeight);
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

    //   double xIndexTopLeft = mapViewParametersP.get().xUpperLeftMapView();
    //  double yIndexTopLeft = mapViewParametersP.get().yUpperLeftMapView();
    //  double xIndexBottomRight = xIndexTopLeft + canvas.getWidth();
    // double yIndexBottomRight = yIndexTopLeft + canvas.getHeight();


/*
    Canvas is an image that can be drawn on using a set of graphics commands provided by a GraphicsContext.
    A Canvas node is constructed with a width and height that specifies the size of the image into which the canvas
    drawing commands are rendered. All drawing operations are clipped to the bounds of that image.

    Example:
        import javafx.scene.*;
        import javafx.scene.paint.*;
        import javafx.scene.canvas.*;

        Group root = new Group();
        Scene s = new Scene(root, 300, 300, Color.BLACK);

        final Canvas canvas = new Canvas(250,250);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLUE);
        gc.fillRect(75,75,100,100);

        root.getChildren().add(canvas);
*/

    }

