package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import puzzle.core.Nonogram;

public class TestNonogram {

    static void printTestCaseName() {
        System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Test
    public void testSimple() {
        printTestCaseName();
        int[][] rows = {{1}, {3}, {1}};
        int[][] cols = {{1}, {3}, {1}};
        Nonogram.solve(rows, cols);
    }

    @Test
    public void test10x10() {
        printTestCaseName();
        int[][] rows = {{5}, {5}, {3}, {3, 2}, {4, 1}, {1, 3, 1}, {1, 1, 3, 1}, {5}, {2, 4}, {1, 4}};
        int[][] cols = {{2, 2}, {4, 1}, {2}, {1, 1}, {1}, {2, 4}, {2, 5}, {3, 4}, {4, 3}, {4, 5}};
        Nonogram.solve(rows, cols);
    }

    @Ignore
    @Test
    public void test25x25() {
        printTestCaseName();
        int[][] rows = {
            {1},
            {1,1},
            {1,1,1},
            {5},
            {3},
            {3,3},
            {5},
            {9},
            {5,6,3},
            {8,7,5},
            {16,2,1},
            {17,2,1,2},
            {8,8,2,2},
            {8,8,6},
            {8,7,7},
            {8,7,4},
            {8,10},
            {8,2,6},
            {8,3,2},
            {9,1,3},
            {9,4},
            {9,2},
            {7},
            {4},
            {1},
        };
        int[][] cols = {
            {5,3},
            {13},
            {15},
            {15},
            {16},
            {16},
            {16},
            {16},
            {3,7},
            {4},
            {1,6,1},
            {2,7,2},
            {15},
            {12},
            {13},
            {1,11,1},
            {2,11,2},
            {3,2,7},
            {1,4,1,6,6},
            {3,11},
            {2,2,4},
            {1,2,1,4},
            {1,3},
            {5},
            {2,1},
        };
        Nonogram.solve(rows, cols);
    }

    static final byte UNDEF = 0, WHITE = -1, BLACK = 1;
    
    static class RanSet {
        int length = -1;
        List<byte[]> sets = new ArrayList<>();

        public int size() {
            return sets.size();
        }

        public void add(byte[] set) {
            if (length < 0)
                length = set.length;
            else if (length != set.length)
                throw new IllegalStateException(
                    "expected length = " + length + " but " + set.length);
            byte[] add = new byte[length];
            for (int i = 0; i < length; ++i) {
                byte b = set[i];
                if (b == BLACK || b == WHITE)
                    add[i] = b;
                else
                    throw new IllegalArgumentException("set: " + Arrays.toString(set));
            }
            sets.add(add);
        }
  
        public byte[] common() {
            if (length < 0)
                throw new IllegalStateException("no element");
            byte[] common = sets.get(0).clone();
            for (byte[] set : sets)
                for (int i = 0; i < length; ++i)
                    if (common[i] != set[i])
                        common[i] = UNDEF;
            return common;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (byte[] set : sets) {
                for (byte b : set)
                    sb.append(b == 1 ? '*' : '.');
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }
        
    }

    static RanSet all(int[] row, int width) {
        int size = row.length;
        RanSet sets = new RanSet();
        new Object() {
            byte[] set = new byte[width];
            void all(int no, int start) {
                if (no >= size) {
                    for (int i = start; i < width; ++i)
                        set[i] = WHITE;
                    sets.add(set);
                } else if (start >= width) {
                    return;
                } else {
                    int seq = row[no];
                    for (int i = start, max = width - seq; i <= max; ++i) {
                        for (int j = start; j < i; ++j)
                            set[j] = WHITE;
                        int e = i + seq;
                        for (int j = i; j < e; ++j)
                            set[j] = BLACK;
                        if (e < width)
                            set[e] = WHITE;
                        all(no + 1, e + 1);
                    }
                }
            }
        }.all(0, 0);
        return sets;
    }
    
    @Test
    public void testAllCommon() {
        printTestCaseName();
        RanSet all = all(new int[] {1, 4}, 8);
        System.out.println(all);
        System.out.println(Arrays.toString(all.common()));
        assertArrayEquals(new byte[] {0, 0, 0, 0, 1, 1, 0, 0}, all.common());
    }
}
