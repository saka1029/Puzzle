package test.puzzle.core;

import static org.junit.Assert.fail;

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
        
        static Cons of(Formula... formulas) {
            Cons result = NIL;
            for (int i = formulas.length - 1; i >= 0; --i)
                result = new Cons(formulas[i], result);
            return result;
        }
    }
    
    static final Cons NIL = Cons.of(null, null);
    
    static class Variable extends Formula {
        static final Map<String, Variable> all = new HashMap<>();
        final String name;
        private Variable(String name) {
            this.name = name;
        }
        
        static Variable of(String name ) {
            return all.computeIfAbsent(name, k -> new Variable(k));
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
    }
    
    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
