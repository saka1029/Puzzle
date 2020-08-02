package test.puzzle.pentomino;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static puzzle.pentomino.Pentomino.PIECES;
import static puzzle.pentomino.Pentomino.PIECE_NAMES;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.pentomino.Piece;

class TestPentomino {

    Logger logger = Logger.getLogger(TestPentomino.class.getName());

    @Test
    void testPIECES() {
        int i = 0;
        for (Piece[] pieces : PIECES)
            logger.info("*** " + PIECE_NAMES[i++] + " " + pieces.length);
                       /* F  I  L  N  P  T  U  V  W  X  Y  Z */
        int[] expected = {8, 2, 8, 8, 8, 4, 4, 4, 4, 1, 8, 4};
        assertEquals(63, IntStream.of(expected).sum());
        assertArrayEquals(expected,
            Arrays.stream(PIECES).mapToInt(a -> a.length).toArray());
    }

}
