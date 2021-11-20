package puzzle.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TruthTable {

    @FunctionalInterface
    interface Bool {
        boolean get();
        default Bool and(Bool r) { return () -> get() ? r.get() : false; }
        default Bool or(Bool r) { return () -> get() ? true : r.get(); }
        default Bool not() { return () -> !get(); }
    }

    String formula;
    int index, ch;
    List<Character> variables;
    Map<Character, Boolean> map;
    Bool bool;

    int get() {
        return ch = index < formula.length() ? formula.charAt(index++) : -1;
    }

    boolean match(int... expects) {
        for (int expect : expects)
            if (ch == expect) {
                get();
                return true;
            }
        return false;
    }

    Bool element() {
        Bool b;
        if (match('(')) {
            b = expression();
            if (!match(')'))
                throw new RuntimeException("')' expected");
        } else if (Character.isAlphabetic(ch)) {
            char v = (char) ch;
            get();
            if (!variables.contains(v))
                variables.add(v);
            b = () -> map.get(v);
        } else
            throw new RuntimeException("unknown char: " + (char) ch);
        return b;
    }

    Bool factor() {
        if (match('￢', '¬', '!'))
            return element().not();
        return element();
    }

    Bool term() {
        Bool b = factor();
        while (match('∧', '&'))
            b = b.and(factor());
        return b;
    }

    Bool expression() {
        Bool b = term();
        while (match('∨', '|'))
            b = b.or(term());
        return b;
    }

    String str(boolean b) {
        return b ? "T" : "F";
    }

    void print() {
        for (char v : variables)
            System.out.print(str(map.get(v)) + " ");
        System.out.println(str(bool.get()));
    }

    void test(int i) {
        if (i >= variables.size())
            print();
        else {
            char c = variables.get(i);
            map.put(c, true);
            test(i + 1);
            map.put(c, false);
            test(i + 1);
        }
    }

    void make(String formula) {
        this.formula = formula.replaceAll("\\s", "");
        index = 0;
        variables = new ArrayList<>();
        map = new HashMap<>();
        get();
        bool = expression();
        if (ch != -1)
            throw new RuntimeException(
                "extra string '" + formula.substring(index - 1) + "'");
        for (char v : variables)
            System.out.print(v + " ");
        System.out.println(formula);
        test(0);
        System.out.println();
    }

    public static void print(String formula) {
        new TruthTable().make(formula);
    }

    public static void main(String[] args) {
        TruthTable.print("!A");
        TruthTable.print("!A & B");
        TruthTable.print("(!A & B) | C");
        TruthTable.print("((!A & B) | C) & A");
        TruthTable.print("!A & B | C & A");
    }
}
