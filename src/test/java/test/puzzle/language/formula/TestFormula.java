package test.puzzle.language.formula;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestFormula {

    interface Formula {
    }

    public static Pair cons(Formula head, Formula tail) {
        return new Pair.Cons(head, tail);
    }

    public static Formula car(Formula f) {
        return ((Pair.Cons)f).car;
    }

    public static Formula cdr(Formula f) {
        return ((Pair.Cons)f).cdr;
    }

    public static Variable ver(String name) {
        return Variable.of(name);
    }

    public static UnboundVariable uvar(String name) {
        return UnboundVariable.of(name);
    }

    public static Capture cap(String name) {
        return Capture.of(name);
    }

    public static Pair list(Formula... elements) {
        Pair result = Pair.NONE;
        for (int i = elements.length - 1; i >= 0; --i)
            result = cons(result, elements[i]);
        return result;
    }

    interface Pair extends Formula {
        public static final Pair NONE = new Pair() {
            @Override
            public String toString() {
                return "()";
            }
        };

        static class Cons implements Pair {
            final Formula car, cdr;

            Cons(Formula car, Formula cdr) {
                this.car = car;
                this.cdr = cdr;
            }

            @Override
            public String toString() {
                return "(" + car + " . " + cdr + ")";
            }
        };
    }

    interface Constant extends Formula {
    }

    static class UnboundVariable implements Constant {
        static final Map<String, UnboundVariable> map = new HashMap<>();
        final String name;

        private UnboundVariable(String name) {
            this.name = name;
        }

        public static UnboundVariable of(String name) {
            return map.computeIfAbsent(name, k -> new UnboundVariable(name));
        }

        @Override
        public String toString() {
            return name;
        }

    }

    static class Variable implements Formula {
        static final Map<String, Variable> map = new HashMap<>();
        final String name;

        private Variable(String name) {
            this.name = name;
        }

        public static Variable of(String name) {
            return map.computeIfAbsent(name, k -> new Variable(name));
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class Capture implements Formula {
        static final Map<String, Capture> map = new HashMap<>();
        final String name;

        private Capture(String name) {
            this.name = name;
        }

        public static Capture of(String name) {
            return map.computeIfAbsent(name, k -> new Capture(name));
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Test
    void test() {
        fail("Not yet implemented");
    }

}
