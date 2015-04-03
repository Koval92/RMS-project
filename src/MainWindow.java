import javax.swing.*;
import java.io.File;

public class MainWindow extends JFrame {
    private JTextField fileNameField;
    private JButton loadButton;
    private JPanel rootPanel;
    private LayerWindow layerWindow;

    MainWindow() {
        setLocationRelativeTo(null);
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setActionListeners();
        pack();

//        ArrayList<Point> route = new ArrayList<>();
//        route.add(new Point(0, 0));
//        route.add(new Point(0, 1));
//        route.add(new Point(1, 2));
//        route.add(new Point(0, 2));
//        route.add(new Point(1, 1));
//        route.add(new Point(2, 3));
//        route.add(new Point(2, 2));
//        route.add(new Point(6, 6));
//        route.add(new Point(9, 6));
//
//        layerWindow.setRoute(route);
    }

    private void setActionListeners() {
        // loading layer
        loadButton.addActionListener(e -> {
            String fileName = fileNameField.getText();
            File file = new File(fileName);
            if (file.exists()) {
                boolean[][] layer = LayerFactory.createFromFile(fileName);
                //LayerFactory.printLayer(layer);
                layerWindow.setLayer(layer);
                pack();
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect file name/path to file!");
            }
        });
        fileNameField.addActionListener(loadButton.getActionListeners()[0]);

        // executing algorithms
    }
}