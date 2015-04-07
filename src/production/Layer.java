package production;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Layer {
    List<List<Boolean>> array;

    public Layer(List<List<Boolean>> array) {
        this.array = array;
    }

    public List<List<Boolean>> getArray() {
        return array;
    }

    public void setArray(List<List<Boolean>> array) {
        this.array = array;
    }

    public int getWidth() {
        if (getHeight() == 0)
            return 0;
        return array.get(0).size();
    }

    public int getHeight() {
        if (array == null)
            return 0;
        return array.size();
    }

    public boolean get(int i, int j) {
        return array.get(i).get(j);
    }

    public List<Point> toPoints() {
        List<Point> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < array.get(i).size(); j++) {
                if (get(i, j)) {
                    list.add(new Point(i, j));
                }
            }
        }
        return list;
    }

    public void printAsTable() {
        for (List<Boolean> row : array) {
            for (Boolean point : row) {
                System.out.print((point ? 'x' : '_') + " ");
            }
            System.out.println();
        }
    }

    public void printAsPoints() {
        List<Point> list = this.toPoints();
        for (Point point : list) {
            System.out.println("(" + point.x + ", " + point.y + ")");
        }
    }
}
