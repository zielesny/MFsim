/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2020  Achim Zielesny (achim.zielesny@googlemail.com)
 * 
 * Source code is available at <https://github.com/zielesny/MFsim>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.gnwi.mfsim.gui.control;

import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.gui.chart.BarChart;
import de.gnwi.mfsim.gui.chart.ChartInterface;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.gui.util.VirtualSlider;
import de.gnwi.mfsim.gui.chart.XyChart;
import de.gnwi.mfsim.model.util.ChartConfiguration;
import de.gnwi.mfsim.model.util.ImageSelection;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.graphics.IImageProvider;

/**
 * Controller class for CustomPanelValueItemMatrixDiagram
 * 
 * @author Jan-Mathis Hein, Achim Zielesny
 */
public class CustomPanelValueItemMatrixDiagramController implements IImageProvider{
    
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Show chartSettingsPanel, false: Show movieSettingsPanel
     */
    private boolean hasChartSettings;
    
    /**
     * True: The ChangeEvent in the domainSlider is caused by a value change,
     * false: The ChangeEvent is caused by a parameter change
     */
    private boolean isSliderValueChange;
    
    /**
     * Configuration of the chart
     */
    private ChartConfiguration chartConfiguration;
    
    /**
     * A chart that will be displayed
     */
    private ChartInterface chart;
    
    /**
     * Panel for which this controlleer is used
     */
    private CustomPanelValueItemMatrixDiagram customPanelValueItemMatrixDiagram;
    
    /**
     * File type for movie images
     */
    private ImageFileType imageFileType;
    
    /**
     * Current value of the virtual slider
     */
    private int virtualSliderIndex;

    /**
     * Timer for the animation
     */
    private Timer animationTimer;
    
    /**
     * Utility string methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();
    
    /**
     * Value item that is the input for the chart
     */
    private ValueItem matrixDiagramValueItem;
    
    /**
     * Virtual slider
     */
    private VirtualSlider virtualSlider;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     * 
     * @param aCustomPanelValueItemMatrixDiagram Panel this controller is made 
     * for
     * @param aValueItem Value item to be displayed in a chart
     * @param anImageFileType File type of the movie images
     */
    public CustomPanelValueItemMatrixDiagramController(
        CustomPanelValueItemMatrixDiagram aCustomPanelValueItemMatrixDiagram, 
        ValueItem aValueItem, 
        ImageFileType anImageFileType
    ) throws IllegalArgumentException{
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelValueItemMatrixDiagram == null) {
            throw new IllegalArgumentException("No CustomPanelValueItemMatrixDiagram was found"); 
        }
        if (anImageFileType == null) {
            throw new IllegalArgumentException("No image file type");
        }
        if (aValueItem == null || !aValueItem.hasMatrixDiagram()) {
            throw new IllegalArgumentException("Illegal value item");
        }
        // </editor-fold>
        this.customPanelValueItemMatrixDiagram = aCustomPanelValueItemMatrixDiagram;
        this.imageFileType = anImageFileType;
        this.matrixDiagramValueItem = aValueItem;
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup">
            // <editor-fold defaultstate="collapsed" desc="- Set up chart and dialog">
            switch (this.matrixDiagramValueItem.getTypeFormat(this.matrixDiagramValueItem.getMatrixDiagramXValueColumn()).getDataType()) {
                case NUMERIC:
                    // <editor-fold defaultstate="collapsed" desc="Xy chart">
                    // <editor-fold defaultstate="collapsed" desc="- Initialize class and local variables">
                    this.virtualSlider = new VirtualSlider(this.customPanelValueItemMatrixDiagram.getDomainSlider());
                    this.isSliderValueChange = true;
                    this.hasChartSettings = true;
                    if (!Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasChartConfiguration()) {
                        Preferences.getInstance().getSimulationMovieSlicerConfiguration().setChartConfiguration(new ChartConfiguration());
                        this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(false);
                    }
                    this.chartConfiguration = Preferences.getInstance().getSimulationMovieSlicerConfiguration().getChartConfiguration();
                    int tmpNumberToAverage = this.chartConfiguration.getNumberToAverage();
                    int tmpNumberOfDiscardedInitialPoints = this.chartConfiguration.getNumberOfDiscardedInitialPoints();
                    double[] tmpZoomValues = this.chartConfiguration.getZoomValues();
                    boolean tmpIsThickLines = this.chartConfiguration.getIsThickLines();
                    boolean tmpIsShapePaint = this.chartConfiguration.getIsShapePaint();
                    boolean tmpIsFillColorWhite = this.chartConfiguration.getIsFillColorWhite();
                    boolean tmpIsOutlinePaintWhite = this.chartConfiguration.getIsOutlinePaintWhite();
                    boolean tmpIsInverted = this.chartConfiguration.getIsInverted();
                    boolean tmpHasTrendLine = this.chartConfiguration.getHasTrendLine();
                    boolean tmpHasReducedTrendLine = false;
                    boolean tmpHasReducedStatistics = false;
                    boolean tmpHasLastPointMarked = this.chartConfiguration.getHasLastPointMarked();
                    boolean tmpIsZoom = this.chartConfiguration.getIsZoom();
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Set up dialog">
                    this.customPanelValueItemMatrixDiagram.getThickCheckBox().setSelected(tmpIsThickLines);
                    this.customPanelValueItemMatrixDiagram.getShapesCheckBox().setSelected(tmpIsShapePaint);
                    this.customPanelValueItemMatrixDiagram.getWhiteCheckBox().setSelected(tmpIsFillColorWhite);
                    this.customPanelValueItemMatrixDiagram.getOutlineCheckBox().setSelected(tmpIsOutlinePaintWhite);
                    this.customPanelValueItemMatrixDiagram.getInvertedCheckBox().setSelected(tmpIsInverted);
                    this.customPanelValueItemMatrixDiagram.getTrendLineCheckBox().setSelected(tmpHasTrendLine);
                    this.customPanelValueItemMatrixDiagram.getReducedTrendLineCheckBox().setSelected(tmpHasReducedTrendLine);
                    this.customPanelValueItemMatrixDiagram.getReducedStatisticsCheckBox().setSelected(tmpHasReducedStatistics);
                    this.customPanelValueItemMatrixDiagram.getMarkLastPointCheckBox().setSelected(tmpHasLastPointMarked);
                    this.updateCheckBoxes();
                    String[] tmpNumberToAverageArray = ModelUtils.getNumberStringsForInterval(1, 100);
                    this.customPanelValueItemMatrixDiagram.getAverageComboBox().setModel(new DefaultComboBoxModel(tmpNumberToAverageArray));
                    this.customPanelValueItemMatrixDiagram.getAverageComboBox().setSelectedIndex(tmpNumberToAverage - 1);
                    this.customPanelValueItemMatrixDiagram.getResetAverageButton().setVisible(tmpNumberToAverage != 1);
                    this.customPanelValueItemMatrixDiagram.getRestoreButton().setVisible(tmpIsZoom || (tmpNumberOfDiscardedInitialPoints != 0));
                    this.customPanelValueItemMatrixDiagram.getChartSettingsRadioButton().setSelected(this.hasChartSettings);
                    this.customPanelValueItemMatrixDiagram.getMovieSettingsRadioButton().setSelected(!this.hasChartSettings);
                    this.customPanelValueItemMatrixDiagram.showChartSettingsPanel(this.hasChartSettings);
                    this.customPanelValueItemMatrixDiagram.getMovieRadioButton().setSelected(true);
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Set up chart">
                    XyChart tmpXyChart = new XyChart(this.matrixDiagramValueItem);
                    tmpXyChart.setHasReducedTrendLine(tmpHasReducedTrendLine);
                    tmpXyChart.setHasTrendLine(tmpHasTrendLine);
                    tmpXyChart.setIsFillColorWhite(tmpIsFillColorWhite);
                    tmpXyChart.setIsInverted(tmpIsInverted);
                    tmpXyChart.setIsOutlinePaintWhite(tmpIsOutlinePaintWhite);
                    tmpXyChart.setIsShapePaint(tmpIsShapePaint);
                    tmpXyChart.setIsThickLines(tmpIsThickLines);
                    tmpXyChart.setHasReducedStatistics(tmpHasReducedStatistics);
                    tmpXyChart.setNumberOfDiscardedInitialPoints(tmpNumberOfDiscardedInitialPoints);
                    tmpXyChart.setHasLastPointMarked(tmpHasLastPointMarked);
                    tmpXyChart.setNumberToAverage(tmpNumberToAverage);
                    if (tmpIsZoom) {
                        tmpXyChart.setZoomValues(tmpZoomValues);
                    }
                    tmpXyChart.setHasAccumulativeChartDataArray(!this.hasChartSettings);
                    this.chart = tmpXyChart;
                    // </editor-fold>
                    break;
                    // </editor-fold>
                case TEXT:
                    // <editor-fold defaultstate="collapsed" desc="Bar chart">
                    // <editor-fold defaultstate="collapsed" desc="- Set up dialog">
                    // Disable possible xy-diagram settings if histogram is displayed
                    this.hasChartSettings = true;
                    this.customPanelValueItemMatrixDiagram.getDiscardButton().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getZoomButton().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getRestoreButton().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getThickCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getShapesCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getWhiteCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getOutlineCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getInvertedCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getTrendLineCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getReducedTrendLineCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getReducedStatisticsCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getMarkLastPointCheckBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getAverageComboBox().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getResetAverageButton().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getChartSettingsRadioButton().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.getMovieSettingsRadioButton().setVisible(false);
                    this.customPanelValueItemMatrixDiagram.showChartSettingsPanel(true);
                    // In bar charts the chart settings panel fills the whole bottom area of the panel
                    this.customPanelValueItemMatrixDiagram.getSpringLayout().putConstraint(
                            SpringLayout.SOUTH, this.customPanelValueItemMatrixDiagram.getChartSettingsPanel(), -12, 
                            SpringLayout.SOUTH, this.customPanelValueItemMatrixDiagram
                    );
                    this.customPanelValueItemMatrixDiagram.getSpringLayout().putConstraint(
                            SpringLayout.NORTH, this.customPanelValueItemMatrixDiagram.getChartSettingsPanel(), -96, 
                            SpringLayout.SOUTH, this.customPanelValueItemMatrixDiagram
                    );
                    this.customPanelValueItemMatrixDiagram.getSpringLayout().putConstraint(
                            SpringLayout.EAST, this.customPanelValueItemMatrixDiagram.getChartSettingsPanel(), -12, 
                            SpringLayout.EAST, this.customPanelValueItemMatrixDiagram
                    );
                    this.customPanelValueItemMatrixDiagram.getSpringLayout().putConstraint(
                            SpringLayout.WEST, this.customPanelValueItemMatrixDiagram.getChartSettingsPanel(), 12, 
                            SpringLayout.WEST, this.customPanelValueItemMatrixDiagram
                    );
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="- Set up chart">
                    BarChart tmpBarChart = new BarChart(this.matrixDiagramValueItem);
                    this.chart = tmpBarChart;
                    // </editor-fold>
                    break;
                    // </editor-fold>
                default:
                    // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                    JOptionPane.showMessageDialog(null, 
                        String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "CustomPanelValueItemMatrixDiagramController"), 
                        GuiMessage.get("Error.ErrorNotificationTitle"), 
                        JOptionPane.ERROR_MESSAGE
                    );
                    // </editor-fold>
                    return;
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set statistics string">
            this.setStatistics(this.chart.getStatisticsString());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set scale mode">
            // Set customImagePanel properties: Since image is calculated in 
            // componentResized method of customImagePanel all scaling operations 
            // can be omitted (for increased speed)
            this.customPanelValueItemMatrixDiagram.getImagePanel().setDrawUnscaled(true);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            this.customPanelValueItemMatrixDiagram.getImagePanel().addComponentListener(new ComponentAdapter() {

                public void componentResized(final ComponentEvent e) {
                    try {
                        if (CustomPanelValueItemMatrixDiagramController.this.chart != null) {
                            if (!CustomPanelValueItemMatrixDiagramController.this.hasChartSettings) {
                                CustomPanelValueItemMatrixDiagramController.this.setImage(CustomPanelValueItemMatrixDiagramController.this.chart.getImage(
                                    CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                                    CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight(),
                                    CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex
                                ));
                            }
                            else {
                                CustomPanelValueItemMatrixDiagramController.this.setImage(CustomPanelValueItemMatrixDiagramController.this.chart.getImage(
                                    CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                                    CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                                ));
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "componentResized()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getSaveGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemMatrixDiagramController.this.stopAnimation();
                        String tmpFilePathname = GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.selectGraphicsFileSaveChooser"), 
                            GuiDefinitions.IMAGE_BMP_FILE_EXTENSION
                        );
                        if (tmpFilePathname != null) {
                            try {
                                GraphicsUtils.writeImageToFile(CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getImage(), ImageFileType.BMP, new File(tmpFilePathname));
                            } catch (Exception anExeption) {
                                // <editor-fold defaultstate="collapsed" desc="Show error message">
                                JOptionPane.showMessageDialog(null, 
                                    GuiMessage.get("Error.SaveOperationFailed"), 
                                    GuiMessage.get("Error.ErrorNotificationTitle"),
                                    JOptionPane.ERROR_MESSAGE
                                );
                                // </editor-fold>
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getCopyGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemMatrixDiagramController.this.stopAnimation();
                        if (!ImageSelection.copyImageToClipboard(CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getImage())) {
                            // <editor-fold defaultstate="collapsed" desc="Show error message">
                            JOptionPane.showMessageDialog(null, 
                                GuiMessage.get("Error.CopyOperationFailed"), 
                                GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE
                            );
                            // </editor-fold>
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getDiscardButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        tmpXyChart.incrementNumberOfDiscardedInitialPoints();
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setNumberOfDiscardedInitialPoints(
                            tmpXyChart.getXyChartDataManipulator().getNumberOfDiscardedInitialPoints()
                        );
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString());
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getRestoreButton().setVisible(true);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getZoomButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        double[] tmpNewDataBoundaryValues = 
                            GuiUtils.getZoomBoundaries(
                                tmpXyChart.getXyChartDataManipulator().getUpdatedDataBoundaires(), 
                                tmpXyChart.getXyChartDataManipulator().getZoomValues()
                            );
                        if (tmpNewDataBoundaryValues != null) {
                            tmpXyChart.setZoomValues(tmpNewDataBoundaryValues);
                            CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setZoomValues(tmpNewDataBoundaryValues);
                            CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setIsZoom(true);
                            CustomPanelValueItemMatrixDiagramController.this.setImage(
                                tmpXyChart.getImage(
                                    CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                                    CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                                )
                            );
                            CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString());
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getRestoreButton().setVisible(true);
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDiscardButton().setVisible(false);
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(
                            null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getRestoreButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        tmpXyChart.reset();
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setZoomValues(
                            new double[] {-Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE}
                        );
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setIsZoom(false);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setNumberOfDiscardedInitialPoints(0);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString());
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDiscardButton().setVisible(true);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getZoomButton().setVisible(true);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getRestoreButton().setVisible(false);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getDefaultButton().addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setToDefault();
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        int tmpNumberToAverage = CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.getNumberToAverage();
                        boolean tmpIsThickLines = CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.getIsThickLines();
                        boolean tmpIsShapePaint = CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.getIsShapePaint();
                        boolean tmpIsFillColorWhite = CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.getIsFillColorWhite();
                        boolean tmpIsOutlinePaintWhite = CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.getIsOutlinePaintWhite();
                        boolean tmpIsInverted = CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.getIsInverted();
                        boolean tmpHasTrendLine = CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.getHasTrendLine();
                        boolean tmpHasLastPointMarked = CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.getHasLastPointMarked();
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getThickCheckBox().setSelected(tmpIsThickLines);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getShapesCheckBox().setSelected(tmpIsShapePaint);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getWhiteCheckBox().setSelected(tmpIsFillColorWhite);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getOutlineCheckBox().setSelected(tmpIsOutlinePaintWhite);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getInvertedCheckBox().setSelected(tmpIsInverted);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getTrendLineCheckBox().setSelected(tmpHasTrendLine);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getMarkLastPointCheckBox().setSelected(tmpHasLastPointMarked);
                        CustomPanelValueItemMatrixDiagramController.this.updateCheckBoxes();
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getAverageComboBox().setSelectedIndex(tmpNumberToAverage - 1);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getResetAverageButton().setVisible(tmpNumberToAverage != 1);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDiscardButton().setVisible(true);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getZoomButton().setVisible(true);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getRestoreButton().setVisible(false);
                        tmpXyChart.reset();
                        tmpXyChart.setHasTrendLine(tmpHasTrendLine);
                        tmpXyChart.setIsFillColorWhite(tmpIsFillColorWhite);
                        tmpXyChart.setIsInverted(tmpIsInverted);
                        tmpXyChart.setIsOutlinePaintWhite(tmpIsOutlinePaintWhite);
                        tmpXyChart.setIsShapePaint(tmpIsShapePaint);
                        tmpXyChart.setIsThickLines(tmpIsThickLines);
                        tmpXyChart.setHasLastPointMarked(tmpHasLastPointMarked);
                        tmpXyChart.setNumberToAverage(tmpNumberToAverage);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(false);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString());
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelValueItemMatrixDiagram.getThickCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpIsThickLines = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getThickCheckBox().isSelected();
                        tmpXyChart.setIsThickLines(tmpIsThickLines);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setIsThickLines(tmpIsThickLines);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getShapesCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpIsShapePaint = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getShapesCheckBox().isSelected();
                        tmpXyChart.setIsShapePaint(tmpIsShapePaint);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setIsShapePaint(tmpIsShapePaint);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                        CustomPanelValueItemMatrixDiagramController.this.updateCheckBoxes();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getWhiteCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpIsFillColorWhite = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getWhiteCheckBox().isSelected();
                        tmpXyChart.setIsFillColorWhite(tmpIsFillColorWhite);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setIsFillColorWhite(tmpIsFillColorWhite);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.updateCheckBoxes();
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getOutlineCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpIsOutlinePaintWhite = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getOutlineCheckBox().isSelected();
                        tmpXyChart.setIsOutlinePaintWhite(tmpIsOutlinePaintWhite);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setIsOutlinePaintWhite(tmpIsOutlinePaintWhite);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getInvertedCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpIsInverted = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getInvertedCheckBox().isSelected();
                        tmpXyChart.setIsInverted(tmpIsInverted);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setIsInverted(tmpIsInverted);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getTrendLineCheckBox().addItemListener(new ItemListener() {
                
                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpHasTrendLine = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getTrendLineCheckBox().isSelected();
                        tmpXyChart.setHasTrendLine(tmpHasTrendLine);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setHasTrendLine(tmpHasTrendLine);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.updateCheckBoxes();
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelValueItemMatrixDiagram.getMarkLastPointCheckBox().addItemListener(new ItemListener() {
                
                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpHasLastPointMarked = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getMarkLastPointCheckBox().isSelected();
                        tmpXyChart.setHasLastPointMarked(tmpHasLastPointMarked);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setHasLastPointMarked(tmpHasLastPointMarked);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.updateCheckBoxes();
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelValueItemMatrixDiagram.getAverageComboBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        int tmpNumberToAverage = Integer.valueOf(CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getAverageComboBox().getSelectedItem().toString());
                        tmpXyChart.setNumberToAverage(tmpNumberToAverage);
                        CustomPanelValueItemMatrixDiagramController.this.chartConfiguration.setNumberToAverage(tmpNumberToAverage);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        if (tmpNumberToAverage > 1) {
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getResetAverageButton().setVisible(true);
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getDefaultButton().setVisible(true);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getResetAverageButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set to first value (= 1)
                        // Triggers an itemEvent in the resetAverageButton
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getAverageComboBox().setSelectedIndex(0);
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getResetAverageButton().setVisible(false);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getChartSettingsRadioButton().addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemMatrixDiagramController.this.stopAnimation();
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        CustomPanelValueItemMatrixDiagramController.this.hasChartSettings = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getChartSettingsRadioButton().isSelected();
                        tmpXyChart.setHasAccumulativeChartDataArray(!CustomPanelValueItemMatrixDiagramController.this.hasChartSettings);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight()
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString());
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.showChartSettingsPanel(CustomPanelValueItemMatrixDiagramController.this.hasChartSettings);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelValueItemMatrixDiagram.getMovieSettingsRadioButton().addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    try {
                        MouseCursorManagement.getInstance().setWaitCursor();
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        CustomPanelValueItemMatrixDiagramController.this.stopAnimation();
                        CustomPanelValueItemMatrixDiagramController.this.hasChartSettings = !CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getMovieSettingsRadioButton().isSelected();
                        tmpXyChart.setHasAccumulativeChartDataArray(!CustomPanelValueItemMatrixDiagramController.this.hasChartSettings);
                        CustomPanelValueItemMatrixDiagramController.this.updateSlider(tmpXyChart);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight(),
                            CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex
                        ));
                        CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString(CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex));
                        CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.showChartSettingsPanel(CustomPanelValueItemMatrixDiagramController.this.hasChartSettings);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    } finally {
                        MouseCursorManagement.getInstance().setDefaultCursor();
                    }
                }
                
            });
            this.customPanelValueItemMatrixDiagram.getImagePanel().addMouseWheelListener(new MouseWheelListener() {
                
                public void mouseWheelMoved(MouseWheelEvent e) {
                    try {
                        if (!CustomPanelValueItemMatrixDiagramController.this.hasChartSettings && !CustomPanelValueItemMatrixDiagramController.this.isAnimationPlaying()) {
                            XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                            int tmpCurrentSliderValue = CustomPanelValueItemMatrixDiagramController.this.virtualSlider.getValue();
                            // NOTE: The "-" is IMPORTANT for "intuitive" scrolling with mouse wheel
                            tmpCurrentSliderValue -= e.getWheelRotation() * CustomPanelValueItemMatrixDiagramController.this.virtualSlider.getMinorTickSpacing();
                            CustomPanelValueItemMatrixDiagramController.this.virtualSlider.setValueWithoutStateChangedEvent(tmpCurrentSliderValue);
                            CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex = CustomPanelValueItemMatrixDiagramController.this.virtualSlider.getValue();
                            CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                                CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                                CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight(),
                                CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex
                            ));
                            CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString(CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex));
                        } 
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseWheelMoved()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelValueItemMatrixDiagram.getDomainSlider().addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        if (CustomPanelValueItemMatrixDiagramController.this.isSliderValueChange && !CustomPanelValueItemMatrixDiagramController.this.hasChartSettings) {
                            CustomPanelValueItemMatrixDiagramController.this.virtualSlider.setValueFromSlider();
                            CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex = CustomPanelValueItemMatrixDiagramController.this.virtualSlider.getValue();
                            CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                                CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                                CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight(),
                                CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex
                            ));
                            CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString(CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>  
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getReducedTrendLineCheckBox().addItemListener(new ItemListener() {
                
                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpHasReducedTrendLine = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getReducedTrendLineCheckBox().isSelected();
                        tmpXyChart.setHasReducedTrendLine(tmpHasReducedTrendLine);
                        CustomPanelValueItemMatrixDiagramController.this.setImage(tmpXyChart.getImage(
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight(),
                            CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex
                        ));
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelValueItemMatrixDiagram.getReducedStatisticsCheckBox().addItemListener(new ItemListener() {
                
                public void itemStateChanged(ItemEvent e) {
                    try {
                        XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                        boolean tmpHasReducedStatistics = CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getReducedStatisticsCheckBox().isSelected();
                        tmpXyChart.setHasReducedStatistics(tmpHasReducedStatistics);
                        CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString(CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex));
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null,
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelValueItemMatrixDiagram.getMovieRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getMovieRadioButton().isSelected()) {
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getFirstMovieSettingsButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.toolTipText1"));
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getFirstMovieSettingsButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.text1"));
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getSecondMovieSettingsButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.toolTipText1"));
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getSecondMovieSettingsButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.text1"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getAnimationRadioButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getAnimationRadioButton().isSelected()) {
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getFirstMovieSettingsButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.toolTipText2"));
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getFirstMovieSettingsButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.text2"));
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getSecondMovieSettingsButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.toolTipText2"));
                            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getSecondMovieSettingsButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.text2"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getPlayAnimationButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemMatrixDiagramController.this.playAnimation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getCreatMovieButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemMatrixDiagramController.this.startMovieCreation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getFirstMovieSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getAnimationRadioButton().isSelected()) {
                            CustomPanelValueItemMatrixDiagramController.this.setDefaultAnimationSettings();
                        }
                        if (CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getMovieRadioButton().isSelected()) {
                            CustomPanelValueItemMatrixDiagramController.this.startMovieImagesCreation();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelValueItemMatrixDiagram.getSecondMovieSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getAnimationRadioButton().isSelected()) {
                            CustomPanelValueItemMatrixDiagramController.this.configureAnimationSettings();
                        }
                        if (CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getMovieRadioButton().isSelected()) {
                            CustomPanelValueItemMatrixDiagramController.this.configureMovieSettings();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelValueItemMatrixDiagramController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelValueItemMatrixDiagramController"), 
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns the image corresponding to an index
     * 
     * @param anIndex An index
     * @return The image corresponding to an index
     */
    @Override
    public BufferedImage getImage(int anIndex) {
        try {
            return this.chart.getImage(
                this.customPanelValueItemMatrixDiagram.getImagePanel().getWidth(), 
                this.customPanelValueItemMatrixDiagram.getImagePanel().getHeight(),
                anIndex
            );
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "getImage()", "CustomPanelValueItemMatrixDiagramController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
            return null;
        }
    }

    /**
     * Returns the image file type for the movie images
     * 
     * @return The image file type for the movie images
     */
    @Override
    public ImageFileType getImageFileType() {
        return this.imageFileType;
    }

    /**
     * Returns the number of images in the animation or zero if an exception is 
     * thrown
     * 
     * @return The number of images in the animation or zero if an exception is 
     * thrown
     */
    @Override
    public int getNumberOfImages(){
        try {
            XyChart tmpXyChart = (XyChart) this.chart;
            return tmpXyChart.getNumberOfPoints();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return 0;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Movie related methods">
    /**
     * Configures movie settings
     */
    private void configureMovieSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        ValueItemContainer tmpChartMovieSettingsValueItemContainer = Preferences.getInstance().getChartMovieEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesMovieSettingsDialog.title"), tmpChartMovieSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpChartMovieSettingsValueItemContainer);
        }
    }

    /**
     * Creates a movie for the images in the selected directory
     */
    private void startMovieCreation() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        if (!GuiUtils.canCreateMovie(Preferences.getInstance().getChartMovieImagePath())) {
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Movie.CreationNotPossible"), 
                GuiMessage.get("Movie.CreationNotPossible.Title"),
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            if (GuiUtils.getYesNoDecision(GuiMessage.get("Movie.CreationDesired.Title"), GuiMessage.get("Movie.CreationDesired"))) {
                GuiUtils.createMovie(Preferences.getInstance().getChartMovieImagePath(), 
                    Preferences.getInstance().getImageDirectoryPathForChartMovies(), 
                    Preferences.getInstance().getMovieDirectoryPathForChartMovies()
                );
            }
        }
    }

    /**
     * Starts creation of movie images
     */
    private void startMovieImagesCreation() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        // Parameter false: No backwards images
        GuiUtils.createMovieImages(this, false, Preferences.getInstance().getImageDirectoryPathForChartMovies());
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Animation related methods">
    /**
     * True: Animation is playing, false: Otherwise
     *
     * @return True: Animation is playing, false: Otherwise
     */
    private boolean isAnimationPlaying() {
        return this.animationTimer != null && this.animationTimer.isRunning();
    }

    /**
     * Configures animation settings
     */
    private void configureAnimationSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        ValueItemContainer tmpAnimationSettingsValueItemContainer = Preferences.getInstance().getAnimationEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesAnimationSettingsDialog.title"), tmpAnimationSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpAnimationSettingsValueItemContainer);
        }
    }

    /**
     * Starts playing the animation. If the animation is already playing the 
     * animation stops
     */
    private void playAnimation() {
        if (!this.isAnimationPlaying()) {
            // Time between two action events of the timer
            double tmpDelay = ((1000 / Preferences.getInstance().getAnimationSpeed()));
            this.animationTimer = new Timer((int)tmpDelay, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    XyChart tmpXyChart = (XyChart) CustomPanelValueItemMatrixDiagramController.this.chart;
                    int tmpCurrentValue = CustomPanelValueItemMatrixDiagramController.this.virtualSlider.getValue();
                    tmpCurrentValue += CustomPanelValueItemMatrixDiagramController.this.virtualSlider.getMinorTickSpacing();
                    if (tmpCurrentValue == CustomPanelValueItemMatrixDiagramController.this.virtualSlider.getMaximum()) {
                        CustomPanelValueItemMatrixDiagramController.this.virtualSlider.setValueWithoutStateChangedEvent(0);
                    }
                    else {
                        CustomPanelValueItemMatrixDiagramController.this.virtualSlider.setValueWithoutStateChangedEvent(tmpCurrentValue);
                    }
                    CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex = CustomPanelValueItemMatrixDiagramController.this.virtualSlider.getValue();
                    CustomPanelValueItemMatrixDiagramController.this.setImage(CustomPanelValueItemMatrixDiagramController.this.getImage(CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex)
                    );
                    CustomPanelValueItemMatrixDiagramController.this.setStatistics(tmpXyChart.getStatisticsString(CustomPanelValueItemMatrixDiagramController.this.virtualSliderIndex)
                    );
                }

            });
            this.animationTimer.start();
            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.toolTipText2"));
            CustomPanelValueItemMatrixDiagramController.this.customPanelValueItemMatrixDiagram.getPlayAnimationButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.text2"));
        } else {
            this.stopAnimation();
        }
    }

    /**
     * Stops the animation
     */
    private void stopAnimation() {
        try {
            if (this.isAnimationPlaying()) {
                this.animationTimer.stop();
                this.customPanelValueItemMatrixDiagram.getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.toolTipText1"));
                this.customPanelValueItemMatrixDiagram.getPlayAnimationButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.text1"));
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stopAnimation()", "CustomPanelValueItemMatrixDiagramController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }

    /**
     * Sets default animation settings
     */
    private void setDefaultAnimationSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setAnimationSpeed(Preferences.getInstance().getDefaultAnimationSpeed());
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- CustomPanelValueItemMatrixDiagram related methods">
    /**
     * Displays an image in the image panel
     * 
     * @param anImage An image
     */
    private void setImage(BufferedImage anImage) {
        if (anImage != null) {
            this.customPanelValueItemMatrixDiagram.getImagePanel().setBasicImage(anImage);
        }
    }
    
    /**
     * Displays the statistics string in the info label
     * 
     * @param aStatisticsString A statistics string that will be displayed
     */
    private void setStatistics(String aStatisticsString) {
        if (aStatisticsString != null && !aStatisticsString.isEmpty()) {
            this.customPanelValueItemMatrixDiagram.getInformationLabel().setText(aStatisticsString);
        }
    }
    
    /**
     * Changes the parameters of the slider according to the number of points in
     * the chart
     * 
     * @param aXyChart A chart
     */
    private void updateSlider(XyChart aXyChart) {
        try {
            // Stops any change events to be invoked on the domain slider that are caused by parameter changes
            this.isSliderValueChange = false;
            // Set new parameters
            this.virtualSlider.setSliderParameters(aXyChart.getNumberOfPoints());
            this.isSliderValueChange = true;
            this.virtualSliderIndex = 0;
            this.virtualSlider.setValueWithoutStateChangedEvent(this.virtualSliderIndex);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null,
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "updateSlider()", "CustomPanelValueItemMatrixDiagramController"),
                GuiMessage.get("Error.ErrorNotificationTitle"),
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    
    /**
     * Updates the visibilty of the check boxes in 
     * CustomPanelValueItemMatrixDiagram
     */
    private void updateCheckBoxes() {
        boolean tmpIsShapePaint = this.customPanelValueItemMatrixDiagram.getShapesCheckBox().isSelected();
        boolean tmpHasLastPointMarked = this.customPanelValueItemMatrixDiagram.getMarkLastPointCheckBox().isSelected();
        boolean tmpHasTrendLine = this.customPanelValueItemMatrixDiagram.getTrendLineCheckBox().isSelected();
        boolean tmpIsFillColorWhite = this.customPanelValueItemMatrixDiagram.getWhiteCheckBox().isSelected();
        this.customPanelValueItemMatrixDiagram.getWhiteCheckBox().setVisible(tmpHasLastPointMarked || tmpIsShapePaint);
        this.customPanelValueItemMatrixDiagram.getOutlineCheckBox().setVisible(
            (tmpIsShapePaint && !tmpIsFillColorWhite) || (tmpHasLastPointMarked && tmpIsFillColorWhite)
        );
        this.customPanelValueItemMatrixDiagram.getReducedTrendLineCheckBox().setVisible(tmpHasTrendLine);
    }
    // </editor-fold>
    // </editor-fold>
    
}
