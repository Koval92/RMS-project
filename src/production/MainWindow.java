package production;

import production.algorithms.LeftToRight;
import test.testAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainWindow extends JFrame implements PathPlanningListener {
    private JTextField fileNameField;
    private JButton loadButton;
    private JPanel rootPanel;
    private JPanel algorithmPanel;
    private JTextField costTextField;
    private JTextField calcTimeTextField;
    private JPanel layerPanelAsJPanel;
    private JTextField sizeTextField;

    private LayerPanel layerPanel;
    private CostFunctionType costFunctionType = CostFunctionType.DISTANCE;

    MainWindow() {
        this.setTitle("Pathfinder");
        //setLocationRelativeTo(null);
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        configureLoadButton();

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
        loadButton.addActionListener(e -> {
            String fileName = fileNameField.getText();
            File file = new File(fileName);
            if (file.exists()) {
                Layer layer = LayerFactory.createFromFile(fileName);
                layerPanel.setLayer(layer);
                algorithmPanel.removeAll();
                sizeTextField.setText(String.valueOf(layer.toListOfPoints().size()));
                addAlgorithms();
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

    private void addAlgorithms() {
        add(new testAlgorithm());
        add(new LeftToRight());
        // add other algorithms
    }

    private void add(PathPlanner algorithm) {
        algorithm.setListener(this);
        JButton algorithmButton = new JButton(algorithm.getName());
        algorithmButton.addActionListener(e ->
                algorithm.invoke());
        algorithmPanel.add(algorithmButton);
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

    @Override
    public CostFunctionType getCostFunctionType() {
        return costFunctionType;
    }

    @Override
    public List<Point> getCopyOfLayerAsList() {
        return layerPanel.getLayer().toListOfPoints();
    }

    @Override
    public List<List<Boolean>> getCopyOfLayerAsTable() {
        return layerPanel.getLayer().toTable();
    }

    @Override
    public boolean[][] getCopyOfLayerAsSimpleTable() {
        return layerPanel.getLayer().toSimpleTable();
    }

    @Override
    public Layer getCopyOfLayer() {
        return new Layer(layerPanel.getLayer());
    }
}