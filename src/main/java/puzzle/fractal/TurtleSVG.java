package puzzle.fractal;

import java.awt.Color;
import java.io.Closeable;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class TurtleSVG implements Closeable {

    static final Color BACKGROUND_COLOR = Color.WHITE;
    static final String LINE_CAP = "round";

    final int width, height;
    final PrintWriter writer;
    final List<String> paths = new ArrayList<>();
    final StringBuilder path = new StringBuilder();
    final Deque<Double> stack = new LinkedList<>();
    private int penWidth = 2;
    public boolean penDown = true;
    private Color penColor = Color.black;
    public double x = 0, y = 0;
    public double step = 5;
    public double angle = 90;
    public double direction = 0;

    public TurtleSVG(int width, int height, Writer writer) {
        this.width = width;
        this.height = height;
        this.writer = new PrintWriter(writer);
    }

    String color(Color c) {
        return "#%06x".formatted(c.getRGB() & 0xffffff);
    }

    int round(double d) {
        return (int)Math.round(d);
    }

    public void forward(double step) {
        double x1 = x + step * Math.cos(Math.toRadians(direction));
        double y1 = y + step * Math.sin(Math.toRadians(direction));
        if (penDown) {
            if (path.length() == 0)
                path.append("<path fill='none' stroke='%s' stroke-width='%d' stroke-linecap='%s' d='M %d %d"
                    .formatted(color(penColor), penWidth, LINE_CAP, round(x), round(y)));
            path.append(" L %d %d".formatted(round(x1), round(y1)));
        }
        x = x1;
        y = y1;
    }

    public void forward() {
        forward(step);
    }

    public void rotate(double angle) {
        direction = (direction + angle) % 360;
    }

    public void rotate() {
        rotate(angle);
    }

    public void left(double angle) {
        rotate(angle);
    }

    public void left() {
        rotate(angle);
    }

    public void right(double angle) {
        rotate(-angle);
    }

    public void right() {
        rotate(-angle);
    }

    void endPath() {
        if (path.length() > 0) {
            path.append("' />");
            paths.add(path.toString());
            path.setLength(0);
        }
    }

    public void push() {
//        endPath();
        stack.push(x);
        stack.push(y);
        stack.push(direction);
        path.append(" M %d %d".formatted(round(x), round(y)));
    }

    public void pop() {
//        endPath();
        direction = stack.pop();
        y = stack.pop();
        x = stack.pop();
        path.append(" M %d %d".formatted(round(x), round(y)));
    }

    public void penColor(Color c) {
        endPath();
        penColor = c;
    }

    public void penWidth(int n) {
        endPath();
        penWidth = n;
    }

    @Override
    public void close() {
        writer.printf("<?xml version=\"1.0\"?>%n");
        writer.printf("<svg xmlns='http://www.w3.org/2000/svg'");
        writer.printf(" x='0' y='0' width='%d' height='%d' style='background-color:%s'>%n",
            width, height, color(BACKGROUND_COLOR));
        endPath();
        for (String p : paths)
            writer.printf("    %s%n", p);
        writer.printf("</svg>%n");
    }

}
