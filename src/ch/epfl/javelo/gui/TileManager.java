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
        memoryCache = new LRUCache<>(100, .75f);
    }

    public Image getTileImage(TileId tile) throws IOException {
        String fileName = tile.getFileNameLocal();
        Path filePath = combineTwoPaths(cachePath.toString(), fileName).normalize();

        if (memoryCache.containsKey(tile)) {
            return memoryCache.get(tile);
        }
        else if (Files.exists(filePath)){
            try (FileInputStream i = new FileInputStream(filePath.toString())){
                Image tileImage = new Image(i);
                memoryCache.put(tile, tileImage);
                return tileImage;
            }
        }
        else {
            URL u = tile.getURL(hostName);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            Files.createDirectories(filePath.getParent());
            try (
                    InputStream i = c.getInputStream();
                    OutputStream o = new FileOutputStream(filePath.toFile())
            )
            {
                Image tileImage = new Image(i);
                memoryCache.put(tile, tileImage);
                i.transferTo(o);

                return tileImage;
            }
        }
    }

    private Path combineTwoPaths(String path1, String path2)
    {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.toPath();
    }


    record TileId(int zoomLevel, int xIndex, int yIndex) {
         public static boolean isValid(int zoomLevel, int xIndex, int yIndex) {
             Preconditions.checkArgument(zoomLevel >= 0 && zoomLevel <= 20);
             int maxIndex = (int) Math.pow(2, zoomLevel) - 1;
             return (xIndex >= 0 && xIndex <= maxIndex && yIndex >= 0 && yIndex <= maxIndex);
         }

        String getFileNameURL(){
            return "%d/%d/%d.png".formatted(zoomLevel, xIndex, yIndex);
        }

        String getFileNameLocal(){
            return "%d%s%d%s%d.png".formatted(zoomLevel, File.separator, xIndex, File.separator, yIndex);
        }

         URL getURL(String hostName) throws MalformedURLException {
             return new URL("https", hostName, getFileNameURL());
         }
    }

}
