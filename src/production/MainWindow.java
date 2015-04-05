package production;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainWindow extends JFrame implements PathPlanningListener {
    private JTextField fileNameField;
    private JButton loadButton;
    private JPanel rootPanel;
    private JPanel layerPanelAsJPanel;
    private JPanel algorithmPanel;
    private JTextField costTextField;
    private JTextField calcTimeTextField;
    private LayerPanel layerPanel;

    MainWindow() {
        this.setTitle("Pathfinder");
        //setLocationRelativeTo(null);
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        configureLoadButton();
        createAlgorithmPanel();

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
//        layerPanel.setRoute(route);
    }

    public static void main(String[] args) {
        setLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            MainWindow app = new MainWindow();
        });
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void configureLoadButton() {
        // loading layer
        loadButton.addActionListener(e -> {
            String fileName = fileNameField.getText();
            File file = new File(fileName);
            if (file.exists()) {
                Layer layer = LayerFactory.createFromFile(fileName);
                layerPanel.setLayer(layer);

                pack();
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect file name/path to file!");
            }
        });
        fileNameField.addActionListener(loadButton.getActionListeners()[0]);
    }

    private void createUIComponents() {
        layerPanelAsJPanel = new LayerPanel();
        layerPanel = (LayerPanel) layerPanelAsJPanel;

        algorithmPanel = new JPanel(new GridLayout(0, 3, 5, 5));
    }

    private void createAlgorithmPanel() {
        // TODO add buttons for invoking algorithms
    }

    @Override
    public void setProgress(double progress) {

    }

    @Override
    public void setCalcTime(double calcTime) {
        calcTimeTextField.setText(Double.toString(calcTime));
    }

    @Override
    public void setCost(double cost) {
        costTextField.setText(Double.toString(cost));
    }

    @Override
    public void setRoute(List<Point> route) {
        layerPanel.setRoute(route);
    }
}