package production;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LayerFactory {
    public static Layer create(ArrayList<ArrayList<Boolean>> array) {
        return new Layer(array);
    }

    public static Layer create(boolean[][] array) {
        // TODO Create from boolean[][]
        return null;
    }

    public static Layer createFromFile(String fileName) {
        File file = new File(fileName);
        return createFromFile(file);
    }

    public static Layer createFromFile(File file) {
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
        return new Layer(imgArray);
    }

    public static Layer copyFromLayer(Layer layerToCopy) {
        ArrayList<ArrayList<Boolean>> copy = new ArrayList<>(layerToCopy.getArray().size());
        for (ArrayList<Boolean> row : layerToCopy.getArray()) {
            copy.add(new ArrayList<>(row));
        }
        return new Layer(copy);
    }

    public static Layer createEmptyLayer(int height, int width) {
        ArrayList<ArrayList<Boolean>> array = new ArrayList<>(height);

        for (int i = 0; i < height; i++) {
            array.add(new ArrayList<>(width));
            for (int j = 0; j < width; j++) {
                array.get(i).add(false);
            }
        }

        return new Layer(array);
    }

    public static Layer createEmptyLayer(int size) {
        return createEmptyLayer(size, size);
    }
}