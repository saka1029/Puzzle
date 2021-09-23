package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

            int getCh() {
                return index < length ? s.charAt(index) : -1;
            }

            boolean eat(String token) {
                while (Character.isWhitespace(getCh()))
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
                    sb.append((char) getCh());
                    ++index;
                } while (Character.isAlphabetic(getCh())
                    || Character.isDigit(getCh()));
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
                } else if (Character.isAlphabetic(getCh())) {
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
        // String s = "(not (t1 and not t3) and not (t4 and t2)) or t5";
        // String s = "(not t1 or t3) and not t4 and not t2 or t5";
        Map<String, String> map = Map.of(
            "t1", "str1",
            "t2", "str2",
            "t3", "str3",
            "t4", "str4",
            "t5", "str5");
        Predicate<String> p = parse(s, map);
        assertTrue(p.test("str5"));
        assertTrue(p.test("str3"));
        assertTrue(p.test("str1 str3"));
        assertFalse(p.test("str1"));
        assertFalse(p.test("str2 str4"));
    }

    record Contains(Predicate<String> predicate, String string) implements Predicate<String> {

        @Override
        public boolean test(String t) {
            return predicate.test(t);
        }

        @Override
        public String toString() {
            return string;
        }

        Contains and(Contains c) {
            return new Contains(predicate.and(c.predicate),
                "and(" + string + ", " + c.string + ")");
        }

        Contains or(Contains c) {
            return new Contains(predicate.or(c.predicate), "or(" + string + ", " + c.string + ")");
        }

        Contains not() {
            return new Contains(predicate.negate(), "not(" + string + ")");
        }
    }

    static Contains parse2(String source, Map<String, String> variables) {
        enum TokenType {
            LP, RP, AND, OR, NOT, IDENTIFIER, EOS
        }
        return new Object() {
            int length = source.length();
            int index = 0;
            TokenType token = getToken();
            String tokenString;

            int getCh() {
                return index < length ? source.charAt(index) : -1;
            }

            RuntimeException error(String format, Object... args) {
                return new RuntimeException(String.format(format, args));
            }

            TokenType getToken() {
                while (Character.isWhitespace(getCh()))
                    ++index;
                if (index >= length)
                    return token = TokenType.EOS;
                switch (getCh()) {
                case '(':
                    ++index;
                    return token = TokenType.LP;
                case ')':
                    ++index;
                    return token = TokenType.RP;
                default:
                    if (Character.isAlphabetic(getCh())) {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append((char) getCh());
                            ++index;
                        } while (Character.isAlphabetic(getCh()) || Character.isDigit(getCh()));
                        tokenString = sb.toString();
                        switch (tokenString) {
                        case "and":
                            return token = TokenType.AND;
                        case "or":
                            return token = TokenType.OR;
                        case "not":
                            return token = TokenType.NOT;
                        default:
                            return token = TokenType.IDENTIFIER;
                        }
                    } else
                        throw error("unknown character '%c'", getCh());
                }
            }

            Contains identifier() {
                String v = variables.get(tokenString);
                if (v == null)
                    throw error("variable '%s' not defined", tokenString);
                return new Contains(s -> s.contains(v), "s -> s.contains(" + v + ")");
            }

            Contains factor() {
                boolean not = false;
                Contains r;
                if (token == TokenType.NOT) {
                    getToken();
                    not = true;
                }
                if (token == TokenType.LP) {
                    getToken();
                    r = expression();
                    if (token != TokenType.RP)
                        throw error("')' expected");
                    getToken();
                } else if (token == TokenType.IDENTIFIER) {
                    r = identifier();
                    getToken();
                } else
                    throw error("unknown token '%s'", token);
                if (not)
                    r = r.not();
                return r;
            }

            Contains term() {
                Contains r = factor();
                while (token == TokenType.AND) {
                    getToken();
                    r = r.and(factor());
                }
                return r;
            }

            Contains expression() {
                Contains r = term();
                while (token == TokenType.OR) {
                    getToken();
                    r = r.or(term());
                }
                return r;
            }

            Contains parse() {
                Contains result = expression();
                if (token != TokenType.EOS)
                    throw error("extra token " + token);
                return result;
            }
        }.parse();
    }

    @Test
    public void testParse2() {
        String s = "not ((t1 and not t3) or (t4 and t2)) or t5";
        // String s = "(not (t1 and not t3) and not (t4 and t2)) or t5";
        // String s = "(not t1 or t3) and not t4 and not t2 or t5";
        Map<String, String> map = Map.of(
            "t1", "str1",
            "t2", "str2",
            "t3", "str3",
            "t4", "str4",
            "t5", "str5");
        Predicate<String> p = parse2(s, map);
        System.out.println(p);
        assertTrue(p.test("str5"));
        assertTrue(p.test("str3"));
        assertTrue(p.test("str1 str3"));
        assertFalse(p.test("str1"));
        assertFalse(p.test("str2 str4"));
    }

    enum TokenType {
        LP, RP, AND, OR, NOT, IDENTIFIER, END
    }

    static class Tokenizer {
        final String source;
        final int length;
        int index;
        int ch;
        public TokenType token;
        public String value;
        final StringBuilder sb = new StringBuilder();

        Tokenizer(String source) {
            this.source = source;
            this.length = source.length();
            this.index = 0;
            getCh();
            get();
        }

        int getCh() {
            return ch = index < length ? source.charAt(index++) : -1;
        }

        public static RuntimeException error(String format, Object... args) {
            return new RuntimeException(String.format(format, args));
        }

        TokenType get0() {
            while (Character.isWhitespace(ch))
                getCh();
            sb.setLength(0);
            value = "";
            if (ch == -1)
                return TokenType.END;
            switch (ch) {
            case '(': getCh(); return TokenType.LP;
            case ')': getCh(); return TokenType.RP;
            default:
                if (Character.isAlphabetic(ch)) {
                    do {
                        sb.append((char)ch);
                        getCh();
                    } while (Character.isAlphabetic(ch)
                        || Character.isDigit(ch));
                    value = sb.toString();
                    switch (value) {
                    case "and": return TokenType.AND;
                    case "or": return TokenType.OR;
                    case "not": return TokenType.NOT;
                    default: return TokenType.IDENTIFIER;
                    }
                } else
                    throw error("unknown char '%c'", (char)ch);
            }
        }

        public TokenType get() {
            return token = get0();
        }

        public boolean eat(TokenType expected) {
            if (token == expected) {
                get();
                return true;
            }
            return false;
        }
    }

    static Contains parse3(String source, Map<String, String> variables) {
        return new Object() {
            Tokenizer tokenizer = new Tokenizer(source);

            Contains identifier() {
                String v = variables.get(tokenizer.value);
                if (v == null)
                    throw Tokenizer.error("variable '%s' not defined", tokenizer.value);
                return new Contains(s -> s.contains(v), "s -> s.contains(" + v + ")");
            }

            Contains factor() {
                boolean not = false;
                Contains r;
                if (tokenizer.eat(TokenType.NOT))
                    not = true;
                if (tokenizer.eat(TokenType.LP)) {
                    r = expression();
                    if (!tokenizer.eat(TokenType.RP))
                        throw Tokenizer.error("')' expected");
                } else if (tokenizer.token == TokenType.IDENTIFIER) {
                    r = identifier();
                    tokenizer.get();
                } else
                    throw Tokenizer.error("unknown token '%s'", tokenizer.token);
                if (not)
                    r = r.not();
                return r;
            }

            Contains term() {
                Contains r = factor();
                while (tokenizer.eat(TokenType.AND))
                    r = r.and(factor());
                return r;
            }

            Contains expression() {
                Contains r = term();
                while (tokenizer.eat(TokenType.OR))
                    r = r.or(term());
                return r;
            }

            Contains parse() {
                Contains result = expression();
                if (!tokenizer.eat(TokenType.END))
                    throw Tokenizer.error("extra token " + tokenizer.token);
                return result;
            }
        }.parse();
    }

    @Test
    public void testParse3() {
        String s = "not ((t1 and not t3) or (t4 and t2)) or t5";
        // String s = "(not (t1 and not t3) and not (t4 and t2)) or t5";
        // String s = "(not t1 or t3) and not t4 and not t2 or t5";
        Map<String, String> map = Map.of(
            "t1", "str1",
            "t2", "str2",
            "t3", "str3",
            "t4", "str4",
            "t5", "str5");
        Predicate<String> p = parse3(s, map);
        System.out.println(p);
        assertTrue(p.test("str5"));
        assertTrue(p.test("str3"));
        assertTrue(p.test("str1 str3"));
        assertFalse(p.test("str1"));
        assertFalse(p.test("str2 str4"));
    }
}
