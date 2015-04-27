package production.algorithms;

import production.CostFunctionType;
import production.MoveCostCalculator;
import production.PathPlanner;
import production.algorithms.route.PrintingTool;

import java.awt.*;

public class Greedy extends PathPlanner {

    private PrintingTool printingTool;
    private final int NR_OF_THREADS = 10;

    @Override
    protected void setUp() {
        printingTool = new PrintingTool(connection);
    }

    @Override
    protected java.util.List<Point> planPath() {
        FindPathThread thread1 = new FindPathThread(printingTool);
        thread1.thread.start();
        try {
            thread1.thread.join();
        } catch(InterruptedException ex){
            System.out.println("exception");
        };
        System.out.println(MoveCostCalculator.calculate(printingTool.getRoute(), CostFunctionType.DISTANCE));
        return thread1.printingTool.getRoute();
    }

    @Override
    protected String getName() {
        return "Greedy";
    }
}

class FindPathThread implements Runnable {

    PrintingTool printingTool;
    Thread thread;

    //Point startingPoint;
    public FindPathThread(PrintingTool printingTool) {
        //add properties for current threat
        //this.startingPoint = startingPoint;
        this.printingTool = printingTool;
        thread = new Thread();

    }


    @Override
    public void run() {
        //Find Path
        findStartingPoint();
        while (printingTool.getNumberOfPoints() > 0)
            findNextPoint();
        System.out.println(MoveCostCalculator.calculate(printingTool.getRoute(), CostFunctionType.DISTANCE));
    }

    private void findStartingPoint() {
        printingTool.getFirstPoint();
        printingTool.print();
    }

    private void findNextPoint() {
        Point currentPoint = printingTool.getCurrentPosition();
        Point nextPoint;
        double distance;

        Point bestPoint = printingTool.getPointFromList(0);
        double currentDistance = calcDistance(currentPoint, bestPoint);
        if (currentDistance < 1.00001 ) {
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

    private double calcDistance(Point first, Point second) {
        return Math.sqrt(Math.pow(second.getX() - first.getX(), 2) + Math.pow(second.getY() - first.getY(), 2));
    }
}
