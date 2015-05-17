package pathfinder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Utils {
    private static Logger logger = Logger.getInstance();

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
                }
                else {
                    imageGraphics.setColor(Color.WHITE);
                }
                imageGraphics.fillRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
            }
        }

        if (route != null) {
            for (int i = 0; i < route.size() - 1; i++) {
                Point start = route.get(i);
                Point end = route.get(i + 1);
                if (MoveCostCalculator.arePointsAdjacent(start, end)) {
                    imageGraphics.setColor(Color.GREEN);
                    ((Graphics2D) imageGraphics).setStroke(new BasicStroke(pixelSize / 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                }
                else {
                    imageGraphics.setColor(Color.RED);
                    ((Graphics2D) imageGraphics).setStroke(new BasicStroke(pixelSize / 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                }

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
        String currentDate = getCurrentTime();
        saveToFile(image, currentDate, null);
    }

    public static void saveToFile(BufferedImage image, String fileName, String directoryName) {
        directoryName = createDirectory(directoryName);
        if (directoryName == null) return;

        File file = new File(directoryName + "/" + fileName + ".png");

        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToFile(Map<String, String> params) {
        String currentDate = getCurrentTime();
        saveToFile(params, currentDate, null);
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
    }

    public static void saveToFile(Map<String, String> params, String fileName, String directoryName) {
        directoryName = createDirectory(directoryName);
        if (directoryName == null) return;

        FileWriter fileWriter = null;
        try {
             fileWriter = new FileWriter(directoryName + "/" + fileName + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(fileWriter == null) {
            logger.log("Error in creating FileWriter");
            return;
        }

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                fileWriter.write(entry.getKey() + " : " + entry.getValue() + System.lineSeparator());
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createDirectory(String directoryName) {
        if(directoryName == null) {
            directoryName = "results";
        }

        File directory = new File(directoryName);
        if (!directory.exists() && !directory.mkdirs()) {
            Logger.getInstance().log("Creation of directory failed");
            return null;
        }
        return directoryName;
    }

    public static Map<CostFunctionType, Double> calculateCosts(List<Point> route) {
        Map<CostFunctionType, Double> costs = new HashMap<>();

        costs.put(CostFunctionType.TIME, MoveCostCalculator.calculate(route, CostFunctionType.TIME));
        costs.put(CostFunctionType.DISTANCE, MoveCostCalculator.calculate(route, CostFunctionType.DISTANCE));
        costs.put(CostFunctionType.ENERGY, MoveCostCalculator.calculate(route, CostFunctionType.ENERGY));

        return costs;
    }

    public static List<Point> toListOfPoints(boolean[][] array) {
        List<Point> points = new ArrayList<>();

        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[i].length; j++) {
                if(array[i][j]) {
                    points.add(new Point(i, j));
                }
            }
        }

        return points;
    }

    public static boolean isEmpty(boolean[][] array) {
        for (boolean[] row : array) {
            for (boolean pixel : row) {
                if(pixel)
                    return false;
            }
        }

        return true;
    }

    public static List<List<Boolean>> transpose(List<List<Boolean>> array) {
        List<List<Boolean>> rotatedArray = new ArrayList<>();

        final int N = array.get(0).size();
        for (int i = 0; i < N; i++) {
            List<Boolean> col = new ArrayList<>();
            for (List<Boolean> row : array) {
                col.add(row.get(i));
            }
            rotatedArray.add(col);
        }

        return rotatedArray;
    }

    public static Point findClosest(Point currentPosition, boolean[][] array, CostFunctionType costType) {
        List<Point> points = Utils.toListOfPoints(array);

        if(currentPosition == null) {
            logger.log("Current point shouldn't be null!");
            return null;
        }

        if(points == null) {
            logger.log("List shouldn't be null!");
            return null;
        }
        Point closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Point point : points) {
            double distance = MoveCostCalculator.calculate(currentPosition, point, costType);
            if(distance < minDistance) {
                closest = point;
                minDistance = distance;
            }
        }

        return closest;
    }

    public static Point findNeighbour(Point currentPosition, boolean[][] edges, CostFunctionType costType) {
        int i = currentPosition.x;
        int j = currentPosition.y;

        int height = edges.length;
        int width = edges[0].length;

        // point has at least one neighbor side-by-side
        if((i-1 >= 0) && edges[i-1][j])
            return new Point(i-1, j);
        if((j+1 < width) && edges[i][j+1])
            return new Point(i, j+1);
        if((i+1 < height) && edges[i+1][j])
            return new Point(i+1, j);
        if((j-1 >= 0) &&edges[i][j-1]) {
            return new Point(i, j-1);
        }

        // point has at least one diagonal neighbor and cost function is appropriate
        if(costType == CostFunctionType.TIME) {
            if((i-1 >= 0 && j+1 < width )  && edges[i-1][j+1])
                return new Point(i-1, j+1);
            if((i+1 < height && j+1 < width) && edges[i+1][j+1])
                return new Point(i+1, j+1);
            if((i+1 < height && j-1 >= 0) && edges[i+1][j-1])
                return new Point(i+1, j-1);
            if((i-1 >= 0 && j-1 >= 0) && edges[i-1][j-1])
                return new Point(i-1, j-1);
        }

        return null;
    }

    private Utils() {
        System.out.println("This shouldn't be invoked");
    }
}
