package test.puzzle.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestNumberLink {

    /**
     * <pre>
     * dir
     * 0 T
     * 1 R
     * 2 B
     * 3 L
     * </pre>
     * 
     * <pre>
     * link  T R B L
     * 0 └   o o x x
     * 1 │   o x o x
     * 2 ┘   o x x o
     * 3 ┌   x o o x
     * 4 ─   x o x o
     * 5 ┐   x x o o
     * </pre>
     */

    static final int[][] DIR_DIR = {
        {-1, 0, 1, 2},
        {0, -1, 3, 4},
        {1, 3, -1, 5},
        {2, 4, 5, -1},
    };
    
    static final int[][] LINK_DIR = {
        {0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {3, 4}};

    static final boolean[][] LINK_DIR_BOOLEAN = {
        {true, true, false, false},
        {true, false, true, false},
        {true, false, false, true},
        {false, true, true, false},
        {false, true, false, true},
        {false, false, true, true},
    };

    static int link(int dir1, int dir2) {
        int link = DIR_DIR[dir1][dir2];
        if (link < 0)
            throw new IllegalArgumentException("dir1, dir2");
        return link;
    }
    
    static int[] dirs(int link) {
        return LINK_DIR[link];
    }

    static boolean dir(int link, int dir) {
        return LINK_DIR_BOOLEAN[link][dir];
    }

    static boolean top(int link) {
        return switch (link) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    static boolean right(int link) {
        return switch (link) {
            case 0, 3, 4 -> true;
            default -> false;
        };
    }

    static boolean bottom(int link) {
        return switch (link) {
            case 1, 3, 5 -> true;
            default -> false;
        };
    }

    static boolean left(int link) {
        return switch (link) {
            case 2, 4, 5 -> true;
            default -> false;
        };
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
