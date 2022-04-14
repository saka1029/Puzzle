package puzzle.core;

public class Matrix {
    
    private Matrix() {
    }

    static int[][] rotate90(int[][] m) {
        int rows = m.length, cols = m[0].length;
        int[][] n = new int[cols][rows];
        for (int r = 0, cc = rows - 1; r < rows; ++r, --cc)
            for (int c = 0, rr = 0; c < cols; ++c, ++rr)
                n[rr][cc] = m[r][c];
        return n;
    }
    
    static int[][] rotate180(int[][] m) {
        int rows = m.length, cols = m[0].length;
        int[][] n = new int[rows][cols];
        for (int r = 0, rr = rows - 1; r < rows; ++r, --rr)
            for (int c = 0, cc = cols - 1; c < cols; ++c, --cc)
                n[rr][cc] = m[r][c];
        return n;
    }
    
    static int[][] rotate270(int[][] m) {
        int rows = m.length, cols = m[0].length;
        int[][] n = new int[cols][rows];
        for (int r = 0, cc = 0; r < rows; ++r, ++cc)
            for (int c = 0, rr = cols - 1; c < cols; ++c, --rr)
                n[rr][cc] = m[r][c];
        return n;
    }
}
