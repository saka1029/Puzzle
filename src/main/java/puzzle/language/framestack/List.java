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

    /**
     * 値を返す場合はスタックトップにReturn(n)をプッシュする。
     * フレームポインタを回復後、
     * スタックトップにあったn個の要素を戻り値としてpush()する。
     */
    @Override
    public void execute(Context context) {
        context.fpush(context.sp);
        for (Executable e : this)
            e.execute(context);
        if (context.peek(0) instanceof Return r) {
            context.pop(); // remove Return
            int end = context.sp, start = end - r.n;
            context.sp = context.fpop();
            for (int i = start; i < end; ++i)
                context.push(context.stack[i]); // push return values
        } else
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