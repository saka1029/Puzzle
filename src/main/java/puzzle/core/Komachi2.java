package puzzle.core;

/**
 * 【再帰版】
 * 【有理数対応版】
 * 小町算（こまちざん）は、数の遊びである数学パズルの一種。
 * 1□2□3□4□5□6□7□8□9 = 100 という数式の□の中に、
 * +, -, ×, ÷, 空白 のいずれかを一つずつ入れて正しい数式を完成させるというものである。
 * 数字の列は配列で与えられるものとする。
 *
 * 9桁の5進数をコードされた計算式として扱う。
 * 各桁の意味は以下の通り。
 * <pre>
 * 0 : 前の桁に追加したものを合計に加算する。
 * 1 : 前の桁に追加したものを合計から減算する。
 * 2 : 前の桁に追加したものを合計に乗算する。
 * 3 : 前の桁に追加したものを合計から除算する。
 * 4 : 前の桁に追加する。
 * </pre>
 */
public class Komachi2 {

    public static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;

    /**
     * SYNTAX
     * <pre>
     * expression = { ( '+' | '-' ) term }
     * term       = factor { ( '*' | '/' ) factor }
     * factor     = NUMBER
     * @return  評価結果の有理数
     */
    public static Rational eval(Cons<Integer> termsOrg) {
        return new Object() {
            Cons<Integer> terms = termsOrg.reverse();
            int token;

            int get() {
                if (terms.isEmpty())
                    return token = -999999;
                token = terms.car();
                terms = terms.cdr();
                return token;
            }

            boolean eat(int expected) {
                if (token == expected) {
                    get();
                    return true;
                }
                return false;
            }

            Rational factor() {
                Rational value = Rational.of(token);
                get();
                return value;
            }

            Rational term() {
                Rational value = factor();
                while (true)
                    if (eat(MULT))
                        value = value.multiply(factor());
                    else if (eat(DIV))
                        value = value.divide(factor());
                    else
                        break;
                return value;
            }

            Rational expression() {
                Rational value = term();
                while (true)
                    if (eat(PLUS))
                        value = value.add(term());
                    else if (eat(MINUS))
                        value = value.subtract(term());
                    else
                        break;
                return value;
            }

            Rational parse() {
                get();
                return expression();
            }
        }.parse();
    }

    public static String string(Cons<Integer> terms) {
        StringBuilder sb = new StringBuilder();
        for (int term : terms.reverse().cdr())
            sb.append(
                switch (term) {
                    case -100 -> "+";
                    case -99 -> "-";
                    case -98 -> "*";
                    case -97 -> "/";
                    default -> "" + term;
                });
        return sb.toString();
    }

    public static void solve(int[] digits, int goal) {
        new Object() {
            Rational rgoal = Rational.of(goal);
            int count = 0;

            void solve(int i, int term, Cons<Integer> terms) {
                if (i >= digits.length) {
                    terms = terms.cons(term);     // the last term
                    if (eval(terms).equals(rgoal)) 
                        System.out.println(++count + ": " + string(terms));
            } else {
                    solve(i + 1, digits[i], terms.cons(term).cons(PLUS));
                    solve(i + 1, digits[i], terms.cons(term).cons(MINUS));
                    if (i > 0) {
                        solve(i + 1, digits[i], terms.cons(term).cons(MULT));
                        solve(i + 1, digits[i], terms.cons(term).cons(DIV));
                        solve(i + 1, term * 10 + digits[i], terms);
                    }
                }
            }
        }.solve(0, 0, Cons.nil());
    }
}
