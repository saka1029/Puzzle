package test.puzzle.language;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class TestArrayLisp {

    static Object[] list(Object... elements) {
        return elements;
    }
    
    static Object car(Object... list) {
        return list[0];
    }
    
    static Object[] cdr(Object... list) {
        return Arrays.copyOfRange(list, 1, list.length);
    }
    
    @Test
    public void testList() {
        assertArrayEquals(new Object[] {1, new Object[] {2, 3}, 4},
            list(1, list(2, 3), 4));
        assertEquals(1, car(list(1, 2, 3, 4)));
        assertArrayEquals(new Object[] {2, 3, 4}, cdr(list(1, 2, 3, 4)));
    }

}
