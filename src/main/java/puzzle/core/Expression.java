package puzzle.core;

import java.util.Map;

/**
 * 1st version
 * expression = [ "-" ] term { ("+" | "-") term }
 * term       = element { ( "*" | "/" ) element }
 * element    = "(" expression ")" | variable | number
 * 
 * expression = [ "-" ] term { ("+" | "-") term }
 * term       = factor { ("*" | "/") factor }
 * factor     = primary [ "^" factor ]
 * primary    = primary | element
 * element    = "(" expression ")" | variable | number
 */
public interface Expression {
    double eval(Map<String, Double> variables);
}
