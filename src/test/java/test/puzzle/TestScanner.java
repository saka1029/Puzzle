package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestScanner {

    interface Executable {
        void execute(RuntimeContext context);
    }

    static class RuntimeContext {
        final List<Object> stack = new ArrayList<>();

        int lastIndex() { return stack.size() - 1; }
        void push(Object element) { stack.add(element); }
        Object pop() { return stack.remove(lastIndex()); }

        final Map<String, Executable> dictionary = new HashMap<>();
        void put(String name, Executable executable) { dictionary.put(name, executable); }
        Executable get(String name) { return dictionary.get(name); }
    }

    static class Parser {
        final Reader reader;
        final RuntimeContext context;
        int ch;

        Parser(Reader reader, RuntimeContext context) throws IOException {
            this.reader = reader;
            this.context = context;
            getCh();
        }

        int getCh() throws IOException {
            if (ch != -1)
                ch = reader.read();
            return ch;
        }

        static boolean isWhitespace(int ch) {
            return Character.isWhitespace(ch);
        }

        static boolean isDigit(int ch) {
            return Character.isDigit(ch);
        }

        void parseString() throws IOException {
            getCh();
            StringBuilder sb = new StringBuilder();
            while (ch != -1 && ch != '\"') {
                sb.append((char)ch);
                getCh();
            }
            if (ch == -1)
                throw new RuntimeException("'\"' expected");
            getCh(); // skip last '\"'
            context.push(sb.toString());
        }

        void parseInteger(int prefix) throws IOException {
            StringBuilder sb = new StringBuilder();
            if (prefix != -1)
                sb.append((char) prefix);
            while (isDigit(ch)) {
                sb.append((char) ch);
                getCh();
            }
            context.push(Integer.valueOf(sb.toString()));
        }

        void parseWord(int prefix) throws IOException {
            StringBuilder sb = new StringBuilder();
            if (prefix != -1)
                sb.append((char) prefix);
            while (ch != -1 && !isWhitespace(ch)) {
                sb.append((char) ch);
                getCh();
            }
            String word = sb.toString();
            context.get(word).execute(context);
        }

        void parse() throws IOException {
            while (ch != -1) {
                while (isWhitespace(ch))
                    getCh();
                switch (ch) {
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
    }

    @Test
    void test() throws IOException {
        RuntimeContext context = new RuntimeContext();
        context.put("+", c -> c.push((int)c.pop() + (int)c.pop()));
        context.put("*", c -> c.push((int)c.pop() * (int)c.pop()));
        String s = "1 2 + 3 4 + *";
        Parser p = new Parser(new StringReader(s), context);
        p.parse();
        assertEquals(21, context.pop());
    }

}
