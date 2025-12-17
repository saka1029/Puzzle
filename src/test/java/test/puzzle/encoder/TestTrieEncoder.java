package test.puzzle.encoder;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import puzzle.encoder.Encoder;
import puzzle.encoder.TrieEncoder;
import puzzle.encoder.Encoder.Entry;

public class TestTrieEncoder {

    @Test
    public void testEncode() {
        Encoder<String> encoder = new TrieEncoder<>();
        encoder.put("a", "A");
        encoder.put("b", "B");
        encoder.put("c", "C");
        encoder.put("ab", "AB");
        encoder.put("bc", "BC");
        encoder.put("abc", "ABC");
        assertEquals(List.of(
            List.of(new Entry<>("a", "A"), new Entry<>("b", "B"), new Entry<>("c", "C")),
            List.of(new Entry<>("a", "A"), new Entry<>("bc", "BC")),
            List.of(new Entry<>("ab", "AB"), new Entry<>("c", "C")),
            List.of(new Entry<>("abc", "ABC"))),
            encoder.encode("abc")
        );
    }
}
