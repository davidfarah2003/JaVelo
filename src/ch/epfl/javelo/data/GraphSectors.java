package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This record represents models the 16_384 sectors wh
 */
public record GraphSectors (ByteBuffer buffer){


    // Constants which enable to access the information stored in the buffer about a sector
    private static final int OFFSET_NODE_ID = 0;
    private static final int OFFSET_NUMBER_OF_NODES = OFFSET_NODE_ID + Integer.BYTES; // 4
    private static final int NODE_INT = OFFSET_NUMBER_OF_NODES + Short.BYTES; // 6

    // identite du premier noeud (U32) + nombre de noeuds (U16)
    // Chaque secteur int + short
    // 16 384 secteurs

    /**
     *
     * @param center point of interest (PointCh, east-north coordinates)
     * @param distance (meters) from which a sector is considered in the area of the point of interest
     * @return a list of sectors in area of the point entered as in input
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){

        // getting the coordinates of the summits of the square which defines the range
        double upper_left_x =   center.e() - SwissBounds.MIN_E - 2*distance; //
        double upper_left_y =   center.n() - SwissBounds.MIN_N +  2*distance;
        double lower_right_x =  center.e() - SwissBounds.MIN_E + 2*distance;
        double lower_right_y =  center.n() - SwissBounds.MIN_N - 2*distance;

        if (upper_left_x < 0){
            upper_left_x = 0;
        }
        if (lower_right_y < 0){
            lower_right_y = 0;
        }

        if (upper_left_y > SwissBounds.HEIGHT){
            upper_left_y = SwissBounds.HEIGHT;
        }

        if (lower_right_x > SwissBounds.WIDTH){
            lower_right_x = SwissBounds.WIDTH;
        }


        // defining some indexes along the horizontal and vertical axes which will be
        // needed to compute the actual index of every sector
        int xMin = (int) Math.floor(upper_left_x / 2730);
        int yMin = (int) Math.floor(lower_right_y / 1730);
        int xMax = (int) Math.floor(lower_right_x / 2730);
        int yMax = (int) Math.floor(upper_left_y / 1730);

        ArrayList<Integer> indexes = new ArrayList<>();

        // adding the indexes of all the sectors
        // which intersect with square centered at the input (PointCh)
        for (int i = xMin; i <= xMax; i++){
            for (int j = yMin; j <= yMax; j++){
                indexes.add(128 * j + i);
            }
        }

        ArrayList<Sector> sectorsInArea = new ArrayList<>();
        // iterating through all the indexes of the sector in the area
        for (int j : indexes){
            int startNodeId = buffer.getInt(NODE_INT * j + OFFSET_NODE_ID);
            // making sure to interpret the short value stored in the buffer as an unsigned int (only positive values)
            int numberOfNodes =  Short.toUnsignedInt(buffer.getShort(NODE_INT * j + OFFSET_NUMBER_OF_NODES));
            int endNodeId = startNodeId + numberOfNodes;
            sectorsInArea.add(new Sector(startNodeId, endNodeId));
        }
          return sectorsInArea;
    }

    /**
     * This nested record represents a sector
     */
    public record Sector(int startNodeId, int endNodeId){}
}
