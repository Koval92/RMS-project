package test;

import production.PathPlanner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TestAlgorithm1 extends PathPlanner {
    @Override
    protected List<Point> planPath() {
        //desiredLayer.print();
        List<Point> route = new ArrayList<>();

        route.add(new Point(0, 0));
        route.add(new Point(0, 1));
        route.add(new Point(1, 2));
        route.add(new Point(0, 2));
        route.add(new Point(1, 1));
        route.add(new Point(2, 3));
        route.add(new Point(2, 2));
        route.add(new Point(6, 6));
        route.add(new Point(9, 6));

        return route;
    }
}