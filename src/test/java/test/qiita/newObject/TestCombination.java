package test.qiita.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Javaで数学の組み合わせ(Combination)を実装する - Qiita
 * https://qiita.com/5zm/items/949bf0721e8324f0ee35
 */
public class TestCombination {

    static List<String[]> combination(String[] data, int k) {
        List<String[]> result = new ArrayList<String[]>();
        combination(data, 0, new String[k], 0, result);
        return result;
    }

    static void combination(String[] data, int di, String[] comb, int ci, List<String[]> result) {
        if (ci == comb.length) {
            result.add(comb.clone());
            return;
        }
        for ( ; di <= data.length - (comb.length - ci); di++) {
            comb[ci] = data[di];
            combination(data, di + 1, comb, ci + 1, result);
        }
    }

    @Test
    public void testCombination() {
        String[] a = {"a", "b", "c", "d"};
        for (String[] s : combination(a, 2))
            System.out.println(Arrays.toString(s));
    }
}