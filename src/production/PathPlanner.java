package production;

import java.awt.*;
import java.util.List;

public abstract class PathPlanner {
    protected final PathPlanningListener listener;
    protected final Layer desiredLayer;
    protected final MoveCostCalculator costCalculator;

    public PathPlanner(Layer desiredLayer) {
        this(desiredLayer, null);
    }

    public PathPlanner(Layer desiredLayer, PathPlanningListener listener) {
        this(desiredLayer, CostFunctionType.DISTANCE, listener);
    }

    public PathPlanner(Layer desiredLayer, CostFunctionType costFunctionType, PathPlanningListener listener) {
        this.desiredLayer = LayerFactory.copyFromLayer(desiredLayer);
        this.listener = listener;
        this.costCalculator = new MoveCostCalculator(costFunctionType);
    }

    protected void sendCostToListener(double cost) {
        if (listener != null)
            listener.setCost(cost);
    }

    protected void sendCalculationTimeToListener(double calcTimeinNano) {
        if (listener != null)
            listener.setCalcTime(calcTimeinNano);
    }

    protected void sendCurrentProgressToListener(double progress) {
        if (listener != null)
            listener.setProgress(progress);
    }

    protected void sendRouteToListener(List<Point> route) {
        if (listener != null)
            listener.setRoute(route);
    }

    public void invoke() {
        long startTime = System.nanoTime();
        List<Point> route = planPath();
        long endTime = System.nanoTime();

        long durationInNano = endTime - startTime;
        double cost = costCalculator.calculate(route);

        sendCostToListener(cost);
        sendCalculationTimeToListener(durationInNano);
        sendRouteToListener(route);
    }

    protected abstract List<Point> planPath();

    protected abstract String getName();
}
