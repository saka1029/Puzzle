package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import static puzzle.sequence.Sequenciable.*;

import java.util.List;

import org.junit.Test;

public class TestSequenciable {

    @Test
    public void testSequence() {
        assertEquals(seq(1, 2, 3), seq(1, 2, 3));
        assertEquals(seq(List.of(1, 2, 3)), seq(1, 2, 3));
    }

    @Test
    public void testMap() {
        assertEquals(seq(2, 4, 6), map(i -> i * 2, seq(1, 2, 3)));
    }

    @Test
    public void testFilter() {
        assertEquals(seq(2, 4, 6), filter(i -> i % 2 == 0, seq(1, 2, 3, 4, 5, 6)));
    }
    
    @Test
    public void test10000() {
        int count = 0;
        for (int i = 1; i <= 10000; ++i)
            if (i % ((int)Math.sqrt(i)) == 0) {
                System.out.print(" " + i);
                ++count;
            }
        System.out.println("count=" + count);
    }
}
