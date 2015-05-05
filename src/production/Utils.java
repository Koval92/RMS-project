package production;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
    public static int getPixelSize() {
        return pixelSize;
    }

    public static void setPixelSize(int pixelSize) {
        Utils.pixelSize = pixelSize;
    }

    private static int pixelSize = 10;

    public static BufferedImage draw(Layer layer, java.util.List<Point> route) {
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

    public static void saveToFile(BufferedImage image) {
        String directoryName = "results";
        File directory = new File(directoryName);
        if (!directory.exists() && !directory.mkdirs()) {
            Logger.getInstance().log("Creation of directory failed");
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
        File file = new File(directoryName + "/" + currentDate + ".png");

        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Utils() {
        System.out.println("This shouldn't be invoked");
    }
}
