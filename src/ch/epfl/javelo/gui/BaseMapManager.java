package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.io.IOException;

public final class BaseMapManager {
    private final TileManager tileManager;
  //  private final WayPointManager wayPointManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private Canvas canvas;
    private Pane pane;
    private boolean redrawNeeded;
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
        System.out.println("test");

        GraphicsContext gc = canvas.getGraphicsContext2D();


        canvas.widthProperty().addListener(o -> System.out.printf("New canvas width: %.2f\n", canvas.getWidth()));
        canvas.heightProperty().addListener(o -> System.out.printf("New canvas height: %.2f\n", canvas.getHeight()));


        System.out.println(canvas.getHeight());
        System.out.println(canvas.getWidth());

        int xMin = (int) Math.floor(mapViewParametersP.get().xUpperLeftMapView() / 256);
        int yMin = (int) Math.floor(mapViewParametersP.get().yUpperLeftMapView() / 256);
        int xMax = (int) Math.floor((mapViewParametersP.get().xUpperLeftMapView() + canvas.getWidth()) / 256);
        int yMax = (int) Math.floor((mapViewParametersP.get().yUpperLeftMapView() + canvas.getHeight()) / 256);


        // int x = (int) Math.ceil(canvas.getWidth()/ 256);
        // int y =  (int) Math.ceil(canvas.getHeight() / 256);
        int rememberX = 0;
        int rememberY = 0;
        for (int j = yMin; j <= yMax; j++) {
            for (int i = xMin; i <= xMax; i++) {
                try {
                    gc.drawImage(tileManager.getTileImage(
                                    new TileManager.TileId(mapViewParametersP.get().zoomLevel(), i, j)),
                            rememberX * 256, rememberY * 256
                    );
                    rememberX++;

                } catch (IOException exception) {

                }
            }
            rememberX = 0;
            rememberY++;
        }
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

