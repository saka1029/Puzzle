package test.puzzle.core;

import org.junit.Test;

public class TestKomachiRPN {

    static int patterns(int n) {
        return new Object() {
            int count = 0;
            void solve(int i, int j, String s) {
                if (i >= n && j >= n - 1)
                    System.out.println(++count + " : " + s);
                else {
                    if (i < n)
                        solve(i + 1, j, s + (char)(i + '1'));
                    if (j < n - 1 && i - j >= 2)
                        solve(i, j + 1, s + (char)(j + 'a'));
                }
            }
            int solve() {
                solve(0, 0, "");
                return count;
            }
        }.solve();
    }

    @Test
    public void testPatterns() {
        for (int i = 2; i < 10; ++i)
            System.out.printf("%3d %8d%n", i, patterns(i));
    }
}
