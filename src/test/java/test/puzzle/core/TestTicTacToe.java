package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static puzzle.core.TicTacToe.*;

import org.junit.Test;

public class TestTicTacToe {
    
    @Test
    public void testBoard() {
        assertEquals(0, board(0, 0, 0, 0, 0, 0, 0, 0, 0));
        assertEquals(13, board(0, 0, 0, 0, 0, 0, 1, 1, 1));
        assertEquals(MAX_BOARD - 1, board(2, 2, 2, 2, 2, 2, 2, 2, 2));
    }
    
    @Test
    public void testArray() {
        assertArrayEquals(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0}, array(0));
        assertArrayEquals(new int[] {0, 0, 0, 0, 0, 0, 1, 1, 1}, array(13));
        assertArrayEquals(new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2}, array(MAX_BOARD - 1));
    }
    
    @Test
    public void testGet() {
        assertEquals(2, get(MAX_BOARD - 1, 0));
        assertEquals(2, get(MAX_BOARD - 1, 8));
        assertEquals(0, get(board(2, 1, 0, 2, 1, 0, 2, 1, 0), 0));
        assertEquals(0, get(board(2, 1, 0, 2, 1, 0, 2, 1, 0), 3));
        assertEquals(1, get(board(2, 1, 0, 2, 1, 0, 2, 1, 0), 4));
        assertEquals(2, get(board(2, 1, 0, 2, 1, 0, 2, 1, 0), 5));
    }

}
