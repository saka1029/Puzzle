package test.puzzle.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntBinaryOperator;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestIntLisp {
    
    public static abstract class Obj implements Iterable<Obj> {
        
        @Override
        public Iterator<Obj> iterator() {
            return new Iterator<>() {
                
                Obj next = Obj.this;

                @Override
                public boolean hasNext() {
                    return next instanceof Cons;
                }

                @Override
                public Obj next() {
                    Obj r = ((Cons)next).car;
                    next = ((Cons)next).cdr;
                    return r;
                }
            };
        }
    }
    
    public static class Cons extends Obj {
        public final Obj car, cdr;
        
        private Cons(Obj car, Obj cdr) {
            this.car = car;
            this.cdr = cdr;
        }
        
        public static Cons of(Obj car, Obj cdr) {
            return new Cons(car, cdr);
        }

        public int length() {
            int length = 0;
            for (Obj o = this; o instanceof Cons c; o = c.cdr)
                ++length;
            return length;
        }

        @Override
        public int hashCode() {
            return Objects.hash(car, cdr);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj
                || obj instanceof Cons c
                    && c.car.equals(car)
                    && c.cdr.equals(cdr);
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            sb.append(car);
            Obj tail = cdr;
            for (; tail instanceof Cons c; tail = c.cdr)
                sb.append(" ").append(c.car);
            if (tail != NIL)
                sb.append(" . ").append(tail);
            sb.append(")");
            return sb.toString();
        }
    }

    public static abstract class Atom extends Obj {
    }
    
    
    public static class Symbol extends Atom {
        public final String name;
        static final Map<String, Symbol> ALL = new HashMap<>();
        
        private Symbol(String name) {
            this.name = name;
        }
        
        public static Symbol of(String name) {
            return ALL.computeIfAbsent(name, k -> new Symbol(k));
        }
        
        @Override
        public String toString() {
            return name;
        }
    }

    public static final Symbol NIL = Symbol.of("nil");
    
    public static class Int extends Atom {
        public final int value;
        
        private Int(int value) {
            this.value = value;
        }
        
        public static Int of(int value) {
            return new Int(value);
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj
                || obj instanceof Int i
                    && i.value == value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }
    
    public static Cons cons(Obj car, Obj cdr) {
        return Cons.of(car, cdr);
    }

    public static Obj list(Obj... elements) {
        Obj result = NIL;
        for (int i = elements.length - 1; i >= 0; --i)
            result = Cons.of(elements[i], result);
        return result;
    }
    
    public static Symbol sym(String name) {
        return Symbol.of(name);
    }

    public static Int i(int value) {
        return Int.of(value);
    }
    
    static final Pattern NUMBER = Pattern.compile("[+-]?\\d+");

    public static Obj parse(String source) {
        int length = source.length();
        return new Object() {
            int index = 0, ch;
            
            RuntimeException error(String format, Object... args) {
                return new RuntimeException(format.formatted(args));
            }
            
            void get() {
                ch = index < length ? source.charAt(index++) : -1;
            }
            
            boolean isSymbolOrNumber(int ch) {
                return switch (ch) {
                    case -1, '(', ')', '.' -> false;
                    default -> !Character.isWhitespace(ch);
                };
            }
            
            void spaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            Obj list() {
                get();  // skip '('
                List<Obj> list = new ArrayList<>();
                spaces();
                while (ch != -1 && ch != ')' && ch != '.') {
                    Obj e = expression();
                    list.add(e);
                    spaces();
                }
                Obj result = NIL;
                if (ch == '.') {
                    get();  // skip '.'
                    result = expression();
                }
                spaces();
                if (ch != ')')
                    throw error("')' expected");
                get();  // skip ')'
                for (int i = list.size() - 1; i >= 0; --i)
                    result = cons(list.get(i), result);
                return result;
            }
            
            Obj expression() {
                spaces();
                switch (ch) {
                    case -1:
                        throw error("unexpected EOS");
                    case ')': case '.':
                        throw error("unexpected '%c'", (char)ch);
                    case '(':
                        return list();
                    default:
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append((char)ch);
                            get();
                        } while (isSymbolOrNumber(ch));
                        String token = sb.toString();
                        return token.matches("[+-]?\\d+")
                            ? i(Integer.parseInt(token))
                            : sym(token);
                }
            }
            
            Obj parse() {
                get();
                Obj result = expression();
                return result;
            }
        }.parse();
    }
    
    public class RuntimeContext {
        final int[] stack;
        int sp = 0, bp = 0;
        
        private RuntimeContext(int stackSize) {
            this.stack = new int[stackSize];
        }
        
        public void push(int value) {
            stack[sp++] = value;
        }

        public int pop() {
            return stack[--sp];
        }
    }
    
    @FunctionalInterface
    public interface Code {
        void execute(RuntimeContext c);

        public static Code push(int value) {
            return new Code() {
                @Override
                public void execute(RuntimeContext c) {
                    c.push(value);
                }
                @Override
                public String toString() {
                    return "" + value;
                }  
            };
        }

        public static Code binary(IntBinaryOperator operator, String name) {
            return new Code() {
                @Override
                public void execute(RuntimeContext c) {
                    int r = c.pop();
                    c.push(operator.applyAsInt(c.pop(), r));
                }
                @Override
                public String toString() {
                    return name;
                }
            };
        }
    }
    
    @FunctionalInterface
    public interface Compiler {
        void compile(Obj obj, CompilerContext context);
    }
    
    public static class CompilerContext {
        public final List<Code> codes = new ArrayList<>();
        public final Map<Symbol, Compiler> compilers = new HashMap<>();
        
        public void add(Symbol symbol, Compiler compiler) {
            compilers.put(symbol, compiler);
        }

        public void add(Code code) {
            codes.add(code);
        }
        
        public void compile(Obj obj) {
            if (obj instanceof Int i)
                codes.add(Code.push(i.value));
            else if (obj instanceof Cons c) {
                Compiler compiler = compilers.get(c.car);
                if (compiler != null)
                    compiler.compile(c.cdr, this);
                else
                    throw new RuntimeException("unknown function " + c.car);
            } else
                throw new RuntimeException("can't compile " + obj);
        }
        
        public void run(RuntimeContext rc) {
            for (Code code : codes)
                code.execute(rc);
        }
        
        public int compileGo(Obj obj, RuntimeContext rc) {
            codes.clear();
            compile(obj);
            run(rc);
            return rc.pop();
        }
        
    }
    
    /**
     * 2項演算子operatorを可変長引数に適用する。
     * 引数がない場合は単位元を返す。
     * +や*はこちらを使う。
     * @param unit 単位元。+の時は0、*の時は1
     * @param operator 二項演算子。
     * @return
     */
    public static Compiler compileBinary(Obj unit, IntBinaryOperator operator, String name) {
        Code code = Code.binary(operator, name);
        return (args, cc) -> {
            if (args instanceof Cons c0) {
                cc.compile(c0.car);
                for (Obj e : c0.cdr) {
                    cc.compile(e);
                    cc.add(code);
                }
            } else
                cc.compile(unit);
        };
    }
    
    /**
     * 2項演算子operatorを可変長引数に適用する。
     * -や/はこちらを使う。<br>
     * <a href='http://www.nct9.ne.jp/m_hiroi/xyzzy_lisp/abclisp02.html'>参考(Common Lisp)</a><br>
     * + は足し算を、* は掛け算を、- は引き算を行います。これらの関数は引数をいくつでも取ることができます。
     * 数以外のデータを引数に与えるとエラーになります。
     * 引数の型が異なる場合は強制的に型変換が行われます。
     * 簡単な例を示しましょう。
     * <pre>
     * (+)           => 0
     * (+ 1)         => 1
     * (+ 1 2 3)     => 6
     * (+ 1 2 3 1/2) => 13/2
     * (+ 1 2 3 4.5) => 10.5
     * 
     * (*)           => 1
     * (* 1)         => 1
     * (* 1 2 3)     => 6
     * (* 1 2 3 1/4) => 3/2
     * (* 1 2 3 4.5) => 27.0
     * 
     * (- 1)         => -1
     * (- 10 5 4)    => 1
     * (- 10 5/2)    => 15/2
     * (- 10 4.5)    => 5.5
     * (-)           => エラー  ; 引数が足りない
     * </pre>
     * / は割り算を行います。整数同士の割り算で割り切れない場合は分数になります。
     * 引数が 0 の場合はエラーになります。
     * <pre>
     * (/ 2)     => 1/2    ; 引数の逆数を求める
     * (/ 8 4 2) => 1      ; 約分されて整数になる
     * (/)       => エラー ; 引数が足りない
     * </pre>
     * @param unit 単位元。-の時は0、/の時は1
     * @param operator 二項演算子。
     * @return
     */
    public static Compiler compileBinary2(Obj unit, IntBinaryOperator operator, String name) {
        Code code = Code.binary(operator, name);
        return (args, cc) -> {
            if (args instanceof Cons c0) {
                if (c0.cdr instanceof Cons c1) {
                    cc.compile(c0.car);
                    for (Obj e : c1) {
                        cc.compile(e);
                        cc.add(code);
                    }
                } else {
                    cc.compile(unit);
                    cc.compile(c0.car);
                    cc.add(code);
                }
            } else
                throw new RuntimeException("insufficient arguments");
        };
    }

    @Test
    public void testConsToString() {
        assertEquals("(a . 2)", cons(sym("a"), i(2)).toString());
        assertEquals("(a 2)", list(sym("a"), i(2)).toString());
        assertEquals(list(sym("a"), i(2)), cons(sym("a"), cons(i(2), NIL)));
        assertEquals(NIL, list());
    }
    
    @Test
    public void testParse() {
        assertEquals(NIL, parse("nil"));
        assertEquals(sym("abc"), parse("abc"));
        assertEquals(NIL, parse("()"));
        assertEquals(list(sym("a"), sym("b"), sym("c")), parse("(a b c)"));
        assertEquals(list(sym("a"), i(2), sym("c")), parse("(a 2 c)"));
        assertEquals(list(sym("+"), list(sym("-"), i(1), i(2)), i(3)), parse("(+ (- 1 2) 3)"));
        assertEquals(cons(sym("a"), cons(i(0), i(1))), parse("(a 0 . 1)"));
    }
    
    @Test
    public void testCompileInt() {
        CompilerContext cc = new CompilerContext();
        cc.compile(i(3));
        RuntimeContext context = new RuntimeContext(20);
        for (Code c : cc.codes)
            c.execute(context);
        assertEquals(3, context.pop());
    }
    
    @Test
    public void testCompileBinary() {
        CompilerContext compilerContext = new CompilerContext();
        compilerContext.add(sym("+"), compileBinary(i(0), (a, b) -> a + b, "+"));
        compilerContext.add(sym("-"), compileBinary2(i(0), (a, b) -> a - b, "-"));
        compilerContext.add(sym("*"), compileBinary(i(1), (a, b) -> a * b, "*"));
        compilerContext.add(sym("/"), compileBinary2(i(1), (a, b) -> a / b, "/"));
        RuntimeContext rc = new RuntimeContext(20);
        assertEquals(0, compilerContext.compileGo(list(sym("+")), rc));
        assertEquals(1, compilerContext.compileGo(list(sym("+"), i(1)), rc));
        assertEquals(3, compilerContext.compileGo(list(sym("+"), i(1), i(2)), rc));
        assertEquals(6, compilerContext.compileGo(list(sym("+"), i(1), i(2), i(3)), rc));
        try {
            assertEquals(0, compilerContext.compileGo(list(sym("-")), rc));
            fail();
        } catch (RuntimeException e) {
            assertEquals("insufficient arguments", e.getMessage());
        }
        assertEquals(-1, compilerContext.compileGo(list(sym("-"), i(1)), rc));
        assertEquals(-1, compilerContext.compileGo(list(sym("-"), i(1), i(2)), rc));
        assertEquals(-4, compilerContext.compileGo(list(sym("-"), i(1), i(2), i(3)), rc));
        assertEquals(1, compilerContext.compileGo(list(sym("*")), rc));
        assertEquals(1, compilerContext.compileGo(list(sym("*"), i(1)), rc));
        assertEquals(2, compilerContext.compileGo(list(sym("*"), i(1), i(2)), rc));
        assertEquals(6, compilerContext.compileGo(list(sym("*"), i(1), i(2), i(3)), rc));
        assertEquals(24, compilerContext.compileGo(list(sym("*"), i(1), i(2), i(3), i(4)), rc));
        try {
            assertEquals(1, compilerContext.compileGo(list(sym("/")), rc));
            fail();
        } catch (RuntimeException e) {
            assertEquals("insufficient arguments", e.getMessage());
        }
        assertEquals(1, compilerContext.compileGo(list(sym("/"), i(1)), rc));
        assertEquals(0, compilerContext.compileGo(list(sym("/"), i(2)), rc));
        assertEquals(2, compilerContext.compileGo(list(sym("/"), i(4), i(2)), rc));
        assertEquals(1, compilerContext.compileGo(list(sym("/"), i(8), i(4), i(2)), rc));
        assertEquals(8, compilerContext.compileGo(parse("(+ (* 2 3) (/ 8 4))"), rc));
        assertEquals("[2, 3, *, 8, 4, /, +]", compilerContext.codes.toString());
    }
}
