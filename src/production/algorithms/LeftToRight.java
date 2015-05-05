package production.algorithms;

import production.PathPlanner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LeftToRight extends PathPlanner {
    boolean[][] layer;
    List<Point> route;

    @Override
    protected List<Point> planPath() {
        for (int i = 0; i < layer.length; i++) {
            for (int j = 0; j < layer[i].length; j++) {
                if (layer[i][j])
                    route.add(new Point(i, j));
            }
        }

        return route;
    }

    @Override
    protected void setUp() {
        layer = connection.getCopyOfLayerAsSimpleTable();
        route = new ArrayList<>();
    }
}
