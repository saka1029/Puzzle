package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

public class TestNumberLink {

    public static class NumberLink {
        static abstract class Cell {
        }

        static class Outer extends Cell {
        }

        static final Outer OUTER = new Outer();

        static abstract class Inner extends Cell {
            final int row, col;
            final Cell[] adjacents = new Cell[4];
            final boolean[] links = new boolean[4];

            Inner(int row, int col) {
                this.row = row;
                this.col = col;
            }
        }

        static class Number extends Inner {
            final int number;

            Number(int row, int col, int number) {
                super(row, col);
                this.number = number;
            }
            
            @Override
            public String toString() {
                return "" + number;
            }
        }

        static class Path extends Inner {
            Path(int row, int col) {
                super(row, col);
            }
            
            @Override
            public String toString() {
                return "-";
            }
        }

        @Test
        public void test() {
            fail("Not yet implemented");
        }
        
        final int height, width;
        final Cell[][] matrix;
        
        NumberLink(String[] lines) {
            matrix = IntStream.range(0, lines.length)
                .mapToObj(row -> {
                    String[] cells = lines[row].trim().split("\\s+");
                    return IntStream.range(0, cells.length)
                        .mapToObj(col -> cells[col].matches("\\d+")
                            ? new Number(row, col, Integer.parseInt(cells[col]))
                            : new Path(row, col))
                        .toArray(Cell[]::new);
                })
                .toArray(Cell[][]::new);
            this.height = matrix.length;
            this.width = matrix[0].length;
            for (Cell[] row : matrix)
                if (row.length != width)
                    throw new IllegalArgumentException("illgal width: " + Arrays.toString(row));
            // adjacentsの設定
            for (int r = 0; r < height; ++r)
                for (int c = 0; c < width; ++c) {
                    Inner inner = (Inner)matrix[r][c];
                    inner.adjacents[0] = r > 0 ?  matrix[r - 1][c] : OUTER;
                    inner.adjacents[1] = c < width - 1 ?  matrix[r][c + 1] : OUTER;
                    inner.adjacents[2] = r < height - 1 ?  matrix[r + 1][c] : OUTER;
                    inner.adjacents[3] = c > 0 ?  matrix[r][c - 1] : OUTER;
                }
        }
        
        @Override
        public String toString() {
            return Stream.of(matrix)
                .map(row -> Stream.of(row)
                    .map(Cell::toString)
                    .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(System.lineSeparator()));
        }
    }
    
    @Test
    public void testNumberLink() {
        String[] simple = {
            "- - - - 3 2 1",
            "- - - - 1 - -",
            "- - - - - - -",
            "- - 2 - - - -",
            "- - - - - - -",
            "- 3 5 - - 4 -",
            "4 - - - - - 5",
        };
        NumberLink n = new NumberLink(simple);
        System.out.println(n);
    }

}
