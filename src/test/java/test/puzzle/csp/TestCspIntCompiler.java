package test.puzzle.csp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.Test;

import puzzle.core.Common;
import puzzle.csp.CspIntCompiler;
import puzzle.csp.CspIntCompiler.Constraint;
import puzzle.csp.CspIntCompiler.Domain;
import puzzle.csp.CspIntCompiler.Problem;
import puzzle.csp.CspIntCompiler.Solver;
import puzzle.csp.CspIntCompiler.Variable;
import puzzle.language.JavaCompilerInMemory.CompileError;

public class TestCspIntCompiler {

    static Logger logger = Common.getLogger(TestCspIntCompiler.class);

    @Test
    public void testSolver() throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        Problem problem = new Problem();
        Domain domain = Domain.rangeClosed(0, 3);
        Variable A = problem.variable("a", domain);
        Variable B = problem.variable("b", domain);
        Variable C = problem.variable("c", domain);
        Constraint c1 = problem.constraint("a < b");
        Constraint c2 = problem.constraint("b < c");
        assertEquals(List.of(A, B, C), problem.variables);
        assertEquals(List.of(c1, c2), problem.constraints);
        assertEquals("a < b", c1.predicate);
        assertEquals(List.of(c1), A.constraints);
        assertEquals(List.of(c1, c2), B.constraints);
        assertEquals("b < c", c2.predicate);
        assertEquals(List.of(c2), C.constraints);
        assertEquals(List.of(A, B), c1.variables);
        assertEquals(List.of(B, C), c2.variables);
        assertEquals("\\b(a|b|c)\\b", problem.variableNames().toString());
        assertEquals(List.of(List.of(), List.of(c1), List.of(c2)),
            Solver.constraintOrder(problem, List.of(A, B, C)));
        assertEquals(List.of(List.of(), List.of(c2), List.of(c1)),
            Solver.constraintOrder(problem, List.of(C, B, A)));
        // String source = Solver.generateSource(problem, problem.variables,
        //     Solver.constraintOrder(problem, problem.variables), null, null);
        // logger.info(source);
        // List<Variable> reverse = List.of(C, B, A);
        // String source2 = Solver.generateSource(problem, reverse,
        //     Solver.constraintOrder(problem, reverse), null, null);
        // logger.info(source2);
        List<int[]> answers = new ArrayList<>();
        // Solver.solve(problem, a -> logger.info(Arrays.toString(a)));
        Solver.solve(problem, a -> answers.add(a));
        int[][] expected = {{0, 1, 2}, {0, 1, 3}, {0, 2, 3}, {1, 2, 3}};
        assertArrayEquals(expected, answers.stream().toArray(int[][]::new));
    }

    public static int number(int... digits) {
        return IntStream.of(digits).reduce(0, (a, b) -> a * 10 + b);
    }

    @Test
    public void testSendMoreMoney()
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        Problem problem = new Problem();
        Domain first = Domain.rangeClosed(1, 9);
        Domain rest = Domain.rangeClosed(0, 9);
        Variable S = problem.variable("s", first);
        Variable E = problem.variable("e", rest);
        Variable N = problem.variable("n", rest);
        Variable D = problem.variable("d", rest);
        Variable M = problem.variable("m", first);
        Variable O = problem.variable("o", rest);
        Variable R = problem.variable("r", rest);
        Variable Y = problem.variable("y", rest);
        problem.allDifferent(S, E, N, D, M, O, R, Y);
        // Constraint c1 = problem.constraint(
        // "((s * 10 + e) * 10 + n) * 10 + d"
        // + " + ((m * 10 + o) * 10 + r) * 10 + e"
        // + " == (((m * 10 + o) * 10 + n) * 10 + e) * 10 + y");
        // Constraint c1 = problem.constraint(
        // String.format("%1$s(s, e, n, d) + %1$s(m, o, r, e) == %1$s(m, o, n,
        // e, y)",
        // getClass().getName() + ".number"));
        problem.constraint(
            "number(s, e, n, d) + number(m, o, r, e) == number(m, o, n, e, y)");
        String prolog = null;
        String epilog = "static int number(int... digits) {\n"
            + "    int r = 0;\n" + "    for (int d : digits)\n"
            + "        r = r * 10 + d;\n" + "    return r;\n" + "}";
        List<int[]> actual = new ArrayList<>();
        long start = System.currentTimeMillis();
        long exec = Solver.solve(problem, a -> actual.add(a), prolog, epilog);
        logger.info("total = " + (System.currentTimeMillis() - start)
            + "msec. exec = " + exec + "msec.");
        int[][] expected = {{9, 5, 6, 7, 1, 0, 8, 2}};
        assertArrayEquals(expected, actual.stream().toArray(int[][]::new));
        // logger.info(Solver.generateSource(problem, problem.variables,
        // Solver.constraintOrder(problem, problem.variables), prolog, epilog));
    }

    static List<int[][]> 数独(int[][] matrix)
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        Problem problem = new Problem();
        Domain unknown = Domain.rangeClosed(1, 9);
        Variable[][] variables = new Variable[9][9];
        List<List<Variable>> clusters = new ArrayList<>();
        List<int[][]> results = new ArrayList<>();

        new Object() {

            String name(int r, int c) {
                return String.format("v%d_%d", r, c);
            }

            void variables() {
                for (int r = 0; r < 9; ++r)
                    for (int c = 0; c < 9; ++c) {
                        int n = matrix[r][c];
                        variables[r][c] = problem.variable(name(r, c),
                            n == 0 ? unknown : Domain.of(n));
                    }
            }

            void clusters() {
                for (int r = 0; r < 9; ++r) {
                    List<Variable> row = new ArrayList<>();
                    clusters.add(row);
                    for (int c = 0; c < 9; ++c)
                        row.add(variables[r][c]);
                }
                for (int c = 0; c < 9; ++c) {
                    List<Variable> col = new ArrayList<>();
                    clusters.add(col);
                    for (int r = 0; r < 9; ++r)
                        col.add(variables[r][c]);
                }
                for (int r = 0; r < 9; r += 3)
                    for (int c = 0; c < 9; c += 3) {
                        List<Variable> box = new ArrayList<>();
                        clusters.add(box);
                        for (int i = 0; i < 3; ++i)
                            for (int j = 0; j < 3; ++j)
                                box.add(variables[r + i][c + j]);
                    }
            }

            void constraint() {
                for (List<Variable> cluster : clusters)
                    problem.allDifferent(cluster.toArray(Variable[]::new));
            }

            int[][] matrix(int[] array) {
                int[][] matrix = new int[9][];
                for (int i = 0, j = 0; i < 9; ++i, j += 9)
                    matrix[i] = Arrays.copyOfRange(array, j, j + 9);
                return matrix;
            }

            void solve() throws ClassNotFoundException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException,
                NoSuchMethodException, SecurityException, CompileError {
                List<Variable> bindingOrder = CspIntCompiler.clusterBinding(problem, clusters);
//                List<Variable> bindingOrder = CspInt.domainBinding(problem);
//                List<Variable> bindingOrder = problem.variables;
                Solver.solve(problem, a -> results.add(matrix(a)),
                    bindingOrder);
            }

            void make() throws ClassNotFoundException, IllegalAccessException,
                IllegalArgumentException, InvocationTargetException,
                NoSuchMethodException, SecurityException, CompileError {
                variables();
                clusters();
                constraint();
                solve();
            }
        }.make();
        return results;
    }

    @Test
    public void testSudokuWikipedia()
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        // Wikipedia 数独 の例題
        // https://ja.wikipedia.org/wiki/%E6%95%B0%E7%8B%AC
        int[][] question = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9},
        };
        List<int[][]> result = 数独(question);
        for (int[][] b : result)
            for (int[] row : b)
                logger.info(Arrays.toString(row));
    }

    @Test
    public void test難問SUDOKU()
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        // 難問SUDOKU の例題
        // https://www.danboko.net/
        int[][] question = {
            { 2, 0, 0, 4, 0, 6, 0, 0, 9 },
            { 0, 3, 1, 0, 5, 0, 6, 8, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 6, 0, 0, 9, 0, 5, 0, 0, 4 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 8, 0, 6, 0, 7, 0, 9, 0 },
            { 5, 0, 0, 0, 0, 0, 0, 0, 2 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 4, 9, 5, 0, 1, 8, 3, 0 },
        };
        List<int[][]> result = 数独(question);
        for (int[][] b : result)
            for (int[] row : b)
                logger.info(Arrays.toString(row));
    }

    @Test
    public void testナンプレ問題10()
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        // https://si-coding.net/sudoku10.html
        int[][] question = {
            { 0, 0, 1, 0, 9, 0, 0, 0, 0 },
            { 0, 5, 0, 4, 0, 0, 0, 0, 2 },
            { 8, 0, 3, 0, 1, 0, 5, 0, 0 },
            { 0, 0, 6, 0, 0, 0, 0, 2, 0 },
            { 0, 0, 0, 0, 6, 0, 0, 0, 8 },
            { 2, 0, 0, 8, 0, 3, 0, 6, 5 },
            { 0, 0, 0, 0, 0, 6, 0, 0, 4 },
            { 0, 0, 0, 0, 0, 4, 0, 7, 0 },
            { 0, 9, 2, 0, 0, 0, 0, 0, 3 },
        };
        List<int[][]> result = 数独(question);
        for (int[][] b : result)
            for (int[] row : b)
                logger.info(Arrays.toString(row));
    }

    @Test
	public void testナンプレNo601010()
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        // https://numpre7.com/np601010
        int[][] question = {
            { 0, 0, 1, 0, 6, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 5, 0, 0 },
            { 0, 8, 0, 3, 0, 0, 0, 0, 9 },
            { 0, 7, 0, 4, 0, 9, 8, 0, 0 },
            { 2, 0, 0, 0, 0, 0, 0, 0, 4 },
            { 0, 0, 6, 1, 0, 2, 0, 5, 0 },
            { 4, 0, 0, 0, 0, 5, 0, 7, 0 },
            { 0, 0, 9, 6, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 7, 0, 6, 0, 0 },
        };
        List<int[][]> result = 数独(question);
        for (int[][] b : result)
            for (int[] row : b)
                logger.info(Arrays.toString(row));
    }

    @Test
	public void testOurHardestSudokuAndHowToSolveIt()
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        // YouTube
        // https://youtu.be/-ZZFEgCQsvA
        int[][] question = {
            { 0, 0, 1, 0, 6, 0, 0, 5, 9 },
            { 0, 0, 0, 0, 0, 3, 0, 2, 0 },
            { 0, 6, 0, 0, 8, 0, 0, 0, 0 },
            { 4, 0, 0, 0, 0, 0, 5, 0, 0 },
            { 0, 2, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 7, 0, 2, 0, 0, 4, 8, 0 },
            { 8, 0, 0, 0, 0, 0, 9, 0, 5 },
            { 7, 0, 0, 6, 0, 9, 0, 3, 0 },
            { 0, 0, 5, 0, 0, 0, 0, 4, 0 },
        };
        List<int[][]> result = 数独(question);
        for (int[][] b : result)
            for (int[] row : b)
                logger.info(Arrays.toString(row));
    }

    @Test
	public void testEvil_sudoku_with_17_initial_values()
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        // https://www.free-sudoku.com/sudoku.php?dchoix=evil
        int[][] question = {
            { 1, 0, 0, 7, 0, 0, 0, 0, 6 },
            { 0, 8, 0, 0, 0, 0, 0, 9, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 6, 0, 0, 4, 0, 0, 2, 0, 0 },
            { 4, 0, 0, 0, 8, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 9, 0, 0, 5, 0 },
            { 0, 5, 3, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 2, 0, 0, 4, 0, 0 },
            { 0, 9, 0, 0, 0, 0, 0, 0, 0 },
        };
        List<int[][]> result = 数独(question);
        for (int[][] b : result)
            for (int[] row : b)
                logger.info(Arrays.toString(row));
    }

    @Test
	public void testGood_at_Sudoku_Heres_some_youll_never_complete()
        throws ClassNotFoundException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        NoSuchMethodException, SecurityException, CompileError {
        logger.info(Common.methodName());
        // http://theconversation.com/good-at-sudoku-heres-some-youll-never-complete-5234
        int[][] question = {
            { 0, 0, 0, 7, 0, 0, 0, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 4, 3, 0, 2, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 6 },
            { 0, 0, 0, 5, 0, 9, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 4, 1, 8 },
            { 0, 0, 0, 0, 8, 1, 0, 0, 0 },
            { 0, 0, 2, 0, 0, 0, 0, 5, 0 },
            { 0, 4, 0, 0, 0, 0, 3, 0, 0 },
        };
        List<int[][]> result = 数独(question);
        for (int[][] b : result)
            for (int[] row : b)
                logger.info(Arrays.toString(row));
    }
}
