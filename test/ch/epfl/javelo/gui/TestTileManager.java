package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;


public final class TestTileManager extends Application {


    @Test
    public void TileIdThrows(){
        assertThrows(IllegalArgumentException.class, () ->
                 new TileManager. TileId(23, 271725, 185422));
    }


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws IOException {

        TileManager tm = new TileManager(Path.of("./cache"), "tile.openstreetmap.org");

     //   TileManager.TileId tileId = new TileManager.TileId(23, 271725, 185422);
       Image tileImage = tm.getTileImage(new TileManager.TileId(19, 271725, 185422));
        //Image image2 = tm.getTileImage(new TileManager.TileId(19, 271725, 185422));

       // assertEquals(tileImage.getHeight(), image2.getHeight());
        Platform.exit();
    }
}