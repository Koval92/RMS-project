package pathfinder;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface PathPlanningConnection {
    void setProgress(double progress);

    void setCalcTime(double calcTimeInNano);

    void setResults(List<Point> route, Map<String, String> params);

    CostFunctionType getCostFunctionType();

    List<Point> getCopyOfLayerAsListOfPoints();

    List<List<Boolean>> getCopyOfLayerAsTable();

    boolean[][] getCopyOfLayerAsSimpleTable();

    Layer getCopyOfLayer();

    Point getInitialPrinterPosition();
}
