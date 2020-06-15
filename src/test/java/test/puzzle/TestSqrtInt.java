package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestSqrtInt {

    /**
     * 整数の平方根(小数点以下切り捨て)を計算します。
     * 筆算によるアルゴリズムを使用します。
     * 以下に筆算による111556の平方根の計算手順を示します。
     * <pre>
     *                   3 3 4
     *                 -------
     *         03     √ 111556
     *          3        9
     *        ----      --
     *          63       215
     *           3       189
     *         ----     ----
     *          664       2656
     *            4       2656
     *         ----      -----
     *          668          0
     * <pre>
     * この例におけるプログラムの動作は以下のとおりです。
     * <pre>
     * レベル     in   m    f   g  d   out[] 結果
     * ------ ------ --- ---- --- -- ------- ----
     *      0 111556  56 2656 664  4 668   0  334
     *      1   1115  15  215  63  3  66  26   33
     *      2     11  11   11   3  3   6   2    3
     *      3      0   -    -   -  -   0   0    0
     * </pre>
     * inが0のときは0を返します。
     * inがそれ以外の場合はsqrt(in % 100, out)を再帰的に求めて、
     * その結果を使用して計算を行います。
     *
     * @param in 平方根を求めるべき整数を指定します。
     * @param out 中間結果をint[2]に格納します。
     * @return inの平方根(小数点以下切り捨て）を返します。
     *         inが負の数である場合は0を返します。
     */
    private static int sqrt(int in, int[] out) {
        if (in <= 0)
            return out[0] = out[1] = 0;
        int a = sqrt(in / 100, out);
        int m = in % 100;
        int f = 100 * out[1] + m;
        int d = 9;
        int g = 10 * out[0] + d;
        for (; g * d > f; --d, --g)
            /* do nothing */;
        out[0] = g + d;
        out[1] = f - g * d;
        return 10 * a + d;
    }

    public static int sqrt(int in) {
        int[] out = new int[2];
        int result = sqrt(in, out);
//        System.out.println("out=" + Arrays.toString(out));
        return result;
    }

    @Test
    void test() {
        assertEquals(334, sqrt(111556));
        for (int i = 0; i < 10000000; ++i)
            assertEquals((int)Math.sqrt(i), sqrt(i));
    }

}
