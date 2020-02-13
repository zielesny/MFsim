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

import de.gnwi.mfsim.gui.chart.DistributionCalculationTask;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.job.TimeStepInfo;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.util.VolumeFrequency;
import de.gnwi.mfsim.gui.chart.DataArrayChart;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.gui.util.VirtualSlider;
import de.gnwi.mfsim.gui.chart.XyChartDataManipulator;
import de.gnwi.mfsim.model.job.JobResult;
import de.gnwi.mfsim.model.util.ChartConfiguration;
import de.gnwi.mfsim.model.util.ImageSelection;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.mfsim.model.graphics.IImageProvider;
import java.util.concurrent.ExecutionException;

/**
 * Controller class for CustomPanelDistributionMovie
 *
 * @author Jan-Mathis Hein, Achim Zielesny
 */
public class CustomPanelDistributionMovieController extends ChangeNotifier implements IImageProvider, PropertyChangeListener{

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Controller has been killed with the kill() method, false: Otherwise
     */
    private boolean isKilled;
    
    /**
     * True: The controller is starting the distribution calculation tasks, 
     * false: Otherwise
     */
    private boolean isStartingTasks;
    
    /**
     * Configuration of the chart
     */
    private ChartConfiguration chartConfiguration;
    
    /**
     * A concurrent queue with all distribution calculation tasks that still 
     * have to be executed
     */
    private ConcurrentLinkedQueue<DistributionCalculationTask> distributionCalculationTasks;
    
    /**
     * A concurrent queue with all started distribution calculation tasks
     */
    private ConcurrentLinkedQueue<DistributionCalculationTask> startedDistributionCalculationTasks;
    
    /**
     * A concurrent queue with all the finished distribution calculation tasks
     */
    private ConcurrentLinkedQueue<DistributionCalculationTask> finishedDistributionCalculationTasks;
    
    /**
     * A panel for which this controller is made, that displays the distribution 
     * movie
     */
    private CustomPanelDistributionMovie customPanelDistributionMovie;
    
    /**
     * Executor service that controlls the execution of the distribution 
     * calculation tasks
     */
    private ExecutorService executorService;
    
    /**
     * A chart that will be displayed
     */
    private DataArrayChart dataArrayChart;
    
    /**
     * File type for movie images
     */
    private ImageFileType imageFileType;
    
    /**
     * Highest index of a finished distribution calculation task
     */
    private int currentHighestIndex;
    
    /**
     * Number of steps for which the frequencies will be calculated
     */
    private int numberOfSteps;
    
    /**
     * Current value of the virtual slider
     */
    private int virtualSliderIndex;
    
    /**
     * Type of the particle that is being examined
     */
    private JobResult.ParticleType particleType;
    
    /**
     * Axis along which the distribution is calculated
     */
    private VolumeFrequency.VolumeAxis volumeAxis;
    
    /**
     * A string describing the particle that is being examined
     */
    private String particleTypeDescriptionString;

    /**
     * Timer for the animation
     */
    private Timer animationTimer;
    
    /**
     * Array with simulation step infos
     */
    private TimeStepInfo[] timeStepInfoArray;
    
    /**
     * Miscellaneous utility methods
     */
    private MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();
    
    /**
     * Value item container containing the job input
     */
    private ValueItemContainer jobInputValueItemContainer;
    
    /**
     * Virtual slider
     */
    private VirtualSlider virtualSlider;
    
    /**
     * Array for the distribution calculation task results
     */
    private XyChartDataManipulator[] xyChartDataArray;
    
    /**
     * Chart range
     */
    private double[] chartRange;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor. Sets up the slider, adds action listener and starts the 
     * distribution calculation tasks
     * 
     * @param aCustomPanelDistributionMovie Panel this controller is made for
     * @param anExecutorService Executor service for the distribution 
     * calculation tasks
     * @param anImageFileType File type for movie images
     * @param aTimeStepInfoArray Array with information for simulation steps
     * @param aJobInputValueItemContainer Value item container with the job 
     * input
     * @param anVolumeAxis Axis along which the distribution is calculated
     * @param aParticleType Type of particle that is examined
     * @param aParticleTypeDescriptionString String describing the particle that
     * is examined
     */
    public CustomPanelDistributionMovieController(
        CustomPanelDistributionMovie aCustomPanelDistributionMovie, 
        ExecutorService anExecutorService, 
        ImageFileType anImageFileType, 
        TimeStepInfo[] aTimeStepInfoArray, 
        ValueItemContainer aJobInputValueItemContainer, 
        VolumeFrequency.VolumeAxis anVolumeAxis, 
        JobResult.ParticleType aParticleType, 
        String aParticleTypeDescriptionString
    ) throws IllegalArgumentException{
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelDistributionMovie == null) {
            throw new IllegalArgumentException("aCustomPanelDistributionMovie is null");
        }
        if (anExecutorService == null) {
            throw new IllegalArgumentException("anExecutorService is null");
        }
        if (anImageFileType == null) {
            throw new IllegalArgumentException("anImageFileType is null");
        }
        if (aJobInputValueItemContainer == null) {
            throw new IllegalArgumentException("aJobInputValueItemContainer is null");
        }
        if (aTimeStepInfoArray == null || aTimeStepInfoArray.length <= 0) {
            throw new IllegalArgumentException("Illegal time step info array");
        }
        if (anVolumeAxis == null) {
            throw new IllegalArgumentException("anVolumeAxis is null");
        }
        if (aParticleType == null) {
            throw new IllegalArgumentException("aParticleType is null");
        }
        if (aParticleTypeDescriptionString == null || aParticleTypeDescriptionString.isEmpty()) {
            throw new IllegalArgumentException("aParticleTypeDescriptionString is null or empty");
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.customPanelDistributionMovie = aCustomPanelDistributionMovie;
            this.customPanelDistributionMovie.getRestoreButton().setVisible(false);
            this.customPanelDistributionMovie.showMainPanel(false);
            this.executorService = anExecutorService;
            this.imageFileType = anImageFileType;
            this.timeStepInfoArray = aTimeStepInfoArray;
            this.jobInputValueItemContainer = aJobInputValueItemContainer;
            this.volumeAxis = anVolumeAxis;
            this.particleType = aParticleType;
            this.particleTypeDescriptionString = aParticleTypeDescriptionString;
            this.numberOfSteps = this.timeStepInfoArray.length;
            this.xyChartDataArray = new XyChartDataManipulator[this.numberOfSteps];
            this.chartRange = null;
            this.isKilled = false;
            this.currentHighestIndex = -1;
            if (!Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasChartConfiguration()) {
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().setChartConfiguration(new ChartConfiguration());
            }
            this.chartConfiguration = Preferences.getInstance().getSimulationMovieSlicerConfiguration().getChartConfiguration();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Start tasks">
            this.startDistributionCalculationTasks();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set up slider">
            this.virtualSliderIndex = 0;
            this.virtualSlider = new VirtualSlider(this.customPanelDistributionMovie.getDomainSlider());
            this.virtualSlider.setSliderParameters(this.numberOfSteps);
            this.virtualSlider.setValueWithoutStateChangedEvent(this.virtualSliderIndex);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Select movie radio button">
            this.customPanelDistributionMovie.getMovieRadioButton().setSelected(true);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            this.customPanelDistributionMovie.getCopyGraphicsButton().addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelDistributionMovieController.this.stopAnimation();
                        if (!ImageSelection.copyImageToClipboard(CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getImagePanel().getImage())) {
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
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                    
                }
                
            });
            this.customPanelDistributionMovie.getSaveGraphicsButton().addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelDistributionMovieController.this.stopAnimation();
                        String tmpFilePathname = GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.selectGraphicsFileSaveChooser"), 
                            GuiDefinitions.IMAGE_BMP_FILE_EXTENSION
                        );
                        if (tmpFilePathname != null) {
                            try {
                                GraphicsUtils.writeImageToFile(CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getImagePanel().getImage(), ImageFileType.BMP, new File(tmpFilePathname));
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
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                    
                }
                
            });
            this.customPanelDistributionMovie.getZoomButton().addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelDistributionMovieController.this.setChartRange();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(
                            null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                    
                }
                
            });
            this.customPanelDistributionMovie.getRestoreButton().addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelDistributionMovieController.this.restoreOptimumChartRange();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(
                            null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                    
                }
                
            });
            this.customPanelDistributionMovie.getImagePanel().addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    try {
                        if (CustomPanelDistributionMovieController.this.dataArrayChart != null) {
                            CustomPanelDistributionMovieController.this.setImage(CustomPanelDistributionMovieController.this.dataArrayChart.getImage(CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getImagePanel().getWidth(), 
                                CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getImagePanel().getHeight(),
                                CustomPanelDistributionMovieController.this.virtualSliderIndex
                            ));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "componentResized()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            this.customPanelDistributionMovie.getImagePanel().addMouseWheelListener(new MouseWheelListener() {
                
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    try {
                        if (!CustomPanelDistributionMovieController.this.isAnimationPlaying()) {
                            int tmpCurrentSliderValue = CustomPanelDistributionMovieController.this.virtualSlider.getValue();
                            // NOTE: The "-" is IMPORTANT for "intuitive" scrolling with mouse wheel
                            tmpCurrentSliderValue -= e.getWheelRotation() * CustomPanelDistributionMovieController.this.virtualSlider.getMinorTickSpacing();
                            CustomPanelDistributionMovieController.this.virtualSlider.setValueWithoutStateChangedEvent(tmpCurrentSliderValue);
                            CustomPanelDistributionMovieController.this.virtualSliderIndex = CustomPanelDistributionMovieController.this.virtualSlider.getValue();
                            CustomPanelDistributionMovieController.this.setImage(CustomPanelDistributionMovieController.this.dataArrayChart.getImage(CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getImagePanel().getWidth(), 
                                CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getImagePanel().getHeight(),
                                CustomPanelDistributionMovieController.this.virtualSliderIndex
                            ));
                            CustomPanelDistributionMovieController.this.setStepInfo(CustomPanelDistributionMovieController.this.timeStepInfoArray[CustomPanelDistributionMovieController.this.virtualSliderIndex].getStepInfo());
                        } 
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseWheelMoved()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            
            });
            this.customPanelDistributionMovie.getDomainSlider().addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    try {
                        CustomPanelDistributionMovieController.this.virtualSlider.setValueFromSlider();
                        CustomPanelDistributionMovieController.this.virtualSliderIndex = CustomPanelDistributionMovieController.this.virtualSlider.getValue();
                        CustomPanelDistributionMovieController.this.setImage(CustomPanelDistributionMovieController.this.dataArrayChart.getImage(CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getImagePanel().getWidth(), 
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getImagePanel().getHeight(),
                            CustomPanelDistributionMovieController.this.virtualSliderIndex
                        ));
                        CustomPanelDistributionMovieController.this.setStepInfo(CustomPanelDistributionMovieController.this.timeStepInfoArray[CustomPanelDistributionMovieController.this.virtualSliderIndex].getStepInfo());
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>  
                    }
                }
                
            });
            this.customPanelDistributionMovie.getPlayAnimationButton().addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelDistributionMovieController.this.playAnimation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelDistributionMovie.getAnimationRadioButton().addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getAnimationRadioButton().isSelected()) {
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getFirstMovieSettingsButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.toolTipText2"));
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getFirstMovieSettingsButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.text2"));
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getSecondMovieSettingsButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.toolTipText2"));
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getSecondMovieSettingsButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.text2"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelDistributionMovie.getMovieRadioButton().addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getMovieRadioButton().isSelected()) {
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getFirstMovieSettingsButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.toolTipText1"));
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getFirstMovieSettingsButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.firstMovieSettingsButton.text1"));
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getSecondMovieSettingsButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.toolTipText1"));
                            CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getSecondMovieSettingsButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.secondMovieSettingsButton.text1"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelDistributionMovie.getFirstMovieSettingsButton().addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getAnimationRadioButton().isSelected()) {
                            CustomPanelDistributionMovieController.this.setDefaultAnimationSettings();
                        }
                        if (CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getMovieRadioButton().isSelected()) {
                            CustomPanelDistributionMovieController.this.startMovieImagesCreation();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelDistributionMovie.getSecondMovieSettingsButton().addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getAnimationRadioButton().isSelected()) {
                            CustomPanelDistributionMovieController.this.configureAnimationSettings();
                        }
                        if (CustomPanelDistributionMovieController.this.customPanelDistributionMovie.getMovieRadioButton().isSelected()) {
                            CustomPanelDistributionMovieController.this.configureMovieSettings();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            this.customPanelDistributionMovie.getCreatMovieButton().addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelDistributionMovieController.this.startMovieCreation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelDistributionMovieController"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelDistributionMovieController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public overriden methods">
    /**
     * Returns file type for movie images
     * 
     * @return File type for movie images
     */
    @Override
    public ImageFileType getImageFileType() {
        return this.imageFileType;
    }

    /**
     * Returns number of images for movie creation
     * 
     * @return Number of images for movie creation
     */
    @Override
    public int getNumberOfImages() {
        return this.dataArrayChart.getNumberOfSteps();
    }
    
    /**
     * Returns the corresponding image for an index
     * 
     * @param anIndex An index
     * @return The corresponding image for an index
     */
    @Override
    public BufferedImage getImage(int anIndex) {
        try {
            return this.dataArrayChart.getImage(
                this.customPanelDistributionMovie.getImagePanel().getWidth(), 
                this.customPanelDistributionMovie.getImagePanel().getHeight(),
                anIndex
            );
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "getImage()", "CustomPanelDistributionMovieController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
            return null;
        }
    }

    /**
     * Property change method from the PropertyChangeListener interface
     * 
     * @param evt Property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!this.isKilled) {
            try {
                // <editor-fold defaultstate="collapsed" desc="Property change due to progress of distribution calculation task">
                if (evt.getPropertyName().equals(ModelDefinitions.PROPERTY_CHANGE_PROGRESS)) {
                    int tmpProgressValue = (Integer) evt.getNewValue();
                    if (tmpProgressValue == 100) {
                        DistributionCalculationTask tmpDistributionCalculationTask = (DistributionCalculationTask) evt.getSource();
                        // Check if the result of this task was already obtained
                        if (this.xyChartDataArray[tmpDistributionCalculationTask.getIndex()] == null) {
                            this.xyChartDataArray[tmpDistributionCalculationTask.getIndex()] = tmpDistributionCalculationTask.getXyChartDataManipulator();
                            // IMPORTANT: Remove property change listener of tmpDistributionCalculationTask ...
                            tmpDistributionCalculationTask.removePropertyChangeListener(this);
                            // ... and remove from this.startedDistributionCalculationTasks
                            this.startedDistributionCalculationTasks.remove(tmpDistributionCalculationTask);
                            this.finishedDistributionCalculationTasks.add(tmpDistributionCalculationTask);
                            if (tmpDistributionCalculationTask.getIndex() > this.currentHighestIndex) {
                                this.currentHighestIndex = tmpDistributionCalculationTask.getIndex();
                                this.updateDisplay(tmpDistributionCalculationTask.getIndex());
                            }
                            else {
                                this.updateDisplay(-1);
                            }
                            this.startNextDistributionCalculationTask();
                        }
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Property change due to internal error in distribution calculation task">
                if (evt.getPropertyName().equals(ModelDefinitions.PROPERTY_CHANGE_ERROR)) {
                    // Remove this instance as propertyChangeListener
                    DistributionCalculationTask tmpDistributionCalculationTask = (DistributionCalculationTask) evt.getSource();
                    tmpDistributionCalculationTask.removePropertyChangeListener(this);
                    this.notifyChangeReceiver(this, new ChangeInformation(ChangeTypeEnum.INTERNAL_ERROR, null));
                }
                // </editor-fold>
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, 
                    String.format(GuiMessage.get("Error.CommandExecutionFailed"), "propertyChange()", "CustomPanelDistributionMovieController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), 
                    JOptionPane.ERROR_MESSAGE
                );
                // </editor-fold>
            }
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Kills all ongoing operations of this controller
     */
    public void kill() {
        this.isKilled = true;
        // <editor-fold defaultstate="collapsed" desc="Do not kill while starting distribution calculation tasks: Wait if necessary!">
        while (this.isStartingTasks) {
            try {
                Thread.sleep(Preferences.getInstance().getTimerIntervalInMilliseconds());
            } catch (InterruptedException anException) {
                ModelUtils.appendToLogfile(true, anException);
                // This should never happen!
                return;
            }
        }
        // </editor-fold>
        try {
            this.stopAnimation();
            this.stopStartedDistributionCalculationTasks();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Protected finalize method">
    /**
     * Finalize method for clean up: Stop all running tasks
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            this.kill();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        } finally {
            super.finalize();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Distribution calculation task related methods">
    /**
     * Creates and starts distribution calculation tasks
     */
    public void startDistributionCalculationTasks() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            this.isStartingTasks = true;
            this.stopAnimation();
            this.stopStartedDistributionCalculationTasks();
            // <editor-fold defaultstate="collapsed" desc="Create distributionCalculationTasks">
            this.distributionCalculationTasks = new ConcurrentLinkedQueue<>();
            this.startedDistributionCalculationTasks = new ConcurrentLinkedQueue<>();
            this.finishedDistributionCalculationTasks = new ConcurrentLinkedQueue<>();
            for (int i = 0; i < this.numberOfSteps; i++) {
                DistributionCalculationTask tmpDistributionCalculationTask = 
                    new DistributionCalculationTask(
                        this.particleType,
                        this.volumeAxis,
                        i, 
                        this.timeStepInfoArray[i].getJobResultParticlePositionsFilePathname(),
                        this.particleTypeDescriptionString,
                        Preferences.getInstance().getNumberOfVolumeBins(),
                        this.jobInputValueItemContainer
                    );
                tmpDistributionCalculationTask.addPropertyChangeListener(this);
                this.distributionCalculationTasks.add(tmpDistributionCalculationTask);
            }
            // </editor-fold>
            this.isKilled = false;
            this.startInitialDistributionCalculationTasks();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            this.isStartingTasks = false;
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "startDistributionCalculationTasks()", "CustomPanelDistributionMovieController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
            this.isStartingTasks = false;
        }
    }
    
    /**
     * Starts initial distribution calculation tasks
     */
    private void startInitialDistributionCalculationTasks() {
        for (int i = 1; i <= Preferences.getInstance().getNumberOfParallelSlicers(); i++) {
            this.startNextDistributionCalculationTask();
        }
    }
    
    /**
     * If there are distribution calculation tasks left to be executed this 
     * method will start the execution of the next one
     */
    private void startNextDistributionCalculationTask() {
        try {
            if (this.distributionCalculationTasks != null && this.distributionCalculationTasks.size() > 0) {
                DistributionCalculationTask tmpDistributionCalculationTask = this.distributionCalculationTasks.poll();
                if (tmpDistributionCalculationTask != null) {
                    this.startedDistributionCalculationTasks.add(tmpDistributionCalculationTask);
                    tmpDistributionCalculationTask.addPropertyChangeListener(this);
                    this.executorService.submit(tmpDistributionCalculationTask);
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "startNextDistributionCalculationTask()", "CustomPanelDistributionMovieController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    
    /**
     * Stops all started distribution calculation tasks
     */
    private void stopStartedDistributionCalculationTasks() {
        try {
            if (this.startedDistributionCalculationTasks != null && this.startedDistributionCalculationTasks.size() > 0) {
                int tmpSize = this.startedDistributionCalculationTasks.size();
                for (int i = 0; i < tmpSize; i++) {
                    DistributionCalculationTask tmpDistributionCalculationTask = this.startedDistributionCalculationTasks.poll();
                    tmpDistributionCalculationTask.removePropertyChangeListener(this);
                    if (tmpDistributionCalculationTask.isWorking()) {
                        tmpDistributionCalculationTask.stop();
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stopStartedDistributionCalculationTasks()", "CustomPanelDistributionMovieController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    
    /**
     * Returns true if all tasks have been finished and false otherwise.
     * 
     * @return True if all tasks have been finished and false otherwise.
     */
    private synchronized boolean areTasksFinished() throws InterruptedException, ExecutionException {
        if (this.startedDistributionCalculationTasks.size() > 0 || this.distributionCalculationTasks.size() > 0) {
            return false;
        }
        Iterator tmpIterator = this.finishedDistributionCalculationTasks.iterator();
        DistributionCalculationTask Task;
        while (tmpIterator.hasNext()) {
            Task = (DistributionCalculationTask) tmpIterator.next();
            if (!Task.isFinished()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Updates the progress label or the image (depending on whether all tasks 
     * are finished or not) of the customPanelDistributionMovie
     * 
     * @param anIndex Index of the distribution calculation task that was 
     * finished last
     */
    private synchronized void updateDisplay(int anIndex) {
        try {
            if (this.areTasksFinished()) {
                for (XyChartDataManipulator tmpXyChartDataManipulator : this.xyChartDataArray) {
                    tmpXyChartDataManipulator.setNumberOfDiscardedInitialPoints(this.chartConfiguration.getNumberOfDiscardedInitialPoints());
                    if (this.chartConfiguration.getIsZoom()) {
                        tmpXyChartDataManipulator.setZoomValues(this.chartConfiguration.getZoomValues());
                    }
                }
                this.dataArrayChart = 
                    new DataArrayChart(
                        this.xyChartDataArray, 
                        String.format(GuiMessage.get("DistributionMovie.title"), this.particleTypeDescriptionString, this.volumeAxis.toString().toLowerCase()),
                        GuiMessage.get("DistributionMovie.xAxisLabel"), 
                        GuiMessage.get("DistributionMovie.yAxisLabel"), 
                        this.chartConfiguration.getNumberToAverage(),
                        this.chartRange
                    );
                this.dataArrayChart.setIsOutlinePaintWhite(this.chartConfiguration.getIsOutlinePaintWhite());
                this.dataArrayChart.setIsFillColorWhite(this.chartConfiguration.getIsFillColorWhite());
                this.dataArrayChart.setIsInverted(this.chartConfiguration.getIsInverted());
                this.dataArrayChart.setIsShapePaint(this.chartConfiguration.getIsShapePaint());
                this.dataArrayChart.setIsThickLines(this.chartConfiguration.getIsThickLines());
                this.dataArrayChart.setHasTrendLine(this.chartConfiguration.getHasTrendLine());
                this.dataArrayChart.setHasLastPointMarked(this.chartConfiguration.getHasLastPointMarked());
                this.customPanelDistributionMovie.showMainPanel(true);
                this.setImage(
                    this.dataArrayChart.getImage(
                        this.customPanelDistributionMovie.getImagePanel().getWidth(), 
                        this.customPanelDistributionMovie.getImagePanel().getHeight(),
                        this.virtualSliderIndex
                    )
                );
                this.setStepInfo(this.timeStepInfoArray[this.virtualSliderIndex].getStepInfo());
            } else if (this.customPanelDistributionMovie.getProgressLabel().isVisible() && anIndex >= 0) {
                this.customPanelDistributionMovie.getProgressLabel().setText(
                    String.format(GuiMessage.get("DistributionMovie.CreatingChartsTimeStep"), this.miscUtilityMethods.getPercent(anIndex, this.numberOfSteps))
                );
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "updateDisplay()", "CustomPanelDistributionMovieController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    // </editor-fold>
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
     * animation is stopped
     */
    private void playAnimation() {
        try {
            if (!this.isAnimationPlaying()) {
                // Time between two action events of the timer
                double tmpDelay = ((1000 / Preferences.getInstance().getAnimationSpeed()));
                this.animationTimer = new Timer((int)tmpDelay, new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int tmpCurrentValue = CustomPanelDistributionMovieController.this.virtualSlider.getValue();
                        tmpCurrentValue += CustomPanelDistributionMovieController.this.virtualSlider.getMinorTickSpacing();
                        if (tmpCurrentValue == CustomPanelDistributionMovieController.this.virtualSlider.getMaximum()) {
                            CustomPanelDistributionMovieController.this.virtualSlider.setValueWithoutStateChangedEvent(0);
                        }
                        else {
                            CustomPanelDistributionMovieController.this.virtualSlider.setValueWithoutStateChangedEvent(tmpCurrentValue);
                        }
                        CustomPanelDistributionMovieController.this.virtualSliderIndex = CustomPanelDistributionMovieController.this.virtualSlider.getValue();
                        CustomPanelDistributionMovieController.this.setImage(CustomPanelDistributionMovieController.this.getImage(CustomPanelDistributionMovieController.this.virtualSliderIndex));
                        CustomPanelDistributionMovieController.this.setStepInfo(CustomPanelDistributionMovieController.this.timeStepInfoArray[CustomPanelDistributionMovieController.this.virtualSliderIndex].getStepInfo());
                    }

                });
                this.animationTimer.start();
                this.customPanelDistributionMovie.getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.toolTipText2"));
                this.customPanelDistributionMovie.getPlayAnimationButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.text2"));
            } else {
                this.stopAnimation();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "playAnimation()", "CustomPanelDistributionMovieController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }

    /**
     * Stops animation
     */
    private void stopAnimation() {
        try {
            if (this.isAnimationPlaying()) {
                this.animationTimer.stop();
                this.customPanelDistributionMovie.getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.toolTipText1"));
                this.customPanelDistributionMovie.getPlayAnimationButton().setText(GuiMessage.get("CustomPanelValueItemMatrixDiagram.playAnimationButton.text1"));
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stopAnimation()", "CustomPanelDistributionMovieController"),
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
    // <editor-fold defaultstate="collapsed" desc="- CustomPanelDistributionMovie related methods">
    /**
     * Display an image in the image panel
     * 
     * @param anImage An image
     */
    private void setImage(BufferedImage anImage) {
        if (anImage != null) {
            this.customPanelDistributionMovie.getImagePanel().setBasicImage(anImage);
        }
    }
    
    /**
     * Display the step info string in the info label
     * 
     * @param aStepInfoString A step info string
     */
    private void setStepInfo(String aStepInfoString) {
        if (aStepInfoString != null && !aStepInfoString.isEmpty()) {
            this.customPanelDistributionMovie.getInformationLabel().setText(aStepInfoString);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Zoom related methods">
    /**
     * Sets chart range
     */
    private void setChartRange() {
        this.stopAnimation();
        double[] tmpChartRange = this.dataArrayChart.getChartRange();
        this.chartRange = GuiUtils.getZoomBoundaries(tmpChartRange, tmpChartRange);
        if (this.chartRange != null) {
            this.updateDisplay(-1);
            this.customPanelDistributionMovie.getRestoreButton().setVisible(true);
        }
    }

    /**
     * Restores optimum boundaries
     */    
    private void restoreOptimumChartRange() {
        this.stopAnimation();
        this.chartRange = null;
        this.customPanelDistributionMovie.getRestoreButton().setVisible(false);
        this.updateDisplay(-1);
    }
    // </editor-fold>
    // </editor-fold>

}
