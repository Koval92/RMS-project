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

    @Override
    public void paintComponent(Graphics g) {
        setBackground(Color.WHITE);
        super.paintComponent(g);

        image = LayerFactory.draw(layer, route, pixelSize);
        g.drawImage(image, 0, 0, null);
    }

    private void saveToFile() {
        String directoryName = "results";
        File directory = new File(directoryName);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.log("Creation of directory failed");
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

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
        this.route = null;
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
