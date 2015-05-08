package production.algorithms;

import production.CostFunctionType;
import production.MoveCostCalculator;
import production.PathPlanner;
import production.PathPlanningConnection;
import production.algorithms.route.PrintingTool;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

public class Greedy extends PathPlanner {

    PrintingTool printingTool;
    // domyslna wartosc, ustawiana jako parametr
    private final int NR_OF_THREADS = 10;
    private int nrOfPoints;
    // Array of starting points
    private List<Point> startingPoints;
    // Array of threads
    private List<Thread> threads;

    private List<FindPathThread> findPathThreads;

    private List<List<Point>> listOfPaths;

    @Override
    protected void setUp() {
        printingTool = new PrintingTool(connection);
        nrOfPoints = printingTool.getNumberOfPoints();

        startingPoints = new ArrayList<>();
        threads = new ArrayList<>();
        findPathThreads = new ArrayList<>();
        listOfPaths = new ArrayList<>();
    }

    @Override
    protected java.util.List<Point> planPath() {
        for (int i = 0; i < NR_OF_THREADS; i++)
            startingPoints.add(printingTool.getPointFromList(nrOfPoints / NR_OF_THREADS * i));
        for (Point point : startingPoints)
            findPathThreads.add(new FindPathThread(connection, point));
        for(FindPathThread path : findPathThreads)
            threads.add(new Thread(path));
        for (Thread thread : threads)
            thread.start();


//        ExecutorService exec = Executors.newCachedThreadPool();
//        for (FindPathThread thread : threads)
//            exec.execute(thread);
        for(Thread thread : threads)
            while(thread.getState() != Thread.State.TERMINATED);

        for (FindPathThread path : findPathThreads)
            listOfPaths.add(path.getRoute());


        //DODAJ WYBOR STARTING POINTS
        // ARRAY OF THREADS
        //FindPathThread f = new FindPathThread(connection);
        //Thread t = new Thread(f);
        //t.start();
        //while(t.getState() != Thread.State.TERMINATED) {}



        return getBestPath(listOfPaths);
    }

    private List<Point> getBestPath(List<List<Point>> listOfRoutes) {
        Iterator<List<Point>> it = listOfRoutes.iterator();
        List<Point> best = null;
        List<Point> point;
        double bestDistance;
        double distance;
        if (it.hasNext()) {
            best = it.next();
            bestDistance = MoveCostCalculator.calculate(best, CostFunctionType.DISTANCE);

            while (it.hasNext()) {
                point = it.next();
                distance = MoveCostCalculator.calculate(point, CostFunctionType.DISTANCE);
                if (distance < bestDistance) {
                    best = point;
                    bestDistance = distance;
                }
            }
        }
        return best;
    }

    @Override
    protected String getName() {
        return "Greedy";
    }
}


class FindPathThread implements Runnable {

    PrintingTool printingTool;

    public FindPathThread(PathPlanningConnection connection) {
        this.printingTool = new PrintingTool(connection);
        findStartingPoint();
    }

    public FindPathThread(PathPlanningConnection connection, Point startingPoint) {
        this.printingTool = new PrintingTool(connection);
        printingTool.setCurrentPosition(startingPoint);
        printingTool.print();
    }

    @Override
    public void run() {
        while (printingTool.getNumberOfPoints() > 0)
            findNextPoint();
    }

    private void findNextPoint() {
        Point currentPoint = printingTool.getCurrentPosition();
        Point nextPoint;
        double distance;

        Point bestPoint = printingTool.getPointFromList(0);
        double currentDistance = calcDistance(currentPoint, bestPoint);
        if (currentDistance == 1) {
            printingTool.print(bestPoint);
            return;
        }
        for (int i = 1; i < printingTool.getNumberOfPoints(); i++) {
            nextPoint = printingTool.getPointFromList(i);
            distance = calcDistance(currentPoint, nextPoint);
            if (distance < currentDistance) {
                bestPoint = nextPoint;
                currentDistance = distance;
                if (currentDistance == 1) {
                    printingTool.print(bestPoint);
                    return;
                }
            }
        }
        printingTool.print(bestPoint);
    }

    private void findStartingPoint() {
        printingTool.getFirstPoint();
        printingTool.print();
    }

    private double calcDistance(Point first, Point second) {
        return Math.sqrt(Math.pow(second.getX() - first.getX(), 2) + Math.pow(second.getY() - first.getY(), 2));
    }

    public List<Point> getRoute() {
        return printingTool.getRoute();
    }
}