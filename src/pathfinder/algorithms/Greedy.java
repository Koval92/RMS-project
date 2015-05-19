package pathfinder.algorithms;

import pathfinder.*;
import pathfinder.algorithms.route.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class Greedy extends PathPlanner {

    PrintingTool printingTool;
    SimpleTableOfNeighbours tableOfNeighbours;


    private int nrOfPoints;

    // List of starting points
    private List<Point> startingPoints;

    // List of threads
    private List<Thread> threads;

    // list of greedyThread classes
    private List<GreedyThread> greedyThreads;

//    private TreeMap<Double, List<Point>> routes;

    // lista dróg i ich dystansów (trzeba bedzie przechowywac nr najlepszej drogi
    private List<RouteAndDistance> routes;
//TODO: Usunąc thready - na razie zrobić wszystko na jednym
//TODO: pozbyć się klas printing tool printing layer itd.
//    TODO: nie robic reprezentacji w postaci mapy bo to zabiera inne rozwiązania wątków jeżeli maja tą samą odległość.
//    TODO: wymyślec inny sposób na sprawdzenie odległości dla danej drogi
//    TODO: dodać greedy annealing


    @Override
    protected void setUp() {
        getParametersFromFile();
    }

    //read paramaters from file -> if file isn't found or is empty default parameters are set.
    private void getParametersFromFile() {
        try {
            File file = new File(System.getProperty("user.dir") + "/params/Greedy.txt");
            params.putAll(ParamReader.getParamsForSingleAlgorithm(file));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        for (Entry<String, String> entry : params.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        if (params.isEmpty())
            GreedyParameters.setDefault();
        else
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
        printingTool = new PrintingTool(connection);
        nrOfPoints = printingTool.getNumberOfPoints();                              //Nr Of Points
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
        tableOfNeighbours = new SimpleTableOfNeighbours(printingTool);                                  // fill table of neighbours
    }

    //wybiera losowo punkty rozpoczecia sciezki
    private void setRandomStartingPoints(int numberOfStartingPoints) {
        Random random = new Random(GreedyParameters.SEED);
        for (int i = 0; i < numberOfStartingPoints; i++)
            startingPoints.add((Point) printingTool.getPointFromList(random.nextInt(nrOfPoints)).clone());
    }

    //wybiera punkty na poczatek sciezki ze zdefiniowana liczba sasiadow, jezeli nie ma
    //takiej liczby dobiera losowo
    private void setRandomStartingPointsWithConcreteNumberOfNeighbour(int nrOfStartingPoints) {
        List<Point> foundPoints = new ArrayList<>();
        for (int i = 0; i < nrOfPoints && foundPoints.size() < nrOfStartingPoints; i++) {
            Point point;
            if (tableOfNeighbours.get(point = printingTool.getPointFromList(i)) == GreedyParameters.NR_OF_NEIGHBOURS_FOR_STARTING_POINT) {
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

    private void setSameStartingPoint() {
        Random random = new Random(GreedyParameters.SEED);
        Point point = printingTool.getPointFromList(random.nextInt(nrOfPoints));
        for (int i = 0; i < GreedyParameters.NR_OF_THREADS; i++)
            startingPoints.add((Point) point.clone());
    }

    private void initializeThreads() {

        for (Point point : startingPoints)
            greedyThreads.add(new GreedyThread(connection, tableOfNeighbours.copyArray(), point));
        for (GreedyThread path : greedyThreads) {
//            path.setParameters(new GreedyThreadParameters());
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

    private void getRoutesFromThreads() {
        //getting paths and distances
        // from greedy threat -> wrzucenie ich do listy (co za roznica czy tree map to posortuje czy ja to zrobie samodzielnie
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


    //TODO: Change name to getRoutesAndDistances
    public List<RouteAndDistance> getRoutes() {
        return routes;
    }


}

//TODO: Write it in file
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


//klasa wyszukujaca jednej sciezki (uruchamiana jako oddzielny watek
class GreedyThread implements Runnable {

    //TODO: pozbyć się printing tool
    private PrintingTool printingTool;
    //TODO: pozbyć się table Of Neighbours
    private SimpleTableOfNeighbours simpleTableOfNeighbours;

    //droga calej trasy
    private double totalDistance;

//    private GreedyThreadParameters greedyThreadParameters;


    public GreedyThread(PathPlanningConnection connection, int[][] tableOfNeighbours, Point startingPoint) {
        this.printingTool = new PrintingTool(connection);
        this.simpleTableOfNeighbours = new SimpleTableOfNeighbours(printingTool, tableOfNeighbours);
        printingTool.setCurrentPosition(startingPoint);
        printingTool.print();
    }

//    public void setParameters(GreedyThreadParameters greedyParameters) {
//        this.greedyThreadParameters = greedyParameters;
//    }

    @Override
    public void run() {
        while (printingTool.getNumberOfPoints() > GreedyParameters.NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY)
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
                    nextNrOfNeighbours = Math.abs(simpleTableOfNeighbours.get(tmpY, tmpX) - GreedyParameters.BEST_NR_OF_NEIGHBOURS);
                    if (nextNrOfNeighbours < bestNrOfNeighbours) {
                        bestNrOfNeighbours = nextNrOfNeighbours;
                        bestPoint = new Point(tmpY, tmpX);
                    }
                    if (bestNrOfNeighbours <= GreedyParameters.BEST_NR_OF_NEIGHBOURS) {
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
        bestNrOfNeighbours = Math.abs(simpleTableOfNeighbours.get(bestPoint) - GreedyParameters.BEST_NR_OF_NEIGHBOURS);
        // jezeli jest to juz idealny punkt to tam idz
        if (bestDistance <= 1 + GreedyParameters.WEIGHT_OF_NEIGHBOURS && bestNrOfNeighbours <= GreedyParameters.BEST_NR_OF_NEIGHBOURS) {
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
            if (nextDistance < bestDistance - GreedyParameters.WEIGHT_OF_DISTANCE) {
                bestPoint = nextPoint;
                bestNrOfNeighbours = Math.abs(simpleTableOfNeighbours.get(bestPoint) - GreedyParameters.BEST_NR_OF_NEIGHBOURS);
                bestDistance = nextDistance;
            }
            //jezeli dystans miedzy punktami jest zblizony wez pod uwage liczbe sasiadow
            else if (nextDistance < bestDistance + GreedyParameters.WEIGHT_OF_NEIGHBOURS) {
                nextNrOfNeighbours = Math.abs(simpleTableOfNeighbours.get(nextPoint) - GreedyParameters.BEST_NR_OF_NEIGHBOURS);

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