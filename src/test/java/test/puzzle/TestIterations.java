package test.puzzle;

import static org.junit.jupiter.api.Assertions.*;
import static puzzle.Iterations.*;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TestIterations {

    @Test
    void testCharacters() {
        int i = 0;
        for (char ch : characters("abc"))
            switch (i++) {
            case 0: assertEquals('a', ch); break;
            case 1: assertEquals('b', ch); break;
            case 2: assertEquals('c', ch); break;
            }
    }

    @Test
    void testIterableString() {
        int i = 0;
        for (int ch : iterable("漢字𩸽"))
            switch (i++) {
            case 0: assertEquals("漢", Character.toString(ch)); break;
            case 1: assertEquals("字", Character.toString(ch)); break;
            case 2: assertEquals("𩸽", Character.toString(ch)); break;
            }

    }

    @Test
    void testCodePoints() {
        int i = 0;
        for (int ch : codePoints("漢字𩸽"))
            switch (i++) {
            case 0: assertEquals("漢", Character.toString(ch)); break;
            case 1: assertEquals("字", Character.toString(ch)); break;
            case 2: assertEquals("𩸽", Character.toString(ch)); break;
            }

    }

    @Test
    void testStreamString() {
        assertEquals(3, "漢字𩸽".codePoints().count());
        assertEquals(4, streamChar("漢字𩸽").count());
        assertEquals("漢字𩸽", streamChar("漢字𩸽").map(ch -> Character.toString(ch)).collect(Collectors.joining()));
    }

}
