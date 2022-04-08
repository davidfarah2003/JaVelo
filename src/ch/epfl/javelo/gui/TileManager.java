package ch.epfl.javelo.gui;

import ch.epfl.javelo.LRUCache;
import ch.epfl.javelo.Preconditions;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.LinkedHashMap;
import javafx.scene.image.Image;

public final class TileManager {

    private final Path cachePath;
    private final String hostName;
    private final LinkedHashMap<TileId, Image> memoryCache;

    public TileManager(Path cachePath, String hostName){
        this.cachePath = cachePath;
        this.hostName = hostName;
        memoryCache = new LRUCache<>(16, .75f);
    }

    public Image getTileImage(TileId tile) throws IOException {
        // avec true apres chaque get,
        // la paire key-value se place a la fin
        // least-recently acessed -> most recentely acessed

        Image tileImage;

        if (memoryCache.containsKey(tile)) {
            return memoryCache.get(tile);
        }

        else if (Files.exists(Path.of(cachePath + tile.getFileName()))) {
            try (InputStream i = TileManager.class.getResourceAsStream(cachePath + tile.getFileName())){
                tileImage = new Image(i);
                memoryCache.put(tile, tileImage);
                return tileImage;
            }


        }
        else {
            URL u = tile.getURL(hostName);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            Files.createDirectories(Path.of(cachePath + "%d/%d".formatted(tile.zoomLevel, tile.xIndex)));
            File file = new File(cachePath + tile.getFileName());

            OutputStream o = new FileOutputStream(file);

            try (InputStream i = c.getInputStream()) {
                i.transferTo(o);
                memoryCache.put(tile, new Image(i));
                return new Image(i);
            }

           // try (InputStream i = new FileInputStream(file)){
           //     map.put(tile, new Image(i));
           //     return new Image(i);
          //  }

        }
    }

    record TileId(int zoomLevel, int xIndex, int yIndex) {
         public static boolean isValid(int zoomLevel, int xIndex, int yIndex) {
             Preconditions.checkArgument(zoomLevel >= 0 && zoomLevel <= 20);
             int maxIndex = (int) Math.pow(2, zoomLevel) - 1;
             return (xIndex >= 0 && xIndex <= maxIndex && yIndex >= 0 && yIndex <= maxIndex);
         }

         String getFileName(){
             return "%d/%d/%d.png".formatted(zoomLevel, xIndex, yIndex);
         }

         URL getURL(String hostName) throws MalformedURLException {
             return new URL("https", hostName, getFileName());
         }
    }

}
