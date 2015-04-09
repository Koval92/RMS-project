package production;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LayerFactory {
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

        List<List<Boolean>> imgArray = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            imgArray.add(new ArrayList<>(width));
            for (int j = 0; j < width; j++) {
                imgArray.get(i).add(img.getRGB(j, i) != -1);
            }
        }
        return new Layer(imgArray);
    }

    public static Layer copy(Layer layerToCopy) {
        List<List<Boolean>> copy = new ArrayList<>(layerToCopy.getArray().size());
        for (List<Boolean> row : layerToCopy.getArray()) {
            copy.add(new ArrayList<>(row));
        }
        return new Layer(copy);
    }

    public static Layer createEmptyLayer(int height, int width) {
        List<List<Boolean>> array = new ArrayList<>(height);

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