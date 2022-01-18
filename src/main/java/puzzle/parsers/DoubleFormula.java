package puzzle.parsers;

import java.util.function.DoubleUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleFormula {
    static Pattern TERM = Pattern.compile(
        "([+-])?(?:(\\d+)?x(?:\\^(\\d+))?|(\\d+))");

    static DoubleUnaryOperator parse(String formula) {
        DoubleUnaryOperator y = x -> 0;
        Matcher m = TERM.matcher(formula.toLowerCase());
        while (m.find()) {
            DoubleUnaryOperator f = y;
            double sign = "-".equals(m.group(1)) ? -1 : 1;
            if (m.group(4) != null) {
                 double v = sign * Double.parseDouble(m.group(4));
                 y = x -> f.applyAsDouble(x) + v;
            } else {
                double coef = sign * (m.group(2) == null ? 1 : Double.parseDouble(m.group(2)));
                double exp = m.group(3) == null ? 1 : Double.parseDouble(m.group(3));
                y = x -> f.applyAsDouble(x) + coef * (float)Math.pow(x, exp);
            }
        }
        return y;
    }

    public static void main(String[] args) {
        DoubleUnaryOperator f = parse("+5x^2+x+2");
        for (double i = 0; i < 10; i++)
            System.out.println("f(" + i + ") = " + f.applyAsDouble(i));
    }
}
