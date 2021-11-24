package puzzle.parsers;

import java.util.HashMap;
import java.util.Map;

public class IntFormula {

    interface Formula {
        int calc();
        default Formula add(Formula r) {
            return () -> calc() + r.calc();
        }
        default Formula sub(Formula r) {
            return () -> calc() - r.calc();
        }
        default Formula mul(Formula r) {
            return () -> calc() * r.calc();
        }
        default Formula pow(Formula r) {
            return () -> (int)Math.pow(calc(), r.calc());
        }
    }

    public static Formula parse(String s, Map<String, Integer> variables) {
        return new Object() {
            int length = s.length(), index = 0, ch = get(), token;

            int get() {
                return ch = index < length ? s.charAt(index++) : -1;
            }

            void spaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            boolean eat(int... expected) {
                spaces();
                for (int e : expected)
                    if (ch == e) {
                        token = ch;
                        get();
                        return true;
                    }
                return false;
            }

            Formula element() {
                Formula f;
                if (eat('(')) {
                    f = expression();
                    if (!eat(')'))
                        throw new RuntimeException("')' expected");
                } else if (eat('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')) {
                    int value = Character.digit(token, 10);
                    f = () -> value;
                } else if (eat('X', 'Y', 'Z')) {
                    String v = Character.toString(token);
                    f = () -> variables.get(v);
                } else
                    throw new RuntimeException("unknown char '" + (char)ch + "'");
                return f;
            }

            Formula power() {
                Formula f = element();
                while (eat('^'))
                    f = f.pow(element());
                return f;
            }

            Formula term() {
                Formula f = power();
                while (eat('*'))
                    f = f.mul(power());
                return f;
            }

            Formula expression() {
                Formula f = term();
                while (eat('+', '-'))
                    f = token == '+' ? f.add(term()) : f.sub(term());
                return f;
            }

            Formula parse() {
                Formula f = expression();
                return f;
            }

        }.parse();
    }

    public static void main(String[] args) {
        Map<String, Integer> variables = new HashMap<>();
        Formula f = parse("X^2 + 3*X + 1", variables);
        variables.put("X", 2);
        int result = f.calc();
        System.out.println(result);
    }

}
