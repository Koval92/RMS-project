package test;

import production.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class testAlgorithm extends PathPlanner {
    public testAlgorithm(Layer desiredLayer) {
        super(desiredLayer);
    }

    public testAlgorithm(Layer desiredLayer, PathPlanningListener listener) {
        super(desiredLayer, listener);
    }

    public testAlgorithm(Layer desiredLayer, CostFunctionType costFunctionType, PathPlanningListener listener) {
        super(desiredLayer, costFunctionType, listener);
    }

    public static void main(String[] args) {
        PathPlanner planner = new testAlgorithm(LayerFactory.createEmptyLayer(10));
        planner.invoke();
    }

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
