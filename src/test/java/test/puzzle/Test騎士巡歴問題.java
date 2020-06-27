package test.puzzle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

class Test騎士巡歴問題 {

    static final int[][] MOVES = {{-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1},
        {1, -2}, {-1, -2}, {-2, -1},};

    static class Cell {

        private int order = 0;
        private Set<Cell> moves = new HashSet<>();
        private Cell previous = null;

        static final Comparator<Cell> MIN_MOVES_ORDER = Comparator
            .comparing(cell -> cell.moves.size());

        Iterable<Cell> moves() {
            return () -> moves.stream().sorted(MIN_MOVES_ORDER).iterator();
        }

        public Cell previous() {
            return this.previous;
        }

        boolean setPrevious(int order, Cell previous) {
            assert this.previous == null : "previous is not null";
            this.order = order;
            this.previous = previous;
            for (Cell m : moves)
                if (m.previous == null && m.moves.size() <= 2)
                    return false;
            for (Cell m : moves)
                if (m.previous == null)
                    m.moves.remove(this);
            return true;
        }

        void unsetPrevious() {
            assert this.previous != null : "previous is null";
            this.order = 0;
            this.previous = null;
            for (Cell m : moves)
                m.moves.add(this);
        }

        static Cell[][] board(int height, int width) {
            Cell[][] board = new Cell[height][width];
            for (int row = 0; row < height; ++row)
                for (int col = 0; col < width; ++col)
                    board[row][col] = new Cell();
            for (int row = 0; row < height; ++row)
                for (int col = 0; col < width; ++col) {
                    Cell cell = board[row][col];
                    for (int i = 0, size = MOVES.length; i < size; ++i) {
                        int nextRow = row + MOVES[i][0];
                        int nextCol = col + MOVES[i][1];
                        if (nextRow >= 0 && nextRow < height && nextCol >= 0
                            && nextCol < width)
                            cell.moves.add(board[nextRow][nextCol]);
                    }
                }
            return board;
        }
        
        @Override
        public String toString() {
            return "Cell(" + order + ": moves.size=" + moves.size() + ")";
        }
    }
    
    int 騎士巡歴(int height, int width, int startRow, int startCol, Consumer<Cell[][]> found) {
        int numberOfCells = height * width;
        Cell[][] board = Cell.board(height, width);
        Cell startCell = board[startRow][startCol];
        return new Object() {
            
            int count = 0;
            
            void visit(int i, Cell previous, Cell current) {
                if (i == numberOfCells + 1 && current == startCell) {
                    ++count;
                    found.accept(board);
                } else if (current.previous() == null) {
                    if (!current.setPrevious(i, previous)) {
                        System.out.println("stuck!");
                        return;
                    }
                    for (Cell next : current.moves())
                        visit(i + 1, current, next);
                    current.unsetPrevious();
                }
            }

            int run() {
                visit(1, null, board[startRow][startCol]);
                return count;
            }

        }.run();
    }
    
    static void print(Cell[][] board) {
        for (Cell[] row : board)
            System.out.println(Arrays.toString(
                Arrays.stream(row).mapToInt(cell -> cell.order).toArray()));
        throw new RuntimeException();
    }

    @Test
    void test() {
        int height = 6;
        int width = 6;
        try {
            int count = 騎士巡歴(height, width, 0, 0, board -> print(board));
        } catch (RuntimeException e) {}
    }

}
