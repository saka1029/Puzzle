package test.puzzle.encoder;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import puzzle.core.Common;
import puzzle.encoder.Encoder;
import puzzle.encoder.Encoder.Entry;
import puzzle.parsers.CSVReader;
import puzzle.encoder.HashEncoder;
import puzzle.encoder.TrieEncoder;

/**
 * TrieEncoderとHashEncoderについて同じテストを実行し、
 * パフォーマンスを比較する。
 */
@RunWith(Theories.class)
public class TestPerformance {

    static final Logger logger = Common.getLogger(TestPerformance.class);

    @DataPoint
    public static Encoder<String> hashEncoder = new HashEncoder<>();

    @DataPoint
    public static Encoder<String> trieEncoder = new TrieEncoder<>();

    @DataPoint
    public static Encoder<String> hashEncoder2 = new HashEncoder<>();

    @DataPoint
    public static Encoder<String> trieEncoder2 = new TrieEncoder<>();

    @Theory
    public void testEncode(Encoder<String> encoder) {
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
    
    static final Charset レセ電CHARSET = Charset.forName("Shift_JIS");
    static final Charset 未コードCHARSET = StandardCharsets.UTF_8;

    static void load(Encoder<String> encoder, int codeCol, int nameCol, Path path) throws IOException {
        try (CSVReader reader = new CSVReader(path, レセ電CHARSET)) {
            List<String> row;
            while ((row = reader.readLine()) != null)
                encoder.put(row.get(nameCol), row.get(codeCol));
        }
    }

    // @Theory
    public void testPerformance(Encoder<String> encoder) throws IOException {
        // logger.info("Testing encoder: " + encoder.getClass().getSimpleName());
        // logger.info("loading start");
        long startLoad = System.currentTimeMillis();
        load(encoder, 2, 5, Path.of("data/レセ電/b_20200601.txt"));
        load(encoder, 2, 6, Path.of("data/レセ電/z_20200601.txt"));
        long endLoad = System.currentTimeMillis();
        // logger.info("loading end: " + (endLoad - startLoad) + " ms");
        // logger.info("encoding start");
        long start = System.currentTimeMillis();
        try (CSVReader reader = new CSVReader(Path.of("data/レセ電/micode.txt"), 未コードCHARSET)) {
            List<String> row;
            while ((row = reader.readLine()) != null) {
                String name = row.get(1);
                List<List<Entry<String>>> entries = encoder.encode(name);
                // logger.info("  " + name);
                for (List<Entry<String>> entryList : entries) {
                    StringBuilder sb = new StringBuilder();
                    for (Entry<String> entry : entryList)
                        sb.append(" ").append(entry.value()).append(":").append(entry.key());
                    // logger.info("    " + sb.toString());
                }
            }
        }
        long end = System.currentTimeMillis();
        // logger.info("encoding end: " + (end - start) + " ms");
        logger.info("Performance %s, load: %dms, encode: %dms".formatted(
            encoder.getClass().getSimpleName(), (endLoad - startLoad), (end - start)));
    }
}
