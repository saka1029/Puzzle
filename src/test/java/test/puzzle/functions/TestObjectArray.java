package test.puzzle.functions;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import puzzle.functions.ObjectArray;

class TestObjectArray {

    @Test
    void testOf() {
        ObjectArray array = ObjectArray.of(1, "a");
        ObjectArray expected = ObjectArray.of(1, "a");
        assertEquals(expected, array);
        ObjectArray expected2 = ObjectArray.of(Integer.valueOf(1), "a");
        assertEquals(expected2, array);
        ObjectArray expected3 = ObjectArray.of(1L, "a");
        assertNotEquals(expected3, array);
    }

    @Test
    void testShallwCopy() {
        Object[] a = {0, new int[] {1, 2}, 3};
        ObjectArray array = ObjectArray.of(a);
        ObjectArray expected = ObjectArray.of(0, new int[] {1, 2}, 3);
        assertEquals(expected, array);
        a[0] = 9;
        // 外部から値を変更できません。
        assertEquals(expected, array);
        // 外部から値を変更できます。
        ((int[]) a[1])[0] = 9;
        assertNotEquals(expected, array);
    }

    /**
     * Objects#deepToStringのドキュメントには以下の記述がある。
     *
     * 無限の再帰を避けるため、指定された配列に自身が要素として含まれるか、
     * 1つまたは複数の配列レベルを介した自身への間接参照が含まれる場合、
     * 自己参照は文字列「[...]」に変換されます。
     * たとえば、自身への参照だけを含む配列は、「[[...]]」として描画されます。
     */
    @Test
    public void testObjectsDeppToString() {
        assertEquals("[abc, [0, 1, 2], [[1, 2], [3, 4]]]",
            Arrays.deepToString(new Object[] {"abc", new int[] {0, 1, 2}, new int[][] {{1, 2}, {3, 4}}}));
        Object[] a = {1, 2, 3, 4, new double[] {8, 9}};
        a[1] = new Object[] {5, a}; // 自己参照
        assertEquals("[1, [5, [...]], 3, 4, [8.0, 9.0]]", Arrays.deepToString(a));
    }

    /**
     * 配列ではなく単一のオブジェクトを文字列化する。
     */
    static String deepToString(Object obj) {
        String result = Arrays.deepToString(new Object[] {obj});
        return result.substring(1, result.length() - 1);
    }

    @Test
    public void testDeepToString() {
        Object[] a = {"a"};
        a[0] = a;
        assertEquals("[[...]]", deepToString(a));
        assertEquals("null", deepToString(null));
        assertEquals("1", deepToString(1));
        assertEquals("[1, 2]", deepToString(new int[] {1,2}));
        assertEquals("obj", deepToString(new Object() { @Override public String toString() { return "obj";}}));
        Object[] b = {0, 0};
        Object[] c = {0};
        Object[] d = {0};
        b[0] = c; b[1] = d;
        c[0] = d; d[0] = c;
        assertEquals("[[[[...]]], [[[...]]]]", deepToString(b));
    }

    @Test
    public void testDeepToString3() {
        Object[] e = {0, 0, 0};
        Object[] f = {0};
        Object[] g = {0};
        Object[] h = {0};
        e[0] = f; e[1] = g; e[2] = h;
        f[0] = g; g[0] = h; h[0] = f;
        assertEquals("[[[[[...]]]], [[[[...]]]], [[[[...]]]]]", deepToString(e));
    }

    @Test
    public void testDeepToString3r() {
        Object[] e = {0, 0, 0};
        Object[] f = {0};
        Object[] g = {0};
        Object[] h = {0};
        e[0] = f; e[1] = g; e[2] = h;
        f[0] = h; g[0] = f; h[0] = g;
        assertEquals("[[[[[...]]]], [[[[...]]]], [[[[...]]]]]", deepToString(e));
    }

    @Test
    public void testDeepToStringN() {
        Object[][][] x = {{{0}}};
        x[0][0][0] = x[0][0];
        System.out.println(Arrays.deepToString(x));

        for (int n = 1; n <= 8; ++n) {
            Object[][] a = new Object[n][1];
            for (int j = 1; j < n; ++j)
                a[j][0] = a[j - 1];
            a[0][0] = a[n - 1];
            System.out.println(Arrays.deepToString(a));
        }
    }

    @Test
    public void testDeepHashCodeDeepEquals() {
//        assertEquals(0, Arrays.deepHashCode(a));    // StackOverflowError
//        Object[] b = {"a"};
//        b[0] = b;
//        assertTrue(Arrays.deepEquals(a, b));    // StackOverflowError
    }

}
