package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Json.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Json.DefaultParseHandler;
import puzzle.Json.ParseHandler;


class TestJson {

    static String LOG_FORMAT_KEY = "java.util.logging.SimpleFormatter.format";
    static String LOG_FORMAT = "%1$tFT%1$tT.%1$tL %4$s %3$s %5$s %6$s%n";
    static {
        System.setProperty(LOG_FORMAT_KEY, LOG_FORMAT);
    }
    static final Logger logger = Logger.getLogger(TestJson.class.getSimpleName());

    static void methodName() {
        logger.info("*** " + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    static final String JSON_BOOKS_STUDENTS = "[\n"
        + "{\n"
        + "   \"book\":[\n"
        + "      {\n"
        + "         \"id\":444,\n"
        + "         \"language\":\"C\",\n"
        + "         \"edition\":\"First\",\n"
        + "         \"author\":\"Dennis Ritchie\"\n"
        + "      },\n"
        + "      {\n"
        + "         \"id\":555,\n"
        + "         \"language\":\"C++\",\n"
        + "         \"edition\":\"second\",\n"
        + "         \"author\":\"Bjarne Stroustrup\"\n"
        + "      }\n"
        + "   ]\n"
        + "},\n"
        + "{\n"
        + "  \"student\": [ \n"
        + "    \n"
        + "     { \n"
        + "        \"id\":\"01\", \n"
        + "        \"name\": \"Tom\", \n"
        + "        \"lastname\": \"Price\" \n"
        + "     }, \n"
        + "    \n"
        + "     { \n"
        + "        \"id\":\"02\", \n"
        + "        \"name\": \"Nick\", \n"
        + "        \"lastname\": \"Thameson\" \n"
        + "     } \n"
        + "  ]\n"
        + "}\n"
        + "]\n";

    void testConstant() {
        methodName();
        assertEquals(true, parse("true"));
        assertEquals(false, parse("false"));
        assertEquals(null, parse("null"));
        assertEquals(12345D, parse("12345"));
        assertEquals(12345.6789, parse("12345.6789"));
        assertEquals(12345.6789e20, parse("12345.6789e20"));
        assertEquals(12345.6789e-20, parse("12345.6789e-20"));
    }

    @Test
    void testGet() {
        methodName();
        Object json = parse(JSON_BOOKS_STUDENTS);
        assertEquals(Map.of(
            "id", 444D,
            "language", "C",
            "edition", "First",
            "author", "Dennis Ritchie"), get(json, "**.book.0"));
        assertEquals(Map.of(
            "id", 555D,
            "language", "C++",
            "edition", "second",
            "author", "Bjarne Stroustrup"),
            get(json, "**.book.1"));
        assertEquals(444D, get(json, "**.book.0.id"));
        assertEquals(444, asInt(get(json, "**.book.0.id")));
        assertEquals("C", get(json, "**.book.0.language"));
        assertEquals("First", get(json, "**.book.0.edition"));
        assertEquals("Dennis Ritchie", get(json, "**.book.0.author"));
        assertEquals(555D, get(json, "**.book.1.id"));
        assertEquals(555, asInt(get(json, "**.book.1.id")));
        assertEquals("C++", get(json, "**.book.1.language"));
        assertEquals("second", get(json, "**.book.1.edition"));
        assertEquals("Bjarne Stroustrup", get(json, "**.book.1.author"));
        assertEquals("01", get(json, "**.student.0.id"));
        assertEquals("Tom", get(json, "**.student.0.name"));
        assertEquals("Price", get(json, "**.student.0.lastname"));
        assertEquals("02", get(json, "**.student.1.id"));
        assertEquals("Nick", get(json, "**.student.1.name"));
        assertEquals("Thameson", get(json, "**.student.1.lastname"));
    }

    @Test
    void testSelect() {
        methodName();
        Object json = parse(JSON_BOOKS_STUDENTS);
        assertEquals(List.of(444D, 555D, "01", "02"), select(json, "**.id"));
        assertEquals(List.of("C", "C++"), select(json, "**.language"));
    }

    @Test
    void testLargeFile() throws IOException {
        methodName();
        File file = new File("data/large-file.json");
        Object json = parse(file);
        logger.info("" + asList(json).size());
        logger.info("" + select(json, "**.name").size());
        logger.info("" + select(json, "**.author.**.name").size());
    }

    @Test
    void testKeywordKey() {
        methodName();
        String s = "{a:0,b:1}";
        assertEquals(Map.of("a", 0.0, "b", 1.0), parse(s));
    }

    @Test
    void testFormatNumber() throws IOException {
        methodName();
        assertEquals("123456", formatter().format(123456).toString());
        assertEquals("123456789012", formatter().format(123456789012L).toString());
        assertEquals("123456.789012", formatter().format(123456.789012D).toString());
    }

    @Test
    void testFormatString() throws IOException {
        methodName();
        assertEquals("\"abc\"", formatter().format("abc").toString());
        assertEquals("\"abc\\r\\ndef\"", formatter().format("abc\r\ndef").toString());
        assertEquals("\"abc\\t\"", formatter().format("abc\t").toString());
        assertEquals("\"\\babc\"", formatter().format("\babc").toString());
        assertEquals("\"a\\\"bc\"", formatter().format("a\"bc").toString());
        assertEquals("\"a/bc\"", formatter().format("a/bc").toString());
    }

    @Test
    void testFormatArray() throws IOException {
        assertEquals("[1,\"a\"]", formatter().format(array(1, "a")).toString());
        assertEquals("[1,2]", formatter().format(array(1, 2)).toString());
        assertTrue(array(1, 2) instanceof Object[]);
    }

    @Test
    void testFormatCollection() throws IOException {
        methodName();
        assertEquals("[1,\"abc\"]", formatter().format(List.of(1, "abc")).toString());
        Stack<Object> stack = new Stack<>();
        stack.push(1); stack.push("abc");
        assertEquals("[1,\"abc\"]", formatter().format(stack).toString());
        Deque<Object> deque = new ArrayDeque<>();
        deque.push(1); deque.push("abc");
        assertEquals("[1,\"abc\"]", formatter().format(set(1, "abc")).toString());
    }

    @Test
    void testFormatMap() throws IOException {
        methodName();
        assertEquals("{\"a\":1,\"b\":2}", formatter().format(map("a", 1, "b", 2)).toString());
        assertEquals("{\n  \"a\":1,\n  \"b\":2\n}",
            formatter().tab("  ").newline("\n").format(map("a", 1, "b", 2)).toString());
        assertEquals("{\"int\":0,\"list\":[1,2]}", formatter().format(map("int", 0, "list", list(1, 2))).toString());
        String f = formatter().tab("  ").newline("\n").format(
            map("int", 0, "list", list(1, 2), "map", map("x", 100, "y", 200))
            ).toString();
        logger.info(f);
        assertEquals("{\n"
            + "  \"int\":0,\n"
            + "  \"list\":[\n"
            + "    1,\n"
            + "    2\n"
            + "  ],\n"
            + "  \"map\":{\n"
            + "    \"x\":100,\n"
            + "    \"y\":200\n"
            + "  }\n"
            + "}", f);
    }

    @Test
    void testParseHandler() throws IOException {
        record Person(String name, int birthYear) {}
        ParseHandler handler = new DefaultParseHandler() {
            @Override
            public Object endObject(Object object) {
                Map<String, Object> map = asMap(object);
                if (map.containsKey("name") && map.containsKey("birthYear"))
                    return new Person(asString(map.get("name")), asInt(map.get("birthYear")));
                else
                    return object;
            }
        };
        String s = "{\n"
            + "  person: { name: \"Jhon\", birthYear: 1988 },\n"
            + "  version: 1.0,\n"
            + "}\n";
        Object parsed = parse(handler, s);
        logger.info("parsed = " + parsed);
        logger.info("parsed Person = " + asMap(parsed).get("person"));
        String formatted = formatter().format(parsed).toString();
        logger.info("formatted = " + formatted);
        assertEquals("{\"person\":\"Person[name=Jhon, birthYear=1988]\",\"version\":1.0}", formatted);
    }

    @Test
    void testStackoverflow() throws IOException {
        String json = "{\"idCode\":\"F-1033\",\"Message\":\" Completed Successfully\",\"statusCode\":1,\"data\":{\"TermsCustomerFr\":\"<p>\\n\\n<style>\\n<!--\\n /* Font Definitions */\\n@font-face\\n\\t{font-family:Arial;\\n\\tpanose-1:2 11 6 4 2 2 2 2 2 4;\\n\\tmso-font-charset:0;\\n\\tmso-generic-font-family:auto;\\n\\tmso-font-pitch:variable;\\n\\tmso-font-signature:-536859905 -1073711037 9 0 511 0;}\\n@font-face\\n\\t{font-family:\\\"Cambria Math\\\";\\n\\tpanose-1:2 4 5 3 5 4 6 3 2 4;\\n\",\"TermsCustomerEn\":null,\"ConditionsCustomerEn\":null}}";
        Object parsed = parse(json);
        System.out.println(formatter().tab("  ").newline("\n").format(parsed).toString());
        System.out.println(get(parsed, "data.TermsCustomerFr"));
    }

    @Test
    void testStackoverflow3() throws IOException {
        Object parsed = parse(new File("data/a.json"));
        System.out.println(formatter().tab("  ").newline("\n").format(parsed).toString());
        System.out.println(select(parsed, "**.title"));
        System.out.println(select(parsed, "**.priority"));
        System.out.println(select(parsed, "**.image.fileType"));
    }
}
