package production.algorithms;

import production.MoveCostCalculator;
import production.PathPlanner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Snake extends PathPlanner {
    boolean[][] layer;
    List<Point> route;

    @Override
    protected List<Point> planPath() {
        Point currentPosition = new Point(0, 0);
        MoveCostCalculator costCalculator = new MoveCostCalculator(connection.getCostFunctionType());

        for (int rowIndex = 0; rowIndex < layer.length; rowIndex++) {
            Point leftEnd = findFirstInRow(rowIndex);

            if (leftEnd != null) {
                Point rightEnd = findLastInRow(rowIndex);

                double distanceToLeftEnd = costCalculator.calculate(currentPosition, leftEnd);
                double distanceToRightEnd = costCalculator.calculate(currentPosition, rightEnd);
                if (distanceToLeftEnd < distanceToRightEnd) {
                    currentPosition = fillFromLeft(leftEnd, rightEnd, rowIndex);
                } else {
                    currentPosition = fillFromRight(leftEnd, rightEnd, rowIndex);
                }
            }
        }
        return route;
    }

    private Point findFirstInRow(int i) {
        Point firstInRow;
        firstInRow = null;
        for (int j = 0; j < layer[i].length; j++) {
            if (layer[i][j]) {
                firstInRow = new Point(i, j);
                break;
            }
        }
        return firstInRow;
    }

    private Point findLastInRow(int i) {
        Point lastInRow;
        lastInRow = null;
        for (int j = layer[i].length - 1; j >= 0; j--) {
            if (layer[i][j]) {
                lastInRow = new Point(i, j);
                break;
            }
        }
        return lastInRow;
    }

    private Point fillFromRight(Point firstInRow, Point lastInRow, int rowIndex) {
        Point currentPosition = null;
        for (int columnIndex = lastInRow.y; columnIndex >= firstInRow.y; columnIndex--) {
            if (layer[rowIndex][columnIndex]) {
                currentPosition = new Point(rowIndex, columnIndex);
                route.add(currentPosition);
            }
        }
        return currentPosition;
    }

    private Point fillFromLeft(Point firstInRow, Point lastInRow, int rowIndex) {
        Point currentPosition = null;
        for (int columnIndex = firstInRow.y; columnIndex <= lastInRow.y; columnIndex++) {
            if (layer[rowIndex][columnIndex]) {
                currentPosition = new Point(rowIndex, columnIndex);
                route.add(currentPosition);
            }
        }
        return currentPosition;
    }

    @Override
    protected void setUp() {
        layer = connection.getCopyOfLayerAsSimpleTable();
        route = new ArrayList<>();
    }
}
