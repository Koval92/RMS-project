package production.algorithms;

import production.PathPlanner;

import java.awt.*;
import java.util.List;

public class LeftToRight extends PathPlanner {
    @Override
    protected List<Point> planPath() {
        return listener.getCopyOfLayerAsList();
    }

    @Override
    protected String getName() {
        return "Left-to-right";
    }
}
