package puzzle.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
public class Sudoku {

    static final int SIZE = 9, MASK = 0b111_111_111_0;
    final int[][] a;
    final int[] rowSet = new int[SIZE], colSet = new int[SIZE], boxSet = new int[SIZE];
    final List<int[][]> result = new ArrayList<>();

    private Sudoku(int[][] a) {
        this.a = a;
        // 配列a初期化：既に確定している番号をbitmapにセットする。
        for (int r = 0; r < SIZE; ++r)
            for (int c = 0; c < SIZE; ++c) {
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
        int r = i / SIZE, c = i % SIZE, b = box(r, c);
        if (r >= SIZE)
            answer();
        else if (a[r][c] != 0)
            solve(i + 1); // 既に番号が付与されている場合は次へ
        else
            // r行c列で配置可能な番号について配置を試みる。
            // v は適用可能な番号の集合。
            // e は最小の要素(bit)。
            // e = -v & v は適用可能な番号のbitmapから右端(最小)のビットを取り出す。
            // v ^= e は処理済のbitをvから除外する。
            for (int v = MASK & ~(rowSet[r] | colSet[c] | boxSet[b]), e = 0; (e = -v & v) != 0; v ^= e) {
                set(r, c, e);     // 配置する。
                solve(i + 1);       // 次へ進む。
                unset(r, c, e);   // もとに戻す。
            }
    }

    public static List<int[][]> solve(int[][] a) {
        Sudoku sudoku = new Sudoku(a);
        sudoku.solve(0);
        return sudoku.result;
    }
}