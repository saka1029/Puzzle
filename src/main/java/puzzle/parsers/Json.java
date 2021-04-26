package puzzle.parsers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/*
 * <pre>
 * json = value.
 * value = object | array | string | number | "true" | "false" | "null".
 * object = '{' [ member { ',' member } '}'.
 * member = string ':' value.
 * array = '[' [ value { "," value } ']'.
 * string = '"' characters '"'.
 * characters = character { character }.
 * character = '0020' ... '10FFFF' - '"' - '\' | '\' escape.
 * escape = '"' | '\' | '/' | 'b' | 'f' | 'n' | 'r' | 't' | 'u' hex hex hex hex.
 * hex = digit | 'A' ... 'F' | 'a' ... 'f'.
 * number = integer [ fraction ] [ exponent ].
 * integer = [ '-' ] ( '0' | '1' ... '9' { digit } ).
 * fraction = '.' digit { digit }.
 * exponent = ( 'E' | 'e' ) [ '+' | '-' ] digit { digit }.
 * digit = '0' ... '9'
 * </pre>
 * Json selector
 *
 * or        ::= selector { ',' selector } [ attribute ]
 * selector  ::= member { ['>'] member }
 * member    ::= string | integer
 */
public class Json {

    public interface ParseHandler {
        Object objectStart(List<Object> path);
        void objectMember(List<Object> path, Object object, String key, Object value);
        Object objectEnd(List<Object> path, Object object);
        Object arrayStart(List<Object> path);
        void arrayMember(List<Object> path, Object array, int index, Object value);
        Object arrayEnd(List<Object> path, Object array);
        default Object number(List<Object> path, String s) { return Double.parseDouble(s); }
        default Object string(List<Object> path, String s) { return s; };
        default Object keyword(List<Object> path, String s) {
            switch (s) {
            case "true": return Boolean.TRUE;
            case "false": return Boolean.FALSE;
            case "null": return null;
            default: return s;
            }
        }
    }

    public static class DefaultParseHandler implements ParseHandler {
        @Override public Object objectStart(List<Object> path) {
            return new LinkedHashMap<>();
        }
        @SuppressWarnings("unchecked")
        @Override public void objectMember(List<Object> path, Object object, String key, Object value) {
            if (!(object instanceof Map)) return;
            ((Map<String, Object>)object).put(key, value);
        }
        @Override public Object objectEnd(List<Object> path, Object object) { return object; }
        @Override public Object arrayStart(List<Object> path) { return new ArrayList<>(); }
        @SuppressWarnings("unchecked")
        @Override public void arrayMember(List<Object> path, Object array, int index, Object value) {
            if (!(array instanceof List)) return;
            ((List<Object>)array).add(value);
        }
        @Override public Object arrayEnd(List<Object> path, Object array) { return array; }
    }

    public static ParseHandler DEFAULT_PARSE_HANDLER = new DefaultParseHandler();

    public static class NullParseHandler implements ParseHandler {
        @Override public Object objectStart(List<Object> path) { return null; }
        @Override public void objectMember(List<Object> path, Object object, String key, Object value) {}
        @Override public Object objectEnd(List<Object> path, Object object) { return null; }
        @Override public Object arrayStart(List<Object> path) { return null; }
        @Override public void arrayMember(List<Object> path, Object array, int index, Object value) {}
        @Override public Object arrayEnd(List<Object> path, Object array) { return null; }
    }

    public static Object parse(Reader reader, ParseHandler handler) throws IOException {
        return new Object() {
            int ch = get();
            LinkedList<Object> path0 = new LinkedList<>();
            List<Object> path = Collections.unmodifiableList(path0);

            int get() throws IOException {
                return ch = reader.read();
            }

            boolean isKeywordFirst(int ch) {
                return Character.isLetter(ch) || ch == '_';
            }

            boolean isKeywordRest(int ch) {
                return isKeywordFirst(ch) || Character.isDigit(ch);
            }

            boolean isDigit(int ch) {
                return ch >= '0' && ch <= '9';
            }

            boolean isHex(int ch) {
                return isDigit(ch) || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f';
            }

            void spaces() throws IOException {
                while (Character.isWhitespace(ch))
                    get();
            }

            boolean eat(int expected) throws IOException {
                spaces();
                if (ch != expected)
                    return false;
                get();
                return true;
            }

            void appendGet(StringBuilder sb, int ch) throws IOException {
                sb.append((char)ch);
                get();
            }

            Object object() throws IOException {
                Object object = handler.objectStart(path);
                spaces();
                while (ch != -1 && ch != '}') {
                    Object key;
                    if (eat('"')) key = string();
                    else if (isKeywordFirst(ch)) key = keyword();
                    else throw new RuntimeException("string expected but '" + ((char)ch) + "'");
                    if (!eat(':')) throw new RuntimeException("':' expected");
                    String keyString = Objects.toString(key);
                    path0.addLast(keyString);
                    Object value = value();
                    path0.removeLast();
                    handler.objectMember(path, object, Objects.toString(key), value);
                    if (!eat(',')) break;
                }
                if (!eat('}')) throw new RuntimeException("'}' expected");
                return handler.objectEnd(path, object);
            }

            Object array() throws IOException {
                Object array = handler.arrayStart(path);
                spaces();
                for (int index = 0; ch != -1 && ch != ']'; ++index) {
                    path0.addLast(index);
                    Object value = value();
                    path0.removeLast();
                    handler.arrayMember(path, array, index, value);
                    if (!eat(',')) break;
                }
                if (!eat(']')) throw new RuntimeException("']' expected");
                return handler.arrayEnd(path, array);
            }

            void unicode(StringBuilder sb) throws IOException {
                int start = sb.length();
                get(); // skip 'u'
                for (int i = 0; i < 4; ++i) {
                    if (!isHex(ch))
                        throw new RuntimeException("malformed unicode escape '" + ((char)ch) + "'");
                    appendGet(sb, ch);
                }
                int unicode = Integer.parseInt(sb.substring(start), 16);
                sb.setLength(start);
                sb.append((char)unicode);
            }

            /**
             *  string = '"' characters '"'.
             *  characters = character { character }.
             *  character = '0020' ... '10FFFF' - '"' - '\' | '\' escape.
             *  escape = '"' | '\' | '/' | 'b' | 'f' | 'n' | 'r' | 't' | 'u' hex hex hex hex.
             *  hex = digit | 'A' ... 'F' | 'a' ... 'f'.
             */
            Object string() throws IOException {
                StringBuilder sb = new StringBuilder();
                while (ch != -1 && ch != '"')
                    if (ch == '\\') {
                        get();
                        switch (ch) {
                        case '"': appendGet(sb, '"'); break;
                        case '\\': appendGet(sb, '\\'); break;
                        case '/': appendGet(sb, '/'); break;
                        case 'b': appendGet(sb, '\b'); break;
                        case 'f': appendGet(sb, '\f'); break;
                        case 'n': appendGet(sb, '\n'); break;
                        case 'r': appendGet(sb, '\r'); break;
                        case 't': appendGet(sb, '\t'); break;
                        case 'u': unicode(sb); break;
                        default: appendGet(sb, ch); break;
                        }
                    } else
                        appendGet(sb, ch);
                if (ch != '"') throw new RuntimeException("'\"' expected");
                get();
                return handler.string(path, sb.toString());
            }

            Object number() throws IOException {
                StringBuilder sb = new StringBuilder();
                do {
                    appendGet(sb, ch);
                } while (isDigit(ch));
                if (ch == '.')
                    do {
                        appendGet(sb, ch);
                    } while (isDigit(ch));
                if (ch == 'e' || ch == 'E') {
                    appendGet(sb, ch);
                    if (ch == '+' || ch == '-')
                        appendGet(sb, ch);
                    while (isDigit(ch))
                        appendGet(sb, ch);
                }
                return handler.number(path, sb.toString());
            }

            Object keyword() throws IOException {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append((char)ch);
                    get();
                } while (isKeywordRest(ch));
                return handler.keyword(path, sb.toString());
            }

            Object value() throws IOException {
                if (eat('{'))
                    return object();
                else if (eat('['))
                    return array();
                else if (eat('"'))
                    return string();
                else if (ch == '-' || Character.isDigit(ch))
                    return number();
                else if (isKeywordFirst(ch))
                    return keyword();
                throw new RuntimeException("unknown character '" + ((char)ch) + "'");
            }

            Object parse() throws IOException {
                return value();
            }
        }.parse();
    }

    public static Object parse(String source, ParseHandler handler) throws IOException {
        try (StringReader reader = new StringReader(source)) {
            return parse(reader, handler);
        }
    }

    public static Object parse(String source) throws IOException {
        return parse(source, DEFAULT_PARSE_HANDLER);
    }

    /*
     * <pre>
     * path = [a, b, c, d]
     * pattern:
     *  []  : true
     *  [x] : false
     *  [d] : true
     *  [c, d] : true
     *  [a, d] : true
     *  [a, b, c, d] : true
     *  [x, a, b, c, d] : false
     * </pre>
     *
     */
    static boolean match(Object path, Object search) {
        return search.equals(path) || search.equals("*");
    }

    public static boolean match(List<Object> path, Object... search) {
        int p = path.size() - 1;
        int s = search.length - 1, start = s;
        for ( ; p >= 0 && s >= 0; --p)
            if (match(path.get(p), search[s]))
                --s;
            else if (s == start)
                return false;
        return s < 0;

    }
    public static boolean match(List<Object> path, List<Object> search) {
        int p = path.size() - 1;
        int s = search.size() - 1, start = s;
        for ( ; p >= 0 && s >= 0; --p)
            if (match(path.get(p), search.get(s)))
                --s;
            else if (s == start)
                return false;
        return s < 0;
    }

    public static List<Object> select(Object target, List<Object> search) {
        LinkedList<Object> path = new LinkedList<>();
        List<Object> result = new ArrayList<>();
        new Object() {

            void array(List<?> target) {
                for (int i = 0, size = target.size(); i < size; ++i) {
                    path.addLast(i);
                    select(target.get(i));
                    path.removeLast();
                }
            }

            void object(Map<?, ?> target) {
                for (Entry<?, ?> e : target.entrySet()) {
                    path.addLast(e.getKey());
                    select(e.getValue());
                    path.removeLast();
                }
            }

            void select(Object target) {
                if (match(path, search))
                    result.add(target);
                if (target instanceof List<?> array)
                    array(array);
                else if (target instanceof Map<?, ?> object)
                    object(object);
            }
        }.select(target);
        return result;
    }

    static class IndexedIterator implements Iterator<Entry<Integer, Object>> {

        final Iterator<?> iterator;
        int index = 0;

        public IndexedIterator(List<?> list) {
            this.iterator = list.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Entry<Integer, Object> next() {
            return Map.entry(index++, iterator.next());
        }
    }

    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        List<T> list = new ArrayList<>(elements.length);
        for (T e : elements)
            list.add(e);
        return list;
    }

    public static Map<Object, Object> map(Object... elements) {
        int size = elements.length;
        if (size % 2 == 1)
            throw new IllegalArgumentException("elements");
        Map<Object, Object> map = new LinkedHashMap<>(size / 2);
        for (int i = 0; i < size; i += 2)
            map.put(elements[i], elements[i + 1]);
        return map;
    }

    public static Object get(Object object, Object... indexes) {
        for (Object index : indexes)
            if (object instanceof Map map)
                object = map.get(index);
            else if (object instanceof List list)
                object = list.get((int)index);
            else
                return null;
        return object;
    }

    public static Iterable<Entry<List<Object>, Object>> traverse(Object target) {
        return () -> new Iterator<Entry<List<Object>, Object>>() {

            LinkedList<Iterator<? extends Entry<?, ?>>> iterators = new LinkedList<>();
            LinkedList<Object> path = new LinkedList<>();

            boolean hasNext = true;
            Object next = target;

            boolean advance() {
                if (next instanceof List<?> list)
                    iterators.addLast(new IndexedIterator(list));
                else if (next instanceof Map<?, ?> map)
                    iterators.addLast(map.entrySet().iterator());
                else if (!path.isEmpty())
                    path.removeLast();
                while (!iterators.getLast().hasNext()) {
                    if (!path.isEmpty())
                        path.removeLast();
                    iterators.removeLast();
                    if (iterators.isEmpty())
                        return false;
                }
                Entry<?, ?> e = iterators.getLast().next();
                path.add(e.getKey());
                next = e.getValue();
                return true;
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Entry<List<Object>, Object> next() {
                List<Object> key = new ArrayList<>(path);
                Entry<List<Object>, Object> result = Map.entry(key, next);
                hasNext = advance();
                return result;
            }
        };
    }

}
