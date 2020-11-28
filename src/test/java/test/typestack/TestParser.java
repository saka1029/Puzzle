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
        typeStack.put("if", new Definition() {
            @Override
            public FunctionType type(FunctionType left) {
                Type[] output = left.output();
                int length = output.length;
                if (length < 3)
                    throw new RuntimeException("too short length=" + length);
                Type elseType = output[length - 1];
                Type thenType = output[length - 2];
                Type predicate = output[length - 3];
                if (!predicate.equals(Type.BOOLEAN))
                    throw new RuntimeException("Boolean expected");
                if (!(thenType instanceof Block))
                    throw new RuntimeException("Block expected");
                if (!(elseType instanceof Block))
                    throw new RuntimeException("Block expected");
                Block elseBlock = (Block)elseType;
                Block thenBlock = (Block)thenType;
                if (!(thenBlock.type().equals(elseBlock.type())))
                    throw new RuntimeException("Block type unmatch");
                return Type.functionType(Type.types(Type.BOOLEAN, thenBlock, elseBlock),
                    ((FunctionType)thenBlock.type()).output());
            }
            @Override
            public Executable executable() {
                return rc -> {
                    Block elseBlock = (Block)rc.pop();
                    Block thenBlock = (Block)rc.pop();
                    boolean predicate = (Boolean)rc.pop();
                    if (predicate)
                        thenBlock.execute(rc);
                    else
                        elseBlock.execute(rc);
                };
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
