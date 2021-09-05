package puzzle;

import java.util.function.IntBinaryOperator;

public class Sort {
    private Sort() {}
    
    /**
     * intの引数を二つ取るコンシューマの
     * 関数型インタフェースです。
     */
    @FunctionalInterface
    public interface IntBiConsumer {
        void accept(int a, int b);
    }

    /**
     * 
     * @param comparator
     * @param swapper
     * @param begin
     * @param end
     */
    public static void quickSort(IntBinaryOperator comparator, IntBiConsumer swapper, int begin, int end) {
        new Object() {

            int partition(int begin, int end) {
                int pivotIndex = end;
                int i = (begin - 1);
                for (int j = begin; j < end; j++) {
                    if (comparator.applyAsInt(j, pivotIndex) <= 0) {
                        i++;
                        swapper.accept(i, j);
                    }
                }
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
        }.sort(begin, end);
    }
}
