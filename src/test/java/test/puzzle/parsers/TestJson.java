package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.parsers.Json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import puzzle.parsers.Json.NullParseHandler;
import puzzle.parsers.Json.ParseHandler;

class TestJson {

    @Test
    void testParse() throws IOException {
        String json = "{\n"
            + "    \"glossary\": {\n"
            + "        \"title\": \"example glossary\",\n"
            + "        \"GlossDiv\": {\n"
            + "            \"title\": \"S\",\n"
            + "            \"GlossList\": {\n"
            + "                \"GlossEntry\": {\n"
            + "                    \"ID\": \"SGML\",\n"
            + "                    \"SortAs\": \"SGML\",\n"
            + "                    \"GlossTerm\": \"Standard Generalized Markup Language\",\n"
            + "                    \"Acronym\": \"SGML\",\n"
            + "                    \"Abbrev\": \"ISO 8879:1986\",\n"
            + "                    \"GlossDef\": {\n"
            + "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n"
            + "                        \"GlossSeeAlso\": [\n"
            + "                            \"GML\",\n"
            + "                            \"XML\"\n"
            + "                        ]\n"
            + "                    },\n"
            + "                    \"GlossSee\": \"markup\"\n"
            + "                }\n"
            + "            }\n"
            + "        }\n"
            + "    }\n"
            + "}\n"
            + "";
//        Object parsed = parse(json);
//        System.out.println(parsed);
        record GlossDef(String para, List<String> GlossSeeAlso) {}
        List<GlossDef> glossDefs = new ArrayList<>();
        ParseHandler handler = new NullParseHandler() {
            String para;
            List<String> GlossSeeAlso;
            List<Object> select = List.of("GlossDef");
            @Override
            public void objectMember(List<Object> path, Object object, String key, Object value) {
                if (match(path, select)) {
                    if (key.equals("para")) para = (String)value;
                    else if (key.equals("GlossSeeAlso")) GlossSeeAlso = (List<String>)value;
                }
            }
            @Override
            public Object objectEnd(List<Object> path, Object object) {
                if (match(path, select))
                    glossDefs.add(new GlossDef(para, GlossSeeAlso));
                return super.objectEnd(path, object);
            }
        };
        Object parsed = parse(json, handler);
        System.out.println(glossDefs);
    }

    @Test
    void testEscape() throws IOException {
        Object parsed = parse("\"a\\r\\nb\\nc\\fd\\be\"");
        assertEquals("a\r\nb\nc\fd\be", parsed);
    }

    @Test
    void testUnicode() throws IOException {
        Object parsed = parse("\"\\u6f22123\u5b57\"");
        assertEquals("漢123字", parsed);
    }

     static final String JSON = "[{\n"
        + "  \"startAt\": 1617605301292,\n"
        + "  \"endAt\": 1617605317095,\n"
        + "  \"duration\": 15803,\n"
        + "  \"selection\": {\n"
        + "    \"selected.Speed\": \"0\",\n"
        + "    \"selected.Low\": \"65535\",\n"
        + "    \"selected.Fast\": \"7173\",\n"
        + "    \"selected.medium\": \"5\"\n"
        + "  },\n"
        + "  \"details\": {\n"
        + "    \"phase\": [{\n"
        + "      \"value\": \"2\",\n"
        + "      \"timestamp\": 1617605301316\n"
        + "    }]\n"
        + "  }\n"
        + "},\n"
        + "{\n"
        + "  \"startAt\": 1617605301292,\n"
        + "  \"endAt\": 1617605317095,\n"
        + "  \"duration\": 15803,\n"
        + "  \"selection\": {\n"
        + "    \"selected.Speed\": \"2\",\n"
        + "    \"selected.Low\": \"-3\",\n"
        + "    \"selected.Fast\": \"173\",\n"
        + "    \"selected.medium\": \"5\"\n"
        + "  },\n"
        + "  \"details\": {\n"
        + "    \"phase\": [{\n"
        + "      \"value\": \"2\",\n"
        + "      \"timestamp\": 1617605301316\n"
        + "    }]\n"
        + "  }\n"
        + "}]";

    @Test
    void testNullParseHandler() throws IOException {
        Map<String, Integer> count = new LinkedHashMap<>();
        ParseHandler h = new NullParseHandler() {
            List<Object> select = List.of("selection", "*");
            @Override
            public void objectMember(List<Object> path, Object object, String key, Object value) {
                if (match(path, select) && value instanceof String sv)
                    count.compute(sv, (k, v) -> v == null ? 1 : v + 1);
            }
        };
        Object parsed = parse(JSON, h);
        System.out.println(parsed);
        System.out.println(count);
    }

    @Test
    public void testSelect() throws IOException {
        List<Object> search = List.of("selection");
        List<Object> found = select(parse(JSON), search);
        System.out.println(found);
        assertEquals(2, found.size());
    }

    @Test
    public void testMatch() {
        List<Object> path = List.of("a", "b", "c", "d");
        assertFalse(match(path, List.of("x")));
        assertTrue(match(path, List.of()));
        assertTrue(match(path, List.of("d")));
        assertTrue(match(path, List.of("c", "d")));
        assertTrue(match(path, List.of("a", "d")));
        assertTrue(match(path, List.of("a", "b", "c", "d")));
        assertFalse(match(path, List.of("x", "a", "b", "c", "d")));
        assertFalse(match(path, List.of("c")));
    }
}
