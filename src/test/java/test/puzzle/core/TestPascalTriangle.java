package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import org.junit.Test;

import puzzle.functions.Memoizer;

public class TestPascalTriangle {

    /**
     * n行分のパスカルの3角形を返します。 結果は列幅可変の2次元配列です。 i行目はi + 1列あります。
     */
    static long[][] pascal(int n) {
        long[][] p = new long[n][];
        for (int i = 0; i < n; ++i) {
            p[i] = new long[i + 1];
            p[i][0] = p[i][i] = 1;
            for (int j = 1; j < i; ++j)
                p[i][j] = p[i - 1][j - 1] + p[i - 1][j];
        }
        return p;
    }

    @Test
    public void testPascal() {
        long[][] p = pascal(35);
        // assertEquals(601080390L, p[32][16]);
        long sum = 1;
        for (long[] row : p) {
            System.out.println(Arrays.toString(row));
            assertEquals(sum, LongStream.of(row).sum());
            sum *= 2;
        }
    }

    /**
     * パスカルの3角形は以下の漸化式で計算できます。
     *
     * <pre>
     * pascal(n, k)
     *  k = 0 -> 1
     *  k = n -> 1
     *  else -> pascal(n - 1, k -1) + pascal(n - 1, k)
     * </pre>
     *
     * これはnこの中からk個取る組み合わせの数でもあります。(nCk) これをMemoizerを使って、メモ化した関数として実装します。
     * 引数はカリー化します。
     */
    Memoizer<Integer, Memoizer<Integer, Long>> pascal =
        Memoizer.memoize(self -> n ->
            Memoizer.memoize(dummy -> k ->
                k == 0 || k == n ? 1L
                : self.apply(n - 1).apply(k - 1) + self.apply(n - 1).apply(k)));

    @Test
    public void testMemoizedPascal() {
        assertEquals(601080390L, (long) pascal.apply(32).apply(16));
        for (int i = 1; i < 20; ++i)
            System.out.println(pascal.cache().get(i));
    }

    /**
     * 共通部分式の計算を1回で済ませるようにしました。 少し速いようです。
     */
    Memoizer<Integer, Memoizer<Integer, Long>> pascal2 =
        Memoizer.memoize(self -> n ->
            Memoizer.memoize(dummy -> k -> {
                if (k == 0 || k == n)
                    return 1L;
                var row = self.apply(n - 1);
                return row.apply(k - 1) + row.apply(k);
            }));

    @Test
    public void testMemoizedPascal2() {
        assertEquals(601080390L, (long) pascal2.apply(32).apply(16));
        for (int i = 1; i < 20; ++i)
            System.out.println(pascal2.cache().get(i));
    }

    /**
     * キャッシュなしの再起呼び出しです。 遅いです。
     */
    static long pascalSimple(int n, int k) {
        if (k == 0 || k == n)
            return 1L;
        return pascalSimple(n - 1, k - 1) + pascalSimple(n - 1, k);
    }

    @Test
    public void testPascalSimple() {
        assertEquals(601080390L, (long) pascalSimple(32, 16));
    }

    /**
     * Stack overvlowで見つけたパスカルの3角形の第ｎ行を求めるプログラム [java - Pascal Triangle Integer
     * Overflow when values need to be find at row 30 - Stack Overflow]
     * (https://stackoverflow.com/questions/63374037/pascal-triangle-integer-overflow-when-values-need-to-be-find-at-row-30/63375157?noredirect=1#comment112066754_63375157)
     * そのままのプログラムではオーバーフローするので、いくつかの型をlongに代える必要がある。
     */
    public List<Integer> getRow(int rowIndex) {
        List<Integer> pt = new ArrayList<>();
        int prev = 1;
        int curr = 1;
        int n = rowIndex + 1;
        pt.add(prev);
        for (int i = 1; i <= rowIndex; i++) {
            curr = prev * (n - i) / i;
            pt.add(curr);
            prev = curr;
        }
        return pt;
    }

    /**
     * オーバーフローしないようにしたもの。
     */
    public int[] pascalRow(int rowIndex) {
        int[] row = new int[rowIndex + 1];
        int prev = 1;
        int curr = 1;
        int n = rowIndex + 1;
        row[0] = (int) prev;

        for (int i = 1; i <= rowIndex; i++) {
            curr = (int) ((long) prev * (n - i) / i); // ここの掛け算がintではオーバーフローする。
            row[i] = curr;
            prev = curr;
        }
        return row;
    }

    @Test
    public void testPascalRow() {
        for (int i = 0; i < 33; ++i)
            System.out.println(Arrays.toString(pascalRow(i)));
    }

}
