/**
 * YouTubeのソルバーをネストしたメソッドで実装しなおしたもの。
 * Java で数独ソルバーを 20 分で作成する - 完全なチュートリアル - YouTube
 * https://www.youtube.com/watch?v=mcXc8Mva2bA&t=1061s
 */
function printBoard(board) {
    console.log(board.map(row => row.join(" ")).join("\n"));
}

function sudoku(board) {
    const size = 9;
    function isNumberInRow(number, row) {
        for (let column = 0; column < size; ++column)
            if (board[row][column] == number)
                return true;
        return false;
    }

    function isNumberInColumn(number, column) {
        for (let row = 0; row < size; ++row)
            if (board[row][column] == number)
                return true;
        return false;
    }

    function isNumberInBox(number, row, column) {
        const boxRow = row - row % 3;
        const boxColumn = column - column % 3;
        for (let i = boxRow, maxRow = boxRow + 3; i < maxRow; ++i)
            for (let j = boxColumn, maxColumn = boxColumn + 3; j < maxColumn; ++j)
                if (board[i][j] == number)
                    return true;
        return false;
    }

    function isValidPlacement(number, row, column) {
        return !isNumberInRow(number, row)
            && !isNumberInColumn(number, column)
            && !isNumberInBox(number, row, column);
    }

    function solve() {
        for (let row = 0; row < size; ++row) {
            for (let column = 0; column < size; ++column) {
                if (board[row][column] == 0) {
                    for (let number = 1; number <= 9; ++number) {
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
    return solve();
}

function solveSudoku(board) {
    console.log("problem:");
    printBoard(board);
    const solved = sudoku(board);
    if (solved) {
        console.log("result:");
        printBoard(board);
    } else
        console.log("No solution exists.");
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
console.log("Sudoku Solver sudoku_simple.js");
solveSudoku(board);