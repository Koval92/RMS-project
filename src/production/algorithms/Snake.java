package production.algorithms;

import production.PathPlanner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Snake extends PathPlanner {
    boolean[][] layer;
    List<Point> route;

    @Override
    protected List<Point> planPath() {
        // TODO starting from closest line end
        for (int i = 0; i < layer.length; i++) {
            if (i % 2 == 0) {
                for (int j = 0; j < layer[i].length; j++) {
                    if (layer[i][j])
                        route.add(new Point(i, j));
                }
            } else {
                for (int j = layer[i].length - 1; j >= 0; j--) {
                    if (layer[i][j])
                        route.add(new Point(i, j));
                }
            }
        }

        return route;
    }

    @Override
    protected String getName() {
        return "Snake";
    }

    @Override
    protected void setUp() {
        layer = connection.getCopyOfLayerAsSimpleTable();
        route = new ArrayList<>();
    }
}
