package production;

import java.awt.*;
import java.util.List;

public abstract class PathPlanner {
    protected PathPlanningListener listener = null;

    public PathPlanner() {
        System.out.println("New instance of " + getName() + " algorithm created");
    }

    final public PathPlanningListener getListener() {
        return listener;
    }

    final public void setListener(PathPlanningListener listener) {
        this.listener = listener;
    }

    final protected void sendCostToListener(double cost) {
        if (listener != null)
            listener.setCost(cost);
    }

    final protected void sendCalculationTimeToListener(double calcTimeinNano) {
        if (listener != null)
            listener.setCalcTime(calcTimeinNano);
    }

    final protected void sendCurrentProgressToListener(double progress) {
        if (listener != null)
            listener.setProgress(progress);
    }

    final protected void sendRouteToListener(List<Point> route) {
        if (listener != null)
            listener.setRoute(route);
    }

    final public void invoke() {
        System.out.println(getName() + " algorithm invoked");
        long startTime = System.nanoTime();
        List<Point> route = planPath();
        long endTime = System.nanoTime();

        long durationInNano = endTime - startTime;
        double cost = MoveCostCalculator.calculate(route, listener.getCostFunctionType());

        sendCostToListener(cost);
        sendCalculationTimeToListener(durationInNano);
        sendRouteToListener(route);
    }

    protected abstract List<Point> planPath();

    protected abstract String getName();
}
