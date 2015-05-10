package production.algorithms;

import production.PathPlanner;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;


public class GreedyTwoOpt extends PathPlanner {

    private List<Point> route;
    Greedy greedy;

    public GreedyTwoOpt(Greedy greedy) {
        this.greedy = greedy;
    }

    @Override
    protected void setUp() {
        //todo: parametry z tego zrobiæ
        GreedyParameters.set(50, 10, 1);
        GreedyThreadParameters.set(24, 0);
        route = new LinkedList<>();
    }

    @Override
    protected java.util.List<Point> planPath() {
        greedy.setUp();
        route = greedy.planPath();

        double bestEdgeSwapCost = 0;
        int bestVertexI = Integer.MAX_VALUE;
        int bestVertexJ = Integer.MAX_VALUE;

        while (bestEdgeSwapCost < 0) {
            bestEdgeSwapCost = 0;
            // sprobuj znalezc lepszy punkt
            for (int i = 0; i < route.size() - 2; i++) {
                for (int j = i + 1; j < route.size() - 1; j++) {
                    double edgeSwapCost = calculate(i, j);
                    if (edgeSwapCost < bestEdgeSwapCost) {
                        bestEdgeSwapCost = edgeSwapCost;
                        bestVertexI = i;
                        bestVertexJ = j;
                    }
                }
            }
            // jezeli znalazlo jakikolwiek punkt który warto zamieniæ to to zrób, je¿eli nie to skoñcz
            if (bestEdgeSwapCost < 0) {
                change(bestVertexI, bestVertexJ);
            }
        }
        GreedyParameters.reset();
        GreedyThreadParameters.reset();
        return route;

    }

    /**
     * Change edges (v[i], v[i+1]) (v[j], v[j+1]) into
     * new ones (v[i], v[j]), (v[i+1], v[j+1])
     *
     * @param vertexI index of vertex i
     * @param vertexJ index of vertex j
     */
    private void change(int vertexI, int vertexJ) {
        int i = vertexI + 1;
        int j = vertexJ;

        while (i < j) {
            Point temp = route.remove(i);
            route.add(j, temp);
            j--;
        }
    }

    //Calculate gain when changin edge (v[1], v[1] + 1_ and (v[2], v[2] + 1)
    // into (v[1] + v[2] and (v[1]+ 1, v[2] + 1)
    // if i + 1 == j nothing change -> the first solution which we can take as optimal in the beginning
    private double calculate(int src1, int src2) {
        return ((calculateDistance(route.get(src1), route.get(src2)) + calculateDistance(route.get(src1 + 1), route.get(src2 + 1)))
                - (((calculateDistance(route.get(src1), route.get(src1 + 1))) + calculateDistance(route.get(src2), route.get(src2 + 1)))));
    }


    private double calculateDistance(Point first, Point second) {
        return Point.distance(first.getY(), first.getX(), second.getY(), second.getX());
    }

    @Override
    protected String getName() {
        return "Greedy 2-Opt";
    }
}
