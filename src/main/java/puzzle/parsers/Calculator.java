package puzzle.parsers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * expression = term { ( '+' | '-' ) term }
 * term       = factor { ( '*' | '/' ) factor }
 * factor     = primary [ ( '^' | '**' ) factor ]
 * primary    = [ '-' ] ( '(' expression ')' | NUMBER | constant | function )
 * constant   = IDENTIFIER
 * function   = IDENTIFIER '(' [ expression { ',' expression } ] ')'
 * NUMBER     = [0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?
 * @author saka1
 *
 */
public class Calculator {

    static final Pattern NUMBER = Pattern.compile(
        "^[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    static final Pattern IDENTIFIER = Pattern.compile(
        "\\p{IsLetter}[\\p{IsLetter}\\d_]*");

    static final Map<String, Double> CONSTANT_MAP = Map.of(
        "pi", Math.PI,
        "π", Math.PI,
        "e", Math.E
    );

    static RuntimeException error(String format, Object... args) {
        return new RuntimeException(String.format(format, args));
    }

    static record FuncDef(int numberOfArguments, Function<List<Double>, Double> function) {
        public Double apply(String name, List<Double> args) {
            if (args.size() != numberOfArguments)
                throw error("'%s' takes %d arguments but %d", name, numberOfArguments, args.size());
            return function.apply(args);
        }
    }

    static final Map<String, FuncDef> FUNCTION_MAP = new HashMap<>();
    static {
        FUNCTION_MAP.put("floor", new FuncDef(1, args -> Math.floor(args.get(0))));
        FUNCTION_MAP.put("ceil", new FuncDef(1, args -> Math.ceil(args.get(0))));
        FUNCTION_MAP.put("sin", new FuncDef(1, args -> Math.sin(args.get(0))));
        FUNCTION_MAP.put("cos", new FuncDef(1, args -> Math.cos(args.get(0))));
        FUNCTION_MAP.put("tan", new FuncDef(1, args -> Math.tan(args.get(0))));
        FUNCTION_MAP.put("asin", new FuncDef(1, args -> Math.asin(args.get(0))));
        FUNCTION_MAP.put("acos", new FuncDef(1, args -> Math.acos(args.get(0))));
        FUNCTION_MAP.put("atan", new FuncDef(1, args -> Math.atan(args.get(0))));
        FUNCTION_MAP.put("atan2", new FuncDef(2, args -> Math.atan2(args.get(0), args.get(1))));
        FUNCTION_MAP.put("hypot", new FuncDef(2, args -> Math.hypot(args.get(0), args.get(1))));
        FUNCTION_MAP.put("factorial", new FuncDef(1, args -> IntStream.rangeClosed(1, args.get(0).intValue())
            .mapToObj(i -> BigInteger.valueOf(i)).reduce(BigInteger.ONE, (a, b) -> a.multiply(b)).doubleValue()));
    }

    public static double calculate(Map<String, Double> context, String input) {
        return new Object() {
            int length = input.length();
            int index = 0;
            String token;

            void spaces() {
                while (index < length && Character.isWhitespace(input.charAt(index)))
                    ++index;
            }

            boolean eat(String... expects) {
                spaces();
                for (String expect : expects)
                    if (input.startsWith(expect, index)) {
                        token = expect;
                        index += token.length();
                        return true;
                    }
                return false;
            }

            boolean eat(Pattern pat) {
                spaces();
                Matcher matcher = pat.matcher(input.substring(index));
                if (!matcher.find())
                    return false;
                token = matcher.group();
                index += token.length();
                return true;
            }

            double paren() {
                double result = expression();
                if (!eat(")")) throw new RuntimeException("')' expected");
                return result;
            }

            double constantOrFunction() {
                String identifier = token;
                if (eat("(")) {
                    FuncDef funcDef = FUNCTION_MAP.get(identifier);
                    if (funcDef == null)
                        throw error("unknown function '%s'", identifier);
                    List<Double> arguments = new ArrayList<>();
                    if (!eat(")")) {
                        arguments.add(expression());
                        while (eat(","))
                            arguments.add(expression());
                        if (!eat(")")) throw error("')' expected");
                    }
                    return funcDef.apply(identifier, arguments);
                } else {
                    Double value = CONSTANT_MAP.get(identifier);
                    if (value != null)
                        return value;
                    value = context.get(identifier);
                    if (value != null)
                        return value;
                    throw error("unknown constant '%s'", identifier);
                }
            }

            /**
             * primary = ['-'] ('(' expression ')' | NUMBER | constant | function )
             * @return
             */
            double primary() {
                int sign = 1;
                if (eat("-")) sign = -1;
                if (eat("(")) return sign * paren();
                else if (eat(NUMBER)) return sign * Double.parseDouble(token);
                else if (eat(IDENTIFIER)) return sign * constantOrFunction();
                else throw error("unknown char '%c'", input.charAt(index));
            }

            /**
             * べき乗演算子(^または**)は右結合
             * @return
             */
            double factor() {
                double result = primary();
                if (eat("**", "^"))
                    result = Math.pow(result, factor());
                return result;
            }

            double term() {
                double result = factor();
                while (true)
                    if (eat("*")) result *= factor();
                    else if (eat("/")) result /= factor();
                    else break;
                return result;
            }

            double expression() {
                double result = term();
                while (true)
                    if (eat("+")) result += term();
                    else if (eat("-")) result -= term();
                    else break;
                return result;
            }

            double assignment() {
                int backup = index;
                if (eat(IDENTIFIER)) {
                    String name = token;
                    if (eat("=")) {
                        double value = assignment();
                        context.put(name, value);
                        return value;
                    }
                }
                index = backup;
                return expression();
            }

            double parse() {
                double result = assignment();
                if (index != length)
                    throw error("extra string '%s'", input.substring(index));
                return result;
            }
        }.parse();
    }
}
