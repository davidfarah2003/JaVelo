package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_EDGE_DIRECTION_AND_ID = 0;
    private static final int OFFSET_LENGTH = OFFSET_EDGE_DIRECTION_AND_ID + 4; //4
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH + 2; //6
    private static final int OFFSET_IDS_OSM = OFFSET_ELEVATION_GAIN + 2; //8
    private static final int EDGE_INTS = OFFSET_IDS_OSM + 2; // 10

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

        if(!hasProfile(edgeId)) {
            return new float[]{};
        }

        int lengthIndex = EDGE_INTS*edgeId + OFFSET_LENGTH;
        int nbSamples = 1 + Math2.ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort(lengthIndex)), Q28_4.ofInt(2));

        ArrayList<Float> profileSamples = new ArrayList<>();
        int idFirstSample = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        profileSamples.add(Q28_4.asFloat(elevations.get(idFirstSample)));

        int i = 1;
        int idCounter = 1;
        int bitCounter;

        int profileId = profileIds.get(edgeId);
        switch (Bits.extractUnsigned(profileId, 30, 2)){
            case 1:
                for (int j = 1; j < nbSamples; j++) {
                    profileSamples.add(Q28_4.asFloat(elevations.get(idFirstSample + j)));
                }
                break;
            case 2:
                while(i < nbSamples){
                    bitCounter = 8;
                    short elevationShort = elevations.get(idFirstSample+idCounter);
                    while(bitCounter > 0 && i < nbSamples){
                        profileSamples.add(profileSamples.get(i-1)+ Q28_4.asFloat(Bits.extractSigned(elevationShort, bitCounter,8)));
                        bitCounter -= 8;
                        i+= 1;
                    }
                    bitCounter = 8;
                    idCounter++;
                }
                break;
            case 3:
                bitCounter = 12;
                while(i < nbSamples){
                    short elevationShort = elevations.get(idFirstSample+idCounter);
                    while(bitCounter >= 0 && i < nbSamples){
                        profileSamples.add(profileSamples.get(i-1) + Q28_4.asFloat(Bits.extractSigned(elevationShort, bitCounter,4)));
                        bitCounter -= 4;
                        i+= 1;
                    }
                    bitCounter = 12;
                    idCounter++;
                }
                break;
        }

       if (isInverted(edgeId)){
           Collections.reverse(profileSamples);
       }

       return toArray(profileSamples);

    }

    private float[] toArray(ArrayList<Float> list){
        float[] toArray = new float[list.size()];
        for (int z = 0; z < list.size(); z++){
            toArray[z] = list.get(z);
        }
        return toArray;
    }

    /**
     * @param edgeId id of the edge
     * @return the identity of the attribute set attached to the edge with the given identity
     * Is this right?
     */
    public int attributesIndex(int edgeId){
        int attributeSetIndex = EDGE_INTS*edgeId + OFFSET_IDS_OSM;
        return Short.toUnsignedInt(edgesBuffer.getShort(attributeSetIndex));
    }
}
