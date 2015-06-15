package pathfinder.algorithms.route;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;


// klasa przechowuj¹ca drogê i jej d³ugoœæ
public class RouteAndDistance implements Comparable<RouteAndDistance>{
    private List<Point> route;
    private double distance;

    public RouteAndDistance() {
        this.route = new ArrayList<>();
        distance =0;
    }

    public RouteAndDistance(List<Point> route, double distance) {
        this.distance = distance;
        this.route = route;
    }

    public List<Point> getRoute() {
        return route;
    }

    public void setRoute(List<Point> route) {
        this.route = route;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(RouteAndDistance otherRoute) {
        if (otherRoute == null)
            return -1;
        if(this.getDistance() > otherRoute.getDistance()) {
            return 1;
        } else if (this.getDistance() < otherRoute.getDistance())
            return -1;
        else
            return 0;
    }
}



