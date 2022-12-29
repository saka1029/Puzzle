package test.puzzle.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.Nonogram;

public class TestNonogram {

    @Test
    public void test() {
        int[][] rows = {{5}, {5}, {3}, {3, 2}, {4, 1}, {1, 3, 1}, {1, 1, 3, 1}, {5}, {2, 4}, {1, 4}};
        int[][] cols = {{2, 2}, {4, 1}, {2}, {1, 1}, {1}, {2, 4}, {2, 5}, {3, 4}, {4, 3}, {4, 5}};
        Nonogram.solve(rows, cols);
    }

}
