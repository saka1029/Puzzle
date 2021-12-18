package test.puzzle.graphics;

import java.util.Map;

import org.junit.jupiter.api.Test;

import puzzle.graphics.Turtle;

class TestTurtle {

    @Test
    void test() {
        Map<String, String> rules = Map.of(
            "A", "+A",
            "+", "F+");
        for (int i = 0; i < 8; ++i)
            System.out.println(Turtle.lsystem("A", i, rules));
    }

}
