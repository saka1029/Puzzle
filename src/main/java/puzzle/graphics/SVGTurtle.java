package puzzle.graphics;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SVGTurtle extends Turtle {

    static final Color BACKGROUND_COLOR = Color.WHITE;
    static final String LINE_CAP = "round";

    final PrintWriter writer;
    final List<String> paths = new ArrayList<>();
    final StringBuilder path = new StringBuilder();
    double prevX = -1, prevY = -1;
    Color prevPenColor = null;
    double prevPenWidth = -1;

    public SVGTurtle(Writer writer, int width, int height) {
        super(width, height);
        this.writer = new PrintWriter(writer);
    }

    static String color(Color color) {
        return "#%06x".formatted(color.getRGB() & 0xFFFFFF);
    }

    void endPath() {
        if (path.length() > 0) {
            path.append("' />");
            paths.add(path.toString());
            path.setLength(0);
        }
    }

    @Override
    public void forward(double step) {
        double x = x(), y = y();
        double x1 = nextX(step), y1 = nextY(step);
        if (writing()) {
            if (path.length() == 0 || penWidth() != prevPenWidth || !penColor().equals(prevPenColor)) {
                endPath();
                path.append("<path fill='none' stroke='%s' stroke-width='%d' stroke-linecap='%s' d='M%d %d"
                    .formatted(color(penColor()), penWidth(), LINE_CAP, round(x), round(y)));
            } else if (round(x) != round(prevX) || round(y) != round(prevY))
                path.append(" M%d %d".formatted(round(x), round(y)));
            path.append(" L%d %d".formatted(round(x1), round(y1)));
            prevX = x1; prevY = y1;
            prevPenColor = penColor();
            prevPenWidth = penWidth();
        }
        position(x1, y1);
    }

    @Override
    public void close() throws IOException {
        endPath();
        writer.printf("<?xml version=\"1.0\"?>%n");
        writer.printf("<svg xmlns='http://www.w3.org/2000/svg'");
        writer.printf(" x='0' y='0' width='%d' height='%d' style='background-color:%s'>%n",
            width(), height(), color(BACKGROUND_COLOR));
        for (String path : paths)
            writer.printf("    %s%n", path);
        writer.printf("</svg>%n");
    }
}
