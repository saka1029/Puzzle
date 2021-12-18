package puzzle.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

public class ImageTurtle extends Turtle {

    public final Graphics2D graphics;
    int prevPenWidth = -1;
    Color prevPenColor;

    public ImageTurtle(Graphics2D graphics, int width, int height) {
        super(width, height);
        this.graphics = graphics;
        this.graphics.setColor(Color.WHITE);
        this.graphics.fillRect(0, 0, width, height);
        this.graphics.setColor(Color.BLACK);
        this.prevPenColor = Color.BLACK;
    }

    public ImageTurtle(ImageWriter iw) {
        this(iw.graphics(), iw.width, iw.height);
    }

    @Override
    public void close() throws IOException {
        /* do nothing */
    }

    @Override
    public void forward(double step) {
        double x1 = nextX(step), y1 = nextY(step);
        if (writing()) {
            if (penWidth() != prevPenWidth)
                graphics.setStroke(new BasicStroke(penWidth()));
            if (!penColor().equals(prevPenColor))
                graphics.setColor(penColor());
            graphics.drawLine(round(x()), round(y()), round(x1), round(y1));
            prevPenColor = penColor();
            prevPenWidth = penWidth();
        }
        position(x1, y1);
    }
}
