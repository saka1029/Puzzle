package puzzle;

import java.util.List;
import java.util.Random;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;

public class IndexedCollectionUtil {
    private IndexedCollectionUtil() {}

    /**
     * intの引数を二つ取るコンシューマの
     * 関数型インタフェースです。
     */
    @FunctionalInterface
    public interface IntBiConsumer {
        void accept(int a, int b);
    }

    /**
     * インデックスで要素にアクセスできるコレクションをソートします。
     * 具体的には配列や、リストが対象です。
     *
     * @param comparator 要素を比較する比較子です。
     *                   二つのインデックス(int)が渡されるので、
     *                   そのインデックスが指し示す要素どうしを比較します。
     *                   結果は以下のように返す必要があります。
     *                   最初の要素 < 次の要素 の時負の整数、
     *                   最初の要素 == 次の要素 の時ゼロ、
     *                   最初の要素 > 次の要素 の時正の整数
     * @param swapper    要素を交換するオブジェクトです。
     *                   二つのインデックス(int)が渡されるので、
     *                   そのインデックスが指し示す要素どうしを交換します。
     * @param begin      ソートする先頭のインデックスを指定します。
     *                   このインデックス自身を含みます(inclusive)。
     * @param end        ソートする最後のインデックスを指定します。
     *                   このインデックス自身は含みません(exclusive)。
     */
    public static void quickSort(IntBinaryOperator comparator, IntBiConsumer swapper, int begin, int end) {
        new Object() {

            int partition(int begin, int end) {
                int pivotIndex = end;
                int i = (begin - 1);
                for (int j = begin; j < end; j++)
                    if (comparator.applyAsInt(j, pivotIndex) <= 0)
                        swapper.accept(++i, j);
                swapper.accept(i + 1, end);
                return i + 1;
            }

            void sort(int begin, int end) {
                if (begin >= end)
                    return;
                int partitionIndex = partition(begin, end);
                sort(begin, partitionIndex - 1);
                sort(partitionIndex + 1, end);
            }
        }.sort(begin, end - 1);
    }

    /**
     * from
     * Fisher–Yates shuffle - Wikipedia
     * https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
     *
     * <pre>
     * -- To shuffle an array a of n elements (indices 0..n-1):
     * for i from n−1 downto 1 do
     *     j ← random integer such that 0 ≤ j ≤ i
     *     exchange a[j] and a[i]
     * </pre>
     * @param swapper
     * @param begin inclusive begin
     * @param end exclusive end
     */
    public static void shuffle(IntBiConsumer swapper, int begin, int end) {
        Random random = new Random();
        for (int i = end - 1; i > begin; --i) {
            int j = random.nextInt(i - begin + 1) + begin;
            swapper.accept(i, j);
        }
    }

    public static void reverse(IntBiConsumer swapper, int begin, int end) {
        for (int i = begin, j = end - 1; i < j; ++i, --j)
            swapper.accept(i, j);
    }
    
    /**
     * Collections.binarySearch(コンパレータ引数あり)のソース
     * <pre>
     *     @SuppressWarnings("unchecked")
     *     public static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c) {
     *         if (c==null)
     *             return binarySearch((List<? extends Comparable<? super T>>) list, key);
     * 
     *         if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
     *             return Collections.indexedBinarySearch(list, key, c);
     *         else
     *             return Collections.iteratorBinarySearch(list, key, c);
     *     }
     * 
     *     private static <T> int indexedBinarySearch(List<? extends T> l, T key, Comparator<? super T> c) {
     *         int low = 0;
     *         int high = l.size()-1;
     * 
     *         while (low <= high) {
     *             int mid = (low + high) >>> 1;
     *             T midVal = l.get(mid);
     *             int cmp = c.compare(midVal, key);
     * 
     *             if (cmp < 0)
     *                 low = mid + 1;
     *             else if (cmp > 0)
     *                 high = mid - 1;
     *             else
     *                 return mid; // key found
     *         }
     *         return -(low + 1);  // key not found
     *     }
     *     
     *     private static <T> int iteratorBinarySearch(List<? extends T> l, T key, Comparator<? super T> c) {
     *         int low = 0;
     *         int high = l.size()-1;
     *         ListIterator<? extends T> i = l.listIterator();
     * 
     *         while (low <= high) {
     *             int mid = (low + high) >>> 1;
     *             T midVal = get(i, mid);
     *             int cmp = c.compare(midVal, key);
     * 
     *             if (cmp < 0)
     *                 low = mid + 1;
     *             else if (cmp > 0)
     *                 high = mid - 1;
     *             else
     *                 return mid; // key found
     *         }
     *         return -(low + 1);  // key not found
     *     }
     * 
     * </pre>
     */
    public static int binarySearch(IntPredicate comparator, int begin, int end) {
        return -1;
    }

    public static void swap(boolean[] a, int i, int j) {
        boolean temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void swap(byte[] a, int i, int j) {
        byte temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void swap(short[] a, int i, int j) {
        short temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void swap(long[] a, int i, int j) {
        long temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void swap(float[] a, int i, int j) {
        float temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static void swap(double[] a, int i, int j) {
        double temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static <T> void swap(T[] a, int i, int j) {
        T temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    public static <T> void swap(List<T> a, int i, int j) {
        T temp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, temp);
    }
}
