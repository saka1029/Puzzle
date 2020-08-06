package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 可変進数を使用した順列生成クラス
 *
 * 可変進数とは各桁ごとに異なる進数を持つ数です。
 * 具体的には下1桁目は1進数、下2桁目は2進数、下3桁目は3進数、…
 * となる数です。
 * 例えば3桁の可変進数は以下のようになります。
 * <pre>
 * 0 : [0, 0, 0]
 * 1 : [0, 1, 0]
 * 2 : [1, 0, 0]
 * 3 : [1, 1, 0]
 * 4 : [2, 0, 0]
 * 5 : [2, 1, 0]
 * </pre>
 * 下1桁目は1進数なので常に0です。
 * 下2桁目は2進数なので0または1です。
 * 下3桁目は3進数なので0または1または2です。
 * この可変進数を順列に置換すると以下のようになります。
 * <pre>
 * 0 : [0, 0, 0] -> [0, 1, 2]
 * 1 : [0, 1, 0] -> [0, 2, 1]
 * 2 : [1, 0, 0] -> [1, 0, 2]
 * 3 : [1, 1, 0] -> [1, 2, 0]
 * 4 : [2, 0, 0] -> [2, 0, 1]
 * 5 : [2, 1, 0] -> [2, 1, 0]
 * </pre>
 * 可変進数[2, 1, 0]を順列に変換するためには、最初に置換配列[0, 1, 2]を用意します。
 * 可変進数の一番左の桁(2)を取り出して置換配列の2番目の要素(2)を取り出します。
 * これを順列の先頭に配置します。置換配列から取り出した要素(2)は置換配列から削除します。
 * 置換配列は[0, 1]となります。
 * 次に左から2番目の桁についても同様の操作をすると以下のようになります。
 * <pre>
 * ステップ  可変進数        置換配列        順列
 * 1桁目     [{2}, 1,  0 ]   [ 0,  1, {2} ]   [{2}]
 * 2桁目     [ 2, {1}, 0 ]   [ 0, {1}]        [ 2, {1} ]
 * 3桁目     [ 2,  1, {0}]   [{0}]            [ 2,  1, {0}]
 * 最終形    [ 2,  1,  0 ]   []               [ 2,  1,  0 ]
 * <pre>
 */
public class PermutationV {

    private PermutationV() {}

    /**
     * n個のものからr個取る順列の総数を計算します。
     */
    public static int size(int n, int r) {
        int size = 1;
        for (int i = 0, e = n; i < r; ++i, --e)
            size *= e;
        return size;
    }

    /**
     * n個からr個取るindex番目の順列の可変進数配列を求めます。
     */
    public static int[] vbn(int n, int r, int index) {
        int[] vbn = new int[r];
        for (int b = n - r + 1, k = index, i = r - 1; i >= 0; k /= b++, --i)
            vbn[i] = k % b;
        return vbn;
    }

    /**
     * n個からr個取るindex番目の順列の可変進数配列から順列を求めます。
     */
    public static int[] perm(int n, int r, int[] vbn) {
        int[] perm = new int[r];
        List<Integer> t = new ArrayList<>(n);
        for (int i = 0; i < n; ++i)
            t.add(i);
        for (int i = 0; i < r; ++i)
           perm[i] = t.remove(vbn[i]);
        return perm;
    }

    /**
     * n個の要素(0, 1, 2, ... , n - 1)からr個選んだ時のindex番目の順列を返します。
     *
     * @param n 選択対象となる要素の数
     * @param r 選択する要素の数
     * @param index 順列の順序番号を指定します。
     * @return index番目の順列を返します。
     */
    public static int[] get(int n, int r, int index) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (r < 0) throw new IllegalArgumentException("r must be >= 0");
        if (r > n) throw new IllegalArgumentException("r must be <= n");
        int size = size(n, r);
        if (index < 0 || index >= size)
            throw new IllegalArgumentException("index must be between 0 and " + size + "(exclusive)");
        int[] vbn = vbn(n, r, index);
        return perm(n, r, vbn);
    }

    static class VBNIterator implements Iterator<int[]> {

        final int n, r, size;
        final int[] permutation;
        final int[] translator;
        int ordinal;

        VBNIterator(int n, int r) {
            this.n = n;
            this.r = r;
            this.size = size(n, r);
            this.permutation = new int[r];
            this.translator = new int[n];
//            this.translator = new ArrayList<>(n);  // LinkedListの方が少し速い。
            this.ordinal = 0;
        }

        @Override
        public boolean hasNext() {
            return ordinal < size;
        }

        int translate(int[] translator, int i) {
            int result = translator[i];
            System.arraycopy(translator, i + 1, translator, i, n - i - 1);
            return result;
        }

        @Override
        public int[] next() {
            for (int b = n - r + 1, k = ordinal, i = r - 1; i >= 0; k /= b++, --i)
                permutation[i] = k % b;
            for (int i = 0; i < n; ++i)
                translator[i] = i;
            for (int i = 0; i < r; ++i)
               permutation[i] = translate(translator, permutation[i]);
            ++ordinal;
            return permutation;
        }
    }

    public static Iterable<int[]> iterable(int n, int r) {
        return () -> new VBNIterator(n, r);
    }

    public static Stream<int[]> stream(int n, int r) {
        return StreamSupport.stream(iterable(n, r).spliterator(), false);
    }

    public static <T> Stream<T[]> stream(T[] array, int r) {
        return stream(array.length, r)
            .map(a -> {
                T[] sub = Arrays.copyOf(array, r);
                for (int i = 0; i < r; ++i)
                    sub[i] = array[a[i]];
                return sub;
            });
    }

    public static <T> Iterable<T[]> iterable(T[] array, int r) {
        return () -> stream(array, r).iterator();
    }

    public static <T> Stream<List<T>> stream(List<T> list, int r) {
        return stream(list.size(), r)
            .map(a -> {
                List<T> sub = new ArrayList<>();
                for (int i : a)
                    sub.add(list.get(i));
                return sub;
            });
    }

    public static <T> Iterable<List<T>> iterable(List<T> list, int r) {
        return () -> stream(list, r).iterator();
    }
}

