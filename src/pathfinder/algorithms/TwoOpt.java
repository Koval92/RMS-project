package pathfinder.algorithms;

import pathfinder.ParamReader;
import pathfinder.PathPlanner;
import pathfinder.algorithms.route.Route;
import pathfinder.algorithms.route.RouteAndDistance;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class TwoOptParameters {

    public static boolean IS_GREEDY = true;
    public static int MAX_NR_OF_ITERATIONS = -1;

    public static void set(boolean isGreedy, int maxNrOfIterations) {
        IS_GREEDY = isGreedy;
        MAX_NR_OF_ITERATIONS = maxNrOfIterations;
    }

    public static void setDefault() {
        IS_GREEDY = true;
        MAX_NR_OF_ITERATIONS = -1;
    }

}

public class TwoOpt extends PathPlanner{

    private List<Point> route;
    private double bestEdgeSwapCost = 0;
    private int bestVertexI = Integer.MAX_VALUE;
    private int bestVertexJ = Integer.MAX_VALUE;


    // if it isn't null set this solutions as a begining solutions
    private List<Point> solutionFromOtherAlgorithm;

    public TwoOpt() {
        solutionFromOtherAlgorithm = null;
    }

    @Override
    protected void setUp() {
        getParametersFromFile();
    }

    private void getParametersFromFile(){
        File file = new File(System.getProperty("user.dir") + "/params/TwoOpt.txt");
        params.putAll(ParamReader.getParamsForSingleAlgorithm(file));
        setParameters();
    }

    private void setParameters() {
        TwoOptParameters.set(Boolean.parseBoolean(params.get("isGreedy")),
                Integer.parseInt(params.get("maxNrOfIterations")));
    }

    @Override
    protected java.util.List<Point> planPath() {
        initializeValues();
        findBeginningPaths();
        if (TwoOptParameters.MAX_NR_OF_ITERATIONS < 0)
            findPathTillNoImprovements();
        else
            findPathTillEndOfIterations();
        return route;
    }

    private void initializeValues() {
        route = new ArrayList<>();
    }

    private void findBeginningPaths() {
        // if first solution wasn't set find it randomly
        if(solutionFromOtherAlgorithm == null) {
            route = Route.generateRandomRoute(connection.getCopyOfLayerAsListOfPoints());
        }
        else {
            route = Route.copyOfRoute(solutionFromOtherAlgorithm);
        }
    }

    public void setSolutionFromOtherAlgorithm(List<Point> route) {
        solutionFromOtherAlgorithm = route;
    }

    private void findPathTillNoImprovements() {
        do {
            bestEdgeSwapCost = 0;
            findTwoEdgesToSwap();
            swapIfItIsProfitable();
        } while (bestEdgeSwapCost < 0);
    }

    private void findPathTillEndOfIterations() {
        int iteration = 0;
        do {
            bestEdgeSwapCost = 0;
            findTwoEdgesToSwap();
            swapIfItIsProfitable();
        } while (bestEdgeSwapCost < 0 && iteration++ < TwoOptParameters.MAX_NR_OF_ITERATIONS);
    }

    private void findTwoEdgesToSwap() {
        // sprobuj znalezc lepszy punkt
        outer:
        for (int i = 0; i < route.size() - 2; i++) {
            for (int j = i + 1; j < route.size() - 1; j++) {
                double edgeSwapCost = calculate(i, j);
                if (edgeSwapCost < bestEdgeSwapCost) {
                    bestEdgeSwapCost = edgeSwapCost;
                    bestVertexI = i;
                    bestVertexJ = j;
                }
                if (TwoOptParameters.IS_GREEDY)
                    if (bestEdgeSwapCost < 0)
                        break outer;
            }
        }
    }

    private void swapIfItIsProfitable() {
        if (bestEdgeSwapCost < 0) {
            Route.swapEdges(bestVertexI, bestVertexJ, route);
        }
    }

    /* Calculate gain when changin edge (v[1], v[1] + 1_ and (v[2], v[2] + 1)
     * into (v[1] + v[2] and (v[1]+ 1, v[2] + 1)
     * if i + 1 == j nothing change -> the first solution which we can take as optimal in the beginning
     *
     * @param src1 first Vertex
     * @param src2 second Vertex
     * @return gain from swaping edges
     */
    private double calculate(int src1, int src2) {
        return ((route.get(src1).distance(route.get(src2)) + route.get(src1 + 1).distance(route.get(src2 + 1)))
                - (((route.get(src1).distance(route.get(src1 + 1))) + route.get(src2).distance(route.get(src2 + 1)))));
    }
}
