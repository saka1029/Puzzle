package test.puzzle;

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

}
