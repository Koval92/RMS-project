package production;

import java.awt.*;
import java.util.List;

public interface PathPlanningConnection {
    void setProgress(double progress);

    void setCalcTime(double calcTimeInNano);

    void setCost(double cost);

    void setRoute(List<Point> route);

    CostFunctionType getCostFunctionType();

    List<Point> getCopyOfLayerAsList();

    List<List<Boolean>> getCopyOfLayerAsTable();

    boolean[][] getCopyOfLayerAsSimpleTable();

    Layer getCopyOfLayer();
}
