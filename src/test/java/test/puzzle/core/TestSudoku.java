package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TestSudoku {

    static String method() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * YouTubeのソルバーをネストしたメソッドで実装しなおしたもの。
     * Java で数独ソルバーを 20 分で作成する - 完全なチュートリアル - YouTube
     * https://www.youtube.com/watch?v=mcXc8Mva2bA&t=1061s
     */
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
    public void testSolve() {
        System.out.println(method());
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
        System.out.println(method());
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
    
    /**
     * YouTubeのソルバーを効率化したもの。
     * 未確定のセルを探すロジックを改善した。
     */
    static void solveSequential(int[][] a) {
        int size = 9;
        new Object() {
            void answer() {
                for (int[] x : a)
                    System.out.println(Arrays.toString(x));
            }
            
            boolean isSafe(int n, int r, int c) {
                for (int i = 0; i < size; ++i)
                    if (a[r][i] == n || a[i][c] == n)
                        return false;
                int x = r - r % 3, mx = x + 3, y = c - c %3, my = y + 3;
                for (int i = x; i < mx; ++i)
                    for (int j = y; j < my; ++j)
                        if (a[i][j] == n)
                            return false;
                return true;
            }

            void solve(int i) {
                int r = i / size, c = i % size;
                if (r >= size)
                    answer();
                else if (a[r][c] != 0)
                    solve(i + 1);
                else
                    for (int n = 1; n <= size; ++n)
                        if (isSafe(n, r, c)) {
                            a[r][c] = n;
                            solve(i + 1);
                            a[r][c] = 0;
                        }
            }
        }.solve(0);
    }

    @Test
    public void testSolveSequential() {
        System.out.println(method());
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
        solveSequential(board);
    }

    /**
     * intによる集合を使うことで、さらに効率化したもの。
     */
    static void solveBitmap(int[][] a) {
        new Object() {
            int size = 9;
            int[] rowSet = new int[size], colSet = new int[size], boxSet = new int[size];
            {
                for (int r = 0; r < size; ++r)
                    for (int c = 0; c < size; ++c) {
                        int n = a[r][c];
                        if (n != 0)
                            set(n, r, c);
                    }
            }

            void answer() {
                for (int[] x : a)
                    System.out.println(Arrays.toString(x));
            }
            
            int boxIndex(int r, int c) {
                return r - r % 3 + c / 3;
            }

            void set(int n, int r, int c) {
                int bit = 1 << n;
                rowSet[r] |= bit;
                colSet[c] |= bit;
                boxSet[boxIndex(r, c)] |= bit;
                a[r][c] = n;
            }
            
            void unset(int n, int r, int c) {
                int bit = ~(1 << n);
                rowSet[r] &= bit;
                colSet[c] &= bit;
                boxSet[boxIndex(r, c)] &= bit;
                a[r][c] = 0;
            }

            boolean isSafe(int n, int r, int c) {
                int bit = 1 << n;
                return (rowSet[r] & bit) == 0
                    && (colSet[c] & bit) == 0
                    && (boxSet[boxIndex(r, c)] & bit) == 0;
            }

            void solve(int i) {
                int r = i / size, c = i % size;
                if (r >= size)
                    answer();
                else if (a[r][c] != 0)
                    solve(i + 1);
                else
                    for (int n = 1; n <= size; ++n)
                        if (isSafe(n, r, c)) {
                            set(n, r, c);
                            solve(i + 1);
                            unset(n, r, c);
                        }
            }
        }.solve(0);
    }

    @Test
    public void testSolveBitmap() {
        System.out.println(method());
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
        solveBitmap(board);
    }

    @Test
    public void testSolveBitmap17() {
        System.out.println(method());
        int[][] board = {
            {0, 0, 0, 8, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 4, 3},
            {5, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 7, 0, 8, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 2, 0, 0, 3, 0, 0, 0, 0},
            {6, 0, 0, 0, 0, 0, 0, 7, 5},
            {0, 0, 3, 4, 0, 0, 0, 0, 0},
            {0, 0, 0, 2, 0, 0, 6, 0, 0},
        };
        solveBitmap(board);
    }

    @Test
    public void testSolveBitmap17_2() {
        System.out.println(method());
        int[][] board = {
            {0, 0, 0, 0, 0, 0, 0, 1, 0},
            {4, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 2, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 5, 0, 4, 0, 7},
            {0, 0, 8, 0, 0, 0, 3, 0, 0},
            {0, 0, 1, 0, 9, 0, 0, 0, 0},
            {3, 0, 0, 4, 0, 0, 2, 0, 0},
            {0, 5, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 8, 0, 6, 0, 0, 0},
        };
        solveBitmap(board);
    }

    @Test
    public void testSolveBitmap17_3() {
        System.out.println(method());
        int[][] board = {
            {0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 2, 3},
            {0, 0, 4, 0, 0, 5, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 3, 0, 6, 0, 0},
            {0, 0, 7, 0, 0, 0, 5, 8, 0},
            {0, 0, 0, 0, 6, 7, 0, 0, 0},
            {0, 1, 0, 0, 0, 4, 0, 0, 0},
            {5, 2, 0, 0, 0, 0, 0, 0, 0},
        };
        solveBitmap(board);
    }
}