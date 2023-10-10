package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.Rational;

public class TestKomachi {

    /**
     * 小町算（こまちざん）は、数の遊びである数学パズルの一種。
     * 1□2□3□4□5□6□7□8□9 = 100 という数式の□の中に、
     * +, -, ×, ÷, 空白 のいずれかを一つずつ入れて正しい数式を完成させるというものである。
     * ここでは ×, ÷ を除外したものについて解く。
     * 数字の列は配列で与えられるものとする。
     *
     * 9桁の3進数をコードされた計算式として扱う。
     * 各桁の意味は以下の通り。
     * <pre>
     * 0 : 前の桁に追加する。
     * 1 : 前の桁に追加したものを合計から減算する。
     * 2 : 前の桁に追加したものを合計に加算する。
     * </pre>
     *
     * 9桁の3進数の内、先頭桁がゼロ以外のもの(1または2のもの)について調べる。
     * これは最後の桁が必ず合計に加算または減算されるようにするためである。
     * i = 3⁸ ... 3⁹ (末尾の3⁹は含まない）
     * <pre>
     * 100000000(3)  = 3⁸       =  6561
     * 100000001(3)  = 3⁸ + 1   =  6562
     * 100000002(3)  = 3⁸ + 2   =  6563
     *   .....
     * 222222222(3)  = 3⁹ - 1   = 19682
     * </pre>
     *
     * @param goal 一致すべき結果の値。
     * @param digits 数字の列
     */
    static int komachi(int goal, int[] digits, Consumer<String> callback) {
        final int RADIX = 3, size = digits.length;
        int count = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = (int)Math.pow(RADIX, size - 1), end = i * RADIX; i < end; ++i) {
            sb.setLength(0);
            int sum = 0;
            for (int j = 0, n = i, term = 0; j < size; ++j, n /= RADIX) {
                int p = n % RADIX;
                term = term * 10 + digits[j];
                if (p == 0) continue;
                sum += p == 1 ? -term : term;
                sb.append(p == 1 ? '-' : '+').append(term);
                term = 0;
            }
            if (sum == goal) {
                ++count;
                callback.accept(sb.toString());
            }
        }
        return count;
    }

    /**
     * <pre>
     * [SYNTAX]
     * expression ::= term { ( '+' | '-' ) term }
     * term       ::= factor { ( '*' | '-' ) factor }
     * factor     ::= [ ( '-' | '+' ) ] ( number | '(' expression ')' )
     * </pre>
     *
     * @param source
     * @return
     */
    static Rational calculate(String source) {
        return new Object() {
            int index = 0;
            int ch = get();

            int get() {
                return ch = index < source.length() ? source.charAt(index++) : -1;
            }

            void skipSpaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            boolean eat(int expected) {
                skipSpaces();
                if (ch != expected)
                    return false;
                get();
                return true;
            }

            Rational number() {
                StringBuilder sb = new StringBuilder();
                while (Character.isDigit(ch)) {
                    sb.append((char)ch);
                    get();
                }
                return Rational.parseRational(sb.toString());
            }

            Rational factor() {
                Rational sign = Rational.ONE;
                if (eat('-')) sign = Rational.MINUS_ONE;
                else if (eat('+')) sign = Rational.ONE;
                if (eat('(')) {
                    Rational result = expression();
                    if (!eat(')'))
                        throw new RuntimeException("')' expected");
                    return result.multiply(sign);
                } else if (Character.isDigit(ch))
                    return number().multiply(sign);
                else
                    throw new RuntimeException("unexpected character '" + ((char)ch) + "'");
            }

            Rational term() {
                Rational result = factor();
                while (true) {
                    if (eat('*')) result = result.multiply(factor());
                    else if (eat('/')) result = result.divide(factor());
                    else break;
                }
                return result;
            }

            Rational expression() {
                Rational result = term();
                while (true) {
                    if (eat('+')) result = result.add(term());
                    else if (eat('-')) result = result.subtract(term());
                    else break;
                }
                return result;
            }

            Rational parse() {
                Rational result = expression();
                if (ch != -1)
                    throw new RuntimeException("extra string '" + source.substring(index - 1) + "'");
                return result;
            }
        }.parse();
    }

    Set<String> ASC_EXPECTED = Set.of(
        "123+4-5+67-89",
        "1+23-4+5+6+78-9",
        "123+45-67+8-9",
        "123-4-5-6-7+8-9",
        "123-45-67+89",
        "12+3+4+5-6-7+89",
        "12-3-4+5-6+7+89",
        "1+2+34-5+67-8+9",
        "1+2+3-4+5+6+78+9",
        "-1+2-3+4+5+6+78+9",
        "12+3-4+5+67+8+9",
        "1+23-4+56+7+8+9"
    );

    Set<String> DESC_EXPECTED = Set.of(
        "98-7+6+5+4-3-2-1",
        "98+7-6+5-4+3-2-1",
        "9-8+7+65-4+32-1",
        "98+7+6-5-4-3+2-1",
        "9+8+76+5+4-3+2-1",
        "98-7+6-5+4+3+2-1",
        "98-7-6-5-4+3+21",
        "-9+8+76+5-4+3+21",
        "98-76+54+3+21",
        "9-8+76-5+4+3+21",
        "9-8+76+54-32+1",
        "98-7+6+5-4+3-2+1",
        "98+7-6-5+4+3-2+1",
        "-9+8+7+65-4+32+1",
        "98+7-6+5-4-3+2+1",
        "-9-8+76-5+43+2+1",
        "9+8+76+5-4+3+2+1",
        "98-7-6+5+4+3+2+1"
    );

    @Test
    public void testCalculate() {
        int[] ascending = IntStream.range(1, 10).toArray();
        int[] descending = IntStream.range(1, 10).map(i -> 10 - i).toArray();
        assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9}, ascending);
        assertArrayEquals(new int[] {9, 8, 7, 6, 5, 4, 3, 2, 1}, descending);
        Rational goal = Rational.of(100);
        Consumer<String> check = s -> assertEquals(goal, calculate(s));
//        Consumer<String> printCheck = s -> {
//            System.out.println(s);
//            assertEquals(goal, calculate(s));
//        };
        assertEquals(12, komachi(100, ascending, check));
        assertEquals(18, komachi(100, descending, check));
    }

    static int komachiByCalculator(int goal, int[] digits, String[] operators, Consumer<String> callback) {
        final int radix = operators.length, size = digits.length;
        final Rational GOAL = Rational.of(goal);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0, end = (int)Math.pow(radix, size); i < end; ++i) {
            if (!operators[i % radix].matches("|-")) continue;
            sb.setLength(0);
            for (int j = 0, n = i; j < size; ++j, n /= radix)
                sb.append(operators[n % radix]).append(digits[j]);
            String expression = sb.toString();
            if (calculate(expression).equals(GOAL)) {
                ++count;
                callback.accept(expression);
            }
        }
        return count;
    }

    @Test
    public void testKomachiByCalculator() {
        assertEquals(Rational.of(100), calculate("+123-45-67+89"));
        int[] ascending = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        String[] operators = {"", "+", "-"};
        Set<String> asc_actual = new HashSet<>();
        assertEquals(12, komachiByCalculator(100, ascending, operators, asc_actual::add));
        assertEquals(ASC_EXPECTED, asc_actual);
        int[] descending = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        Set<String> desc_actual = new HashSet<>();
        assertEquals(18, komachiByCalculator(100, descending, operators, desc_actual::add));
        assertEquals(DESC_EXPECTED, desc_actual);
    }

    @Test
    public void testKomachiFourRulesOperation() {
        Set<String> actual = new HashSet<>();
        assertEquals(162, komachiByCalculator(100,
            new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9},
            new String[] {"", "+", "-", "*", "/"},
            actual::add));
        for (String s : actual)
            System.out.println(s);
    }
    
    static int eval(String s) {
        int length = s.length(), sum = 0, sign = 1, term = 0;
        for (int i = 0; i < length; ++i) {
            char ch = s.charAt(i);
            if (ch == '+' || ch == '-') {
                sum += sign * term;
                sign = ch == '+' ? 1 : -1;
                term = 0;
            } else
                term = term * 10 + Character.getNumericValue(ch);
        }
        sum += sign * term;
        return sum;
    }

    static List<String> komachiRecursive(String input) {
        int length = input.length();
        List<String> output = new ArrayList<>();
        new Object() {
            void komachi(int i, int sum, int term, String e) {
                if (i >= length) {
                    if (sum + term == 100)
                        output.add(e + "+" + term);
                    else if (sum - term == 100)
                        output.add(e + "-" + term);
                } else {
                    int digit = input.charAt(i) - '0';
                    komachi(i + 1, sum, term * 10 + digit, e);
                    komachi(i + 1, sum + term, digit, e + (e.isEmpty() ? "" : "+") + term);
                    komachi(i + 1, sum - term, digit, e + "-" + term);
                }
            }
        }.komachi(1, 0, input.charAt(0) - '0', "");
        return output;
    }
    
    @Test
    public void testKomachiRecursive() {
        for (String e : komachiRecursive("123456789")) {
            int sum = eval(e);
            assertEquals(100, sum);
        }
    }

    static List<int[]> komachi2(int[] digits) {
        int size = digits.length;
        int[] terms = new int[size];
        List<int[]> result = new ArrayList<>();
        new Object() {
            void komachi(int i, int sum, int term, int j) {
                if (i >= size) {
                    if (sum + (terms[j] = term) == 100 || sum + (terms[j] = -term) == 100)
                        result.add(Arrays.copyOf(terms, j + 1));
                } else {
                    komachi(i + 1, sum, term * 10 + digits[i], j);
                    komachi(i + 1, sum + (terms[j] = term), digits[i], j + 1);
                    komachi(i + 1, sum + (terms[j] = -term), digits[i], j + 1);
                }
            }
        }.komachi(1, 0, digits[0], 0);
        return result;
    }
    
    @Test
    public void testKomachi2() {
        List<int[]> asc = komachi2(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
        assertEquals(ASC_EXPECTED.size(), asc.size());
        for (int[] e : asc)
            assertEquals(100, IntStream.of(e).sum());
        List<int[]> desc = komachi2(new int[] {9, 8, 7, 6, 5, 4, 3, 2, 1});
        assertEquals(DESC_EXPECTED.size(), desc.size());
        for (int[] e : desc)
            assertEquals(100, IntStream.of(e).sum());
    }
}
