package test.puzzle.functions;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.functions.ObjectArray;
import puzzle.functions.VisualCache;

class TestVisualCache {

    static final Logger logger = Logger.getLogger(TestVisualCache.class.getName());

    @Test
    void testEnterExit() {
        int result = new Object() {
            VisualCache<Integer> combination = VisualCache.forFunction(
                "combination",
                a -> combination((int) a[0], (int) a[1]))
                .output(logger::info);

            int combination(int n, int r) {
                combination.enter(n, r);
                if (r == 0 || r == n)
                    return combination.exit(1);
                else
                    return combination.exit(
                        combination(n - 1, r - 1)
                            + combination(n - 1, r));
            }
        }.combination(6, 3);
        logger.info("result=" + result);
    }

    @Test
    void testEnterExitCall() {
        int result = new Object() {
            VisualCache<Integer> combination = VisualCache.forFunction(
                "combination",
                a -> combination((int) a[0], (int) a[1]))
                // .caching(false)
                .output(logger::info);

            int combination(int n, int r) {
                combination.enter(n, r);
                if (r == 0 || r == n)
                    return combination.exit(1);
                else
                    return combination.exit(
                        combination.call(n - 1, r - 1)
                            + combination.call(n - 1, r));
            }

            int main() {
                int result = combination.call(8, 4);
                logger.info(combination.toString());
                logger.info(combination.cache().get(ObjectArray.of(4, 2)).toString());
                return result;
            }

        }.main();
        logger.info("result=" + result);
    }

    @Test
    void testFibonacci() {
        new Object() {
            VisualCache<Integer> fibonacci = VisualCache.forFunction(
                "fibonacci",
                args -> fibonacci((int) args[0]))
                // .caching(false)
                .output(logger::info);

            int fibonacci(int n) {
                fibonacci.enter(n);
                if (n < 2)
                    return fibonacci.exit(n);
                else
                    return fibonacci.exit(
                        fibonacci.call(n - 1)
                            + fibonacci.call(n - 2));
            }

            void main() {
                fibonacci.call(8);
                fibonacci.call(9);
                logger.info(fibonacci.toString());
            }

        }.main();
    }

    @Test
    public void testGCD() {
        new Object() {

            VisualCache<Integer> gcd = VisualCache.forFunction("gcd", a -> gcd((int) a[0], (int) a[1]))
                .caching(false)
                .output(logger::info);

            int gcd(int x, int y) {
                gcd.enter(x, y);
                if (y == 0)
                    return gcd.exit(x);
                else
                    return gcd.exit(gcd.call(y, x % y));
            }

            void main() {
                logger.info("result = " + gcd.call(111, 259));
                logger.info(gcd.toString());
            }
        }.main();
    }

    @Test
    public void testTowerOfHanoi() {
        new Object() {
            VisualCache<Void> hanoi = VisualCache.forProcedure("hanoi")
                .output(logger::info);

            void hanoi(int n, String a, String b, String c) {
                hanoi.enter(n, a, b, c);
                if (n > 0) {
                    hanoi(n - 1, a, c, b);
                    logger.info("move " + n + " from " + a + " to " + c);
                    hanoi(n - 1, b, a, c);
                }
                hanoi.exit();
            }

            void main() {
                hanoi(4, "A", "B", "C");
                logger.info(hanoi.toString());
            }
        }.main();
    }

    @Test
    public void testCombinationMatrixCache() {
        int max = 12;
        int[][] cache = new int[max + 1][];
        for (int i = 0; i < max + 1; ++i)
            cache[i] = new int[i + 1];

        new Object() {

            void print() {
                for (int[] row : cache) {
                    StringBuilder sb = new StringBuilder();
                    for (int e : row)
                        sb.append(String.format("%6d ", e));
                    logger.info(sb.toString());
                }
            }

            int combinationCached(int n, int r) {
                logger.info("n = " + n + " r = " + r);
                print();
                if (cache[n][r] == 0)
                    return combination(n, r);
                else
                    return cache[n][r];
            }

            int combination(int n, int r) {
                if (r == 0 || r == n)
                    return cache[n][r] = 1;
                else
                    return cache[n][r] = combinationCached(n - 1, r - 1)
                        + combinationCached(n - 1, r);
            }

            void main() {
                logger.info("result=" + combinationCached(max, max / 2));
                print();
            }
        }.main();
    }

    @Test
    public void testBinarySearch() {
        new Object() {

            VisualCache<Integer> binarySearch = VisualCache.forFunction(
                "binarySearch", a -> binarySearch((int[]) a[0], (int) a[1], (int) a[2], (int) a[3]))
                .output(logger::info);

            int search(int[] data, int toFind) {
                return binarySearch.call(data, toFind, 0, data.length - 1);
            }

            int binarySearch(int[] data, int toFind, int start, int end) {
                binarySearch.enter(data, toFind, start, end);
                int mid = start + (end - start) / 2;
                if (start > end)
                    return binarySearch.exit(-1);
                else if (data[mid] == toFind)
                    return binarySearch.exit(mid);
                else if (data[mid] > toFind)
                    return binarySearch.exit(binarySearch.call(data, toFind, start, mid - 1));
                else
                    return binarySearch.exit(binarySearch.call(data, toFind, mid + 1, end));
            }

            void main() {
                int[] a = {1, 2, 4, 5, 6, 8, 9, 10};
                assertEquals(6, search(new int[] {1, 2, 4, 5, 6, 8, 9, 10}, 9));
                logger.info(binarySearch.toString());
            }
        }.main();
    }

    /*
     * 一般的なメソッドをビジュアル化するのは単純ではありません。
     */
    static class Node {
        int data;
        Node left;
        Node right;
        static VisualCache<Boolean> contains = VisualCache.forFunction(
            "contains", a -> ((Node) a[0]).contains((int) a[1]))    // a = {this, i}と考える。
            .output(logger::info);

        Node(int data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        Node(int data) {
            this(data, null, null);
        }

        boolean contains(int i) {
            contains.enter(this, i);
            if (data == i)
                return contains.exit(true);
            else
                return contains.exit(
                    left != null && contains.call(left, i)
                        || right != null && contains.call(right, i));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            toString(sb);
            return "[" + sb.substring(1) + "]";
        }

        void toString(StringBuilder sb) {
            if (left != null)
                left.toString(sb);
            sb.append(" ").append(data);
            if (right != null)
                right.toString(sb);
        }
    }

    @Test
    public void testTreeNode() {
        Node root = new Node(3,
            new Node(1, new Node(0), new Node(2)),
            new Node(5, new Node(4), new Node(6)));
        assertTrue(Node.contains.call(root, 0));
        assertTrue(Node.contains.call(root, 4));
        assertFalse(Node.contains.call(root, 8));
        assertTrue(Node.contains.call(root, 4));
        assertFalse(Node.contains.call(root, 8));
        logger.info(root.toString());
        logger.info(Node.contains.toString());
    }

    /**
     * Find all solutions in positive integers a, b, c to the equation
     *
     * <pre>
     *  a! * b! = a! + b! + c!
     * </pre>
     * Incredible Factorial Problem! - YouTube - MindYourDecisions
     * https://www.youtube.com/watch?v=9dyK_op-Ocw
     */
    @Test
    public void testFactorial() {
        BigInteger ZERO = BigInteger.ZERO;
        BigInteger ONE = BigInteger.ONE;
        new Object() {
            VisualCache<BigInteger> fact = VisualCache.forFunction(
                "fact", a -> fact((BigInteger)a[0])).noOutput();
            BigInteger fact(BigInteger n) {
                fact.enter(n);
                if (n.compareTo(ONE) <= 0)
                    return fact.exit(ONE);
                else
                    return fact.exit(n.multiply(fact.call(n.subtract(ONE))));
            }

            void main() {
                BigInteger max = BigInteger.valueOf(100);
                for (BigInteger a = ZERO; a.compareTo(max) < 0; a = a.add(ONE)) {
                    BigInteger fa = fact.call(a);
                    for (BigInteger b = a; b.compareTo(max) < 0; b = b.add(ONE)) { // aとbは交換可なのでa<=bとする。
                        BigInteger fb = fact.call(b);
                        BigInteger faMultiplyfb = fa.multiply(fb);
                        BigInteger faPlusfb = fa.add(fb);
                        for (BigInteger c = ZERO; c.compareTo(max) < 0; c = c.add(ONE)) {
                            BigInteger fc = fact.call(c);
                            if (faMultiplyfb.equals(faPlusfb.add(fc)))
                                logger.info("a=" + a + " b=" + b + " c=" + c);
                        }
                    }
                }
                logger.info(fact.toString());
            }
        }.main();
    }

    @Test
    public void testTarai() {
        new Object() {
            VisualCache<Integer> tarai = VisualCache
                .forFunction("tarai", args -> tarai((int)args[0], (int)args[1], (int)args[2]))
                .output(logger::info)
                .caching(true);

            int tarai(int x, int y, int z) {
                tarai.enter(x, y, z);
                if (x <= y)
                    return tarai.exit(y);
                else
                    return tarai.exit(tarai.call(
                        tarai.call(x - 1, y, z),
                        tarai.call(y - 1, z, x),
                        tarai.call(z - 1, x, y)));
            }

            void test() {
                assertEquals(5, (int)tarai.call(5, 2, 1));
                System.out.println(tarai.cache());
            }

        }.test();
    }

}
