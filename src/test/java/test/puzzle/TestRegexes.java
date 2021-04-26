package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Regexes.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class TestRegexes {

    @Test
    void testMatcherResults() {
        Pattern pat = Pattern.compile("\\d+");
        String source = "abc123r4g582s";
        Matcher matcher = pat.matcher(source);
        matcher.results()
            .forEach(m -> System.out.println(m.start() + ", " + m.end() + ", " + m.group() + ", "));
    }

    @Test
    void testReplaceString() {
        String hex = replaceAll("abc123r4g511s", "\\d+",
            m -> "(" + Integer.toHexString(Integer.parseInt(m.group())) + ")");
        assertEquals("abc(7b)r(4)g(1ff)s", hex);
    }

    @Test
    void testReplacePattern() {
        String source = "abc123r4g511s";
        Pattern pat = Pattern.compile("\\d+");
        String hex = replaceAll(source, pat,
            m -> "(" + Integer.toHexString(Integer.parseInt(m.group())) + ")");
        assertEquals("abc(7b)r(4)g(1ff)s", hex);
    }

    /**
     * java - Append using Regex Replace with repeating pattern - Stack Overflow
     * https://stackoverflow.com/questions/67224816/append-using-regex-replace-with-repeating-pattern/67226870#67226870
     */
    @Test
    void testStackoverflow() {
        String content = "{ \"values\" : [\"AnyValue1\", \"TestValue\", \"Dummy\", \"SomeValue\"], \"key\" : \"value\" }";
        Pattern bracket = Pattern.compile("\\[.*?\\]");
        Pattern string = Pattern.compile("\"(.*?)\"");
        String result = replaceAll(content, bracket,
            m -> replaceAll(m.group(), string,
                n -> "\"" + n.group(1) + "_val\""));
        String expected = "{ \"values\" : [\"AnyValue1_val\", \"TestValue_val\", \"Dummy_val\", \"SomeValue_val\"], \"key\" : \"value\" }";
        assertEquals(expected, result);
    }
}
