package test.puzzle.fractal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

import puzzle.fractal.Point;

public class TurtleGraphics {

    public final Graphics2D g;
    public Point position = Point.of(0, 0);
    public int direction = 0;
    public int step = 5;
    public boolean penDown = true;
    public Color color = Color.BLACK;
    public Deque<Point> positionStack = new LinkedList<>();
    public Deque<Integer> directionStack = new LinkedList<>();

    public TurtleGraphics(Graphics2D g) {
        this.g = g;
    }

    public void reset(Point position, int direction, int step) {
        this.position = position;
        this.direction = direction;
        positionStack.clear();
        directionStack.clear();
        penDown = true;
    }

    public void forward() {
        Point next = position.add(
            Point.of(step * Math.cos(Math.toRadians(direction)),
                step * Math.sin(Math.toRadians(direction))));
        if (penDown) {
            g.setColor(color);
            g.drawLine((int)position.x, (int)position.y, (int)next.x, (int)next.y);
        }
        position = next;
    }

    public void rotate(int degree) {
        direction = (direction + degree) % 360;
    }

    public void push() {
        positionStack.push(position);
        directionStack.push(direction);
    }

    public void pop() {
        position = positionStack.pop();
        direction = directionStack.pop();
    }

    public void run(String input, Map<String, Runnable> actions) {
        for (char c : input.toCharArray())
            actions.get(Character.toString(c)).run();
    }
}
