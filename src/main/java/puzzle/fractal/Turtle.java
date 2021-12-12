package puzzle.fractal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Turtle {

    public final Graphics2D graphics;
    public int x = 0, y = 0;
    public int angle = 0;
    public boolean penDown = true;
    public int penWidth = 1;
    public Color penColor = Color.BLACK;

    public Turtle(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void forward(int distance) {
        int x1 = (int) Math.round(x + Math.cos(Math.toRadians(angle)) * distance);
        int y1 = (int) Math.round(y + Math.sin(Math.toRadians(angle)) * distance);
        if (penDown) {
            graphics.setColor(penColor);
            graphics.setStroke(new BasicStroke(penWidth));
            graphics.drawLine(x, y, x1, y1);
        }
        x = x1;
        y = y1;
    }

    public void rotate(int angle) {
        this.angle = (this.angle + angle) % 360;
    }

    public void run(String commands) {
        String lower = commands.toLowerCase();
        new Object() {
            int length = lower.length(), index = 0;
            int ch = get();

            int get() {
                return ch = index < length ? lower.charAt(index++) : -1;
            }

            void spaces() {
                while (Character.isWhitespace(ch))
                    get();
            }

            boolean match(int expect) {
                spaces();
                if (ch == expect) {
                    get();
                    return true;
                }
                return false;
            }

            boolean isHexadecimal(int ch) {
                if (Character.isDigit(ch))
                    return true;
                ch = Character.toLowerCase(ch);
                return ch >= 'a' && ch <= 'f';
            }

            int number() {
                int sign = 1;
                if (match('+'))
                    sign = 1;
                else if (match('-'))
                    sign = -1;
                StringBuilder n = new StringBuilder();
                if (match('#')) {
                    while (isHexadecimal(ch)) {
                        n.append((char) ch);
                        get();
                    }
                    if (!match('#'))
                        throw new RuntimeException("'#' expected");
                    return sign * Integer.parseInt(n.toString(), 16);
                } else {
                    while (Character.isDigit(ch)) {
                        n.append((char) ch);
                        get();
                    }
                    if (n.length() <= 0)
                        return sign;
                    return sign * Integer.parseInt(n.toString());
                }
            }

            Runnable factor() {
                int n = number();
                Runnable r;
                if (match('f'))
                    r = () -> forward(n);
                else if (match('r'))
                    r = () -> rotate(n);
                else if (match('x'))
                    r = () -> x = n;
                else if (match('y'))
                    r = () -> y = n;
                else if (match('a'))
                    r = () -> angle = n;
                else if (match('w'))
                    r = () -> penWidth = n;
                else if (match('c'))
                    r = () -> penColor = new Color(n);
                else if (match('p'))
                    r = n == 0 ? () -> penDown = false : () -> penDown = true;
                else if (match('(')) {
                    Runnable e = expression();
                    r = () -> {
                        for (int i = 0; i < n; ++i)
                            e.run();
                    };
                    if (!match(')'))
                        throw new RuntimeException("')' expected");
                } else
                    throw new RuntimeException("unknown command: " + (char) ch);
                return r;
            }

            Runnable expression() {
                Runnable r = factor();
                while (ch != -1 && ch != ')') {
                    Runnable a = r, b = factor();
                    r = () -> {
                        a.run();
                        b.run();
                    };
                }
                return r;
            }
        }.expression().run();
    }
}
