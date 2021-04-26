package puzzle.parsers;

import java.io.Reader;
import java.util.List;

public class Json {
    
    public interface ParseHandler {
        Object objectStart(List<Object> path);
        void objectElement(List<Object> path, Object object, String name, Object value);
        Object objectEnd(List<Object> path, Object object);
        Object arrayStart(List<Object> path);
        void arrayElement(List<Object> path, Object array, int index, Object value);
        Object arrayEnd(List<Object> path, Object array);
        Object string(List<Object> path, String string);
        Object number(List<Object> path, String number);
        Object keyword(List<Object> path, String keyword);
    }

    public static Object parse(Reader reader, ParseHandler handler) {
        return new Object() {
            
            Object parse() {
                return null;
            }
        }.parse();
    }

}
