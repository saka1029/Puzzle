package test.puzzle.csp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import puzzle.csp.CspInt.Constraint;
import puzzle.csp.CspInt.Domain;
import puzzle.csp.CspInt.Problem;
import puzzle.csp.CspInt.Solver;
import puzzle.csp.CspInt.Variable;

class TestCspInt {

    @Test
    void testSolverConstraintOrder() {
        Problem problem = new Problem();
        Domain domain = Domain.rangeClosed(0, 3);
        Variable A = problem.variable("a", domain);
        Variable B = problem.variable("b", domain);
        Variable C = problem.variable("c", domain);
        Constraint c1 = problem.constraint((a, b) -> a < b, A, B);
        Constraint c2 = problem.constraint((b, c) -> b < c, B, C);
        assertEquals(List.of(List.of(), List.of(c1), List.of(c2)),
            Solver.constraintOrder(problem, List.of(A, B, C)));
        assertEquals(List.of(List.of(), List.of(c2), List.of(c1)),
            Solver.constraintOrder(problem, List.of(C, B, A)));
        assertTrue(c1.predicate.test(1, 2));
        assertFalse(c1.predicate.test(1, 0));
    }

}
