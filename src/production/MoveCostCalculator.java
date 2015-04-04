package production;

import java.awt.*;
import java.awt.geom.Point2D;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class MoveCostCalculator {
    public static double distance(Point p1, Point p2) {
        new Point2D.Double();
        return Point2D.distance(p1.x, p1.y, p2.x, p2.y);
    }

    public static double time(Point p1, Point p2) {
        return max(abs(p1.x - p2.x), abs(p1.y - p2.y));
    }

    public static double energy(Point p1, Point p2) {
        return abs(p1.x - p2.x) + abs(p1.y - p2.y);
    }

    public static boolean arePointsAdjacent(Point p1, Point p2) {
        return !(Math.abs(p1.x - p2.x) > 1 || Math.abs(p1.y - p2.y) > 1);
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        return distance(new Point(x1, y1), new Point(x2, y2));
    }

    public static double time(int x1, int y1, int x2, int y2) {
        return time(new Point(x1, y1), new Point(x2, y2));
    }

    public static double energy(int x1, int y1, int x2, int y2) {
        return energy(new Point(x1, y1), new Point(x2, y2));
    }

    public static boolean arePointsAdjacent(int x1, int y1, int x2, int y2) {
        return arePointsAdjacent(new Point(x1, y1), new Point(x2, y2));
    }
}
