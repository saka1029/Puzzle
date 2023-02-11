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

        public abstract int preceedance();

        public String string() {
            return toString();
        }

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
        
        @Override
        public int preceedance() {
            return 1;
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
        
        @Override
        public int preceedance() {
            return 0;
        }
    }
    
    static abstract class Unary extends Formula {
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
        
        @Override
        public int preceedance() {
            return 10;
        }
        
        @Override
        public String string() {
            String s = argument.string();
            if (argument.preceedance() > preceedance())
                s = "(" + s + ")";
            return "-" + s;
        }
    }

    static abstract class Binary extends Formula {
        final Formula[] arguments;
        
        Binary(Formula... arguments) {
            this.arguments = arguments.clone();
        }
        
        abstract String operator();
        
        public String toString() {
            return Stream.of(arguments)
                .map(f -> f.toString())
                .collect(Collectors.joining(" ", operator() + "(", ")"));
        }
        
        @Override
        public String string() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Formula e : arguments) {
                String s = e.string();
                if (e.preceedance() > preceedance())
                    s = "(" + s + ")";
                if (first)
                    first = false;
                else
                    sb.append(operator());
                sb.append(s);
            }
            return sb.toString();
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
        String operator() {
            return "+";
        }

        @Override
        public int preceedance() {
            return 60;
        }
        
        @Override
        public String string() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Formula e : arguments) {
                if (e instanceof Neg n) {
                    sb.append(n.string());
                } else {
                    String s = e.string();
                    if (e.preceedance() > preceedance())
                        s = "(" + s + ")";
                    if (!first)
                        sb.append("+");
                    sb.append(s);
                }
                first = false;
            }
            return sb.toString();
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
        String operator() {
            return "*";
        }
        
        @Override
        public int preceedance() {
            return 40;
        }
        
        @Override
        public String string() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Formula e : arguments) {
                String s = e.string();
                if (e.preceedance() > preceedance() || !first && e instanceof Num)
                    s = "(" + s + ")";
                if (first)
                    first = false;
                sb.append(s);
            }
            return sb.toString();
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
        String operator() {
            return "^";
        }
        
        @Override
        public int preceedance() {
            return 20;
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
    public void testParse() {
        System.out.println(parse("x^2+2x+1"));
        System.out.println(parse("x^2^3"));
        System.out.println(parse("x(x + 1)(x + 2)"));
        System.out.println(parse("abc+ab+bc+ca"));
    }

    @Test
    public void testString() {
        System.out.println(parse("x^2+2x+1").string());
        System.out.println(parse("x^2^3").string());
        System.out.println(parse("x(x + 1)(x + 2)").string());
        System.out.println(parse("abc+ab+bc+ca").string());
        System.out.println(parse("2*3*4").string());
        System.out.println(parse("-(x + 1)").string());
        System.out.println(parse("x^(y+2)^3").string());
        System.out.println(parse("x^y+2^3").string());
        System.out.println(parse("x^2-2x+1").string());
        System.out.println(parse("x^2-(2+y)+1").string());
    }
}
