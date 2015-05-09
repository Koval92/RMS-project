package production.algorithms;

import production.PathPlanner;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;



public class GreedyTwoOpt extends PathPlanner {

    //todo: zrobic z tego parametr
    private final static int NR_OF_ITERATIONS = 20;

    private List<Point> route;
    double[][] distanceMatrix;
    Greedy greedy;

    public GreedyTwoOpt(Greedy greedy) {
        this.greedy = greedy;
    }

    @Override
    protected void setUp() {
        //todo: parametry z tego zrobiæ
        GreedyParameters.set(200, 10, 1);
        GreedyThreadParameters.set(24, 0);
        route = new LinkedList<>();
    }

    @Override
    protected java.util.List<Point> planPath() {
        greedy.setUp();
        route = greedy.planPath();
        distanceMatrix = new double[route.size()][route.size()];

        double bestEdgeSwapCost = 0;
        int bestVertexI = Integer.MAX_VALUE;
        int bestVertexJ = Integer.MAX_VALUE;


        for (int k = 0; k < NR_OF_ITERATIONS; k++) {

//            while (bestEdgeSwapCost >= 0) {

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
//                    if (bestEdgeSwapCost < 0)
//                        break;
                }
                // jezeli znalazlo jakikolwiek optymalny
//                if (bestVertexI != Integer.MAX_VALUE && bestVertexJ != Integer.MAX_VALUE) {
                    if (bestEdgeSwapCost < 0) {
                        change(bestVertexI, bestVertexJ);
//                        break;
                    }
//                }
                else
                    break;
            }
//        }
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

//        if (vertexI != vertexJ) {
//            double tmpDistance = distanceMatrix[vertexI][vertexI + 1];
//            distanceMatrix[vertexI][vertexI + 1] = distanceMatrix[vertexI][vertexJ];
//            distanceMatrix[vertexI][vertexJ] = tmpDistance;
//            tmpDistance = distanceMatrix[vertexJ][vertexJ + 1];
//            distanceMatrix[vertexJ][vertexJ + 1] = distanceMatrix[vertexI + 1][vertexJ + 1];
//            distanceMatrix[vertexI + 1][vertexJ + 1] = tmpDistance;
//        }

//        vertexI++;
//        vertexJ--;

//        while (vertexI < vertexJ) {
//            double tmpDistance = distanceMatrix[vertexI][vertexI + 1];
//            distanceMatrix[vertexI][vertexI + 1] = distanceMatrix[vertexJ][vertexJ + 1];
//            distanceMatrix[vertexJ][vertexJ + 1] = tmpDistance;
//        }


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
//        return ((distanceMatrix[src1][src2] + distanceMatrix[src1 + 1][src2 + 1]) - (distanceMatrix[src1][src1 + 1] + distanceMatrix[src2][src2 + 1]));
        return ((calculateDistance(route.get(src1), route.get(src2)) + calculateDistance(route.get(src1 + 1), route.get(src2 + 1)))
                - (((calculateDistance(route.get(src1), route.get(src1+1))) + calculateDistance(route.get(src2), route.get(src2 + 1)))));
    }

    //Cost of moving in two directions is the same
    private void generateDistanceMatrix() {
        for (int i = 0; i < route.size(); i++) {
            for (int j = i; j < route.size(); j++) {
                distanceMatrix[i][j] = calculateDistance(route.get(i), route.get(j));
                distanceMatrix[j][i] = distanceMatrix[i][j];
            }
        }
    }

    private double calculateDistance(Point first, Point second) {
        return Math.sqrt((second.getX() - first.getX()) * (second.getX() - first.getX())
                + (second.getY() - first.getY()) * (second.getY() - first.getY()));
    }

    @Override
    protected String getName() {
        return "Greedy 2-Opt";
    }
}

//class DistanceMatrix implements Comparable<DistanceMatrix> {
//    Point vertexI;
//    Point vertexJ;
//
//    @Override
//    public int compareTo(DistanceMatrix o) {
//        return;
//    }
//}