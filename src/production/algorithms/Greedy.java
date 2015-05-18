package production.algorithms;

import production.PathPlanner;
import production.PathPlanningConnection;
import production.algorithms.route.PrintingTool;
import production.algorithms.route.SimpleTableOfNeighbours;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Greedy extends PathPlanner {

    PrintingTool printingTool;
    SimpleTableOfNeighbours tableOfNeighbours;


    private int nrOfPoints;
    // Array of starting points
    private List<Point> startingPoints;
    // Array of threads
    private List<Thread> threads;

    private List<GreedyThread> greedyThreads;

    private TreeMap<Double, List<Point>> routes;


    @Override
    protected void setUp() {
        initializeValues();
    }


    private void initializeValues() {
        printingTool = new PrintingTool(connection);
        nrOfPoints = printingTool.getNumberOfPoints();
        startingPoints = new ArrayList<>();
        threads = new ArrayList<>();
        greedyThreads = new ArrayList<>();
        // automatyczne sortowanie -> najkrotsza trasa pod pierwszym elementem
        routes = new TreeMap<>();
    }



    @Override
    protected java.util.List<Point> planPath() {
        fillTableOfNeighbours();
        if(!GreedyParameters.SAME_STARTING_POINT)
            setRandomStartingPointsWithConcreteNumberOfNeighbour(GreedyParameters.NR_OF_THREADS);
        else
            setSameStartingPoint();
        initializeThreads();
        startThreads();
        joinThreads();
        fillMapWithRoutes();
        return getBestPath(routes);
    }

    private void fillTableOfNeighbours() {
        //wyliczenie liczby sasiadow kazdego punktu, konieczne zeby wygenerowac punkty
        tableOfNeighbours = new SimpleTableOfNeighbours(printingTool);
    }

    //wybiera losowo punkty rozpoczecia sciezki
    private void setRandomStartingPoints(int numberOfStartingPoints) {
        Random random = new Random(GreedyParameters.SEED);
        for (int i = 0; i < numberOfStartingPoints; i++)
            startingPoints.add(printingTool.getPointFromList(random.nextInt(nrOfPoints)));
    }

    //wybiera punkty na poczatek sciezki ze zdefiniowana liczba sasiadow, jezeli nie ma
    //takiej liczby dobiera losowo
    private void setRandomStartingPointsWithConcreteNumberOfNeighbour(int nrOfStartingPoints) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < nrOfPoints; i++) {
            Point point;
            if (tableOfNeighbours.get(point = printingTool.getPointFromList(i)) == GreedyParameters.NR_OF_NEIGHBOURS_FOR_STARTING_POINT) {
                points.add(point);
            }
        }
        int nrOfPointsStillToAdd = nrOfStartingPoints;
        Random random = new Random(GreedyParameters.SEED);
        for (; nrOfPointsStillToAdd > 0; nrOfPointsStillToAdd--) {
            if (!points.isEmpty())
                startingPoints.add(points.remove(random.nextInt(points.size())));
            else
                setRandomStartingPoints(nrOfPointsStillToAdd);
        }
    }

    private void setSameStartingPoint() {
        Random random = new Random(GreedyParameters.SEED);
        Point point = printingTool.getPointFromList(random.nextInt(nrOfPoints));
        for (int i = 0; i < GreedyParameters.NR_OF_THREADS; i++)
            startingPoints.add(point);
    }

    private void initializeThreads() {

        for (Point point : startingPoints)
            greedyThreads.add(new GreedyThread(connection, tableOfNeighbours.copyArray(), point));
        for (GreedyThread path : greedyThreads) {
            path.setParameters(new GreedyThreadParameters());
            threads.add(new Thread(path));
        }
    }


    private void startThreads(){
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

    private void fillMapWithRoutes() {
        //getting paths and distances
        for (GreedyThread greedySolution : greedyThreads)
            routes.put(greedySolution.getTotalDistance(), greedySolution.getRoute());
    }

    private List<Point> getBestPath(TreeMap<Double, List<Point>> routes) {
        return routes.firstEntry().getValue();
    }

    @Override
    protected String getName() {
        return "Greedy";
    }

    public TreeMap<Double, List<Point>> getRoutes() {
        return routes;
    }
}

class GreedyParameters {
    //seed for random
    public static long SEED = 50;
    //ile watkow rownoczesnie
    public static int NR_OF_THREADS = 20;
    public static int NR_OF_NEIGHBOURS_FOR_STARTING_POINT = 1;
    public static boolean SAME_STARTING_POINT = false;

    public static void set(int seed, int nrOfThreads, int nrOfNeighboursForStartingPoint, boolean sameStartingPoint) {
        SEED = seed;
        NR_OF_THREADS = nrOfThreads;
        NR_OF_NEIGHBOURS_FOR_STARTING_POINT = nrOfNeighboursForStartingPoint;
        SAME_STARTING_POINT = sameStartingPoint;
    }

    public static void reset() {
        SEED = 50;
        NR_OF_THREADS = 20;
        NR_OF_NEIGHBOURS_FOR_STARTING_POINT = 1;
        SAME_STARTING_POINT = false;
    }

}


//--------------------------------------------------------------------------------------------------

class GreedyThreadParameters {

    // po ilu punktow zmienic metode szukania
    public static  int NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY = 40;
    public static int BEST_NR_OF_NEIGHBOURS = 0;
    public static float WEIGHT_OF_NEIGHBOURS = 0.42F;
    public static float WEIGHT_OF_DISTANCE = 0.32F;

    public static void set(int nrOfPointsNeededToCheckArray, int bestNrOfNeighbours, float weightOfNeighbours, float weightOfDistance) {
        NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY = nrOfPointsNeededToCheckArray;
        BEST_NR_OF_NEIGHBOURS = bestNrOfNeighbours;
        WEIGHT_OF_NEIGHBOURS = weightOfNeighbours;
        WEIGHT_OF_DISTANCE = weightOfDistance;
    }

    public static void set(int nrOfPointsNeededToCheckArray, int bestNrOfNeighbours) {
        NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY = nrOfPointsNeededToCheckArray;
        BEST_NR_OF_NEIGHBOURS = bestNrOfNeighbours;
    }

    public static void reset() {
        NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY = 40;
        BEST_NR_OF_NEIGHBOURS = 0;
        WEIGHT_OF_NEIGHBOURS = 0.42F;
        WEIGHT_OF_DISTANCE = 0.32F;
    }
}


//--------------------------------------------------------------------------------------------------

//klasa wyszukujaca jednej sciezki (uruchamiana jako oddzielny watek
class GreedyThread implements Runnable {

    private PrintingTool printingTool;
    private SimpleTableOfNeighbours simpleTableOfNeighbours;

    //droga calej trasy
    private double totalDistance;

    private GreedyThreadParameters greedyThreadParameters;


    public GreedyThread(PathPlanningConnection connection, int[][] tableOfNeighbours, Point startingPoint) {
        this.printingTool = new PrintingTool(connection);
        this.simpleTableOfNeighbours = new SimpleTableOfNeighbours(printingTool, tableOfNeighbours);
        printingTool.setCurrentPosition(startingPoint);
        printingTool.print();
    }

    public void setParameters(GreedyThreadParameters greedyParameters) {
        this.greedyThreadParameters = greedyParameters;
    }

    @Override
    public void run() {
        while (printingTool.getNumberOfPoints() > greedyThreadParameters.NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY)
            if (!findNextPointFromArray())
                findNextPointFromList();
        while (printingTool.getNumberOfPoints() > 0)
            findNextPointFromList();
    }

    private boolean findNextPointFromArray() {
        Point currentPoint = printingTool.getCurrentPosition();
        int y = (int) currentPoint.getX();
        int x = (int) currentPoint.getY();
        int[][] coeff = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1},
                {1, 1}, {-1, 1}, {-1, -1}, {1, -1},
                {2, 0}, {0, 2}, {-2, 0}, {0, -2},
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1},
                {2, 2}, {-2, 2}, {-2, -2}, {2, -2}};

        int bestNrOfNeighbours = 100;
        int nextNrOfNeighbours;
        Point bestPoint = null;

        int bounding = 8;
        for (int j = 0; j < coeff.length; ) {
            for (int i = j; i < j + bounding; i++) {
                int tmpY = y + coeff[i][0];
                int tmpX = x + coeff[i][1];
                if (printingTool.get(tmpY, tmpX)) {
                    nextNrOfNeighbours = Math.abs(simpleTableOfNeighbours.get(tmpY, tmpX) - greedyThreadParameters.BEST_NR_OF_NEIGHBOURS);
                    if (nextNrOfNeighbours < bestNrOfNeighbours) {
                        bestNrOfNeighbours = nextNrOfNeighbours;
                        bestPoint = new Point(tmpY, tmpX);
                    }
                    if (bestNrOfNeighbours <= greedyThreadParameters.BEST_NR_OF_NEIGHBOURS) {
                        printingTool.print(bestPoint);
                        simpleTableOfNeighbours.updateNeighbours(bestPoint);
                        return true;
                    }
                }
            }
            if (bestPoint != null) {
                printingTool.print(bestPoint);
                simpleTableOfNeighbours.updateNeighbours(bestPoint);
                return true;
            }
            j += bounding;
            bounding += bounding;
        }

        return false;
    }

    private void findNextPointFromList() {
        Point currentPoint = printingTool.getCurrentPosition();
        Point bestPoint;

        int bestNrOfNeighbours;
        int nextNrOfNeighbours;

        //przyjmij pierwszy punkt jako najlepszy
        bestPoint = printingTool.getPointFromList(0);
        double bestDistance = calculateDistance(currentPoint, bestPoint);
        bestNrOfNeighbours = Math.abs(simpleTableOfNeighbours.get(bestPoint) - greedyThreadParameters.BEST_NR_OF_NEIGHBOURS);
        // jezeli jest to juz idealny punkt to tam idz
        if (bestDistance <= 1 + greedyThreadParameters.WEIGHT_OF_NEIGHBOURS && bestNrOfNeighbours <= greedyThreadParameters.BEST_NR_OF_NEIGHBOURS) {
            totalDistance += bestDistance;
            printingTool.print(bestPoint);
            simpleTableOfNeighbours.updateNeighbours(bestPoint);
            return;
        }

        //przegladaj inne punkty
        for (int i = 1; i < printingTool.getNumberOfPoints(); i++) {
            Point nextPoint = printingTool.getPointFromList(i);
            double nextDistance = calculateDistance(currentPoint, nextPoint);

            //jezeli dystans miedzy punktammi jest znaczacy od razu wez nowy punkt
            if (nextDistance < bestDistance - greedyThreadParameters.WEIGHT_OF_DISTANCE) {
                bestPoint = nextPoint;
                bestNrOfNeighbours = Math.abs(simpleTableOfNeighbours.get(bestPoint) - greedyThreadParameters.BEST_NR_OF_NEIGHBOURS);
                bestDistance = nextDistance;
            }
            //jezeli dystans miedzy punktami jest zblizony wez pod uwage liczbe sasiadow
            else if (nextDistance < bestDistance + greedyThreadParameters.WEIGHT_OF_NEIGHBOURS) {
                nextNrOfNeighbours = Math.abs(simpleTableOfNeighbours.get(nextPoint) - greedyThreadParameters.BEST_NR_OF_NEIGHBOURS);

                //jezeli zmalala liczba sasiadow
                if (nextNrOfNeighbours < bestNrOfNeighbours) {
                    bestPoint = nextPoint;
                    bestNrOfNeighbours = nextNrOfNeighbours;
                    bestDistance = nextDistance;
                }
                //jezeli tak nie bylo to zostanmy przy starym punkcie
            }

            //sprawdzmy czy obecna odleglosc nie jest juz wystarczajaco dobra
            if (bestDistance <= 1 + greedyThreadParameters.WEIGHT_OF_NEIGHBOURS && bestNrOfNeighbours <= greedyThreadParameters.BEST_NR_OF_NEIGHBOURS) {
                printingTool.print(bestPoint);
                simpleTableOfNeighbours.updateNeighbours(bestPoint);
                totalDistance += bestDistance;
                return;
            }
        }

        totalDistance += bestDistance;
        printingTool.print(bestPoint);
        simpleTableOfNeighbours.updateNeighbours(bestPoint);
    }


    private double calculateDistance(Point first, Point second) {
        return Point.distance(first.getY(), first.getX(), second.getY(), second.getX());
    }

    public List<Point> getRoute() {
        return printingTool.getRoute();
    }

    public double getTotalDistance() {
        return totalDistance;
    }
}