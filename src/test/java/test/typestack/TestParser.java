package test.typestack;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import typestack.Block;
import typestack.Definition;
import typestack.Executable;
import typestack.FunctionType;
import typestack.RuntimeContext;
import typestack.Type;
import typestack.TypeStack;

class TestParser {

    static void putDefinitions(TypeStack typeStack) {
        typeStack.put("+", new Definition() {

            @Override
            public FunctionType type(FunctionType left) {
                return Type.functionType(Type.types(Type.INTEGER, Type.INTEGER), Type.types(Type.INTEGER));
            }

            @Override
            public Executable executable() {
                return rc -> rc.push((int)rc.pop() + (int)rc.pop());
            }
        });
        typeStack.put("+", new Definition() {

            @Override
            public FunctionType type(FunctionType left) {
                return Type.functionType(Type.types(Type.STRING, Type.OBJECT), Type.types(Type.STRING));
            }

            @Override
            public Executable executable() {
                return rc -> { Object right = rc.pop(); rc.push((String)rc.pop() + right); };
            }
        });
    }

    @Test
    void testIntegerAddition() {
        TypeStack typeStack = new TypeStack();
        putDefinitions(typeStack);
        Block block = typeStack.parse("1 2 + ");
        RuntimeContext rc = new RuntimeContext();
        block.execute(rc);
        assertEquals(3, rc.pop());
    }

    @Test
    void testStringAddition() {
        TypeStack typeStack = new TypeStack();
        putDefinitions(typeStack);
        Block block = typeStack.parse("\"ABC\" 1 2 + +");
        RuntimeContext rc = new RuntimeContext();
        block.execute(rc);
        assertEquals("ABC3", rc.pop());
    }

    @Test
    void testBlock() {
        TypeStack typeStack = new TypeStack();
        putDefinitions(typeStack);
        Block block = typeStack.parse("[1]");
        RuntimeContext rc = new RuntimeContext();
        block.execute(rc);
        Object top = rc.pop();
        assertTrue(top instanceof Block);
        assertEquals(Type.functionType(Type.types(), Type.types(Type.INTEGER)), ((Block)top).type());
    }

}
