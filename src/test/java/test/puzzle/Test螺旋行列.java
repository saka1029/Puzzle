package test.puzzle;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class Test螺旋行列 {

    /**
     * 螺旋正方行列を求めます。
     *
     * s = 6の時は外側の6x6の枠、4x4の枠、2x2の枠の順に値を埋めます。
     * 最初のfor文はnxnの枠を作ります。
     * sが奇数の場合は中央に1x1の枠が残ってしまうので、これはループ外で特別にしょりします。
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

    @Test
    public void testSpiralMatrix() {
        for (int s = 0; s < 10; ++s) {
            int[][] a = spiralMatrix(s);
            System.out.println("s=" + s);
            for (int[] row : a)
                System.out.println(Arrays.toString(row));
            System.out.println();
        }
    }
}
