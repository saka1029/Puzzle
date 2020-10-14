package typestack;

public interface TypeResolver {

    FunctionType type(FunctionType previous);

}
