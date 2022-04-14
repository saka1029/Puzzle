package test.puzzle.pentomino;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import puzzle.pentomino.Mino;
import puzzle.pentomino.Point;

public class TestMino {

    @Test
    public void testNormalize() {
        Mino f = Mino.of(0, 1, 0, 2, 1, 1, 1, 0, 2, 1);
        assertArrayEquals(
            Point.array(0, 0, 0, 1, 1, -1, 1, 0, 2, 0),
            f.points());
    }

    @Test
    public void testAllDirections() {
        Mino f = Mino.of(0, 1, 0, 2, 1, 1, 1, 0, 2, 1);
        assertEquals(8, f.allDirections().size());
        Mino x = Mino.of(0, 0, -1, 0, 1, 0, 0, -1, 0, 1);
        assertEquals(1, x.allDirections().size());
    }

    @Test
    public void testAllMinos() {
        Set<Mino> allMinos = Mino.allMinos(5);
        assertEquals(63, allMinos.size());
    }

    @Test
    public void testAllMinosSet() {
        List<Set<Mino>> allMinosSet = Mino.allMinosSet(5);
        assertEquals(12, allMinosSet.size());
        assertEquals(63, allMinosSet.stream().mapToInt(set -> set.size()).sum());
        assertEquals(true, allMinosSet.stream().allMatch(set -> set.size() >= 1 && set.size() <= 8));
    }

}
