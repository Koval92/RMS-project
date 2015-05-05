package production;

import javax.imageio.ImageIO;
import java.awt.*;
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

        List<List<Boolean>> array = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
        	List<Boolean> row = new ArrayList<>(width);
            for (int j = 0; j < width; j++) {
                row.add(img.getRGB(j, i) != -1);
            }
            array.add(row);
        }
        return new Layer(array);
    }

    public static Layer createEmptyLayer(int height, int width) {
        List<List<Boolean>> array = new ArrayList<>(height);

        for (int i = 0; i < height; i++) {
            List<Boolean> row = new ArrayList<>(width);
            for (int j = 0; j < width; j++) {
                row.add(false);
            }
            array.add(row);
        }

        return new Layer(array);
    }

    public static Layer createEmptyLayer(int size) {
        return createEmptyLayer(size, size);
    }

    public static int getPixelSize() {
        return pixelSize;
    }

    public static void setPixelSize(int pixelSize) {
        LayerFactory.pixelSize = pixelSize;
    }

    private static int pixelSize = 10;

    public static BufferedImage draw(Layer layer, List<Point> route) {
        BufferedImage image = new BufferedImage(layer.getWidth() * pixelSize, layer.getHeight() * pixelSize, BufferedImage.TYPE_3BYTE_BGR);
        Graphics imageGraphics = image.getGraphics();

        for (int i = 0; i < layer.getHeight(); i++) {
            for (int j = 0; j < layer.getWidth(); j++) {
                if (layer.get(i, j)) {
                    imageGraphics.setColor(Color.BLACK);
                } else {
                    imageGraphics.setColor(Color.WHITE);
                }
                imageGraphics.fillRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
            }
        }

        if (route != null) {
            for (int i = 0; i < route.size() - 1; i++) {
                Point start = route.get(i);
                Point end = route.get(i + 1);
                if (MoveCostCalculator.arePointsAdjacent(start, end))
                    imageGraphics.setColor(Color.GREEN);
                else
                    imageGraphics.setColor(Color.RED);
                ((Graphics2D) imageGraphics).setStroke(new BasicStroke(pixelSize / 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

                imageGraphics.drawLine(
                        pixelSize / 2 + pixelSize * start.y,
                        pixelSize / 2 + pixelSize * start.x,
                        pixelSize / 2 + pixelSize * end.y,
                        pixelSize / 2 + pixelSize * end.x);
            }
        }

        return image;
    }
}
