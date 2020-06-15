package puzzle;

public class PascalsTriangle {

    /**
     * Pascalの３角形のn段目を返す。
     * @param n 段数を指定します。
     * @return Pascalの３角形のn段目を返します。
     *         配列のサイズはnです。
     *         0段目は[], 1段目は[1]、2段目は[1, 1]、3段目は[1, 2, 1]...となります。
     */
    public static int[] pascalsTriangle(int n) {
        int[] r = new int[n];
        for (int i = 0; i < n; ++i) {
            r[i] = 1;
            for (int j = i - 1; j > 0; --j)
                r[j] += r[j - 1];
        }
        return r;
    }

}
