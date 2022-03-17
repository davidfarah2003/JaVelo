package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
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
        //Preconditions.checkArgument(distance >= 0);

        double sectorLength = 1730;
        double sectorWidth = 2730;


        // getting the coordinates of the summits of the square which defines the range
        double upper_left_x =   center.e() - SwissBounds.MIN_E - distance;
        double upper_left_y =   center.n() - SwissBounds.MIN_N + distance;
        double lower_right_x =  center.e() - SwissBounds.MIN_E + distance;
        double lower_right_y =  center.n() - SwissBounds.MIN_N - distance;


        // if the bounds are exceeded, taking into the account the extremes of the Swiss Coordinates
        /*
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
        /
         */


        // defining some indexes along the horizontal and vertical axes which will be
        // needed to compute the actual index of every sector
        int xMin = (int) Math.floor(upper_left_x / sectorWidth);
        int yMin = (int) Math.floor(lower_right_y / sectorLength);
        int xMax = (int) Math.floor(lower_right_x / sectorWidth);
        int yMax = (int) Math.floor(upper_left_y / sectorLength);


        ArrayList<Sector> sectorsInArea = new ArrayList<>();

        // adding the indexes of all the sectors
        // which intersect with square centered at the input (PointCh)
        for (int x = xMin; x <= xMax; x++){
            for (int y = yMin; y <= yMax; y++){
                int sectorIndex = 128 * y + x;
                int startNodeId = buffer.getInt(NODE_INT * sectorIndex + OFFSET_NODE_ID);
                int numberOfNodes =  Short.toUnsignedInt(buffer.getShort(NODE_INT * sectorIndex + OFFSET_NUMBER_OF_NODES));
                int endNodeId = startNodeId + numberOfNodes;
                sectorsInArea.add(new Sector(startNodeId, endNodeId));
            }
        }
        return sectorsInArea;
    }

    /**
     * This nested record represents a sector
     */
    public record Sector(int startNodeId, int endNodeId){}
}
