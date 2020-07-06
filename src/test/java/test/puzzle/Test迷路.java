package test.puzzle;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class Test迷路 {

    @Test
    void test() {
        int HEIGHT = 10, WIDTH = 10;
        char START = 's', FREE = '.', WALL = '#', GOAL = 'g';
        String[] MAZE_STRING = {
            "s.........",
            "#########.",
            "#.......#.",
            "#..####.#.",
            "##....#.#.",
            "#####.#.#.",
            "g.#.#.#.#.",
            "#.#.#.#.#.",
            "#.#.#.#.#.",
            "#.....#...",
        };
        char[][] MAZE = new char[HEIGHT][WIDTH];
        int sr = 0, sc = 0;
        int gr = 0, gc = 0;
        for (int r = 0; r < HEIGHT; ++r)
            for (int c = 0; c < WIDTH; ++c) {
                char ch = MAZE_STRING[r].charAt(c);
                MAZE[r][c] = ch;
                if (ch == START) { sr = r; sc = c; }
                if (ch == GOAL) { gr = r; gc = c; }
            }
        for (char[] row : MAZE)
            System.out.println(Arrays.toString(row));
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int dirSize = dr.length;
        boolean[][] seen = new boolean[HEIGHT][WIDTH];
        new Object() {
            void visit(int r, int c) {
                seen[r][c] = true;
                for (int i = 0; i < dirSize; ++i) {
                    int nr = r + dr[i], nc = c + dc[i];
                    if (nr < 0 || nr >= HEIGHT || nc < 0 || nc >= HEIGHT) continue;
                    if (seen[nr][nc]) continue;
                    if (MAZE[nr][nc] == WALL) continue;
                    visit(nr, nc);
                }
            }
        }.visit(sr, sc);
        for (boolean[] row : seen) {
            StringBuilder sb = new StringBuilder();
            for (boolean b : row)
                sb.append(b ? "*" : " ");
            System.out.println(sb.toString());
        }
        System.out.println("goal: " + (seen[gr][gc] ? "YES" : "NO"));
    }

}
