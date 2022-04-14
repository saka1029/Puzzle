package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestType4 {

    static interface Type {
    }

    static class BasicType implements Type {
        final Class<?> clazz;

        BasicType(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return ((BasicType) obj).clazz == clazz;
        }

        @Override
        public String toString() {
            return clazz.getSimpleName();
        }
    }

    static class WordType implements Type {
        final Type[] input;
        final Type[] output;

        WordType(Type[] input, Type[] output) {
            this.input = input;
            this.output = output;
        }

        static void check(Type[] output, Type[] input, int matchLength) {
            int op = output.length - matchLength;
            int ip = input.length - matchLength;
            for (int k = 0; k < matchLength; ++k, ++op, ++ip) {
                System.out.println("check: pos=" + k + " " + output[op] + ":" + input[ip]);
            }
        }

        WordType composite(WordType next) {
            int matchLength = Math.min(this.output.length, next.input.length);
            int inputLength = this.input.length + next.input.length - matchLength;
            int outputLength = this.output.length + next.output.length - matchLength;
            check(this.output, next.input, matchLength);
            Type[] input = new Type[inputLength];
            int inputRest = next.input.length - matchLength;
            System.arraycopy(next.input, 0, input, 0, inputRest);
            System.arraycopy(this.input, 0, input, inputRest, this.input.length);
            Type[] output = new Type[outputLength];
            int outputRest = this.output.length - matchLength;
            System.arraycopy(this.output, 0, output, 0, outputRest);
            System.arraycopy(next.output, 0, output, outputRest, next.output.length);
            WordType result = new WordType(input, output);
            System.out.println(this + " . " + next + " == " + result);
            return result;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(input) * 31 + Arrays.hashCode(output);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            WordType other = (WordType) obj;
            return Arrays.equals(input, other.input) && Arrays.equals(output, other.output);
        }

        @Override
        public String toString() {
            return Arrays.toString(input) + "->" + Arrays.toString(output);
        }
    }

    static final BasicType OBJECT = new BasicType(Object.class);
    static final BasicType BOOLEAN = new BasicType(Boolean.class);
    static final BasicType INT = new BasicType(Integer.class);
    static final BasicType STR = new BasicType(String.class);
    static final BasicType CHAR = new BasicType(Character.class);
    static final BasicType BLOCK = new BasicType(Block.class);

    static Type[] types(Type... types) {
        return types;
    }

    static class RuntimeContext {
        static final boolean DEBUG = false;
        final List<Object> stack = new ArrayList<>();

        void debug(String m) { if (DEBUG) System.out.println(m + ": " + this); }
        public void push(Object e) { stack.add(e); debug("push"); }
        public Object pop() { Object r = stack.remove(stack.size() - 1); debug("pop"); return r; }

        @Override public String toString() { return stack.toString(); }
    }

    interface Executable {
        void execute(RuntimeContext rc);
    }

    interface Compilable {
        WordType type();
        Executable body();

        public static Compilable of(WordType type, Executable body) {
            return new Compilable() {
                @Override public WordType type() { return type; }
                @Override public Executable body() { return body; }
            };
        }
    }

    static class Type4 {

        Map<String, Compilable> dictionary = new HashMap<>();

        void put(String name, Compilable c) {
            dictionary.put(name, c);
        }

        Block compile(String source) {
            return new Object() {
                Block block = new Block();
                int index = 0;
                int ch;

                int next() { return ch = index >= source.length() ? -1 : source.charAt(index++); }

                void skipSpaces() {
                    while (Character.isWhitespace(ch))
                        next();
                }
                void word(int first) {
                    StringBuilder sb = new StringBuilder();
                    if (first != -1)
                        sb.append((char) first);
                    while (ch != -1 && !Character.isWhitespace(ch)) {
                        sb.append((char) ch);
                        next();
                    }
                    block.add(dictionary.get(sb.toString()));
                }

                void number(int first) {
                    StringBuilder sb = new StringBuilder();
                    if (first != -1)
                        sb.append((char) first);
                    while (Character.isDigit(ch)) {
                        sb.append((char) ch);
                        next();
                    }
                    int value = Integer.valueOf(sb.toString());
                    block.add(Compilable.of(new WordType(new Type[0], new Type[] {INT}),
                        rc -> rc.push(value)));
                }

                void block() {
                    Block parent = block;
                    Block child = block = new Block();
                    parse();
                    if (ch != ']')
                        throw new RuntimeException();
                    next();  // skip ']'
                    // ラムダ式で参照するのはローカル変数child、
                    // インスタンス変数blockを参照してはいけない。
                    parent.add(Compilable.of(new WordType(new Type[0], new Type[] {BLOCK}),
                        rc -> rc.push(child)));
                    block = parent;
                }

                void token() {
                    switch (ch) {
                    case '0': case '1': case '2': case '3': case '4':
                    case '5': case '6': case '7': case '8': case '9':
                        number(-1);
                        break;
                    case '-':
                        if (Character.isDigit(next()))
                            number('-');
                        else
                            word('-');
                        break;
                    case '[':
                        block();
                        break;
                    default:
                        word(-1);
                        break;
                    }
                }

                Block parse() {
                    next();
                    while (true) {
                        skipSpaces();
                        if (ch == -1 || ch == ']')
                            break;
                        token();
                    }
                    return block;
                }
            }.parse();
        }
    }

    static class Block implements Executable {

        WordType type = new WordType(types(), types());


        final List<Executable> elements = new ArrayList<>();

        void add(Executable e) { elements.add(e); }

        void add(Compilable c) {
            type = type.composite(c.type());
            add(c.body());
        }

        @Override
        public void execute(RuntimeContext rc) {
            for (Executable e : elements)
                e.execute(rc);
        }

        public WordType type() { return type; }
    }

    @Test
    public void test() {
        Type4 type4 = new Type4();
        type4.put("+", Compilable.of(new WordType(types(INT, INT), types(INT)),
            rc -> {
                int b = (int) rc.pop(), a = (int) rc.pop();
                rc.push(a + b);
            }));
        type4.put(".", Compilable.of(new WordType(types(OBJECT), types()),
            rc -> System.out.println(rc.pop())));
        Block block = type4.compile("1 2 +");
        assertEquals(new WordType(types(), types(INT)), block.type());
        RuntimeContext rc = new RuntimeContext();
        block.execute(rc);
        assertEquals(3, rc.pop());
    }

    @Test
    public void testIf() {
        Type4 type4 = new Type4();
        type4.put("+", Compilable.of(new WordType(types(INT, INT), types(INT)),
            rc -> {
                int b = (int) rc.pop(), a = (int) rc.pop();
                rc.push(a + b);
            }));
        type4.put("true", Compilable.of(new WordType(types(), types(BOOLEAN)), rc -> rc.push(true)));
        type4.put("false", Compilable.of(new WordType(types(), types(BOOLEAN)), rc -> rc.push(false)));
        // 正しい型は`[Boolean [T] [T]] -> [T]`
        // thenとelseを実行したときの型が一致して、かつそれがifの型になる。
        // `Boolean Block Block while` は `Boolean [[] -> Boolean] [[] -> []] -> []`
        type4.put("if", Compilable.of(new WordType(types(BOOLEAN, BLOCK, BLOCK), types(INT)),
            rc -> {
                Block f = (Block)rc.pop(), t = (Block)rc.pop();
                if ((boolean)rc.pop())
                    t.execute(rc);
                else
                    f.execute(rc);
            }));
        Block block = type4.compile("true [1] [2] if");
        assertEquals(new WordType(types(), types(INT)), block.type());
        RuntimeContext rc = new RuntimeContext();
        block.execute(rc);
        assertEquals(1, rc.pop());
    }

}
