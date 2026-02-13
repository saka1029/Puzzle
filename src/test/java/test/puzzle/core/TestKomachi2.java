package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
}
