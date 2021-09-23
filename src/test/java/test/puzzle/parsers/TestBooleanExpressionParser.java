package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

/**
 * 
 * Parse Boolean Expression in Java - Stack Overflow
 * https://stackoverflow.com/questions/69292555/parse-boolean-expression-in-java/69294374#69294374
 *
 */
class TestBooleanExpressionParser {

    static Predicate<String> parse(String s, Map<String, String> map) {
        return new Object() {
			int length = s.length();
            int index = 0;

            int ch() {
                return index < length ? s.charAt(index) : -1;
            }

            boolean eat(String token) {
                while (Character.isWhitespace(ch()))
                    ++index;
                if (s.startsWith(token, index)) {
                    index += token.length();
                    return true;
                }
                return false;
            }
            
            Predicate<String> identifier() {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append((char)ch());
                    ++index;
                } while (Character.isAlphabetic(ch())
                    || Character.isDigit(ch()));
                String id = sb.toString();
                String value = map.get(id);
                if (value == null)
                    throw new RuntimeException(
                        "identifier '" + id + "' undefined");
                return s -> s.contains(value);
            }
            
            Predicate<String> factor() {
                boolean not = false;
                Predicate<String> p;
                if (eat("not"))
                    not = true;
                if (eat("(")) {
                    p = expression();
                    if (!eat(")"))
                        throw new RuntimeException("')' expected");
                } else if (Character.isAlphabetic(ch())) {
                    p = identifier();
                } else
                    throw new RuntimeException("unknown token");
                if (not)
                    p = p.negate();
                return p;
            }
            
            Predicate<String> term() {
                Predicate<String> p = factor();
                while (eat("and"))
                    p = p.and(factor());
                return p;
            }

            Predicate<String> expression() {
                Predicate<String> p = term();
                while (eat("or"))
                    p = p.or(term());
                return p;
            }

            Predicate<String> parse() {
                Predicate<String> p = expression();
                if (index < length)
                    throw new RuntimeException(
                        "extra string '" + s.substring(index) + "' found");
                return p;
            }
        }.parse();
    }

    @Test
    public void testParse() {
        String s = "not ((t1 and not t3) or (t4 and t2)) or t5";
//      String s = "(not (t1 and not t3) and not (t4 and t2)) or t5";
//      String s = "(not t1 or t3) and not t4 and not t2 or t5";
        Map<String, String> map = Map.of(
            "t1", "str1",
            "t2", "str2",
            "t3", "str3",
            "t4", "str4",
            "t5", "str5"
        );
        Predicate<String> p = parse(s, map);
        assertTrue(p.test("str5"));
        assertTrue(p.test("str3"));
        assertTrue(p.test("str1 str3"));
        assertFalse(p.test("str1"));
        assertFalse(p.test("str2 str4"));
    }

}
