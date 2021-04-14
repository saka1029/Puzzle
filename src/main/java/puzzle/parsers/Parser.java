package puzzle.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class Parser {

    public static final int EOF = -1;
    public static final int LP = '(', RP = ')';
    public static final int LB = '{', RB = '}';
    public static final int LS = '[', RS = ']';
    public static final int LT = '<', GT = '>';
    public static final int EQ = '=';
    public static final int COLON = ':', SEMICOLON = ';', DOT = '.';
    public static final int COMMA = ',';
    public static final int PERIOD = '.';
    public static final int QUOTE = '"', APOS = '\'';
    public static final int AND = '&', OR = '|';
    public static final int SLASH = '/', BACKSLASH = '\\';
    public static final int PLUS = '+', MINUS = '-';
    public static final int ASTERISK = '*';
    public static final int CARET = '^';
    public static final int QUESTION = '?', EXCL = '!', PERCENT = '%';
    public static final int SHARP = '#', DOLLAR = '$', ATMARK = '@';
    public static final int UNDERSCORE = '_';
    public static final int CR = '\r', LF = '\n';

    protected final BufferedReader reader;
    protected int ch;

    protected Parser(BufferedReader reader) throws IOException {
        this.reader = reader;
        this.ch = get();
    }

    protected int get() throws IOException {
        return ch = reader.read();
    }

    protected boolean isSpace(int ch) {
        return Character.isWhitespace(ch);
    }

    protected void spaces() throws IOException {
        while (isSpace(ch))
            get();
    }

    protected boolean eat(int expect) throws IOException {
        if (ch != expect)
            return false;
        get();
        return true;
    }

    protected boolean eat(Predicate<Integer> expect) throws IOException {
        if (!expect.test(ch))
            return false;
        get();
        return true;
    }

    protected void append(StringBuilder sb, int c) throws IOException {
        sb.append((char)c);
    }

    protected int appendGet(StringBuilder sb, int c) throws IOException {
        sb.append((char)c);
        return get();
    }

    protected void error(String format, Object... args) {
        throw new RuntimeException(String.format(format, args));
    }

    @FunctionalInterface
    public interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }

    @FunctionalInterface
    public interface IOFunction<T, R> {
        R apply(T t) throws IOException;
    }

    @FunctionalInterface
    public interface IOSupplier<T> {
        T get() throws IOException;
    }

    public static <T> void with(Reader reader,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        action.accept(constructor.apply(new BufferedReader(reader)));
    }

    public static <T, R> R with(Reader reader,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        return action.apply(constructor.apply(new BufferedReader(reader)));
    }

    public static <T> void with(BufferedReader reader,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        action.accept(constructor.apply(reader));
    }

    public static <T, R> R with(BufferedReader reader,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        return action.apply(constructor.apply(reader));
    }

    public static <T> void with(InputStream inputStream,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        with(new InputStreamReader(inputStream), constructor, action);
    }

    public static <T, R> R with(InputStream inputStream,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        return with(new InputStreamReader(inputStream), constructor, action);
    }

    public static <T> void with(InputStream inputStream, Charset encoding,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        with(new InputStreamReader(inputStream, encoding), constructor, action);
    }

    public static <T, R> R with(InputStream inputStream, Charset encoding,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        return with(new InputStreamReader(inputStream, encoding), constructor, action);
    }

    public static <T> void with(File file,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            action.accept(constructor.apply(reader));
        }
    }

    public static <T, R> R with(File file,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return action.apply(constructor.apply(reader));
        }
    }

    public static <T> void with(File file, Charset encoding,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, encoding))) {
            action.accept(constructor.apply(reader));
        }
    }

    public static <T, R> R with(File file, Charset encoding,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, encoding))) {
            return action.apply(constructor.apply(reader));
        }
    }

    public static <T> void with(Path file,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            action.accept(constructor.apply(reader));
        }
    }

    public static <T, R> R with(Path file,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return action.apply(constructor.apply(reader));
        }
    }

    public static <T> void with(Path file, Charset encoding,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, encoding)) {
            action.accept(constructor.apply(reader));
        }
    }

    public static <T, R> R with(Path file, Charset encoding,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, encoding)) {
            return action.apply(constructor.apply(reader));
        }
    }

    public static <T> void with(String source,
        IOFunction<BufferedReader, T> constructor, IOConsumer<T> action) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(source))) {
            action.accept(constructor.apply(reader));
        }
    }

    public static <T, R> R with(String source,
        IOFunction<BufferedReader, T> constructor, IOFunction<T, R> action) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(source))) {
            return action.apply(constructor.apply(reader));
        }
    }
}
