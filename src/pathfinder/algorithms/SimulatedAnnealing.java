package pathfinder.algorithms;


import pathfinder.PathPlanner;
import pathfinder.algorithms.route.LayerAsSimpleTable;
import pathfinder.algorithms.route.Route;


import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

public class SimulatedAnnealing extends PathPlanner{

    private List<Point> currentRoute;
    private List<Point> bestRoute;
    private List<Point> nextRoute;
    private double temperature;
    private double temperatureMin;
    private double coolingRate;

    private double currentDistance;
    private double bestDistance;
    private double nextDistance;

    private int swapPosition1;
    private int swapPosition2;


    private LayerAsSimpleTable layerAsSimpleTable;


    public SimulatedAnnealing() {
    }

    //probability of new solution acceptance
    public static double acceptanceProbability(double currentDistance, double nextDistance, double temperature) {
        // if new solution is better than previous always accept
        if (nextDistance <= currentDistance)
            return 1.1;
        return Math.exp(-(nextDistance - currentDistance) / temperature);
    }

    @Override
    protected void setUp() {
        currentRoute = new ArrayList<>();
        bestRoute = new ArrayList<>();
        temperature = 1.0;
        temperatureMin = 0.0001;
        coolingRate = 0.98;
        layerAsSimpleTable = new LayerAsSimpleTable(connection);
    }

    @Override
    protected List<Point> planPath() {
        findFirstSolution();
        setCurrentRouteAsBest();
        while(isStillHot()) {
            //how many times on each temperature
            for (int i = 0 ; i < 1000; i++) {
                nextRoute = new ArrayList<>();
                nextRoute = Route.copyOfRoute(currentRoute);
                generateSwapPosition();
                swapRoute();
                updateNextRouteDistance();
                if (acceptanceProbability(currentDistance, nextDistance, temperature) > Math.random()) {
                    currentRoute = Route.copyOfRoute(nextRoute);
                    currentDistance = nextDistance;
                }
                if (currentDistance < bestDistance) {
                    bestRoute = Route.copyOfRoute(currentRoute);
                    bestDistance = currentDistance;
                }
            }
            temperature *= coolingRate;
            currentRoute = Route.copyOfRoute(bestRoute);
            currentDistance = bestDistance;

        }
        return bestRoute;
    }

    private void findFirstSolution() {
        currentRoute = Route.generateRandomRoute(connection.getCopyOfLayerAsListOfPoints());
        currentDistance = Route.calculateTotalDistance(currentRoute);
    }

    private void setCurrentRouteAsBest() {
        bestRoute = Route.copyOfRoute(currentRoute);
        bestDistance = currentDistance;
    }

    private boolean isStillHot() {
        return temperature > temperatureMin;
    }

    private void generateSwapPosition() {
        swapPosition1 = (int) (nextRoute.size() * Math.random());
        swapPosition2 = (int) (nextRoute.size() * Math.random());
    }



    private void swapRoute() {
        //polowa szansy ze zamieni krawedzie
        if (Math.random() > 0.5) {
            if (swapPosition1 < swapPosition2)
                Route.swapEdges(swapPosition1, swapPosition2, nextRoute);
            else
                Route.swapEdges(swapPosition2, swapPosition1, nextRoute);
        } else { //polowa szansy ze punkty
            Point point1 = nextRoute.get(swapPosition1);
            Point point2 = nextRoute.get(swapPosition2);
            nextRoute.set(swapPosition2, point1);
            nextRoute.set(swapPosition1, point2);
        }
    }

    private void updateNextRouteDistance() {
        nextDistance = Route.calculateTotalDistance(nextRoute);
    }

}
