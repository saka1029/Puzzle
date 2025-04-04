package test.puzzle.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class TestSudoku {

    static void method() {
//        System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
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
        method();
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
        int[][] expected = {
            {7, 3, 2, 4, 5, 8, 6, 1, 9},
            {9, 5, 6, 1, 7, 3, 8, 2, 4},
            {1, 8, 4, 6, 2, 9, 5, 3, 7},
            {8, 7, 1, 5, 6, 4, 3, 9, 2},
            {6, 4, 3, 8, 9, 2, 7, 5, 1},
            {2, 9, 5, 3, 1, 7, 4, 6, 8},
            {3, 2, 9, 7, 8, 6, 1, 4, 5},
            {4, 1, 8, 2, 3, 5, 9, 7, 6},
            {5, 6, 7, 9, 4, 1, 2, 8, 3},
        };
        assertTrue(solve(board));
        assertArrayEquals(expected, board);
    }

    /**
     * YouTubeのソルバーを効率化したもの。
     * 未確定のセルを探すロジックを改善した。
     */
    static List<int[][]> solveSequential(int[][] a) {
        int size = 9;
        List<int[][]> result = new ArrayList<>();
        new Object() {
            void answer() {
                result.add(Stream.of(a)
                    .map(x -> Arrays.copyOf(x, x.length))
                    .toArray(int[][]::new));
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
        return result;
    }

    @Test
    public void testSolveSequential() {
        method();
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
        int[][] expected = {
            {7, 3, 2, 4, 5, 8, 6, 1, 9},
            {9, 5, 6, 1, 7, 3, 8, 2, 4},
            {1, 8, 4, 6, 2, 9, 5, 3, 7},
            {8, 7, 1, 5, 6, 4, 3, 9, 2},
            {6, 4, 3, 8, 9, 2, 7, 5, 1},
            {2, 9, 5, 3, 1, 7, 4, 6, 8},
            {3, 2, 9, 7, 8, 6, 1, 4, 5},
            {4, 1, 8, 2, 3, 5, 9, 7, 6},
            {5, 6, 7, 9, 4, 1, 2, 8, 3},
        };
        List<int[][]> result = solveSequential(board);
        assertEquals(1, result.size());
        assertArrayEquals(expected, result.get(0));
    }

    /**
     * intによる集合を使うことで、さらに効率化したもの。
     */
    static List<int[][]> solveBitmap(int[][] a) {
        List<int[][]> result = new ArrayList<>();
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
                result.add(Stream.of(a)
                    .map(x -> Arrays.copyOf(x, x.length))
                    .toArray(int[][]::new));
            }
            
            int b(int r, int c) {
                return r - r % 3 + c / 3;
            }

            void set(int n, int r, int c) {
                int bit = 1 << n;
                rowSet[r] |= bit;
                colSet[c] |= bit;
                boxSet[b(r, c)] |= bit;
                a[r][c] = n;
            }
            
            void unset(int n, int r, int c) {
                int bit = ~(1 << n);
                rowSet[r] &= bit;
                colSet[c] &= bit;
                boxSet[b(r, c)] &= bit;
                a[r][c] = 0;
            }

            boolean isSafe(int n, int r, int c) {
                int bit = 1 << n;
                return (rowSet[r] & bit) == 0
                    && (colSet[c] & bit) == 0
                    && (boxSet[b(r, c)] & bit) == 0;
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
        return result;
    }

    @Test
    public void testSolveBitmap() {
        method();
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
        int[][] expected = {
            {7, 3, 2, 4, 5, 8, 6, 1, 9},
            {9, 5, 6, 1, 7, 3, 8, 2, 4},
            {1, 8, 4, 6, 2, 9, 5, 3, 7},
            {8, 7, 1, 5, 6, 4, 3, 9, 2},
            {6, 4, 3, 8, 9, 2, 7, 5, 1},
            {2, 9, 5, 3, 1, 7, 4, 6, 8},
            {3, 2, 9, 7, 8, 6, 1, 4, 5},
            {4, 1, 8, 2, 3, 5, 9, 7, 6},
            {5, 6, 7, 9, 4, 1, 2, 8, 3},
        };
        List<int[][]> result = solveBitmap(board);
        assertEquals(1, result.size());
        assertArrayEquals(expected, result.get(0));
    }

    /**
     * bitmapを使って適用可能な番号のみを順次取り出すようにし、
     * 適用不能な番号のチェックを行わずに済むようにした。
     * つまり、for (int n = 1; n <= 9; ++n)のループを高速化した。
     * 終盤になると適用不能な番号が増えてくるので、
     * そのチェックをスキップできると高速化できる。
     * xxxSet[]は使用済み番号の集合の配列。
     * 各要素は番号nが使用済みのとき、2ⁿビットがONになっている。
     * nは1から9の数字なので、全ての番号が使用済みの時は0b111_111_111_0となる。
     */
    static List<int[][]> solveBitmapFast(int[][] a) {
        List<int[][]> result = new ArrayList<>();
        int size = 9, mask = 0b111_111_111_0;
        int[] rowSet = new int[size], colSet = new int[size], boxSet = new int[size];
        new Object() {
            {
                // 配列a初期化：既に確定している番号をbitmapにセットする。
                for (int r = 0; r < size; ++r)
                    for (int c = 0; c < size; ++c) {
                        int n = a[r][c];
                        if (n != 0)
                            set(r, c, 1 << n);
                    }
            }

            void set(int r, int c, int bit) {
                rowSet[r] |= bit;
                colSet[c] |= bit;
                boxSet[box(r, c)] |= bit;
                a[r][c] = Integer.numberOfTrailingZeros(bit);
            }

            void unset(int r, int c, int bit) {
                rowSet[r] ^= bit;
                colSet[c] ^= bit;
                boxSet[box(r, c)] ^= bit;
                a[r][c] = 0;
            }

            void answer() {
                result.add(Stream.of(a)
                    .map(x -> Arrays.copyOf(x, x.length))
                    .toArray(int[][]::new));
            }
            
            /**
             * r行c列が属するbox(3x3)のセル位置を求める。
             * <pre>
             * \ c 0 1 2 3 4 5 6 7 8
             * r +------------------
             * 0 | 0 0 0 1 1 1 2 2 2 
             * 1 | 0 0 0 1 1 1 2 2 2 
             * 2 | 0 0 0 1 1 1 2 2 2 
             * 3 | 3 3 3 4 4 4 5 5 5 
             * 4 | 3 3 3 4 4 4 5 5 5 
             * 5 | 3 3 3 4 4 4 5 5 5 
             * 6 | 6 6 6 7 7 7 8 8 8 
             * 7 | 6 6 6 7 7 7 8 8 8 
             * 8 | 6 6 6 7 7 7 8 8 8 
             * </pre>
             */
            int box(int r, int c) {
                return r - r % 3 + c / 3;
            }

            void solve(int i) {
                int r = i / size, c = i % size, b = box(r, c);
                if (r >= size)
                    answer();
                else if (a[r][c] != 0)
                    solve(i + 1); // 既に番号が付与されている場合は次へ
                else
                    // r行c列で配置可能な番号について配置を試みる。
                    // vは適用可能な番号のbit値、v ^= bitは処理済のbitをvから除外する。
                    for (int v = mask & ~(rowSet[r] | colSet[c] | boxSet[b]), bit = 0; v != 0; v ^= bit) {
                        // 適用可能な番号のbitmapから右端(最小)のビットを取り出す。
                        bit = Integer.lowestOneBit(v); // or -v & v
                        set(r, c, bit);     // 配置する。
                        solve(i + 1);       // 次へ進む。
                        unset(r, c, bit);   // もとに戻す。
                    }
            }
        }.solve(0);
        return result;
    }

    @Test
    public void testSolveBitmapFast() {
        method();
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
        int[][] expected = {
            {7, 3, 2, 4, 5, 8, 6, 1, 9},
            {9, 5, 6, 1, 7, 3, 8, 2, 4},
            {1, 8, 4, 6, 2, 9, 5, 3, 7},
            {8, 7, 1, 5, 6, 4, 3, 9, 2},
            {6, 4, 3, 8, 9, 2, 7, 5, 1},
            {2, 9, 5, 3, 1, 7, 4, 6, 8},
            {3, 2, 9, 7, 8, 6, 1, 4, 5},
            {4, 1, 8, 2, 3, 5, 9, 7, 6},
            {5, 6, 7, 9, 4, 1, 2, 8, 3},
        };
        List<int[][]> result = solveBitmapFast(board);
        assertEquals(1, result.size());
        assertArrayEquals(expected, result.get(0));
    }

//    @Ignore
    @Test
    public void testSolveBitmapFast17() {
        method();
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
        int[][] expected = {
            {2, 3, 7, 8, 4, 1, 5, 6, 9},
            {1, 8, 6, 7, 9, 5, 2, 4, 3},
            {5, 9, 4, 3, 2, 6, 7, 1, 8},
            {3, 1, 5, 6, 7, 4, 8, 9, 2},
            {4, 6, 9, 5, 8, 2, 1, 3, 7},
            {7, 2, 8, 1, 3, 9, 4, 5, 6},
            {6, 4, 2, 9, 1, 8, 3, 7, 5},
            {8, 5, 3, 4, 6, 7, 9, 2, 1},
            {9, 7, 1, 2, 5, 3, 6, 8, 4},
        };
        List<int[][]> result = solveBitmapFast(board);
        assertEquals(1, result.size());
        assertArrayEquals(expected, result.get(0));
    }

//    @Ignore
    @Test
    public void testSolveBitmapFast17_2() {
        method();
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
        int[][] expected = {
            {6, 9, 3, 7, 8, 4, 5, 1, 2},
            {4, 8, 7, 5, 1, 2, 9, 3, 6},
            {1, 2, 5, 9, 6, 3, 8, 7, 4},
            {9, 3, 2, 6, 5, 1, 4, 8, 7},
            {5, 6, 8, 2, 4, 7, 3, 9, 1},
            {7, 4, 1, 3, 9, 8, 6, 2, 5},
            {3, 1, 9, 4, 7, 5, 2, 6, 8},
            {8, 5, 6, 1, 2, 9, 7, 4, 3},
            {2, 7, 4, 8, 3, 6, 1, 5, 9},
        };
        List<int[][]> result = solveBitmapFast(board);
        assertEquals(1, result.size());
        assertArrayEquals(expected, result.get(0));
    }

//    @Ignore
    @Test
    public void testSolveBitmapFast17_3() {
        method();
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
        int[][] expected = {
            {6, 7, 2, 9, 8, 3, 4, 5, 1},
            {9, 5, 1, 4, 7, 6, 8, 2, 3},
            {3, 8, 4, 2, 1, 5, 9, 7, 6},
            {4, 6, 8, 1, 5, 9, 2, 3, 7},
            {2, 9, 5, 7, 3, 8, 6, 1, 4},
            {1, 3, 7, 6, 4, 2, 5, 8, 9},
            {8, 4, 3, 5, 6, 7, 1, 9, 2},
            {7, 1, 9, 8, 2, 4, 3, 6, 5},
            {5, 2, 6, 3, 9, 1, 7, 4, 8},
        };
        List<int[][]> result = solveBitmapFast(board);
        assertEquals(1, result.size());
        assertArrayEquals(expected, result.get(0));
    }
}