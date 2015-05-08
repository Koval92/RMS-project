package production.algorithms.route;

import production.PathPlanningConnection;

public class LayerAsSimpleTable implements PrintingLayer{

    boolean[][] layer;

    public LayerAsSimpleTable(PathPlanningConnection connection){
        this.layer = connection.getCopyOfLayerAsSimpleTable();
    }

    public boolean get(int y, int x) {
        return layer[y][x];
    }

    public void setTrue(int y, int x) {
        layer[y][x] = true;
    }

    public void setFalse(int y, int x) {
        layer[y][x] = false;
    }


    public int getWidth() {
        if (getHeight() > 0)
            return layer[0].length;
        return 0;
    }

    public int getHeight() {
        return layer.length;
    }

}
