package production;

import java.awt.*;
import java.util.List;

/*
 * IMPORTANT!
 * All child classes must implement two methods:
 * getName - returning name of algorithm)
 * planPath - doing all calculations
 *
 * There is also a possibility to override empty setUp method,
 * to set up all variables, asking user for algorithm parameters,
 * getting a layer to print and so on...
 * Remember that you shouldn't use default values for variables,
 * because user can invoke algorithm more times using the same algorithm's object.
 * Therefore you should do all constructor's work in this method,
 * because it's the only way to revert to algorithm's initial state.
 */

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

    final protected void sendCalculationTimeToListener(double calcTimeInNano) {
        if (connection != null)
            connection.setCalcTime(calcTimeInNano);
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
        connection.log(getName() + " algorithm invoked");
        connection.log("\tSetting up algorithm");
        setUp();
        connection.log("\tSetting up completed");
        connection.log("\tAlgorithm starting");
        long startTime = System.nanoTime();
        List<Point> route = planPath();
        long endTime = System.nanoTime();
        connection.log("\tAlgorithm finished");

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
