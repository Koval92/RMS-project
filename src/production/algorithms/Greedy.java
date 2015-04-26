package production.algorithms;

import production.PathPlanner;
import production.algorithms.route.PrintingTool;

import java.awt.*;

public class Greedy extends PathPlanner {

    PrintingTool printingTool;

    @Override
    protected void setUp() {
        printingTool = new PrintingTool(connection);
    }

    @Override
    protected java.util.List<Point> planPath() {
        findStartingPoint();
        while (printingTool.getNumberOfPoints() > 0)
            findNextPoint();
        return printingTool.getRoute();
    }

    private void findStartingPoint() {
        printingTool.getFirstPoint();
        printingTool.print();
    }

    private void findNextPoint() {
        Point currentPoint = printingTool.getCurrentPosition();
        Point nextPoint;
        int distance;

        Point bestPoint = printingTool.getPointFromList(0);
        int currentDistance = calcDistance(currentPoint, bestPoint);
        if (currentDistance == 1) {
            printingTool.print(bestPoint);
            return;
        }
        for (int i = 1; i < printingTool.getNumberOfPoints(); i++) {
            nextPoint = printingTool.getPointFromList(i);
            distance = calcDistance(currentPoint, nextPoint);
            if (distance < currentDistance) {
                bestPoint = nextPoint;
                currentDistance = distance;
                if (currentDistance == 1) {
                    printingTool.print(bestPoint);
                    return;
                }
            }
        }
        printingTool.print(bestPoint);
    }

    private int calcDistance(Point first, Point second) {
        return (int) Math.sqrt(Math.pow(second.getX() - first.getX(), 2) + Math.pow(second.getY() - first.getY(), 2));
    }

    @Override
    protected String getName() {
        return "Greedy";
    }
}
