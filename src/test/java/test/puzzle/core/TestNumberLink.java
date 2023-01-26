package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
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
            boolean linked;
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
            
            @Override
            public String toString() {
                return "%d@%d".formatted(row, col) + neighbors.values().stream()
                    .filter(link -> link.linked)
                    .map(link -> link.direction.toString().substring(0, 1))
                    .sorted()
                    .collect(Collectors.joining());
            }
            
            int linkCount() {
                return (int)neighbors.values().stream()
                    .filter(link -> link.linked).count();
            }
            
            void link(Cell other, boolean linked) {
                this.neighbors.get(other).linked
                    = other.neighbors.get(this).linked = linked;
            }
            
            boolean connected(Cell other) {
                Set<Cell> visited = new HashSet<>();
                Deque<Cell> que = new LinkedList<>();
                que.add(this);
                visited.add(this);
                while (!que.isEmpty()) {
                    Cell c = que.remove();
                    if (c.equals(other))
                        return true;
                    for (var e : c.neighbors.entrySet())
                        if (e.getValue().linked && visited.add(e.getKey()))
                            que.add(e.getKey());
                }
                return false;
            }
            
            abstract void solve();
        }
        
        class Number extends Cell {
            final int number;
            
            Number(int row, int col, int number) {
                super(row, col);
                this.number = number;
            }

            @Override
            public String toString() {
                return "%s[%d]".formatted(super.toString(), number);
            }

            @Override
            void solve() {
            }
        }
        
        class Path extends Cell {
            Path(int row, int col) {
                super(row, col);
            }

            void solve() {
            }
        }
        
        final int height, width;
        final Cell[][] board;
        private final Deque<Cell> que = new LinkedList<>();
        
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
        
        void enque(Cell cell) {
            if (!que.contains(cell))
                que.add(cell);
        }
        
        public void solve() {
            que.clear();
            for (var row : board)
                for (var cell : row)
                    que.add(cell);
            while (!que.isEmpty())
                que.remove().solve();
        }
    }
    
    static void printTestCaseName() {
        System.out.println("*** " + Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    
    @Test
    public void testNumberLinkConstructor() {
        printTestCaseName();
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
        assertEquals(Set.of(NumberLink.Direction.RIGHT, NumberLink.Direction.DOWN),
            n.board[0][0].neighbors.values().stream().map(c -> c.direction).collect(Collectors.toSet()));

        assertEquals(NumberLink.Path.class, n.board[1][1].getClass());
        assertEquals(Set.of(n.board[1][0], n.board[0][1], n.board[2][1], n.board[1][2]), n.board[1][1].neighbors.keySet());
        assertEquals(Set.of(NumberLink.Direction.values()),
            n.board[1][1].neighbors.values().stream().map(c -> c.direction).collect(Collectors.toSet()));

        assertEquals(NumberLink.Number.class, n.board[0][4].getClass());
        assertEquals(Set.of(n.board[0][3], n.board[0][5], n.board[1][4]), n.board[0][4].neighbors.keySet());
        assertEquals(Set.of(NumberLink.Direction.DOWN, NumberLink.Direction.LEFT, NumberLink.Direction.RIGHT),
            n.board[0][4].neighbors.values().stream().map(c -> c.direction).collect(Collectors.toSet()));

        assertEquals(NumberLink.Number.class, n.board[0][5].getClass());

        assertEquals(NumberLink.Number.class, n.board[0][6].getClass());
        assertEquals(Set.of(n.board[0][5], n.board[1][6]), n.board[0][6].neighbors.keySet());

        assertEquals(NumberLink.Number.class, n.board[3][2].getClass());

        assertEquals(NumberLink.Number.class, n.board[6][6].getClass());
        assertEquals(Set.of(n.board[6][5], n.board[5][6]), n.board[6][6].neighbors.keySet());
        assertEquals(Set.of(NumberLink.Direction.UP, NumberLink.Direction.LEFT),
            n.board[6][6].neighbors.values().stream().map(c -> c.direction).collect(Collectors.toSet()));

        // neighbors.size()が2,3,4のいずれかであり、それぞれの総数が正しいこと。
        assertEquals(
            Map.of(
                2, 4L,
                3, (long)((n.height - 2 + n.width - 2) * 2),
                4, (long)((n.height - 2) * (n.width - 2))),
            Stream.of(n.board)
                .flatMap(row -> Stream.of(row))
                .map(cell -> cell.neighbors.size())
                .collect(Collectors.groupingBy(size -> size, Collectors.counting())));
        
        // すべてのLink.connectedがfalseであること。
        assertTrue(Stream.of(n.board)
                .flatMap(row -> Stream.of(row))
                .flatMap(cell -> cell.neighbors.values().stream())
                .map(link -> link.linked)
                .allMatch(c -> !c));
    }
    
    @Test
    public void testNumberLinkConnected() {
        printTestCaseName();
        String problem = 
            "- - - - 3 2 1\r\n"
            + "- - - - 1 - -\r\n"
            + "- - - - - - -\r\n"
            + "- - 2 - - - -\r\n"
            + "- - - - - - -\r\n"
            + "- 3 5 - - 4 -\r\n"
            + "4 - - - - - 5\r\n";
        NumberLink n = new NumberLink(problem);
        n.board[0][0].link(n.board[0][1], true);
        n.board[0][0].link(n.board[1][0], true);
        n.board[1][0].link(n.board[2][0], true);
        n.board[2][0].link(n.board[3][0], true);
        n.board[0][6].link(n.board[1][6], true);
        System.out.println(n);
        assertEquals(2, n.board[0][0].linkCount());
        assertEquals(2, n.board[1][0].linkCount());
        assertEquals(2, n.board[2][0].linkCount());
        assertEquals(1, n.board[3][0].linkCount());
        assertEquals(1, n.board[0][6].linkCount());
        assertEquals(1, n.board[1][6].linkCount());
        assertTrue(n.board[0][1].connected(n.board[3][0]));
        assertFalse(n.board[0][1].connected(n.board[0][3]));
    }
    
    @Test
    public void testKanji() {
    	System.out.println("漢字");
    	System.out.println("あいうえお");
    }
}
