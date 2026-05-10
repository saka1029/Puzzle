package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import puzzle.graphics.ImageWriter;

/**
 * 以下のページで使われているフィルオミノ問題のURL表現をデコードする。
 * <br>
 * https://puzzle-laboratory.hatenadiary.jp/entry/2021/11/30/011446
 * <br>
 * ex)
 * 17行22列の問題<br>
 * http://pzv.jp/p.html?fillomino/22/17/i5h5h6h6k5i123i66g6g4h665i4
 * 5h4h2l55j-11g-113k6g2h1g1h4g14g-11g33g22g1g31g455g4g55i14h1h42h-
 * 11i55g1h-11g4g5g32g43g442g3k1g4g1g1h33g4421812g1g32i55p82i51g3g3
 * 71g455g12h1g5g5g3k4g252g38g88g1g2g1h5g76i2h35h3h21i55g2g65-10g44
 * g4g54g63g4g52g2h6g1h1m2-10g4j41l1h6h3j714h-10g2g56i463i2k4h5h4h1i
 */
public class TestFilominoDecoder {

    static int[][] matrixDecode(int rows, int cols, String data) {
        int[] array = new int[rows * cols];
        int j = 0;
        for (int i = 0, length = data.length(); i < length; ++i) {
            char c = data.charAt(i);
            switch (c) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    array[j++] = Character.digit(c, 16);
                    break;
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    for (int r = c - 'f'; r > 0; --r)
                        array[j++] = 0;
                    break;
                case '-':
                    array[j++] = Character.digit(data.charAt(++i), 16) * 16
                            + Character.digit(data.charAt(++i), 16);
                    break;
                default:
                    throw new RuntimeException(
                            "Unknown char '%c' at %d".formatted(c, i));
            }

        }
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; ++i)
            System.arraycopy(array, i * cols, matrix[i], 0, cols);
        return matrix;
    }

    static final Pattern FILOMINO_URL = Pattern.compile("/(\\d+)/(\\d+)/(\\S+)$");

    static int[][] matrixUrlDecode(String url) {
        Matcher m = FILOMINO_URL.matcher(url);
        if (m.find())
            return matrixDecode(
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(1)),
                    m.group(3));
        else
            throw new RuntimeException("Illgal format '%s'".formatted(url));
    }

    static String URL_22x17 = "http://pzv.jp/p.html?fillomino/22/17/i5h5h6h6k5i123i66g6g4h665i4"
            + "5h4h2l55j-11g-113k6g2h1g1h4g14g-11g33g22g1g31g455g4g55i14h1h42h-"
            + "11i55g1h-11g4g5g32g43g442g3k1g4g1g1h33g4421812g1g32i55p82i51g3g3"
            + "71g455g12h1g5g5g3k4g252g38g88g1g2g1h5g76i2h35h3h21i55g2g65-10g44"
            + "g4g54g63g4g52g2h6g1h1m2-10g4j41l1h6h3j714h-10g2g56i463i2k4h5h4h1i";

    // @Test
    public void testDecode() {
        int[][] matrix = matrixUrlDecode(URL_22x17);
        for (int[] row : matrix)
            System.out.println(Arrays.toString(row));
    }

    static void matrixWriter(int[][] matrix, Path file) throws IOException {
        int cellSize = 40;
        int margin = 5;
        int rows = matrix.length, cols = matrix[0].length;
        int height = rows * cellSize + 2 * margin, width = cols * cellSize + 2 * margin;
        try (OutputStream os = Files.newOutputStream(file);
                ImageWriter iw = new ImageWriter(os, width, height);) {
            iw.graphics().setColor(Color.WHITE);
            iw.graphics().fillRect(0, 0, width, height);
            iw.graphics().setColor(Color.BLACK);
            iw.graphics().setStroke(new BasicStroke(3));
            iw.graphics().drawRect(margin, margin, cols * cellSize, rows * cellSize);
            iw.graphics().setStroke(
                    new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[] { 2, 6 }, 0));
            for (int x = 0; x < cols; ++x) {
                int x0 = x * cellSize + margin;
                iw.graphics().drawLine(x0, margin, x0, rows * cellSize + margin);
            }
            for (int y = 0; y < rows; ++y) {
                int y0 = y * cellSize + margin;
                iw.graphics().drawLine(margin, y0, cols * cellSize + margin, y0);
            }
            iw.graphics().setFont(new Font("Helvetica", Font.PLAIN, 25));
            FontMetrics fm = iw.graphics().getFontMetrics();
            Rectangle2D rect0 = fm.getStringBounds("0", iw.graphics());
            int fontHeight = (int) rect0.getHeight();
            for (int r = 0; r < rows; ++r) {
                int y = r * cellSize + margin + fontHeight + (cellSize - fontHeight) / 2;
                for (int c = 0; c < cols; ++c) {
                    int number = matrix[r][c];
                    if (number == 0)
                        continue;
                    String numberStr = Integer.toString(number);
                    Rectangle2D rect = fm.getStringBounds(numberStr, iw.graphics());
                    int x = c * cellSize + margin + (cellSize - (int) rect.getWidth()) / 2;
                    // System.out.println(numberStr + " : " + rect);
                    iw.graphics().drawString(numberStr, x, y);
                }
            }
        }
    }

    // @Test
    public void testMatrixWriter() throws IOException {
        int[][] matrix = { { 0, 1, 22 }, { 3, 4, 5 } };
        matrixWriter(matrix, Paths.get("testMatrixWriter.png"));
    }

    // @Test
    public void testDecodeMatrixWriter() throws IOException {
        int[][] matrix = matrixUrlDecode(URL_22x17);
        matrixWriter(matrix, Paths.get("22x17.png"));
    }

    static final String INDEX_URL = "https://puzzle-laboratory.hatenadiary.jp/entry/2021/11/30/011446";
    static final String INDEX_URL2 = "https://puzzle-laboratory.hatenadiary.jp/entry/2022/08/05/161101";

    static void matrixDecodePage(String url, String dir) throws IOException {
        Path path = Paths.get(dir);
        Files.createDirectories(path);
        Document doc = Jsoup.connect(INDEX_URL).get();
        Elements links = doc.select("a");
        for (Element e : links) {
            String href = e.attr("href");
            if (href.contains("fillomino")) {
                System.out.println(e.text() + " " + href);
                matrixWriter(matrixUrlDecode(href), path.resolve(e.text() + ".png"));
            }
        }

    }

    // @Test
    public void testMatrixDecodePage() throws IOException {
        matrixDecodePage(INDEX_URL, "fillomino");
        matrixDecodePage(INDEX_URL2, "fillomino2");
    }

    static int[] changeBase(int[] in, int inBase, int outBase) {
        BigInteger bigInBase = BigInteger.valueOf(inBase);
        BigInteger bigOutBase = BigInteger.valueOf(outBase);
        BigInteger number = IntStream.of(in)
                .mapToObj(i -> BigInteger.valueOf(i))
                .reduce(BigInteger.ZERO, (n, d) -> n.multiply(bigInBase).add(d));
        List<Integer> out = new LinkedList<>();
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] d = number.divideAndRemainder(bigOutBase);
            number = d[0];
            out.addFirst(d[1].intValue());
        }
        return out.stream().mapToInt(i -> i).toArray();
    }

    @Test
    public void testChangeBase() {
        // (12345)10 = (3039)16
        int[] in = { 1, 2, 3, 4, 5 };
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, changeBase(in, 10, 10));
        assertArrayEquals(new int[] { 3, 0, 3, 9 }, changeBase(in, 10, 16));
    }

    static Map<Character, Integer> unmap(String map) {
        return IntStream.range(0, map.length())
                .mapToObj(i -> i)
                .collect(Collectors.toMap(i -> map.charAt(i), i -> i));
    }

    static int[] encode(String s, Map<Character, Integer> map) {
        return s.chars().map(i -> map.get((char) i)).toArray();
    }

    static String decode(int[] s, String map) {
        StringBuilder sb = new StringBuilder();
        IntStream.of(s).map(i -> map.charAt(i)).forEach(i -> sb.appendCodePoint(i));
        return sb.toString();
    }

    @Test
    public void testMapEncode() {
        String map = "abc";
        var unmap = unmap(map);
        assertEquals(Map.of('a', 0, 'b', 1, 'c', 2), unmap);
        assertArrayEquals(new int[] { 2, 1, 0 }, encode("cba", unmap));
        assertEquals("cba", decode(encode("cba", unmap), "abc"));
    }

    static int[][] NIKOLI = {
        { 0, 2, 0, 4, 0, 2, 0 },
        { 1, 0, 2, 0, 6, 0, 6 },
        { 3, 0, 0, 3, 0, 0, 3 },
        { 0, 0, 0, 5, 0, 0, 0 },
        { 3, 0, 0, 2, 0, 0, 3 },
        { 3, 0, 2, 0, 4, 0, 2 },
        { 0, 3, 0, 3, 0, 1, 0 },
    };

    static String fillominoString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        sb.append(matrix.length).append(",").append(matrix[0].length);
        for (int[] row : matrix)
            for (int cell : row)
                sb.append(",").append(cell == 0 ? "" : ("" + cell));
        return sb.toString();
    }

    @Test
    public void testFillominoString() {
        assertEquals(
            "7,7,,2,,4,,2,,1,,2,,6,,6,3,,,3,,,3,,,,5,,,,3,,,2,,,3,3,,2,,4,,2,,3,,3,,1,",
            fillominoString(NIKOLI));
    }

    static String fillominoBlanks(int n) {
        return "z".repeat(n / 26) + (char)('a' + n % 26 - 1);
    }

    static String fillominoEncode(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        sb.append(matrix.length).append("a").append(matrix[0].length);
        int commas = 1;
        for (int[] row : matrix)
            for (int cell : row)
                if (cell == 0)
                    ++commas;
                else {
                    String spaces = "z".repeat(commas / 26)
                        + (char)('a' + commas % 26 - 1);
                    sb.append(spaces).append(cell);
                    commas = 1;
                }
        return sb.toString();
    }

    @Test
    public void testFillominoEncode() {
        assertEquals("7a7b2b4b2b1b2b6b6a3c3c3d5d3c2c3a3b2b4b2b3b3b1", fillominoEncode(NIKOLI));
        int[][] m = {
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,2},
        };
        assertEquals("2a14a1za2", fillominoEncode(m));
    }

    static final Pattern FILLOMINO_DECODE_PATTERN = Pattern.compile("[a-z]");

    static int[][] fillominoDecode(String s) {
        String csv = FILLOMINO_DECODE_PATTERN.matcher(s).replaceAll(
            m -> ",".repeat(m.group().charAt(0) - 'a' + 1));
        int[] numbers = Stream.of(csv.split(","))
            .mapToInt(x -> x.isEmpty() ? 0 : Integer.parseInt(x))
            .toArray();
        int len = numbers.length;
        int rows = numbers[0], cols = numbers[1];
        int[][] matrix = new int[rows][cols];
        for (int i = 2, r = 0; r < rows; ++r)
            for (int c = 0; i < len && c < cols; ++c, ++i)
                matrix[r][c] = numbers[i];
        return matrix;
    }

    @Test
    public void testFillominoDecode() {
        assertArrayEquals(NIKOLI, fillominoDecode("7a7b2b4b2b1b2b6b6a3c3c3d5d3c2c3a3b2b4b2b3b3b1"));
        int[][] m = {
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,2},
        };
        assertArrayEquals(m, fillominoDecode("2a14a1za2"));
    }

    static final String IN_MAP = "0123456789,";
    static final Map<Character, Integer> IN_UNMAP = unmap(IN_MAP);
    static final String OUT_MAP = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.-";
    static final Map<Character, Integer> OUT_UNMAP = unmap(OUT_MAP);

    @Test
    public void testFillominoStringEncode() {
        assertEquals(11, IN_MAP.length());
        assertEquals(64, OUT_MAP.length());
        String string = fillominoString(NIKOLI);
        System.out.println(string);
        assertEquals("7,7,,2,,4,,2,,1,,2,,6,,6,3,,,3,,,3,,,,5,,,,3,,,2,,,3,3,,2,,4,,2,,3,,3,,1,", string);
        int[] ints = encode(string, IN_UNMAP);
        int[] baseChange = changeBase(ints, IN_MAP.length(), OUT_MAP.length());
        String encode = decode(baseChange, OUT_MAP);
        System.out.println(encode);
        int[] encodeInts = encode(encode, OUT_UNMAP);
        int[] encodeBaseChange = changeBase(encodeInts, OUT_MAP.length(), IN_MAP.length());
        String encodeEncode = decode(encodeBaseChange, IN_MAP);
        assertArrayEquals(baseChange, encodeInts);
        assertArrayEquals(ints, encodeBaseChange);
        assertEquals(string, encodeEncode);
    }
}
