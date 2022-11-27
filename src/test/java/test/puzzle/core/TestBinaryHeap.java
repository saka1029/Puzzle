package test.puzzle.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * 二分ヒープ
 * 構造
 * 親要素が常に2つの子要素より大きくならない（またはその逆）構造になっている。
 * 挿入、削除がO(log n)で可能。
 * 探索は O(n)。
 * ルートが常に最小（または最大）要素となっているので、
 * ルートの削除を繰り返すことで、ソートを行うことができる。
 * このときの計算量は O(n \ log n)。(ヒープソート)
 * 
 * 要素の添字を 1 から開始すると、
 * 要素 n の親は n / 2 、子は 2n および 2n + 1
 * 添字を 0 から開始すると
 * 要素 n の親は (n - 1) / 2、子は 2n + 1 および 2n + 2
 */
public class TestBinaryHeap {

    static void insert(List<Integer> binaryHeap, int element) {
        binaryHeap.add(element);
        for (int n = binaryHeap.size() - 1; n > 0; ) {
            int parent = (n - 1) / 2;
            if (binaryHeap.get(parent) < binaryHeap.get(n))
                break;
            Collections.swap(binaryHeap, n, parent);
            n = parent;
        }
    }

    @Test
    public void test() {
        List<Integer> binaryHeap = new ArrayList<>();
        insert(binaryHeap, 0);
        insert(binaryHeap, 3);
        insert(binaryHeap, 1);
        insert(binaryHeap, 2);
        insert(binaryHeap, 4);
        System.out.println(binaryHeap);
    }

}
