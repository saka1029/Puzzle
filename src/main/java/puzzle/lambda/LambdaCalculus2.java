package puzzle.lambda;

import java.util.HashMap;
import java.util.Map;

public class LambdaCalculus2 {

    static class Bind<K, V> {
        final K key; final V value;
        final Bind<K, V> previous;
        int count = 0;

        Bind(Bind<K, V> previous, K key, V value) {
            this.key = key;
            this.value = value;
            this.previous = previous;
        }

        static <K, V> V find(Bind<K, V> bind, K key) {
            for ( ; bind != null; bind = bind.previous)
                if (key.equals(bind.key)) {
                    ++bind.count;
                    return bind.value;
                }
            return null;
        }
    }


    static class IntHolder {
        public int value = 0;
    }

    public static abstract class Term {

        abstract void normalize(Bind<Lambda, Integer> bind, IntHolder number, StringBuilder sb);
        public String normalize() {
            StringBuilder sb = new StringBuilder();
            normalize(null, new IntHolder(), sb);
            return sb.toString();
        }

        abstract Term reduce(Bind<Lambda, BoundVariable> lambdaBind, Bind<Lambda, Term> reductionBind);
        public Term reduce() {
            return reduce(null, null);
        }

        public static String normalizedBoundVariableName(int n) {
            return "%" + n;
        }
    }

    public static class Lambda extends Term {
        final String name;
        Term body = null;
        int referenceCount = -1;

        Lambda(String name) {
            this.name = name;
        }

        void setBody(Term body, int referenceCount) {
            if (body == null)
                throw new IllegalArgumentException("body");
            if (this.body != null)
                throw new IllegalStateException("body was set");
            this.body = body;
            this.referenceCount = referenceCount;
        }

        @Override
        public String toString() {
            return "λ" + name + "[ref=" + referenceCount + "]" + "." + body;
        }

        @Override
        public void normalize(Bind<Lambda, Integer> bind, IntHolder number, StringBuilder sb) {
            sb.append("λ").append(normalizedBoundVariableName(number.value)).append(".");
            body.normalize(new Bind<>(bind, this, number.value++), number, sb);
        }

        @Override
        Term reduce(Bind<Lambda, BoundVariable> lambdaBind, Bind<Lambda, Term> reductionBind) {
            Lambda newLambda = new Lambda(name);
            BoundVariable newVariable = new BoundVariable(newLambda);
            Bind<Lambda, BoundVariable> newBind = new Bind<>(lambdaBind, this, newVariable);
            Term newBody = body.reduce(newBind, reductionBind);
            newLambda.setBody(newBody, newBind.count);
            return newLambda;
        }
    }

    public static abstract class Variable extends Term {
    }

    public static class BoundVariable extends Variable {
        final Lambda lambda;

        BoundVariable(Lambda lambda) {
            this.lambda = lambda;
        }

        @Override
        public String toString() {
            return lambda.name;
        }

        @Override
        public void normalize(Bind<Lambda, Integer> bind, IntHolder number, StringBuilder sb) {
            sb.append(normalizedBoundVariableName(Bind.find(bind, lambda)));
        }

        @Override
        Term reduce(Bind<Lambda, BoundVariable> lambdaBind, Bind<Lambda, Term> reductionBind) {
            BoundVariable newVariable = Bind.find(lambdaBind, lambda);
            if (newVariable != null)
                return newVariable;
            Term newTerm = Bind.find(reductionBind, lambda);
            if (newTerm != null)
                return newTerm;
            return this;
        }
    }

    public static class FreeVariable extends Variable {
        static final Map<String, FreeVariable> all = new HashMap<>();
        final String name;

        private FreeVariable(String name) {
            this.name = name;
        }

        static FreeVariable of(String name) {
            return all.computeIfAbsent(name, k -> new FreeVariable(k));
        }

        @Override
        public String toString() {
            return name + "[free]";
        }

        @Override
        public void normalize(Bind<Lambda, Integer> bind, IntHolder number, StringBuilder sb) {
            sb.append(name);
        }

        @Override
        Term reduce(Bind<Lambda, BoundVariable> lambdaBind, Bind<Lambda, Term> reductionBind) {
            return this;
        }
    }

    public static class Application extends Term {
        final Term head, tail;

        Application(Term head, Term tail) {
            this.head = head;
            this.tail = tail;
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
        public void normalize(Bind<Lambda, Integer> bind, IntHolder number, StringBuilder sb) {
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
        Term reduce(Bind<Lambda, BoundVariable> lambdaBind, Bind<Lambda, Term> reductionBind) {
            Term newHead = head.reduce(lambdaBind, reductionBind);
            Term newTail = tail.reduce(lambdaBind, reductionBind);
            if (newHead instanceof Lambda) {
                Lambda lambda = (Lambda)newHead;
                // bodyが属するラムダの変数を参照していなければ単純にbodyをそのまま返します。
                if (lambda.referenceCount <= 0)
                    return lambda.body;
                Bind<Lambda, Term> newBind = new Bind<>(reductionBind, lambda, newTail);
                return lambda.body.reduce(lambdaBind, newBind);
            } else
                return new Application(newHead, newTail);
        }
    }

    public static Term parse(String source) {
        return new Object() {
            int index = 0;
            int[] codePoints = source.codePoints().toArray();
            int ch = ' ';
            Bind<String, Lambda> bind = null;

            int get() {
                return ch = index < codePoints.length ? codePoints[index++] : -1;
            }

            boolean isVariableChar(int ch) {
                switch (ch) {
                case -1: case 'λ': case '.':
                case '(': case ')':
                    return false;
                default:
                    return !Character.isWhitespace(ch);
                }
            }

            void skipSpaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            Term parseLambda() {
                get();  // skip 'λ'
                skipSpaces();
                if (!isVariableChar(ch))
                    throw new RuntimeException("variable expected");
                String variableName = parseVariableName();
                skipSpaces();
                if (ch != '.')
                    throw new RuntimeException("'.' expected");
                get();  // skip '.'
                Lambda lambda = new Lambda(variableName);
                bind = new Bind<>(bind, variableName, lambda);
                Term body = parse();
                lambda.setBody(body, bind.count);
                bind = bind.previous;
                return lambda;
            }

            Term parseParen() {
                get();  // skip '('
                skipSpaces();
                Term term = parse();
                if (ch != ')')
                    throw new RuntimeException("')' expected");
                get();  // skip ')'
                return term;
            }

            String parseVariableName() {
                StringBuilder sb = new StringBuilder();
                for ( ; isVariableChar(ch); get())
                    sb.appendCodePoint(ch);
                return sb.toString();
            }

            Term parseVariable() {
                String variableName = parseVariableName();
                Lambda lambda = Bind.find(bind, variableName);
                return lambda != null
                    ? new BoundVariable(lambda)
                    : new FreeVariable(variableName);
            }

            Term parseTerm() {
                skipSpaces();
                switch (ch) {
                case -1:
                    throw new RuntimeException("unexpected end of string");
                case 'λ':
                    return parseLambda();
                case '(':
                    return parseParen();
                default:
                    if (!isVariableChar(ch))
                        throw new RuntimeException("unexpected '" + ((char)ch) + "'");
                    return parseVariable();
                }
            }

            Term parse() {
                Term term = parseTerm();
                while (true) {
                    skipSpaces();
                    if (ch != 'λ' && ch != '(' && !isVariableChar(ch))
                        break;
                    term = new Application(term, parseTerm());
                }
                return term;
            }
        }.parse();
    }

}
