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

    private TreeMap<Double, List<Point>> distancesAndPaths;


    @Override
    protected void setUp() {
        initializeValues();
    }

    private void initializeThreads() {

        for (Point point : startingPoints)
            greedyThreads.add(new GreedyThread(connection, tableOfNeighbours.copyArray(), point));
        for (GreedyThread path : greedyThreads) {
            path.setParameters(new GreedyThreadParameters());
            threads.add(new Thread(path));
        }
    }

    private void initializeValues() {
        printingTool = new PrintingTool(connection);
        nrOfPoints = printingTool.getNumberOfPoints();
        startingPoints = new ArrayList<>();
        threads = new ArrayList<>();
        greedyThreads = new ArrayList<>();
        // automatyczne sortowanie -> najkrotsza trasa pod pierwszym elementem
        distancesAndPaths = new TreeMap<>();
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
        List<Point> pointsWithOneNeighbour = new ArrayList<>();
        for (int i = 0; i < nrOfPoints; i++) {
            Point point;
            if (tableOfNeighbours.get(point = printingTool.getPointFromList(i)) == GreedyParameters.NR_OF_NEIGHBOURS_FOR_STARTING_POINT) {
                pointsWithOneNeighbour.add(point);
            }
        }
        int nrOfPointsStillToAdd = nrOfStartingPoints;
        Random random = new Random(GreedyParameters.SEED);
        for (; nrOfPointsStillToAdd > 0; nrOfPointsStillToAdd--) {
            if (!pointsWithOneNeighbour.isEmpty())
                startingPoints.add(pointsWithOneNeighbour.remove(random.nextInt(pointsWithOneNeighbour.size())));
            else
                setRandomStartingPoints(nrOfPointsStillToAdd);
        }
    }

    @Override
    protected java.util.List<Point> planPath() {
        //wyliczenie liczby sasiadow kazdego punktu, konieczne zeby wygenerowac punkty
        tableOfNeighbours = new SimpleTableOfNeighbours(printingTool);
        setRandomStartingPointsWithConcreteNumberOfNeighbour(GreedyParameters.NR_OF_THREADS);
        initializeThreads();

        //starting threads
        for (Thread thread : threads)
            thread.start();

        //joining threads
        try {
            for (Thread thread : threads)
                thread.join();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        //getting paths and distances
        for (GreedyThread greedySolution : greedyThreads)
            distancesAndPaths.put(greedySolution.getTotalDistance(), greedySolution.getRoute());

        return getBestPath(distancesAndPaths);
    }

    private List<Point> getBestPath(TreeMap<Double, List<Point>> routes) {
        return routes.firstEntry().getValue();
    }

    @Override
    protected String getName() {
        return "Greedy";
    }
}

class GreedyParameters {
    //seed for random
    public static final long SEED = 50;
    //ile watkow rownoczesnie
    public final static int NR_OF_THREADS = 20;
    public final static int NR_OF_NEIGHBOURS_FOR_STARTING_POINT = 1;
}


//--------------------------------------------------------------------------------------------------

class GreedyThreadParameters {

    // po ilu punktow zmienic metode szukania
    public final int NR_OF_POINTS_NEEDED_TO_CHECK_ARRAY = 40;
    public final int BEST_NR_OF_NEIGHBOURS = 0;
    public final float WEIGHT_OF_NEIGHBOURS = 0.42F;
    public final float WEIGHT_OF_DISTANCE = 0.32F;
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
                    if (bestNrOfNeighbours <= greedyThreadParameters.BEST_NR_OF_NEIGHBOURS + 1) {
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
        return Math.sqrt((second.getX() - first.getX()) * (second.getX() - first.getX())
                + (second.getY() - first.getY()) * (second.getY() - first.getY()));
    }

    public List<Point> getRoute() {
        return printingTool.getRoute();
    }

    public double getTotalDistance() {
        return totalDistance;
    }
}