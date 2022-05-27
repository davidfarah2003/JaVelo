package ch.epfl.javelo.gui;

import ch.epfl.javelo.LRUCache;
import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Tile Manager class
 * This class manages the access to the tiles constituting parts of Switzerland
 *
 *  @author Wesley Nana Davies (344592)
 *  @author David Farah (341017)
 *
 */
public final class TileManager {

    private final Path cachePath;
    private final String hostName;
    private final LinkedHashMap<TileId, Image> memoryCache;

    public TileManager(Path cachePath, String hostName){
        this.cachePath = cachePath;
        this.hostName = hostName;
        memoryCache = new LRUCache<>(100, .75f);
    }

    public Image getTileImage(TileId tile) throws IOException {
        String fileName = tile.getFileNameLocal();
        Path filePath = cachePath.resolve(fileName).normalize();

        if (memoryCache.containsKey(tile)) {
            return memoryCache.get(tile);

        }
        else if (Files.exists(filePath)) {
            return getImageFromFile(filePath, tile);

        } else {
            URL u = tile.getURL(hostName);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            Files.createDirectories(filePath.getParent());
            try (InputStream i = c.getInputStream();
                 FileOutputStream o = new FileOutputStream(filePath.toFile()))
            {
                i.transferTo(o);
            }

            return getImageFromFile(filePath, tile);
        }
    }

    private Image getImageFromFile(Path filePath, TileId tile) throws IOException {
        try (InputStream i = new FileInputStream(filePath.toFile())) {
            Image tileImage = new Image(i);
            memoryCache.put(tile, tileImage);
            return tileImage;
        }
    }


    record TileId(int zoomLevel, int xIndex, int yIndex) {

        TileId{
            Preconditions.checkArgument(isValid(zoomLevel, xIndex, yIndex));
        }

         public static boolean isValid(int zoomLevel, int xIndex, int yIndex) {

             int maxIndex = (int) Math.pow(2, zoomLevel) - 1;
             return (zoomLevel >= 0 && zoomLevel <= 20 &&
                     xIndex >= 0 && xIndex <= maxIndex &&
                     yIndex >= 0 && yIndex <= maxIndex);
         }

        private String getFileNameURL(){
            return "/%d/%d/%d.png".formatted(zoomLevel, xIndex, yIndex);
        }

        private String getFileNameLocal(){
            return "%d%s%d%s%d.png".formatted(zoomLevel, File.separator, xIndex, File.separator, yIndex);
        }

         private URL getURL(String hostName) throws MalformedURLException {
             return new URL("https", hostName, getFileNameURL());
         }
    }

}
