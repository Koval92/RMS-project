package pathfinder;

import java.awt.*;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class MoveCostCalculator {
    CostFunctionType type;

    public MoveCostCalculator(CostFunctionType type) {
        this.type = type;
    }

    public static double distance(Point p1, Point p2) {
        return Point.distance(p1.x, p1.y, p2.x, p2.y);
    }

    public static double time(Point p1, Point p2) {
        return max(abs(p1.x - p2.x), abs(p1.y - p2.y));
    }

    public static double energy(Point p1, Point p2) {
        return abs(p1.x - p2.x) + abs(p1.y - p2.y);
    }

    public static boolean arePointsAdjacent(Point p1, Point p2, CostFunctionType costType) {
        return !(calculate(p1, p2, costType) > 1);
        //return !(Math.abs(p1.x - p2.x) > 1 || Math.abs(p1.y - p2.y) > 1);
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

    public static boolean arePointsAdjacent(int x1, int y1, int x2, int y2, CostFunctionType costType) {
        return arePointsAdjacent(new Point(x1, y1), new Point(x2, y2), costType);
    }

    public static double calculate(Point p1, Point p2, CostFunctionType type) {
        switch (type) {
            case DISTANCE:
                return distance(p1, p2);
            case TIME:
                return time(p1, p2);
            case ENERGY:
                return energy(p1, p2);
            default:
                return distance(p1, p2);
        }
    }

    public static double calculate(int x1, int y1, int x2, int y2, CostFunctionType type) {
        return calculate(new Point(x1, y1), new Point(x2, y2), type);
    }

    public static double calculate(List<Point> route, CostFunctionType type) {
        if (route == null || route.size() == 0) return 0;
        double cost = 0;
        Point previous = route.get(0);
        for (Point current : route) {
            cost += calculate(previous, current, type);
            previous = current;
        }
        return cost;
    }

    public double calculate(Point p1, Point p2) {
        return MoveCostCalculator.calculate(p1, p2, type);
    }

    public double calculate(List<Point> route) {
        return MoveCostCalculator.calculate(route, type);
    }
}
