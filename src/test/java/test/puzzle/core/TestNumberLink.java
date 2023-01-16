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

        static final Cell OUTER = new Cell() {};

        abstract class Inner extends Cell {
            final int row, col;
            final Cell[] adjacents = new Cell[4];
            final boolean[] links = new boolean[4];
            int pathCount;

            Inner(int row, int col) {
                this.row = row;
                this.col = col;
            }
        }

        class Number extends Inner {
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

        class Path extends Inner {
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
        final Inner[][] matrix;
        
        NumberLink(String[] lines) {
            matrix = IntStream.range(0, lines.length)
                .mapToObj(row -> {
                    String[] cells = lines[row].trim().split("\\s+");
                    return IntStream.range(0, cells.length)
                        .mapToObj(col -> cells[col].matches("\\d+")
                            ? new Number(row, col, Integer.parseInt(cells[col]))
                            : new Path(row, col))
                        .toArray(Inner[]::new);
                })
                .toArray(Inner[][]::new);
            this.height = matrix.length;
            this.width = matrix[0].length;
            for (int r = 0; r < height; ++r)
                if (matrix[r].length != width)
                    throw new IllegalArgumentException("illgal width at row: " + r);
            // adjacentsの設定
            for (int r = 0; r < height; ++r)
                for (int c = 0; c < width; ++c) {
                    Inner inner = matrix[r][c];
                    inner.adjacents[0] = r > 0 ?  matrix[r - 1][c] : OUTER;
                    inner.adjacents[1] = c < width - 1 ?  matrix[r][c + 1] : OUTER;
                    inner.adjacents[2] = r < height - 1 ?  matrix[r + 1][c] : OUTER;
                    inner.adjacents[3] = c > 0 ?  matrix[r][c - 1] : OUTER;
                    inner.pathCount = 0;
                    for (Cell adj : inner.adjacents)
                        if (adj instanceof Path)
                            inner.pathCount++;
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
    public void testPathCount() {
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
        assertEquals(NumberLink.Path.class, n.matrix[0][0].getClass());
        assertEquals(2, n.matrix[0][0].pathCount);
        assertEquals(NumberLink.Path.class, n.matrix[1][1].getClass());
        assertEquals(4, n.matrix[1][1].pathCount);
        assertEquals(NumberLink.Number.class, n.matrix[0][4].getClass());
        assertEquals(1, n.matrix[0][4].pathCount);
        assertEquals(NumberLink.Number.class, n.matrix[0][5].getClass());
        assertEquals(1, n.matrix[0][5].pathCount);
        assertEquals(NumberLink.Number.class, n.matrix[0][6].getClass());
        assertEquals(1, n.matrix[0][6].pathCount);
        assertEquals(NumberLink.Number.class, n.matrix[3][2].getClass());
        assertEquals(4, n.matrix[3][2].pathCount);
        assertEquals(NumberLink.Number.class, n.matrix[6][6].getClass());
        assertEquals(2, n.matrix[6][6].pathCount);
    }

}
