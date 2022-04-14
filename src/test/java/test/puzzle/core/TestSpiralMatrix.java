package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TestSpiralMatrix {

    /**
     * s行s列の螺旋正方行列を求めます。
     * 左上隅(0, 0)に0を格納し、時計回りのらせん状に1, 2, 3, ...を埋めた正方行列を返します。
     *
     * s = 6の時は外側の6x6の枠、4x4の枠、2x2の枠の順に値を埋めます。
     * 最初のfor文はnxnの枠を作ります。
     * sが奇数の場合は中央に1x1の枠が残ってしまうので、これはループ外で特別に処理します。
     * <pre>
     * 6  6  6  6  6  6
     * 6  4  4  4  4  6
     * 6  4  2  2  4  6
     * 6  4  2  2  4  6
     * 6  4  4  4  4  6
     * 6  6  6  6  6  6
     * </pre>
     * 次のfor文はnxnの枠の1辺を作ります。(4回繰り返します)
     * 各辺の長さは(n - 1)になります。
     * <pre>
     * 5  -  -  -  >  5
     * ^  3  -  >  3  |
     * |  ^  1  1  |  |
     * |  |  1  1  v  |
     * |  3  <  -  3  v
     * 5  <  -  -  -  5
     * </pre>
     * 最後のfor文は辺の中の各点を作ります。
     *
     * nは埋める値です。0から始めて値を格納するごとにインクリメントします。
     * dxとdyは値を埋める方向です。
     * <br>
     * <pre>
     * t = dx, dx = dy, dy = -t は方向を右回りに90度回転させます。
     * 座標を左回りにΘ回転させる式は
     *
     * x' = x * cos(Θ) - y * sin(Θ)
     * y' = x * sin(Θ) + y * cos(Θ)
     *
     * ここでは右回りに90度回転させるので、
     * Θ = -π / 2, sin(Θ) = -1, cos(Θ) = 0 となり
     *
     * x' = y
     * y' = -x
     *
     * となります。
     * 結果として右回りに90度回転する場合は
     * dx = 0, dy = 1からはじめてt = dx, dx = dy, dy = -tとします。
     * 左回りに90度回転する場合は
     * dx = 1, dy = 0からはじめてt = dy, dy = dx, dx = -tとします。
     *
     * b : 辺の長さ
     * c : 各四角形の左上隅の座標(x, yで共用)
     * x, y : 移動する座標
     * dx, dy : 移動方向
     * j : 辺の番号(0, 1, 2, 3)
     * i : 辺上の位置
     *
     * <br>
     * sが奇数の時は中央の値（最後の値）だけループの外で埋めてやる必要があります。
     *
     * @param s 螺旋行列の1辺の大きさを指定します。
     * @return s行s列の螺旋行列を返します。
     */
    static int[][] spiralMatrix(int s) {
        int[][] a = new int[s][s];
        int n = 0;
        for (int b = s - 1, c = 0, x = 0, y = 0, dx = 0, dy = 1; b > 0; b -= 2, x = y = ++c)
            for (int j = 0, t = 0; j < 4; ++j, t = dx, dx = dy, dy = -t)  // Repeat 4 times while changing the direction 90 degrees.
                for (int i = 0; i < b; ++i, x += dx, y += dy, ++n)
                    a[x][y] = n;
        if (s % 2 == 1)             // if s is odd
            a[s / 2][s / 2] = n;    // fill the last element at the center
        return a;
    }

    /**
     * sが奇数の場合の最後のif文を無理やり削除したもの。
     */
    static int[][] spiralMatrix2(int s) {
        int[][] a = new int[s][s];
        int n = 0;  // 埋め込む数字
        // c 左上隅の座標(x, yで共用)
        // b 辺の長さ
        // t 一時変数
        int x = 0, y = 0, dx = 0, dy = 1, t = 0;
        for (int b = s - 1, c = 0; b >= 0; b -= 2, x = y = ++c) {
            System.out.println("F1 n=" + n + " x=" + x + " y=" + y + " dx=" + dx + " dy=" + dy + " c=" + c + " b=" + b);
            for (int j = 0, m = b == 0 ? 1 : 4; j < m; ++j, t = dx, dx = dy, dy = -t) {
                System.out.println("F2 n=" + n + " x=" + x + " y=" + y + " dx=" + dx + " dy=" + dy + " c=" + c + " b=" + b);
                for (int i = 0; b == 0 && i <= b || i < b; ++i, x += dx, y += dy, ++n) {
                    System.out.println("F3 n=" + n + " x=" + x + " y=" + y + " dx=" + dx + " dy=" + dy + " c=" + c + " b=" + b);
                    a[x][y] = n;
                }
            }
        }
//        if (s % 2 == 1)             // if s is odd
//            a[s / 2][s / 2] = n;    // fill the last element at the center
        System.out.println("END n=" + n + " x=" + x + " y=" + y + " dx=" + dx + " dy=" + dy);
        return a;
    }

    /**
     * dx = 1, dy = 0からはじめてt = dy, dy = dx, dx = -tとします。
     */
    static int[][] spiralLeftMatrix(int s) {
        int[][] a = new int[s][s];
        int n = 0;
        for (int b = s - 1, c = 0, x = 0, y = 0, dx = 1, dy = 0; b > 0; b -= 2, x = y = ++c)
            for (int j = 0, t = 0; j < 4; ++j, t = dy, dy = dx, dx = -t)
                for (int i = 0; i < b; ++i, x += dx, y += dy, ++n)
                    a[x][y] = n;
        if (s % 2 == 1)             // if s is odd
            a[s / 2][s / 2] = n;    // fill the last element at the center
        return a;
    }

    void print(int[][] m) {
        for (int[] r : m)
            System.out.println(Arrays.toString(r));
    }

    static final int[][][] SPIRALS = {
        // s = 0
        {},
        // s = 1
        {{0}},
        // s = 2
        {{0, 1},
        {3, 2}},
        // s = 3
        {{0, 1, 2},
        {7, 8, 3},
        {6, 5, 4}},
        // s = 4
        {{0, 1, 2, 3},
        {11, 12, 13, 4},
        {10, 15, 14, 5},
        {9, 8, 7, 6}},
        // s = 5
        {{0, 1, 2, 3, 4},
        {15, 16, 17, 18, 5},
        {14, 23, 24, 19, 6},
        {13, 22, 21, 20, 7},
        {12, 11, 10, 9, 8}},
        // s = 6
        {{0, 1, 2, 3, 4, 5},
        {19, 20, 21, 22, 23, 6},
        {18, 31, 32, 33, 24, 7},
        {17, 30, 35, 34, 25, 8},
        {16, 29, 28, 27, 26, 9},
        {15, 14, 13, 12, 11, 10}},
        // s = 7
        {{0, 1, 2, 3, 4, 5, 6},
        {23, 24, 25, 26, 27, 28, 7},
        {22, 39, 40, 41, 42, 29, 8},
        {21, 38, 47, 48, 43, 30, 9},
        {20, 37, 46, 45, 44, 31, 10},
        {19, 36, 35, 34, 33, 32, 11},
        {18, 17, 16, 15, 14, 13, 12}},
        // s = 8
        {{0, 1, 2, 3, 4, 5, 6, 7},
        {27, 28, 29, 30, 31, 32, 33, 8},
        {26, 47, 48, 49, 50, 51, 34, 9},
        {25, 46, 59, 60, 61, 52, 35, 10},
        {24, 45, 58, 63, 62, 53, 36, 11},
        {23, 44, 57, 56, 55, 54, 37, 12},
        {22, 43, 42, 41, 40, 39, 38, 13},
        {21, 20, 19, 18, 17, 16, 15, 14}},
        // s = 9
        {{0, 1, 2, 3, 4, 5, 6, 7, 8},
        {31, 32, 33, 34, 35, 36, 37, 38, 9},
        {30, 55, 56, 57, 58, 59, 60, 39, 10},
        {29, 54, 71, 72, 73, 74, 61, 40, 11},
        {28, 53, 70, 79, 80, 75, 62, 41, 12},
        {27, 52, 69, 78, 77, 76, 63, 42, 13},
        {26, 51, 68, 67, 66, 65, 64, 43, 14},
        {25, 50, 49, 48, 47, 46, 45, 44, 15},
        {24, 23, 22, 21, 20, 19, 18, 17, 16}},
    };

    @Test
    public void testSpiralMatrix() {
        for (int s = 0; s < 10; ++s) {
            int[][] a = spiralMatrix(s);
            System.out.println("s=" + s);
            print(a);
            assertArrayEquals(SPIRALS[s], a);
        }
    }

    @Test
    public void testSpiralMatrix2() {
        for (int s = 0; s < 10; ++s) {
            int[][] a = spiralMatrix2(s);
            System.out.println("s=" + s);
            print(a);
            assertArrayEquals(SPIRALS[s], a);
        }
    }

    static final int[][][] LEFTS = {
        // s=0
        {},
        // s=1
        {{0}},
        // s=2
        {{0, 3},
        {1, 2}},
        // s=3
        {{0, 7, 6},
        {1, 8, 5},
        {2, 3, 4}},
        // s=4
        {{0, 11, 10, 9},
        {1, 12, 15, 8},
        {2, 13, 14, 7},
        {3, 4, 5, 6}},
        // s=5
        {{0, 15, 14, 13, 12},
        {1, 16, 23, 22, 11},
        {2, 17, 24, 21, 10},
        {3, 18, 19, 20, 9},
        {4, 5, 6, 7, 8}},
        // s=6
        {{0, 19, 18, 17, 16, 15},
        {1, 20, 31, 30, 29, 14},
        {2, 21, 32, 35, 28, 13},
        {3, 22, 33, 34, 27, 12},
        {4, 23, 24, 25, 26, 11},
        {5, 6, 7, 8, 9, 10}},
        // s=7
        {{0, 23, 22, 21, 20, 19, 18},
        {1, 24, 39, 38, 37, 36, 17},
        {2, 25, 40, 47, 46, 35, 16},
        {3, 26, 41, 48, 45, 34, 15},
        {4, 27, 42, 43, 44, 33, 14},
        {5, 28, 29, 30, 31, 32, 13},
        {6, 7, 8, 9, 10, 11, 12}},
        // s=8
        {{0, 27, 26, 25, 24, 23, 22, 21},
        {1, 28, 47, 46, 45, 44, 43, 20},
        {2, 29, 48, 59, 58, 57, 42, 19},
        {3, 30, 49, 60, 63, 56, 41, 18},
        {4, 31, 50, 61, 62, 55, 40, 17},
        {5, 32, 51, 52, 53, 54, 39, 16},
        {6, 33, 34, 35, 36, 37, 38, 15},
        {7, 8, 9, 10, 11, 12, 13, 14}},
        // s=9
        {{0, 31, 30, 29, 28, 27, 26, 25, 24},
        {1, 32, 55, 54, 53, 52, 51, 50, 23},
        {2, 33, 56, 71, 70, 69, 68, 49, 22},
        {3, 34, 57, 72, 79, 78, 67, 48, 21},
        {4, 35, 58, 73, 80, 77, 66, 47, 20},
        {5, 36, 59, 74, 75, 76, 65, 46, 19},
        {6, 37, 60, 61, 62, 63, 64, 45, 18},
        {7, 38, 39, 40, 41, 42, 43, 44, 17},
        {8, 9, 10, 11, 12, 13, 14, 15, 16}},
    };

    @Test
    public void testSpiralLeftMatrix() {
        for (int s = 0; s < 10; ++s) {
            int[][] a = spiralLeftMatrix(s);
            System.out.println("s=" + s);
            print(a);
            assertArrayEquals(LEFTS[s], a);
        }
    }

    /**
     * 正方行列の中身をらせん状に取り出す。
     */
    public static void printSpiral(int[][] m) {
        int s = m.length;
        for (int b = s - 1, c = 0, x = 0, y = 0, dx = 0, dy = 1; b > 0; b -= 2, x = y = ++c)
            for (int j = 0, t = 0; j < 4; ++j, t = dx, dx = dy, dy = -t)
                for (int i = 0; i < b; ++i, x += dx, y += dy)
                    System.out.print(m[x][y] + " ");
        if (s % 2 == 1)
            System.out.print(m[s / 2][s / 2]);
        System.out.println();
    }

    @Test
    public void testPrintSpiral() {
        int size = 5;
        int[][] m = new int[size][size];
        for (int i = 0, n = 1; i < size; ++i)
            for (int j = 0; j < size; ++j, ++n)
                m[i][j] = n;
        for (int[] row : m)
            System.out.println(Arrays.toString(row));
        printSpiral(m);
    }
}
