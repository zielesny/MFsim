/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2021  Achim Zielesny (achim.zielesny@googlemail.com)
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

import de.gnwi.mfsim.gui.util.VirtualSlider;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.gui.dialog.DialogSingleSlicerShow;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.model.util.ImageSelection;
import de.gnwi.mfsim.model.util.MovieSlicerConfiguration;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.util.ImageStorageEnum;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.graphics.slice.SimulationBoxViewSingleTimeStepSlice;
import de.gnwi.mfsim.model.job.TimeStepInfo;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.job.JobUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.util.concurrent.ExecutorService;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.mfsim.model.graphics.IImageProvider;

/**
 * Controller class for CustomPanelStepSlicer for simulation movie
 *
 * @author Achim Zielesny
 */
public class CustomPanelSimulationMovieSlicerController extends ChangeNotifier implements IImageProvider, ChangeListener, PropertyChangeListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * True: Controller is starting slicers, false: Otherwise.
     */
    private boolean isStartingSlicers;

    /**
     * Miscellaneous utility methods
     */
    private MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Utility for jobs
     */
    private final JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * Step slicer panel
     */
    private CustomPanelStepSlicer stepSlicerPanel;

    /**
     * File type of images
     */
    private ImageFileType imageFileType;

    /**
     * Current box view
     */
    private SimulationBoxViewEnum currentBoxView;

    /**
     * True: 3rd dimension scrolling is allowed, false: Otherwise
     */
    private boolean isThirdDimensionScroll;

    /**
     * List of started all SimulationBoxViewSingleTimeStepSlice instances
     */
    private ConcurrentLinkedQueue<SimulationBoxViewSingleTimeStepSlice> simulationBoxViewSingleTimeStepSliceList;

    /**
     * List of started SimulationBoxViewSingleTimeStepSlice instances
     */
    private ConcurrentLinkedQueue<SimulationBoxViewSingleTimeStepSlice> startedSimulationBoxViewSingleTimeStepSliceList;

    /**
     * Enlarged box size info
     */
    private BoxSizeInfo enlargedBoxSizeInfo;

    /**
     * Timer for animation
     */
    private Timer animationTimer;

    /**
     * Virtual slider
     */
    private VirtualSlider virtualSlider;

    /**
     * Current slice index
     */
    private int currentSliceIndex;

    /**
     * Array with simulation step infos
     */
    private TimeStepInfo[] timeStepInfoArray;

    /**
     * Value item container of corresponding Job Input to this.timeStepInfoArray
     */
    private ValueItemContainer jobInputValueItemContainer;

    /**
     * Movie slicer configuration
     */
    MovieSlicerConfiguration simulationMovieSlicerConfiguration;

    /**
     * HashMap that maps SimulationBoxViewEnum to TimeStepInfo array instance
     */
    private ConcurrentHashMap<SimulationBoxViewEnum, TimeStepInfo[]> simulationBoxViewToTimeStepInfoMap;

    /**
     * HashMap that maps SimulationBoxViewEnum to completeness information
     */
    private ConcurrentHashMap<SimulationBoxViewEnum, Boolean> simulationBoxViewToIsCompleteMap;

    /**
     * HashMap for identification of slicer instances
     */
    private ConcurrentHashMap<String, String> slicerIdentification;

    /**
     * True: Controller has been killed with the kill() method, false: Otherwise
     */
    private boolean isKilled;

    /**
     * Last intermediate image index
     */
    private int lastIntermediateImageIndex;

    /**
     * Slicer executor service
     */
    private ExecutorService slicerExecutorService;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param anExecutorService Executor service
     * @param aCustomPanelStepSlicer Panel this controller is made for
     * @param aTimeStepInfoArray Array with simulation step infos (where
     * internal boxViewIndex of each simulation step info MUST correspond to
     * index in aTimeStepInfoArray)
     * @param aJobInputValueItemContainer Value item container of corresponding
     * Job Input
     * @param anImageFileType File type of the images to be created
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelSimulationMovieSlicerController(
        ExecutorService anExecutorService,
        CustomPanelStepSlicer aCustomPanelStepSlicer, 
        TimeStepInfo[] aTimeStepInfoArray,
        ValueItemContainer aJobInputValueItemContainer, 
        ImageFileType anImageFileType
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anExecutorService == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aCustomPanelStepSlicer == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aTimeStepInfoArray == null || aTimeStepInfoArray.length == 0) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aJobInputValueItemContainer == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        for (int i = 0; i < aTimeStepInfoArray.length; i++) {
            if (!(new File(aTimeStepInfoArray[i].getJobResultParticlePositionsFilePathname())).isFile()) {
                throw new IllegalArgumentException("An argument is illegal.");
            }
            if (aTimeStepInfoArray[i].getBoxViewIndex() != i) {
                throw new IllegalArgumentException("An argument is illegal.");
            }
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            // NOTE: Command sequence is NOT to be changed
            // NOTE: Setting unscaled drawing of this.slicerPanel.getSlicerImagePanel().getDrawPanel() is NOT necessary since width and height correspond to control size
            this.slicerExecutorService = anExecutorService;
            this.isKilled = false;
            this.isStartingSlicers = false;
            this.stepSlicerPanel = aCustomPanelStepSlicer;
            this.stepSlicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(false);
            this.stepSlicerPanel.getSimulationBoxPanel().getCreateMovieButton().setVisible(false);
            this.stepSlicerPanel.getSimulationBoxPanel().getEditMoveAndSpinSettingsButton().setVisible(false);
            this.stepSlicerPanel.getSimulationBoxPanel().getBoxWaitButton().setVisible(false);
            this.stepSlicerPanel.getSimulationBoxPanel().getBoxMoveButton().setVisible(false);
            this.stepSlicerPanel.getSimulationBoxPanel().getSpinAroundXButton().setVisible(false);
            this.stepSlicerPanel.getSimulationBoxPanel().getSpinAroundYButton().setVisible(false);
            this.stepSlicerPanel.getSimulationBoxPanel().getSpinAroundZButton().setVisible(false);
            this.timeStepInfoArray = aTimeStepInfoArray;
            this.jobInputValueItemContainer = aJobInputValueItemContainer;
            this.imageFileType = anImageFileType;
            // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
            this.enlargedBoxSizeInfo
                    = this.jobUtilityMethods.getBoxSizeInfo(this.jobInputValueItemContainer).getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
            // Movie slicer configuration
            this.simulationMovieSlicerConfiguration = Preferences.getInstance().getSimulationMovieSlicerConfiguration();
            // Set virtualSlider BEFORE setting this.setThirdDimensionSlider()
            this.virtualSlider = new VirtualSlider(this.stepSlicerPanel.getSimulationBoxPanel().getThirdDimensionSlider());
            // Set ThirdDimensionSlider AFTER setting virtual slider
            this.setThirdDimensionSlider();
            // NOTE: this.updateBoxView() sets this.currentBoxView
            this.setBoxView(Preferences.getInstance().getBoxViewDisplay());
            switch (this.currentBoxView) {
                case XZ_FRONT:
                    this.stepSlicerPanel.getXzFrontRadioButton().setSelected(true);
                    break;
                case XZ_BACK:
                    this.stepSlicerPanel.getXzBackRadioButton().setSelected(true);
                    break;
                case YZ_LEFT:
                    this.stepSlicerPanel.getYzLeftRadioButton().setSelected(true);
                    break;
                case YZ_RIGHT:
                    this.stepSlicerPanel.getYzRightRadioButton().setSelected(true);
                    break;
                case XY_TOP:
                    this.stepSlicerPanel.getXyTopRadioButton().setSelected(true);
                    break;
                case XY_BOTTOM:
                    this.stepSlicerPanel.getXyBottomRadioButton().setSelected(true);
                    break;
            }
            // Set slicer start settings
            this.setSlicerStartSettings();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            // <editor-fold defaultstate="collapsed" desc="- Listeners for graphics panel">
            this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().addMouseWheelListener(new MouseWheelListener() {

                public void mouseWheelMoved(MouseWheelEvent e) {
                    try {
                        if (!CustomPanelSimulationMovieSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSimulationMovieSlicerController.this.setThirdDimensionIncrement(e.getWheelRotation());
                            CustomPanelSimulationMovieSlicerController.this.showBoxView();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseWheelMoved()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.stepSlicerPanel.getSimulationBoxPanel().getSaveGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSimulationMovieSlicerController.this.stopAnimation();
                        String tmpFilePathname = GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.selectGraphicsFileSaveChooser"), GuiDefinitions.IMAGE_BMP_FILE_EXTENSION);
                        if (tmpFilePathname != null) {
                            try {
                                GraphicsUtils.writeImageToFile(CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getImage(),
                                        ImageFileType.BMP, new File(tmpFilePathname));
                            } catch (Exception anExeption) {
                                // <editor-fold defaultstate="collapsed" desc="Show error message">
                                JOptionPane.showMessageDialog(null, GuiMessage.get("Error.SaveOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                        JOptionPane.ERROR_MESSAGE);

                                // </editor-fold>
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.stepSlicerPanel.getSimulationBoxPanel().getCopyGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSimulationMovieSlicerController.this.stopAnimation();
                        if (!ImageSelection.copyImageToClipboard(CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getImage())) {

                            // <editor-fold defaultstate="collapsed" desc="Show error message">
                            JOptionPane.showMessageDialog(null, GuiMessage.get("Error.CopyOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                    JOptionPane.ERROR_MESSAGE);

                            // </editor-fold>
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.stepSlicerPanel.getSimulationBoxPanel().getRedrawButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(false);
                        // Stop animation if necessary
                        CustomPanelSimulationMovieSlicerController.this.stopAnimation();
                        CustomPanelSimulationMovieSlicerController.this.startSlicers();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.stepSlicerPanel.getSimulationBoxPanel().getViewFullBoxButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    // FIRST: Save rotation and magnification
                    int tmpRotationAroundXaxisAngle = Preferences.getInstance().getRotationAroundXaxisAngle();
                    int tmpRotationAroundYaxisAngle = Preferences.getInstance().getRotationAroundYaxisAngle();
                    int tmpRotationAroundZaxisAngle = Preferences.getInstance().getRotationAroundZaxisAngle();
                    int tmpXShiftInPixel = Preferences.getInstance().getXshiftInPixelSlicer();
                    int tmpYShiftInPixel = Preferences.getInstance().getYshiftInPixelSlicer();
                    int tmpSimulationBoxMagnificationPercentage = Preferences.getInstance().getSimulationBoxMagnificationPercentage();
                    // THEN: Set current rotation of simulation step
                    // NOTE: Current rotation of simulation step may EXCEED 360 degree so use modulo operation
                    TimeStepInfo tmpCurrentTimeStepInfo = 
                        CustomPanelSimulationMovieSlicerController.this.simulationBoxViewToTimeStepInfoMap.
                            get(CustomPanelSimulationMovieSlicerController.this.currentBoxView)[CustomPanelSimulationMovieSlicerController.this.currentSliceIndex];
                    Preferences.getInstance().setRotationAroundXaxisAngle(((int) tmpCurrentTimeStepInfo.getRotationAroundXaxisAngle()) % 360);
                    Preferences.getInstance().setRotationAroundYaxisAngle(((int) tmpCurrentTimeStepInfo.getRotationAroundYaxisAngle()) % 360);
                    Preferences.getInstance().setRotationAroundZaxisAngle(((int) tmpCurrentTimeStepInfo.getRotationAroundZaxisAngle()) % 360);
                    try {
                        // Stop animation if necessary
                        CustomPanelSimulationMovieSlicerController.this.stopAnimation();
                        String tmpJobResultParticlePositionsFilePathname = 
                            CustomPanelSimulationMovieSlicerController.this.simulationBoxViewToTimeStepInfoMap.
                                get(CustomPanelSimulationMovieSlicerController.this.currentBoxView)[CustomPanelSimulationMovieSlicerController.this.currentSliceIndex].
                                    getJobResultParticlePositionsFilePathname();
                        // IMPORTANT: tmpJobResultParticlePositionsFilePathname may NOT be available (e.g. if simulation has finished)
                        if (!(new File(tmpJobResultParticlePositionsFilePathname)).isFile()) {
                            JOptionPane.showMessageDialog(null, 
                                GuiMessage.get("Error.CanNotFindGraphicalParticlePositions"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), 
                                JOptionPane.ERROR_MESSAGE
                            );
                        } else {
                            MouseCursorManagement.getInstance().setWaitCursor();
                            // Maximum of 10 repetitions with 1 second delay for each repetition
                            GraphicalParticlePositionInfo tmpGraphicalParticlePositionInfo = 
                                CustomPanelSimulationMovieSlicerController.this.jobUtilityMethods.readGraphicalParticlePositionsWithRepetitions(tmpJobResultParticlePositionsFilePathname, 
                                    CustomPanelSimulationMovieSlicerController.this.jobInputValueItemContainer,
                                    ModelDefinitions.NUMBER_OF_GRAPHICAL_PARTICLE_POSITION_FILE_READ_REPETITIONS,
                                    ModelDefinitions.GRAPHICAL_PARTICLE_POSITION_FILE_READ_DELAY
                                );
                            MouseCursorManagement.getInstance().setDefaultCursor();
                            if (tmpGraphicalParticlePositionInfo == null) {
                                JOptionPane.showMessageDialog(null, 
                                    GuiMessage.get("Information.GraphicalParticlePositionsAtWork.Message"), 
                                    GuiMessage.get("Information.GraphicalParticlePositionsAtWork.Title"),
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                            } else {
                                DialogSingleSlicerShow.show(String.format(GuiMessage.get("DialogSlicerShow.titleFormat"),
                                        CustomPanelSimulationMovieSlicerController.this.simulationBoxViewToTimeStepInfoMap.
                                        get(CustomPanelSimulationMovieSlicerController.this.currentBoxView)[CustomPanelSimulationMovieSlicerController.this.currentSliceIndex].getStepInfo()
                                    ),
                                    tmpGraphicalParticlePositionInfo,
                                    CustomPanelSimulationMovieSlicerController.this.currentBoxView
                                );
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    } finally {
                        // FINALLY: Restore rotation and magnification if necessary (may be changed by simulation step rotation and/or DialogSingleSlicerShow.show())
                        if (tmpRotationAroundXaxisAngle != Preferences.getInstance().getRotationAroundXaxisAngle()) {
                            Preferences.getInstance().setRotationAroundXaxisAngle(tmpRotationAroundXaxisAngle);
                        }
                        if (tmpRotationAroundYaxisAngle != Preferences.getInstance().getRotationAroundYaxisAngle()) {
                            Preferences.getInstance().setRotationAroundYaxisAngle(tmpRotationAroundYaxisAngle);
                        }
                        if (tmpRotationAroundZaxisAngle != Preferences.getInstance().getRotationAroundZaxisAngle()) {
                            Preferences.getInstance().setRotationAroundZaxisAngle(tmpRotationAroundZaxisAngle);
                        }
                        if (tmpXShiftInPixel != Preferences.getInstance().getXshiftInPixelSlicer()) {
                            Preferences.getInstance().setXshiftInPixelSlicer(tmpXShiftInPixel);
                        }
                        if (tmpYShiftInPixel != Preferences.getInstance().getYshiftInPixelSlicer()) {
                            Preferences.getInstance().setYshiftInPixelSlicer(tmpYShiftInPixel);
                        }
                        if (tmpSimulationBoxMagnificationPercentage != Preferences.getInstance().getSimulationBoxMagnificationPercentage()) {
                            Preferences.getInstance().setSimulationBoxMagnificationPercentage(tmpSimulationBoxMagnificationPercentage);
                        }
                        // Set default mouse cursor
                        MouseCursorManagement.getInstance().setDefaultCursor();
                    }
                }

            });

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listeners for settings panel">
            this.stepSlicerPanel.getAnimationRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.defaultAnimationButton.toolTipText"));
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.defaultAnimationButton.text"));
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.configureAnimationButton.toolTipText"));
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.configureAnimationButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.stepSlicerPanel.getMovieRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.makeMovieButton.toolTipText"));
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.makeMovieButton.text"));
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.configureMovieButton.toolTipText"));
                            CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.configureMovieButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.stepSlicerPanel.getFirstSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelSimulationMovieSlicerController.this.stopAnimation();

                        // </editor-fold>
                        if (CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelSimulationMovieSlicerController.this.setDefaultAnimationSettings();
                        }
                        if (CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelSimulationMovieSlicerController.this.startMovieCreation();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.stepSlicerPanel.getSecondSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelSimulationMovieSlicerController.this.stopAnimation();

                        // </editor-fold>
                        if (CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelSimulationMovieSlicerController.this.configureAnimationSettings();
                        }
                        if (CustomPanelSimulationMovieSlicerController.this.stepSlicerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelSimulationMovieSlicerController.this.configureMovieSettings();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.stepSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelSimulationMovieSlicerController.this.playAnimation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelSimulationMovieSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add this instance as change listener to third dimension slider">
            // Add this as change listener to third dimension slider
            this.stepSlicerPanel.getSimulationBoxPanel().getThirdDimensionSlider().addChangeListener(this);

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Select movie radio button">
            this.stepSlicerPanel.getMovieRadioButton().setSelected(true);

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "CustomPanelSimulationMovieSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- setVisibilityOfRedrawButton">
    /**
     * Sets visibility of redraw button
     *
     * @param aValue True: Redraw button is visible, false: Redraw button in
     * invisible
     */
    public void setVisibilityOfRedrawButton(boolean aValue) {
        if (this.stepSlicerPanel.getSimulationBoxPanel().getRedrawButton().isVisible() != aValue) {
            this.stepSlicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(aValue);
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- getImage">
    /**
     * Returns slice image
     *
     * @param anIndex Index
     * @return Slice image or null if image is not available
     */
    public BufferedImage getImage(int anIndex) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anIndex < 0 || anIndex > this.timeStepInfoArray.length - 1) {
            return null;
        }

        // </editor-fold>
        try {
            return this.simulationBoxViewToTimeStepInfoMap.get(this.currentBoxView)[anIndex].getImage();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "getSliceImage()", "CustomPanelSimulationMovieSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return null;
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- kill">
    /**
     * Kills all ongoing operations of controller
     */
    public void kill() {
        this.isKilled = true;
        // <editor-fold defaultstate="collapsed" desc="Do not kill while starting slicers: Wait if necessary!">
        while (this.isStartingSlicers) {
            try {
                Thread.sleep(Preferences.getInstance().getTimerIntervalInMilliseconds());
            } catch (InterruptedException anException) {
                ModelUtils.appendToLogfile(true, anException);
                // This should never happen!
                // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "kill()", "CustomPanelSimulationMovieSlicerController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
        }

        // </editor-fold>
        try {
            this.stopAnimation();
            this.stopStartedSlicers();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "kill()", "CustomPanelSimulationMovieSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- canStartSlicers">
    /**
     * Returns if slicers can be started by calling startSlicers()
     *
     * @return True: Slicers can be started, false: Otherwise
     */
    public boolean canStartSlicers() {
        return !this.isStartingSlicers && this.enlargedBoxSizeInfo != null;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- startSlicers">
    /**
     * Restarts simulation box slicer
     */
    public void startSlicers() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set wait cursor and set starting slicer flag">
            MouseCursorManagement.getInstance().setWaitCursor();
            this.isStartingSlicers = true;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
            this.stopAnimation();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set slicer start settings and third dimension slider">
            this.setSlicerStartSettings();
            this.setThirdDimensionSlider();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Stop old SimulationBoxViewSingleTimeStepSlice instances">
            this.stopStartedSlicers();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create destination path for images if necessary">
            String tmpDestinationPath = this.fileUtilityMethods.getUniqueTemporaryDirectoryPath();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create slicers">
            // Set slicer image panel ratio of height to width (may have changed due to changed box size info)
            this.setSlicerImagePanelRatioOfHeightToWidth();
            // Set this.enlargedBoxSizeInfo to null
            this.enlargedBoxSizeInfo = null;
            // Create new instances for safety reasons
            this.simulationBoxViewSingleTimeStepSliceList = new ConcurrentLinkedQueue<SimulationBoxViewSingleTimeStepSlice>();
            this.startedSimulationBoxViewSingleTimeStepSliceList = new ConcurrentLinkedQueue<SimulationBoxViewSingleTimeStepSlice>();
            this.simulationBoxViewToTimeStepInfoMap = new ConcurrentHashMap<SimulationBoxViewEnum, TimeStepInfo[]>(6);
            this.simulationBoxViewToIsCompleteMap = new ConcurrentHashMap<SimulationBoxViewEnum, Boolean>(6);
            // Important: Set this.currentBoxView
            this.slicerIdentification = new ConcurrentHashMap<String, String>(this.timeStepInfoArray.length);
            String tmpBoxViewDestinationPath = null;
            if (Preferences.getInstance().getImageStorageMode() == ImageStorageEnum.HARDDISK_COMPRESSED) {
                File tmpPath = new File(tmpDestinationPath, this.currentBoxView.name());
                this.fileUtilityMethods.createDirectory(tmpPath);
                tmpBoxViewDestinationPath = tmpPath.getPath();
            }
            for (int i = 0; i < this.timeStepInfoArray.length; i++) {
                this.timeStepInfoArray[i].clearData();
                this.timeStepInfoArray[i].setBoxView(this.currentBoxView);
                SimulationBoxViewSingleTimeStepSlice tmpSimulationBoxViewSingleTimeStepSlice = new SimulationBoxViewSingleTimeStepSlice(
                        this.timeStepInfoArray[i],
                        tmpBoxViewDestinationPath,
                        this.jobInputValueItemContainer,
                        this.simulationMovieSlicerConfiguration,
                        this.imageFileType,
                        this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getWidth(),
                        this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getHeight());
                // Add this as property change listener to simulation box slicer
                tmpSimulationBoxViewSingleTimeStepSlice.addPropertyChangeListener(this);
                // Add to map
                this.simulationBoxViewSingleTimeStepSliceList.add(tmpSimulationBoxViewSingleTimeStepSlice);
            }
            this.simulationBoxViewToTimeStepInfoMap.put(this.currentBoxView, new TimeStepInfo[this.timeStepInfoArray.length]);
            this.simulationBoxViewToIsCompleteMap.put(this.currentBoxView, false);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Controller is NOT killed">
            this.isKilled = false;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Start initial slicers">
            this.startInitialSlicers();

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set default cursor">
            MouseCursorManagement.getInstance().setDefaultCursor();

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Set default cursor and set starting slicer flag">
            MouseCursorManagement.getInstance().setDefaultCursor();
            this.isStartingSlicers = false;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "startSlicers()", "CustomPanelSimulationMovieSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            // <editor-fold defaultstate="collapsed" desc="Set default cursor and set starting slicer flag">
            MouseCursorManagement.getInstance().setDefaultCursor();
            this.isStartingSlicers = false;

            // </editor-fold>
        }
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get only)">
    // <editor-fold defaultstate="collapsed" desc="- ImageFileType">
    /**
     * Returns image file type
     *
     * @return Image file type
     */
    public ImageFileType getImageFileType() {
        return this.imageFileType;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- NumberOfImages">
    /**
     * Returns number of images
     *
     * @return Number of images
     */
    public int getNumberOfImages() {
        return this.timeStepInfoArray.length;
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public events">
    // <editor-fold defaultstate="collapsed" desc="- ChangeListener stateChanged">
    /**
     * ChangeListener state changed
     *
     * @param e ChangeEvent
     */
    public void stateChanged(ChangeEvent e) {
        try {
            // <editor-fold defaultstate="collapsed" desc="Check if 3rd dimension scroll is allowed">
            if (!this.isThirdDimensionScroll) {
                this.virtualSlider.setValue(0);
                return;
            }

            // </editor-fold>
            this.virtualSlider.setValueFromSlider();
            this.showBoxView();
            this.setSliceImage();
            this.showStep();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "CustomPanelSimulationMovieSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- PropertyChangeListener propertyChange">
    /**
     * PropertyChangeListener property change
     *
     * @param e PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (!this.isKilled) {
            try {
                // <editor-fold defaultstate="collapsed" desc="Property change due to progress of slicer">
                if (e.getPropertyName().equals(ModelDefinitions.PROPERTY_CHANGE_PROGRESS)) {
                    int tmpProgressValue = (Integer) e.getNewValue();
                    if (tmpProgressValue == 100) {
                        SimulationBoxViewSingleTimeStepSlice tmpSimulationBoxViewSingleTimeStepSlice = (SimulationBoxViewSingleTimeStepSlice) e.getSource();
                        if (!this.slicerIdentification.containsKey(tmpSimulationBoxViewSingleTimeStepSlice.getGloballyUniqueID())) {
                            this.slicerIdentification.put(tmpSimulationBoxViewSingleTimeStepSlice.getGloballyUniqueID(), tmpSimulationBoxViewSingleTimeStepSlice.getGloballyUniqueID());
                            // Set this.enlargedBoxSizeInfo
                            if (this.enlargedBoxSizeInfo == null) {
                                this.enlargedBoxSizeInfo = tmpSimulationBoxViewSingleTimeStepSlice.getBoxSizeInfo();
                            }
                            // Set simulation step info
                            TimeStepInfo tmpTimeStepInfo = tmpSimulationBoxViewSingleTimeStepSlice.getTimeStepInfo();
                            TimeStepInfo[] tmpTimeStepInfoArray = this.simulationBoxViewToTimeStepInfoMap.get(tmpTimeStepInfo.getBoxView());
                            tmpTimeStepInfoArray[tmpTimeStepInfo.getBoxViewIndex()] = tmpTimeStepInfo;
                            // IMPORTANT: Remove property change listener of tmpSimulationBoxViewSingleTimeStepSlice ...
                            tmpSimulationBoxViewSingleTimeStepSlice.removePropertyChangeListener(this);
                            // ... and remove from this.startedSimulationBoxViewSingleTimeStepSliceList
                            this.startedSimulationBoxViewSingleTimeStepSliceList.remove(tmpSimulationBoxViewSingleTimeStepSlice);
                            // Update display
                            if (tmpTimeStepInfo.getBoxViewIndex() > this.lastIntermediateImageIndex) {
                                this.lastIntermediateImageIndex = tmpTimeStepInfo.getBoxViewIndex();
                                this.updateDisplay(tmpTimeStepInfo.getStepImage());
                            } else {
                                this.updateDisplay(null);
                            }
                        }
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Property change due to internal error in slicer">
                if (e.getPropertyName() == ModelDefinitions.PROPERTY_CHANGE_ERROR) {
                    // Remove this instance as propertyChangeListener
                    SimulationBoxViewSingleTimeStepSlice tmpSimulationBoxViewSingleTimeStepSlice = (SimulationBoxViewSingleTimeStepSlice) e.getSource();
                    tmpSimulationBoxViewSingleTimeStepSlice.removePropertyChangeListener(this);
                    this.notifyChangeReceiver(this, new ChangeInformation(ChangeTypeEnum.INTERNAL_ERROR, null));
                }

                // </editor-fold>
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "propertyChange()", "CustomPanelSimulationMovieSlicerController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
            }
        }
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Show methods">
    /**
     * Shows no coordinates
     */
    private void showBoxView() {
        this.stepSlicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("CompartmentGraphics.BoxView"), this.currentBoxView.toRepresentation(), Preferences.getInstance().getFirstSliceIndex()));
    }

    /**
     * Shows step
     */
    private void showStep() {
        this.stepSlicerPanel.getSimulationBoxPanel().getInfoLabel().setText(this.simulationBoxViewToTimeStepInfoMap.get(this.currentBoxView)[this.currentSliceIndex].getStepInfo());
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Set methods">
    /**
     * Sets box view
     *
     * @param aBoxView Box view
     */
    private void setBoxView(SimulationBoxViewEnum aBoxView) {
        this.currentBoxView = aBoxView;
        this.setSlicerImagePanelRatioOfHeightToWidth();
        // Set third dimension slider to 0
        this.virtualSlider.setValue(0);
        this.showBoxView();
        this.setSimulationBoxViewImage();
    }

    /**
     * Sets this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel() with
     * ratio of height to width according to this.currentBoxView and
     * this.enlargedBoxSizeInfo
     */
    private void setSlicerImagePanelRatioOfHeightToWidth() {
        switch (this.currentBoxView) {
            case XZ_FRONT:
            case XZ_BACK:
                this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        / this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength());
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        / this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength());
                break;
            case XY_TOP:
            case XY_BOTTOM:
                this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength()
                        / this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength());
                break;
        }
    }

    /**
     * Sets third dimension
     *
     * @param anIncrementItemNumber Number of increment items
     */
    private void setThirdDimensionIncrement(int anIncrementItemNumber) {

        // <editor-fold defaultstate="collapsed" desc="Check if 3rd dimension scroll is allowed">
        if (!this.isThirdDimensionScroll) {
            return;
        }

        // </editor-fold>
        int tmpCurrentSliderValue = this.virtualSlider.getValue();
        // NOTE: The "-" is IMPORTANT for "intuitive" scrolling with mouse wheel
        tmpCurrentSliderValue -= anIncrementItemNumber * this.virtualSlider.getMinorTickSpacing();
        this.virtualSlider.setValueWithoutStateChangedEvent(tmpCurrentSliderValue);
        this.setSliceImage();
        this.showStep();
    }

    /**
     * Sets settings at slicer start
     */
    private void setSlicerStartSettings() {
        // Set slice image creation information
        this.stepSlicerPanel.getGeneralInfoLabel().setVisible(true);
        // Initializse this.lastIntermediateImageIndex
        this.lastIntermediateImageIndex = -1;
        this.stepSlicerPanel.getGeneralInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.CreatingSliceImages"));
        // Set simulation box panel controls invisible
        this.stepSlicerPanel.getSimulationBoxPanel().setVisible(false);
        // Set box view panel and controls invisible
        this.stepSlicerPanel.getBoxViewPanel().setVisible(false);
        this.stepSlicerPanel.getXzFrontRadioButton().setVisible(false);
        this.stepSlicerPanel.getXzBackRadioButton().setVisible(false);
        this.stepSlicerPanel.getYzLeftRadioButton().setVisible(false);
        this.stepSlicerPanel.getYzRightRadioButton().setVisible(false);
        this.stepSlicerPanel.getXyTopRadioButton().setVisible(false);
        this.stepSlicerPanel.getXyBottomRadioButton().setVisible(false);
        this.stepSlicerPanel.getBoxViewImagePanel().setVisible(false);
        // Set settings panel invisible
        this.stepSlicerPanel.getSettingsPanel().setVisible(false);
        // Disable 3rd dimension scrolling
        this.isThirdDimensionScroll = false;
    }

    /**
     * Sets slice image
     *
     * @return True: Slice image could be set, false: Slice image was not
     * available
     */
    private boolean setSliceImage() {
        this.currentSliceIndex = this.virtualSlider.getValue() / this.virtualSlider.getMinorTickSpacing();
        BufferedImage tmpImage = this.simulationBoxViewToTimeStepInfoMap.get(this.currentBoxView)[this.currentSliceIndex].getImage();
        if (tmpImage == null) {
            return false;
        } else {
            this.stepSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBasicImage(tmpImage);
            return true;
        }
    }

    /**
     * Sets third dimension slider
     */
    private void setThirdDimensionSlider() {
        this.virtualSlider.setSliderParameters(this.timeStepInfoArray.length);
        this.virtualSlider.setValue(0);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Configure/default animation settings">
    /**
     * Configures animation settings
     */
    private void configureAnimationSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSimulationMovieSlicerController.this.stopAnimation();

        // </editor-fold>
        ValueItemContainer tmpAnimationSettingsValueItemContainer = Preferences.getInstance().getAnimationEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesAnimationSettingsDialog.title"), tmpAnimationSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpAnimationSettingsValueItemContainer);
            // Do NOT call this.startSlicers() since changed animation settings work with current slices
        }
    }

    /**
     * Sets default animation settings
     */
    private void setDefaultAnimationSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSimulationMovieSlicerController.this.stopAnimation();

        // </editor-fold>
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setAnimationSpeed(Preferences.getInstance().getDefaultAnimationSpeed());
            // Do NOT call this.startSlicers() since changed animation settings work with current slices
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Movie related methods">
    /**
     * Configures movie settings
     */
    private void configureMovieSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSimulationMovieSlicerController.this.stopAnimation();

        // </editor-fold>
        ValueItemContainer tmpMovieSettingsValueItemContainer = Preferences.getInstance().getSimulationMovieEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesMovieSettingsDialog.title"), tmpMovieSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpMovieSettingsValueItemContainer);
        }

    }

    /**
     * Starts movie creation
     */
    private void startMovieCreation() {
        // Parameter false: No backwards images
        GuiUtils.createMovieImages(this, false, Preferences.getInstance().getImageDirectoryPathForSimulationMovies());
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Animation related methods">
    // NOTE: Method stopAnimation() is in the public section
    /**
     * Plays animation
     */
    private void playAnimation() {
        if (!this.isAnimationPlaying()) {
            int tmpDelay = 1000 / Preferences.getInstance().getAnimationSpeed();
            this.animationTimer = new Timer(tmpDelay, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    int tmpSliceIndex = CustomPanelSimulationMovieSlicerController.this.virtualSlider.getValue() / CustomPanelSimulationMovieSlicerController.this.virtualSlider.getMinorTickSpacing();
                    if (tmpSliceIndex == CustomPanelSimulationMovieSlicerController.this.timeStepInfoArray.length - 1) {
                        CustomPanelSimulationMovieSlicerController.this.virtualSlider.setValueWithoutStateChangedEvent(0);
                    } else {
                        CustomPanelSimulationMovieSlicerController.this.virtualSlider.setValueWithoutStateChangedEvent(CustomPanelSimulationMovieSlicerController.this.virtualSlider.getValue()
                                + CustomPanelSimulationMovieSlicerController.this.virtualSlider.getMinorTickSpacing());
                    }
                    CustomPanelSimulationMovieSlicerController.this.showBoxView();
                    CustomPanelSimulationMovieSlicerController.this.setSliceImage();
                    CustomPanelSimulationMovieSlicerController.this.showStep();
                }

            });
            this.animationTimer.start();
            this.stepSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textStop"));
            this.stepSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText2"));
        } else {
            this.stopAnimation();
        }
    }

    /**
     * Stops animation
     */
    private void stopAnimation() {
        if (this.isAnimationPlaying()) {
            this.animationTimer.stop();
            this.stepSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textPlay"));
            this.stepSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText"));
        }
    }

    /**
     * True: Animation is playing, false: Otherwise
     *
     * @return True: Animation is playing, false: Otherwise
     */
    private boolean isAnimationPlaying() {
        return this.animationTimer != null && this.animationTimer.isRunning();

    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    /**
     * Updates display
     *
     * @param aStepImage Step image
     */
    private void updateDisplay(BufferedImage aStepImage) {
        LinkedList<SimulationBoxViewEnum> tmpFinishedBoxViewList = this.getFinishedBoxViews();
        if (tmpFinishedBoxViewList != null && tmpFinishedBoxViewList.size() > 0) {
            // <editor-fold defaultstate="collapsed" desc="Set finished box view controls">
            for (SimulationBoxViewEnum tmpSingleBoxView : tmpFinishedBoxViewList) {
                switch (tmpSingleBoxView) {
                    case XZ_FRONT:
                        if (!this.stepSlicerPanel.getXzFrontRadioButton().isVisible()) {
                            this.stepSlicerPanel.getXzFrontRadioButton().setVisible(true);
                            this.isThirdDimensionScroll = true;
                        }
                        break;
                    case XZ_BACK:
                        if (!this.stepSlicerPanel.getXzBackRadioButton().isVisible()) {
                            this.stepSlicerPanel.getXzBackRadioButton().setVisible(true);
                            this.isThirdDimensionScroll = true;
                        }
                        break;
                    case YZ_LEFT:
                        if (!this.stepSlicerPanel.getYzLeftRadioButton().isVisible()) {
                            this.stepSlicerPanel.getYzLeftRadioButton().setVisible(true);
                            this.isThirdDimensionScroll = true;
                        }
                        break;
                    case YZ_RIGHT:
                        if (!this.stepSlicerPanel.getYzRightRadioButton().isVisible()) {
                            this.stepSlicerPanel.getYzRightRadioButton().setVisible(true);
                            this.isThirdDimensionScroll = true;
                        }
                        break;
                    case XY_TOP:
                        if (!this.stepSlicerPanel.getXyTopRadioButton().isVisible()) {
                            this.stepSlicerPanel.getXyTopRadioButton().setVisible(true);
                            this.isThirdDimensionScroll = true;
                        }
                        break;
                    case XY_BOTTOM:
                        if (!this.stepSlicerPanel.getXyBottomRadioButton().isVisible()) {
                            this.stepSlicerPanel.getXyBottomRadioButton().setVisible(true);
                            this.isThirdDimensionScroll = true;
                        }
                        break;
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Display settings panels">
            if (this.stepSlicerPanel.getXzFrontRadioButton().isVisible() || this.stepSlicerPanel.getXzBackRadioButton().isVisible() || this.stepSlicerPanel.getYzLeftRadioButton().isVisible()
                    || this.stepSlicerPanel.getYzRightRadioButton().isVisible() || this.stepSlicerPanel.getXyTopRadioButton().isVisible() || this.stepSlicerPanel.getXyBottomRadioButton().isVisible()) {
                this.stepSlicerPanel.getSettingsPanel().setVisible(true);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set box view panel and box view image panel">
            if (!this.stepSlicerPanel.getBoxViewPanel().isVisible()) {
                this.stepSlicerPanel.getBoxViewPanel().setVisible(true);
            }
            if (!this.stepSlicerPanel.getBoxViewImagePanel().isVisible()) {
                this.stepSlicerPanel.getBoxViewImagePanel().setVisible(true);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set slice image if necessary">
            if (!this.stepSlicerPanel.getSimulationBoxPanel().isVisible()) {

                // <editor-fold defaultstate="collapsed" desc="Set virtual slider with default slice index">
                this.virtualSlider.setValue(0);
                this.showBoxView();

                // </editor-fold>
                if (this.setSliceImage()) {
                    this.stepSlicerPanel.getGeneralInfoLabel().setVisible(false);
                    this.showStep();
                    this.stepSlicerPanel.getSimulationBoxPanel().setVisible(true);
                }
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Start animation">
            this.playAnimation();
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Set info label and step image if necessary/defined">
            if (!this.stepSlicerPanel.getBoxViewPanel().isVisible()) {
                this.stepSlicerPanel.getGeneralInfoLabel().setText(
                    String.format(
                        GuiMessage.get("SimulationBoxSlicer.CreatingSliceImagesTimeStep"), 
                        this.miscUtilityMethods.getPercent(
                            this.timeStepInfoArray.length - this.simulationBoxViewSingleTimeStepSliceList.size(), this.timeStepInfoArray.length)
                    )
                );
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Start next slicer">
            this.startNextSlicer();
            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Resource image related methods">
    /**
     * Sets compartments image
     */
    private void setSimulationBoxViewImage() {
        switch (this.currentBoxView) {
            case XZ_FRONT:
                GuiUtils.setResourceImage(this.stepSlicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_FRONT_IMAGE_FILENAME);
                break;
            case XZ_BACK:
                GuiUtils.setResourceImage(this.stepSlicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_BACK_IMAGE_FILENAME);
                break;
            case YZ_LEFT:
                GuiUtils.setResourceImage(this.stepSlicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_LEFT_IMAGE_FILENAME);
                break;
            case YZ_RIGHT:
                GuiUtils.setResourceImage(this.stepSlicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_RIGHT_IMAGE_FILENAME);
                break;
            case XY_TOP:
                GuiUtils.setResourceImage(this.stepSlicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_TOP_IMAGE_FILENAME);
                break;
            case XY_BOTTOM:
                GuiUtils.setResourceImage(this.stepSlicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_BOTTOM_IMAGE_FILENAME);
                break;
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Slicer related methods">
    /**
     * Returns box views of finished slicers
     *
     * @return Box views of finished slicers
     */
    private LinkedList<SimulationBoxViewEnum> getFinishedBoxViews() {
        LinkedList<SimulationBoxViewEnum> tmpBoxViewList = new LinkedList<SimulationBoxViewEnum>();
        Set<SimulationBoxViewEnum> tmpBoxViewSet = this.simulationBoxViewToTimeStepInfoMap.keySet();
        Iterator<SimulationBoxViewEnum> tmpBoxViewSetIterator = tmpBoxViewSet.iterator();
        while (tmpBoxViewSetIterator.hasNext()) {
            SimulationBoxViewEnum tmpBoxView = tmpBoxViewSetIterator.next();
            if (this.simulationBoxViewToIsCompleteMap.get(tmpBoxView)) {
                tmpBoxViewList.add(tmpBoxView);
            } else {
                TimeStepInfo[] tmpTimeStepInfoArray = this.simulationBoxViewToTimeStepInfoMap.get(tmpBoxView);
                boolean tmpIsComplete = true;
                for (TimeStepInfo tmpTimeStepInfo : tmpTimeStepInfoArray) {
                    if (tmpTimeStepInfo == null || !tmpTimeStepInfo.hasImageInformation()) {
                        tmpIsComplete = false;
                        break;
                    }
                }
                if (tmpIsComplete) {
                    tmpBoxViewList.add(tmpBoxView);
                    this.simulationBoxViewToIsCompleteMap.remove(tmpBoxView);
                    this.simulationBoxViewToIsCompleteMap.put(tmpBoxView, true);
                }
            }
        }
        if (tmpBoxViewList.size() > 0) {
            return tmpBoxViewList;
        } else {
            return null;
        }
    }

    /**
     * Starts initial slicers
     */
    private void startInitialSlicers() {
        for (int i = 0; i < Preferences.getInstance().getNumberOfParallelSlicers(); i++) {
            this.startNextSlicer();
        }
    }

    /**
     * Starts next slicer
     */
    private void startNextSlicer() {
        if (this.simulationBoxViewSingleTimeStepSliceList != null 
            && this.simulationBoxViewSingleTimeStepSliceList.size() > 0
            && !this.slicerExecutorService.isShutdown()) {
            SimulationBoxViewSingleTimeStepSlice tmpSimulationBoxViewSingleTimeStepSlice = this.simulationBoxViewSingleTimeStepSliceList.poll();
            if (tmpSimulationBoxViewSingleTimeStepSlice != null) {
                this.startedSimulationBoxViewSingleTimeStepSliceList.add(tmpSimulationBoxViewSingleTimeStepSlice);
                tmpSimulationBoxViewSingleTimeStepSlice.addPropertyChangeListener(this);
                this.slicerExecutorService.submit(tmpSimulationBoxViewSingleTimeStepSlice);
            }
        }
    }

    /**
     * Stops started slicers
     */
    private void stopStartedSlicers() {
        if (this.startedSimulationBoxViewSingleTimeStepSliceList != null && this.startedSimulationBoxViewSingleTimeStepSliceList.size() > 0) {
            for (int i = 0; i < this.startedSimulationBoxViewSingleTimeStepSliceList.size(); i++) {
                SimulationBoxViewSingleTimeStepSlice tmpSimulationBoxViewSingleTimeStepSlice = this.startedSimulationBoxViewSingleTimeStepSliceList.poll();
                if (tmpSimulationBoxViewSingleTimeStepSlice != null) {
                    // IMPORTANT: Remove property change listener of tmpSimulationBoxViewSingleTimeStepSlice
                    tmpSimulationBoxViewSingleTimeStepSlice.removePropertyChangeListener(this);
                    if (tmpSimulationBoxViewSingleTimeStepSlice.isWorking()) {
                        tmpSimulationBoxViewSingleTimeStepSlice.stop();
                    }
                }
            }
        }
    }
    // </editor-fold>
    // </editor-fold>
   
}
