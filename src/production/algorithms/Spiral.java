package production.algorithms;

import production.PathPlanner;
import production.algorithms.route.LayerAsSimpleTable;
import production.algorithms.route.PrintingLayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Spiral extends PathPlanner {
    PrintingLayer layer;
    List<Point> route;
    int width, height;

    @Override
    protected List<Point> planPath() {
        int leftBorder = 0,
            bottomBorder = 0;
        int rightBorder = width - 1;
        int topBorder = height - 1;

        if (height < 1 || width < 1)
            return null;
        while (true) {
            if (rightBorder < leftBorder)
                break;
            moveRight(leftBorder, rightBorder, bottomBorder);
            bottomBorder++;
            if (topBorder < bottomBorder)
                break;
            moveUp(bottomBorder, topBorder, rightBorder);
            rightBorder--;
            if (rightBorder < leftBorder)
                break;
            moveLeft(rightBorder, leftBorder, topBorder);
            topBorder--;
            if (topBorder < bottomBorder)
                break;
            moveDown(topBorder, bottomBorder, leftBorder);
            leftBorder++;
        }
        return route;
    }

    private void moveRight(int begin, int end, int height) {
        for (; begin <= end; begin++)
            if (layer.get(height,begin))
                route.add(new Point(height, begin));
    }

    private void moveUp(int begin, int end, int width) {
        for (; begin <= end; begin++) {
            if (layer.get(begin,width))
                route.add(new Point(begin, width));
        }
    }

    private void moveLeft(int end, int begin, int height) {
        for (; begin <= end; end--)
            if (layer.get(height, end))
                route.add(new Point(height, end));
    }

    private void moveDown(int end, int begin, int width) {
        for (; begin <= end; end--)
            if (layer.get(end,width))
                route.add(new Point(end, width));
    }


    @Override
    protected String getName() {
        return "Spiral";
    }

    @Override
    protected void setUp() {
        layer = new LayerAsSimpleTable(connection);
        route = new ArrayList<>();
        width = layer.getWidth();
        height = layer.getHeight();
    }
}
