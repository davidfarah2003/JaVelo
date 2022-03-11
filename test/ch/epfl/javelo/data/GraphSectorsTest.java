package ch.epfl.javelo.data;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSectorsTest {

    @Test
    void testSecteur() {
        RandomGenerator rng = newRandom();
        int[] noeuds = new int[16384];
        ByteBuffer buffer = ByteBuffer.allocate(16384 * 6);
        for (int i = 0; i < 16384; i++) {
            int nbNoeuds = 0;
            for (int j = i - 1; j >= 0; j--) {
                nbNoeuds += noeuds[j];
            }
            if (i == 0) {
                buffer.putInt(i * 6, 0);
            } else {
                buffer.putInt(i * 6, nbNoeuds);
            }
            int nb = rng.nextInt(1, 100);
            noeuds[i] = nb;
            buffer.putShort(i * 6 + 4, (short) nb);
        }
        GraphSectors s = new GraphSectors(buffer);
        List<GraphSectors.Sector> liste = new ArrayList<>();
        for (int i = 0; i < 16384; i++) {
            int n = 0;
            for (int j = 0; j < i; j++) {
                n += noeuds[j];
            }
            liste.add(new GraphSectors.Sector(n, n + noeuds[i]));
        }
        double distanceE = SwissBounds.MAX_E - SwissBounds.MIN_E;
        double distanceN = SwissBounds.MAX_N - SwissBounds.MIN_N;
        assertEquals(liste, s.sectorsInArea(new PointCh(SwissBounds.MIN_E + distanceE/2, SwissBounds.MIN_N + distanceN/2),
                distanceE/2));
    }

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
        double distance = 400;
        GraphSectors sector = new GraphSectors(buffer);
        List<GraphSectors.Sector> list =  sector.sectorsInArea(point, distance);
        System.out.println(list.get(0).startNodeId());
        System.out.println(list.get(0).endNodeId());
    }
}
