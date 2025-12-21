package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestSudokuIntSet {

    @Test
    public void testIntToBit() {
        assertEquals(0b000_000_100_0, 1 << 3);
        assertEquals(0b100_000_000_0, 1 << 9);
    }

    @Test
    public void testBitToInt() {
        assertEquals(3, Integer.numberOfTrailingZeros(0b000_000_100_0));
        assertEquals(9, Integer.numberOfTrailingZeros(0b100_000_000_0));
    }

    @Test
    public void testAdd() {
        int set = 0;
        set |= 1 << 1;  // add 1
        set |= 1 << 3;  // add 3
        set |= 1 << 5;  // add 5
        assertEquals(0b000_010_101_0, set);
    }

    @Test
    public void testRemove() {
        int set = 0b000_010_101_0;  // {1, 3, 5}
        set ^= 1 << 3;  // remove 3
        assertEquals(0b000_010_001_0, set);
    }

    /**
     * 集合に含まれる一番小さい要素を取り出す。
     */
    @Test
    public void testLowestOneBit() {
        int set = 0b000_010_101_0;  // {1, 3, 5}
        assertEquals(0b000_000_001_0, Integer.lowestOneBit(set));   // lowest one == 1
        assertEquals(0b000_000_001_0, -set & set);                  // lowest one == 1
        set ^= 1 << 1;  // remove 1
        set ^= 1 << 3;  // remove 3
        assertEquals(0b000_010_000_0, Integer.lowestOneBit(set));   // lowest one == 5;
        assertEquals(0b000_010_000_0, -set & set);                  // lowest one == 5;
    }

    /**
     * 集合に含まれる要素を小さいものから順に取り出す。
     */
    @Test
    public void testIterateIntSet() {
        int set = 0b100_100_100_0;  // {9, 6, 3}
        List<Integer> elements = new ArrayList<>();
        for (int s = set, b = 0; s != 0; s ^= b) {
            b = -s & s;     // get lowest one bit
            elements.add(Integer.numberOfTrailingZeros(b));
        }
        assertEquals(List.of(3, 6, 9), elements);
    }
}
