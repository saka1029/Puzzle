package typestack;

public interface Type {
    boolean isAssignableFrom(Type other);

    public static final Type OBJECT = new BasicType(Object.class);
    public static final Type BOOLEAN = new BasicType(Boolean.class);
    public static final Type INTEGER = new BasicType(Integer.class);
    public static final Type STRING = new BasicType(String.class);
    public static final Type CHARACTER = new BasicType(Character.class);
    public static final Type[] EMPTY_TYPES = new Type[0];

    public static Type[] types(Type... types) { return types.clone(); }
    public static FunctionType functionType(Type[] input, Type[] output) { return new FunctionType(input, output); }
}
