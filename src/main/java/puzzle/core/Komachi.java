package puzzle.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 【繰返し計算版】
 * 【有理数非対応版】
 * 小町算（こまちざん）は、数の遊びである数学パズルの一種。
 * 1□2□3□4□5□6□7□8□9 = 100 という数式の□の中に、
 * +, -, ×, ÷, 空白 のいずれかを一つずつ入れて正しい数式を完成させるというものである。
 * 数字の列は配列で与えられるものとする。
 *
 * 9桁の5進数をコードされた計算式として扱う。
 * 各桁の意味は以下の通り。
 * <pre>
 * 0 : 前の桁に追加したものを合計に加算する。
 * 1 : 前の桁に追加したものを合計から減算する。
 * 2 : 前の桁に追加したものを合計に乗算する。
 * 3 : 前の桁に追加したものを合計から除算する。
 * 4 : 前の桁に追加する。
 * </pre>
 */
public class Komachi {

    public static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;

    public static class DivisionException extends Exception {}

    public static int eval(List<Integer> input) throws DivisionException {
        return new Object() {
            static int EOF = -99999;
            int index = 0, max = input.size();
            int token;

            int get() {
                return token = index < max ? input.get(index++) : EOF;
            }

            boolean eat(int expected) {
                if (token == expected) {
                    get();
                    return true;
                }
                return false;
            }

            int primary() {
                int value = token;
                get();
                return value;
            }

            int term() throws DivisionException {
                int value = primary();
                while (true)
                    if (eat(MULT)) {
                        value *= primary();
                    } else if (eat(DIV)) {
                        int p = primary();
                        if (value % p != 0)
                            throw new DivisionException();
                        value /= p;
                    } else
                        break;
                return value;
            }

            int factor() throws DivisionException {
                int value = 0;
                while (true)
                    if (eat(PLUS))
                        value += term();
                    else if (eat(MINUS))
                        value -= term();
                    else
                        break;
                return value;
            }

            int parse() throws DivisionException {
                get();
                return factor();
            }
        }.parse();
    }

    public static List<Integer> makeTerms(int[] digits, int[] ops) {
        int term = 0;
        List<Integer> terms = new ArrayList<>();
        for (int j = 0; j < digits.length; ++j) {
            int op = ops[j];
            switch (op) {
                case 0: case 1: case 2: case 3:
                    if (j > 0)
                        terms.add(term);
                    terms.add(op - 100);
                    term = digits[j];
                    break;
                case 4:
                    term = term * 10 + digits[j];
                    break;
            }
        }
        terms.add(term);
        return terms;
    }

    public static int[] intsBase5(int i, int length) {
        String base5 = Integer.toString(i, 5);
        String s = "0".repeat(length - base5.length()) + base5;
        return s.codePoints().map(c -> c - '0').toArray();
    }

    public static String string(List<Integer> terms) {
        StringBuilder sb = new StringBuilder();
        for (int i  : terms)
            sb.append(
                switch (i) {
                    case -100 -> "+";
                    case -99 -> "-";
                    case -98 -> "*";
                    case -97 -> "/";
                    default -> "" + i;
                });
        return sb.toString();
    }

    public static void solve(int[] digits, int total) {
        int length = digits.length;
        int tryMax = 2 * IntStream.range(1, length).reduce(1, (a, b) -> a * 5);
        System.out.println(tryMax);
        int count = 0;
        for (int i = 0; i < tryMax; ++i) {
            int[] ops = intsBase5(i, length);
            List<Integer> terms = makeTerms(digits, ops);
            try {
                int sum = eval(terms);
                if (sum == total)
                    System.out.println(++count + ": " + string(terms));
            } catch (DivisionException e) {
            }
        }
    }
}
