package production;

import java.util.ArrayList;

public class Layer {
    ArrayList<ArrayList<Boolean>> array;

    public Layer(ArrayList<ArrayList<Boolean>> array) {
        this.array = array;
    }

    public ArrayList<ArrayList<Boolean>> getArray() {
        return array;
    }

    public void setArray(ArrayList<ArrayList<Boolean>> array) {
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

    public void print() {
        for (ArrayList<Boolean> row : array) {
            for (Boolean point : row) {
                System.out.print((point ? 'x' : '_') + " ");
            }
            System.out.println();
        }
    }
}
