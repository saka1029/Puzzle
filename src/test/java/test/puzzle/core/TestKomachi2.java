package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestKomachi2 {

    static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;

    static int eval(List<Integer> input) {
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

            int term() {
                int value = primary();
                while (true)
                    if (eat(MULT))
                        value *= primary();
                    else if (eat(DIV))
                        value /= primary();
                    else
                        break;
                return value;
            }

            int factor() {
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

            int parse() {
                get();
                return factor();
            }
        }.parse();
    }

    @Test
    public void testEval() {
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


    static String padLeftZero(String s, int size) {
        for (int i = s.length(); i < size; ++i)
            s = "0" + s;
        return s;
    }

    static void komachi2(int[] digits, int total) {
        int length = digits.length;
        int tryMax = 2 * IntStream.range(1, length).reduce(1, (a, b) -> a * 5);
        System.out.println(tryMax);
        for (int i = 0; i < tryMax; ++i) {
            String ops = padLeftZero(Integer.toString(i, 5), length);
            int term = 0;
            List<Integer> terms = new ArrayList<>();
            for (int j = 0; j < length; ++j) {
                int op = ops.charAt(j) - '0';
                switch (op) {
                    case 0: case 1: case 2: case 3:
                        if (j > 0)
                            terms.add(term);
                        terms.add(op - 100);
                        term = digits[j];
                        break;
                    case 4:
                        term = term * 10 * digits[j];
                        break;
                }

            }
            System.out.printf("i=%d(%s) terms=%s%n", i, Integer.toString(i, 5), terms);
        }
    }

    @Test
    public void testKomachi2() {
        komachi2(new int[] {1,2,3,4,5,6,7,8,9}, 100);
    }
}
