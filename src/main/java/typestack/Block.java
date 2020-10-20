package typestack;

import java.util.ArrayList;
import java.util.List;

public class Block implements Executable, Type {

    final List<Executable> executables = new ArrayList<>();
    FunctionType type = Type.functionType(Type.types(), Type.types());

    public void add(Definition definition) {
        type = type.composite(definition.type(type));
        executables.add(definition.executable());
    }

    public Type type() {
        return type;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void execute(RuntimeContext rc) {
        for (Executable e : executables)
            e.execute(rc);
    }

}
