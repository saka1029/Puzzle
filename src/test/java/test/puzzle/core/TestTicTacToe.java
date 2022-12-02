package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestTicTacToe {
    
    static final int[] RANKS = {1, 3, 9, 27, 81, 243, 729, 2187, 6561};
    static final char[] CHARS = {' ', 'x', 'o'};
    
    static int index(int row, int col) {
        return row * 3 + col;
    }

    static int get(int board, int row, int col) {
        int index = index(row, col);
        return board / RANKS[index] % 3;
    }

    static int set(int board, int row, int col, int value) {
        int index = index(row, col);
        int rank = RANKS[index], rank3 = rank * 3;
        return board / rank3 * rank3 + value * rank + board % rank;
    }
    
    static String toString(int board) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 3; ++c)
                sb.append(CHARS[get(board, r, c)]);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    static int board(String s) {
        return Integer.parseInt(s, 3);
    }

    @Test
    public void testGet() {
        assertEquals(1, get(1, 0, 0));
        assertEquals(1, get(3, 0, 1));
        assertEquals(1, get(9, 0, 2));
        assertEquals(2, get(board("000000200"), 0, 2));
        assertEquals(1, get(board("001000000"), 2, 0));
        assertEquals(2, get(board("201000000"), 2, 2));
    }
    
    @Test
    public void testSet() {
        assertEquals(board("200000000"), set(0, 2, 2, 2));
        assertEquals(board("222212222"), set(board("222222222"), 1, 1, 1));
    }

    @Test
    public void testToString() {
        assertEquals("xxx%n   %n   %n".formatted(), toString(board("000000111")));
        assertEquals("o  %n o %n  o%n".formatted(), toString(board("200020002")));
    }

}
