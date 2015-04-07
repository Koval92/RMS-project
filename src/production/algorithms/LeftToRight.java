package production.algorithms;

import production.CostFunctionType;
import production.Layer;
import production.PathPlanner;
import production.PathPlanningListener;

import java.awt.*;
import java.util.List;

public class LeftToRight extends PathPlanner {
    public LeftToRight(Layer desiredLayer) {
        super(desiredLayer);
    }

    public LeftToRight(Layer desiredLayer, PathPlanningListener listener) {
        super(desiredLayer, listener);
    }

    public LeftToRight(Layer desiredLayer, CostFunctionType costFunctionType, PathPlanningListener listener) {
        super(desiredLayer, costFunctionType, listener);
    }

    @Override
    protected List<Point> planPath() {
        return desiredLayer.toPoints();
    }

    @Override
    protected String getName() {
        return "Left-to-right";
    }
}
