package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;

class TestIntList {

    /**
     * <pre>
     * list    = '[' [ element { ',' element } ] ']'
     * list    = '[' ( ']' | element { ',' element } ']' )
     * element = number | list;
     * number  = [ '-' ] digits [ '.' digits ] [ ( 'e' | 'E' ) [ '+' | '-' ] digits ]
     * digits  = digit { digit }
     * </pre>
     */
    static List<Object> parse(String source) {
        return new Object() {
            int length = source.length();
            int index = 0;

            int get() {
                return index < length ? source.charAt(index) : -1;
            }

            boolean isSpace(int ch) {
                return Character.isWhitespace(ch);
            }

            void skip() {
                ++index;
            }

            boolean spaces() {
                while (isSpace(get()))
                    skip();
                return true;
            }

            StringBuilder token = new StringBuilder();

            boolean clear() {
                token.setLength(0);
                return true;
            }

            boolean append(int ch) {
                token.append((char)ch);
                skip();
                return true;
            }

            boolean imatch(Object... expects) {
                if (get() == -1)
                    return false;
                for (Object expect : expects)
                    if (expect instanceof Character ch) {
                        if (get() == ch)
                            return append(get());
                    } else if (expect instanceof IntPredicate p) {
                        if (p.test(get()))
                            return append(get());
                    } else
                        throw new IllegalArgumentException("expects: " + expect);
                return false;
            }

            boolean match(Object... expects) {
                spaces();
                return imatch(expects);
            }

            RuntimeException error(String format, Object... args) {
                return new RuntimeException(String.format(format, args));
            }

            IntPredicate DIGITS = c -> c >= '0' && c <= '9';
//            int[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

            void digits() {
                if (imatch(DIGITS))
                    while (imatch(DIGITS))
                        ;
                else
                    throw error("[0-9] expected");
            }

            Double number() {
                clear();
                match('-');
                digits();
                if (imatch('.'))
                    digits();
                if (imatch('e', 'E')) {
                    imatch('+', '-');
                    digits();
                }
                return Double.parseDouble(token.toString());
            }

            String string() {
                clear();
                L: while (true)
                    switch (get()) {
                    case -1: throw error("'\"' expected");
                    case '"': skip(); break L;
                    case '\\': skip();
                        switch (get()) {
                        case '\\': append('\\'); break;
                        case 'r': append('\r'); break;
                        case 'n': append('\n'); break;
                        case 't': append('\t'); break;
                        }
                        break;
                    default: append(get()); break;
                    }
                return token.toString();
            }

            Object element() {
                if (match('['))
                    return list();
                else if (match('"'))
                    return string();
                else
                    return number();
            }

            List<Object> list() {
                List<Object> list = new ArrayList<>();
                if (match(']'))
                    /* do nothing */;
                else {
                    list.add(element());
                    while (match(','))
                        list.add(element());
                    if (!match(']'))
                        throw error("']' expected");
                }
                return list;
            }

            List<Object> parse() {
                if (match('['))
                    return list();
                else
                    throw error("'[' expected");
            }
        }.parse();
    }

    @Test
    void test() {
        assertEquals(List.of(1.0, 2.0, 3.0), parse("[1, 2, 3]"));
        assertEquals(List.of(-123.0), parse("[ -123 ]"));
        assertEquals(List.of(), parse("[]"));
        assertEquals(List.of(1.0, List.of(2.0, 3.0), 4.0), parse("[1, [2, 3], 4]"));
        assertEquals(List.of(1.0, List.of(), 2.0), parse("[  1 , [  ] , 2 ]"));
        assertEquals(List.of("zero", 1.0, 2.0), parse("[  \"zero\" , 1, 2]"));
        assertEquals(List.of("ze\r\nro", 1.0, 2.0), parse("[  \"ze\\r\\nro\" , 1, 2]"));
        assertEquals(List.of("ze\tro"), parse("[  \"ze\\tro\" ]"));
    }

    @Test
    void testNumber() {
        assertEquals(List.of(12.0), parse("[ 12 ]"));
        assertEquals(List.of(12.345), parse("[ 12.345 ]"));
        assertEquals(List.of(12e3), parse("[ 12e3 ]"));
        assertEquals(List.of(12e3), parse("[ 12e+3 ]"));
        assertEquals(List.of(12e-3), parse("[ 12e-3 ]"));
    }

    @Test
    void testNumberError() {
        try {
            parse("[1.]");
            fail();
        } catch (RuntimeException e) {
            assertEquals("[0-9] expected", e.getMessage());
        }
        try {
            parse("[-.2]");
            fail();
        } catch (RuntimeException e) {
            assertEquals("[0-9] expected", e.getMessage());
        }
        try {
            parse("[-2e]");
            fail();
        } catch (RuntimeException e) {
            assertEquals("[0-9] expected", e.getMessage());
        }
    }

    @Test
    void testSyntaxError() {
        try {
            parse("1, 2, x");
            fail();
        } catch (RuntimeException e) {
            assertEquals("'[' expected", e.getMessage());
        }
        try {
            parse("[1, 2");
            fail();
        } catch (RuntimeException e) {
            assertEquals("']' expected", e.getMessage());
        }
        try {
            parse("[1, 2, x]");
            fail();
        } catch (RuntimeException e) {
            assertEquals("[0-9] expected", e.getMessage());
        }
        try {
            parse("[12  2]");
            fail();
        } catch (RuntimeException e) {
            assertEquals("']' expected", e.getMessage());
        }
    }
}
