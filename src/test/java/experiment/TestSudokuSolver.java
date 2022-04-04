package experiment;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * Create a Sudoku Solver In Java In 20 Minutes - Full Tutorial - YouTube
 * https://www.youtube.com/watch?v=mcXc8Mva2bA
 */
class TestSudokuSolver {

    @Test
    void test() {
        int[][] board = {
                { 7, 0, 2, 0, 5, 0, 6, 0, 0 },
                { 0, 0, 0, 0, 0, 3, 0, 0, 0 },
                { 1, 0, 0, 0, 0, 9, 5, 0, 0 },
                { 8, 0, 0, 0, 0, 0, 0, 9, 0 },
                { 0, 4, 3, 0, 0, 0, 7, 5, 0 },
                { 0, 9, 0, 0, 0, 0, 0, 0, 8 },
                { 0, 0, 9, 7, 0, 0, 0, 0, 5 },
                { 0, 0, 0, 2, 0, 0, 0, 0, 0 },
                { 0, 0, 7, 0, 4, 0, 2, 0, 3 }
        };
        System.out.println("solved = " + new Solver(board).solveBoard());
        for (int[] row : board)
        	System.out.println(Arrays.toString(row));
    }
    
    static class Solver {
    	static final int GRID_SIZE = 9;

    	final int[][] board;

    	Solver(int[][] board) {
    		this.board = board;
    	}
    	
    	boolean isNumberInRow(int number, int row) {
    		return Arrays.stream(board[row]).anyMatch(n -> number == n);
    	}
    	
    	boolean isNumberInColumn(int number, int column) {
    		return Arrays.stream(board).anyMatch(row -> number == row[column]);
    	}
    	
    	boolean isNumberInBox(int number, int row, int column) {
    		int localBoxRow = row - row % 3;
    		int localBoxColumn = column - column % 3;
    		return IntStream.range(localBoxRow, localBoxRow + 3)
    			.anyMatch(i -> IntStream.range(localBoxColumn, localBoxColumn + 3)
    				.anyMatch(j -> board[i][j] == number));
    	}
    	
    	boolean isValidPlacement(int number, int row, int column) {
    		return !isNumberInRow(number, row)
    			&& !isNumberInColumn(number, column)
    			&& !isNumberInBox(number, row, column);
    	}
    	
    	boolean solveBoard() {
    		for (int row = 0; row < GRID_SIZE; ++row)
    			for (int column = 0; column < GRID_SIZE; ++column)
    				if (board[row][column] == 0) {
    					for (int numberToTry = 1; numberToTry <= GRID_SIZE; ++numberToTry) {
    						if (isValidPlacement(numberToTry, row, column)) {
    							board[row][column] = numberToTry;
    							if (solveBoard())
    								return true;
                                board[row][column] = 0;
    						}
    					}
    					return false;
    				}
    		return true;
    	}
    }
}
