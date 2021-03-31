package test.typestack;

import static org.junit.jupiter.api.Assertions.*;
import static typestack.Type.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import typestack.FunctionType;

class TestFunctionType {

    Logger logger = Logger.getLogger(TestFunctionType.class.getName());

    @Test
    void testEquals() {
        assertEquals(functionType(types(INTEGER, INTEGER), types(INTEGER)),
            functionType(types(INTEGER, INTEGER), types(INTEGER)));
    }

    @Test
    void testComposite() {
        FunctionType loadInteger = functionType(types(), types(INTEGER));
        FunctionType integerOperator = functionType(types(INTEGER, INTEGER), types(INTEGER));
        assertEquals(functionType(types(), types(INTEGER)),
            loadInteger.composite(loadInteger).composite(integerOperator));
        assertEquals(functionType(types(INTEGER), types(INTEGER)),
            loadInteger.composite(integerOperator));
    }

    @Test
    void testCompositeUnmatch() {
        FunctionType loadString = functionType(types(), types(STRING));
        FunctionType integerOperator = functionType(types(INTEGER, INTEGER), types(INTEGER));
        try {
            assertEquals(functionType(types(), types(INTEGER)),
                loadString.composite(loadString).composite(integerOperator));
            fail();
        } catch (AssertionFailedError e) {
            logger.info(e.toString());
        }
    }

}
