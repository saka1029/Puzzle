package puzzle.core;

/**
 * 手番は1,2で表現する。 局面(board)は整数で表現する。 局面は空き(0),手番1(1)、手番2(2)の9個の組み合わせで表現する。
 * 局面の種類は3^9 = 19683通りある。 short[3^9][2][9] ALLはすべての局面とその可能な次の手を持つ。
 * ALL[n]は局面nにおける次の手を表す。
 * 
 */
public class TicTacToe {

    private TicTacToe() {
    }

    public static final int MAX_BOARD = 19683; // exclusive
    public static final int SIZE = 9;
    public static final int RADIX = 3, ROWS = 3, COLS = 3;
    public static final int[] POW3 = {1, 3, 9, 27, 81, 243, 729, 2187, 6561};
    public static final int[][] PATHS = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // horizontal
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // vertical
        {0, 4, 8}, {2, 4, 6} // diagonal
    };

    /**
     * 9個の要素の並びから局面を求めます。
     * 
     * @param elements 9個の要素を指定します。各値は(0, 1, 2)
     * @return 局面を返します。(0, 1, .. , MAX_BOARD-1)
     */
    public static short board(int... elements) {
        short board = 0;
        for (int e : elements)
            board = (short) (board * RADIX + e);
        return board;
    }

    /**
     * 局面からその1次元配列表現を求めます。
     * 
     * @param board 局面を指定します。(0, 1, .. , MAX_BOARD-1)
     * @return 局面の1次元配列を返します。
     */
    public static int[] array(int board) {
        int[] result = new int[SIZE];
        for (int i = SIZE - 1; i >= 0 && board > 0; --i, board /= RADIX)
            result[i] = board % RADIX;
        return result;
    }

    /**
     * 
     * @param x 行を指定します。
     * @param y 列を指定します。
     * @return 一次元座標としてのインデックスを返します。
     */
    public static int index(int x, int y) {
        return x * COLS + y;
    }

    /**
     * @param board 局面(0, 1, .. , MAX_BOARD-1)
     * @param index 位置(0, 1, .. ,7)
     * @return 局面における位置の値を返します。(0, 1, 2)
     */
    public static int get(int board, int index) {
        return board / POW3[index] % RADIX;
    }

    /**
     * @param board 局面(0, 1, .. , MAX_BOARD-1)
     * @param index 位置(0, 1, .. ,7)
     * @param value セットする値(0, 1, 2)
     * @return boardのインデクス位置にvalueをセットした局面を返します。(0, 1, .. , MAX_BOARD-1)
     *         引数boardを更新することはできないので、結果を値として返す点に注意します。
     */
    public static int set(int board, int index, int value) {
        int rank = POW3[index], rank3 = rank * RADIX;
        return board / rank3 * rank3 + value * rank + board % rank;
    }

    /**
     * 次の手番を返します。 手番1の着手数をm、手番2の着手数をnとしたとき、 m - n = 1の時2を返します。 m - n = -1の時1を返します。 m
     * - n = 0の時0を返します。 上記以外の場合は-1を返します。
     * 
     * @param board
     * @return 手番1の場合1、手番2の場合2、どちらの手番もありうる場合0、 次の手番があり得ない場合-1を返します。
     *         手番1と2の着手数の差の絶対値が1より大きい場合は次の手番はあり得ません。
     */
    public static int next(int board) {
        int one = 0, two = 0;
        for (int i = 0; i < SIZE && board > 0; ++i, board /= RADIX)
            switch (board % RADIX) {
                case 1: ++one; break;
                case 2: ++two; break;
            }
        return switch (one - two) {
            case 1 -> 2;
            case -1 -> 1;
            case 0 -> 0;
            default -> -1;
        };
    }

    /**
     * 
     * @param board
     * @return 手番1が勝っている場合は1、
     *         手番2が勝っている場合は2、
     *         未決着の場合は0
     *         ありえない局面の場合は-1を返します。
     */
    public static int winner(int board) {
        int[] array = array(board);
        for (int[] path : PATHS) {
            
        }

    }

}
