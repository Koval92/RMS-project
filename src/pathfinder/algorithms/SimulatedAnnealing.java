package pathfinder.algorithms;

import pathfinder.ParamReader;
import pathfinder.PathPlanner;
import pathfinder.algorithms.route.Route;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.util.Random;

class SimulatedAnnealingParameters {
    public static long SEED = 50;
    public static double TEMPERATURE_MIN  = 0.0001;
    public static double COOLING_RATE  = 0.98;
    public static int ITERATIONS_ON_TEMPERATURE  = 1000;

    public static void set(long seed, double temperatureMin, double coolingRate, int iterationsOnTemperature) {
        SEED = seed;
        TEMPERATURE_MIN = temperatureMin;
        COOLING_RATE = coolingRate;
        ITERATIONS_ON_TEMPERATURE = iterationsOnTemperature;
    }

    public static void setDefault() {
        SEED = 50;
        TEMPERATURE_MIN  = 0.0001;
        COOLING_RATE  = 0.95;
        ITERATIONS_ON_TEMPERATURE  = 300;
    }

}


public class SimulatedAnnealing extends PathPlanner{

    private List<Point> currentRoute;
    private List<Point> bestRoute;
    private List<Point> nextRoute;

    private double temperature;
    private double currentDistance;
    private double bestDistance;
    private double nextDistance;

    private int swapPosition1;
    private int swapPosition2;

    private Random random;


    //probability of new solution acceptance
    public static double acceptanceProbability(double currentDistance, double nextDistance, double temperature) {
        // if new solution is better than previous always accept
        if (nextDistance <= currentDistance)
            return 1.1;
        return Math.exp(-(nextDistance - currentDistance) / temperature);
    }

    @Override
    protected void setUp() {
        getParametersFromFile();
    }

    private void getParametersFromFile() {
        File file = new File(System.getProperty("user.dir") + "/params/SimulatedAnnealing.txt");
        params.putAll(ParamReader.getParamsForSingleAlgorithm(file));
        setParameters();
    }

    private void setParameters() {
        SimulatedAnnealingParameters.set(Long.parseLong(params.get("seed")),
                Double.parseDouble(params.get("temperatureMin")),
                Double.parseDouble(params.get("coolingRate")),
                Integer.parseInt(params.get("iterationsOnTemperature")));
    }

    private void initializeValues(){
        currentRoute = new ArrayList<>();
        bestRoute = new ArrayList<>();
        temperature = 1.0;
        random = new Random(SimulatedAnnealingParameters.SEED);
    }

    @Override
    protected List<Point> planPath() {
        initializeValues();
        findFirstSolution();
        setCurrentRouteAsBest();
        while(isStillHot()) {
            //how many times on each temperature
            for (int i = 0 ; i < SimulatedAnnealingParameters.ITERATIONS_ON_TEMPERATURE; i++) {
                nextRoute = new ArrayList<>();
                nextRoute = Route.copyOfRoute(currentRoute);
                generateSwapPosition();
                swapRoute();
                updateNextRouteDistance();
                if (acceptanceProbability(currentDistance, nextDistance, temperature) > random.nextDouble()) {
                    currentRoute = Route.copyOfRoute(nextRoute);
                    currentDistance = nextDistance;
                }
                if (currentDistance < bestDistance) {
                    bestRoute = Route.copyOfRoute(currentRoute);
                    bestDistance = currentDistance;
                }
            }
            temperature *= SimulatedAnnealingParameters.COOLING_RATE;
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
        return temperature > SimulatedAnnealingParameters.TEMPERATURE_MIN;
    }

    private void generateSwapPosition() {
        swapPosition1 = (int) (nextRoute.size() * random.nextDouble());
        swapPosition2 = (int) (nextRoute.size() * random.nextDouble());
    }



    private void swapRoute() {
        //polowa szansy ze zamieni krawedzie
        if (random.nextDouble() > 0.5) {
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


