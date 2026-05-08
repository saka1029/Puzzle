package test.puzzle.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        for (int i = 0, length = data.length(); i < length;  ++i) {
            char c = data.charAt(i);
            switch (c) {
                case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8':
                case '9': case 'a': case 'b': case 'c':
                case 'd': case 'e': case 'f':
                    array[j++] = Character.digit(c, 16);
                    break;
                case 'g': case 'h': case 'i': case 'j':
                case 'k': case 'l': case 'm': case 'n':
                case 'o': case 'p': case 'q': case 'r':
                case 's': case 't': case 'u': case 'v':
                case 'w': case 'x': case 'y': case 'z':
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
            iw.graphics().setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[]{2, 6}, 0));
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
            int fontHeight = (int)rect0.getHeight();
            for (int r = 0; r < rows; ++r) {
                int y = r * cellSize + margin + fontHeight + (cellSize - fontHeight) / 2;
                for (int c = 0; c < cols; ++c) {
                    int number = matrix[r][c];
                    if (number == 0)
                        continue;
                    String numberStr = Integer.toString(number);
                    Rectangle2D rect = fm.getStringBounds(numberStr, iw.graphics());
                    int x = c * cellSize + margin + (cellSize - (int)rect.getWidth()) / 2;
                    // System.out.println(numberStr + " : " + rect);
                    iw.graphics().drawString(numberStr, x, y);
                }
            }
        }
    }

    // @Test
    public void testMatrixWriter() throws IOException {
        int[][] matrix = {{0, 1, 22}, {3, 4, 5}};
        matrixWriter(matrix, Paths.get("testMatrixWriter.png"));
    }

    // @Test
    public void testDecodeMatrixWriter() throws IOException {
        int[][] matrix = matrixUrlDecode(URL_22x17);
        matrixWriter(matrix, Paths.get("22x17.png"));
    }

    static final String INDEX_URL = "https://puzzle-laboratory.hatenadiary.jp/entry/2021/11/30/011446";

    @Test
    public void testMatrixDecodeAll() throws IOException {
        Path dir = Paths.get("filomino");
        Files.createDirectories(dir);
        Document doc = Jsoup.connect(INDEX_URL).get();
        Elements links = doc.select("#MyTable a");
        for (Element e : links) {
            System.out.println(e.text() + " " + e.attr("href"));
            matrixWriter(matrixUrlDecode(e.attr("href")), dir.resolve(e.text() + ".png"));
        }
    }

}
