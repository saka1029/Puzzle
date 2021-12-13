package puzzle.fractal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class TurtleGraphics {

    public final Graphics2D g;
    public double x = 0;
    public double y = 0;
    public double direction = 0;
    public double step = 5;
    public double rotation = 90;
    public double width = 1;
    public boolean penDown = true;
    public Color color = Color.BLACK;
    public Deque<Double> stack = new LinkedList<>();

    public TurtleGraphics(Graphics2D g) {
        this.g = g;
    }

    public void forward(double step) {
        this.step = step;
        forward();
    }

    int round(double d) {
        return (int)Math.round(d);
    }

    public void forward() {
        double x1 = x + step * Math.cos(Math.toRadians(direction));
        double y1 = y + step * Math.sin(Math.toRadians(direction));
        if (penDown) {
            g.setStroke(new BasicStroke(round(width)));
            g.setColor(color);
            g.drawLine(round(x), round(y), round(x1), round(y1));
        }
        x = x1;
        y = y1;
    }

    public void rotate(double degree) {
        rotation = degree;
        left();
    }

    public void left() {
        direction = (direction + rotation) % 360;
    }

    public void right() {
        direction = (direction - rotation) % 360;
    }

    public void push() {
        stack.push(x);
        stack.push(y);
        stack.push(direction);
    }

    public void pop() {
        direction = stack.pop();
        y = stack.pop();
        x = stack.pop();
    }

    public void crearStack() {
        stack.clear();
    }

    public void run(String input, Map<String, Runnable> actions) {
        for (char c : input.toCharArray())
            actions.get(Character.toString(c)).run();
    }
}
