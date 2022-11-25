package puzzle.language;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Stack {

    private Stack() {
    }

    public static class Context {

        private final Map<String, Value> globals = new HashMap<>();

        public Context put(String name, Executable e) {
            globals.put(name, code(name, e));
            return this;
        }

        private final Value[] stack;
        int sp = 0;
        private Consumer<String> trace;
        int nest = 0;

        private Context(int stackSize) {
            this.stack = new Value[stackSize];
        }

        public static Context of(int stackSize) {
            return new Context(stackSize);
        }

        public Context traceTo(Consumer<String> trace) {
            this.trace = trace;
            return this;
        }

        public void trace(Object output) {
            if (trace != null)
                trace.accept("  ".repeat(nest) + output);
        }

        public void trace(Value v) {
            if (trace != null)
                trace(this + " : " + v);
        }

        public void trace() {
            if (trace != null)
                trace(this);
        }

        public void execute(Value v) {
            trace(v);
            ++nest;
            v.execute(this);
            --nest;
        }

        public boolean isEmpty() {
            return sp == 0;
        }

        public void push(Value v) {
            stack[sp++] = v;
        }

        public Value pop() {
            return stack[--sp];
        }

        public Value peek(int index) {
            return stack[sp - 1 - index];
        }

        @Override
        public String toString() {
            return Arrays.stream(stack)
                .limit(sp)
                .map(Object::toString)
                .collect(Collectors.joining(" ", "[", "]"));
        }

        public static Value code(String name) {
            return code(name, c -> {
                Value x = c.globals.get(name);
                if (x == null)
                    throw new RuntimeException(name + " is not defined");
                x.run(c);
            });
        }

        public static Value code(String name, Executable e) {
            return new Value() {

                @Override
                public void execute(Context c) {
                    e.execute(c);
                }

                @Override
                public String toString() {
                    return name;
                }
            };
        }
    }

    public interface Executable {
        void execute(Context c);
    }

    public interface Value extends Executable, Comparable<Value>, Iterable<Value> {

        @Override
        default void execute(Context c) {
            c.push(this);
        }

        default void run(Context c) {
            execute(c);
        }

        @Override
        default int compareTo(Value o) {
            throw new UnsupportedOperationException();
        }

        @Override
        default Iterator<Value> iterator() {
            throw new UnsupportedOperationException();
        }

        default Value car() {
            if (this instanceof Cons cons)
                return cons.car;
            if (this instanceof Str str)
                return Int.of(str.value[0]);
            throw new UnsupportedOperationException();
        }

        default Value cdr() {
            if (this instanceof Cons cons)
                return cons.cdr;
            if (this instanceof Str str)
                return Str.of(Arrays.copyOfRange(str.value, 1, str.value.length));
            throw new UnsupportedOperationException();
        }

        default Value cons(Value tail) {
            if (tail instanceof Cons cons)
                return Cons.of(this, cons);
            if (tail instanceof Str str)
                return Str.cons(((Int) this).value, str.value);
            throw new UnsupportedOperationException();
        }

        default Bool eq(Value right) {
            return Bool.of(equals(right));
        }

        default Bool ne(Value right) {
            return Bool.of(!equals(right));
        }

        default Bool lt(Value right) {
            return Bool.of(compareTo(right) < 0);
        }

        default Bool le(Value right) {
            return Bool.of(compareTo(right) <= 0);
        }

        default Bool gt(Value right) {
            return Bool.of(compareTo(right) > 0);
        }

        default Bool ge(Value right) {
            return Bool.of(compareTo(right) >= 0);
        }

        default Value and(Value right) {
            return Bool.of(((Bool) this).value & ((Bool) right).value);
        }

        default Value or(Value right) {
            return Bool.of(((Bool) this).value | ((Bool) right).value);
        }

        default Value not() {
            return Bool.of(!((Bool) this).value);
        }

        default Value add(Value right) {
            if (this instanceof Int i)
                return Int.of(i.value + ((Int) right).value);
            if (this instanceof Cons c)
                return c.append((Cons) right);
            if (this instanceof Str s)
                return s.append((Str) right);
            throw new UnsupportedOperationException();
        }

        default Value sub(Value right) {
            return Int.of(((Int) this).value - ((Int) right).value);
        }

        default Value mul(Value right) {
            return Int.of(((Int) this).value * ((Int) right).value);
        }

        default Value div(Value right) {
            return Int.of(((Int) this).value / ((Int) right).value);
        }

        default Value mod(Value right) {
            return Int.of(((Int) this).value % ((Int) right).value);
        }

        default Value sqrt() {
            return Int.of((int) Math.sqrt(((Int) this).value));
        }

        default Cons list() {
            Cons.Builder builder = Cons.builder();
            for (Value v : this)
                builder.add(v);
            return builder.build();
        }

        default Str str() {
            Str.Builder builder = Str.builder();
            for (Value v : this)
                if (v instanceof Int i)
                    builder.add(i.value);
                else
                    throw new RuntimeException("Int expected but " + v);
            return builder.build();
        }
    }

    public static class Bool implements Value {

        public static final Bool TRUE = new Bool(true);
        public static final Bool FALSE = new Bool(false);

        public final boolean value;

        private Bool(boolean value) {
            this.value = value;
        }

        public static Bool of(boolean value) {
            return value ? TRUE : FALSE;
        }

        @Override
        public int hashCode() {
            return Boolean.hashCode(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Bool other = (Bool) obj;
            return value == other.value;
        }

        @Override
        public int compareTo(Value o) {
            if (!(o.getClass() != getClass()))
                throw new UnsupportedOperationException();
            return Boolean.compare(value, ((Bool) o).value);
        }

        @Override
        public String toString() {
            return Boolean.toString(value);
        }
    }

    public static class Int implements Value {

        public static final Int ZERO = new Int(0);
        public static final Int ONE = new Int(1);

        public final int value;

        private Int(int value) {
            this.value = value;
        }

        public static Int of(int value) {
            return new Int(value);
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Int other = (Int) obj;
            return value == other.value;
        }

        @Override
        public int compareTo(Value o) {
            if (o.getClass() == Int.class)
                return Integer.compare(value, ((Int) o).value);
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }

    public static class Cons implements Value {

        public static final Cons NIL = new Cons(null, null);
        public final Value car;
        public final Cons cdr;

        private Cons(Value car, Cons cdr) {
            this.car = car;
            this.cdr = cdr;
        }

        public static Cons of(Value... values) {
            Cons result = NIL;
            for (int i = values.length - 1; i >= 0; --i)
                result = new Cons(values[i], result);
            return result;
        }

        public static Cons of(Value head, Cons tail) {
            return new Cons(head, tail);
        }

        public Cons append(Cons right) {
            if (this == NIL)
                return right;
            else
                return new Cons(car, cdr.append(right));
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            final ArrayList<Value> list = new ArrayList<>();

            public Builder add(Value v) {
                list.add(v);
                return this;
            }

            public Cons build() {
                Cons result = NIL;
                for (int i = list.size() - 1; i >= 0; --i)
                    result = new Cons(list.get(i), result);
                return result;
            }
        }

        @Override
        public void run(Context c) {
            for (Cons cons = this; cons != NIL; cons = cons.cdr)
                c.execute(cons.car);
            c.trace();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            for (Cons c = this; c != NIL; c = c.cdr)
                result = prime * result + car.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Cons other = (Cons) obj;
            if (this == NIL || other == NIL)
                return false;
            return car.equals(other.car) && cdr.equals(other.cdr);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            String sep = "";
            for (Cons c = this; c != NIL; c = c.cdr, sep = " ")
                sb.append(sep).append(c.car);
            sb.append("]");
            return sb.toString();
        }

        @Override
        public Iterator<Value> iterator() {
            return new Iterator<>() {
                Cons cons = Cons.this;

                @Override
                public boolean hasNext() {
                    return cons != NIL;
                }

                @Override
                public Value next() {
                    Value result = cons.car;
                    cons = cons.cdr;
                    return result;
                }
            };
        }
    }

    public static class Str implements Value {

        final int[] value;

        private Str(int[] value) {
            this.value = value;
        }

        public static Str of(String s) {
            return new Str(s.codePoints().toArray());
        }

        public static Str of(int[] value) {
            return new Str(value.clone());
        }

        public static Str cons(int head, int[] value) {
            int length = value.length;
            int[] a = new int[length + 1];
            a[0] = head;
            System.arraycopy(value, 0, a, 1, length);
            return new Str(a);
        }

        public Str append(Str right) {
            int[] a = new int[value.length + right.value.length];
            System.arraycopy(this.value, 0, a, 0, value.length);
            System.arraycopy(right.value, 0, a, value.length, right.value.length);
            return new Str(a);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            ArrayList<Integer> list = new ArrayList<>();

            public Builder add(int n) {
                list.add(n);
                return this;
            }

            public Str build() {
                return new Str(list.stream().mapToInt(Integer::intValue).toArray());
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Str other = (Str) obj;
            return Arrays.equals(value, other.value);
        }

        @Override
        public int compareTo(Value o) {
            if (o.getClass() != getClass())
                throw new UnsupportedOperationException();
            return Arrays.compare(value, ((Str) o).value);
        }

        @Override
        public String toString() {
            return new String(value, 0, value.length);
        }

        @Override
        public Iterator<Value> iterator() {
            return new Iterator<>() {
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < value.length;
                }

                @Override
                public Value next() {
                    return Int.of(value[index++]);
                }
            };
        }
    }

    public static Value range(Value start, Value end) {
        int istart = ((Int) start).value;
        int iend = ((Int) end).value;
        return new Value() {

            @Override
            public Iterator<Value> iterator() {
                return new Iterator<>() {
                    int current = istart;

                    @Override
                    public boolean hasNext() {
                        return current <= iend;
                    }

                    @Override
                    public Value next() {
                        return Int.of(current++);
                    }
                };
            }

            @Override
            public String toString() {
                return String.format("{%s %s range}", start, end);
            }
        };
    }

    public static Value map(Value collection, Function<Value, Value> mapper) {
        return new Value() {
            @Override
            public Iterator<Value> iterator() {
                return new Iterator<>() {
                    Iterator<Value> iterator = collection.iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Value next() {
                        return mapper.apply(iterator.next());
                    }
                };
            }

            @Override
            public String toString() {
                return "{" + collection + " " + mapper + " map}";
            }
        };
    }

    public static Value filter(Value collection, Function<Value, Value> filter) {
        return new Value() {
            @Override
            public Iterator<Value> iterator() {
                return new Iterator<Value>() {
                    Iterator<Value> iterator = collection.iterator();
                    boolean hasNext = advance();
                    Value next;

                    boolean advance() {
                        while (iterator.hasNext()) {
                            next = iterator.next();
                            Value r = filter.apply(next);
                            if (r instanceof Bool b) {
                                if (b.value)
                                    return true;
                            } else
                                throw new RuntimeException("boolean expected but " + r);
                        }
                        return false;
                    }

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public Value next() {
                        Value result = next;
                        hasNext = advance();
                        return result;
                    }
                };
            }

            @Override
            public String toString() {
                return "{" + collection + " " + filter + " filter}";
            }
        };
    }

    public static class ParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseException(String format, Object... args) {
            super(format.formatted(args));
        }
    }


    /**
     * <pre>
     * SYNTAX
     * value       =  word | block | char | integer | string | symbol
     * word        =  word-char { word-char }
     * block       = '[' { value } ']'
     * char        = ' character '
     * integer     = [ '+' | '-' ]
     *             ( decimal-digits
     *             | ( '0b' | '0B' ) binary-digits
     *             | ( '0x' | '0X' ) hex-digits
     *             | '0' octal-digits )
     * string      = '"' { string-char } '"'
     * string-char = '\r' | '\n' | '\t' | '\f' | '\' 'u' '{' hexadecimal-digits '}' | any-char-except-quote-and-escape
     * symbol      = '\' symbol-char { symbol-char }
     * </pre>
     * 
     * word-charは'[', ']'以外の文字の並びです。
     * integerは<code>Integer.decode(String nm) throws NumberFormatException</code>で
     * 読み取り可能な整数表現です。
     */
    public static class Reader {
        public static final Value END = Context.code("Reader_END",
            c -> {throw new RuntimeException("Reader_END");});
        final java.io.Reader reader;
        int ch;

        public Reader(java.io.Reader reader) {
            this.reader = reader;
            get();
        }

        void get() {
            try {
                int ch = reader.read();
                if (ch == -1)
                    this.ch = ch;
                else if (Character.isHighSurrogate((char) ch)) {
                    int low = reader.read();
                    if (ch == -1)
                        throw new IOException("expected low surrogate");
                    this.ch = Character.toCodePoint((char) ch, (char) low);
                } else
                    this.ch = ch;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Value parseList() {
            get();  // eat '['
            Cons.Builder cb = Cons.builder();
            while (ch != -1 && ch != ']')
                cb.add(read());
            if (ch != ']')
                throw new ParseException("']' expected");
            get();  // eat ']'
            return cb.build();
        }
        
        void appendGet(StringBuilder sb, int ch) {
            sb.appendCodePoint(ch);
            get();
        }
        
        static boolean isHexadecimalDigit(int ch) {
            return ch >= '0' && ch <= '9' 
                || ch >= 'a' && ch <= 'f'
                || ch >= 'A' && ch <= 'F';
        }

        Str parseString() {
            get(); // eat '"'
            StringBuilder sb = new StringBuilder();
            while (ch != -1 && ch != '"') {
                if (ch == '\\') {
                    get(); // eat '\\'
                    if (ch == 'r')
                        appendGet(sb, '\r');
                    else if (ch == 'n')
                        appendGet(sb, '\n');
                    else if (ch == 't')
                        appendGet(sb, '\t');
                    else if (ch == 'b')
                        appendGet(sb, '\b');
                    else if (ch == 'f')
                        appendGet(sb, '\f');
                    else if (ch == 'u') {
                        get(); // eat 'u'
                        if (ch == '{') {
                            get(); // eat '{'
                            StringBuilder uni = new StringBuilder();
                            while (isHexadecimalDigit(ch))
                                appendGet(uni, ch);
                            if (ch != '}')
                                throw new ParseException("unicode escape sequence '}' expected");
                            if (uni.length() == 0)
                                throw new ParseException("empty unicode escape sequence");
                            appendGet(sb, Integer.parseInt(uni.toString(), 16));
                        } else
                            sb.appendCodePoint('u');
                    } else
                        appendGet(sb, ch);
                } else 
                    appendGet(sb, ch);
            }
            if (ch != '"')
                throw new ParseException("'\"' expected");
            get();  // eat '"'
            return Str.of(sb.toString());
        }

        static boolean isWordChar(int ch) {
            return ch != -1
                && !Character.isWhitespace(ch)
                && ch != '[' && ch != ']';
        }

        static final Pattern CHAR_REGEX = Pattern.compile("(?i)'(.)'");
        static final Pattern INTEGER_REGEX = Pattern.compile("(?i)[+-]?([0-9]+|0x[0-9a-f]+|0[0-7]+)");
        static final Pattern BINARY_REGEX = Pattern.compile("(?i)([+-])?0b([01]+)");
        static final Map<String, Value> CONSTANTS = Map.of("true", Bool.TRUE, "false", Bool.FALSE);

        Value parseSymbol() {
            get();  // eat '/'
            if (ch == -1 || Character.isWhitespace(ch))
                return Context.code("/");
            StringBuilder sb = new StringBuilder();
            while (isWordChar(ch))
                appendGet(sb, ch);
            return Str.of(sb.toString());
        }

        Value parseWordNumber() {
            StringBuilder sb = new StringBuilder();
            while (isWordChar(ch))
                appendGet(sb, ch);
            String word = sb.toString();
            Matcher m;
            if ((m = CHAR_REGEX.matcher(word)).matches())
                return Int.of(Character.codePointAt(m.group(1), 0));
            else if ((m = INTEGER_REGEX.matcher(word)).matches())
                return Int.of(Integer.decode(word));
            else if ((m = BINARY_REGEX.matcher(word)).matches())
                return Int.of(Integer.parseInt(
                    (m.group(1) == null ? "" : m.group(1)) + m.group(2) , 2));
            Value constant = CONSTANTS.get(word);
            if (constant != null)
                return constant;
            return Context.code(word);
        }

        public Value read() {
            while (Character.isWhitespace(ch))
                get();
            switch (ch) {
            case -1:
                return END;
            case '[':
                return parseList();
            case ']':
                throw new ParseException("unexpected ']'");
            case '"':
                return parseString();
            case '/':
                return parseSymbol();
            default:
                return parseWordNumber();
            }
        }
    }

    public static void repl(Context context, java.io.Reader reader) {
        Reader valueReader = new Reader(reader);
        while (true) {
            Value element = valueReader.read();
            if (element == Reader.END)
                break;
            context.execute(element);
        }
        context.trace();
    }

    public static void repl(Context context, String source) {
        repl(context, new StringReader(source));
    }

    public static Value parse(Context context, String source) {
        try (java.io.Reader reader = new StringReader(source)) {
            Reader valueReader = new Reader(reader);
            return valueReader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Value eval(Context context, String source) {
        repl(context, source);
        return context.pop();
    }

    public static Function<Value, Value> function(Context c, Value f) {
        return new Function<Value, Value>() {
            @Override
            public Value apply(Value t) {
                c.push(t);
                f.run(c);
                return c.pop();
            }

            @Override
            public String toString() {
                return f.toString();
            }
        };
    }

    public static Context context(int stackSize) {
        Context context = Context.of(stackSize)
            .put("drop", c -> c.pop())
            .put("dup", c -> c.push(c.peek(0)))
            .put("over", c -> c.push(c.peek(1)))
            .put("over2", c -> c.push(c.peek(2)))
            .put("stack", c -> System.out.println("stack: sp=" + c.sp + " " + c))
            .put("swap", c -> {
                Value t = c.pop(), s = c.pop();
                c.push(t);
                c.push(s);
            })
            .put("null", c -> c.push(c.pop().eq(Cons.NIL)))
            .put("car", c -> c.push(c.pop().car()))
            .put("cdr", c -> c.push(c.pop().cdr()))
            .put("cons", c -> {
                Value t = c.pop();
                c.push(c.pop().cons(t));
            })
            .put("&", c -> {
                Value r = c.pop();
                c.push(c.pop().and(r));
            })
            .put("|", c -> {
                Value r = c.pop();
                c.push(c.pop().or(r));
            })
            .put("!", c -> c.push(c.pop().not()))
            .put("==", c -> {
                Value r = c.pop();
                c.push(c.pop().eq(r));
            })
            .put("!=", c -> {
                Value r = c.pop();
                c.push(c.pop().ne(r));
            })
            .put("<", c -> {
                Value r = c.pop();
                c.push(c.pop().lt(r));
            })
            .put("<=", c -> {
                Value r = c.pop();
                c.push(c.pop().le(r));
            })
            .put(">", c -> {
                Value r = c.pop();
                c.push(c.pop().gt(r));
            })
            .put(">=", c -> {
                Value r = c.pop();
                c.push(c.pop().ge(r));
            })
            .put("+", c -> {
                Value r = c.pop();
                c.push(c.pop().add(r));
            })
            .put("-", c -> {
                Value r = c.pop();
                c.push(c.pop().sub(r));
            })
            .put("*", c -> {
                Value r = c.pop();
                c.push(c.pop().mul(r));
            })
            .put("/", c -> {
                Value r = c.pop();
                c.push(c.pop().div(r));
            })
            .put("%", c -> {
                Value r = c.pop();
                c.push(c.pop().mod(r));
            })
            .put("sqrt", c -> c.push(c.pop().sqrt()))
            .put("exec", c -> c.pop().run(c))
            .put("if", c -> {
                Value orElse = c.pop(), then = c.pop(), predicate = c.pop();
                if (((Bool) predicate).value)
                    then.run(c);
                else
                    orElse.run(c);
            })
            .put("map", c -> {
                Value f = c.pop();
                c.push(map(c.pop(), function(c, f)));
            })
            .put("filter", c -> {
                Value f = c.pop();
                c.push(filter(c.pop(), function(c, f)));
            })
            .put("define", c -> {
                Value f = c.pop();
                c.globals.put(c.pop().toString(), f);
            })
            .put("list", c -> c.push(c.pop().list()))
            .put("str", c -> c.push(c.pop().str()))
            .put("range", c -> {
                Value e = c.pop();
                c.push(range(c.pop(), e));
            })
            .put("for", c -> {
                Value lambda = c.pop(), i = c.pop();
                for (Value v : i) {
                    c.push(v);
                    lambda.run(c);
                }
            })
            .put("while", c -> {
                Value body = c.pop(), predicate = c.pop();
                while (true) {
                    predicate.run(c);
                    if (!((Bool) c.pop()).value)
                        break;
                    body.run(c);
                }
            });
        return context;
    }
}
