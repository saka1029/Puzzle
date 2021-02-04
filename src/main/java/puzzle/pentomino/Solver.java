package puzzle.pentomino;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Solver {

    public static void solve(Board board, List<Set<Mino>> minos, Predicate<Board> answer, Consumer<Board> set) {
        int size = minos.size();
        Set<Integer> used = new HashSet<>();
        new Object() {
            boolean cont = true;
            void solve(int index, Point start) {
                if (!cont)
                    return;
                if (index >= size) {
                    cont = answer.test(board);
                    return;
                }
                L: for (int i = 0; i < size; ++i) {
                    if (used.contains(i)) continue;
                    used.add(i);
                    for (Mino mino : minos.get(i)) {
                        if (!cont) break L;
                        if (!board.placeable(start, mino)) continue;
                        board.set(start, mino, i);
                        if (set != null) set.accept(board);
                        solve(index + 1, board.next(start));
                        board.unset(start, mino);
                        if (set != null) set.accept(board);
                    }
                    used.remove(i);
                }
            }
        }.solve(0, board.next(Point.of(0, 0)));
    }

}
