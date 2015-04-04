import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LayerFactory {
    public static boolean[][] createFromFile(String fileName) {
        File file = new File(fileName);
        return createFromFile(file);
    }

    public static boolean[][] createFromFile(File file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        if(img==null) {
            System.out.println("File doesn't exist or it's not an image");
            return null;
        }


        int width = img.getWidth();
        int height = img.getHeight();

        boolean[][] imgArray = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++)
                imgArray[i][j] = img.getRGB(j, i) != -1;
        }

        return imgArray;
    }

    public static void printLayer(boolean[][] imgArray) {
        for (boolean[] anImgArray : imgArray) {
            for (int j = 0; j < imgArray.length; j++) {
                System.out.print((anImgArray[j] ? 'x' : ' ') + " ");
            }
            System.out.println();
        }
    }

    public static boolean[][] copyFromLayer(boolean[][] layerToCopy) {
        boolean[][] copy = new boolean[layerToCopy.length][];
        for(int i=0; i< copy.length; i++)
        {
            copy[i] = layerToCopy[i].clone();
        }
        return copy;
    }

    public static boolean[][] createEmptyLayer(int height, int width) {
        return new boolean[height][width];
    }

    public static boolean[][] createEmptyLayer(int size) {
        return new boolean[size][size];
    }
}