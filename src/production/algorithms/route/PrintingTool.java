package production.algorithms.route;

import production.PathPlanningConnection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrintingTool extends LayerAsSimpleTable {
    private List<Point> route;
    private List<Point> list;
    private Point currentPosition;

    public Point getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Point currentPosition) {
        this.currentPosition = currentPosition;
    }

    public PrintingTool(PathPlanningConnection connection) {
        super(connection);
        route = new ArrayList<>();
        list = connection.getCopyOfLayerAsListOfPoints();
    }

    public List<Point> getRoute() {
        return route;
    }

    public int getNumberOfPoints() {
        return list.size();
    }

    public Point getPointFromList(int index) {
        return list.get(index);
    }

    public void print() {
        route.add(currentPosition);
        setFalse((int) currentPosition.getX(), (int) currentPosition.getY());
        removeFromList(currentPosition);
    }

    public void removeFromList(int y, int x) {
        list.remove(new Point(y, x));
    }

    public void removeFromList(Point p) {
        list.remove(p);
    }

    public void print(int y, int x) {
        currentPosition = new Point(y, x);
        route.add(new Point(y, x));
        setFalse(y, x);
        removeFromList(y, x);
    }

    public void print(Point p) {
        currentPosition = p;
        route.add(p);
        setFalse((int) p.getX(), (int) p.getY());
        removeFromList(p);
    }

    public Point getFirstPoint() {
        return currentPosition = list.get(0);
    }
}
