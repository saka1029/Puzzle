package test.puzzle.core;

import java.util.Arrays;

import org.junit.Test;

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

    static int[][] decode(int rows, int cols, String data) {
        int[] array = new int[rows * cols];
        int j = 0;
        for (int i = 0, length = data.length(); i < length;  ++i) {
            char c = data.charAt(i);
            switch (c) {
                case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    array[j++] = c - '0';
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

    @Test
    public void testDecode() {
        String data = "i5h5h6h6k5i123i66g6g4h665i4"
        + "5h4h2l55j-11g-113k6g2h1g1h4g14g-11g33g22g1g31g455g4g55i14h1h42h-"
        + "11i55g1h-11g4g5g32g43g442g3k1g4g1g1h33g4421812g1g32i55p82i51g3g3"
        + "71g455g12h1g5g5g3k4g252g38g88g1g2g1h5g76i2h35h3h21i55g2g65-10g44"
        + "g4g54g63g4g52g2h6g1h1m2-10g4j41l1h6h3j714h-10g2g56i463i2k4h5h4h1i";
        int[][] matrix = decode(17, 22, data);
        for (int[] row : matrix)
            System.out.println(Arrays.toString(row));
    }

    

}
