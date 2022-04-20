package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import java.io.IOException;

public final class BaseMapManager {
    private final TileManager tileManager;
  //  private final WayPointManager wayPointManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final static int SIZE_TILE = 256;
// WayPointManager wayPointManager;

    public BaseMapManager(TileManager tileManager,
             ObjectProperty<MapViewParameters> mapViewParametersP) {
            this.tileManager = tileManager;
           // this.wayPointManager = wayPointManager;
            this.mapViewParametersP = mapViewParametersP;
            this.canvas = new Canvas();
            this.pane = new Pane(canvas);
            canvas.widthProperty().bind(pane.widthProperty());
            canvas.heightProperty().bind(pane.heightProperty());
            canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
            });
            redrawOnNextPulse();
    }

    private void redrawIfNeeded() {

        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        int xMin = (int) Math.floor(mapViewParametersP.get().xUpperLeftMapView() / SIZE_TILE);
        int yMin = (int) Math.floor(mapViewParametersP.get().yUpperLeftMapView() / SIZE_TILE);
        int xMax = (int) Math.floor((mapViewParametersP.get().xUpperLeftMapView() + canvas.getWidth()) / SIZE_TILE);
        int yMax = (int) Math.floor((mapViewParametersP.get().yUpperLeftMapView() + canvas.getHeight()) / SIZE_TILE);

        double xValue = mapViewParametersP.get().xUpperLeftMapView() - xMin * SIZE_TILE;
        double yValue = mapViewParametersP.get().yUpperLeftMapView() - yMin * SIZE_TILE;
        double sourceWidth = SIZE_TILE - xValue;
        double sourceHeight = SIZE_TILE - yValue;
        double xFinal = mapViewParametersP.get().xUpperLeftMapView() + canvas.getWidth() - xMax * SIZE_TILE;
        double yFinal = mapViewParametersP.get().yUpperLeftMapView() + canvas.getHeight() - yMax * SIZE_TILE;

        double length = 0;
        double height = 0;

        for (int j = yMin; j <= yMax; j++) {
            for (int i = xMin; i <= xMax; i++) {
                if (j == yMin) {
                    if (i == xMin) {
                        try {
                            gc.drawImage(tileManager.getTileImage(
                                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                    xValue, yValue, sourceWidth, sourceHeight, 0, 0, sourceWidth, sourceHeight);
                        } catch (IOException e) {}
                        length += sourceWidth;
                    }

                    else if (i == xMax) {
                        try {
                            gc.drawImage(tileManager.getTileImage(
                                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                    0, yValue, xFinal, sourceHeight, length, 0, xFinal, sourceHeight);
                        } catch (IOException e) {}
                    }

                    else {
                        try {
                            gc.drawImage(tileManager.getTileImage(
                                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                    0, yValue, SIZE_TILE, sourceHeight, length, 0, SIZE_TILE, sourceHeight);
                        } catch (IOException e) {}
                        length += SIZE_TILE;
                    }
                }

                else if (j == yMax) {
                    if (i == xMin) {
                        try {
                            gc.drawImage(tileManager.getTileImage(
                                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                    xValue, 0, sourceWidth, yFinal, 0, height, sourceWidth, yFinal);
                        } catch (IOException e) {}
                        length += sourceWidth;
                    }

                    else if (i == xMax) {
                        try {
                            gc.drawImage(tileManager.getTileImage(
                                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                    0, 0, xFinal, yFinal, length, height, xFinal, yFinal);
                        } catch (IOException e) {}
                    }

                    else{
                        try {
                            gc.drawImage(tileManager.getTileImage(
                                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                    0, 0, SIZE_TILE, yFinal, length, height, SIZE_TILE, yFinal);
                        } catch (IOException e) {}
                        length += SIZE_TILE;
                    }
                }

                else{
                    if (i == xMin){
                        try {
                            gc.drawImage(tileManager.getTileImage(
                                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                    xValue, 0, sourceWidth, SIZE_TILE, 0, height, sourceWidth, SIZE_TILE);
                        } catch (IOException e) {}
                        length += sourceWidth;
                    }

                    else if (i == xMax){
                        try {
                            gc.drawImage(tileManager.getTileImage(
                                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                    0, 0, xFinal, SIZE_TILE, length, height, xFinal, SIZE_TILE);
                        }
                        catch (IOException e) {}
                    }

                    else{
                        try {
                                gc.drawImage(tileManager.getTileImage(
                                                new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                                        0, 0, SIZE_TILE, SIZE_TILE, length, height, SIZE_TILE, SIZE_TILE);
                        } catch (IOException e) {}
                        length += SIZE_TILE;
                    }
                }
            }

            length = 0;
            height += (j == yMin ? sourceHeight : SIZE_TILE);

        }




        // int x = (int) Math.ceil(canvas.getWidth()/ 256);
        // int y =  (int) Math.ceil(canvas.getHeight() / 256);


   //     for (int j = yMin; j <= yMax; j++) {
    //        for (int i = xMin; i <= xMax; i++) {
     //           try {
     //               gc.drawImage(tileManager.getTileImage(
     //                               new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
      //                      rememberX * 256, rememberY * 256
       //             );
       //             rememberX++;

        //        } catch (IOException exception) {

        //        }
        //    }
      //      rememberX = 0;
       //     rememberY++;
      //  }


    }
       // System.out.println(x);
      //  System.out.println(y);

        /*
        for (int j = 0; j < y; j++){
            for (int i = 0; i < x; i++){
                try {
                    gc.drawImage(tileManager.getTileImage(
                            new TileManager.TileId(mapViewParametersP.get().zoomLevel(),
                                    (int) Math.floor((mapViewParametersP.get().xUpperLeftMapView() + i * 256) / 256),
                                    (int) Math.floor((mapViewParametersP.get().yUpperLeftMapView() + j * 256) / 256))
                    ), i * 256, j * 256
                    , 256, 256);
                }
                catch (IOException e){
                }
            }
        }
    }

         */

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    public Pane pane() {
        return pane;
    }
     //   double xIndexTopLeft = mapViewParametersP.get().xUpperLeftMapView();
      //  double yIndexTopLeft = mapViewParametersP.get().yUpperLeftMapView();
      //  double xIndexBottomRight = xIndexTopLeft + canvas.getWidth();
       // double yIndexBottomRight = yIndexTopLeft + canvas.getHeight();






        /*
        Canvas is an image that can be drawn on using a set of graphics commands provided by a GraphicsContext.
A Canvas node is constructed with a width and height that specifies the size of the image into which the canvas drawing commands are rendered. All drawing operations are clipped to the bounds of that image.

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

