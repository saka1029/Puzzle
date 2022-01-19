package puzzle.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleFormula {
    interface DoubleF {
        double apply(double x);
        default DoubleF add(DoubleF f) { return x -> apply(x) + f.apply(x); }
    }

    static Pattern TERM = Pattern.compile(
        "([+-])?(?:(\\d+)?x(?:\\^(\\d+))?|(\\d+))");

    /**
     * <pre>
     * SYNTAX:
     *   formula  = term { term }
     *   term     = [ "+" | "-" ] ( variable | constant )
     *   variable = [ DIGITS ] "x" [ "^" DIGITS ]
     *   constant = DIGITS
     * </pre>
     * SYNTAX:
     */
    static DoubleF parse(String formula) {
        DoubleF f = x -> 0;
        Matcher m = TERM.matcher(formula.toLowerCase());
        while (m.find()) {
            double sign = "-".equals(m.group(1)) ? -1 : 1;
            if (m.group(4) != null) {
                 double c = sign * Double.parseDouble(m.group(4));
                 f = f.add(x -> c);
            } else {
                double c = sign * (m.group(2) == null ? 1 : Double.parseDouble(m.group(2)));
                double e = m.group(3) == null ? 1 : Double.parseDouble(m.group(3));
                f = f.add(x -> c * (float)Math.pow(x, e));
            }
        }
        return f;
    }

    public static void main(String[] args) {
        DoubleF f = parse("+5x^2+x+2");
        for (double i = 0; i < 10; i++)
            System.out.println("f(" + i + ") = " + f.apply(i));
    }
}
