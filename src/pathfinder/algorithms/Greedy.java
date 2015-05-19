package pathfinder.algorithms;

import pathfinder.*;
import pathfinder.algorithms.route.*;

import java.awt.*;
import java.util.*;
import java.util.List;

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
        initializeValues();
    }


    private void initializeValues() {
        printingTool = new PrintingTool(connection);
        nrOfPoints = printingTool.getNumberOfPoints();                              //Nr Of Points
        startingPoints = new ArrayList<>();                                         //  list of starting points
        threads = new ArrayList<>();                                                //list of threads
        greedyThreads = new ArrayList<>();
        // automatyczne sortowanie -> najkrotsza trasa pod pierwszym elementem


//        routes = new TreeMap<>();
        routes = new ArrayList<>();
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
        for (int i = 0; i < nrOfPoints && foundPoints.size() < nrOfStartingPoints ; i++) {
            Point point;
            if (tableOfNeighbours.get(point = printingTool.getPointFromList(i)) == GreedyParameters.NR_OF_NEIGHBOURS_FOR_STARTING_POINT) {
                foundPoints.add((Point)point.clone());
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
            startingPoints.add((Point)point.clone());
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

    //TODO: Change it from map to ???   ------------ Changed to array!
    private void getRoutesFromThreads() {
        //getting paths and distances
        // from greedy threat -> wrzucenie ich do listy (co za roznica czy tree map to posortuje czy ja to zrobie samodzielnie
        for (GreedyThread greedySolution : greedyThreads) {
            RouteAndDistance routeAndDistance = new RouteAndDistance(greedySolution.getRoute(), greedySolution.getTotalDistance());
//            routes.put(greedySolution.getTotalDistance(), greedySolution.getRoute());
            routes.add(routeAndDistance);
        }
    }

    private void sortRoutesAndDistances() {
        Collections.sort(routes);
    }

    //TODO: Change get Best Path ----- przejrzyj listę i wybierz tą z najmniejszym dystansem
//    private List<Point> getBestPath(TreeMap<Double, List<Point>> routes) {
//        return routes.firstEntry().getValue();
    private List<Point> getBestPath(List<RouteAndDistance> routes) {
        return routes.get(0).getRoute();
//        if(routes.size() == 0)
//            return null;
//        int idOfBestRoute = 0;
//        double bestDistance = routes.get(0).getDistance();
//        double nextDistance;
//        for (int i = 1; i < routes.size(); i++) {
//            nextDistance = routes.get(i).getDistance();
//            if (nextDistance < bestDistance) {
//                bestDistance = nextDistance;
//                idOfBestRoute = i;
//            }
//        }
//        return routes.get(idOfBestRoute).getRoute();
    }

    //TODO: Change get Routes
//    public TreeMap<Double, List<Point>> getRoutes() {
//        return routes;
//    }

    //TODO: Change name to getRoutesAndDistances
    public List<RouteAndDistance> getRoutes() {
        return routes;
    }


}

//TODO: Write it in file
class GreedyParameters {
    //seed for random
    public static long SEED = 50;
    //ile watkow rownoczesnie
    public static int NR_OF_THREADS = 10;
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

//TODO: Write it in ile
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

    //TODO: pozbyć się printing tool
    private PrintingTool printingTool;
    //TODO: pozbyć się table Of Neighbours
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