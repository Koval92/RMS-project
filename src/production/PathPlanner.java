package production;

import java.awt.*;
import java.util.List;

public abstract class PathPlanner {
    protected PathPlanningConnection connection = null;

    public PathPlanner() {
        System.out.println("New instance of " + getName() + " algorithm created");
    }

    final public PathPlanningConnection getConnection() {
        return connection;
    }

    final public void setConnection(PathPlanningConnection connection) {
        this.connection = connection;
    }

    final protected void sendCostToListener(double cost) {
        if (connection != null)
            connection.setCost(cost);
    }

    final protected void sendCalculationTimeToListener(double calcTimeinNano) {
        if (connection != null)
            connection.setCalcTime(calcTimeinNano);
    }

    final protected void sendCurrentProgressToListener(double progress) {
        if (connection != null)
            connection.setProgress(progress);
    }

    final protected void sendRouteToListener(List<Point> route) {
        if (connection != null)
            connection.setRoute(route);
    }

    final public void invoke() {
        System.out.println(getName() + " algorithm invoked");
        System.out.println("\tSetting up algorithm");
        setUp();
        System.out.println("\tSetting up completed");
        System.out.println("\tAlgorithm starting");
        long startTime = System.nanoTime();
        List<Point> route = planPath();
        long endTime = System.nanoTime();
        System.out.println("\tAlgorithm finished");

        long durationInNano = endTime - startTime;
        double cost = MoveCostCalculator.calculate(route, connection.getCostFunctionType());

        sendCostToListener(cost);
        sendCalculationTimeToListener(durationInNano);
        sendRouteToListener(route);
    }

    protected void setUp() {
    }

    protected abstract List<Point> planPath();

    protected abstract String getName();
}
