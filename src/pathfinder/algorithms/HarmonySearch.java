package pathfinder.algorithms;

import pathfinder.ParamReader;
import pathfinder.PathPlanner;
import pathfinder.algorithms.route.Route;
import pathfinder.algorithms.route.RouteAndDistance;


import java.awt.Point;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

class HarmonySearchParameters {
    public static long SEED = 50;
    public static int MEMORY_SIZE = 10;
    public static int NR_OF_ITERATIONS = 1000;
    public static double MEMORY_PROBABILITY = 0.3;

    public static void set(long seed, int memorySize, int nrOfIterations, double memoryProbability) {
        SEED = seed;
        MEMORY_SIZE = memorySize;
        NR_OF_ITERATIONS = nrOfIterations;
        MEMORY_PROBABILITY = memoryProbability;
    }

    public static void setDefault() {
        SEED = 50;
        MEMORY_SIZE = 10;
        NR_OF_ITERATIONS = 1000;
        MEMORY_PROBABILITY = 0.3;
    }

}


class Memory {
    private List<RouteAndDistance> routesAndDistances;

    public Memory() {
        routesAndDistances = new ArrayList<>();
    }

    public void addRoute(List<Point> route, double distance) {
        routesAndDistances.add(new RouteAndDistance(Route.copyOfRoute(route), distance));
    }

    public Point getPoint(int row, int nrOfPoint) {
        return routesAndDistances.get(row).getRoute().get(nrOfPoint);

//        return routes.get(row).get(nrOfPoint);
    }

    public void updateMemory(List<Point> route, double distance) {
        Collections.sort(routesAndDistances);
        // jeżeli tak to wyrzuć najgorszy i dodaj to rozwiązanie
        if (distance < routesAndDistances.get(routesAndDistances.size() - 1).getDistance()) {
            routesAndDistances.remove(routesAndDistances.size() - 1);
            routesAndDistances.add(new RouteAndDistance(Route.copyOfRoute(route), distance));
        }
    }

    public List<Point> getBestRoute() {
        Collections.sort(routesAndDistances);
        return routesAndDistances.get(0).getRoute();
    }
}


public class HarmonySearch extends PathPlanner {

    //if it isn't null set this solutions as a memory of this algorithm
    private List<Point> solutionFromOtherAlgorithm;

    public HarmonySearch() {
        solutionFromOtherAlgorithm = null;
    }

    private Memory memory;
    private List<Point> availablePoints;
    private List<Point> route;
    private int startingPointIndex;
    private Random random;

    @Override
    protected void setUp() {
        solutionFromOtherAlgorithm = null;
        getParametersFromFile();
    }

    private void getParametersFromFile() {
        File file = new File(System.getProperty("user.dir") + "/params/HarmonySearch.txt");
        params.putAll(ParamReader.getParamsForSingleAlgorithm(file));
        setParameters();
    }

    private void setParameters() {
        HarmonySearchParameters.set(Long.parseLong(params.get("seed")),
                Integer.parseInt(params.get("memorySize")),
                Integer.parseInt(params.get("nrOfIterations")),
                Double.parseDouble(params.get("memoryProbability")));
    }

    @Override
    protected java.util.List<Point> planPath() {
        initializeValues();
        initializeAlgorithm();
        generateMemory();

        for (int i = 0; i < HarmonySearchParameters.NR_OF_ITERATIONS; i++) {

            initializeIteration();
            while (availablePoints.size() > 0) {
                findNextPoint();
            }
            double distance = Route.calculateTotalDistance(route);
            logger.log("Iteration: " + i + ", found path with distance: " + distance);
            memory.updateMemory(route, distance);
        }
        return memory.getBestRoute();
    }

    private void findNextPoint() {
        Point nextPoint;
        if (isNextPointFromMemory()) {
            nextPoint = searchNextPointFromMemory();
            if (!isRouteContainingPoint(nextPoint))
                addPointToRoute(nextPoint);
            else {
                addNextPointFromAvailablePoints();
            }
        } else {
            addNextPointFromAvailablePoints();
        }
    }

    public void setSolutionFromOtherAlgorithm(List<Point> route) {
        solutionFromOtherAlgorithm = route;
    }

    private void initializeValues() {
        availablePoints = new ArrayList<>();
        memory = new Memory();
        random = new Random(HarmonySearchParameters.SEED);
    }

    private void initializeAlgorithm() {
        availablePoints = connection.getCopyOfLayerAsListOfPoints();
        startingPointIndex = random.nextInt(availablePoints.size());
//        availablePoints.clear();
        route = new ArrayList<>();
    }

    private void generateMemory() {
        // if first solution wasn't set find it randomly
        if (solutionFromOtherAlgorithm == null) {
            for (int i = 0; i < HarmonySearchParameters.MEMORY_SIZE; i++) {
                initializeIteration();
                while (availablePoints.size() > 0)
                    addPointToRoute(random.nextInt(availablePoints.size()));
                memory.addRoute(route, Route.calculateTotalDistance(route));
            }
        } else {
            double distance = Route.calculateTotalDistance(solutionFromOtherAlgorithm);
            for (int i = 0; i < HarmonySearchParameters.MEMORY_SIZE; i++) {
                memory.addRoute(Route.copyOfRoute(solutionFromOtherAlgorithm), distance);
            }
        }
    }

    //reset available points
    //clear route
    //add starting point to route
    private void initializeIteration() {
        availablePoints = connection.getCopyOfLayerAsListOfPoints();
        route.clear();
        addStartingPoint(route, availablePoints);
    }

    private void addStartingPoint(List<Point> route, List<Point> availablePoints) {
        route.add(availablePoints.remove(startingPointIndex));
    }

    private Point searchNextPointFromMemory() {
        int memoryRow = random.nextInt(HarmonySearchParameters.MEMORY_SIZE);
        return memory.getPoint(memoryRow, route.size());
    }

    private void addNextPointFromAvailablePoints() {
        addPointToRoute(random.nextInt(availablePoints.size()));
    }

    private boolean isRouteContainingPoint(Point nextPoint) {
        return route.contains(nextPoint);
    }

    private boolean isNextPointFromMemory() {
        return random.nextDouble() <= HarmonySearchParameters.MEMORY_PROBABILITY;
    }

    private void addPointToRoute(int i) {
        route.add(new Point(availablePoints.remove(i)));
    }

    private void addPointToRoute(Point point) {
        if (availablePoints.remove(point))
            route.add(new Point(point));
    }
}

