package test.puzzle;

import java.util.Arrays;

import org.junit.Test;

import puzzle.LexicographicalOrder.IntsIterator;

public class TestLexicographicalOrder {
    
    @Test
    public void testIntsIterator() {
        for (int[] a : (Iterable<int[]>)() -> new IntsIterator(6, 3))
            System.out.println(Arrays.toString(a));
    }

}
