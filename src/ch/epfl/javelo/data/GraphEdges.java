package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_EDGE_DIRECTION_AND_ID = 0;
    private static final int OFFSET_LENGTH = OFFSET_EDGE_DIRECTION_AND_ID + 4;
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH + 2;
    private static final int OFFSET_IDS_OSM = OFFSET_ELEVATION_GAIN + 2;
    private static final int EDGE_INTS = OFFSET_IDS_OSM + 2;

    //edgesBuffer contains in order:    an integer of type int (direction of the edge and identity of the destination node),
    //                                  an integer of type short (length of the edge),
    //                                  an integer of type short (total positive elevation) and
    //                                  an integer of type short (identity of the set of OSM attributes)

    //profileIds contains, for each edge of the graph, a single integer of type int (type of the profile and index of the first sample)

    //elevations : the buffer memory containing all the samples of the profiles, compressed or not


    /**
     * @param edgeId id of the edge
     * @return true iff the edge with the given identity goes in the opposite direction to the OSM road it comes from
     */
    public boolean isInverted(int edgeId){
        int edgeIndex = EDGE_INTS*edgeId + OFFSET_EDGE_DIRECTION_AND_ID;
        return edgesBuffer.getInt(edgeIndex) < 0;
    }

    /**
     * @param edgeId id of the edge
     * @return the identity of the destination node of the given identity edge
     */
    public int targetNodeId(int edgeId){
        int edgeIndex = EDGE_INTS*edgeId + OFFSET_EDGE_DIRECTION_AND_ID;
        int edgeInt = edgesBuffer.getInt(edgeIndex);

        return (edgeInt >= 0) ? edgeInt: ~edgeInt;
    }

    /**
     * @param edgeId id of the edge
     * @return the length, in meters, of the given identity edge
     */
    public double length(int edgeId){
        int lengthIndex = EDGE_INTS*edgeId + OFFSET_LENGTH;
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(lengthIndex)));
    }

    /**
     * @param edgeId id of the edge
     * @return the positive elevation, in meters, of the edge with the given identity
     */
    public double elevationGain(int edgeId){
        int elevationIndex = EDGE_INTS*edgeId + OFFSET_ELEVATION_GAIN;
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(elevationIndex)));
    }

    /**
     * @param edgeId id of the edge
     * @return true iff the given identity edge has a profile
     */
    public boolean hasProfile(int edgeId){
        int profileId = profileIds.get(edgeId);
        return Bits.extractUnsigned(profileId, 30, 2) != 0;
    }

    /**
     * @param edgeId id of the edge
     * @return the array of samples of the profile of the edge with the given identity,
     * which is empty if the edge does not have a profile
     * ask about this method. not very clear
     */
/*    public float[] profileSamples(int edgeId){
        if(!hasProfile(edgeId))
            return new float[] {};

        int profileId = profileIds.get(edgeId);
        int lengthIndex = EDGE_INTS*edgeId + OFFSET_LENGTH;
        edgesBuffer.getShort(lengthIndex);
        int nbSamples = 1 + Math2.ceilDiv(Q28_4.ofInt(edgesBuffer.getShort(lengthIndex)), Q28_4.ofInt(2));//(int)(1 + Math.ceil(length(edgeId)/2));
        int idFirstSample = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        double distanceSamples = length(edgeId)/(nbSamples - 1);

        float[] profileSamples = new float[nbSamples];

        switch (Bits.extractUnsigned(profileId, 30, 2)){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
}
        return new float[] {};
    }*/

    public float[] profileSamples(int edgeId){
        //return an empty array if the edge does not have a profile
        //determine the number of samples of the profile of the edge according to its length
        //For each case of compression create a float array containing the elevation values (samples), in Q28_4 representation (use Q28_4.asFloat)
        //return the array of samples

        if(!hasProfile(edgeId))
            return new float[] {};
        int lengthIndex = EDGE_INTS*edgeId + OFFSET_LENGTH;

        int profileId = profileIds.get(edgeId);
        int nbSamples = 1 + Math2.ceilDiv(Q28_4.ofInt(Short.toUnsignedInt(edgesBuffer.getShort(lengthIndex))), Q28_4.ofInt(2));
        int idFirstSample = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);

        float[] profileSamples = new float[nbSamples];
        profileSamples[0] = Q28_4.asFloat(elevations.get(idFirstSample));


        int idCounter = 1;
        switch (Bits.extractUnsigned(profileId, 30, 2)){
            case 1:
                for (int i = 0; i < nbSamples; i++) {
                    profileSamples[i] = Q28_4.asFloat(elevations.get(idFirstSample + i));
                }
                break;
            case 2:
                //reread consignes for this
                while(i <= nbSamples){
                    short elevationShort = elevations.get(idFirstSample+idCounter);
                    profileSamples.add(Q28_4.asFloat(Bits.extractSigned(elevationShort, 0,8)));
                    profileSamples.add(Q28_4.asFloat(Bits.extractSigned(elevationShort, 8,8)));
                    i+= 2;
                    idCounter++;
                }
                break;
            case 3:
                while(i <= nbSamples){
                    short elevationShort = elevations.get(idFirstSample+idCounter);
                    float elevation1 = Q28_4.asFloat(Bits.extractSigned(elevationShort, 0,4));
                    float elevation2 = Q28_4.asFloat(Bits.extractSigned(elevationShort, 4,4));
                    float elevation3 = Q28_4.asFloat(Bits.extractSigned(elevationShort, 8,4));
                    float elevation4 = Q28_4.asFloat(Bits.extractSigned(elevationShort, 12,4));
                    i+= 2;
                    idCounter++;
                }
                break;
        }
        return new float[] {};
    }

    /**
     * @param edgeId id of the edge
     * @return the identity of the attribute set attached to the edge with the given identity
     * Is this right?
     */
    public int attributesIndex(int edgeId){
        int attributeSetIndex = EDGE_INTS*edgeId + OFFSET_IDS_OSM;
        return edgesBuffer.getShort(attributeSetIndex);
    }
}
