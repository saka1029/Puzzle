package puzzle.parsers;

public class BooleanExpression {

    /**
     * expression = or
     * or         = and { '||' and }
     * and        = factor { '&&' factor }
     * factor     = paren | comp
     * paren      = '(' expression ')'
     * comp       = number op number
     * op         = '==' | '!=' | '<' | '<= | '>' | '>='
     */
    static boolean eval(String expression) {
        return new Object() {
            int index = 0;
            int ch = get();

            int get() {
                return ch = index < expression.length() ? expression.charAt(index++) : -1;
            }

            void spaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            boolean eat(String word) {
                spaces();
                if (ch != word.charAt(0) || !expression.startsWith(word.substring(1), index))
                    return false;
                index += word.length() - 1;
                get();
                return true;
            }

            int number() {
                spaces();
                if (ch != '-' && !Character.isDigit(ch))
                    throw new RuntimeException(
                        "number expected but '" + ((char)ch) + "'");
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append((char)ch);
                    get();
                } while (Character.isDigit(ch));
                return Integer.parseInt(sb.toString());
            }

            boolean paren() {
                boolean result = or();
                if (!eat(")")) throw new RuntimeException("')' expected");
                return result;
            }

            boolean comp() {
                if (eat("(")) return paren();
                int term = number();
                if (eat("==")) return term == number();
                else if (eat("!=")) return term != number();
                else if (eat("<=")) return term <= number();
                else if (eat("<")) return term < number();
                else if (eat(">=")) return term >= number();
                else if (eat(">")) return term > number();
                else throw new RuntimeException();
            }

            boolean and() {
                boolean result = comp();
                while (eat("&&"))
                    result &= comp();
                return result;
            }

            boolean or() {
                boolean result = and();
                while (eat("||"))
                    result |= and();
                return result;
            }

            boolean parse() {
                boolean result = or();
                if (ch != -1)
                    throw new RuntimeException(
                        "extra char '" + expression.substring(index - 1) + "'");
                return result;
            }
        }.parse();
    }

}
