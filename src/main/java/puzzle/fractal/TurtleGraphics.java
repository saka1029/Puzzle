package puzzle.fractal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class TurtleGraphics {

    public final Graphics2D g;
    public Point position = Point.of(0, 0);
    public double direction = 0;
    public double step = 5;
    public double rotation = 90;
    public double width = 1;
    public boolean penDown = true;
    public Color color = Color.BLACK;
    public Deque<Point> positionStack = new LinkedList<>();
    public Deque<Double> directionStack = new LinkedList<>();

    public TurtleGraphics(Graphics2D g) {
        this.g = g;
    }

    public void forward(double step) {
        this.step = step;
        forward();
    }

    public void forward() {
        Point next = position.add(
            Point.of(step * Math.cos(Math.toRadians(direction)),
                step * Math.sin(Math.toRadians(direction))));
        if (penDown) {
            g.setStroke(new BasicStroke((int)width));
            g.setColor(color);
            g.drawLine((int)position.x, (int)position.y, (int)next.x, (int)next.y);
        }
        position = next;
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
        positionStack.push(position);
        directionStack.push(direction);
    }

    public void pop() {
        position = positionStack.pop();
        direction = directionStack.pop();
    }

    public void crearStack() {
        positionStack.clear();
        directionStack.clear();
    }

    public void run(String input, Map<String, Runnable> actions) {
        for (char c : input.toCharArray())
            actions.get(Character.toString(c)).run();
    }
}
