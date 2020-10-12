package test.typestack;

import static org.junit.jupiter.api.Assertions.*;
import static typestack.Type.*;

import org.junit.jupiter.api.Test;

import typestack.BasicType;

class TestType {

    @Test
    void testEquals() {
        assertEquals(OBJECT, new BasicType(Object.class));
        assertEquals(OBJECT.hashCode(), new BasicType(Object.class).hashCode());
    }

    @Test
    void testIsAssignableFrom() {
        assertTrue(OBJECT.isAssignableFrom(STRING));
        assertFalse(STRING.isAssignableFrom(OBJECT));
        assertFalse(STRING.isAssignableFrom(INTEGER));
        assertFalse(INTEGER.isAssignableFrom(STRING));
    }

}
