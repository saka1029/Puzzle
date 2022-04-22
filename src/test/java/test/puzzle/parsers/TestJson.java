package test.puzzle.parsers;

import static org.junit.Assert.*;
import static puzzle.parsers.Json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import puzzle.parsers.Json.DefaultParseHandler;
import puzzle.parsers.Json.NullParseHandler;
import puzzle.parsers.Json.ParseHandler;

public class TestJson {

    @Test
    public void testKeyword() {
        assertTrue(Character.isLetter('a'));
        assertTrue(Character.isLetter('漢'));
        assertTrue(Character.isLetter('二'));
        assertFalse(Character.isLetter('_'));
        assertFalse(Character.isLetter('&'));
        assertFalse(Character.isLetter('0'));
        assertFalse(Character.isLetter('【'));
        assertFalse(Character.isLetter('０'));
    }

    @Test
    public void testParse() throws IOException {
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

        record GlossDef(String para, List<String> GlossSeeAlso) {}

        List<GlossDef> glossDefs = new ArrayList<>();
        ParseHandler handler = new DefaultParseHandler() {
            List<Object> criterial = List.of("GlossDef");

            @Override
            @SuppressWarnings("unchecked")
            public Object objectEnd(List<Object> path, Object object) {
                if (match(path, criterial)) {
                    Map<String, Object> map = (Map<String, Object>)object;
                    glossDefs.add(new GlossDef(
                        (String)map.get("para"), (List<String>)map.get("GlossSeeAlso")));
                }
                return super.objectEnd(path, object);
            }
        };
        parse(json, handler);
        assertEquals(List.of(new GlossDef(
            "A meta-markup language, used to create markup languages such as DocBook.",
            List.of("GML", "XML"))), glossDefs);
        System.out.println(glossDefs);
    }

    @Test
    public void testSpace() throws IOException {
        Object parsed = parse("\"a b  c\"");
        assertEquals("a b  c", parsed);
    }

    @Test
    public void testEscape() throws IOException {
        Object parsed = parse("\"a\\r\\nb\\nc\\fd\\be\"");
        assertEquals("a\r\nb\nc\fd\be", parsed);
        String str = "\"{\\\"name\\\":\\\"Gerson Beahan\\\",\\\"age\\\":70,\\\"balance\\\":424695.03332155856}\"";
        System.out.println(str);
        System.out.println(parse(str));
    }

    @Test
    public void testUnicode() throws IOException {
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
    public void testNullParseHandler() throws IOException {
        Map<String, Integer> count = new LinkedHashMap<>();
        ParseHandler h = new NullParseHandler() {
            List<Object> criterial = List.of("selection");
            @Override
            public void objectMember(List<Object> path, Object object, String key, Object value) {
                if (match(path, criterial) && value instanceof String sv)
                    count.compute(sv, (k, v) -> v == null ? 1 : v + 1);
            }
        };
        parse(JSON, h);
        assertEquals(Map.of("0", 1, "65535", 1, "7173", 1, "5", 2, "2", 1, "-3", 1, "173", 1), count);
    }

    @Test
    public void testSelect() throws IOException {
        List<Object> criterial = List.of("selection");
        List<Object> found = select(parse(JSON), criterial);
        System.out.println(found);
        assertEquals(2, found.size());
        assertEquals(Map.of(
            "selected.Speed", "0",
            "selected.Low", "65535",
            "selected.Fast", "7173",
            "selected.medium", "5"), found.get(0));
        assertEquals(Map.of(
            "selected.Speed", "2",
            "selected.Low", "-3",
            "selected.Fast", "173",
            "selected.medium", "5"), found.get(1));
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

    @Test
    public void testGet() {
        Object map = map("a", 1, "b", list("c", "d", 4), "e", true);
        assertEquals(1, get(map, "a"));
        assertEquals(list("c", "d", 4), get(map, "b"));
        assertEquals("c", get(map, "b", 0));
        assertEquals("d", get(map, "b", 1));
        assertEquals(4, get(map, "b", 2));
        assertEquals(true, get(map, "e"));
        assertEquals(null, get(map, "f"));
    }

    @Test
    public void testTraverse() {
        Object map = map("a", 1, "b", list("c", "d", 4), "e", 2);
        Iterator<Entry<List<Object>, Object>> it = traverse(map).iterator();
        assertEquals(Map.entry(list(), map), it.next());
        assertEquals(Map.entry(list("a"), get(map, "a")), it.next());
        assertEquals(Map.entry(list("b"), get(map, "b")), it.next());
        assertEquals(Map.entry(list("b", 0), get(map, "b", 0)), it.next());
        assertEquals(Map.entry(list("b", 1), get(map, "b", 1)), it.next());
        assertEquals(Map.entry(list("b", 2), get(map, "b", 2)), it.next());
        assertEquals(Map.entry(list("e"), get(map, "e")), it.next());
        assertFalse(it.hasNext());
        for (Entry<List<Object>, Object> e : traverse(map))
            System.out.println(e);
    }

    @Test
    public void testTwitter() throws IOException {
        record Tweet(ZonedDateTime date, String text) {}
        List<Tweet> list = new ArrayList<>();
        Path file = Paths.get("data/tweet.js.txt");
        ZoneId zone = ZoneId.of("Japan");
        DateTimeFormatter f = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        ParseHandler handler = new NullParseHandler() {
            ZonedDateTime date;
            @Override
            public void objectMember(List<Object> path, Object object, String key, Object value) {
                switch (key) {
                case "created_at":
                    date = ZonedDateTime.of(LocalDateTime.parse(String.valueOf(value), f), zone);
                    break;
                case "full_text":
                    list.add(new Tweet(date, String.valueOf(value)));
                    break;
                }
                super.objectMember(path, object, key, value);
            }
        };
        parse(Files.newBufferedReader(file), handler);
        List<Tweet> selected = list.stream()
            .filter(t -> !t.text.contains("閉鎖") && !t.text.contains("elonmusk"))
            .sorted(Comparator.comparing(Tweet::date).reversed())
            .toList();
        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Tweet e : selected)
            System.out.printf("<dt class='history'>%s</dt>%n"
                + "<dd>%s</dd>%n",
                e.date.plusHours(9).format(d), e.text.replaceAll("http[-:/._a-zA-Z0-9]+", "<a href='$0'>$0</a>"));
    }

    @Test
    public void testStackoverflowJsonRegex() throws IOException {
        String content = "{ \"values\" : [\"AnyValue1\", \"TestValue\", \"Dummy\", \"SomeValue\"], \"key\" : \"value\" }";
        Object result = parse(content, new DefaultParseHandler() {
            @Override
            public Object string(List<Object> path, String s) {
                if (match(path, "values", "*"))
                    s = s + "_val";
                return super.string(path, s);
            }
        });
        System.out.println(result);
    }
}
