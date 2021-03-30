package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestMagicSquare {

    static int[][] oddMagicSquare(int n) {
        if (n % 2 != 1)
            throw new IllegalArgumentException("n must be odd number");
        int[][] result = new int[n][n];
        for (int i = 1, max = n * n, x = 0, y = n / 2; i <= max; i++) {
            result[x][y] = i;
            if (x == 0 && y == 0 || x > 0 && y > 0 && result[x - 1][y - 1] != 0) {
                ++x;
            } else {
                if (--x < 0) x += n;
                if (--y < 0) y += n;
            }
        }
        return result;
    }

    static void checkMagicSaquare(int[][] m) {
        int n = m.length;
        int expected = n * (n * n + 1) / 2;
        int diagLeftTotal = 0, diagRightTotal = 0;
        for (int i = 0, k = n - 1; i < n; ++i, --k) {
            int rowTotal = 0, colTotal = 0;
            for (int j = 0; j < n; ++j) {
                rowTotal += m[i][j];
                colTotal += m[j][i];
            }
            assertEquals(expected, rowTotal);
            assertEquals(expected, colTotal);
            diagLeftTotal += m[i][i];
            diagRightTotal += m[i][k];
        }
        assertEquals(expected, diagLeftTotal);
        assertEquals(expected, diagRightTotal);
    }

    @Test
    void testOddMagicSquare() {
        int[][] expected = {
            {28,  19,  10,   1,  48,  39,  30},
            {29,  27,  18,   9,   7,  47,  38},
            {37,  35,  26,  17,   8,   6,  46},
            {45,  36,  34,  25,  16,  14,   5},
            { 4,  44,  42,  33,  24,  15,  13},
            {12,   3,  43,  41,  32,  23,  21},
            {20,  11,   2,  49,  40,  31,  22},
        };
        int[][] actual = oddMagicSquare(7);
        assertArrayEquals(expected, actual);
        for (int i = 1; i < 100; i += 2)
            checkMagicSaquare(oddMagicSquare(i));
    }


}
