package test.puzzle.fractal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Map;

public class TurtleGraphics {
    
    public final Graphics2D g;
    public Point current = Point.of(0, 0), unit = Point.of(0, 5);
    public boolean penDown = true;
    
    public TurtleGraphics(Graphics2D g) {
        this.g = g;
    }
    
    public void penDown() { penDown = true; }
    public void penUp() { penDown = false; }
    public void color(Color c) { g.setColor(c); }
    public Color color() { return g.getColor(); }
    
    public void go(Point x) { current = x; }
    public void unit(Point x) { unit = x; }

    public void forward() {
        Point next = current.add(unit);
        if (penDown)
            g.drawLine((int)current.x, (int)current.y, (int)next.x, (int)next.y);
        current = next;
    }
    
    public void rotate(int degree) {
        unit = unit.rotate(degree);
    }
    
    public void run(String input, Map<String, Runnable> actions) {
        for (char c : input.toCharArray())
            actions.get(Character.toString(c)).run();
    }

}
