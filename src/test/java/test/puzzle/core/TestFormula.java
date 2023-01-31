package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.Test;

public class TestFormula {

    static abstract class Formula {

        boolean match(Formula other, Map<Variable, Formula> result) {
            return equals(other);
        }

        public Map<Variable, Formula> match(Formula other) {
            Map<Variable, Formula> result = new HashMap<>();
            return match(other, result) ? result : null;
        }
    }
    
    static class Cons extends Formula {
        final Formula car, cdr;
        Cons(Formula car, Formula cdr) {
            this.car = car;
            this.cdr = cdr;
        }
        
        static Cons of(Formula car, Formula cdr) {
            return new Cons(car, cdr);
        }
        
        static Cons list(Formula... formulas) {
            Cons result = NIL;
            for (int i = formulas.length - 1; i >= 0; --i)
                result = new Cons(formulas[i], result);
            return result;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(car, cdr);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            return obj instanceof Cons cons
                && cons.car.equals(car) && cons.cdr.equals(cdr);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            Formula f = this;
            for (String sep = ""; f instanceof Cons c && c != NIL; f = c.cdr, sep = " ")
                sb.append(sep).append(c.car);
            if (f != NIL)
                sb.append(" . ").append(f);
            sb.append(")");
            return sb.toString();
        }
        
        boolean match(Formula other, Map<Variable, Formula> result) {
            if (equals(other))
                return true;
            else if (other instanceof Cons cons)
                return car.match(cons.car, result) && cdr.match(cons.cdr, result);
            else
                return false;
        }
    }
    
    static final Cons NIL = new Cons(null, null) {
        @Override public String toString() {
            return "nil";
        }
    };
    
    static class Variable extends Formula {
        static final Map<String, Variable> all = new HashMap<>();
        final String name;
        private Variable(String name) {
            this.name = name;
        }
        
        static Variable of(String name ) {
            return all.computeIfAbsent(name, k -> new Variable(k));
        }
        
        @Override
        public String toString() {
            return name;
        }
        
        @Override
        boolean match(Formula other, Map<Variable, Formula> result) {
            Formula bound = result.get(this);
            if (bound == null) {
                result.put(this, other);
                return true;
            } else
                return bound.match(other, result);
        }
    }
    
    static class Symbol extends Formula {
        static final Map<String, Symbol> all = new HashMap<>();
        final String name;
        private Symbol(String name) {
            this.name = name;
        }
        
        static Symbol of(String name ) {
            return all.computeIfAbsent(name, k -> new Symbol(k));
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    static Symbol sym(String name) {
        return Symbol.of(name);
    }

    static Variable var(String name) {
        return Variable.of(name);
    }

    static Cons cons(Formula car, Formula cdr) {
        return Cons.of(car, cdr);
    }

    static Cons list(Formula... formulas) {
        return Cons.list(formulas);
    }

    @Test
    public void testSymbol() {
        assertEquals("a", sym("a").toString());
        assertEquals(sym("a"), sym("a"));
    }
    
    @Test
    public void testList() {
        assertEquals("(a b)", list(sym("a"), sym("b")).toString());
        assertEquals("(a . b)", cons(sym("a"), sym("b")).toString());
    }
    
    @Test
    public void testMatch() {
        Symbol a = sym("a"), b = sym("b"), c = sym("c");
        Variable X = var("X"), Y = var("Y");
        assertEquals(Map.of(), a.match(a));
        assertEquals(null, a.match(b));
        assertEquals(Map.of(), cons(a, b).match(cons(a, b)));
        assertEquals(null, cons(a, b).match(cons(a, c)));
        assertEquals(null, cons(a, b).match(cons(c, b)));
        assertEquals(Map.of(), list(a, b).match(list(a, b)));
        assertEquals(null, list(a, b).match(list(a, c)));
        assertEquals(null, list(a, b).match(list(c, b)));
        assertEquals(Map.of(X, b), X.match(b));
        assertEquals(Map.of(X, a, Y, b), cons(X, Y).match(cons(a, b)));
        assertEquals(Map.of(X, a, Y, b), list(X, Y).match(list(a, b)));
        assertEquals(Map.of(X, a, Y, list(b, c)), cons(X, Y).match(list(a, b, c)));
        assertEquals(Map.of(X, a, Y, cons(b, c)), list(X, Y).match(list(a, cons(b, c))));
        assertEquals(Map.of(X, a), list(X, X).match(list(a, a)));
        assertEquals(Map.of(X, a), list(X, list(b, X)).match(list(a, list(b, a))));
        assertEquals(Map.of(X, a), list(X, cons(b, X)).match(list(a, cons(b, a))));
        assertEquals(null, list(X, list(b, X)).match(list(a, list(a, a))));
        assertEquals(Map.of(X, list(a, b)), list(X, list(b, X)).match(list(list(a, b), list(b, list(a, b)))));
    }

}
