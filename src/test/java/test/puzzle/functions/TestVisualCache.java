package test.puzzle.functions;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.functions.ObjectArray;
import puzzle.functions.VisualCache;

class TestVisualCache {

    static final Logger logger = Logger.getLogger(TestVisualCache.class.getName());

    @Test
    void testEnterExit() {
        int result = new Object() {
            VisualCache<Integer> combination = new VisualCache<>("combination",
                a -> combination((int) a[0], (int) a[1]))
                    .out(logger::info);

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
            VisualCache<Integer> combination = new VisualCache<>("combination",
                a -> combination((int) a[0], (int) a[1]))
                    .out(logger::info);

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
                logger.info(combination.cach().get(ObjectArray.of(4, 2)).toString());
                return result;
            }

        }.main();
        logger.info("result=" + result);
    }

}
