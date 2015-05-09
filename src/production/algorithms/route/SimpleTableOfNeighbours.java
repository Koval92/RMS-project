package production.algorithms.route;

import java.awt.*;

public class SimpleTableOfNeighbours {

    private LayerAsSimpleTable layerAsSimpleTable;
    int[][] nrOfNeighbours;
    private int height;
    private int width;

    public SimpleTableOfNeighbours(LayerAsSimpleTable layerAsSimpleTable) {
        this.layerAsSimpleTable = layerAsSimpleTable;
        height = layerAsSimpleTable.getHeight();
        width = layerAsSimpleTable.getWidth();
        findNeighbours();
    }

    public SimpleTableOfNeighbours(LayerAsSimpleTable layerAsSimpleTable, int[][] copyArray) {
        this.layerAsSimpleTable = layerAsSimpleTable;
        height = layerAsSimpleTable.getHeight();
        width = layerAsSimpleTable.getWidth();
        this.nrOfNeighbours = copyArray;
    }

    public void updateNeighbours(Point point) {
        updateNeighbours((int)(point.getX()),(int)(point.getY()));
    }

    public void updateNeighbours(int y, int x) {
        for (int i = -1; i <= 1; i++)
            if (layerAsSimpleTable.get(y + 1, x + i))
                nrOfNeighbours[y+1][x+i]--;
        if(layerAsSimpleTable.get(y, x - 1))
            nrOfNeighbours[y][x-1]--;
        if(layerAsSimpleTable.get(y, x + 1))
            nrOfNeighbours[y][x+1]--;
        for(int i = -1; i <= 1; i++)
            if (layerAsSimpleTable.get(y - 1, x + i))
                nrOfNeighbours[y-1][x+i]--;
    }

    public int get(int y, int x) {
        return nrOfNeighbours[y][x];
    }

    public int get(Point point) {
        return nrOfNeighbours[(int)(point.getX())][(int)(point.getY())];
    }

    private void findNeighbours() {
        nrOfNeighbours = new int[height][width];
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < height; j++) {
                nrOfNeighbours[j][i] = lookAround(j, i);
            }
        }
    }

    private int lookAround(int y, int x) {
        int count = 0;
        for (int i = -1; i <= 1; i++)
            if (layerAsSimpleTable.get(y + 1, x + i))
                count++;
        if(layerAsSimpleTable.get(y, x - 1))
            count++;
        if(layerAsSimpleTable.get(y, x + 1))
            count++;
        for(int i = -1; i <= 1; i++)
            if (layerAsSimpleTable.get(y - 1, x + i))
                count++;
        return count;
    }

    public int[][] copyArray() {
        int[][] copy = new int[height][width];
        for(int i = 0; i < nrOfNeighbours.length; i++)
            for(int j = 0; j < nrOfNeighbours[0].length; j++)
                copy[i][j] = nrOfNeighbours[i][j];
        return copy;
    }
}
