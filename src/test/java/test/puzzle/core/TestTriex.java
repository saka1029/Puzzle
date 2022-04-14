package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import puzzle.core.Triex;

public class TestTriex {

    @Test
    public void test() {
        Triex<String, String> t = new Triex<>();
        t.put("A", "a");
        t.put("B", "a", "b");
        t.put("C", "a", "c");
        t.put("D", "a", "c", "d");
        System.out.println(t);
        assertEquals("A", t.get("a"));
        assertEquals("B", t.get("a", "b"));
        assertEquals("C", t.get("a", "c"));
        assertEquals("D", t.get("a", "c", "d"));
        assertEquals(null, t.get("x"));
    }

    static Integer[] array(String s) {
        return s.chars().boxed().toArray(Integer[]::new);
    }

    @Test
    public void testString() {
        Triex<Integer, String> t = new Triex<>();
        t.put("A", array("a"));
        t.put("B", array("ab"));
        t.put("C", array("ac"));
        t.put("D", array("acd"));
        System.out.println(t);
    }
    
    @Test
    public void testEmoji() throws IOException {
        URL url = new URL("https://ja.wikipedia.org/wiki/Unicode%E3%81%AEEmoji%E3%81%AE%E4%B8%80%E8%A6%A7");
        Document doc = Jsoup.parse(url, 5000);
        Elements elements = doc.select("table.sortable tbody tr");
        for (Element e : elements) {
            String emoji = e.select("td:eq(0)").text();
            String[] description = e.select("td:eq(2)").text().trim().toLowerCase().split("\\s+");
            System.out.println(emoji + ":" + Arrays.toString(description));
        }

    }

}
