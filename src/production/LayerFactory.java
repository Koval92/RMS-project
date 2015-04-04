package production;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LayerFactory {
    public static ArrayList<ArrayList<Boolean>> createFromFile(String fileName) {
        File file = new File(fileName);
        return createFromFile(file);
    }

    public static ArrayList<ArrayList<Boolean>> createFromFile(File file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        if (img == null) {
            System.out.println("File doesn't exist or it's not an image");
            return null;
        }

        int width = img.getWidth();
        int height = img.getHeight();

        ArrayList<ArrayList<Boolean>> imgArray = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            imgArray.add(new ArrayList<>(width));
            for (int j = 0; j < width; j++) {
                imgArray.get(i).add(img.getRGB(j, i) != -1);
            }
        }
        return imgArray;
    }

    public static void printLayer(ArrayList<ArrayList<Boolean>> imgArray) {
        for (ArrayList<Boolean> row : imgArray) {
            for (Boolean point : row) {
                System.out.print((point ? 'x' : ' ') + " ");
            }
            System.out.println();
        }
    }

    public static ArrayList<ArrayList<Boolean>> copyFromLayer(ArrayList<ArrayList<Boolean>> layerToCopy) {
        ArrayList<ArrayList<Boolean>> copy = new ArrayList<>(layerToCopy.size());
        for (ArrayList<Boolean> row : layerToCopy) {
            copy.add(new ArrayList<>(row));
        }
        return copy;
    }

    public static ArrayList<ArrayList<Boolean>> createEmptyLayer(int height, int width) {
        ArrayList<ArrayList<Boolean>> layer = new ArrayList<>(height);

        for (int i = 0; i < height; i++) {
            layer.add(new ArrayList<>(width));
            for (int j = 0; j < width; j++) {
                layer.get(i).add(false);
            }
        }

        return layer;
    }

    public static ArrayList<ArrayList<Boolean>> createEmptyLayer(int size) {
        return createEmptyLayer(size, size);
    }
}