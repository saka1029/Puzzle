package test.puzzle.language;

import static puzzle.language.LambdaCalculus.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import puzzle.language.LambdaCalculus.Application;
import puzzle.language.LambdaCalculus.Bind;
import puzzle.language.LambdaCalculus.BoundVariable;
import puzzle.language.LambdaCalculus.Expression;
import puzzle.language.LambdaCalculus.FreeVariable;
import puzzle.language.LambdaCalculus.Lambda;

class TestLambdaCalculusStatic {

    public static Expression expand(Expression expression, Map<String, Expression> globals) {
        return expand(expression, globals, null);
    }

    public static Expression expand(Expression expression, Map<String, Expression> globals,
        Bind<BoundVariable, BoundVariable> lambdaBind) {
        if (expression instanceof Lambda lambda) {
            BoundVariable newVariable = BoundVariable.of(lambda.variable.name);
            Bind<BoundVariable, BoundVariable> newBind = new Bind<>(
                lambdaBind, lambda.variable, newVariable);
            Expression newBody = expand(lambda.body, globals, newBind);
            Lambda newLambda = Lambda.of(newVariable, newBody, newBind.count);
            return newLambda;

        } else if (expression instanceof BoundVariable boundVariable) {
            return Bind.find(lambdaBind, boundVariable);
        } else if (expression instanceof FreeVariable freeVariable) {
            Expression defined = globals.get(freeVariable.name);
            if (defined != null)
                return expand(defined, globals, lambdaBind);
            return expression;
        } else if (expression instanceof Application application) {
            return Application.of(expand(application.head, globals, lambdaBind),
                expand(application.tail, globals, lambdaBind));
        } else
            throw new RuntimeException("Unknown expression type:" + expression);
    }

    Map<String, Expression> globals = new HashMap<>();

    void define(String name, String body) {
        globals.put(name, parse(body));
    }

    @Test
    void testExpand() {
        define("0", "λf x.x");
        define("succ", "λn f x.f(n f x)");
        System.out.println(expand(parse("succ 0"), globals));
    }

}
