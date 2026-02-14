package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestKomachi2 {

    static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;

    static class DivisionException extends Exception {}

    static int eval(List<Integer> input) throws DivisionException {
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

    static List<Integer> makeTerms(int[] digits, int[] ops) {
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

    static String padLeftZero(String s, int size) {
        for (int i = s.length(); i < size; ++i)
            s = "0" + s;
        return s;
    }

    static int[] intsBase5(int i, int length) {
        String base5 = Integer.toString(i, 5);
        String s = "0".repeat(length - base5.length()) + base5;
        return s.codePoints().map(c -> c - '0').toArray();
    }

    static String string(List<Integer> terms) {
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

    static void komachi2(int[] digits, int total) {
        int length = digits.length;
        int tryMax = 2 * IntStream.range(1, length).reduce(1, (a, b) -> a * 5);
        System.out.println(tryMax);
        for (int i = 0; i < tryMax; ++i) {
            int[] ops = intsBase5(i, length);
            List<Integer> terms = makeTerms(digits, ops);
            try {
                int sum = eval(terms);
                if (sum == total)
                    System.out.println(string(terms));
            } catch (DivisionException e) {
            }
        }
    }

    @Test
    public void testEval() throws DivisionException {
        assertEquals(579, eval(List.of(PLUS, 123, PLUS, 456)));
        assertEquals(147, eval(List.of(PLUS, 123, PLUS, 4, MULT, 6)));
        assertEquals(-99, eval(List.of(MINUS, 123, PLUS, 4, MULT, 6)));
    }

    @Test
    public void testCount() {
        int max = 2 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5;
        System.out.printf("max=%d(%s base 5)%n", max, Integer.toString(max, 5));
        System.out.printf("max - 1=%d(%s base 5)%n", max - 1, Integer.toString(max - 1, 5));
    }

    @Test
    public void testMakeTerms() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        assertEquals(List.of(-100, 123, -100, 456, -100, 789), makeTerms(digits, new int[] {0,4,4,0,4,4,0,4,4}));
    }

    @Test
    public void testIntsBase5() {
        assertArrayEquals(new int[] {0,4,4}, intsBase5(Integer.parseInt("044", 5), 3));
        assertArrayEquals(new int[] {0,1,2,3,4,1,2,3,4}, intsBase5(Integer.parseInt("012341234", 5), 9));
    }

    @Test
    public void testKomachi2() {
        int[] digits = {1,2,3,4,5,6,7,8,9};
        komachi2(digits, 100);
    }

}
