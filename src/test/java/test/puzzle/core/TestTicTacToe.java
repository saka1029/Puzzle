package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static puzzle.core.TicTacToe.*;

import java.util.Arrays;

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
    public void testString() {
        assertEquals(" xo%n xo%n xo%n".formatted(), string(board(2, 1, 0, 2, 1, 0, 2, 1, 0)));
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
    
    @Test
    public void testSet() {
        assertEquals(board(2, 1, 0, 2, 1, 0, 2, 1, 2), set(board(2, 1, 0, 2, 1, 0, 2, 1, 0), 0, 2));
        assertEquals(board(0, 1, 0, 2, 1, 0, 2, 1, 0), set(board(2, 1, 0, 2, 1, 0, 2, 1, 0), 8, 0));
        assertEquals(board(2, 1, 0, 2, 2, 0, 2, 1, 0), set(board(2, 1, 0, 2, 1, 0, 2, 1, 0), 4, 2));
    }
    
    @Test
    public void testNext() {
        assertEquals(0, next(board(0, 0, 0, 0, 0, 0, 0, 0, 0)));
        assertEquals(1, next(board(0, 0, 0, 0, 0, 0, 0, 0, 2)));
        assertEquals(2, next(board(1, 0, 0, 0, 0, 0, 0, 0, 0)));
        assertEquals(0, next(board(1, 0, 0, 0, 0, 0, 0, 0, 2)));
        assertEquals(-1, next(board(1, 0, 0, 1, 1, 0, 0, 0, 2)));
        assertEquals(-1, next(board(1, 2, 0, 2, 0, 0, 0, 0, 2)));
    }
    
    @Test
    public void testWinner() {
        assertEquals(1, winner(board(1, 1, 1, 1, 1, 1, 1, 1, 1)));
        assertEquals(0, winner(board(0, 0, 0, 0, 0, 0, 0, 0, 0)));
        assertEquals(0, winner(board(0, 0, 0, 0, 0, 0, 0, 0, 2)));
        assertEquals(0, winner(board(1, 0, 0, 0, 0, 0, 0, 0, 0)));
        assertEquals(0, winner(board(1, 0, 0, 0, 0, 0, 0, 0, 2)));
        assertEquals(0, winner(board(1, 0, 0, 1, 1, 0, 0, 0, 2)));
        assertEquals(0, winner(board(1, 2, 0, 2, 0, 0, 0, 0, 2)));
        assertEquals(1, winner(board(0, 0, 0, 0, 0, 0, 1, 1, 1)));
        assertEquals(1, winner(board(0, 0, 0, 1, 1, 1, 0, 0, 0)));
        assertEquals(1, winner(board(1, 1, 1, 0, 0, 0, 0, 0, 0)));
        assertEquals(1, winner(board(0, 0, 1, 0, 0, 1, 0, 0, 1)));
        assertEquals(1, winner(board(0, 1, 0, 0, 1, 0, 0, 1, 0)));
        assertEquals(1, winner(board(1, 0, 0, 1, 0, 0, 1, 0, 0)));
        assertEquals(1, winner(board(1, 0, 0, 0, 1, 0, 0, 0, 1)));
        assertEquals(1, winner(board(0, 0, 1, 0, 1, 0, 1, 0, 0)));
        assertEquals(2, winner(board(0, 0, 0, 0, 0, 0, 2, 2, 2)));
        assertEquals(2, winner(board(0, 0, 0, 2, 2, 2, 0, 0, 0)));
        assertEquals(2, winner(board(2, 2, 2, 0, 0, 0, 0, 0, 0)));
        assertEquals(2, winner(board(0, 0, 2, 0, 0, 2, 0, 0, 2)));
        assertEquals(2, winner(board(0, 2, 0, 0, 2, 0, 0, 2, 0)));
        assertEquals(2, winner(board(2, 0, 0, 2, 0, 0, 2, 0, 0)));
        assertEquals(2, winner(board(2, 0, 0, 0, 2, 0, 0, 0, 2)));
        assertEquals(2, winner(board(0, 0, 2, 0, 2, 0, 2, 0, 0)));
        assertEquals(-1, winner(board(1, 1, 1, 0, 0, 0, 2, 2, 2)));
    }
    
    @Test
    public void testALL() {
        short[][] b = ALL[6561];
        System.out.println(Arrays.deepToString(b));
        int count = 0;
        for (short[][] e : ALL)
            if (e != null) {
                if (e[0] != null)
                    ++count;
                if (e[1] != null)
                    ++count;
            }
        System.out.println("count=" + count);
    }
}