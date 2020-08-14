package test.puzzle;

import org.junit.jupiter.api.Test;

class TestNQueens {

    /**
     * N Queens Problem (number of Solutions)
     * http://www.ic-net.or.jp/home/takaken/e/queen/
     */
    static int nQueens(int size) {
        return new Object() {
            int[] bits = new int[size];
            final int mask = (1 << size) - 1;
            int count = 0;

            void found() {
                count++;
                System.out.println(count);
                for (int bit : bits) {
                    String row = Integer.toString(bit, 2);
                    row = "0".repeat(size - row.length()) + row;
                    System.out.println(row);
                }
            }

            /**
             *
             * @param y
             * @param left 行けないところをbit1で表現している。
             * @param down 行けないところをbit1で表現している。
             * @param right 行けないところをbit1で表現している。
             */
            void backtrack(int y, int left, int down, int right) {
                if (y == size)
                    found();
                else {
                    int bitmap = mask & ~(left | down | right);  // 行けるところをbit1で表現している。
                    while (bitmap != 0) {
                        int bit = -bitmap & bitmap;     // bitmapの一番右にある1のbitを取り出している。
                        bits[y] = bit;
                        bitmap ^= bit;
                        backtrack(y + 1, (left | bit) << 1, down | bit, (right | bit) >> 1);
                    }
                }
            }

            int run() {
                backtrack(0, 0, 0, 0);
                return count;
            }
        }.run();
    }

    @Test
    public void testNQueens() {
        int size = 8;
        System.out.println(size + "-queens : " + nQueens(size));
    }

    static String bits(int n, int width) {
        String r = Integer.toString(n, 2);
        return "0".repeat(width - r.length()) + r;
    }
    @Test
    public void testMostRightBit() {
        int w = 8;
        int mask = (1 << 8) - 1;
        for (int i = 0; i < 64; ++i)
            System.out.printf("i=%2d(%s) MRB=%s lowest on bit=%s%n", i, bits(i, w), bits(mask & (-i & i), w), bits(Integer.lowestOneBit(i), w));
    }

    @Test
    public void testBitToInt() {
        for (int i = 1; i < 128; i <<= 1)
            System.out.println(bits(i, 8) + " : " + Integer.numberOfTrailingZeros(i));
    }

}
