package puzzle.language.framestack;

import java.util.stream.Collectors;

public class Block extends List {
    
    public final int args, returns;
    
    Block(int args, int returns, Executable... elements) {
        super(elements);
        this.args = args;
        this.returns = returns;
    }
    
    /**
     * args個の要素を除いてスタックポインタを回復後、
     * スタックトップにあったreturns個の要素を戻り値としてpush()する。
     */
    @Override
    public void execute(Context context) {
        context.fpush(context.sp);  // save frame
        for (Executable e : this)
            e.execute(context);
        int end = context.sp, start = end - returns; // 戻り値の先頭と末尾
        context.sp = context.fpop() - args; // restore frame (unwind)
        for (int i = start; i < end; ++i)
            context.push(context.stack[i]); // push return values
    }

    @Override
    public String toString() {
        String head = "(%d %d : ".formatted(args, returns);
        return elements.stream()
            .map(Executable::toString)
            .collect(Collectors.joining(" ", head, ")"));
    }
}
