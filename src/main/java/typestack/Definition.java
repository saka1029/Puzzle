package typestack;

public interface Definition {
    Executable executable();
    FunctionType type(FunctionType left);
}
