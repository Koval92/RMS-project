package production;

import test.testAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

// test commit
public class MainWindow extends JFrame implements PathPlanningListener {
    private JTextField fileNameField;
    private JButton loadButton;
    private JPanel rootPanel;
    private JPanel algorithmPanel;
    private JTextField costTextField;
    private JTextField calcTimeTextField;
    private JPanel layerPanelAsJPanel;
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

        algorithmPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    }

    private void createAlgorithmPanel() {
        add(new testAlgorithm(layerPanel.getLayer(), CostFunctionType.ENERGY, this));
        // add other algorithms
    }

    private void add(PathPlanner algorithm) {
        JButton testAlgorithmButton = new JButton(algorithm.getName());
        testAlgorithmButton.addActionListener(e ->
                algorithm.invoke());
        algorithmPanel.add(testAlgorithmButton);
    }

    @Override
    public void setProgress(double progress) {
        System.out.println("Current progress: " + progress);
    }

    @Override
    public void setCalcTime(double calcTimeInNano) {
        calcTimeTextField.setText(Double.toString(calcTimeInNano / 1000) + " ms");
    }

    @Override
    public void setCost(double cost) {
        costTextField.setText(String.format("%.2f", cost));
    }

    @Override
    public void setRoute(List<Point> route) {
        layerPanel.setRoute(route);
    }
}