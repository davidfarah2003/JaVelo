package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    //Repartition (in bytes) of edgesBuffer
    private static final int OFFSET_EDGE_DIRECTION_AND_ID = 0;
    private static final int OFFSET_LENGTH = OFFSET_EDGE_DIRECTION_AND_ID + 4;
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH + 2;
    private static final int OFFSET_IDS_OSM = OFFSET_ELEVATION_GAIN + 2;
    private static final int EDGE_INTS = OFFSET_IDS_OSM + 2;


    /**
     * @param edgeId id of the edge
     * @return true iff the edge with the given identity goes in the opposite direction to the OSM road it comes from
     */
    public boolean isInverted(int edgeId){
        int edgeIndex = EDGE_INTS*edgeId;
        return edgesBuffer.getInt(edgeIndex) < 0;
    }

    /**
     * @param edgeId id of the edge
     * @return the identity of the destination node of the given identity edge
     */
    public int targetNodeId(int edgeId){
        int edgeIndex = EDGE_INTS*edgeId;
        int edgeInt = edgesBuffer.getInt(edgeIndex);

        if (edgeInt >= 0){
            return Bits.extractUnsigned(edgesBuffer.getInt(edgeInt), 0, 31);
        }
        else{
            int targetNodeId = ~Bits.extractUnsigned(edgesBuffer.getInt(edgeInt), 0, 31);
        }
    }

    /**
     * @param edgeId id of the edge
     * @return the length, in meters, of the given identity edge
     */
    public double length(int edgeId){
        int lengthIndex = EDGE_INTS*edgeId + OFFSET_LENGTH;
        return Q28_4.asDouble(edgesBuffer.getShort(lengthIndex));
    }

    /**
     * @param edgeId id of the edge
     * @return the positive elevation, in meters, of the edge with the given identity
     */
    public double elevationGain(int edgeId){
        int elevationIndex = EDGE_INTS*edgeId + OFFSET_ELEVATION_GAIN;
        //return Q28_4.asDouble(abs(edgesBuffer.getShort(lengthIndex)));
        return 0;
    }

    /**
     * @param edgeId id of the edge
     * @return true iff the given identity edge has a profile
     */
    public boolean hasProfile(int edgeId){
        return false;
    }

    /**
     * @param edgeId id of the edge
     * @return the array of samples of the profile of the edge with the given identity,
     * which is empty if the edge does not have a profile
     */
    public float[] profileSamples(int edgeId){
        return new float[] {};
    }

    /**
     * @param edgeId id of the edge
     * @return the identity of the attribute set attached to the edge with the given identity
     */
    public int attributesIndex(int edgeId){
        return 0;
    }
}
