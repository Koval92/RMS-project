package pathfinder.algorithms;


import pathfinder.ParamReader;
import pathfinder.PathPlanner;

import java.awt.*;
import java.io.File;
import java.util.List;

//polaczenie greedy z simulated annealing
public class GreedyAnnealing extends PathPlanner{

    Greedy greedy;
    SimulatedAnnealing simulatedAnnealing;

    public GreedyAnnealing(Greedy greedy) {
        this.greedy = greedy;
    }

    List<Point> route;


    @Override
    protected void setUp() {
        simulatedAnnealing = new SimulatedAnnealing();
        getParametersFromFile();
    }

    private void getParametersFromFile() {
        File file = new File(System.getProperty("user.dir") + "/params/GreedyAnnealing.txt");
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
                Float.parseFloat(params.get("weightOfNeighbours")),
                Float.parseFloat(params.get("weightOfDistance")));
        SimulatedAnnealingParameters.set(Long.parseLong(params.get("seed")),
                Double.parseDouble(params.get("temperatureMin")),
                Double.parseDouble(params.get("coolingRate")),
                Integer.parseInt(params.get("iterationsOnTemperature")));
    }

    @Override
    protected List<Point> planPath() {

        simulatedAnnealing.setFirstSolutionFromOtherAlgorithm(greedy.planPath());
        route = simulatedAnnealing.planPath();
        return route;
    }
}
