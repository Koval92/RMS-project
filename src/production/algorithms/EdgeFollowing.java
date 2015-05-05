package production.algorithms;

import production.CostFunctionType;
import production.Layer;
import production.PathPlanner;
import production.Utils;

import java.awt.*;

public class EdgeFollowing extends PathPlanner{
    @Override
    protected java.util.List<Point> planPath() {
        boolean[][] remainingPoints = connection.getCopyOfLayerAsSimpleTable();

        boolean[][] G = findEdges(remainingPoints);

        Utils.saveToFile(Utils.draw(new Layer(G), null));
        return null;
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
                if(connection.getCostFunctionType() == CostFunctionType.TIME
                        && (!L[i-1][j+1] || !L[i+1][j+1] || !L[i+1][j-1] || !L[i-1][j-1])) {
                    G[i][j] = true;
                }
            }
        }
        return G;
    }
}
