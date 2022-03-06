package ch.epfl.javelo.data;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GraphSectorsTest {
    @Test
    void sectorsInAreaWorks(){
        ByteBuffer buffer = ByteBuffer.allocate(16_384 * 6);
        // 0
        buffer.putInt(0, 0b00000000001000000110000111111000);
        buffer.putShort(4, (short) 0b0110100111101000);

        //1
        buffer.putInt(6, 0b00000100000000000110000011111000);
        buffer.putShort(10, (short) 0b0110000101101000);

        //2
        buffer.putInt(12, 0b00001100000000000010000011010011);
        buffer.putShort(16, (short) 0b010010101101011);

        //127
        buffer.putInt(768, 0b00001100001000000110000110000001);
        buffer.putShort(772, (short) 0b001110100101000);

        //128
        buffer.putInt(774, 0b00000111111000000110000110011001);
        buffer.putShort(778, (short) 0b000000100101111);

        //129
        buffer.putInt(780, 0b00000001111000000100000110000001);
        buffer.putShort(784, (short) 0b000011100101110);

        PointCh point = new PointCh(2_485_000 + 3000,  1_075_000 + 2000);
        double distance = 200;
        GraphSectors sector = new GraphSectors(buffer);
        List<GraphSectors.Sector> list =  sector.sectorsInArea(point, distance);
        System.out.println(list.get(1).startNodeId());
        System.out.println(list.get(1).endNodeId());

    }
}
