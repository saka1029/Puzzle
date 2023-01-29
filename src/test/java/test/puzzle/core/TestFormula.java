package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestFormula {

    static abstract class Formula {
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

}
