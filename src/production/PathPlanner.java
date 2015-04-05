package production;

import java.awt.*;
import java.util.ArrayList;

public abstract class PathPlanner {
    private final PathPlanningListener listener;
    private final Layer desiredLayer;
    private final MoveCostCalculator costCalculator;

    public PathPlanner(Layer desiredLayer) {
        this(desiredLayer, null);
    }

    public PathPlanner(Layer desiredLayer, PathPlanningListener listener) {
        this(desiredLayer, CostFunctionType.DISTANCE, listener);
    }

    public PathPlanner(Layer desiredLayer, CostFunctionType costFunctionType, PathPlanningListener listener) {
        this.desiredLayer = desiredLayer;
        this.listener = listener;
        this.costCalculator = new MoveCostCalculator(costFunctionType);
    }

    protected void sendCostToListener(double cost) {
        if (listener != null)
            listener.setCost(cost);
    }

    protected void sendCalculationTimeToListener(double calcTime) {
        if (listener != null)
            listener.setCalcTime(calcTime);
    }

    protected void sendCurrentProgressToListener(double progress) {
        if (listener != null)
            listener.setProgress(progress);
    }

    protected void sendRouteToListener(ArrayList<Point> route) {
        if (listener != null)
            listener.setRoute(route);
    }

    public void invoke() {
        long startTime = System.nanoTime();
        ArrayList<Point> route = planPath();
        long endTime = System.nanoTime();

        long durationInNano = endTime - startTime;
        double cost = costCalculator.calculate(route);

        sendCostToListener(cost);
        sendCalculationTimeToListener(durationInNano);
        sendRouteToListener(route);
    }

    protected abstract ArrayList<Point> planPath();
}
