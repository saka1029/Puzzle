package puzzle.pentomino;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Solver {

    public static void solve(Board board, List<Set<Mino>> minos, Consumer<Board> answer) {
        int size = minos.size();
        Set<Integer> used = new HashSet<>();
        new Object() {
            void solve(int index, Point start) {
                if (index >= size) {
                    answer.accept(board);
                    return;
                }
                for (int i = 0; i < size; ++i) {
                    if (used.contains(i)) continue;
                    used.add(i);
                    for (Mino mino : minos.get(i)) {
                        if (!board.placeable(start, mino)) continue;
                        board.set(start, mino, i);
                        solve(index + 1, board.next(start));
                        board.unset(start, mino);
                    }
                    used.remove(i);
                }
            }
        }.solve(0, board.next(Point.of(0, 0)));
    }

}
