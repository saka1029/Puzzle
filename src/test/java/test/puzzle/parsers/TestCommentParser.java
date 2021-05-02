package test.puzzle.parsers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class TestCommentParser {

    /**
     * program = comment | any comment = '&#x2f;*' { NOT '*&#x2f;' } '*&#x2f;'
     *
     * @throws IOException
     */
    public static void removeComment(Reader reader, Writer writer) throws IOException {
        // inline comment
        boolean inComment = false;
        int ch = reader.read();
        while (ch != -1) {
            if (!inComment) {
                if (ch == '/') {
                    ch = reader.read();
                    if (ch == '*') {
                        ch = reader.read();
                        inComment = true;
                    } else
                        writer.write('/');
                } else {
                    writer.write(ch);
                    ch = reader.read();
                }
            } else {
                if (ch == '*') {
                    ch = reader.read();
                    if (ch == '/') {
                        ch = reader.read();
                        inComment = false;
                    }
                } else
                    ch = reader.read();
            }
        }
    }

    @Test
    void test() throws IOException {
        try (
            Reader reader = Files.newBufferedReader(
                Path.of("src/test/java/test/puzzle/parsers/TestCommentParser.java"));
            Writer writer = new StringWriter()) {
            removeComment(reader, writer);
            System.out.println(writer.toString());
        }
    }

    public static void removeComment2(Reader reader, Writer writer) throws IOException {
        new Object() {
            int ch = get();

            int get() throws IOException {
                return ch = reader.read();
            }

            void put(int ch) throws IOException {
                writer.write(ch);
            }

            boolean match(int expect) throws IOException {
                if (ch != expect) return false;
                get();
                return true;
            }

            void parse() throws IOException {
                boolean inComment = false;
                while (ch != -1) {
                    if (!inComment)
                        if (match('/'))
                            if (match('*'))
                                inComment = true;
                            else
                                put('/');
                        else {
                            put(ch);
                            get();
                        }
                    else if (match('*') && match('/'))
                        inComment = false;
                    else
                        get();
                }
            }
        }.parse();
    }

    @Test
    void test2() throws IOException {
        try (
            Reader reader = Files.newBufferedReader(
                Path.of("src/test/java/test/puzzle/parsers/TestCommentParser.java"));
            Writer writer = new StringWriter()) {
            removeComment2(reader, writer);
            System.out.println(writer.toString());
        }
    }

    public static void removeComment3(Reader reader, Writer writer) throws IOException {
        new Object() {
            int ch = get();

            int get() throws IOException {
                return ch = reader.read();
            }

            void put(int ch) throws IOException {
                writer.write(ch);
            }

            boolean match(int expect) throws IOException {
                if (ch != expect) return false;
                get();
                return true;
            }

            void comment() throws IOException {
                while (ch != -1)
                    if (match('*') && match('/'))
                        return;
                    else
                        get();
            }

            void parse() throws IOException {
                while (ch != -1) {
                    if (match('/'))
                        if (match('*'))
                            comment();
                        else
                            put('/');
                    put(ch);
                    get();
                }
            }
        }.parse();
    }

    @Test
    void test3() throws IOException {
        try (
            Reader reader = Files.newBufferedReader(
                Path.of("src/test/java/test/puzzle/parsers/TestCommentParser.java"));
            Writer writer = new StringWriter()) {
            removeComment3(reader, writer);
            System.out.println(writer.toString());
        }
    }
}
