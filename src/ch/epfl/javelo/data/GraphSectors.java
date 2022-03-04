package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public record GraphSectors (ByteBuffer buffer){


    private static final int OFFSET_NODE_ID = 0;
    private static final int OFFSET_NUMBER_OF_NODES = OFFSET_NODE_ID + 4;
    private static final int NODE_INT = OFFSET_NUMBER_OF_NODES + 2;



    // identite du premier noeud (U32) + nombre de noeuds (U16)
    // Chaque secteur int + short
    // 16 384 secteurs
    public List<Sector> sectorsInArea(PointCh center, double distance){
        double upper_left_x =   center.e() + 2_600_000 - 2*distance;
        double upper_left_y =   center.n() + 1_200_000 +  2*distance;
        double upper_right_x =  center.e() + 2_600_000+ 2*distance;
        double upper_right_y =  center.n() + 1_200_000 + 2*distance;
        double lower_right_x =  center.e() + 2_600_000 + 2*distance;
        double lower_right_y =  center.n() + 1_200_000 - 2*distance;
        double lower_left_x =  center.e() + 2_600_000 - 2*distance;
        double lower_left_y =  center.n() + 1_200_000 - 2*distance;

        int xMin = (int) Math.floor(upper_left_x / 2730);
        int yMin = (int) Math.floor(lower_left_y / 1730);
        int xMax = (int) Math.floor(upper_right_x / 2730);
        int yMax = (int) Math.floor(upper_left_y / 1730);

        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = xMin; i <= xMax; i++){
            for (int j = yMin; j <= yMax; j++){
                indexes.add(128 * j + i);
            }
        }
        ArrayList<Sector> sectorsInArea = new ArrayList<>();

        for (int j : indexes){
            sectorsInArea.add(new Sector(buffer.getInt(NODE_INT * j + OFFSET_NODE_ID), buffer.getShort(NODE_INT * j + OFFSET_NUMBER_OF_NODES)));
        }

        return sectorsInArea;

    }
    public record Sector(int startNodeId, int endNodeId){
    }
}
