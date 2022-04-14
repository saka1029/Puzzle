package test.puzzle.parsers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 *
 * Parse Boolean Expression in Java - Stack Overflow
 * https://stackoverflow.com/questions/69292555/parse-boolean-expression-in-java/69294374#69294374
 *
 */
public class TestBooleanExpressionParser {

    /**
     * トークン化を行わないパーサです。
     * "and"や"or"で始まる識別子を"and", "or"と誤認識する可能性があります。
     * 構文解析の結果としてPredicate<String>を返します。
     * これは与えられた文字列がmapで定義されている変数の値を含むかどうかを返す
     * Predicateです。
     */
    static Predicate<String> parse0(String s, Map<String, String> map) {
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
    public void testParse0() {
        String s = "not ((t1 and not t3) or (t4 and t2)) or t5";
        // String s = "(not (t1 and not t3) and not (t4 and t2)) or t5";
        // String s = "(not t1 or t3) and not t4 and not t2 or t5";
        Map<String, String> map = Map.of(
            "t1", "str1",
            "t2", "str2",
            "t3", "str3",
            "t4", "str4",
            "t5", "str5");
        Predicate<String> p = parse0(s, map);
        assertTrue(p.test("str5"));
        assertTrue(p.test("str3"));
        assertTrue(p.test("str1 str3"));
        assertFalse(p.test("str1"));
        assertFalse(p.test("str2 str4"));
    }

    /**
     * toStringで文字列表現を返すことのできるPredicate<String>の
     * 実装です。
     */
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
                "(" + string + " and " + c.string + ")");
        }

        Contains or(Contains c) {
            return new Contains(predicate.or(c.predicate), "(" + string + " or " + c.string + ")");
        }

        Contains not() {
            return new Contains(predicate.negate(), "not (" + string + ")");
        }
    }

    /**
     * トークン化を行わないパーサ。
     * "and"や"or"で始まる識別子を"and", "or"と誤認識する可能性があります。
     * 返すPredicate<String>はtoString()で意味のある文字列に変換できます。
     */
    static Contains parse2(String source, Map<String, String> variables) {
        enum Token {
            LP, RP, AND, OR, NOT, IDENTIFIER, EOS
        }
        return new Object() {
            int length = source.length();
            int index = 0;
            Token token = getToken();
            String tokenString;

            int getCh() {
                return index < length ? source.charAt(index) : -1;
            }

            RuntimeException error(String format, Object... args) {
                return new RuntimeException(String.format(format, args));
            }

            Token getToken() {
                while (Character.isWhitespace(getCh()))
                    ++index;
                if (index >= length)
                    return token = Token.EOS;
                switch (getCh()) {
                case '(':
                    ++index;
                    return token = Token.LP;
                case ')':
                    ++index;
                    return token = Token.RP;
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
                            return token = Token.AND;
                        case "or":
                            return token = Token.OR;
                        case "not":
                            return token = Token.NOT;
                        default:
                            return token = Token.IDENTIFIER;
                        }
                    } else
                        throw error("unknown character '%c'", getCh());
                }
            }

            Contains identifier() {
//                String v = variables.get(tokenString);
//                if (v == null)
//                    throw error("variable '%s' not defined", tokenString);
//                return new Contains(s -> s.contains(v), "s -> s.contains(" + v + ")");
                String identifier = tokenString;
                return new Contains(s -> {
                    String value = variables.get(identifier);
                    if (value == null)
                        throw error("variable '%s' not defined", identifier);
                    return s.contains(value);
                }, identifier);
            }

            Contains factor() {
                boolean not = false;
                Contains r;
                if (token == Token.NOT) {
                    getToken();
                    not = true;
                }
                if (token == Token.LP) {
                    getToken();
                    r = expression();
                    if (token != Token.RP)
                        throw error("')' expected");
                    getToken();
                } else if (token == Token.IDENTIFIER) {
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
                while (token == Token.AND) {
                    getToken();
                    r = r.and(factor());
                }
                return r;
            }

            Contains expression() {
                Contains r = term();
                while (token == Token.OR) {
                    getToken();
                    r = r.or(term());
                }
                return r;
            }

            Contains parse() {
                Contains result = expression();
                if (token != Token.EOS)
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
        Map<String, String> map = new HashMap<>(Map.of(
            "t1", "str1",
            "t2", "str2",
            "t3", "str3",
            "t4", "str4",
            "t5", "str5"));
        Predicate<String> p = parse2(s, map);
        System.out.println(p);
        assertTrue(p.test("str5"));
        assertTrue(p.test("str3"));
        assertTrue(p.test("str1 str3"));
        assertFalse(p.test("str1"));
        assertFalse(p.test("str2 str4"));
        map.put("t5", "FOO");
        assertTrue(p.test("FOO"));
        assertTrue(p.test("str3"));
    }

    enum TokenType { LP, RP, AND, OR, NOT, IDENTIFIER, END }

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

    /**
     * トークン化を独立させたパーサです。
     */
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

    @Test
    public void testSplit() {
        String s = "not ((t1 and not t3) or (t4 and t2)) or t5";
        List<String> list = Pattern.compile("(?i)[()]|[a-z][a-z0-9]*")
            .matcher(s).results()
            .map(MatchResult::group)
            .toList();
        System.out.println(list);
    }

    /**
     * 正規表現によってトークン化を行うパーサです。
     * 遅延評価をするので、後からmapの値を変更することができます。
     * expression = term { "or" term }.
     * term       = factor { "and" factor }.
     * factor     = [ "not" ] ( "(" expression ")" | identifier ).
     */
    static Predicate<String> parse(String s, Map<String, String> map) {
        return new Object() {
            String[] tokens = Pattern.compile("[()]|[a-z][a-z0-9]*")
                .matcher(s).results()
                .map(MatchResult::group)
                .toArray(String[]::new);
            int length = tokens.length;
            int index = 0;
            String token = get();

            String get() {
                return token = index < length ? tokens[index++] : null;
            }

            boolean eat(String expect) {
                if (expect.equals(token)) {
                    get();
                    return true;
                }
                return false;
            }

            Predicate<String> identifier() {
                String id = token;
                return s -> {
                    String value = map.get(id);
                    if (value == null)
                        throw new RuntimeException(
                            "identifier '" + id + "' undefined");
                    return s.contains(value);
                };
            }

            Predicate<String> factor() {
                boolean not = false;
                Predicate<String> p;
                if (eat("not"))
                    not = true;
                switch (token) {
                case "(":
                    get();
                    p = expression();
                    if (!eat(")"))
                        throw new RuntimeException("')' expected");
                    break;
                case ")": case "not": case "and": case "or":
                    throw new RuntimeException("syntax error at '" + token + "'");
                default:
                    p = identifier();
                    get();
                    break;
                }
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
                if (token != null)
                    throw new RuntimeException("extra tokens string");
                return p;
            }
        }.parse();
    }

    @Test
    public void testParse() {
        String s = "not ((t1 and not t3) or (t4 and t2)) or t5";
        Map<String, String> map = new HashMap<>(Map.of(
            "t1", "str1",
            "t2", "str2",
            "t3", "str3",
            "t4", "str4",
            "t5", "str5"));
        Predicate<String> p = parse(s, map);
        assertTrue(p.test("str5"));
        assertTrue(p.test("str3"));
        assertTrue(p.test("str1 str3"));
        assertFalse(p.test("str1"));
        assertFalse(p.test("str2 str4"));
        // you can change value of variables.
        assertFalse(p.test("str1 FOO"));
        map.put("t5", "FOO");
        assertTrue(p.test("str1 FOO"));
    }

    /**
     * Predicate<T>のand(Predicate<T> r)とor(Predicate<T> r)
     * はショートカット演算子です。
     */
    @Test
    public void testPredicateAndOr() {
        Predicate<String> t = s -> {
            System.out.println("t called");
            return true;
        };
        Predicate<String> f = s -> {
            System.out.println("f called");
            return false;
        };
        System.out.println("true or true");
        t.or(t).test(null);
        System.out.println("false or true");
        f.or(t).test(null);
        System.out.println("true and true");
        t.and(t).test(null);
        System.out.println("false and true");
        f.and(t).test(null);
    }

}
