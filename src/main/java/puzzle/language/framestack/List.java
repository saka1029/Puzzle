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
    
    public static List of(Executable... elements) {
        return new List(elements);
    }
    
    public static List of(int args, int returns, Executable... elements) {
        return new Block(args, returns, elements);
    }
    
    @Override
    public void execute(Context context) {
        for (Executable e : this)
            e.execute(context);
    }

    @Override
    public Iterator<Executable> iterator() {
        return elements.iterator();
    }

    @Override
    public String toString() {
        return elements.stream()
            .map(Executable::toString)
            .collect(Collectors.joining(" ", "(", ")"));
    }
}
