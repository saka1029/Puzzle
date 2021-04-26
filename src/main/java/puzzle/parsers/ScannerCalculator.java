package puzzle.parsers;

import java.util.HashMap;
import java.util.Map;

public class ScannerCalculator {

    Scanner s;
    Map<String, Double> variables = new HashMap<>();

    double paren() {
        double result = expression();
        if (!s.match(")")) throw s.error("')' expected");
        return result;
    }

    double factor() {
        int sign = 1;
        if (s.match("-")) sign = -1;
        if (s.match("(")) return sign * paren();
        else if (s.realNumber()) return sign * Double.parseDouble(s.token);
        else if (s.identifier()) return sign * variables.get(s.token);
        else throw s.error("unknown character '%c'", s.top());
    }

    double term() {
        double result = factor();
        while (true)
            if (s.match("*")) result *= factor();
            else if (s.match("/")) result /= factor();
            else break;
        return result;
    }

    double expression() {
        double result = term();
        while (true)
            if (s.match("+")) result += term();
            else if (s.match("-")) result -= term();
            else break;
        return result;
    }

    double assignment() {
        int backup = s.index;
        if (s.identifier()) {
            String name = s.token;
            if (s.match("=")) {
                double value = assignment();
                variables.put(name, value);
                return value;
            }
        }
        s.index = backup;
        return expression();
    }

    public double calculate(String source) {
        s = new Scanner(source);
        double result = assignment();
        if (s.index < source.length())
            throw s.error("extra string '" + source.substring(s.index) + "'");
        return result;
    }
}
