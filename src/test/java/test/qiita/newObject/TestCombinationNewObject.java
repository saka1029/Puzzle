package test.qiita.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestCombinationNewObject {

    static List<String[]> combination(String[] data, int k) {
        int length = data.length;
        List<String[]> result = new ArrayList<String[]>();
        String[] comb = new String[k];
        new Object() {
            void combination(int di, int ci) {
                if (ci == k) {
                    result.add(comb.clone());
                    return;
                }
                for (; di <= length - (k - ci); di++) {
                    comb[ci] = data[di];
                    combination(di + 1, ci + 1);
                }
            }
        }.combination(0, 0);
        return result;
    }

    @Test
    public void testCombination() {
        String[] a = {"a", "b", "c", "d"};
        for (String[] s : combination(a, 2))
            System.out.println(Arrays.toString(s));
    }
}