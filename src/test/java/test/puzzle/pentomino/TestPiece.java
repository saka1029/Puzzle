package test.puzzle.pentomino;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.pentomino.Piece;

public class TestPiece {

    static final Logger logger = Logger.getLogger(TestPiece.class.getName());

    @Test
    public void testNormalize() {
        int[][] points = {{7, 6}, {6, 6}, {6, 7}, {5, 5}, {5, 6}};
        Piece piece = new Piece(points);
        logger.info(piece.toString());
    }

    @Test
    public void testRotateRight() {
        int[][] points = {{7, 6}, {6, 6}, {6, 7}, {5, 5}, {5, 6}};
        Piece piece = new Piece(points);
        Piece rotated = piece;
        for (int i = 0; i < 4; ++i, rotated = rotated.rotateRight())
            logger.info(rotated.toString());
        assertEquals(piece, rotated);
    }

    @Test
    public void testTransform() {
        int[][] points = new int[][] {{7, 6}, {6, 6}, {6, 7}, {5, 5}, {5, 6}};
        Piece piece = new Piece(points);
        logger.info("origin");
        logger.info(piece.toString());
        Piece transformed = piece.transform();
        logger.info("transformed");
        logger.info(transformed.toString());
        Piece transformed2 = transformed.transform();
        logger.info("transformed x 2");
        logger.info(transformed2.toString());
        assertEquals(piece, transformed2);
    }

    @Test
    public void testDirections() {
        Piece[] allF = Piece.allOf(new int[][] {{7, 6}, {6, 6}, {6, 7}, {5, 5}, {5, 6}});
        assertEquals(8, allF.length);
        Piece[] allI = Piece.allOf(new int[][] {{7, 6}, {7, 7}, {7, 8}, {7, 9}, {7, 10}});
        assertEquals(2, allI.length);
        Piece[] allV = Piece.allOf(new int[][] {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}});
        assertEquals(4, allV.length);
    }

    static void permutation(int[] array, Consumer<int[]> found) {
        int size = array.length;
        int[] perm = new int[size];
        boolean[] used = new boolean[size];
        new Object() {
            void search(int index) {
                if (index >= size)
                    found.accept(perm);
                else
                    for (int i = 0; i < size; ++i) {
                        if (used[i]) continue;
                        perm[index] = array[i];
                        used[i] = true;
                        search(index + 1);
                        used[i] = false;
                    }
            }
        }.search(0);
    }

    @Test
    public void testPermutation() {
        List<int[]> all = new ArrayList<>();
        int[] array = {1, 2, 3};
        permutation(array, a -> all.add(a.clone()));
        logger.info(all.toString());
        assertArrayEquals(new int[][] {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}},
            all.toArray(int[][]::new));
    }
}
