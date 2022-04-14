package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestRomanNumber {

    static int roman(String s) {
        return new Object() {
            int index = 0;
            int ch = get();

            RuntimeException error(String format, Object... args) {
                return new RuntimeException(String.format(format, args));
            }

            int get() {
                return ch = index >= s.length() ? -1 : s.charAt(index++);
            }

            boolean eat(int e) {
                if (Character.toUpperCase(ch) == e) {
                    get();
                    return true;
                }
                return false;
            }

            /**
             * <pre>
             *  roman = d1 [ d1 [ d1 ] | d5 | d10 ]
             *        | d5 [ d1 [ d1 [ d1 ] ] ]
             *  ex)
             *  roman1 = 'I' [ 'I' [ 'I' ] | 'V' | 'X' ]
             *         | 'V' [ 'I' [ 'I' [ 'I' ] ] ]
             * </pre>
             */
            int parse(int d1, int d5, int d10) {
                return eat(d1) ? eat(d1) ? eat(d1) ? 3 : 2 : eat(d5) ? 4 : eat(d10) ? 9 : 1
                    : eat(d5) ? eat(d1) ? eat(d1) ? eat(d1) ? 8 : 7 : 6 : 5 : 0;
            }

            int parse() {
                int v = parse('M', '\0', '\0') * 1000
                    + parse('C', 'D', 'M') * 100
                    + parse('X', 'L', 'C') * 10
                    + parse('I', 'V', 'X');
                if (index < s.length())
                    throw error("extra string: " + s.substring(index - 1));
                return v;
            }
        }.parse();
    }

    @Test
    public void test() {
        assertEquals(1, roman("I"));
        assertEquals(2, roman("II"));
        assertEquals(3, roman("III"));
        assertEquals(4, roman("IV"));
        assertEquals(5, roman("V"));
        assertEquals(6, roman("VI"));
        assertEquals(7, roman("VII"));
        assertEquals(8, roman("VIII"));
        assertEquals(9, roman("IX"));
        assertEquals(10, roman("X"));
        assertEquals(11, roman("XI"));
        assertEquals(12, roman("XII"));
        assertEquals(13, roman("XIII"));
        assertEquals(14, roman("XIV"));
        assertEquals(15, roman("XV"));
        assertEquals(16, roman("XVI"));
        assertEquals(17, roman("XVII"));
        assertEquals(18, roman("XVIII"));
        assertEquals(19, roman("XIX"));
        assertEquals(20, roman("XX"));
        assertEquals(1957, roman("MCMLVII"));
        assertEquals(3999, roman("MMMCMXCIX"));
    }

}
