package test.puzzle.fractal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Test;

import puzzle.fractal.ImageWriter;

public class TestImageWriter {

    static class ExPolygon {
        public final Polygon origin;
        public final Polygon mirror;

        public ExPolygon(Polygon origin) {
            this.origin = origin;
            this.mirror = mirror(origin);
        }

        public boolean contains(int x, int y) {
            return origin.contains(x, y)
                || onVertex(origin, x, y)
                || mirror.contains(-x, -y);
        }

        static Polygon mirror(Polygon p) {
            int npoints = p.npoints;
            int[] xpoints = new int[npoints];
            int[] ypoints = new int[npoints];
            for (int i = 0; i < npoints; ++i) {
                xpoints[i] = -p.xpoints[i];
                ypoints[i] = -p.ypoints[i];
            }
            return new Polygon(xpoints, ypoints, npoints);
        }

        static boolean onVertex(Polygon p, int x, int y) {
            int npoints = p.npoints;
            for (int i = 0; i < npoints; ++i)
                if (p.xpoints[i] == x && p.ypoints[i] == y)
                    return true;
            return false;
        }

        static boolean contains(Polygon p, int x, int y) {
            return p.contains(x, y)
                || onVertex(p, x, y)
                || mirror(p).contains(-x, -y);
        }

    }

    static Polygon mirror(Polygon p) {
        int npoints = p.npoints;
        int[] xpoints = new int[npoints];
        int[] ypoints = new int[npoints];
        for (int i = 0; i < npoints; ++i) {
            xpoints[i] = -p.xpoints[i];
            ypoints[i] = -p.ypoints[i];
        }
        return new Polygon(xpoints, ypoints, npoints);
    }

    static boolean onVertex(Polygon p, int x, int y) {
        int npoints = p.npoints;
        for (int i = 0; i < npoints; ++i)
            if (p.xpoints[i] == x && p.ypoints[i] == y)
                return true;
        return false;
    }

    static boolean contains(Polygon p, int x, int y) {
        return p.contains(x, y)
            || onVertex(p, x, y)
            || mirror(p).contains(-x, -y);
    }

//    @Test
    public void testImageWriter() throws IOException {
        int width = 100, height = 100;
        // int[] xs = {20, 80, 80, 50, 50, 60, 60, 40, 40, 80, 80, 20};
        // int[] ys = {20, 20, 50, 50, 40, 40, 30, 30, 60, 60, 70, 70};
        // 穴あき四角形
        // int[] xs = {20, 80, 80, 20, 20, 40, 60, 60, 40, 40};
        // int[] ys = {20, 20, 80, 80, 20, 40, 40, 60, 60, 40};
        // 正五角形
        // int[] xr = {0, 38, 23, -23, -38};
        // int[] yr = {40, 12, -32, -32, 12};
        // 五芒星
        int[] xr = {0, 23, -38, 38, -23};
        int[] yr = {40, -32, 12, 12, -32};
        int[] xs = Arrays.stream(xr).map(x -> x + 50).toArray();
        int[] ys = Arrays.stream(yr).map(y -> y + 50).toArray();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Polygon p = new Polygon(xs, ys, xs.length);
        // ExPolygon ex = new ExPolygon(p);
        Graphics2D g = image.createGraphics();
        try (Closeable c = () -> g.dispose()) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.BLACK);
            g.drawPolygon(p);
            g.setColor(Color.RED);
            for (int x = 0; x < width; ++x)
                for (int y = 0; y < height; ++y)
                    if (contains(p, x, y))
                        // if (p.contains(x, y))
                        g.fillRect(x, y, 1, 1);
        }
        ImageIO.write(image, "png", new File("data/testPolygon.png"));
    }

    @Test
    public void AndroidAnimation() throws IOException {
        int circleCount = 20;
        double[] sin = new double[circleCount];
        double[] cos = new double[circleCount];
        for (int i = 0; i < circleCount; ++i) {
            int subCircles = i + 1;
            double angleSegment = Math.PI * 2 / subCircles;
            sin[i] = Math.sin(angleSegment);
            cos[i] = Math.cos(angleSegment);
        }
        System.out.println(Arrays.toString(sin));
        System.out.println(Arrays.toString(cos));
        int width = 1000, height = 1000;
        Point center = new Point(width / 2, height / 2);
        int small = 6;
        int mPaint = -1;
        int pointPaint = -1;
        double shiftAngle = 0;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D canvas = image.createGraphics();
        try (Closeable c = () -> canvas.dispose()) {
            canvas.setColor(Color.WHITE);
            canvas.fillRect(0, 0, width, height);
            canvas.setColor(Color.BLACK);
            int startRadius = 20;
            int endRadius = 400;
            int circleDistance = (int) (((endRadius - startRadius) / (float) circleCount));

            for (int i = 0; i < circleCount; i++) {
                int radius = startRadius + i * circleDistance;
//                canvas.drawCircle(center.x, center.y, radius, mPaint);
                canvas.drawOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
                int subCircles = i + 1;
                double angleSegment = Math.PI * 2 / subCircles;
                for (int segment = 0; segment < subCircles; segment++) {
                    double angle = angleSegment * segment + shiftAngle;
                    double x = Math.cos(angle) * radius;
                    double y = Math.sin(angle) * radius;
//                    canvas.drawCircle((float) (x + center.x), (float) (y + center.y), 6, pointPaint);
                    canvas.fillOval((int) (x + center.x) - small, (int) (y + center.y) - small, small * 2, small * 2);
                }
            }
        }
        ImageIO.write(image, "png", new File("data/androidAnimation.png"));
    }

    static void draw(Graphics2D g, int[][] p) {
        for (int i = 0; i < p.length; i += 4) {
            int[] x = new int[4];
            int[] y = new int[4];
            for (int j = 0; j < 4; ++j) {
                x[j] = p[i + j][0];
                y[j] = p[i + j][1];
            }
            g.drawPolygon(x, y, 4);
        }
    }

    @Test
    public void testStackoverflow() throws IOException {
        ImageWriter image = new ImageWriter(120, 120);
//        int[] x = {38, 100, 80, 18};
//        int[] y = {50, 50, 100, 100};
        int[] x = {38, 104, 84, 18};
        int[] y = {42, 42, 96, 96};
        Polygon poly = new Polygon(x, y, 4);
        Graphics2D g = image.graphics;
        g.drawPolygon(x, y, 4);
        int[][] d4 = {
          {38,42}, {71,42}, {51,69}, {18,69},
          {71,42}, {104,42}, {84,69}, {51,69},
          {38,69}, {71,69}, {51,96}, {18,96},
          {71,69}, {104,69}, {84,96}, {51,96},
        };
        int[][] d9 = {
          {38,42}, {60,42}, {40,60}, {18,60},
          {60,42}, {82,42}, {62,60}, {40,60},
          {82,42}, {104,42}, {84,60}, {62,60},
          {38,60}, {60,60}, {40,78}, {18,78},
          {60,60}, {82,60}, {62,78}, {40,78},
          {82,60}, {104,60}, {84,78}, {62,78},
          {38,78}, {60,78}, {40,96}, {18,96},
          {60,78}, {82,78}, {62,96}, {40,96},
          {82,78}, {104,78}, {84,96}, {62,96},
        };
        int[][] d36 = {
          {38,42}, {49,42}, {29,51}, {18,51},
          {49,42}, {60,42}, {40,51}, {29,51},
          {60,42}, {71,42}, {51,51}, {40,51},
          {71,42}, {82,42}, {62,51}, {51,51},
          {82,42}, {93,42}, {73,51}, {62,51},
          {93,42}, {104,42}, {84,51}, {73,51},
          {38,51}, {49,51}, {29,60}, {18,60},
          {49,51}, {60,51}, {40,60}, {29,60},
          {60,51}, {71,51}, {51,60}, {40,60},
          {71,51}, {82,51}, {62,60}, {51,60},
          {82,51}, {93,51}, {73,60}, {62,60},
          {93,51}, {104,51}, {84,60}, {73,60},
          {38,60}, {49,60}, {29,69}, {18,69},
          {49,60}, {60,60}, {40,69}, {29,69},
          {60,60}, {71,60}, {51,69}, {40,69},
          {71,60}, {82,60}, {62,69}, {51,69},
          {82,60}, {93,60}, {73,69}, {62,69},
          {93,60}, {104,60}, {84,69}, {73,69},
          {38,69}, {49,69}, {29,78}, {18,78},
          {49,69}, {60,69}, {40,78}, {29,78},
          {60,69}, {71,69}, {51,78}, {40,78},
          {71,69}, {82,69}, {62,78}, {51,78},
          {82,69}, {93,69}, {73,78}, {62,78},
          {93,69}, {104,69}, {84,78}, {73,78},
          {38,78}, {49,78}, {29,87}, {18,87},
          {49,78}, {60,78}, {40,87}, {29,87},
          {60,78}, {71,78}, {51,87}, {40,87},
          {71,78}, {82,78}, {62,87}, {51,87},
          {82,78}, {93,78}, {73,87}, {62,87},
          {93,78}, {104,78}, {84,87}, {73,87},
          {38,87}, {49,87}, {29,96}, {18,96},
          {49,87}, {60,87}, {40,96}, {29,96},
          {60,87}, {71,87}, {51,96}, {40,96},
          {71,87}, {82,87}, {62,96}, {51,96},
          {82,87}, {93,87}, {73,96}, {62,96},
          {93,87}, {104,87}, {84,96}, {73,96},
        };
        g.setColor(Color.RED);
        draw(g, d36);
        image.writeTo(new File("data/four.png"));
    }

}
