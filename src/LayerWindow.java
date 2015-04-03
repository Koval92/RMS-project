import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LayerWindow extends JPanel {
    public static final int pixelSize = 15;
    private boolean[][] layer;
    private List<Point> route;

    private void drawLayer(Graphics2D g) {
        //Graphics2D g2d = (Graphics2D) g;
        for(int i=0; i<layer.length; i++) {
            for(int j=0; j<layer[i].length; j++) {
                drawPixel(g, i, j);
            }
        }
    }

    private void drawRoute(Graphics2D g) {
        if(route == null)
            return;

        for(int i = 0; i<route.size() - 1; i++) {
            Point start = route.get(i);
            Point end = route.get(i+1);
            if(arePointsAdjacent(start, end))
                g.setColor(Color.GREEN);
            else g.setColor(Color.RED);
            g.setStroke(new BasicStroke(pixelSize/3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));


            g.drawLine( pixelSize/2 + pixelSize * start.y,
                        pixelSize/2 + pixelSize * start.x,
                        pixelSize/2 + pixelSize * end.y,
                        pixelSize/2 + pixelSize * end.x);
        }
    }

    private void drawPixel(Graphics2D g, int i, int j) {
        if(layer[i][j])
            g.fillRect(j*pixelSize, i*pixelSize, pixelSize, pixelSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        setBackground(Color.WHITE);
        super.paintComponent(g);
        drawLayer((Graphics2D) g);
        drawRoute((Graphics2D) g);
    }

    public void setLayer(boolean[][] layer) {
        this.layer = layer;
        if(layer.length > 0) {
            this.setPreferredSize(new Dimension(pixelSize * layer[0].length, pixelSize * layer.length));
            this.repaint();
            this.revalidate();
        }
    }

    public void setRoute(List<Point> route) {
        this.route = route;
        this.repaint();
        this.revalidate();
    }

    private boolean arePointsAdjacent(Point p1, Point p2) {
        return !(Math.abs(p1.x - p2.x) > 1 || Math.abs(p1.y - p2.y) > 1);
    }
}
