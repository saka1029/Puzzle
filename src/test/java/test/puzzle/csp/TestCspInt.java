package test.puzzle.csp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import puzzle.Common;
import puzzle.csp.CspInt.Constraint;
import puzzle.csp.CspInt.Domain;
import puzzle.csp.CspInt.Problem;
import puzzle.csp.CspInt.Solver;
import puzzle.csp.CspInt.Variable;


class TestCspInt {

    static Logger logger = Common.getLogger(TestCspInt.class);

    @Test
    void testSolverConstraintOrder() {
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
            Solver.constraintOrder(problem, problem.variables));
        logger.info(source);
        List<Variable> reverse = List.of(C, B, A);
        String source2 = Solver.generateSource(problem, reverse,
            Solver.constraintOrder(problem, reverse));
        logger.info(source2);
    }

    public class CspSolver {
        public static void solve(Consumer<int[]> _callback) {
            int[] _d0 = {0, 1, 2, 3};
            for (int c : _d0)
                for (int b : _d0)
                    if ((b < c))
                        for (int a : _d0)
                            if ((a < b))
                                _callback.accept(new int[] {a, b, c});
        }
    }
}
