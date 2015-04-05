package production;

import java.awt.*;
import java.util.List;

public interface PathPlanningListener {
    void setProgress(double progress);

    void setCalcTime(double calcTimeInNano);

    void setCost(double cost);

    void setRoute(List<Point> route);
}
