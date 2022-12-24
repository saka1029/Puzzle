package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TestSudoku {

    static boolean solve(int[][] board) {
        int size = 9;
        return new Object() {
            
            boolean isNumberInRow(int number, int row) {
                for (int column = 0; column < size; ++column)
                    if (board[row][column] == number)
                        return true;
                return false;
            }
            
            boolean isNumberInColumn(int number, int column) {
                for (int row = 0; row < size; ++row)
                    if (board[row][column] == number)
                        return true;
                return false;
            }
            
            boolean isNumberInBox(int number, int row, int column) {
                int boxRow = row - row % 3;
                int boxColumn = column - column % 3;
                for (int i = boxRow, maxRow = boxRow + 3; i < maxRow; ++i)
                    for (int j = boxColumn, maxColumn = boxColumn + 3; j < maxColumn; ++j)
                        if (board[i][j] == number)
                            return true;
                return false;
            }
            
            boolean isValidPlacement(int number, int row, int column) {
                return !isNumberInRow(number, row)
                    && !isNumberInColumn(number, column)
                    && !isNumberInBox(number, row, column);
            }
            
            boolean solve() {
                for (int row = 0; row < size; ++row) {
                    for (int column = 0; column < size; ++column) {
                        if (board[row][column] == 0) {
                            for (int number = 1; number <= 9; ++number) {
                                if (isValidPlacement(number, row, column)) {
                                    board[row][column] = number;
                                    if (solve())
                                        return true;
                                    else
                                        board[row][column] = 0;
                                }
                            }
                            return false;
                        }
                    }
                }
                return true;
            }
        }.solve();
    }

    @Test
    public void test() {
        int[][] board = {
            {7, 0, 2, 0, 5, 0, 6, 0, 0},
            {0, 0, 0, 0, 0, 3, 0, 0, 0},
            {1, 0, 0, 0, 0, 9, 5, 0, 0},
            {8, 0, 0, 0, 0, 0, 0, 9, 0},
            {0, 4, 3, 0, 0, 0, 7, 5, 0},
            {0, 9, 0, 0, 0, 0, 0, 0, 8},
            {0, 0, 9, 7, 0, 0, 0, 0, 5},
            {0, 0, 0, 2, 0, 0, 0, 0, 0},
            {0, 0, 7, 0, 4, 0, 2, 0, 3},
        };
        if (solve(board))
            for (int[] row : board)
                System.out.println(Arrays.toString(row));
    }

    @Test
    public void testZeros() {
        int[][] board = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
        };
        if (solve(board))
            for (int[] row : board)
                System.out.println(Arrays.toString(row));
    }

}
