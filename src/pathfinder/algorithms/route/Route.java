package pathfinder.algorithms.route;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class Route {

    public static int SEED = 0;

    // generate route randomly
    public static List<Point> generateRandomRoute(List<Point> route) {
        Random random = new Random(SEED);
        List<Point> generatedRoute = new ArrayList<>();
        while(route.size() > 0)
            generatedRoute.add((Point)route.remove(random.nextInt(route.size())).clone());
        return generatedRoute;
    }

    // calculate total distance of route
    public static double calculateTotalDistance(List<Point> route) {
        Iterator<Point> it = route.iterator();
        if (! it.hasNext())
            return -1;
        double disatnce = 0;
        Point currentPoint = it.next();
        Point nextPoint;
        while(it.hasNext()) {
            nextPoint = it.next();
            disatnce += currentPoint.distance(nextPoint);
            currentPoint = nextPoint;
        }
        return disatnce;
    }

    public static List<Point> copyOfRoute(List<Point> route) {
        List<Point> copyRoute = new ArrayList<>();
        for(Point point : route)
            copyRoute.add((Point)point.clone());
        return copyRoute;
    }

    public static void swapEdges(int vertexI, int vertexJ, List<Point> route) {
        int i = vertexI + 1;
        int j = vertexJ;

        while (i < j) {
            Point temp = route.remove(i);
            route.add(j, temp);
            j--;
        }
    }
}
