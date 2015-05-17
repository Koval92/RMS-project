package pathfinder;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * IMPORTANT!
 * All child classes must implement planPath() method, which should do all calculations
 *
 * There is also a possibility to override empty setUp method,
 * to set up all variables, read parameters from file,
 * getting a layer to print and so on...
 * Remember that you shouldn't use default values for variables,
 * because user can invoke algorithm more than once using the same algorithm's object.
 * Therefore you should do all constructor's work in setUp,
 * because it's the only way to revert to algorithm's initial state.
 *
 * All algorithms have final map for parameters, so if you have another map for them,
 * you should use params.putAll(yourMap) method
 */

public abstract class PathPlanner {
    protected PathPlanningConnection connection;
    protected Logger logger = Logger.getInstance();
    protected final Map<String,String> params = new HashMap<>();

    public PathPlanner() {
        logger.log("New instance of " + getName() + " algorithm created");
        params.put("algorithm_name", getName());
    }

    final public PathPlanningConnection getConnection() {
        return connection;
    }

    final public void setConnection(PathPlanningConnection connection) {
        this.connection = connection;
    }

    final public void invoke() {
        logger.log(getName() + " algorithm invoked");
        logger.log("\tSetting up algorithm");
        setUp();
        logger.log("\tSetting up completed");
        logger.log("\tAlgorithm starting");
        long startTime = System.nanoTime();
        List<Point> route = planPath();
        long endTime = System.nanoTime();
        logger.log("\tAlgorithm finished");

        Point initialPrinterPosition = connection.getInitialPrinterPosition();
        if (initialPrinterPosition != null && route.get(0) != null && route.get(0) != initialPrinterPosition)
            route.add(0, initialPrinterPosition);

        long durationInNano = endTime - startTime;
        double cost = MoveCostCalculator.calculate(route, connection.getCostFunctionType());

        connection.setCalcTime(durationInNano);
        connection.setResults(route, params);
    }

    protected final String getName() {
        return this.getClass().getSimpleName();
    }

    protected void setUp() {
    }

    protected abstract List<Point> planPath();
}
