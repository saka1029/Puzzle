package test.puzzle.pentomino;

import static org.junit.Assert.*;

import org.junit.Test;

import puzzle.pentomino.Point;

public class TestPoint {

    @Test
    public void testAdd() {
        assertEquals(Point.of(3, 4), Point.of(1, 1).add(Point.of(2, 3)));
    }

    @Test
    public void testSubtract() {
        assertEquals(Point.of(3, 4), Point.of(4, 5).subtract(Point.of(1, 1)));
    }

    @Test
    public void testMap() {
        assertArrayEquals(Point.array(1, 2, 3, 4), Point.map(Point.array(0, 1, 2, 3), p -> p.add(Point.of(1, 1))));
    }

    @Test
    public void testReduce() {
        assertEquals(Point.of(2, 4), Point.reduce(Point.array(0, 1, 2, 3), (a, b) -> a.add(b)));
    }

    @Test
    public void testArray() {
        assertArrayEquals(new Point[] { Point.of(0, 0), Point.of(1, 1)}, Point.array(0, 0, 1, 1));
    }

    @Test
    public void testEquals() {
        assertEquals(Point.of(4, 5), Point.of(4, 5));
    }

    @Test
    public void testHashCode() {
        assertEquals(Point.of(4, 5).hashCode(), Point.of(4, 5).hashCode());
    }

    @Test
    public void testCompareTo() {
        assertTrue(Point.of(4, 5).compareTo(Point.of(4,  5)) == 0);
        assertTrue(Point.of(3, 5).compareTo(Point.of(4,  5)) < 0);
        assertTrue(Point.of(4, 3).compareTo(Point.of(4,  5)) < 0);
        assertTrue(Point.of(5, 5).compareTo(Point.of(4,  5)) > 0);
        assertTrue(Point.of(4, 6).compareTo(Point.of(4,  5)) > 0);
    }

    @Test
    public void testToString() {
        assertEquals("4@5", Point.of(4, 5).toString());
    }


}
