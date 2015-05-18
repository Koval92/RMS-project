package production.algorithms;


import production.PathPlanner;
import production.algorithms.route.PrintingTool;


import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

class Memory {
    private List<List<Point>> routes;
    private List<Double> distances;
    private List<Double> allDistances = new ArrayList<>();

    public Memory() {
        routes = new ArrayList<>();
        distances = new ArrayList<>();
    }

    public void addRoute(List<Point> route, double distance) {
        routes.add(clone(route));
        distances.add(distance);
        allDistances.add(distance);
    }

    private List<Point> clone(List<Point> route) {
        List<Point> clone = new ArrayList<>();
        for (Point point : route)
            clone.add((Point)point.clone());
        return clone;
    }

    public Point getPoint(int row, int nrOfPoint) {
        return routes.get(row).get(nrOfPoint);
    }

    public void updateMemory(List<Point> route, double distance) {
        if (isWorseRouteThan(distance)) {
            removeWorstRoute();
            addRoute(route, distance);
        }
    }

    private boolean isWorseRouteThan(double newDistance) {
        for (double distance : distances) {
            if (newDistance < distance) {
                return true;
            }
        }
        return false;
    }

    private void removeWorstRoute() {
        int idOfWorstRoute = 0;
        double worstDistance = Double.MIN_VALUE;
        for (int i = 0; i < distances.size(); i++) {
            double nextDistance = distances.get(i);
            if (nextDistance > worstDistance) {
                worstDistance = nextDistance;
                idOfWorstRoute = i;
            }
        }

        distances.remove(idOfWorstRoute);
        routes.remove(idOfWorstRoute);
    }

    public List<Point> getBestRoute() {
        int idOfBestRoute = 0;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < distances.size(); i++) {
            double nextDistance = distances.get(i);
            if (nextDistance < bestDistance) {
                bestDistance = nextDistance;
                idOfBestRoute = i;
            }
        }
        System.out.println(allDistances);
        return routes.get(idOfBestRoute);
    }
}


public class HarmonySearch extends PathPlanner {

    // wygeneruj iles losowych rozwiazan
    // wybierz najlepsze iles HMS - harmonyMemorySize

    private int memorySize = 10;

    private Memory memory;
    private List<Point> availablePoints;
    private List<Point> route;
    private final double memoryPropability = 0.3;
    private final static int SEED = 0;
    private int startingPointIndex;
    private Random random;
    private int nrOfIterations = 10000;
    private List<Double> distances = new ArrayList<>();

    public HarmonySearch() {

    }

    private Greedy greedy;
    public HarmonySearch(Greedy greedy) {
        this.greedy = greedy;
    }

    @Override
    protected void setUp() {
        availablePoints = new ArrayList<>();
        memory = new Memory();
    }

    @Override
    protected java.util.List<Point> planPath() {
        initializeAlgorithm();
//        generateMemory();
        greedyGenerateMemory();

        for (int i = 0; i < nrOfIterations; i++) {
            Point nextPoint;
            initializeIteration();
            while(availablePoints.size() > 0) {
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
            memory.updateMemory(route, calculateDistance(route));
        }
        return memory.getBestRoute();
    }

    private Point searchNextPointFromMemory() {
        int memoryRow = random.nextInt(memorySize);
        return memory.getPoint(memoryRow, route.size());
    }

    private void addNextPointFromAvailablePoints() {
        addPointToRoute(random.nextInt(availablePoints.size()));
    }

    private boolean isRouteContainingPoint(Point nextPoint) {
        return route.contains(nextPoint);
    }

    private boolean isNextPointFromMemory() {
        return random.nextDouble() <= memoryPropability;
    }


    private void initializeAlgorithm() {
        random = new Random(SEED);
        //set all avaliable points
        availablePoints = connection.getCopyOfLayerAsListOfPoints();
        startingPointIndex = random.nextInt(availablePoints.size());
//        availablePoints.clear();
        route = new ArrayList<>();
    }

    private void initializeIteration() {
        availablePoints = connection.getCopyOfLayerAsListOfPoints();
        route.clear();
        addStartingPoint(route, availablePoints);
    }

    private void generateMemory() {
        for (int i = 0; i < memorySize; i++) {
            initializeIteration();
            while (availablePoints.size() > 0)
                addPointToRoute(random.nextInt(availablePoints.size()));
            memory.addRoute(route, calculateDistance(route));
        }
    }

    private void greedyGenerateMemory() {
        GreedyParameters.set(0, 10, 1, true);
        greedy.setUp();
        greedy.planPath();
        TreeMap<Double, List<Point>> routeMap = greedy.getRoutes();
        startingPointIndex = availablePoints.indexOf(routeMap.firstEntry().getValue().get(0));
        createMemory(routeMap);
        for(int i = routeMap.size(); i < memorySize; i++)
            memory.addRoute(routeMap.firstEntry().getValue(), routeMap.firstKey());
        GreedyParameters.reset();
    }

    private void createMemory(TreeMap<Double, List<Point>> routeMap) {
        for(java.util.Map.Entry<Double, List<Point>> entry : routeMap.entrySet()) {
            memory.addRoute(entry.getValue(), entry.getKey());
        }
    }

    private void addPointToRoute(int i) {
        route.add(new Point(availablePoints.remove(i)));
    }

    private void addPointToRoute(Point point) {
        if (availablePoints.remove(point))
            route.add(new Point(point));
        else
            throw new RuntimeException();               //!!!!!!!!!!!!!!!!!!! do zmiany
    }

    private void addStartingPoint(List<Point> route, List<Point> availablePoints) {
        route.add(availablePoints.remove(startingPointIndex));
    }

    private double calculateDistance(List<Point> route) {
        double distance = 0;
        Point currentPoint = route.get(0);
        Point nextPoint;
        for (int i = 1; i < route.size(); i++) {
            nextPoint = route.get(i);
            distance += currentPoint.distance(nextPoint);
            currentPoint = nextPoint;
        }
        return distance;
    }

    @Override
    protected String getName() {
        return "Harmony Search";
    }
}
