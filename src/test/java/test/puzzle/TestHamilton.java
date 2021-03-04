package test.puzzle;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class TestHamilton {

    static final Logger logger = Logger.getLogger(TestHamilton.class.getName());

    static class Node {
        final String name;
        final Set<Node> links = new LinkedHashSet<>();
        final Set<Node> originalLinks = new LinkedHashSet<>();
        void link(Node... nodes) {
            for (Node node : nodes) {
                links.add(node);
                originalLinks.add(node);
            }
        }

        Node(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static void search(int size, Node start, boolean closed, Consumer<LinkedHashSet<Node>> found) {
        LinkedHashSet<Node> visited = new LinkedHashSet<>();
        new Object() {

            void search(Node node, int no) {
                assert !visited.contains(node) : "already visited " + node;
                visited.add(node);
                if (visited.size() >= size) {
                    if (!closed || node.originalLinks.contains(start))
                        found.accept(visited);
                } else if (node.links.size() > 0) {
                    Set<Node> backup = new HashSet<>();
//                    String t = Arrays.toString(node.links.stream().mapToInt(n -> n.links.size()).toArray());
                    for (Node n : node.links)
                        if (n.links.remove(node))
                            backup.add(n);
//                    logger.info(no + " " + t + " -> " + Arrays.toString(node.links.stream().mapToInt(n -> n.links.size()).toArray()));
                    for (Node next : node.links)
                        search(next, no + 1);
                    for (Node n : backup)
                        n.links.add(node);
                }
                visited.remove(node);
            }
        }.search(start, 0);
    }

    @Test
    void testNode2() {
        Node a = new Node("A");
        Node b = new Node("B");
        Node[] nodes = {a, b};
        a.link(b);
        b.link(a);
        Set<List<Node>> result = new LinkedHashSet<>();
        search(nodes.length, a, true, path -> result.add(new ArrayList<>(path)));
        assertEquals(Set.of(List.of(a, b)), result);
    }

    @Test
    void testNode3() {
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node[] nodes = {a, b, c};
        a.link(b, c);
        b.link(a, c);
        c.link(a, b);
        Set<List<Node>> result = new LinkedHashSet<>();
        search(nodes.length, a, true, path -> result.add(new ArrayList<>(path)));
        assertEquals(Set.of(
            List.of(a, b, c),
            List.of(a, c, b)), result);
    }

    @Test
    void testNode4() {
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node[] nodes = {a, b, c, d};
        a.link(b, c, d);
        b.link(a, c, d);
        c.link(a, b, d);
        d.link(a, b, c);
        Set<List<Node>> result = new LinkedHashSet<>();
        search(nodes.length, a, true, path -> result.add(new ArrayList<>(path)));
        assertEquals(Set.of(
            List.of(a, b, c, d),
            List.of(a, b, d, c),
            List.of(a, c, b, d),
            List.of(a, c, d, b),
            List.of(a, d, b, c),
            List.of(a, d, c, b)), result);
    }

    static final int[] moveRows = { -2, -1, 1, 2, 2, 1, -1, -2 };
    static final int[] moveCols = { 1, 2, 2, 1, -1, -2, -2, -1 };

    static Node[][] knightBoard(int height, int width) {
        Node[][] board = new Node[height][width];
        int moveMax = moveRows.length;
        for (int r = 0; r < height; ++r)
            for (int c = 0; c < width; ++c)
                board[r][c] = new Node(r + "@" + c);
        for (int r = 0; r < height; ++r)
            for (int c = 0; c < width; ++c)
                for (int i = 0; i < moveMax; ++i) {
                    int nr = r + moveRows[i];
                    int nc = c + moveCols[i];
                    if (nr < 0 || nr >= height || nc < 0 || nc >= width)
                        continue;
                    board[r][c].link(board[nr][nc]);
                }
        return board;
    }

    @Test
    public void testKnightBoardOpend() {
        int N = 5;
        Node[][] board = knightBoard(N, N);
        int[] count = {0};
        for (int r = 0; r < N; ++r)
            for (int c = 0; c < N; ++c)
                search(N * N, board[r][c], false, path -> ++count[0]);
        System.out.println(count[0]);
    }

    static void print(int height, int width, int seq, LinkedHashSet<Node> path) {
        int[][] order = new int[height][width];
        int i = 0;
        for (Node n : path) {
            String[] rc = n.name.split("@");
            int row = Integer.parseInt(rc[0]);
            int col = Integer.parseInt(rc[1]);
            order[row][col] = i++;
        }
        logger.info("-- " + seq);
        StringBuilder sb = new StringBuilder();
        for (int[] row : order) {
            sb.setLength(0);
            for (int n : row)
                sb.append(String.format("%3d", n));
            logger.info(sb.toString());
        }
    }

//    @Test
    public void testKnightBoardClosed() {
        int N = 6;
        Node[][] board = knightBoard(N, N);
        assertEquals(2, board[0][0].links.size());
        assertEquals(8, board[2][2].links.size());
        assertEquals(2, board[N - 1][N - 1].links.size());
//        search(N * N, board[0][0], true, path -> logger.info("*** " + path));
        int[] count = {0};
        search(N * N, board[0][0], true, path -> print(N, N, count[0]++, path));
    }
}
