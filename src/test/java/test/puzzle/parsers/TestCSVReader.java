package test.puzzle.parsers;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;
import org.junit.Ignore;

import puzzle.parsers.CSVReader;
import puzzle.parsers.RFC4180CSVReader;

public class TestCSVReader {

    @Test
    public void testReadLine() throws IOException {
        String csv = "a,\"b \"\"c\\\",d\",e\r\n"    // 引用符内の引用符およびカンマ、CRLFの行区切り
            + "\"f\rg\r\nh\ni\", j ,k\n"            // 引用符内の改行、LFの行区切り
            + "l,m n \"o,p\" q";                    // 引用符あり、なしの混在
        try (BufferedReader br = new BufferedReader(new StringReader(csv))) {
            CSVReader reader = new CSVReader(br);
            assertEquals(List.of("a", "b \"c\",d", "e"), reader.readLine());
            assertEquals(List.of("f\rg\r\nh\ni", " j ", "k"), reader.readLine());
            assertEquals(List.of("l", "m n o,p q"), reader.readLine());
            assertEquals(null, reader.readLine());
        }
    }

    @Ignore
    @Test
    public void testFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("data/test.csv"))) {
            CSVReader reader = new CSVReader(br);
            assertEquals(List.of("a", "b \"c\",d", "e"), reader.readLine());
            assertEquals(List.of("f\ng\nh\ni", " j ", "k"), reader.readLine());
            assertEquals(List.of("l", "m n o,p q"), reader.readLine());
            assertEquals(null, reader.readLine());
        }
    }

    @Test
    public void testRFC4180() throws IOException {
        String csv = "a,\"b \"\"c\"\",d\",e\r\n"    // 引用符内の引用符およびカンマ、CRLFの行区切り
            + "\"f\rg\r\nh\ni\", j ,k\r\n"            // 引用符内の改行、LFの行区切り
            + "l,\"o,p\"";                    // 引用符あり、なしの混在
        try (BufferedReader br = new BufferedReader(new StringReader(csv))) {
            RFC4180CSVReader reader = new RFC4180CSVReader(br);
            assertEquals(List.of("a", "b \"c\",d", "e"), reader.readLine());
            assertEquals(List.of("f\rg\r\nh\ni", " j ", "k"), reader.readLine());
            assertEquals(List.of("l", "o,p"), reader.readLine());
            assertEquals(null, reader.readLine());
        }
    }

}
