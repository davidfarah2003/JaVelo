package ch.epfl.javelo.data;
import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

public record GraphNodes(IntBuffer buffer) {
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;  //number of integers needed to represent a node, i.e. 3


    /**
     * @return total number of nodes
     */
    public int count() {
        return buffer.capacity()/NODE_INTS;
    }

    /**
     * @param nodeId id of the node
     * @return East coordinate E of the node with id nodeId (CH)
     */
    public double nodeE(int nodeId){
        int nodeIndex = nodeId*NODE_INTS + OFFSET_E;
        return Q28_4.asDouble(buffer.get(nodeIndex));
    }

    /**
     * @param nodeId id of the node
     * @return North coordinate N of the node with id nodeId (CH)
     */
    public double nodeN(int nodeId){
        int nodeIndex = nodeId*NODE_INTS;
        return Q28_4.asDouble(buffer.get(nodeIndex+OFFSET_N));
    }

    /**
     * @param nodeId id of the node
     * @return the number of edges exiting the node with the given identity
     */
    public int outDegree(int nodeId){
        int nodeIndex = nodeId*NODE_INTS;
        return Bits.extractUnsigned(buffer.get(nodeIndex+OFFSET_OUT_EDGES), 28, 4);
    }

    /**
     * @param nodeId id of the node
     * @param edgeIndex index of the edge for the given node
     * @return returns the identity of the edgeIndex-th edge outgoing from the node with identity nodeId
     */
    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int nodeIndex = nodeId*NODE_INTS;
        return Bits.extractUnsigned(buffer.get(nodeIndex+OFFSET_OUT_EDGES), 0, 28) + edgeIndex;
    }
}
