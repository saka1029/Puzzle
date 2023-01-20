package test.puzzle.core;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

public class TestNumberLink {

    public static class NumberLink {
        
        enum Direction {
            UP, DOWN, LEFT, RIGHT;
        }

        static class Link {
            final Direction direction;
            boolean connected;
            Link(Direction direction) {
                this.direction = direction;
            }
        }

        abstract class Cell {
            final int row, col;
            final Map<Cell, Link> neighbors = new HashMap<>();

            Cell(int row, int col) {
                this.row = row;
                this.col = col;
            }
        }
        
        class Number extends Cell {
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
        
        class Path extends Cell {
            Path(int row, int col) {
                super(row, col);
            }
            
            @Override
            public String toString() {
                return "-";
            }
        }
        
        final int height, width;
        final Cell[][] board;
        
        NumberLink(String problem) {
            String[] lines = problem.split("\\R");
            board = IntStream.range(0, lines.length)
                .mapToObj(row -> {
                    String[] cells = lines[row].trim().split("\\s");
                    return IntStream.range(0, cells.length)
                        .mapToObj(col -> cells[col].matches("\\d+")
                            ? new Number(row, col, Integer.parseInt(cells[col]))
                            : new Path(row, col))
                        .toArray(Cell[]::new);
                })
                .toArray(Cell[][]::new);
            this.height = board.length;
            this.width = board[0].length;
            for (int row = 0; row < height; ++row)
                for (int col = 0; col < width; ++col) {
                    Cell cell = board[row][col];
                    if (row > 0)
                        cell.neighbors.put(board[row - 1][col], new Link(Direction.UP));
                    if (row < height - 1)
                        cell.neighbors.put(board[row + 1][col], new Link(Direction.DOWN));
                    if (col > 0)
                        cell.neighbors.put(board[row][col - 1], new Link(Direction.LEFT));
                    if (col < width - 1)
                        cell.neighbors.put(board[row][col + 1], new Link(Direction.RIGHT));
                }
        }
        
        @Override
        public String toString() {
            return Stream.of(board)
                .map(row -> Stream.of(row)
                    .map(Cell::toString)
                    .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(System.lineSeparator()));
        }
    }
    
    @Test
    public void testPathCount() {
        String problem = 
            "- - - - 3 2 1\r\n"
            + "- - - - 1 - -\r\n"
            + "- - - - - - -\r\n"
            + "- - 2 - - - -\r\n"
            + "- - - - - - -\r\n"
            + "- 3 5 - - 4 -\r\n"
            + "4 - - - - - 5\r\n";
        NumberLink n = new NumberLink(problem);
        System.out.println(n);
        assertEquals(NumberLink.Path.class, n.board[0][0].getClass());
        assertEquals(Set.of(n.board[1][0], n.board[0][1]), n.board[0][0].neighbors.keySet());
        assertEquals(NumberLink.Path.class, n.board[1][1].getClass());
        assertEquals(Set.of(n.board[1][0], n.board[0][1], n.board[2][1], n.board[1][2]), n.board[1][1].neighbors.keySet());
        assertEquals(NumberLink.Number.class, n.board[0][4].getClass());
        assertEquals(Set.of(n.board[0][3], n.board[0][5], n.board[1][4]), n.board[0][4].neighbors.keySet());
        assertEquals(NumberLink.Number.class, n.board[0][5].getClass());
        assertEquals(NumberLink.Number.class, n.board[0][6].getClass());
        assertEquals(Set.of(n.board[0][5], n.board[1][6]), n.board[0][6].neighbors.keySet());
        assertEquals(NumberLink.Number.class, n.board[3][2].getClass());
        assertEquals(NumberLink.Number.class, n.board[6][6].getClass());
        assertEquals(Set.of(n.board[6][5], n.board[5][6]), n.board[6][6].neighbors.keySet());
    }

}
