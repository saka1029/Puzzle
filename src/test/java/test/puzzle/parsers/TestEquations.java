package test.puzzle.parsers;

import static org.junit.Assert.*;
import static puzzle.parsers.Equations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import puzzle.parsers.Equations.Expression;
import puzzle.parsers.Equations.Variable;

public class TestEquations {

    @Test
    public void testParse() {
        String source = " あ = い + 2\r\n"
            + "う = 5 * -(3 + 12)\r\n"
            + "い = う - 2\r\n";
        Map<Variable, Expression> equations = parse(source);
        Map<Variable, Integer> values = new HashMap<>();
        for (Entry<Variable, Expression> e : equations.entrySet()) {
            System.out.println(e + " : " + e.getKey().value(equations, values));
        }
        System.out.println(values);
    }

    @Test
    public void testRegexVARIABLE() {
        assertTrue(VARIABLE.matcher("日本語___bc𩸽３").matches());
    }

}
