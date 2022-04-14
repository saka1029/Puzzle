package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestScanner {

    static class RuntimeContext {
        final List<Object> stack = new ArrayList<>();
        int pc;

        void push(Object element) { stack.add(element); }
        Object pop() { return stack.remove(stack.size() - 1); }
    }

    interface Executable {
        void execute(RuntimeContext context);
    }

    interface Compiler {
        void compile(CompileContext context);
    }

    static class CompileContext {

        final List<Executable> codes = new ArrayList<>();
        final List<Integer> stack = new ArrayList<>();
        final Map<String, Compiler> dictionary = new HashMap<>();

        void execute(RuntimeContext c) {
            int size = codes.size();
            c.pc = 0;
            while (c.pc < size)
                codes.get(c.pc++).execute(c);
        }

        void parse(String input) {
            new Object() {
                int index = 0;
                int ch;

                int getCh() {
                    return ch = index < input.length() ? input.charAt(index++) : -1;
                }

                void parseString() {
                    getCh();
                    StringBuilder sb = new StringBuilder();
                    while (ch != -1 && ch != '\"') {
                        sb.append((char) ch);
                        getCh();
                    }
                    if (ch == -1)
                        throw new RuntimeException("'\"' expected");
                    getCh(); // skip last '\"'
                    codes.add(c -> c.push(sb.toString()));
                }

                void parseInteger(int prefix) {
                    StringBuilder sb = new StringBuilder();
                    if (prefix != -1)
                        sb.append((char) prefix);
                    while (Character.isDigit(ch)) {
                        sb.append((char) ch);
                        getCh();
                    }
                    codes.add(c -> c.push(Integer.valueOf(sb.toString())));
                }

                void parseWord(int prefix) {
                    StringBuilder sb = new StringBuilder();
                    if (prefix != -1)
                        sb.append((char) prefix);
                    while (ch != -1 && !Character.isWhitespace(ch)) {
                        sb.append((char) ch);
                        getCh();
                    }
                    String word = sb.toString();
                    dictionary.get(word).compile(CompileContext.this);
                }

                void parse() {
                    getCh();
                    L: while (ch != -1) {
                        while (Character.isWhitespace(ch))
                            getCh();
                        switch (ch) {
                        case -1:
                            break L;
                        case '\"':
                            parseString();
                            break;
                        case '-':
                            if (Character.isDigit(getCh()))
                                parseInteger('-');
                            else
                                parseWord('-');
                            break;
                        default:
                            if (Character.isDigit(ch))
                                parseInteger(-1);
                            else
                                parseWord(-1);
                            break;
                        }
                    }
                }
            }.parse();
        }
    }

    @Test
    public void testInteger() {
        CompileContext context = new CompileContext();
        context.dictionary.put("+", c -> c.codes.add(r -> r.push((int) r.pop() + (int) r.pop())));
        context.dictionary.put("-", c -> c.codes.add(r -> r.push(-(int) r.pop() + (int)r.pop())));
        context.dictionary.put("*", c -> c.codes.add(r -> r.push((int) r.pop() * (int) r.pop())));
        context.parse(" 1 2 +");
        context.parse(" 3 4 - *");
        RuntimeContext rc = new RuntimeContext();
        context.execute(rc);
        assertEquals(-3, rc.pop());
    }

    @Test
    public void testString() {
        CompileContext context = new CompileContext();
        context.dictionary.put("+", c -> c.codes.add(r -> { Object t = r.pop(); r.push(r.pop().toString() + t); }));
        context.parse("\"ABC\" 123 + ");
        RuntimeContext rc = new RuntimeContext();
        context.execute(rc);
        assertEquals("ABC123", rc.pop());
    }

    /*
     * <pre>
     * ... bool if ... else ... then ...
     * </pre>
     */
    static Executable branchFalse(int offset) {
        return r -> {
            if (!(boolean)r.pop())
                r.pc += offset;
        };
    }

    static Executable branchAlways(int offset) {
        return r -> r.pc += offset;
    }

    static void ifThenElse(CompileContext context) {
        context.dictionary.put("if", c -> {
            int pos = c.codes.size();
            c.codes.add(null);
            c.stack.add(pos);
        });
        context.dictionary.put("else", c -> {
            int ifPos = c.stack.remove(c.stack.size() - 1);
            int elsePos = c.codes.size();
            c.codes.set(ifPos, branchFalse(elsePos - ifPos));
            c.codes.add(null);
            c.stack.add(-elsePos);
        });
        context.dictionary.put("then", c -> {
            int prevPos = c.stack.remove(c.stack.size() - 1);
            int thenPos = c.codes.size();
            if (prevPos < 0)
                c.codes.set(-prevPos, branchAlways(thenPos + prevPos));
            else
                c.codes.set(prevPos, branchFalse(thenPos - prevPos));
            c.codes.add(r -> {}); // NOP
        });
        context.dictionary.put("true", c -> c.codes.add(r -> r.push(true)));
        context.dictionary.put("false", c -> c.codes.add(r -> r.push(false)));
    }

    @Test
    public void testIfElseThenTrue() {
        CompileContext context = new CompileContext();
        ifThenElse(context);
        context.parse("true if 1 else 2 then");
        RuntimeContext rc = new RuntimeContext();
        context.execute(rc);
        assertEquals(1, rc.pop());
    }

    @Test
    public void testIfElseTheFalse() {
        CompileContext context = new CompileContext();
        ifThenElse(context);
        context.parse("false if 1 else 2 then");
        RuntimeContext rc = new RuntimeContext();
        context.execute(rc);
        assertEquals(2, rc.pop());
    }

    @Test
    public void testIfThenTrue() {
        CompileContext context = new CompileContext();
        ifThenElse(context);
        context.parse("true if 1 then");
        RuntimeContext rc = new RuntimeContext();
        context.execute(rc);
        assertEquals(1, rc.pop());
    }

    @Test
    public void testIfThenFalse() {
        CompileContext context = new CompileContext();
        ifThenElse(context);
        context.parse("false if 1 then");
        RuntimeContext rc = new RuntimeContext();
        context.execute(rc);
        assertEquals(true, rc.stack.isEmpty());
    }

}
