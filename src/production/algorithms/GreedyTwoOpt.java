package production.algorithms;

 import production.PathPlanner;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;


public class GreedyTwoOpt extends PathPlanner {

    private TreeMap<Double, List<Point>> routes;
    Greedy greedy;

    public GreedyTwoOpt(Greedy greedy) {
        this.greedy = greedy;
    }

    private List<Thread> threads;

    private List<GreedyTwoOptThread> greedyTwoOptThreads;


    @Override
    protected void setUp() {
        //todo: parametry z tego zrobiæ
        GreedyParameters.set(50, 10, 1);
        GreedyThreadParameters.set(24, 0);
        initializeValues();
    }

    private void initializeValues() {
        //        route = new LinkedList<>();
        routes = new TreeMap<>();
        threads = new ArrayList<>();
        greedyTwoOptThreads = new ArrayList<>();
    }

    @Override
    protected List<Point> planPath() {
        findPathsWithGreedyAlgorithm();
        initializeThreads();
        startThreads();
        joinThreads();
        fillMapWithDistancesAndPaths();
        resetParameters();
        return getBestPath(routes);
    }

    private void resetParameters() {
        GreedyParameters.reset();
        GreedyThreadParameters.reset();
    }

    private void findPathsWithGreedyAlgorithm() {
        greedy.setUp();
        greedy.planPath();
        routes = greedy.getRoutes();
    }

    private void initializeThreads() {
        for (Entry<Double, List<Point>> entry : routes.entrySet())
            greedyTwoOptThreads.add(new GreedyTwoOptThread(entry.getKey(), entry.getValue()));
        for (GreedyTwoOptThread path : greedyTwoOptThreads) {
            threads.add(new Thread(path));
        }
    }

    private void startThreads() {
        //starting threads
        for (Thread thread : threads)
            thread.start();
    }

    private void joinThreads() {
        //joining threads
        try {
            for (Thread thread : threads)
                thread.join();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    private void fillMapWithDistancesAndPaths() {
        //getting paths and distances
//        routes.clear();
        for (GreedyTwoOptThread greedySolution : greedyTwoOptThreads)
            routes.put(greedySolution.getTotalDistance(), greedySolution.getRoute());
    }

    private List<Point> getBestPath(TreeMap<Double, List<Point>> routes) {
        return routes.firstEntry().getValue();
    }

    @Override
    protected String getName() {
        return "Greedy 2-Opt";
    }
}

class GreedyTwoOptParameters {
    public static boolean IS_GREEDY = true;
    public static int MAX_NR_OF_ITERATIONS = 50;
    public static boolean IS_INFINITIVE = false;

    public static void setParameters(boolean isGreedy, int maxNrOfIterations, boolean isInfinitive) {
        IS_GREEDY = isGreedy;
        MAX_NR_OF_ITERATIONS = maxNrOfIterations;
        IS_INFINITIVE = isInfinitive;

    }

    public static void resetParameters() {
        IS_GREEDY = false;
        MAX_NR_OF_ITERATIONS = 50;
        IS_INFINITIVE = false;
    }
}

class GreedyTwoOptThread implements Runnable {

    private double totalDistance;
    private List<Point> route;
    private double bestEdgeSwapCost = 0;
    int bestVertexI = Integer.MAX_VALUE;
    int bestVertexJ = Integer.MAX_VALUE;

    public GreedyTwoOptThread(double totalDistance, List<Point> route) {
        this.totalDistance = totalDistance;
        this.route = route;
    }

    @Override
    public void run() {
        if (GreedyTwoOptParameters.IS_INFINITIVE)
            findPathTillNoImprovements();
        else
            findPathTillEndOfIterations();

    }

    private void findPathTillNoImprovements() {
        do {
            bestEdgeSwapCost = 0;
            findTwoEdgesToSwap();
            swapIfItIsProfitable();
        } while (bestEdgeSwapCost < 0);
    }

    private void findPathTillEndOfIterations() {
        int iteration = 0;
        do {
            bestEdgeSwapCost = 0;
            findTwoEdgesToSwap();
            swapIfItIsProfitable();
        } while (bestEdgeSwapCost < 0 && iteration++ < GreedyTwoOptParameters.MAX_NR_OF_ITERATIONS);
    }

    private void findTwoEdgesToSwap() {
        // sprobuj znalezc lepszy punkt
        outer:
        for (int i = 0; i < route.size() - 2; i++) {
            for (int j = i + 1; j < route.size() - 1; j++) {
                double edgeSwapCost = calculate(i, j);
                if (edgeSwapCost < bestEdgeSwapCost) {
                    bestEdgeSwapCost = edgeSwapCost;
                    bestVertexI = i;
                    bestVertexJ = j;
                }
                if (bestEdgeSwapCost < 0) {
                    break outer;
                }
            }
        }
    }

    private void swapIfItIsProfitable() {
        if (bestEdgeSwapCost < 0) {
            swap(bestVertexI, bestVertexJ);
            totalDistance += bestEdgeSwapCost;
        }
    }

    /**
     * Change edges (v[i], v[i+1]) (v[j], v[j+1]) into
     * new ones (v[i], v[j]), (v[i+1], v[j+1])
     *
     * @param vertexI index of vertex i
     * @param vertexJ index of vertex j
     */
    private void swap(int vertexI, int vertexJ) {
        int i = vertexI + 1;
        int j = vertexJ;

        while (i < j) {
            Point temp = route.remove(i);
            route.add(j, temp);
            j--;
        }
    }


    /**
     * Calculate gain when changin edge (v[1], v[1] + 1_ and (v[2], v[2] + 1)
     * into (v[1] + v[2] and (v[1]+ 1, v[2] + 1)
     * if i + 1 == j nothing change -> the first solution which we can take as optimal in the beginning
     *
     * @param src1 first Vertex
     * @param src2 second Vertex
     * @return gain from swaping edges
     */
    private double calculate(int src1, int src2) {
        return ((calculateDistance(route.get(src1), route.get(src2)) + calculateDistance(route.get(src1 + 1), route.get(src2 + 1)))
                - (((calculateDistance(route.get(src1), route.get(src1 + 1))) + calculateDistance(route.get(src2), route.get(src2 + 1)))));
    }

    private double calculateDistance(Point first, Point second) {
        return Point.distance(first.getY(), first.getX(), second.getY(), second.getX());
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public List<Point> getRoute() {
        return route;
    }
}
