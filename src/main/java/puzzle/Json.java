package puzzle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
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
 */
public class Json {

    public static final String KEY_ESCAPE = "/";
    public static final String KEY_SEPARATOR = ".";
    static final Pattern DELIMITER = Pattern.compile("(?<!" + KEY_ESCAPE + ")[" + KEY_SEPARATOR + "]");
    public static final String ANY_KEY = "*";
    public static final String ANY_KEYS = "**";
    public static final Object NOTHING = new Object() { public String toString() { return "NOTHING"; }};

    public static boolean isCollection(Object value) { return value instanceof Collection<?>; }
    public static boolean isArray(Object value) { return value instanceof Object[]; }
    public static boolean isMap(Object value) { return value instanceof Map<?, ?>; }
    public static boolean isList(Object value) { return value instanceof List<?>; }
    public static boolean isBoolean(Object value) { return value instanceof Boolean; }
    public static boolean isString(Object value) { return value instanceof String; }
    public static boolean isNumber(Object value) { return value instanceof Number; }
    public static boolean isDouble(Object value) { return value instanceof Double; }
    public static boolean isInt(Object value) { return value instanceof Integer; }
    public static boolean isLong(Object value) { return value instanceof Long; }
    @SuppressWarnings("unchecked")
    public static Collection<Object> asCollection(Object value) { return (Collection<Object>) value; }
    public static Object[] asArray(Object value) { return (Object[]) value; }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object value) { return (Map<String, Object>) value; }
    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object value) { return (List<Object>) value; }
    public static boolean asBoolean(Object value) { return (boolean) value; }
    public static String asString(Object value) { return (String) value; }
    public static Number asNumber(Object value) { return (Number) value; }
    public static double asDouble(Object value) { return ((Number) value).doubleValue(); }
    public static int asInt(Object value) { return ((Number) value).intValue(); }
    public static long asLong(Object value) { return ((Number) value).longValue(); }

    public static Collection<Object> values(Object value) {
        return isList(value) ? asList(value)
            : isMap(value) ? asMap(value).values()
            : Collections.emptyList();
    }

    public static Object get(Object value, String path) {
        Object result = get(value, 0, parsePath(path));
        if (result == NOTHING)
            throw new IllegalArgumentException("Not found path=" + path);
        return result;
    }

    static Object get(Object value, int index, String... keys) {
//        System.out.println("get: index=" + index + " keys=" + Arrays.toString(keys) +" value=" + value);
        for (int i = index, size = keys.length; i < size; ++i) {
            String key = keys[i];
            if (key == ANY_KEY) {
                for (Object child : values(value)) {
                    Object result = get(child, i + 1, keys);
                    if (result != NOTHING)
                        return result;
                }
                return NOTHING;
            } else if (key == ANY_KEYS)
                return getDeep(value, i + 1, keys);
            else if (isList(value) && key.matches("\\d+"))
                value = asList(value).get(Integer.parseInt(key));
            else if (isMap(value) && asMap(value).containsKey(key))
                value = asMap(value).get(key);
            else
                return NOTHING;
        }
        return value;
    }

    static Object getDeep(Object value, int index, String... keys) {
//        System.out.println("getDeep: index=" + index + " keys=" + Arrays.toString(keys) +" value=" + value);
        if (index >= keys.length)
            return NOTHING;
        Object result = get(value, index, keys);
        if (result != NOTHING)
            return result;
        else if (isList(value))
            for (Object next : asList(value)) {
                Object r = getDeep(next, index, keys);
                if (r != NOTHING)
                    return r;
            }
        else if (isMap(value))
            for (Object next : asMap(value).values()) {
                Object r = getDeep(next, index, keys);
                if (r != NOTHING)
                    return r;
            }
        return NOTHING;
    }

    public static List<Object> select(Object value, String path) {
        List<Object> result = new ArrayList<>();
        select(value, result, 0, parsePath(path));
        return result;
    }

    private static void select(Object value, List<Object> result, int index, String... keys) {
        for (int i = index, size = keys.length; i < size; ++i) {
            String key = keys[i];
            if (key == ANY_KEY) {
                for (Object child : values(value))
                    select(child, result, i + 1, keys);
                return;
            } else if (key == ANY_KEYS)
                selectDeep(value, result, i + 1, keys);
            else if (isList(value) && key.matches("\\d+"))
                value = asList(value).get(Integer.parseInt(key));
            else if (isMap(value) && asMap(value).containsKey(key))
                value = asMap(value).get(key);
            else
                return;
        }
        result.add(value);
    }

    static void selectDeep(Object value, List<Object> results, int index, String... keys) {
        if (index >= keys.length)
            return;
        select(value, results, index, keys);
        if (isList(value))
            for (Object next : asList(value))
                selectDeep(next, results, index, keys);
        else if (isMap(value))
            for (Object next : asMap(value).values())
                selectDeep(next, results, index, keys);
    }

    public static String[] parsePath(String path) {
        String[] keys = DELIMITER.split(path);
        int size = keys.length;
        for (int i = 0; i < size; ++i) {
            if (keys[i].equals(ANY_KEY))
                keys[i] = ANY_KEY;
            else if (keys[i].equals(ANY_KEYS))
                keys[i] = ANY_KEYS;
            else
                keys[i] = keys[i].replaceAll(KEY_ESCAPE + "(.)", "$1");
        }
//        System.out.println("path=" + path + " -> " + Arrays.toString(keys));
        return keys;
    }

    public static Object[] array(Object... objects) {
        return objects;
    }

    public static List<Object> list(Object... objects) {
        return Stream.of(objects)
            .collect(Collectors.toList());
    }

    public static Set<Object> set(Object... objects) {
        return Arrays.stream(objects)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Map<String, Object> map(Object... objects) {
        int size = objects.length;
        if (size % 2 != 0)
            throw new IllegalArgumentException("Size of objects must be even");
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < objects.length; i += 2) {
            if (!(objects[i] instanceof String))
                throw new IllegalArgumentException("Key must be string: " + objects[i]);
            map.put((String)objects[i], objects[i + 1]);
        }
        return map;
    }

    public static class Formatter {
        final Writer writer;
        String newline = "";
        String tab = "";
        public int nest = 0;
        int outputSize = 0;

        Formatter() {
            this.writer = new StringWriter();
        }

        Formatter(Writer writer) {
            this.writer = writer;
        }
        public Formatter newline(String newline) { this.newline = newline; return this; }
        public Formatter tab(String tab) { this.tab = tab; return this; }

        public void print(Object obj) throws IOException {
            String output = obj.toString();
            writer.write(output);
            outputSize += output.length();
        }

        public void printIndent() throws IOException { print(tab.repeat(nest)); }

        @Override
        public String toString() {
            return writer.toString();
        }

        void string(String s) throws IOException {
            print("\"");
            for (int i = 0, size = s.length(); i < size; ++i) {
                char ch = s.charAt(i);
                switch (ch) {
                case '\t': print("\\t"); break;
                case '\n': print("\\n"); break;
                case '\r': print("\\r"); break;
                case '\b': print("\\b"); break;
                case '\"': print("\\\""); break;
                case '\\': print("\\\\"); break;
//                case '/': print("\\/"); break;
                default: print(ch); break;
                }
            }
            print("\"");
        }

        void object(Map<String, Object> map) throws IOException {
            print("{");
            String sep = newline;
            ++nest;
            for (Entry<String, Object> e : map.entrySet()) {
                print(sep);
                printIndent();
                format(e.getKey());
                print(":");
                format(e.getValue());
                sep = "," + newline;
            }
            --nest;
            print(newline);
            printIndent();
            print("}");
        }

        void collection(Collection<Object> collection) throws IOException {
            print("[");
            ++nest;
            String sep = newline;
            for (Object e : collection) {
                print(sep);
                printIndent();
                format(e);
                sep = "," + newline;
            }
            --nest;
            print(newline);
            printIndent();
            print("]");
        }

        void array(Object[] array) throws IOException {
            print("[");
            ++nest;
            String sep = newline;
            for (Object e : array) {
                print(sep);
                printIndent();
                format(e);
                sep = "," + newline;
            }
            --nest;
            print(newline);
            printIndent();
            print("]");
        }

        public Formatter format(Object obj) throws IOException {
            if (obj == null)
                print("null");
            else if (isBoolean(obj))
                print(obj);
            else if (isString(obj))
                string(asString(obj));
            else if (isNumber(obj))
                print(obj);
            else if (isMap(obj))
                object(asMap(obj));
            else if (isCollection(obj))
                collection(asCollection(obj));
            else if (isArray(obj))
                array(asArray(obj));
            else
                string(obj.toString());
            return this;
        }
    }

    public static Formatter formatter() {
        return new Formatter();
    }

    public static Formatter formatter(Writer writer) {
        return new Formatter(writer);
    }

    public interface ParseHandler {
        Object startObject();
        Object endObject(Object object);
        Object startArray();
        Object endArray(Object array);
        void member(Object object, Object name, Object value);
        void element(Object array, Object value);
        Object string(String string);
        Object number(String number);
        Object keyword(String keyword);
    }

    public static class DefaultParseHandler implements ParseHandler {
        @Override public Object startObject() {
            return new LinkedHashMap<String, Object>();
        }
        @SuppressWarnings("unchecked")
        @Override public Object endObject(Object object) {
            return Collections.unmodifiableMap((Map<String, Object>)object);
        }
        @Override public Object startArray() {
            return new ArrayList<Object>();
        }
        @SuppressWarnings("unchecked")
        @Override public Object endArray(Object array) {
            return Collections.unmodifiableList((List<Object>)array);
        }
        @SuppressWarnings("unchecked")
        @Override public void member(Object object, Object name, Object value) {
            ((Map<String, Object>)object).put((String)name, value);
        }
        @SuppressWarnings("unchecked")
        @Override public void element(Object array, Object value) {
            ((List<Object>)array).add(value);
        }
        @Override public Object string(String string) { return string; }
        @Override public Object number(String number) { return Double.parseDouble(number); }
        @Override public Object keyword(String keyword) {
            switch (keyword) {
            case "true": return true;
            case "false": return false;
            case "null": return null;
            default: return keyword;
            }
        }
    }

    public static ParseHandler DEFAULT_HANDLER = new DefaultParseHandler();

    public static Object parse(File file) throws IOException {
        return parse(DEFAULT_HANDLER, file);
    }

    public static Object parse(ParseHandler handler, File file) throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            return parse(handler, reader);
        }
    }

    public static Object parse(File file, Charset encoding) throws IOException {
        return parse(DEFAULT_HANDLER, file, encoding);
    }

    public static Object parse(ParseHandler handler, File file, Charset encoding) throws IOException {
        try (Reader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), encoding))) {
            return parse(handler, reader);
        }
    }

    public static Object parse(Path path) throws IOException {
        return parse(DEFAULT_HANDLER, path);
    }

    public static Object parse(ParseHandler handler, Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return parse(handler, reader);
        }
    }

    public static Object parse(Path path, Charset encoding) throws IOException {
        return parse(DEFAULT_HANDLER, path, encoding);
    }

    public static Object parse(ParseHandler handler, Path path, Charset encoding) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, encoding)) {
            return parse(handler, reader);
        }
    }

    public static Object parse(String s) {
        return parse(DEFAULT_HANDLER, s);
    }

    public static Object parse(ParseHandler handler, String s) {
        try (Reader reader = new StringReader(s)) {
            return parse(handler, reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object parse(Reader reader) throws IOException {
        return parse(DEFAULT_HANDLER, reader);
    }

    public static Object parse(ParseHandler handler, Reader reader) throws IOException {
        return new Object() {
            private static final int EOF = -1;
            private int ch = ' ';

            boolean isDigit(int ch) {
                return ch >= '0' && ch <= '9';
            }

            boolean isIdFirst(int ch) {
                return ch >= 'a' && ch <= 'z'
                    || ch >= 'A' && ch <= 'Z'
                    || ch == '_'
                    || ch > 255;
            }

            boolean isIdRest(int ch) {
                return isIdFirst(ch) || isDigit(ch);
            }

            int get() throws IOException {
                return ch = reader.read();
            }

            void skipSpaces() throws IOException {
                while (Character.isWhitespace(ch))
                    get();
            }

            void pair(Object object) throws IOException {
                Object key = parse();
//                if (!(key instanceof String))
//                    throw new RuntimeException("String expected but '" + key + "' appeared");
                skipSpaces();
                if (ch != ':')
                    throw new RuntimeException("':' expected but '" + (char) ch + "' appeared");
                get(); // skip ':'
                Object value = parse();
                handler.member(object, key, value);
            }

            /**
             * '{' [ STRING ':' OBJECT { ',' STRING ':' OBJECT } ] '}'
             * @return
             * @throws IOException
             */
            Object object() throws IOException {
                Object object = handler.startObject();
                get(); // skip '{'
                skipSpaces();
                while (ch != EOF && ch != '}') {
                    pair(object);
                    skipSpaces();
                    if (ch != ',')
                        break;
                    get();  // skip ','
                    skipSpaces();
                }
                if (ch != '}')
                    throw new RuntimeException("'}' expected");
                get();  // skip '}'
                return handler.endObject(object);
            }

            /**
             * '[' [ OBJECT { ',' OBJECT } ] ']'
             *
             * @return
             * @throws IOException
             */
            Object array() throws IOException {
                Object array = handler.startArray();
                get(); // skip '['
                skipSpaces();
                while (ch != EOF && ch != ']') {
                    handler.element(array, parse());
                    skipSpaces();
                    if (ch != ',')
                        break;
                    get();  // skip ','
                    skipSpaces();
                }
                if (ch != ']')
                    throw new RuntimeException("']' expected");
                get();  // skip ']'
                return handler.endArray(array);
            }

            boolean isHexDigit(int ch) {
                ch = Character.toLowerCase(ch);
                return ch >= 'a' && ch <= 'f' || ch >= '0' && ch <= '9';
            }

            void unicode(StringBuilder sb) throws IOException {
                int start = sb.length();
                sb.append((char)ch);    // 'u'
                get();
                StringBuilder hex = new StringBuilder();
                for (int i = 0; i < 4 && isHexDigit(ch); ++i) {
                    sb.append((char)ch);
                    hex.append((char)ch);
                    get();
                }
                if (hex.length() == 4) {
                    sb.setLength(start);
                    sb.append((char)Integer.parseInt(hex.toString(), 16));
                }
            }

            Object string() throws IOException {
                StringBuilder sb = new StringBuilder();
                get(); // skip '"'
                L: while (true) {
                    switch (ch) {
                    case EOF: throw new RuntimeException("Unterminated string: " + sb);
                    case '\\':
                        switch (get()) {
                        case EOF: throw new RuntimeException("Unterminated string: " + sb);
                        case '"': sb.append('\"'); get(); break;
                        case '\\': sb.append('\\'); get(); break;
                        case '/': sb.append('/'); get(); break;
                        case 'b': sb.append('\b'); get(); break;
                        case 'f': sb.append('\f'); get(); break;
                        case 'n': sb.append('\n'); get(); break;
                        case 'r': sb.append('\r'); get(); break;
                        case 't': sb.append('\t'); get(); break;
                        case 'u': unicode(sb); break;
                        default: sb.append((char) ch); get(); break;
                        }
                        break;
                    case '"': get(); break L;
                    case '\n':
                    case '\r': throw new RuntimeException("Unterminated string: " + sb);
                    default: sb.append((char) ch); get(); break;
                    }
                }
                return handler.string(sb.toString());
            }

            Object number() throws IOException {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append((char) ch);
                    get();
                } while (isDigit(ch));
                if (ch == '.') {
                    do {
                        sb.append((char) ch);
                        get();
                    } while (isDigit(ch));
                }
                if (Character.toLowerCase(ch) == 'e') {
                    sb.append((char) ch);
                    get();
                    if (ch == '+' || ch == '-') {
                        sb.append((char) ch);
                        get();
                    }
                    while (isDigit(ch)) {
                        sb.append((char) ch);
                        get();
                    }
                }
                String s = sb.toString();
                return handler.number(s);
            }

            Object keyword() throws IOException {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append((char) ch);
                    get();
                } while (isIdRest(ch));
                String keyword = sb.toString();
                return handler.keyword(keyword);
            }

            Object parse() throws IOException {
                skipSpaces();
                switch (ch) {
                case '{': return object();
                case '[': return array();
                case '\"': return string();
                case '-': return number();
                case '+': return number();
                default:
                    if (isDigit(ch))
                        return number();
                    else if (isIdFirst(ch))
                        return keyword();
                    else
                        throw new RuntimeException("Unknown char '" + (char) ch + "'");
                }
            }
        }.parse();
    }
}
