package production;

import java.awt.*;
import java.util.ArrayList;

enum NearestPointFinderType {
    DISTANCE, TIME, ENERGY
}

public class FindNearestPoint {
    public static NearestPointFinderType type = NearestPointFinderType.DISTANCE;
    private static ArrayList<ArrayList<Boolean>> layer;

    private FindNearestPoint() {
    }

    public static void setLayer(ArrayList<ArrayList<Boolean>> layer) {
        FindNearestPoint.layer = layer;
    }

    public static void setType(NearestPointFinderType type) {
        FindNearestPoint.type = type;
    }


    public static Point byEnergy(Point p) {
        return null;
    }

    public static Point byDistanceTo(Point p) {
        return null;
    }

    public static Point byTimeTo(Point p) {
        return null;
    }

    public static Point to(Point p) {
        switch (type) {
            case DISTANCE:
                return byDistanceTo(p);
            case TIME:
                return byTimeTo(p);
            case ENERGY:
                return byEnergy(p);
            default:
                return byDistanceTo(p);
        }
    }
}


