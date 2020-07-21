package test.puzzle.pentomino;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.pentomino.Pentomino.*;

import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.pentomino.Piece;

class TestPentomino {

    Logger logger = Logger.getLogger(TestPentomino.class.getName());

    @Test
    void testPIECES() {
        int i = 0;
        for (Piece[] pieces : PIECES) {
            logger.info("*** " + PIECE_NAMES[i++] + " " + pieces.length);
                                  /* F  I  L  N  P  T  U  V  W  X  Y  Z */
        assertArrayEquals(new int[] {8, 2, 8, 8, 8, 4, 4, 4, 4, 1, 8, 4},
            Arrays.stream(PIECES).mapToInt(a -> a.length).toArray());
        }
    }

}
