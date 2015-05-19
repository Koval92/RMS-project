package pathfinder.algorithms;

import pathfinder.ParamReader;
import pathfinder.PathPlanner;

import java.awt.*;
import java.io.File;
import java.util.List;


public class GreedyTwoOpt extends PathPlanner {

    private Greedy greedy;
    private TwoOpt twoOpt;


    public GreedyTwoOpt(Greedy greedy) {
        this.greedy = greedy;
    }

    @Override
    protected void setUp() {
        twoOpt = new TwoOpt();
        getParametersFromFile();
    }

    private void getParametersFromFile() {
        File file = new File(System.getProperty("user.dir") + "/params/GreedyTwoOpt.txt");
        params.putAll(ParamReader.getParamsForSingleAlgorithm(file));
        setParameters();
    }

    private void setParameters() {
        GreedyParameters.set(Long.parseLong(params.get("seed")),
                Integer.parseInt(params.get("nrOfThreads")),
                Integer.parseInt(params.get("nrOfNeighboursForStartingPoint")),
                Boolean.parseBoolean(params.get("sameStartingPoint")),
                Integer.parseInt(params.get("nrOfPointsNeededToCheckArray")),
                Integer.parseInt(params.get("bestNrOfNeighbours")),
                Double.parseDouble(params.get("weightOfNeighbours")),
                Double.parseDouble(params.get("weightOfDistance")));
        TwoOptParameters.set(Boolean.parseBoolean(params.get("isGreedy")),
                Integer.parseInt(params.get("maxNrOfIterations")));
    }

    @Override
    protected List<Point> planPath() {
        twoOpt.setSolutionFromOtherAlgorithm(greedy.planPath());
        return twoOpt.planPath();
    }
}
