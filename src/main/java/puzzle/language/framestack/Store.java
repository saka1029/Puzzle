package puzzle.language.framestack;

public class Store implements Executable {
    
    public static final Store L0 = Store.of(0, 0);
    public static final Store L1 = Store.of(0, 1);
    public static final Store L2 = Store.of(0, 2);
    public static final Store L3 = Store.of(0, 3);

    public final int frameNo, index;
    
    Store(int frameNo, int index) {
        this.frameNo = frameNo;
        this.index = index;
    }
    
    public static Store of(int frameNo, int index) {
        return new Store(frameNo, index);
    }

    @Override
    public void execute(Context context) {
        context.store(frameNo, index);
    }
 
    @Override
    public String toString() {
        String f = frameNo == 0 ? "" : Integer.toString(frameNo);
        return "%d!%s".formatted(index, f);
    }
}
