package production;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LayerPanel extends JPanel {
    public static final int pixelSize = 10;
    BufferedImage image;
    Graphics2D imageGraphics;
    private Layer layer;
    private List<Point> route;

    private Logger logger;

    public LayerPanel() {
        super();
        setLayer(LayerFactory.createEmptyLayer(10));
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private void drawLayer(Graphics2D g) {
        if (layer == null) return;

        //Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < layer.getHeight(); i++) {
            for (int j = 0; j < layer.getWidth(); j++) {
                drawPixel(g, i, j);
            }
        }
    }

    private void drawRoute(Graphics2D g) {
        if (route == null) return;

        for (int i = 0; i < route.size() - 1; i++) {
            Point start = route.get(i);
            Point end = route.get(i + 1);
            if (MoveCostCalculator.arePointsAdjacent(start, end))
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.RED);
            g.setStroke(new BasicStroke(pixelSize / 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

            g.drawLine(
                    pixelSize / 2 + pixelSize * start.y,
                    pixelSize / 2 + pixelSize * start.x,
                    pixelSize / 2 + pixelSize * end.y,
                    pixelSize / 2 + pixelSize * end.x);
        }
    }

    private void drawPixel(Graphics2D g, int i, int j) {
        if (layer.get(i, j)) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        setBackground(Color.WHITE);
        super.paintComponent(g);

        drawLayer(imageGraphics);
        drawRoute(imageGraphics);

        g.drawImage(image, 0, 0, null);
    }

    private void saveToFile() {
        String directoryName = "results";
        File directory = new File(directoryName);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.log("Creating of directory failed");
            return;
        }

        String fileName = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-SSS").format(new Date()) + ".png";
        File file = new File(directoryName + "/" + fileName);

        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
        this.route = null;
        image = new BufferedImage(layer.getWidth() * pixelSize, layer.getHeight() * pixelSize, BufferedImage.TYPE_3BYTE_BGR);
        imageGraphics = image.createGraphics();
        this.setPreferredSize(new Dimension(pixelSize * layer.getWidth(), pixelSize * layer.getHeight()));
        this.repaint();
        this.revalidate();
    }

    public void setRoute(List<Point> route) {
        this.route = route;
        this.repaint();
        this.revalidate();

        saveToFile();
    }
}
