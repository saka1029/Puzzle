/**
 * bitSetを使って適用可能な番号のみを順次取り出すようにし、
 * 適用不能な番号のチェックを行わずに済むようにした。
 * つまり、for (int n = 1; n <= 9; ++n)のループを高速化した。
 * 終盤になると適用不能な番号が増えてくるので、
 * そのチェックをスキップできると高速化できる。
 * xxxSet[]は使用済み番号の集合の配列。
 * 各要素は番号nが使用済みのとき、2ⁿビットがONになっている。
 * nは1から9の数字なので、全ての番号が使用済みの時は0b111_111_111_0となる。
 */
function solveBitmapFast(a) {
    const result = [];
    const size = 9, mask = 0b111_111_111_0;
    const rowSet = Array(size).fill(0);
    const colSet = Array(size).fill(0);
    const boxSet = Array(size).fill(0);
    // 配列a初期化：既に確定している番号をbitmapにセットする。
    for (let r = 0; r < size; ++r)
        for (let c = 0; c < size; ++c) {
            let n = a[r][c];
            if (n != 0)
                set(r, c, 1 << n);
        }
    // ある整数の2進数表現において、末尾から連続する0の個数を求める。
    function numberOfTrailingZeros(x) {
        if (x == 0) return(32);
        let n = 1;
        if ((x & 0x0000FFFF) == 0) {n = n +16; x = x >>16;}
        if ((x & 0x000000FF) == 0) {n = n + 8; x = x >> 8;}
        if ((x & 0x0000000F) == 0) {n = n + 4; x = x >> 4;}
        if ((x & 0x00000003) == 0) {n = n + 2; x = x >> 2;}
        return n - (x & 1);
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
    function box(r, c) {
        const result = r - r % 3 + Math.floor(c / 3);
        // console.log("box:" + r + "," + c + "->" + result);
        return result;
    }

    function set(r, c, bit) {
        rowSet[r] |= bit;
        colSet[c] |= bit;
        boxSet[box(r, c)] |= bit;
        a[r][c] = numberOfTrailingZeros(bit);
    }

    function unset(r, c, bit) {
        rowSet[r] ^= bit;
        colSet[c] ^= bit;
        boxSet[box(r, c)] ^= bit;
        a[r][c] = 0;
    }

    function answer() {
        result.push(a.concat());
    }

    function solve(i) {
        let r = Math.floor(i / size), c = i % size, b = box(r, c);
        if (r >= size)
            answer();
        else if (a[r][c] != 0)
            solve(i + 1); // 既に番号が付与されている場合は次へ
        else
            // r行c列で配置可能な番号について配置を試みる。
            // vは適用可能な番号のbit値、v ^= bitは処理済のbitをvから除外する。
            for (v = mask & ~(rowSet[r] | colSet[c] | boxSet[b]), bit = 0; v != 0; v ^= bit) {
                // 適用可能な番号のbitmapから右端(最小)のビットを取り出す。
                // bit = Integer.lowestOneBit(v); // or -v & v
                bit = -v & v
                set(r, c, bit);     // 配置する。
                solve(i + 1);       // 次へ進む。
                unset(r, c, bit);   // もとに戻す。
            }
    }
    solve(0);
    return result;
}

const board = [
    [7, 0, 2, 0, 5, 0, 6, 0, 0],
    [0, 0, 0, 0, 0, 3, 0, 0, 0],
    [1, 0, 0, 0, 0, 9, 5, 0, 0],
    [8, 0, 0, 0, 0, 0, 0, 9, 0],
    [0, 4, 3, 0, 0, 0, 7, 5, 0],
    [0, 9, 0, 0, 0, 0, 0, 0, 8],
    [0, 0, 9, 7, 0, 0, 0, 0, 5],
    [0, 0, 0, 2, 0, 0, 0, 0, 0],
    [0, 0, 7, 0, 4, 0, 2, 0, 3],
];

console.log("Sudoku Solver sudoku_bitset.js");
// for (let i = 121; i <= 999999999; i <<= 1)
//     console.log("numberOfTrailingZerosproblem:" + i.toString(2) + "=" + numberOfTrailingZeros(i));
const solutions = solveBitmapFast(board);
console.log("Number of solutions: " + solutions.length);
for (let index = 0; index < solutions.length; ++index) {
    console.log("Solution " + (index + 1) + ":");
    printBoard(solutions[index]);
}