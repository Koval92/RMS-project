package pathfinder;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import pathfinder.algorithms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    private JTextField sizeTextField;
    protected JTextField calcTimeTextField;

    protected JTextField timeTextField;
    protected JTextField distanceTextField;
    protected JTextField energyTextField;

    private JRadioButton timeRadioButton;
    private JRadioButton distanceRadioButton;
    private JRadioButton energyRadioButton;

    private JTextField fileNameField;
    private JButton loadButton;
    private JPanel layerPanel;
    private JPanel rootPanel;
    private JPanel algorithmPanel;

    protected CostFunctionType costFunctionType;
    protected Layer layer = LayerFactory.createEmptyLayer(10);
    protected List<Point> route;
    private List<PathPlanner> algorithms;
    private Connection connection = new Connection(this);
    private Logger logger = Logger.getInstance();

    MainWindow() {
        $$$setupUI$$$();
        this.setTitle("Pathfinder");
        //setLocationRelativeTo(null);
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        configureLoadButton();

        updateLayerPanel();

        pack();

        distanceRadioButton.addActionListener(e -> {
            costFunctionType = CostFunctionType.DISTANCE;
            costTypeChanged();
        });
        timeRadioButton.addActionListener(e -> {
            costFunctionType = CostFunctionType.TIME;
            costTypeChanged();
        });
        energyRadioButton.addActionListener(e -> {
            costFunctionType = CostFunctionType.ENERGY;
            costTypeChanged();
        });

        distanceRadioButton.doClick();
    }

    private void costTypeChanged() {
        logger.log("Cost function type set to " + this.costFunctionType);
        this.loadButton.doClick();
    }

    void updateLayerPanel() {
        layerPanel.setPreferredSize(new Dimension(Utils.getPixelSize() * layer.getWidth(), Utils.getPixelSize() * layer.getHeight()));
        layerPanel.repaint();
        layerPanel.revalidate();
    }

    void resetStats() {
        timeTextField.setText("-");
        distanceTextField.setText("-");
        energyTextField.setText("-");
        calcTimeTextField.setText("-");
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

    private void addAlgorithms() {
        // remember to add new algorithms here (and new instances of them)
        algorithms.add(new LeftToRight());
        algorithms.add(new Snake());
        algorithms.add(new EdgeFollowing());
        // add other algorithms below
    }

    private void configureLoadButton() {
        loadButton.addActionListener(e -> {
            String fileName = fileNameField.getText();
            File file = new File(fileName);
            if (file.exists()) {
                layer = LayerFactory.createFromFile(fileName);
                route = null;
                updateLayerPanel();
                resetStats();
                resetAlgorithms();
                sizeTextField.setText(String.valueOf(layer.toListOfPoints().size()));
                pack();
            } else {
                logger.log("Incorrect file name/path to file!");
            }
        });
        fileNameField.addActionListener(loadButton.getActionListeners()[0]);
    }

    private void resetAlgorithms() {
        algorithmPanel.removeAll();
        algorithms = new ArrayList<>();
        addAlgorithms();
        for (PathPlanner algorithm : algorithms)
            add(algorithm);
    }

    private void createUIComponents() {
        layerPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                BufferedImage image = Utils.draw(layer, route);
                g.drawImage(image, 0, 0, null);
            }
        };

        algorithmPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    }

    private void add(PathPlanner algorithm) {
        algorithm.setConnection(this.connection);
        JButton algorithmButton = new JButton(algorithm.getName());
        algorithmButton.addActionListener(e ->
                algorithm.invoke());
        algorithmPanel.add(algorithmButton);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(4, 5, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(layerPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        layerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final Spacer spacer1 = new Spacer();
        rootPanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        rootPanel.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        rootPanel.add(spacer3, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1), null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        rootPanel.add(spacer4, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 2, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel1, new GridConstraints(1, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fileNameField = new JTextField();
        fileNameField.setText("img/sp-1.png");
        panel1.add(fileNameField, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel1.add(algorithmPanel, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Time");
        panel1.add(label1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Calculation time:");
        panel1.add(label2, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeTextField = new JTextField();
        timeTextField.setEditable(false);
        timeTextField.setHorizontalAlignment(4);
        timeTextField.setText("-");
        panel1.add(timeTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), null, 0, false));
        calcTimeTextField = new JTextField();
        calcTimeTextField.setEditable(false);
        calcTimeTextField.setHorizontalAlignment(4);
        calcTimeTextField.setText("-");
        panel1.add(calcTimeTextField, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), null, 0, false));
        loadButton = new JButton();
        loadButton.setText("Load/Reset");
        loadButton.setMnemonic('L');
        loadButton.setDisplayedMnemonicIndex(0);
        panel1.add(loadButton, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(9, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Point on layer:");
        panel1.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sizeTextField = new JTextField();
        sizeTextField.setEditable(false);
        sizeTextField.setHorizontalAlignment(4);
        sizeTextField.setText("-");
        panel1.add(sizeTextField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        distanceRadioButton = new JRadioButton();
        distanceRadioButton.setText("Distance");
        distanceRadioButton.setMnemonic('D');
        distanceRadioButton.setDisplayedMnemonicIndex(0);
        panel2.add(distanceRadioButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeRadioButton = new JRadioButton();
        timeRadioButton.setText("Time");
        timeRadioButton.setMnemonic('T');
        timeRadioButton.setDisplayedMnemonicIndex(0);
        panel2.add(timeRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        energyRadioButton = new JRadioButton();
        energyRadioButton.setText("Energy");
        energyRadioButton.setMnemonic('E');
        energyRadioButton.setDisplayedMnemonicIndex(0);
        panel2.add(energyRadioButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Distance");
        panel1.add(label4, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Energy");
        panel1.add(label5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        distanceTextField = new JTextField();
        distanceTextField.setEditable(false);
        distanceTextField.setHorizontalAlignment(4);
        distanceTextField.setText("-");
        panel1.add(distanceTextField, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), null, 0, false));
        energyTextField = new JTextField();
        energyTextField.setEditable(false);
        energyTextField.setHorizontalAlignment(4);
        energyTextField.setText("-");
        panel1.add(energyTextField, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), null, 0, false));
        final Spacer spacer6 = new Spacer();
        rootPanel.add(spacer6, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10), null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        rootPanel.add(spacer7, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(distanceRadioButton);
        buttonGroup.add(timeRadioButton);
        buttonGroup.add(energyRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}

class Connection implements PathPlanningConnection {
    MainWindow mainWindow;
    Logger logger = Logger.getInstance();

    Connection(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void setProgress(double progress) {
        logger.log("Current progress: " + progress);
    }

    @Override
    public void setCalcTime(double calcTimeInNano) {
        mainWindow.calcTimeTextField.setText(String.format("%.2f", calcTimeInNano / 1_000_000) + " ms");
    }

    @Override
    public void setRoute(List<Point> route) {
        mainWindow.route = route;

        double timeCost = MoveCostCalculator.calculate(route, CostFunctionType.TIME);
        double distanceCost = MoveCostCalculator.calculate(route, CostFunctionType.DISTANCE);
        double energyCost = MoveCostCalculator.calculate(route, CostFunctionType.ENERGY);

        mainWindow.timeTextField.setText(String.format("%.2f", timeCost));
        mainWindow.distanceTextField.setText(String.format("%.2f", distanceCost));
        mainWindow.energyTextField.setText(String.format("%.2f", energyCost));

        mainWindow.updateLayerPanel();
        Utils.saveToFile(Utils.draw(mainWindow.layer, mainWindow.route));
    }

    @Override
    public CostFunctionType getCostFunctionType() {
        return mainWindow.costFunctionType;
    }

    @Override
    public List<Point> getCopyOfLayerAsListOfPoints() {
        return mainWindow.layer.toListOfPoints();
    }

    @Override
    public List<List<Boolean>> getCopyOfLayerAsTable() {
        return mainWindow.layer.toTable();
    }

    @Override
    public boolean[][] getCopyOfLayerAsSimpleTable() {
        return mainWindow.layer.toSimpleTable();
    }

    @Override
    public Layer getCopyOfLayer() {
        return new Layer(mainWindow.layer);
    }

    @Override
    public Point getInitialPrinterPosition() {
        return null;
    }
}

