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
public class Komachi {

    public static final int PLUS = -100, MINUS = -99, MULT = -98, DIV = -97;

    /**
     * SYNTAX
     * <pre>
     * expression = { ( '+' | '-' ) term }
     * term       = factor { ( '*' | '/' ) factor }
     * factor     = NUMBER
     * @return  評価結果の有理数
     */
    public static Rational eval(int[] terms, int tsize) {
        return new Object() {
            int index = 0;
            int token;

            int get() {
                return token = index < tsize ? terms[index++] : -999999;
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

    public static String string(int[] terms, int tsize) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < tsize; ++i) 
            sb.append(
                switch (terms[i]) {
                    case -100 -> "+";
                    case -99 -> "-";
                    case -98 -> "*";
                    case -97 -> "/";
                    default -> "" + terms[i];
                });
        return sb.toString();
    }

    public static void solve(int[] digits, int goal) {
        new Object() {
            Rational rgoal = Rational.of(goal);
            int[] terms = new int[digits.length * 2 + 1];
            int tsize = 0, count = 0;

            void push(int value) {
                terms[tsize++] = value;
            }

            void solve(int i, int term) {
                if (i >= digits.length) {
                    push(term);     // the last term
                    if (eval(terms, tsize).equals(rgoal)) 
                        System.out.println(++count + ": " + string(terms, tsize));
            } else {
                    int backup = tsize;
                    push(term); push(PLUS); solve(i + 1, digits[i]); tsize = backup;
                    push(term); push(MINUS); solve(i + 1, digits[i]); tsize = backup;
                    if (i > 0) {
                        push(term); push(MULT); solve(i + 1, digits[i]); tsize = backup;
                        push(term); push(DIV); solve(i + 1, digits[i]); tsize = backup;
                        solve(i + 1, term * 10 + digits[i]);
                    }
                }
            }
        }.solve(0, 0);
    }
}
