package production.algorithms;

import production.Layer;
import production.PathPlanner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotr on 2015-04-21.
 */
public class Spiral extends PathPlanner{
    @Override
    protected void setUp() {
        connection.getCopyOfLayerAsTable();
    }

    public Spiral() {

    }

    @Override
    protected List<Point> planPath() {

//        System.out.println("width: " + layer.getWidth());
//        System.out.println("height: " + layer.getHeight());
        List<Point> route = new ArrayList<Point>();
        route.add(new Point(0,0));
        return route;
    }

    @Override
    protected String getName() {
        return "Spiral algorithm";
    }
}
