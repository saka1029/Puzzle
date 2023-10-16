package puzzle.language.framestack;

import java.io.IOException;
import java.io.StringReader;

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
    
    List list() {
        
    }
    
    Executable word() {
        
    }

    Executable read() {
        spaces();
        switch (ch) {
            case '(':
                return list();
            case ')':
                throw error("Unexpected ')'");
            default:
                return word();
        }
    }

}
