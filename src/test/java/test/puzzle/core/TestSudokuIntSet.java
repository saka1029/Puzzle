package test.puzzle.core;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testLastOneBit() {
        int set = 0b000_010_101_0;  // {1, 3, 5}
        assertEquals(0b000_000_001_0, Integer.lowestOneBit(set));   // last one == 1
        assertEquals(0b000_000_001_0, -set & set);                  // last one == 1
        set ^= 1 << 1;  // remove 1
        set ^= 1 << 3;  // remove 3
        assertEquals(0b000_010_000_0, Integer.lowestOneBit(set));   // last one == 5;
        assertEquals(0b000_010_000_0, -set & set);                  // last one == 5;
    }
}
