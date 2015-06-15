package pathfinder.algorithms;

import pathfinder.ParamReader;
import pathfinder.PathPlanner;

import java.awt.*;
import java.io.File;
import java.util.List;

public class GreedyHarmony extends PathPlanner {

    private Greedy greedy;
    private HarmonySearch harmonySearch;

    public GreedyHarmony(Greedy greedy, HarmonySearch harmonySearch) {
        this.greedy = greedy;
        this.harmonySearch = harmonySearch;
    }

    @Override
    protected void setUp() {
        getParametersFromFile();
    }

    private void getParametersFromFile() {
        File file = new File(System.getProperty("user.dir") + "/params/GreedyHarmony.txt");
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
        HarmonySearchParameters.set(Long.parseLong(params.get("seed")),
                Integer.parseInt(params.get("memorySize")),
                Integer.parseInt(params.get("nrOfIterations")),
                Double.parseDouble(params.get("memoryProbability")));
    }

    @Override
    protected List<Point> planPath() {
        harmonySearch.setSolutionFromOtherAlgorithm(greedy.planPath());
        return harmonySearch.planPath();
    }
}
