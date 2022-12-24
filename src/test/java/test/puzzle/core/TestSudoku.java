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

    static final int SIZE = 9;

    static boolean isNumberInRow(int[][] board, int number, int row) {
        for (int column = 0; column < SIZE; ++column)
            if (board[row][column] == number)
                return true;
        return false;
    }

    static boolean isNumberInColumn(int[][] board, int number, int column) {
        for (int row = 0; row < SIZE; ++row)
            if (board[row][column] == number)
                return true;
        return false;
    }

    static boolean isNumberInBox(int[][] board, int number, int row, int column) {
        int boxRow = row - row % 3;
        int boxColumn = column - column % 3;
        for (int i = boxRow, maxRow = boxRow + 3; i < maxRow; ++i)
            for (int j = boxColumn, maxColumn = boxColumn + 3; j < maxColumn; ++j)
                if (board[i][j] == number)
                    return true;
        return false;
    }

    static boolean isValidPlacement(int[][] board, int number, int row, int column) {
        board[row][column] = 0;
        boolean r = !isNumberInRow(board, number, row)
            && !isNumberInColumn(board, number, column)
            && !isNumberInBox(board, number, row, column);
        board[row][column] = number;
        return r;
    }

    @Test
    public void testGen() {
        int[][] a = {
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
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (a[i][j] == 0) {
                    String v = "a[%d][%d]".formatted(i, j);
                    System.out.printf("for (%1$s = 1; %1$s <= 9; ++%1$s)%n", v);
                    System.out.printf("if (isValidPlacement(a, %s, %d, %d))%n", v, i, j);
                }
            }
        }
        System.out.println("for (int[] row : a)");
        System.out.println("System.out.println(Arrays.toString(row));");
    }

    
    @Test
    public void testSolve() {
        int[][] a = {
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
        for (a[0][1] = 1; a[0][1] <= 9; ++a[0][1])
        if (isValidPlacement(a, a[0][1], 0, 1))
        for (a[0][3] = 1; a[0][3] <= 9; ++a[0][3])
        if (isValidPlacement(a, a[0][3], 0, 3))
        for (a[0][5] = 1; a[0][5] <= 9; ++a[0][5])
        if (isValidPlacement(a, a[0][5], 0, 5))
        for (a[0][7] = 1; a[0][7] <= 9; ++a[0][7])
        if (isValidPlacement(a, a[0][7], 0, 7))
        for (a[0][8] = 1; a[0][8] <= 9; ++a[0][8])
        if (isValidPlacement(a, a[0][8], 0, 8))
        for (a[1][0] = 1; a[1][0] <= 9; ++a[1][0])
        if (isValidPlacement(a, a[1][0], 1, 0))
        for (a[1][1] = 1; a[1][1] <= 9; ++a[1][1])
        if (isValidPlacement(a, a[1][1], 1, 1))
        for (a[1][2] = 1; a[1][2] <= 9; ++a[1][2])
        if (isValidPlacement(a, a[1][2], 1, 2))
        for (a[1][3] = 1; a[1][3] <= 9; ++a[1][3])
        if (isValidPlacement(a, a[1][3], 1, 3))
        for (a[1][4] = 1; a[1][4] <= 9; ++a[1][4])
        if (isValidPlacement(a, a[1][4], 1, 4))
        for (a[1][6] = 1; a[1][6] <= 9; ++a[1][6])
        if (isValidPlacement(a, a[1][6], 1, 6))
        for (a[1][7] = 1; a[1][7] <= 9; ++a[1][7])
        if (isValidPlacement(a, a[1][7], 1, 7))
        for (a[1][8] = 1; a[1][8] <= 9; ++a[1][8])
        if (isValidPlacement(a, a[1][8], 1, 8))
        for (a[2][1] = 1; a[2][1] <= 9; ++a[2][1])
        if (isValidPlacement(a, a[2][1], 2, 1))
        for (a[2][2] = 1; a[2][2] <= 9; ++a[2][2])
        if (isValidPlacement(a, a[2][2], 2, 2))
        for (a[2][3] = 1; a[2][3] <= 9; ++a[2][3])
        if (isValidPlacement(a, a[2][3], 2, 3))
        for (a[2][4] = 1; a[2][4] <= 9; ++a[2][4])
        if (isValidPlacement(a, a[2][4], 2, 4))
        for (a[2][7] = 1; a[2][7] <= 9; ++a[2][7])
        if (isValidPlacement(a, a[2][7], 2, 7))
        for (a[2][8] = 1; a[2][8] <= 9; ++a[2][8])
        if (isValidPlacement(a, a[2][8], 2, 8))
        for (a[3][1] = 1; a[3][1] <= 9; ++a[3][1])
        if (isValidPlacement(a, a[3][1], 3, 1))
        for (a[3][2] = 1; a[3][2] <= 9; ++a[3][2])
        if (isValidPlacement(a, a[3][2], 3, 2))
        for (a[3][3] = 1; a[3][3] <= 9; ++a[3][3])
        if (isValidPlacement(a, a[3][3], 3, 3))
        for (a[3][4] = 1; a[3][4] <= 9; ++a[3][4])
        if (isValidPlacement(a, a[3][4], 3, 4))
        for (a[3][5] = 1; a[3][5] <= 9; ++a[3][5])
        if (isValidPlacement(a, a[3][5], 3, 5))
        for (a[3][6] = 1; a[3][6] <= 9; ++a[3][6])
        if (isValidPlacement(a, a[3][6], 3, 6))
        for (a[3][8] = 1; a[3][8] <= 9; ++a[3][8])
        if (isValidPlacement(a, a[3][8], 3, 8))
        for (a[4][0] = 1; a[4][0] <= 9; ++a[4][0])
        if (isValidPlacement(a, a[4][0], 4, 0))
        for (a[4][3] = 1; a[4][3] <= 9; ++a[4][3])
        if (isValidPlacement(a, a[4][3], 4, 3))
        for (a[4][4] = 1; a[4][4] <= 9; ++a[4][4])
        if (isValidPlacement(a, a[4][4], 4, 4))
        for (a[4][5] = 1; a[4][5] <= 9; ++a[4][5])
        if (isValidPlacement(a, a[4][5], 4, 5))
        for (a[4][8] = 1; a[4][8] <= 9; ++a[4][8])
        if (isValidPlacement(a, a[4][8], 4, 8))
        for (a[5][0] = 1; a[5][0] <= 9; ++a[5][0])
        if (isValidPlacement(a, a[5][0], 5, 0))
        for (a[5][2] = 1; a[5][2] <= 9; ++a[5][2])
        if (isValidPlacement(a, a[5][2], 5, 2))
        for (a[5][3] = 1; a[5][3] <= 9; ++a[5][3])
        if (isValidPlacement(a, a[5][3], 5, 3))
        for (a[5][4] = 1; a[5][4] <= 9; ++a[5][4])
        if (isValidPlacement(a, a[5][4], 5, 4))
        for (a[5][5] = 1; a[5][5] <= 9; ++a[5][5])
        if (isValidPlacement(a, a[5][5], 5, 5))
        for (a[5][6] = 1; a[5][6] <= 9; ++a[5][6])
        if (isValidPlacement(a, a[5][6], 5, 6))
        for (a[5][7] = 1; a[5][7] <= 9; ++a[5][7])
        if (isValidPlacement(a, a[5][7], 5, 7))
        for (a[6][0] = 1; a[6][0] <= 9; ++a[6][0])
        if (isValidPlacement(a, a[6][0], 6, 0))
        for (a[6][1] = 1; a[6][1] <= 9; ++a[6][1])
        if (isValidPlacement(a, a[6][1], 6, 1))
        for (a[6][4] = 1; a[6][4] <= 9; ++a[6][4])
        if (isValidPlacement(a, a[6][4], 6, 4))
        for (a[6][5] = 1; a[6][5] <= 9; ++a[6][5])
        if (isValidPlacement(a, a[6][5], 6, 5))
        for (a[6][6] = 1; a[6][6] <= 9; ++a[6][6])
        if (isValidPlacement(a, a[6][6], 6, 6))
        for (a[6][7] = 1; a[6][7] <= 9; ++a[6][7])
        if (isValidPlacement(a, a[6][7], 6, 7))
        for (a[7][0] = 1; a[7][0] <= 9; ++a[7][0])
        if (isValidPlacement(a, a[7][0], 7, 0))
        for (a[7][1] = 1; a[7][1] <= 9; ++a[7][1])
        if (isValidPlacement(a, a[7][1], 7, 1))
        for (a[7][2] = 1; a[7][2] <= 9; ++a[7][2])
        if (isValidPlacement(a, a[7][2], 7, 2))
        for (a[7][4] = 1; a[7][4] <= 9; ++a[7][4])
        if (isValidPlacement(a, a[7][4], 7, 4))
        for (a[7][5] = 1; a[7][5] <= 9; ++a[7][5])
        if (isValidPlacement(a, a[7][5], 7, 5))
        for (a[7][6] = 1; a[7][6] <= 9; ++a[7][6])
        if (isValidPlacement(a, a[7][6], 7, 6))
        for (a[7][7] = 1; a[7][7] <= 9; ++a[7][7])
        if (isValidPlacement(a, a[7][7], 7, 7))
        for (a[7][8] = 1; a[7][8] <= 9; ++a[7][8])
        if (isValidPlacement(a, a[7][8], 7, 8))
        for (a[8][0] = 1; a[8][0] <= 9; ++a[8][0])
        if (isValidPlacement(a, a[8][0], 8, 0))
        for (a[8][1] = 1; a[8][1] <= 9; ++a[8][1])
        if (isValidPlacement(a, a[8][1], 8, 1))
        for (a[8][3] = 1; a[8][3] <= 9; ++a[8][3])
        if (isValidPlacement(a, a[8][3], 8, 3))
        for (a[8][5] = 1; a[8][5] <= 9; ++a[8][5])
        if (isValidPlacement(a, a[8][5], 8, 5))
        for (a[8][7] = 1; a[8][7] <= 9; ++a[8][7])
        if (isValidPlacement(a, a[8][7], 8, 7))
        for (int[] row : a)
        System.out.println(Arrays.toString(row));
    }
}
