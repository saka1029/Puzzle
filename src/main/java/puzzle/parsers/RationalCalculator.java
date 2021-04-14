package puzzle.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import puzzle.Rational;

public class RationalCalculator extends Parser {

    private RationalCalculator(BufferedReader reader) throws IOException {
        super(reader);
    }

    public static Rational calc(String source) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(source))) {
            return new RationalCalculator(reader).parse();
        }
    }

    Rational paren() throws IOException {
        Rational v = expression();
        if (!eat(')')) error("')' expected");
        return v;
    }

    Rational number() throws IOException {
        StringBuilder number = new StringBuilder();
        while (Character.isDigit(ch))
            appendGet(number, ch);
        return Rational.parseRational(number.toString());
    }

    Rational factor() throws IOException {
        Rational sign = Rational.ONE;
        if (eat('+')) sign = Rational.ONE;
        else if (eat('-')) sign = Rational.MINUS_ONE;
        if (eat('('))
            return sign.multiply(paren());
        else if (Character.isDigit(ch))
            return sign.multiply(number());
        error("unexpected character '%c'", (char)ch);
        return null; // never reach here
    }

    Rational term() throws IOException {
        Rational v = factor();
        while (true)
            if (eat('*')) v = v.multiply(factor());
            else if (eat('/')) v = v.divide(factor());
            else break;
        return v;
    }

    Rational expression() throws IOException {
        Rational v = term();
        while (true)
            if (eat('+')) v = v.add(term());
            else if (eat('-')) v = v.subtract(term());
            else break;
        return v;
    }

    public Rational parse() throws IOException {
        Rational v = expression();
        if (ch != EOF)
            error("extra character '%c'", (char)ch);
        return v;
    }
}
