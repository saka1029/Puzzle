package test.puzzle.fractal;

public class Point {
    
    public final double x, y;
    
    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public static Point of(double x, double y) {
        return new Point(x, y);
    }
    
    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }
    
    public Point divide(double d) {
        return new Point(x / d, y / d);
    }
    
    public Point rotate(int degree) {
        double radian = Math.toRadians(degree);
        double cos = Math.cos(radian), sin = Math.sin(radian);
        return new Point(x * cos - y * sin, x * sin + y * cos);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
