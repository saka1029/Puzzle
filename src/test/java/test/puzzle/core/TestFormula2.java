package test.puzzle.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class TestFormula2 {

    static abstract class Formula {
        public Formula expand() {
            return this;
        }
    }
    
    static class Num extends Formula {
        final int value;
        
        Num(int value) {
            this.value = value;
        }
        
        public static Num of(int value) {
            return new Num(value);
        }
        
        @Override
        public String toString() {
            return "" + value;
        }
    }
    
    static class Sym extends Formula {
        static final Map<String, Sym> all = new HashMap<>();
        final String name;
        
        Sym(String name) {
            this.name = name;
        }
        
        public static Sym of(String name) {
            return all.computeIfAbsent(name, k -> new Sym(k));
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
        
        String toString(String operator) {
            return "%s(%s)".formatted(operator, argument);
        }
    }
    
    static class Neg extends Unary {
        Neg(Formula argument) {
            super(argument);
        }
        
        static Formula of(Formula argument) {
            return new Neg(argument);
        }
        
        @Override
        public String toString() {
            return toString("-");
        }
    }

    static class Binary extends Formula {
        final Formula[] arguments;
        
        Binary(Formula... arguments) {
            this.arguments = arguments.clone();
        }
        
        String toString(String operator) {
            return Stream.of(arguments)
                .map(f -> f.toString())
                .collect(Collectors.joining(" ", operator + "(", ")"));
        }
    }
    
    static class Add extends Binary {
        Add(Formula... arguments) {
            super(arguments);
        }

        static Formula of(Formula... arguments) {
            return new Add(arguments);
        }
        
        @Override
        public String toString() {
            return toString("+");
        }
    }
    
    static class Mul extends Binary {
        Mul(Formula... arguments) {
            super(arguments);
        }

        static Formula of(Formula... arguments) {
            return new Mul(arguments);
        }
        
        @Override
        public String toString() {
            return toString("*");
        }
    }
    
    static class Pow extends Binary {
        Pow(Formula... arguments) {
            super(arguments);
        }

        static Formula of(Formula... arguments) {
            return new Pow(arguments);
        }
        
        @Override
        public String toString() {
            return toString("^");
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
            
            RuntimeException error(String format, Object... args) {
                return new RuntimeException(format.formatted(args));
            }
            
            Formula factor() {
                Formula factor;
                boolean minus = eat('-');
                if (eat('(')) {
                    factor = expression();
                    if (!eat(')'))
                        throw error("')' expected");
                } else if (Character.isDigit(ch)) {
                    StringBuilder sb = new StringBuilder();
                    do {
                        sb.append((char)ch);
                        get();
                    } while (Character.isDigit(ch));
                    factor = Num.of(Integer.parseInt(sb.toString()));
                } else if (isVariable(ch)) {
                    factor = Sym.of(Character.toString(ch));
                    get();
                } else
                    throw error("unexpected character '%c'", (char)ch);
                if (minus)
                    factor = Neg.of(factor);
                return factor;
            }

            Formula power() {
                List<Formula> factors = new ArrayList<>();
                factors.add(factor());
                while (true)
                    if (eat('^'))
                        factors.add(factor());
                    else
                        break;
                return factors.size() == 1 ? factors.get(0) : Pow.of(factors.toArray(Formula[]::new));
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
                return powers.size() == 1 ? powers.get(0) : Mul.of(powers.toArray(Formula[]::new));
            }

            Formula expression() {
                List<Formula> terms = new ArrayList<>();
                terms.add(term());
                while (true)
                    if (eat('+'))
                        terms.add(term());
                    else if (eat('-'))
                        terms.add(Neg.of(term()));
                    else
                        break;
                return terms.size() == 1 ? terms.get(0) : Add.of(terms.toArray(Formula[]::new));
            }
            
            Formula parse() {
                return expression();
            }
        }.parse();
    }

    @Test
    public void test() {
        System.out.println(parse("x^2+2x+1"));
        System.out.println(parse("x^2^3"));
        System.out.println(parse("x(x + 1)(x + 2)"));
        System.out.println(parse("abc+ab+bc+ca"));
    }
}
