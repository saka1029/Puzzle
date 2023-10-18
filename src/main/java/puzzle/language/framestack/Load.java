package puzzle.language.framestack;

public record Load(int frameNo, int index) implements Executable {
    
    public static final Load A1 = Load.of(0, -1);
    public static final Load A2 = Load.of(0, -2);
    public static final Load A3 = Load.of(0, -3);
    public static final Load A4 = Load.of(0, -4);
    public static final Load L0 = Load.of(0, 0);
    public static final Load L1 = Load.of(0, 1);
    public static final Load L2 = Load.of(0, 2);
    public static final Load L3 = Load.of(0, 3);

    public static Load of(int frameNo, int index) {
        return new Load(frameNo, index);
    }

    @Override
    public void execute(Context context) {
        context.load(frameNo, index);
    }
 
    @Override
    public String toString() {
        String f = frameNo == 0 ? "" : Integer.toString(frameNo);
        return "%d@%s".formatted(index, f);
    }
}
