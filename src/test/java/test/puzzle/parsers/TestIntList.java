package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;

class TestIntList {

    /**
     * <pre>
     * list    = '[' [ integer { ',' integer } ] ']'
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

            void spaces() {
                while (isSpace(get()))
                    ++index;
            }

            StringBuilder token = new StringBuilder();

            boolean clear() {
                token.setLength(0);
                return true;
            }

            boolean match(IntPredicate... predicates) {
                spaces();
                for (IntPredicate predicate : predicates)
                    if (predicate.test(get())) {
                        token.append((char) get());
                        ++index;
                        return true;
                    }
                return false;
            }

            boolean match(int... expects) {
                spaces();
                for (int expect : expects)
                    if (get() == expect) {
                        token.append((char) get());
                        ++index;
                        return true;
                    }
                return false;
            }

            RuntimeException error(String format, Object... args) {
                return new RuntimeException(String.format(format, args));
            }

            IntPredicate digit = c -> c >= '0' && c <= '9';
//            int[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

            Integer integer() {
                clear();
                match('-');
                if (match(digit))
                    while (match(digit))
                    /* do nothing */;
                else
                    throw error("digit expected but '" + ((char) get()) + "'");
                return Integer.parseInt(token.toString());
            }

            Object element() {
                if (match('['))
                    return list();
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
        assertEquals(List.of(), parse("[]"));
        assertEquals(List.of(1, List.of(2, 3), 4), parse("[1, [2, 3], 4]"));
        assertEquals(List.of(1, List.of(), 2), parse("[  1 , [  ] , 2 ]"));
    }

}
