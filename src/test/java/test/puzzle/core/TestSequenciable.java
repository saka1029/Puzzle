package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import static puzzle.sequence.Sequenciable.map;
import static puzzle.sequence.Sequenciable.seq;

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

}
