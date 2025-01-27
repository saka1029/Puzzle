package puzzle.graphics;

import java.awt.Color;
import java.io.Closeable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 座標系はX座標が右方向、Y座標が下方向です。
 * <pre>
 *    |
 * ---+--- X
 *    |
 *    Y
 */
public abstract class Turtle implements Closeable {

    private final int width, height;
    private double x = 0, y = 0, step = 5;
    private double angle = 90, direction = 0;
    private int penWidth = 1;
    private Color penColor = Color.BLACK;
    private boolean writing = true;
    private Deque<Double> stack = new LinkedList<>();

    public Turtle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static int round(double value) {
        return (int)Math.round(value);
    }

    public double nextX(double step) {
        return x + step * Math.cos(Math.toRadians(direction));
    }

    public double nextY(double step) {
        return y + step * Math.sin(Math.toRadians(direction));
    }

    public int width() { return width; }
    public int height() { return height; }
    public double x() { return x; }
    public double y() { return y; }
    public void position(double x, double y) { this.x = x; this.y = y; }
    public abstract void forward(double step);
    public void forward() { forward(step); }
    public void backward(double step) { forward(-step); }
    public void backward() { backward(step); }
    public void step(double step) { this.step = step; }
    public double step() { return step; }
    public double direction() { return direction; }
    public void direction(double degree) { this.direction = degree; }
    public void angle(double degree) { this.angle = degree; }
    public double angle() { return angle; }
    public boolean writing() { return writing; }
    public void writing(boolean writing) { this.writing = writing; }
    public void penDown() { writing(true); }
    public void penUp() { writing(false); }
    public int penWidth() { return penWidth; }
    public void penWidth(int penWidth) { this.penWidth = penWidth; }
    public Color penColor() { return penColor; }
    public void penColor(Color penColor) { this.penColor = penColor; }
    public void rotate(double angle) { direction += angle; }
    public void rotate() { rotate(this.angle); }
    public void left(double angle) { rotate(-angle); }
    public void left() { left(this.angle); }
    public void right(double angle) { rotate(angle); }
    public void right() { right(this.angle); }
    public void push() { stack.push(x); stack.push(y); stack.push(direction); }
    public void pop() { direction = stack.pop(); y = stack.pop(); x = stack.pop(); }

    public static String lsystem(String start, int level, Map<String, String> rules) {
        StringBuilder in = new StringBuilder(start);
        for (int i = 0; i < level; ++i) {
            StringBuilder out = new StringBuilder();
            for (int p = 0, size = in.length(); p < size; ++p) {
                String key = Character.toString(in.charAt(p));
                out.append(rules.getOrDefault(key, key));
            }
            in = out;
        }
        return in.toString();
    }

    public void run(String s, Map<String, Consumer<Turtle>> commands) {
        s.chars()
            .mapToObj(Character::toString)
            .forEach(c -> commands.getOrDefault(c, t -> {}).accept(this));
    }

    static final Pattern COMMAND_SEQUENCE = Pattern.compile("(.)\\1*");

    public void run2(String s, Map<String, BiConsumer<Turtle, Integer>> commands) {
        Matcher m = COMMAND_SEQUENCE.matcher(s);
        while (m.find()) {
            String c = m.group();
            BiConsumer<Turtle, Integer> command = commands.get(c.substring(0, 1));
            if (command != null)
                command.accept(this, c.length());
        }
    }

    public void lsystem(String start, int level, Map<String, String> rules, Map<String, Consumer<Turtle>> commands) {
        run(lsystem(start, level, rules), commands);
    }

    public void lsystem2(String start, int level, Map<String, String> rules, Map<String, BiConsumer<Turtle, Integer>> commands) {
        run2(lsystem(start, level, rules), commands);
    }
}
