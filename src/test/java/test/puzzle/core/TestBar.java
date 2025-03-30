package test.puzzle.core;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestBar {

    static class Bar {
        final int size;
        final boolean[] sheets;

        Bar(int size) {
            this.size = size;
            this.sheets = new boolean[size];
        }

        int distance(int i) {
            if (sheets[i])
                return 0;
            int left = size;
            for (int j = i - 1; j >= 0; --j)
                if (sheets[j]) {
                    left = i - j;
                    break;
                }
            int right = size;
            for (int j = i + 1; j < size; ++j)
                if (sheets[j]) {
                    right = j - i;
                    break;
                }
            return Math.min(left, right);
        }
    }

    @Test
    public void testMaxDistance() {
        int size = 25;
        Bar b = new Bar(size);
        assertEquals(size, b.distance(0));
        assertEquals(size, b.distance(1));
        assertEquals(size, b.distance(24));
        b.sheets[0] = true;
        assertEquals(0, b.distance(0));
        assertEquals(1, b.distance(1));
        assertEquals(24, b.distance(24));
        b.sheets[24] = true;
        assertEquals(0, b.distance(0));
        assertEquals(1, b.distance(1));
        assertEquals(11, b.distance(11));
        assertEquals(12, b.distance(12));
        assertEquals(11, b.distance(13));
        assertEquals(1, b.distance(23));
        assertEquals(0, b.distance(24));
    }

}
