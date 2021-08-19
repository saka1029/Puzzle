package test.puzzle.csp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.csp.CspInt.Constraint;
import puzzle.csp.CspInt.Domain;
import puzzle.csp.CspInt.Problem;
import puzzle.csp.CspInt.Solver;
import puzzle.csp.CspInt.Variable;
import puzzle.language.JavaCompilerInMemory.CompileError;

public class TestCspInt {

    static Logger logger = Common.getLogger(TestCspInt.class);

    @Test
    void testSolver() throws ClassNotFoundException, IllegalAccessException,
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
        String source = Solver.generateSource(problem, problem.variables,
            Solver.constraintOrder(problem, problem.variables), null, null);
//        logger.info(source);
        List<Variable> reverse = List.of(C, B, A);
        String source2 = Solver.generateSource(problem, reverse,
            Solver.constraintOrder(problem, reverse), null, null);
//        logger.info(source2);
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
//        Constraint c1 = problem.constraint(
//            "((s * 10 + e) * 10 + n) * 10 + d"
//                + " + ((m * 10 + o) * 10 + r) * 10 + e"
//                + " == (((m * 10 + o) * 10 + n) * 10 + e) * 10 + y");
//        Constraint c1 = problem.constraint(
//            String.format("%1$s(s, e, n, d) + %1$s(m, o, r, e) == %1$s(m, o, n, e, y)",
//                getClass().getName() + ".number"));
        Constraint c1 = problem.constraint(
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
//        logger.info(Solver.generateSource(problem, problem.variables, Solver.constraintOrder(problem, problem.variables), prolog, epilog));
    }

    static List<int[][]> sudoku(int[][] matrix) {
        Problem problem = new Problem();
        Domain unknown = Domain.rangeClosed(1, 9);
        Variable[][] variables = new Variable[9][9];
        List<List<Variable>> clusters = new ArrayList<>();
        new Object() {
            void variables() {
                for (int i = 0; i < 9; ++i)
                    for (int j = 0; j < 9; ++j) {
                        int n = matrix[i][j];
                        variables[i][j] = problem.variable("v" + i + "_" + j,
                            n == 0 ? unknown : Domain.of(n));
                    }
            }
            
            void clusters() {
                for (int i = 0; i < 9; ++i)
                    clusters.add(IntStream.range(0, 9)
                        .mapToObj(j -> variables[i][j]).toList());
                for (int j = 0; j < 9; ++j)
                    clusters.add(IntStream.range(0, 9)
                        .mapToObj(i -> variables[i][j]).toList());
                for (int r = 0; r < 9; r += 3)
                    for (int c = 0; c < 9; c += 3)
                        clusters.add(IntStream.range(r, r + 3)
                            .boxed()
                            .flatMap(i -> IntStream.range(c, c + 3)
                                .mapToObj(j -> variables[i][j]))
                            .toList());
            }

            void make() {
                variables();
                clusters();
            }
        }.make();
    }
}
