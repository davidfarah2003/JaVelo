package ch.epfl.javelo.gui;
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
    private final ObjectProperty<MapViewParameters> mapViewParameters;
    private ObjectProperty<Point2D> test;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final static int SIZE_TILE = 256;


    /**
     * @param tileManager
     * @param mapViewParameters
     */
    public BaseMapManager(
            TileManager tileManager , WayPointsManager wayPointsManager, ObjectProperty<MapViewParameters> mapViewParameters
    )
    {
        this.tileManager = tileManager;
        this.wayPointsManager = wayPointsManager;
        this.mapViewParameters = mapViewParameters;

        this.test = new SimpleObjectProperty<>(new Point2D(0,0));
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
       //     System.out.println(event.getX() + " " + event.getY());
         //  mapViewParametersP.setValue(new MapViewParameters(Math2.clamp(8, (int) Math.round(mapViewParametersP.get().zoomLevel()
           //+ event.getDeltaY()), 19), mapViewParametersP.get().xUpperLeftMapView(), mapViewParametersP.get().yUpperLeftMapView()));
         //  redrawOnNextPulse();
        });
        pane.setOnMousePressed(event -> {
                Point2D point = new Point2D(event.getX(), event.getY());
                test = new SimpleObjectProperty<>(point);
                System.out.println(test.get());

                if (event.isStillSincePress()){
                    // ajout point
                }
                else{
                    pane.setOnMouseDragged(event1 ->
                            {
                                mapViewParameters.setValue
                                (
                                    new MapViewParameters(
                                        mapViewParameters.get().zoomLevel(),
                                        (test.get().getX() - event1.getX())/25 + mapViewParameters.get().xUpperLeftMapView(),
                                        (test.get().getY() - event1.getY())/25 + mapViewParameters.get().yUpperLeftMapView()
                                        )
                                );

                                redrawOnNextPulse();
                            }
                    );
                }

               // pane.setOnMouseReleased(event2 -> redrawOnNextPulse());
            });

        redrawOnNextPulse();
    }

    /**
     * method that performs a redraw of the map if and only if the attribute redrawNeeded is true
     */
    private boolean redrawIfNeeded() {
        if (redrawNeeded) {
            redrawNeeded = false;

            //get canvas context
            GraphicsContext gc = canvas.getGraphicsContext2D();


            int xMin = (int) Math.floor(mapViewParameters.get().xUpperLeftMapView() / SIZE_TILE);
            int xMax = (int) Math.floor((mapViewParameters.get().xUpperLeftMapView() + canvas.getWidth()) / SIZE_TILE);

            int yMin = (int) Math.floor(mapViewParameters.get().yUpperLeftMapView() / SIZE_TILE);
            int yMax = (int) Math.floor((mapViewParameters.get().yUpperLeftMapView() + canvas.getHeight()) / SIZE_TILE);


            double xValue = mapViewParameters.get().xUpperLeftMapView() - xMin * SIZE_TILE;
            double yValue = mapViewParameters.get().yUpperLeftMapView() - yMin * SIZE_TILE;
            double sourceWidth = SIZE_TILE - xValue;
            double sourceHeight = SIZE_TILE - yValue;
            double xFinal = mapViewParameters.get().xUpperLeftMapView() + canvas.getWidth() - xMax * SIZE_TILE;
            double yFinal = mapViewParameters.get().yUpperLeftMapView() + canvas.getHeight() - yMax * SIZE_TILE;


            //What does this loop do?
            double length = 0;
            double height = 0;
            for (int j = yMin; j <= yMax; j++) {
                for (int i = xMin; i <= xMax; i++) {
                    // System.out.println(mapViewParametersP.get().zoomLevel());
                    if (j == yMin) {
                        if (i == xMin) {
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        xValue, yValue, sourceWidth, sourceHeight, 0, 0, sourceWidth, sourceHeight);
                            } catch (IOException e) {}
                            length += sourceWidth;
                        }

                        else if (i == xMax) {
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        0, yValue, xFinal, sourceHeight, length, 0, xFinal, sourceHeight);
                            } catch (IOException e) {}
                        }

                        else {
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        0, yValue, SIZE_TILE, sourceHeight, length, 0, SIZE_TILE, sourceHeight);
                            } catch (IOException e) {}
                            length += SIZE_TILE;
                        }
                    }

                    else if (j == yMax) {
                        if (i == xMin) {
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        xValue, 0, sourceWidth, yFinal, 0, height, sourceWidth, yFinal);
                            } catch (IOException e) {}
                            length += sourceWidth;
                        }

                        else if (i == xMax) {
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        0, 0, xFinal, yFinal, length, height, xFinal, yFinal);
                            } catch (IOException e) {}
                        }

                        else{
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        0, 0, SIZE_TILE, yFinal, length, height, SIZE_TILE, yFinal);
                            } catch (IOException e) {}
                            length += SIZE_TILE;
                        }
                    }

                    else{
                        if (i == xMin){
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        xValue, 0, sourceWidth, SIZE_TILE, 0, height, sourceWidth, SIZE_TILE);
                            } catch (IOException e) {}
                            length += sourceWidth;
                        }

                        else if (i == xMax){
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        0, 0, xFinal, SIZE_TILE, length, height, xFinal, SIZE_TILE);
                            }
                            catch (IOException e) {}
                        }

                        else{
                            try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParameters.get().zoomLevel(), i, j)),
                                        0, 0, SIZE_TILE, SIZE_TILE, length, height, SIZE_TILE, SIZE_TILE);
                            } catch (IOException e) {}
                            length += SIZE_TILE;
                        }
                    }
                }
                length = 0;
                height += (j == yMin ? sourceHeight : SIZE_TILE);
            }
            return true;
        }

        return false;
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

