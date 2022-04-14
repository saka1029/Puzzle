package test.puzzle.parsers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Test;

import puzzle.core.Common;

public class TestIntCalculator {

    static final Logger logger = Common.getLogger(TestIntCalculator.class);

    @Test
    public void testCharSequence() {
        class CharSubsequence implements CharSequence {

            final String source;
            final int start;
            final List<String> log = new ArrayList<>();

            public CharSubsequence(String source, int start) {
                this.source = source;
                this.start = start;
            }

            @Override
            public int length() {
                log.add("length()");
                return source.length() - start;
            }

            @Override
            public char charAt(int index) {
                log.add("charAt(" + index + ")");
                return source.charAt(index + start);
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                log.add("subSequence(" + start + "," + end + ")");
                return new CharSubsequence(source, start + this.start);
            }

            @Override
            public String toString() {
                return source.substring(start);
            }
        }

        CharSubsequence str = new CharSubsequence("abcd03d", 2);
        assertEquals("cd03d", str.toString());
        assertEquals("03d", str.subSequence(2, 5).toString());
        Pattern pat = Pattern.compile("\\w+[\\w\\d]+");
        str.log.clear();
        assertTrue(pat.matcher(str).matches());
        assertEquals(List.of("length()", "charAt(0)", "charAt(1)", "charAt(2)",
            "charAt(3)", "charAt(4)", "charAt(4)"), str.log);
    }

}
