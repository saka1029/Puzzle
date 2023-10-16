package puzzle.language.framestack;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Reader {
    
    final java.io.Reader reader;
    int ch;
    
    Reader(java.io.Reader reader) {
        this.reader = reader;
        get();
    }
    
    public static Reader of(java.io.Reader reader) {
        return new Reader(reader);
    }
    
    public static Reader of(String source) {
        return new Reader(new StringReader(source));
    }
    
    RuntimeException error(String format, Object... args) {
        return new RuntimeException(format.formatted(args));
    }
    
    int get() {
        try {
            return ch = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }
    
    Quote quote() {
        get(); // skip '\''
        return Quote.of(read());
    }
    
    List list() {
        get(); // skip '('
        spaces();
        ArrayList<Executable> list = new ArrayList<>();
        while (ch != -1 && ch != ')') {
            list.add(read());
            spaces();
        }
        if (ch != ')')
            throw error("')' expected");
        get(); // skip ')'
        if (list.size() >= 3
            && list.get(0) instanceof Int args
            && list.get(1) instanceof Int returns
            && list.get(2).equals(Symbol.of(":"))) {
            return List.of(args.value, returns.value, list.stream().skip(3).toArray(Executable[]::new));
        } else
            return List.of(list.toArray(Executable[]::new));
    }
    
    static boolean isWord(int ch) {
        return switch (ch) {
            case '(', ')', '\'', -1 -> false;
            default -> !Character.isWhitespace(ch);
        };
    }

    static Pattern INTPAT = Pattern.compile("[+-]?\\d+");

    Executable word() {
        StringBuilder sb = new StringBuilder();
        while (isWord(ch)) {
            sb.append((char)ch);
            get();
        }
        String word = sb.toString();
        if (INTPAT.matcher(word).matches())
            return Int.of(Integer.parseInt(word));
        else
            return Symbol.of(word);
    }

    Executable read() {
        spaces();
        switch (ch) {
            case -1:
                return null;
            case '\'':
                return quote();
            case '(':
                return list();
            case ')':
                throw error("Unexpected ')'");
            default:
                return word();
        }
    }

}
