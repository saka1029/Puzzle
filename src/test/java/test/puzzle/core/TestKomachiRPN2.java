package test.puzzle.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import puzzle.core.Cons;

public class TestKomachiRPN2 {

    static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;

    static List<Cons<Integer>> solve(int[] digits, int goal) {
        return new Object() {
            List<Cons<Integer>> list = new ArrayList<>();
            final int digitsSize = digits.length;
            void solve(int i, int rpnTermSize, int rpnOpSize, int rpnDigitsSize, int termDigitsSize, int term, Cons<Integer> rpn) {
                if (rpnDigitsSize >= digitsSize && rpnOpSize >= rpnTermSize - 1) {
                    list.add(rpn.reverse());
                } else {
                    if (i < digitsSize)
                        solve(i + 1, rpnTermSize, rpnOpSize, rpnDigitsSize, termDigitsSize + 1, term * 10 + digits[i], rpn);
                    if (termDigitsSize > 0)
                        solve(i, rpnTermSize + 1, rpnOpSize, rpnDigitsSize + termDigitsSize, 0, 0, rpn.cons(term));
                    if (rpnOpSize < rpnTermSize - 1) {
                        solve(i, rpnTermSize, rpnOpSize + 1, rpnDigitsSize, termDigitsSize, term, rpn.cons(PLUS));
                        solve(i, rpnTermSize, rpnOpSize + 1, rpnDigitsSize, termDigitsSize, term, rpn.cons(MINUS));
                        solve(i, rpnTermSize, rpnOpSize + 1, rpnDigitsSize, termDigitsSize, term, rpn.cons(MULT));
                        solve(i, rpnTermSize, rpnOpSize + 1, rpnDigitsSize, termDigitsSize, term, rpn.cons(DIV));
                    }
                }
            }
            List<Cons<Integer>> solve() {
                solve(0, 0, 0, 0, 0, 0, Cons.nil());
                return list;
            }
        }.solve();
    }

    Map<Integer, String> OPS = Map.of(PLUS, "+", MINUS, "-", MULT, "*", DIV, "/");
    String print(Cons<Integer> list) {
        return list.stream()
            .map(e -> OPS.containsKey(e) ? OPS.get(e) : ("" + e))
            .collect(Collectors.joining(", ", "[", "]"));
    }

    @Test
    public void testSolve() {
        int[] digits = {1, 2, 3};
        List<Cons<Integer>> list = solve(digits, 100);
        for (var c : list)
            System.out.println(print(c));
    }
}
