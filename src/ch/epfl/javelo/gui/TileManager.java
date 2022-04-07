package ch.epfl.javelo.gui;

import ch.epfl.javelo.CacheLinkedHashMap;
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

    private final Path access;
    private final String hostName;
    private final LinkedHashMap<TileId, Image> map;

    public TileManager(Path access, String hostName){
        this.access = access;
        this.hostName = hostName;
        map = new CacheLinkedHashMap<>(16, .75f, true);
    }

    public Image imageForTileAt(TileId tile) throws IOException {
        // avec true apres chaque get,
        // la paire key-value se place a la fin
        // least-recently acessed -> most recentely acessed
        if (map.containsKey(tile)) {
            return map.get(tile);
        }

        else if (Files.exists(Path.of(access + tile.getFileName()))) {
            TileManager.class.getResourceAsStream(access + tile.getFileName());
            try (InputStream i = TileManager.class.getResourceAsStream(access + tile.getFileName())){
                map.put(tile, new Image(i));
                return new Image(i);
            }

        }
        else {
            URL u = tile.getURL(hostName);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            Files.createDirectories(Path.of(access + "%d/%d".formatted(tile.zoomLevel, tile.xIndex)));
            File file = new File(access + tile.getFileName());

            OutputStream o = new FileOutputStream(file);

            try (InputStream i = c.getInputStream()) {
                i.transferTo(o);
                map.put(tile, new Image(i));
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
             Preconditions.checkArgument(zoomLevel >= 0);
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
