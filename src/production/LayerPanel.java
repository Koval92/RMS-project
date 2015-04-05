package production;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LayerPanel extends JPanel {
    public static final int pixelSize = 10;
    private Layer layer;
    private List<Point> route;

    public LayerPanel() {
        super();
        setLayer(LayerFactory.createEmptyLayer(10));
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
        if (layer.get(i, j))
            g.fillRect(j * pixelSize, i * pixelSize, pixelSize, pixelSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        setBackground(Color.WHITE);
        super.paintComponent(g);
        drawLayer((Graphics2D) g);
        drawRoute((Graphics2D) g);
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
        this.setPreferredSize(new Dimension(pixelSize * layer.getWidth(), pixelSize * layer.getHeight()));
        this.repaint();
        this.revalidate();
    }

    public void setRoute(List<Point> route) {
        this.route = route;
        this.repaint();
        this.revalidate();
    }
}
