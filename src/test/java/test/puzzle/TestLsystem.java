package test.puzzle;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class TestLsystem {

    /**
     * 例1：藻類 L-system 誕生の契機となった、藻類の成長を記述する例。
     *
     * <pre>
     * V ： A, B
     * S ： なし
     * ω ： A
     * P ： (A → AB), (B → A)
     *
     * 順次計算してゆくと、文字列は以下のように成長する。
     *
     * n = 0 ： A
     * n = 1 ： AB
     * n = 2 ： ABA
     * n = 3 ： ABAAB
     * n = 4 ： ABAABABA
     * </pre>
     */

    /**
     * 次の世代を求める。
     */
    static <T> List<T> 次世代(List<T> s, Map<T, List<T>> 置換規則) {
        return s.stream()
            .flatMap(e -> 置換規則.get(e).stream())
            .collect(Collectors.toList());
    }

    static <T> List<List<T>> 世代履歴(List<T> s, Map<T, List<T>> 置換規則, int 世代数) {
//        List<List<T>> 結果 = new ArrayList<>();
//        for (int 世代 = 0; 世代 <= 世代数; ++世代, s = 次世代(s, 置換規則))
//            結果.add(s);
//        return 結果;

//        return IntStream.rangeClosed(0, 世代数)
//            .mapToObj(i -> i)
//            .reduce(new LinkedList<List<T>>(), (a, b) -> {
//                a.add(a.isEmpty() ? s : 次世代(a.getLast(), 置換規則));
//                return a;
//            }, (a, b) -> { a.addAll(b); return a; });

        return Stream.iterate(s, t -> 次世代(t, 置換規則))
            .limit(世代数 + 1)
            .collect(Collectors.toList());
    }

    @Test
    void test藻類() {
        List<List<Character>> 結果 = 世代履歴(
            List.of('A'),
            Map.of('A', List.of('A', 'B'), 'B', List.of('A')),
            10);
        System.out.println(結果);
        System.out.println(Arrays.toString(
            結果.stream().mapToInt(List::size).toArray()));
    }

    static List<Character> list(String s) {
        return s.chars().mapToObj(i -> Character.valueOf((char)i)).collect(Collectors.toList());
    }

    @Test
    void testDragonCurve() {
        Map<Character, List<Character>> 置換規則 = Map.of(
            'X', list("X+YF+"),
            'Y', list("-FX-Y"),
            'F', list("F"),
            '+', list("+"),
            '-', list("-"));
        List<List<Character>> 結果 = 世代履歴(list("FX"), 置換規則, 10);
        System.out.println(結果);
    }

}
