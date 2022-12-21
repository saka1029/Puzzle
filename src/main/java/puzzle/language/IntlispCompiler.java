package puzzle.language;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntBinaryOperator;

public class IntlispCompiler {

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
                    Obj element = ((Cons) next).car;
                    next = ((Cons) next).cdr;
                    return element;
                }
            };
        }

        public int length() {
            int length = 0;
            for (@SuppressWarnings("unused") Obj e : this)
                ++length;
            return length;
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

    /**
     * 点対はない。
     * 
     * <pre>
     * SYNTAX
     * expression = symbol | number | list
     * symbol     = SYMBOL_CHAR { SYMBOL_CHAR }
     * number     = [ '+' | '-' ] DIGIT { DIGIT }
     * list       = '(' { expression } ')'
     * </pre>
     */
    public static class Parser {
        final Reader reader;
        int ch;

        public Parser(Reader reader) {
            this.reader = reader;
            get();
        }

        public Parser(String source) {
            this(new StringReader(source));
        }

        static RuntimeException error(String format, Object... args) {
            return new RuntimeException(format.formatted(args));
        }

        static boolean isSymbolOrNumber(int ch) {
            return switch (ch) {
                case -1, '(', ')' -> false;
                default -> !Character.isWhitespace(ch);
            };
        }

        void get() {
            try {
                ch = reader.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        void spaces() {
            while (Character.isWhitespace(ch))
                get();
        }

        Obj list() {
            get(); // skip '('
            List<Obj> list = new ArrayList<>();
            spaces();
            while (ch != -1 && ch != ')') {
                list.add(expression());
                spaces();
            }
            Obj result = NIL;
            spaces();
            if (ch != ')')
                throw error("')' expected");
            get(); // skip ')'
            for (int i = list.size() - 1; i >= 0; --i)
                result = cons(list.get(i), result);
            return result;
        }

        Obj expression() {
            spaces();
            switch (ch) {
                case -1:
                    return null;
                case ')':
                    throw error("unexpected '%c'", (char) ch);
                case '(':
                    return list();
                default:
                    StringBuilder sb = new StringBuilder();
                    do {
                        sb.append((char) ch);
                        get();
                    } while (isSymbolOrNumber(ch));
                    String token = sb.toString();
                    return token.matches("[+-]?\\d+")
                        ? i(Integer.parseInt(token))
                        : sym(token);
            }
        }

        public Obj read() {
            return expression();
        }
    }

    public static Obj parse(String source) {
        try (StringReader r = new StringReader(source)) {
            return new Parser(r).read();
        }
    }

    public static class RuntimeContext {
        final int[] stack;
        int sp = 0, bp = 0, pc = 0;

        private RuntimeContext(int stackSize) {
            this.stack = new int[stackSize];
        }
        
        public static RuntimeContext create(int stackSize) {
            return new RuntimeContext(stackSize);
        }

        public void push(int value) {
            stack[sp++] = value;
        }

        public int pop() {
            return stack[--sp];
        }

        public void arg(int n) {
            push(stack[bp + n]);
        }

        public void run(List<Code> codes) {
            int size = codes.size();
            sp = bp = pc = 0;
            while (pc < size)
                codes.get(pc++).execute(this);
        }
    }

    @FunctionalInterface
    public interface Code {
        void execute(RuntimeContext c);

        public static Code constant(int value) {
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

        public static Code arg(int index) {
            return new Code() {
                @Override
                public void execute(RuntimeContext c) {
                    c.arg(index);
                }

                @Override
                public String toString() {
                    return "arg " + index;
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

        public static Code jump(int address) {
            return new Code() {
                @Override
                public void execute(RuntimeContext c) {
                    c.pc = address;
                }

                @Override
                public String toString() {
                    return "jump " + address;
                }
            };
        }

        public static Code jumpZ(int address) {
            return new Code() {
                @Override
                public void execute(RuntimeContext c) {
                    if (c.pop() == 0)
                        c.pc = address;
                }

                @Override
                public String toString() {
                    return "jumpZ " + address;
                }
            };
        }

        public static Code call(int address) {
            return new Code() {
                @Override
                public void execute(RuntimeContext c) {
                    c.push(c.pc);
                    c.pc = address;
                }

                @Override
                public String toString() {
                    return "call " + address;
                }
            };
        }

        public static Code enter(int argc) {
            return new Code() {
                @Override
                public void execute(RuntimeContext c) {
                    c.push(c.bp);
                    c.bp = c.sp - 2 - argc;
                }

                @Override
                public String toString() {
                    return "enter " + argc;
                }
            };
        }

        public static Code exit() {
            return new Code() {
                @Override
                public void execute(RuntimeContext c) {
                    int bp = c.bp; // 現在のbpを退避
                    int result = c.pop(); // 戻り値を退避
                    c.bp = c.pop(); // bpを回復
                    c.pc = c.pop(); // pcを回復
                    c.sp = bp; // spを回復
                    c.push(result); // 戻り値をpush
                }

                @Override
                public String toString() {
                    return "exit";
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
        public final Map<Symbol, Integer> arguments = new HashMap<>();
        public final Map<Symbol, int[]> functions = new HashMap<>();

        private CompilerContext() {
        }

        public static CompilerContext create() {
            CompilerContext cc = new CompilerContext();
            cc.compilers.put(sym("="), compileBinary((a, b) -> a == b ? 1 : 0, "="));
            cc.compilers.put(sym("/="), compileBinary((a, b) -> a != b ? 1 : 0, "/="));
            cc.compilers.put(sym("<"), compileBinary((a, b) -> a < b ? 1 : 0, "<"));
            cc.compilers.put(sym("<="), compileBinary((a, b) -> a <= b ? 1 : 0, "<="));
            cc.compilers.put(sym(">"), compileBinary((a, b) -> a > b ? 1 : 0, ">"));
            cc.compilers.put(sym(">="), compileBinary((a, b) -> a >= b ? 1 : 0, ">="));
            cc.compilers.put(sym("+"), compileBinary(i(0), (a, b) -> a + b, "+"));
            cc.compilers.put(sym("-"), compileBinary2(i(0), (a, b) -> a - b, "-"));
            cc.compilers.put(sym("*"), compileBinary(i(1), (a, b) -> a * b, "*"));
            cc.compilers.put(sym("/"), compileBinary2(i(1), (a, b) -> a / b, "/"));
            cc.compilers.put(sym("define"), compileDefine());
            cc.compilers.put(sym("if"), compileIf());
            return cc;
        }

        public void compile(Obj obj) {
            if (obj instanceof Int i) {
                codes.add(Code.constant(i.value));
            } else if (obj instanceof Cons c) {
                int[] function = functions.get(c.car);
                if (function != null) {
                    if (c.cdr.length() != function[1])
                        throw new RuntimeException("invalid argument size: %d expected: %d"
                            .formatted(c.cdr.length(), function[1]));
                    for (Obj arg : c.cdr)
                        compile(arg);
                    codes.add(Code.call(function[0]));
                } else {
                    Compiler compiler = compilers.get(c.car);
                    if (compiler != null)
                        compiler.compile(c.cdr, this);
                    else
                        throw new RuntimeException("unknown function '%s'".formatted(c.car));
                }
            } else if (obj instanceof Symbol s) {
                Integer argNo = arguments.get(s);
                if (argNo != null)
                    codes.add(Code.arg(argNo));
                else
                    throw new RuntimeException("undefined argument '%s'".formatted(s));
            } else
                throw new RuntimeException("can't compile " + obj);
        }

        public int compileGo(RuntimeContext rc, String source) {
            codes.clear();
            Parser parser = new Parser(source);
            Obj obj;
            while ((obj = parser.read()) != null)
                compile(obj);
            rc.run(codes);
            return rc.pop();
        }

        public static Compiler compileIf() {
            return (args, cc) -> {
                if (args instanceof Cons c0 && c0.cdr instanceof Cons c1 && c1.cdr instanceof Cons c2) {
                    cc.compile(c0.car); // condition
                    int w = cc.codes.size();
                    cc.codes.add(null); // dummy for jump if zero
                    cc.compile(c1.car); // then part
                    int x = cc.codes.size();
                    cc.codes.add(null); // dummy for jump
                    int y = cc.codes.size();
                    cc.codes.set(w, Code.jumpZ(y));
                    cc.compile(c2.car); // else part
                    int z = cc.codes.size();
                    cc.codes.set(x, Code.jump(z));
                } else
                    throw new RuntimeException("'if' requires 3 arguments but: " + args);
            };
        }

        /**
         * (define (name args...) body)
         */
        public static Compiler compileDefine() {
            return (args, cc) -> {
                if (args instanceof Cons c0 && c0.cdr instanceof Cons c1) {
                    if (c0.car instanceof Cons a0 && a0.car instanceof Symbol s0) {
                        int begin = cc.codes.size();
                        cc.codes.add(null); // dummy for jump to end
                        int start = cc.codes.size();
                        int argc = a0.cdr.length();
                        cc.functions.put(s0, new int[] {start, argc});
                        int index = 0;
                        for (Obj arg : a0.cdr)
                            if (arg instanceof Symbol s1)
                                cc.arguments.put(s1, index++);
                            else
                                throw new RuntimeException("symbol expected but: " + arg);
                        cc.codes.add(Code.enter(argc));
                        cc.compile(c1.car);
                        cc.codes.add(Code.exit());
                        int end = cc.codes.size();
                        cc.codes.set(begin, Code.jump(end));
                        cc.arguments.clear();
                    } else
                        throw new RuntimeException("'(func args)' expected after 'define' but: " + c0.car);
                } else
                    throw new RuntimeException("'define' requires 2 arguments but: " + args);

            };
        }

        public static Compiler compileBinary(IntBinaryOperator operator, String name) {
            return (args, cc) -> {
                if (args instanceof Cons c0 && c0.cdr instanceof Cons c1) {
                    cc.compile(c0.car);
                    cc.compile(c1.car);
                    cc.codes.add(Code.binary(operator, name));
                } else
                    throw new RuntimeException("invalid args: " + args);
            };
        }

        /**
         * 2項演算子operatorを可変長引数に適用する。 引数がない場合は単位元を返す。 +や*はこちらを使う。
         * 
         * @param unit     単位元。+の時は0、*の時は1
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
                        cc.codes.add(code);
                    }
                } else
                    cc.compile(unit);
            };
        }

        /**
         * 2項演算子operatorを可変長引数に適用する。 -や/はこちらを使う。<br>
         * <a href='http://www.nct9.ne.jp/m_hiroi/xyzzy_lisp/abclisp02.html'>参考(Common
         * Lisp)</a><br>
         * + は足し算を、* は掛け算を、- は引き算を行います。これらの関数は引数をいくつでも取ることができます。
         * 数以外のデータを引数に与えるとエラーになります。 引数の型が異なる場合は強制的に型変換が行われます。 簡単な例を示しましょう。
         * 
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
         * 
         * / は割り算を行います。整数同士の割り算で割り切れない場合は分数になります。 引数が 0 の場合はエラーになります。
         * 
         * <pre>
         * (/ 2)     => 1/2    ; 引数の逆数を求める
         * (/ 8 4 2) => 1      ; 約分されて整数になる
         * (/)       => エラー ; 引数が足りない
         * </pre>
         * 
         * @param unit     単位元。-の時は0、/の時は1
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
                            cc.codes.add(code);
                        }
                    } else {
                        cc.compile(unit);
                        cc.compile(c0.car);
                        cc.codes.add(code);
                    }
                } else
                    throw new RuntimeException("insufficient arguments");
            };
        }
    }
}
