package puzzle.lambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaCalculus {

    public static class Bind<K, V> {
        final K key;
        final V value;
        final Bind<K, V> previous;

        Bind(Bind<K, V> previous, K key, V value) {
            this.key = key;
            this.value = value;
            this.previous = previous;
        }

        static <K, V> V find(Bind<K, V> bind, K key) {
            for ( ; bind != null; bind = bind.previous)
                if (bind.key.equals(key))
                    return bind.value;
            return null;
        }
    }

    public static class NormalizeBind {
        Bind<Variable, Variable> bind = null;
        public int size = 0;

        public Variable add(Variable key) {
            Variable replace = Variable.of(size++);
            bind = new Bind<>(bind, key, replace);
            return replace;
        }

        public void remove() {
            bind = bind.previous;
        }

        public Variable find(Variable key) {
            return Bind.find(bind, key);
        }
    }

    public static abstract class Term {

        public Term apply(Term arg) {
            return new Application(this, arg);
        }

        /**
         * 簡約します。
         * 最内側から順にβ簡約のみ行います。
         * @param bind 簡約するためのコンテキストを指定します。
         * @return 簡約した式を返します。
         */
        public abstract Term reduce(Bind<Variable, Term> bind);

        public boolean equivalentTo(Term term) {
            if (term == null)
                return false;
            if (term == this)
                return true;
            return normalize().equals(term.normalize());
        }

        public Term reduce() {
            return reduce(null);
        }

        /**
         * 式を正規化します。具体的には
         * (1)束縛変数を出現順にユニークな変数名に置換します。
         * (2)自由変数は置換せず、そのままにします。
         * ex) λx.(λx.x) x p -> λA.(λB.B) A p
         * @param map 正規化用のコンテキストです。
         * @return 正規化されたTermを返します。
         */
        public abstract Term normalize(NormalizeBind map);

        public Term normalize() {
            return normalize(new NormalizeBind());
        }
    }

    public static class Variable extends Term {
        static final Map<String, Variable> freeVariables = new HashMap<>();
        static final List<Variable> boundVariables = new ArrayList<>();
        final String name;

        private Variable(String name ) {
            this.name = name;
        }

        static Variable of(String name) {
            Variable v = freeVariables.get(name);
            if (v == null)
                freeVariables.put(name, v = new Variable(name));
            return v;
        }

        /**
         * Excelの列番号を列名に変換します。
         * ex) 1 -> A, 2 -> B, ... 26 -> Z, 27 -> AA, 28 -> AB, ...
         * @param n Excelの列番号を指定します。
         * @return Excelの列名を返します。
         */
        static String excelColumnIndexToName(int n) {
            StringBuilder sb = new StringBuilder();
            for ( ; n > 0; n /= 26)
                sb.append((char)('A' + --n % 26));
            return sb.reverse().toString();
        }

        static String intToString(int n) {
            return excelColumnIndexToName(n + 1);
        }

        static Variable of(int number) {
            int size = boundVariables.size();
            for (int i = size; i <= number; ++i)
                boundVariables.add(new Variable(intToString(i)));
//                boundVariables.add(new Variable("" + (char)('A' + i)));
            return boundVariables.get(number);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public Term reduce(Bind<Variable, Term> bind) {
            Term r = Bind.find(bind, this);
            return r != null ? r : this;
        }

        @Override
        public Term normalize(NormalizeBind bind) {
            Variable replace = bind.find(this);
            return replace == null ? this : replace;
        }
    }

    public static class Lambda extends Term {
        final Variable variable;
        final Term body;

        Lambda(Variable variable, Term body) {
            this.variable = variable;
            this.body = body;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (obj == this)
                return true;
            if (!(obj instanceof Lambda))
                return false;
            Lambda o = (Lambda)obj;
            return o.variable.equals(variable) && o.body.equals(body);
        }

        @Override
        public String toString() {
            return String.format("(λ%s.%s)", variable, body);
        }

        @Override
        public Term reduce(Bind<Variable, Term> bind) {
            return new Lambda(variable, body.reduce(bind));
        }

        @Override
        public Term normalize(NormalizeBind bind) {
            Variable replace = bind.add(variable);
            Term newBody = body.normalize(bind);
            bind.remove();
            return new Lambda(replace, newBody);
        }
    }

    public static class Application extends Term {
        final Term head, tail;

        Application(Term head, Term tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (obj == this)
                return true;
            if (obj.getClass() != Application.class)
                return false;
            Application o = (Application)obj;
            return o.head.equals(head) && o.tail.equals(tail);
        }

        @Override
        public String toString() {
            return String.format("(%s %s)", head, tail);
        }

        @Override
        public Term reduce(Bind<Variable, Term> bind) {
            Term newHead = head.reduce(bind);
            Term newTail = tail.reduce(bind);
            if (!(newHead instanceof Lambda))
                return new Application(newHead, newTail);
            Lambda lambda = (Lambda)newHead;
            Term reduced = lambda.body.reduce(new Bind<>(bind, lambda.variable, newTail));
            return reduced;
        }

        @Override
        public Term normalize(NormalizeBind bind) {
            return new Application(head.normalize(bind), tail.normalize(bind));
        }
    }

    /**
     * 文字列を式に変換します。 式の文法は以下のとおりです。
     *
     * <pre>
     * <code>
     * Expression = Term { Term }
     * Term       = Variable | Lambda | '(' Expression ')'
     * Lambda     = Variable '.' Expression
     * </code>
     * </pre>
     */
    public static Term parse(String source) {
        return new Object() {
            Bind<String, Variable> bind = null;
            int index = 0;
            int ch = ' ';

            int get() {
                return ch = index < source.length() ? source.charAt(index++) : -1;
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

            Variable parseVariable(boolean isDefinition) {
                StringBuilder sb = new StringBuilder();
                while (isVariableChar(ch)) {
                    sb.append((char) ch);
                    get();
                }
                String name = sb.toString();
                if (isDefinition)
                    return Variable.of(name);  // variable definition
                Variable bound = Bind.find(bind, name);
                if (bound != null)
                    return bound;           // bound variable
                return Variable.of(name);  // free variable
            }

            Lambda parseLambda() {
                get(); // skip 'λ'
                skipSpaces();
                if (!isVariableChar(ch))
                    throw new RuntimeException("variable expected");
                Variable variable = parseVariable(true);
                skipSpaces();
                if (ch != '.')
                    throw new RuntimeException("'.' expected");
                get(); // skip '.'
                bind = new Bind<>(bind, variable.name, variable);   // bind lambda variable
                Term body = parse();    // parse lambda body
                bind = bind.previous;   // restore bind lambda variable
                return new Lambda(variable, body);
            }

            Term parseParen() {
                get(); // skip '('
                Term body = parse();
                skipSpaces();
                if (ch != ')')
                    throw new RuntimeException("')' expected");
                get(); // skip ')'
                return body;
            }

            Term parseTerm() {
                skipSpaces();
                switch (ch) {
                case -1:
                    throw new RuntimeException("unexpected EOS");
                case '.': case ')':
                    throw new RuntimeException("unexpected '" + ((char)ch) + "'");
                case 'λ':
                    return parseLambda();
                case '(':
                    return parseParen();
                default:
                    return parseVariable(false);
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
