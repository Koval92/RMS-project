package pathfinder.algorithms;

import pathfinder.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class EdgeFollowing extends PathPlanner{
    List<Point> route;
    boolean[][] remainingPoints;
    private CostFunctionType costType;

    @Override
    protected void setUp() {
        remainingPoints = connection.getCopyOfLayerAsSimpleTable();
        route = new ArrayList<>();
        costType = CostFunctionType.valueOf(params.get("cost_type"));
    }

    @Override
    protected java.util.List<Point> planPath() {
        Point currentPosition = new Point(0, 0);

        while(! Utils.isEmpty(remainingPoints)) {
            boolean[][] edges = findEdges(remainingPoints);

            currentPosition = Utils.findClosest(currentPosition, edges, costType);

            while(true) {
                if(currentPosition == null) {
                    logger.log("current position is null!");
                    return null;
                }
                route.add(currentPosition);
                int i = (int) currentPosition.getX();
                int j = (int) currentPosition.getY();
                remainingPoints[i][j] = false;
                edges[i][j] = false;

                Point neighbour;
                if((neighbour = Utils.findNeighbour(currentPosition, edges, costType)) == null)
                    break;
                else
                    currentPosition = neighbour;
            }
        }

        return route;
    }

    private boolean[][] findEdges(boolean[][] L) {
        int height = L.length;
        int width = L[0].length;

        boolean[][] G = new boolean[height][width];

        for (int i=0; i < height; i++)
        {
            G[i] = new boolean[width];
            for (int j=0; j< width; j++)
            {
                // point is empty
                if(!L[i][j]) {
                    G[i][j] = false;
                    continue;
                }
                // point is on the edge
                if(i == 0 || j == 0 || i == height - 1 || j == width - 1) {
                    G[i][j] = true;
                    continue;
                }
                // point has at least one empty neighbor side-by-side
                if(!L[i-1][j] || !L[i][j+1] || !L[i+1][j] || !L[i][j-1]) {
                    G[i][j] = true;
                    continue;
                }
                // point has at least one empty diagonal neighbor and cost function is appropriate
                if(costType == CostFunctionType.TIME
                        && (!L[i-1][j+1] || !L[i+1][j+1] || !L[i+1][j-1] || !L[i-1][j-1])) {
                    G[i][j] = true;
                }
            }
        }
        return G;
    }
}
