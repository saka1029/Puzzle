package puzzle.language;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LambdaCalculus {

    private LambdaCalculus() {
    }

    static class Bind<K, V> {
        final K key;
        final V value;
        final Bind<K, V> previous;
        int count = 0;

        Bind(Bind<K, V> previous, K key, V value) {
            this.key = key;
            this.value = value;
            this.previous = previous;
        }

        static <K, V> V find(Bind<K, V> bind, K key) {
            for (; bind != null; bind = bind.previous)
                if (key.equals(bind.key)) {
                    ++bind.count;
                    return bind.value;
                }
            return null;
        }

        static <K, V> String toString(Bind<K, V> bind) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            String sep = "";
            for (Bind<K, V> e = bind; e != null; e = e.previous, sep = " ")
                sb.append(sep).append(e.key).append("=").append(e.value);
            sb.append("}");
            return sb.toString();
        }
    }

    static class IntHolder {
        public int value = 0;
    }

    static class Tracer {
        final Consumer<String> writer;
        int level = 0;

        private Tracer(Consumer<String> writer) {
            this.writer = writer;
        }

        static Tracer of(Consumer<String> writer) {
            return new Tracer(writer);
        }

        Expression enter(Expression e, Bind<BoundVariable, Expression> bind) {
            if (writer != null)
                writer.accept("  ".repeat(level) + "< " + e + " " + Bind.toString(bind));
            ++level;
            return e;
        }

        Expression exit(Expression e) {
            --level;
            if (writer != null)
                writer.accept("  ".repeat(level) + "> " + e);
            return e;
        }
    }

    static String excelColumnName(int columnNumber) {
        StringBuilder sb = new StringBuilder();
        for (; columnNumber > 0; columnNumber /= 26)
            sb.append((char) ('A' + --columnNumber % 26));
        return sb.reverse().toString();
    }

    public static abstract class Expression {

        abstract void normalize(Bind<BoundVariable, String> bind, IntHolder number,
            StringBuilder sb);

        public String normalize() {
            StringBuilder sb = new StringBuilder();
            normalize(null, new IntHolder(), sb);
            return sb.toString();
        }

        abstract Expression reduce(Bind<BoundVariable, BoundVariable> lambdaBind,
            Bind<BoundVariable, Expression> reductionBind, Tracer tracer);

        public Expression reduce() {
            return reduce(null, null, Tracer.of(null));
        }

        public Expression reduce(Consumer<String> writer) {
            Tracer tracer = Tracer.of(writer);
            tracer.enter(this, null);
            return tracer.exit(reduce(null, null, tracer));
        }

        abstract Expression expand(Map<String, Expression> globals,
            Bind<BoundVariable, BoundVariable> lambdaBind);

        public Expression expand(Map<String, Expression> globals) {
            return expand(globals, null);
        }
    }

    public static class Lambda extends Expression {
        final BoundVariable variable;
        final Expression body;
        final int referenceCount;

        private Lambda(BoundVariable variable, Expression body, int referenceCount) {
            this.variable = variable;
            this.body = body;
            this.referenceCount = referenceCount;
        }

        static Lambda of(BoundVariable variable, Expression body, int referenceCount) {
            return new Lambda(variable, body, referenceCount);
        }

        @Override
        public String toString() {
            return "λ" + variable + "." + body;
            /*
             * compact version StringBuilder sb = new StringBuilder();
             * Expression e = this; String prefix = "λ"; while (e instanceof
             * Lambda) { Lambda l = (Lambda)e;
             * sb.append(prefix).append(l.variable); e = l.body; prefix = " "; }
             * return sb.append(".").append(e).toString();
             */
        }

        static String normalizedVariableName(int n) {
            return excelColumnName(n + 1);
        }

        @Override
        void normalize(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb) {
            sb.append("λ").append(normalizedVariableName(number.value)).append(".");
            body.normalize(new Bind<>(bind, variable, normalizedVariableName(number.value++)),
                number, sb);
            /*
             * compact version Expression e = this; String prefix = "λ"; while
             * (e instanceof Lambda) { Lambda l = (Lambda)e;
             * sb.append(prefix).append(normalizedVariableName(number.value));
             * bind = new Bind<>(bind, l.variable,
             * normalizedVariableName(number.value++)); e = l.body; prefix =
             * " "; } sb.append("."); e.normalize(bind, number, sb);
             */
        }

        @Override
        Expression reduce(Bind<BoundVariable, BoundVariable> lambdaBind,
            Bind<BoundVariable, Expression> reductionBind,
            Tracer tracer) {
            BoundVariable newVariable = BoundVariable.of(variable.name);
            Bind<BoundVariable, BoundVariable> newBind = new Bind<>(lambdaBind, variable,
                newVariable);
            Expression newBody = body.reduce(newBind, reductionBind, tracer);
            Lambda newLambda = Lambda.of(newVariable, newBody, newBind.count);
            return newLambda;
        }

        @Override
        Expression expand(Map<String, Expression> globals,
            Bind<BoundVariable, BoundVariable> lambdaBind) {
            BoundVariable newVariable = BoundVariable.of(variable.name);
            Bind<BoundVariable, BoundVariable> newBind = new Bind<>(lambdaBind, variable,
                newVariable);
            Expression newBody = body.expand(globals, newBind);
            Lambda newLambda = Lambda.of(newVariable, newBody, newBind.count);
            return newLambda;
        }
    }

    public static abstract class Variable extends Expression {

        final String name;

        Variable(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class BoundVariable extends Variable {

        private BoundVariable(String name) {
            super(name);
        }

        static BoundVariable of(String name) {
            return new BoundVariable(name);
        }

        @Override
        void normalize(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb) {
            sb.append(Bind.find(bind, this));
        }

        @Override
        Expression reduce(Bind<BoundVariable, BoundVariable> lambdaBind,
            Bind<BoundVariable, Expression> reductionBind,
            Tracer tracer) {
            BoundVariable newVariable = Bind.find(lambdaBind, this);
            if (newVariable != null)
                return newVariable;
            Expression newTerm = Bind.find(reductionBind, this);
            if (newTerm != null)
                return newTerm;
            return this;
        }

        @Override
        Expression expand(Map<String, Expression> globals,
            Bind<BoundVariable, BoundVariable> lambdaBind) {
            return Bind.find(lambdaBind, this);
        }
    }

    public static class FreeVariable extends Variable {

        static final Map<String, FreeVariable> all = new HashMap<>();

        private FreeVariable(String name) {
            super(name);
        }

        static FreeVariable of(String name) {
            return all.computeIfAbsent(name, k -> new FreeVariable(k));
        }

        @Override
        void normalize(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb) {
            sb.append(name);
        }

        @Override
        Expression reduce(Bind<BoundVariable, BoundVariable> lambdaBind,
            Bind<BoundVariable, Expression> reductionBind,
            Tracer tracer) {
            return this;
        }

        @Override
        Expression expand(Map<String, Expression> globals,
            Bind<BoundVariable, BoundVariable> lambdaBind) {
            Expression term = globals.get(name);
            if (term != null)
                return term.expand(globals);
            return this;
        }
    }

    public static class Application extends Expression {
        final Expression head, tail;

        private Application(Expression head, Expression tail) {
            this.head = head;
            this.tail = tail;
        }

        static Application of(Expression head, Expression tail) {
            return new Application(head, tail);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (head instanceof Lambda)
                sb.append("(").append(head).append(")");
            else
                sb.append(head);
            sb.append(" ");
            if (!(tail instanceof Variable))
                sb.append("(").append(tail).append(")");
            else
                sb.append(tail);
            return sb.toString();
        }

        @Override
        void normalize(Bind<BoundVariable, String> bind, IntHolder number, StringBuilder sb) {
            boolean isHeadLambda = head instanceof Lambda;
            boolean isTailVariable = tail instanceof Variable;
            if (isHeadLambda)
                sb.append("(");
            head.normalize(bind, number, sb);
            if (isHeadLambda)
                sb.append(")");
            sb.append(" ");
            if (!isTailVariable)
                sb.append("(");
            tail.normalize(bind, number, sb);
            if (!isTailVariable)
                sb.append(")");
        }

        @Override
        Expression reduce(Bind<BoundVariable, BoundVariable> lambdaBind,
            Bind<BoundVariable, Expression> reductionBind,
            Tracer tracer) {
            Expression newHead = head.reduce(lambdaBind, reductionBind, tracer);
            Expression newTail = tail.reduce(lambdaBind, reductionBind, tracer);
            if (newHead instanceof Lambda) {
                Lambda lambda = (Lambda) newHead;
                // bodyが属するラムダの変数を参照していなければ単純にbodyをそのまま返します。
                // if (lambda.referenceCount <= 0)
                // return lambda.body;
                Bind<BoundVariable, Expression> newBind = new Bind<>(reductionBind, lambda.variable,
                    newTail);
                tracer.enter(lambda.body, newBind);
                return tracer.exit(lambda.body.reduce(lambdaBind, newBind, tracer));
            } else
                return Application.of(newHead, newTail);
        }

        @Override
        Expression expand(Map<String, Expression> globals,
            Bind<BoundVariable, BoundVariable> lambdaBind) {
            return Application.of(head.expand(globals, lambdaBind),
                tail.expand(globals, lambdaBind));
        }
    }

    /**
     * <pre>
     * Expression ::= Term { Term }.
     * Term       ::= Variable
     *              | 'λ' Variable { Variable } '.' Expression
     *              | '(' Expression ')'.
     * </pre>
     */
    public static Expression parse(String source) {
        return new Object() {
            int index = 0;
            int[] codePoints = source.codePoints().toArray();
            int ch = ' ';
            Bind<String, BoundVariable> bind = null;

            int get() {
                return ch = index < codePoints.length ? codePoints[index++] : -1;
            }

            boolean isVariableChar(int ch) {
                switch (ch) {
                case -1:
                case 'λ':
                case '.':
                case '(':
                case ')':
                    return false;
                default:
                    return !Character.isWhitespace(ch);
                }
            }

            void skipSpaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            Lambda parseLambda() {
                skipSpaces();
                if (!isVariableChar(ch))
                    throw new RuntimeException("variable expected");
                String variableName = parseVariableName();
                BoundVariable variable = BoundVariable.of(variableName);
                bind = new Bind<>(bind, variableName, variable);
                skipSpaces();
                Expression body;
                if (ch == '.') {
                    get(); // skip '.'
                    body = parse();
                } else
                    body = parseLambda();
                Lambda lambda = Lambda.of(variable, body, bind.count);
                bind = bind.previous;
                return lambda;
            }

            Expression parseParen() {
                skipSpaces();
                Expression term = parse();
                if (ch != ')')
                    throw new RuntimeException("')' expected");
                get(); // skip ')'
                return term;
            }

            String parseVariableName() {
                StringBuilder sb = new StringBuilder();
                for (; isVariableChar(ch); get())
                    sb.appendCodePoint(ch);
                return sb.toString();
            }

            Variable parseVariable() {
                String variableName = parseVariableName();
                BoundVariable variable = Bind.find(bind, variableName);
                return variable != null ? variable : FreeVariable.of(variableName);
            }

            Expression parseTerm() {
                skipSpaces();
                switch (ch) {
                case -1:
                    throw new RuntimeException("unexpected end of string");
                case 'λ':
                    get(); // skip 'λ'
                    return parseLambda();
                case '(':
                    get(); // skip '('
                    return parseParen();
                default:
                    if (!isVariableChar(ch))
                        throw new RuntimeException("unexpected '" + ((char) ch) + "'");
                    return parseVariable();
                }
            }

            Expression parse() {
                Expression term = parseTerm();
                while (true) {
                    skipSpaces();
                    if (ch != 'λ' && ch != '(' && !isVariableChar(ch))
                        break;
                    term = Application.of(term, parseTerm());
                }
                return term;
            }
        }.parse();
    }

}