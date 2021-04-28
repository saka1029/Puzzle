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
     * element = integer | list;
     * integer =  [ '-' ] digit { digit }
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

            boolean spaces() {
                while (isSpace(get()))
                    ++index;
                return true;
            }

            StringBuilder token = new StringBuilder();

            boolean clear() {
                token.setLength(0);
                return true;
            }

//            boolean match(IntPredicate... predicates) {
//                for (IntPredicate predicate : predicates)
//                    if (predicate.test(get())) {
//                        token.append((char) get());
//                        ++index;
//                        return true;
//                    }
//                return false;
//            }

            boolean append(int ch) {
                token.append((char)ch);
                ++index;
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

            Integer integer() {
                clear();
                match('-');
                if (imatch(DIGITS))
                    while (imatch(DIGITS))
                    /* do nothing */;
                else
                    throw error("digit expected but found '%s'", source.substring(index));
                return Integer.parseInt(token.toString());
            }

            String string() {
                clear();
                L: while (true)
                    switch (get()) {
                    case -1: throw error("'\"' expected");
                    case '"': ++index; break L;
                    case '\\': ++index;
                        switch (get()) {
                        case 'r': append('\r'); break;
                        case 'n': append('\n'); break;
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
                    return integer();
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
        assertEquals(List.of(1, 2, 3), parse("[1, 2, 3]"));
        assertEquals(List.of(-123), parse("[ -123 ]"));
        assertEquals(List.of(), parse("[]"));
        assertEquals(List.of(1, List.of(2, 3), 4), parse("[1, [2, 3], 4]"));
        assertEquals(List.of(1, List.of(), 2), parse("[  1 , [  ] , 2 ]"));
        assertEquals(List.of("zero", 1, 2), parse("[  \"zero\" , 1, 2]"));
        assertEquals(List.of("ze\r\nro", 1, 2), parse("[  \"ze\\r\\nro\" , 1, 2]"));
    }

    @Test
    void testError() {
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
            assertEquals("digit expected but found 'x]'", e.getMessage());
        }
        try {
            parse("[12  2]");
            fail();
        } catch (RuntimeException e) {
            assertEquals("']' expected", e.getMessage());
        }
    }
}
