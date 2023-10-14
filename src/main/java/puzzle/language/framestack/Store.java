package puzzle.language.framestack;

public class Store implements Executable {
    
    public static final Store S0 = Store.of(0, 0);
    public static final Store S1 = Store.of(0, 1);
    public static final Store S2 = Store.of(0, 2);
    public static final Store S3 = Store.of(0, 3);

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
        return "store(%d, %d)".formatted(frameNo, index);
    }
}
