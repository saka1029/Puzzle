package puzzle.parsers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Equations {

    public interface Expression {
        int value(Map<Variable, Expression> equations, Map<Variable, Integer> values);
    }

    public record Variable(String name) implements Expression {
        @Override
        public int value(Map<Variable, Expression> equations, Map<Variable, Integer> values) {
            Integer value = values.get(this);
            if (value == null) {
                Expression e = equations.get(this);
                if (e == null)
                    throw new RuntimeException("variable not defined: " + name);
                values.put(this, value = e.value(equations, values));
            }
            return value;
        }

        @Override
        public boolean equals(Object object) {
            if (object == null)
                return false;
            if (this == object)
                return true;
            if (object instanceof Variable obj)
                return obj.name.equals(name);
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public record Number(int value) implements Expression {
        @Override
        public int value(Map<Variable, Expression> vars, Map<Variable, Integer> values) {
            return value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }

    public record Binary(String operator, Expression left, Expression right) implements Expression {
        @Override
        public int value(Map<Variable, Expression> equations, Map<Variable, Integer> values) {
            int leftValue = left.value(equations, values);
            int rightValue = right.value(equations, values);
            switch (operator) {
            case "+": return leftValue + rightValue;
            case "-": return leftValue - rightValue;
            case "*": return leftValue * rightValue;
            case "/": return leftValue / rightValue;
            default: throw new RuntimeException("unknown operator: " + operator);
            }
        }

        @Override
        public String toString() {
            return String.format("(%s %s %s)", operator, left, right);
        }
    }

    public record Unary(String operator, Expression left) implements Expression {
        @Override
        public int value(Map<Variable, Expression> equations, Map<Variable, Integer> values) {
            int leftValue = left.value(equations, values);
            switch (operator) {
            case "-": return -leftValue;
            default: throw new RuntimeException("unknown operator: " + operator);
            }
        }

        @Override
        public String toString() {
            return String.format("(%s %s)", operator, left);
        }
    }

    public static Pattern VARIABLE = Pattern.compile("^\\p{L}+[\\p{L}\\p{Digit}\uFF10-\uFF19_]*");
    public static Pattern NUMBER = Pattern.compile("^\\d+");

    public static Map<Variable, Expression> parse(String source) {
        Map<Variable, Expression> equations = new HashMap<>();

        new Object() {
            int index = 0;
            String matched;
            Map<String, Variable> variables = new HashMap<>();

            Variable variable(String name) {
                return variables.computeIfAbsent(name, k -> new Variable(k));
            }

            RuntimeException error(String format, Object... args) {
                return new RuntimeException(String.format(format, args));
            }

            void spaces() {
                while (index < source.length() && Character.isWhitespace(source.charAt(index)))
                    ++index;
            }

            boolean match(Pattern e) {
                spaces();
                Matcher m = e.matcher(source.substring(index));
                if (m.find()) {
                    matched = m.group();
                    index += matched.length();
                    return true;
                }
                return false;
            }

            boolean match(String e) {
                spaces();
                if (source.startsWith(e, index)) {
                    matched = e;
                    index += matched.length();
                    return true;
                }
                return false;
            }

            Expression factor() {
                Expression expression;
                int sign = 1;
                if (match("-"))
                    sign = -1;
                if (match(NUMBER))
                    expression = new Number(Integer.parseInt(matched));
                else if (match(VARIABLE))
                    expression = variable(matched);
                else if (match("(")) {
                    expression = expression();
                    if (!match(")"))
                        throw error("')' expected");
                } else
                    throw error("factor expected");
                return sign == -1 ? new Unary("-", expression) : expression;
            }

            Expression term() {
                Expression left = factor();
                while (true)
                    if (match("*"))
                        left = new Binary(matched, left, factor());
                    else if (match("/"))
                        left = new Binary(matched, left, factor());
                    else
                        break;
                return left;
            }

            Expression expression() {
                Expression left = term();
                while (true)
                    if (match("+"))
                        left = new Binary(matched, left, term());
                    else if (match("-"))
                        left = new Binary(matched, left, term());
                    else
                        break;
                return left;
            }

            Variable assign() {
                if (match(VARIABLE)) {
                    String name = matched;
                    if (match("=")) {
                        Variable var = variable(name);
                        equations.put(var, expression());
                        return var;
                    } else
                        throw error("'=' expected");
                } else
                    throw error("variable expected");
            }

            void parse() {
                while (index < source.length())
                    assign();
                for (Variable v : variables.values())
                    if (!equations.containsKey(v))
                        throw error("variable not define: " + v);
            }
        }.parse();
        return equations;
    }





}
