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
    
    static class Unary extends Formula {
        final Formula argument;
        
        Unary(Formula argument) {
            this.argument = argument;
        }
    }
    
    static class Neg extends Unary {
        Neg(Formula argument) {
            super(argument);
        }
        
        static Formula of(Formula argument) {
            return new Neg(argument);
        }
    }

    static class Binary extends Formula {
        final Formula[] arguments;
        
        Binary(Formula... arguments) {
            this.arguments = arguments.clone();
        }
    }
    
    static class Add extends Binary {
        Add(Formula... arguments) {
            super(arguments);
        }

        static Formula of(Formula... arguments) {
            return new Add(arguments);
        }
    }
    
    static class Mul extends Binary {
        Mul(Formula... arguments) {
            super(arguments);
        }

        static Formula of(Formula... arguments) {
            return new Mul(arguments);
        }
    }
    
    static class Pow extends Binary {
        Pow(Formula... arguments) {
            super(arguments);
        }

        static Formula of(Formula... arguments) {
            return new Pow(arguments);
        }
    }
    
    public static Formula parse(String source) {
        return new Object() {
            int length = source.length(), index = 0, ch = get();
            
            int get() {
                return ch = index < length ? source.charAt(index++) : -1;
            }
            
            boolean eat(int expected) {
                while (Character.isWhitespace(ch))
                    get();
                if (ch == expected) {
                    get();
                    return true;
                }
                return false;
            }
            
            boolean isVariable(int ch) {
                return ch >= 'a' && ch <= 'z'
                    || ch >= 'A' && ch <= 'Z';
            }
            
            Formula factor() {

            }

            Formula power() {
                List<Formula> factors = new ArrayList<>();
                factors.add(factor());
                while (true)
                    if (eat('^'))
                        factors.add(factor());
                    else
                        break;
                return Pow.of(factors.toArray(Formula[]::new));
            }

            Formula term() {
                List<Formula> powers = new ArrayList<>();
                powers.add(power());
                while (true)
                    if (eat('*'))
                        powers.add(power());
                    else if (isVariable(ch) || ch == '(')
                        powers.add(power());
                    else
                        break;
                return Mul.of(powers.toArray(Formula[]::new));
            }

            Formula parse() {
                List<Formula> terms = new ArrayList<>();
                terms.add(term());
                while (true)
                    if (eat('+'))
                        terms.add(term());
                    else if (eat('-'))
                        terms.add(Neg.of(term()));
                    else
                        break;
                return Add.of(terms.toArray(Formula[]::new));
            }
        }.parse();
    }

    @Test
    public void test() {
    }
}
