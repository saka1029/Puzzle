package test.puzzle.parsers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import puzzle.parsers.CSVReader;
import puzzle.parsers.RFC4180CSVReader;

class TestCSVReader {

    @Test
    void testReadLine() throws IOException {
        String csv = "a,\"b \"\"c\\\",d\",e\r\n"    // 引用符内の引用符およびカンマ、CRLFの行区切り
            + "\"f\rg\r\nh\ni\", j ,k\n"            // 引用符内の改行、LFの行区切り
            + "l,m n \"o,p\" q";                    // 引用符あり、なしの混在
        CSVReader.with(csv, CSVReader::new, reader -> {
            assertEquals(List.of("a", "b \"c\",d", "e"), reader.readLine());
            assertEquals(List.of("f\rg\r\nh\ni", " j ", "k"), reader.readLine());
            assertEquals(List.of("l", "m n o,p q"), reader.readLine());
            assertEquals(null, reader.readLine());
        });
    }

    @Test
    void testFile() throws IOException {
        CSVReader.with(new File("data/test.csv"), CSVReader::new, reader -> {
            assertEquals(List.of("a", "b \"c\",d", "e"), reader.readLine());
            assertEquals(List.of("f\ng\nh\ni", " j ", "k"), reader.readLine());
            assertEquals(List.of("l", "m n o,p q"), reader.readLine());
            assertEquals(null, reader.readLine());
        });
    }

    @Test
    void testRFC4180() throws IOException {
        String csv = "a,\"b \"\"c\"\",d\",e\r\n"    // 引用符内の引用符およびカンマ、CRLFの行区切り
            + "\"f\rg\r\nh\ni\", j ,k\r\n"            // 引用符内の改行、LFの行区切り
            + "l,\"o,p\"";                    // 引用符あり、なしの混在
        CSVReader.with(csv, RFC4180CSVReader::new, reader -> {
            assertEquals(List.of("a", "b \"c\",d", "e"), reader.readLine());
            assertEquals(List.of("f\rg\r\nh\ni", " j ", "k"), reader.readLine());
            assertEquals(List.of("l", "o,p"), reader.readLine());
            assertEquals(null, reader.readLine());
        });
    }

}
