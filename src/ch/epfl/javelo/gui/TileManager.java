package ch.epfl.javelo.gui;

import ch.epfl.javelo.CacheLinkedHashMap;
import ch.epfl.javelo.Preconditions;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.LinkedHashMap;
import javafx.scene.image.Image;

public final class TileManager {

    private final Path access;
    private final String name;

    public TileManager(Path access, String name){
        this.access = access;
        this.name = name;
    }

    public Image imageForTileAt(TileId tile) throws IOException {
        // avec true apres chaque get,
        // la paire key-value se place a la fin
        // least-recently acessed -> most recentely acessed

        LinkedHashMap<TileId, Image> map =
                new CacheLinkedHashMap<>(16, .75f, true);

        if (map.containsKey(tile)) {
            return map.get(tile);

        } else if (Files.exists(Path.of(""))) {
            // comment la charger?
            map.put(tile, ...)
            return ...
        }
        else {

            URL u = tile.getURL("tile.openstreetmap.org");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            Files.createDirectories(Path.of("zoomLevel/yCoordinate/xCoordinate.png"));

            File file = new File("...");

            OutputStream o = new FileOutputStream(file);

            try (InputStream i = c.getInputStream()) {
                i.transferTo(o);
                map.put(tile, ...);
                return new Image(i);
            }


            //  ImageInputStream stream = ImageIO.createImageInputStream(o);
            // return stream;


        }
    }

     record TileId(int zoomLevel, int xIndex, int yIndex) {

         public static boolean isValid(int zoomLevel, int xIndex, int yIndex) {
             Preconditions.checkArgument(zoomLevel >= 0);
             int maxIndex = (int) Math.pow(2, zoomLevel) - 1;
             return (xIndex >= 0 && xIndex <= maxIndex && yIndex >= 0 && yIndex <= maxIndex);
         }

         URL getURL(String hostName) throws MalformedURLException {
             String file = "/%d/%d/%d.png".formatted(zoomLevel, xIndex, yIndex);
             return new URL("https", hostName, file);

         }
     }


}
