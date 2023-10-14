package puzzle.language.framestack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class List implements Executable, Iterable<Executable> {

    final ArrayList<Executable> elements = new ArrayList<>();
    
    List(Executable... elements) {
        for (Executable e : elements)
            this.elements.add(e);
    }

    /**
     * TODO: 値を返すブロックの場合はどうする？
     */
    @Override
    public void execute(Context context) {
        context.fpush(context.sp);
        for (Executable e : this)
            e.execute(context);
        context.sp = context.fpop();
    }

    @Override
    public Iterator<Executable> iterator() {
        return elements.iterator();
    }

    @Override
    public String toString() {
        return elements.stream()
            .map(Executable::toString)
            .collect(Collectors.joining(", ", "(", ")"));
    }
}
