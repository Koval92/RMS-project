package production.algorithms;

import production.PathPlanner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LeftToRight extends PathPlanner {
    @Override
    protected List<Point> planPath() {
        boolean[][] layer = listener.getCopyOfLayerAsSimpleTable();
        List<Point> route = new ArrayList<>();

        for (int i = 0; i < layer.length; i++) {
            for (int j = 0; j < layer[i].length; j++) {
                if (layer[i][j])
                    route.add(new Point(i, j));
            }
        }

        return route;
    }

    @Override
    protected String getName() {
        return "Left-to-right";
    }
}
