package pathfinder.algorithms.route;

import java.awt.*;

public class TableOfNeighbours {
    private int[][] nrOfNeighbours;
    private int height;
    private int width;

    public TableOfNeighbours(int[][] nrOfNeighbours) {
        this.nrOfNeighbours = copyArray(nrOfNeighbours);
        height = nrOfNeighbours.length;
        width = nrOfNeighbours[0].length;
    }

    //konstruktor kopiuj¹cy
    public static TableOfNeighbours newInstance(TableOfNeighbours tableOfNeighbours) {
        return new TableOfNeighbours(tableOfNeighbours.getNrOfNeighbours());
    }

    public int[][] getNrOfNeighbours() {
        return nrOfNeighbours;
    }

    public int get(int y, int x) {
        return nrOfNeighbours[y][x];
    }

    public int get(Point point) {
        return nrOfNeighbours[(int)(point.getX())][(int)(point.getY())];
    }


    public void updateNeighbours(int y, int x) {
        for (int i = -1; i <= 1; i++)
            if (isInRange(y + 1, x + i))
                nrOfNeighbours[y+1][x+i]--;
        if(isInRange(y, x - 1))
            nrOfNeighbours[y][x-1]--;
        if(isInRange(y, x + 1))
            nrOfNeighbours[y][x+1]--;
        for(int i = -1; i <= 1; i++)
            if (isInRange(y - 1, x + i))
                nrOfNeighbours[y-1][x+i]--;
    }

    public void updateNeighbours(Point point) {
        updateNeighbours((int)(point.getX()),(int)(point.getY()));
    }

    private boolean isInRange(int y, int x) {
        return y < height && y >= 0 && x < width && x >= 0;
    }

    public int[][] copyArray(int[][] old) {
        int[][] copy = new int[old.length][old[0].length];
        for(int i = 0; i < old.length; i++)
            for(int j = 0; j < old[0].length; j++)
                copy[i][j] = old[i][j];
        return copy;
    }

    public static int[][] findNeighbours(boolean[][] layer) {
        FindNeighbours findNeighbours = new FindNeighbours();
        return findNeighbours.findNeighbours(layer);
    }
}

class FindNeighbours {
    private int height;
    private int width;
    private boolean[][] layer;

    public int[][] findNeighbours(boolean[][] layer) {
        this.height = layer.length;
        this.width = layer[0].length;
        this.layer = layer;
        int[][] nrOfNeighbours = new int[height][width];
        for (int i = 0; i < width; i ++) {
            for (int j = 0; j < height; j++) {
                nrOfNeighbours[j][i] = lookAround(j, i);
            }
        }
        return nrOfNeighbours;
    }

    private int lookAround(int y, int x) {
        int count = 0;
        for (int i = -1; i <= 1; i++)
            if (layerValue(y + 1, x + i))
                count++;
        if (layerValue(y, x - 1))
            count++;
        if(layerValue(y, x + 1))
            count++;
        for(int i = -1; i <= 1; i++)
            if (layerValue(y - 1, x + i))
                count++;
        return count;
    }

    private boolean layerValue(int y, int x) {
        return y < height && y >= 0 && x < width && x >= 0 && layer[y][x];
    }
}
