package typestack;

import java.util.ArrayList;

public class RuntimeContext {

    final ArrayList<Object> stack = new ArrayList<>();

    public void push(Object element) { stack.add(element); }
    public Object pop() { return stack.remove(stack.size()); }

}
