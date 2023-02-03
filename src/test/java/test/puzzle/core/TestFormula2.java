package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestFormula2 {

    static abstract class Formula {
        public Formula expand() {
            return this;
        }
    }
    
    static class Int extends Formula {
        final int value;
        
        Int(int value) {
            this.value = value;
        }
        
        @Override
        public String toString() {
            return "" + value;
        }
    }
    
    static class Symbol extends Formula {
        static final Map<String, Symbol> all = new HashMap<>();
        final String name;
        
        Symbol(String name) {
            this.name = name;
        }
        
        public static Symbol of(String name) {
            return all.computeIfAbsent(name, k -> new Symbol(k));
        }
        
        @Override
        public String toString() {
            return name;
        }
    }

    static final Symbol PLUS = Symbol.of("+"), MINUS = Symbol.of("-"),
        MULT = Symbol.of("*"), DIV = Symbol.of("/"),
        POW = Symbol.of("^");

    static class Func extends Formula {
        final Symbol operator;
        final List<Formula> arguments = new ArrayList<>();
        
        Func(Symbol operator, Formula... arguments) {
            this.operator = operator;
            for (Formula a : arguments)
                this.arguments.add(a);
        }
        
        Func(Symbol operator, List<Formula> arguments) {
            this.operator = operator;
            this.arguments.addAll(arguments);
        }
        
        @Override
        public String toString() {
            return "" + operator + arguments;
        }
        
        @Override
        public Formula expand() {
            if (!operator.equals(MULT))
                return this;
            return null;
        }
    }

    @Test
    public void test() {
        double n = 10, r = n;
        for (int i = 0; i < 100; ++i) {
            System.out.println(r);
            r = Math.sqrt(r) * 10;
        }
    }

}
