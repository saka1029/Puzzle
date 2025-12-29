package test.puzzle.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class TestNumberLinkNanoPico {

    static class NumLink {
        record EndPointsType(int sx, int sy, int tx, int ty) {}
        final int[][] board;
        final EndPointsType[] endPoints;

        NumLink(int[][] board) {
            this.board = Stream.of(board).map(row -> row.clone()).toArray(int[][]::new);
            Map<Integer, EndPointsType> map = new HashMap<>();
            for (int x = 0; x < board.length; ++x)
                for (int y = 0; y < board[x].length; ++y) {
                    int elm = board[x][y];
                    if (elm == 0) continue;
                    EndPointsType ep = map.get(elm);
                    map.put(elm, ep == null 
                        ? new EndPointsType(x, y, -1, -1)
                        : new EndPointsType(ep.sx, ep.sy, x, y));
                }
            this.endPoints = new EndPointsType[map.size() + 1];
            for (Entry<Integer, EndPointsType> e : map.entrySet())
                this.endPoints[e.getKey()] = e.getValue();
        }

        int dist(int x1, int y1, int x2, int y2) {
            return Math.abs(x1 - x2) + Math.abs(y1 - y2);
        }

        void tryit(int elm, int x, int y) {
            if (board[x][y] == 0) {
                board[x][y] = elm;
                solve(elm, x, y);
                board[x][y] = 0;
            }
        }

        void found() {

        }

        void solve(int elm, int fx, int fy) {
            if (elm >= endPoints.length)
                found();
            else if (dist(fx, fy, endPoints[elm].tx, endPoints[elm].ty) == 1)
                solve(elm + 1, endPoints[elm + 1].sx, endPoints[elm + 1].sy);
            else {
                tryit(elm, fx + 1, fy);
                tryit(elm, fx, fy + 1);
                tryit(elm, fx - 1, fy);
                tryit(elm, fx, fy - 1);
            }
        }

        static void solve(int[][] board) {
            NumLink obj = new NumLink(board);
            obj.solve(1, obj.endPoints[1].sx, obj.endPoints[1].sy);
        }

    }

}
