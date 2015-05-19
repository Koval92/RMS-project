package pathfinder.algorithms;

import pathfinder.*;
import pathfinder.algorithms.route.*;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

class GreedyParameters {
    //seed of the random
    public static long SEED = 50;
    //nr of greedy threads
    public static int NR_OF_THREADS = 10;
    //best nr of neighbours for the starting point
    public static int NR_OF_NEIGHBOURS_FOR_STARTING_POINT = 1;
    //should all greedy threads start from the same point
    public static boolean SAME_STARTING_POINT = false;
    //how many points are necessary to look into list, not into array,
    //for next closest point
    public static int NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY = 40;
    //what is the best nr of neighbours for the next point of path
    public static int BEST_NR_OF_NEIGHBOURS = 0;
    //coefficient describing importance of closest value of point's neighbours
    //rather than nearest distance
    public static float WEIGHT_OF_NEIGHBOURS = 0.42F;
    //coefficient describing importance of closest distance rather than point with number
    //of neighbours closest to value of best number of neighbours
    public static float WEIGHT_OF_DISTANCE = 0.32F;

    public static void set(int seed, int nrOfThreads, int nrOfNeighboursForStartingPoint,
                           boolean sameStartingPoint, int nrOfPointsNeededToCheckArray,
                           int bestNrOfNeighbours, float weightOfNeighbours, float weightOfDistance) {
        SEED = seed;
        NR_OF_THREADS = nrOfThreads;
        NR_OF_NEIGHBOURS_FOR_STARTING_POINT = nrOfNeighboursForStartingPoint;
        SAME_STARTING_POINT = sameStartingPoint;

        NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY = nrOfPointsNeededToCheckArray;
        BEST_NR_OF_NEIGHBOURS = bestNrOfNeighbours;
        WEIGHT_OF_NEIGHBOURS = weightOfNeighbours;
        WEIGHT_OF_DISTANCE = weightOfDistance;
    }

    public static void setDefault() {
        SEED = 50;
        NR_OF_THREADS = 20;
        NR_OF_NEIGHBOURS_FOR_STARTING_POINT = 1;
        SAME_STARTING_POINT = false;

        NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY = 40;
        BEST_NR_OF_NEIGHBOURS = 0;
        WEIGHT_OF_NEIGHBOURS = 0.42F;
        WEIGHT_OF_DISTANCE = 0.32F;
    }

}


//--------------------------------------------------------------------------------------------------

public class Greedy extends PathPlanner {

    TableOfNeighbours tableOfNeighbours;

    //list of all points to print
    private List<Point> allPointsToPrint;

    private int nrOfPoints;
    // List of starting points
    private List<Point> startingPoints;
    // List of threads
    private List<Thread> threads;
    // list of greedyThread classes
    private List<GreedyThread> greedyThreads;
    // lista dróg i ich dystansów (trzeba bedzie przechowywac nr najlepszej drogi
    private List<RouteAndDistance> routes;
    

    //set up - read parameters from file
    @Override
    protected void setUp() {
        getParametersFromFile();
    }

    //read parameters from file
    private void getParametersFromFile() {
        File file = new File(System.getProperty("user.dir") + "/params/Greedy.txt");
        params.putAll(ParamReader.getParamsForSingleAlgorithm(file));
        setParameters();
    }

    private void setParameters() {
        GreedyParameters.set(Integer.parseInt(params.get("seed")),
                Integer.parseInt(params.get("nrOfThreads")),
                Integer.parseInt(params.get("nrOfNeighboursForStartingPoint")),
                Boolean.parseBoolean(params.get("sameStartingPoint")),
                Integer.parseInt(params.get("nrOfPointsNeededToCheckArray")),
                Integer.parseInt(params.get("bestNrOfNeighbours")),
                Float.parseFloat(params.get("weightOfNeighbours")),
                Float.parseFloat(params.get("weightOfDistance")));
    }

    private void initializeValues() {
        allPointsToPrint = connection.getCopyOfLayerAsListOfPoints();

        nrOfPoints = allPointsToPrint.size();

        startingPoints = new ArrayList<>();                                         //  list of starting points
        threads = new ArrayList<>();                                                //list of threads
        greedyThreads = new ArrayList<>();
        routes = new ArrayList<>();
    }


    @Override
    protected java.util.List<Point> planPath() {
        initializeValues();
        fillTableOfNeighbours();
        if (!GreedyParameters.SAME_STARTING_POINT)
            setRandomStartingPointsWithConcreteNumberOfNeighbour(GreedyParameters.NR_OF_THREADS);
        else
            setSameStartingPoint();
        initializeThreads();
        startThreads();
        joinThreads();
        getRoutesFromThreads();
        sortRoutesAndDistances();
        return getBestPath(routes);
    }

    private void fillTableOfNeighbours() {
        //wyliczenie liczby sasiadow kazdego punktu, konieczne zeby wygenerowac punkty
        tableOfNeighbours = new TableOfNeighbours(TableOfNeighbours.findNeighbours(connection.getCopyOfLayerAsSimpleTable()));
    }

    //wybiera losowo punkty rozpoczecia sciezki
    private void setRandomStartingPoints(int numberOfStartingPoints) {
        Random random = new Random(GreedyParameters.SEED);
        for (int i = 0; i < numberOfStartingPoints; i++)
            startingPoints.add(allPointsToPrint.get(random.nextInt(nrOfPoints)));
    }

    //wybiera punkty na poczatek sciezki ze zdefiniowana liczba sasiadow, jezeli nie ma
    //takiej liczby dobiera losowo
    private void setRandomStartingPointsWithConcreteNumberOfNeighbour(int nrOfStartingPoints) {
        List<Point> foundPoints = new ArrayList<>();
        for (int i = 0; i < nrOfPoints && foundPoints.size() < nrOfStartingPoints; i++) {
            Point point;
            if (tableOfNeighbours.get(point = allPointsToPrint.get(i)) == GreedyParameters.NR_OF_NEIGHBOURS_FOR_STARTING_POINT) {
                foundPoints.add((Point) point.clone());
            }
        }
        int nrOfPointsStillToAdd = nrOfStartingPoints;
        for (; nrOfPointsStillToAdd > 0; nrOfPointsStillToAdd--) {
            if (!foundPoints.isEmpty())
                startingPoints.add(foundPoints.remove(0));
            else
                break;
        }
        setRandomStartingPoints(nrOfPointsStillToAdd);
    }

    // ustawia wszystkie punkty na to samo
    private void setSameStartingPoint() {
        Random random = new Random(GreedyParameters.SEED);
        Point point = allPointsToPrint.get(random.nextInt(nrOfPoints));
        for (int i = 0; i < GreedyParameters.NR_OF_THREADS; i++)
            startingPoints.add((Point) point.clone());
    }

    private void initializeThreads() {
        for (Point startingPoint : startingPoints)
            greedyThreads.add(new GreedyThread(allPointsToPrint, tableOfNeighbours, startingPoint));
        for (GreedyThread path : greedyThreads)
            threads.add(new Thread(path));
    }

    private void startThreads() {
        threads.forEach(Thread::start);
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

    private void getRoutesFromThreads() {
        //getting paths and distances
        for (GreedyThread greedySolution : greedyThreads) {
            RouteAndDistance routeAndDistance = new RouteAndDistance(greedySolution.getRoute(), greedySolution.getTotalDistance());
            routes.add(routeAndDistance);
        }
    }

    private void sortRoutesAndDistances() {
        Collections.sort(routes);
    }

    private List<Point> getBestPath(List<RouteAndDistance> routes) {
        return routes.get(0).getRoute();
    }

    public List<RouteAndDistance> getRoutesAndDistances() {
        return routes;
    }


}
//klasa wyszukujaca jednej sciezki (uruchamiana jako oddzielny watek
class GreedyThread implements Runnable {
    
    private static final int[][] COEFFICIENTS= new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1},
            {1, 1}, {-1, 1}, {-1, -1}, {1, -1},
            {2, 0}, {0, 2}, {-2, 0}, {0, -2},
            {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1},
            {2, 2}, {-2, 2}, {-2, -2}, {2, -2}};


    private TableOfNeighbours tableOfNeighbours;

    //droga calej trasy
    private double totalDistance;

    private List<Point> availablePoints;
    private List<Point> route;
    private Point currentPoint;

    
    public GreedyThread(List<Point> allPointsToPrint, TableOfNeighbours tableOfNeighbours, Point startingPoint) {
        this.availablePoints = Route.copyOfRoute(allPointsToPrint);
        this.tableOfNeighbours = TableOfNeighbours.newInstance(tableOfNeighbours);
        this.currentPoint = startingPoint;
        this.route = new ArrayList<>();

    }



    @Override
    public void run() {
        route.add(currentPoint);
        availablePoints.remove(currentPoint);
        while( availablePoints.size() > GreedyParameters.NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY)
            if (!findNextPointFromArray())
                findNextPointFromList();
        while (availablePoints.size() > 0)
            findNextPointFromList();
    }

    private boolean findNextPointFromArray() {
        int y = (int) currentPoint.getX();
        int x = (int) currentPoint.getY();
        

        int bestNrOfNeighbours = 100;
        int nextNrOfNeighbours;
        Point bestPoint = null;

        int bounding = 8;
        for (int j = 0; j < COEFFICIENTS.length; ) {
            for (int i = j; i < j + bounding; i++) {
                int tmpY = y + COEFFICIENTS[i][0];
                int tmpX = x + COEFFICIENTS[i][1];
                if( availablePoints.contains(new Point(tmpY, tmpX))) {
                    nextNrOfNeighbours = Math.abs(tableOfNeighbours.get(tmpY, tmpX) - GreedyParameters.BEST_NR_OF_NEIGHBOURS);
                    if (nextNrOfNeighbours < bestNrOfNeighbours) {
                        bestNrOfNeighbours = nextNrOfNeighbours;
                        bestPoint = new Point(tmpY, tmpX);
                    }
                    if (bestNrOfNeighbours <= GreedyParameters.BEST_NR_OF_NEIGHBOURS) {
                        addToRoute(bestPoint);
                        tableOfNeighbours.updateNeighbours(bestPoint);
                        return true;
                    }
                }
            }
            if (bestPoint != null) {
                addToRoute(bestPoint);
                tableOfNeighbours.updateNeighbours(bestPoint);
                return true;
            }
            j += bounding;
            bounding += bounding;
        }

        return false;
    }

    private void findNextPointFromList() {
        Point bestPoint;

        int bestNrOfNeighbours;
        int nextNrOfNeighbours;

        //przyjmij pierwszy punkt jako najlepszy
        bestPoint = availablePoints.get(0);
        double bestDistance = calculateDistance(currentPoint, bestPoint);
        bestNrOfNeighbours = Math.abs(tableOfNeighbours.get(bestPoint) - GreedyParameters.BEST_NR_OF_NEIGHBOURS);
        // jezeli jest to juz idealny punkt to tam idz
        if (bestDistance <= 1 + GreedyParameters.WEIGHT_OF_NEIGHBOURS && bestNrOfNeighbours <= GreedyParameters.BEST_NR_OF_NEIGHBOURS) {
            totalDistance += bestDistance;
            addToRoute(bestPoint);
            tableOfNeighbours.updateNeighbours(bestPoint);
            return;
        }

        //przegladaj inne punkty
        for(int i = 1; i < availablePoints.size(); i++) {
            Point nextPoint = availablePoints.get(i);
            double nextDistance = calculateDistance(currentPoint, nextPoint);

            //jezeli dystans miedzy punktammi jest znaczacy od razu wez nowy punkt
            if (nextDistance < bestDistance - GreedyParameters.WEIGHT_OF_DISTANCE) {
                bestPoint = nextPoint;
                bestNrOfNeighbours = Math.abs(tableOfNeighbours.get(bestPoint) - GreedyParameters.BEST_NR_OF_NEIGHBOURS);
                bestDistance = nextDistance;
            }
            //jezeli dystans miedzy punktami jest zblizony wez pod uwage liczbe sasiadow
            else if (nextDistance < bestDistance + GreedyParameters.WEIGHT_OF_NEIGHBOURS) {
                nextNrOfNeighbours = Math.abs(tableOfNeighbours.get(nextPoint) - GreedyParameters.BEST_NR_OF_NEIGHBOURS);

                //jezeli zmalala liczba sasiadow
                if (nextNrOfNeighbours < bestNrOfNeighbours) {
                    bestPoint = nextPoint;
                    bestNrOfNeighbours = nextNrOfNeighbours;
                    bestDistance = nextDistance;
                }
                //jezeli tak nie bylo to zostanmy przy starym punkcie
            }

            //sprawdzmy czy obecna odleglosc nie jest juz wystarczajaco dobra
            if (bestDistance <= 1 + GreedyParameters.WEIGHT_OF_NEIGHBOURS && bestNrOfNeighbours <= GreedyParameters.BEST_NR_OF_NEIGHBOURS) {
                addToRoute(bestPoint);
                tableOfNeighbours.updateNeighbours(bestPoint);
                totalDistance += bestDistance;
                return;
            }
        }

        totalDistance += bestDistance;
        addToRoute(bestPoint);
        tableOfNeighbours.updateNeighbours(bestPoint);
    }

    private void addToRoute(Point p) {
        currentPoint = p;
        route.add(p);
        availablePoints.remove(p);
    }



    private double calculateDistance(Point first, Point second) {
        return Point.distance(first.getY(), first.getX(), second.getY(), second.getX());
    }

    public List<Point> getRoute() {
        return route;
    }

    public double getTotalDistance() {
        return totalDistance;
    }


}