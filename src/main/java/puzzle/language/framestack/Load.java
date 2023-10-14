package puzzle.language.framestack;

public class Load implements Executable {
    
    public final int frameNo, index;
    
    Load(int frameNo, int index) {
        this.frameNo = frameNo;
        this.index = index;
    }
    
    public static Load of(int frameNo, int index) {
        return new Load(frameNo, index);
    }

    @Override
    public void execute(Context context) {
        context.load(frameNo, index);
    }
 
    @Override
    public String toString() {
        return "load(%d, %d)".formatted(frameNo, index);
    }
}
