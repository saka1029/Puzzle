package puzzle.pentomino;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class Pentomino {

    static final Logger logger = Logger.getLogger(Pentomino.class.toString());

    public static final char[] PIECE_NAMES =
        {'F', 'I', 'L', 'N', 'P', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static final Piece[][] PIECES = {
        /* F */ Piece.allOf(new int[][] {{0, 0}, {1, 0}, {1, 1}, {1, 2}, {2, 1}}),
        /* I */ Piece.allOf(new int[][] {{0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}}),
        /* L */ Piece.allOf(new int[][] {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {3, 1}}),
        /* N */ Piece.allOf(new int[][] {{0, 0}, {1, 0}, {1, 1}, {2, 1}, {3, 1}}),
        /* P */ Piece.allOf(new int[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1}, {2, 0}}),
        /* T */ Piece.allOf(new int[][] {{0, 0}, {0, 1}, {0, 2}, {1, 1}, {2, 1}}),
        /* U */ Piece.allOf(new int[][] {{0, 0}, {1, 0}, {1, 1}, {1, 2}, {0, 2}}),
        /* V */ Piece.allOf(new int[][] {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}}),
        /* W */ Piece.allOf(new int[][] {{0, 0}, {1, 0}, {1, 1}, {2, 1}, {2, 2}}),
        /* X */ Piece.allOf(new int[][] {{0, 0}, {1, -1}, {1, 0}, {1, 1}, {2, 0}}),
        /* Y */ Piece.allOf(new int[][] {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {3, 0}}),
        /* Z */ Piece.allOf(new int[][] {{0, 0}, {0, 1}, {1, 1}, {2, 1}, {2, 2}}),
    };

    public static void solve(char[][] board, Consumer<char[][]> found) {
        int height = board.length, width = board[0].length;
        int size = PIECES.length;
        char[][] b = new char[height][width];
        for (int r = 0; r < height; ++r)
            for (int c = 0; c < width; ++c) {
                char ch = board[height][width];
                b[r][c] = ch == 0 ? ' ' : ch;
            }
        boolean[] used = new boolean[size];
        new Object() {

            void solve(int index, int[] start) {
                if (index >= size)
                    found.accept(b);
                else
                    for (int i = 0; i < size; ++i) {
                        if (used[i]) continue;
                        used[i] = true;
                        int[] next = start;
                        solve(index + 1, next);
                        used[i] = false;
                    }
            }
        }.solve(0, new int[] {0, 0});
    }


}
