/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2023  Achim Zielesny (achim.zielesny@googlemail.com)
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

import de.gnwi.mfsim.gui.dialog.DialogValueItemShow;
import de.gnwi.mfsim.gui.util.VirtualSlider;
import de.gnwi.mfsim.gui.dialog.DialogSpinStepSlicerShow;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.dialog.DialogMoveStepSlicerShow;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.model.util.SimulationBoxChangeInfo;
import de.gnwi.mfsim.model.util.ImageSelection;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.graphics.point.PointInPlane;
import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.slice.SimulationBoxViewSlicer;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.gui.util.SpinAxisEnum;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.spices.IPointInSpace;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.mfsim.model.graphics.IImageProvider;

/**
 * Controller class for CustomPanelSlicer
 *
 * @author Achim Zielesny
 */
public class CustomPanelSlicerController extends ChangeNotifier implements IImageProvider, ChangeListener {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Mouse counter for debug purposes
     */
    private int debugCounter;

    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Utility graphics methods
     */
    private final GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Slicer panel
     */
    private CustomPanelSlicer slicerPanel;

    /**
     * File type of images
     */
    private ImageFileType imageFileType;

    /**
     * Current box view
     */
    private SimulationBoxViewEnum currentBoxView;

    /**
     * Current x position in box
     */
    private double currentX;

    /**
     * Current y position in box
     */
    private double currentY;

    /**
     * Current z position in box
     */
    private double currentZ;

    /**
     * True: 3rd dimension scrolling is allowed, false: Otherwise
     */
    private boolean isThirdDimensionScroll;

    /**
     * First point for measurement
     */
    private PointInSpace firstPointForMeasurement;

    /**
     * First slice index for measurement
     */
    private int firstSliceIndexForMeasurement;

    /**
     * Second point for measurement
     */
    private PointInSpace secondPointForMeasurement;

    /**
     * Second slice index for measurement
     */
    private int secondSliceIndexForMeasurement;

    /**
     * First zoom point
     */
    private PointInSpace firstZoomPoint;

    /**
     * Second zoom point
     */
    private PointInSpace secondZoomPoint;

    /**
     * GraphicalParticlePositionInfo instance
     */
    private GraphicalParticlePositionInfo graphicalParticlePositionInfo;

    /**
     * Simulation box view slicer
     */
    private SimulationBoxViewSlicer simulationBoxViewSlicer;
    
    /**
     * Enlarged box size info
     */
    private BoxSizeInfo enlargedBoxSizeInfo;

    /**
     * x-value of mouse if left mouse-button is pressed
     */
    private int mousePressedX;

    /**
     * y-value of mouse if left mouse-button is pressed
     */
    private int mousePressedY;

    /**
     * Value of x-shift if left mouse-button is pressed
     */
    private int mousePressedXshift;

    /**
     * Value of y-shift if left mouse-button is pressed
     */
    private int mousePressedYshift;

    /**
     * Value of x-rotation if left mouse-button is pressed
     */
    private int mousePressedXRotationAngle;

    /**
     * Value of y-rotation if left mouse-button is pressed
     */
    private int mousePressedYRotationAngle;

    /**
     * Value of z-rotation if left mouse-button is pressed
     */
    private int mousePressedZRotationAngle;

    /**
     * Simulation box magnification percentage if left mouse-button is pressed
     * with shift-key
     */
    private int mousePressedMagnificationPercentage;

    /**
     * True: Animation is playing forwards, false: Otherwise
     */
    private boolean isAnimationPlayingForwards;

    /**
     * Timer for animation
     */
    private Timer animationTimer;

    /**
     * Virtual slider
     */
    private VirtualSlider virtualSlider;

    /**
     * Number of box move steps
     */
    private int numberOfBoxMoveSteps;

    /**
     * Simulation box change info for box move
     */
    private SimulationBoxChangeInfo boxMoveChangeInfo;

    /**
     * True: Molecule selection is possible, false: Otherwise
     */
    private boolean isMoleculeSelection;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelSlicer Panel this controller is made for
     * @param aGraphicalParticlePositionInfo GraphicalParticlePositionInfo instance
     * @param anImageFileType File type of the images to be created
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelSlicerController(
        CustomPanelSlicer aCustomPanelSlicer, 
        GraphicalParticlePositionInfo aGraphicalParticlePositionInfo, 
        ImageFileType anImageFileType
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelSlicer == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aGraphicalParticlePositionInfo == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            // NOTE: Command sequence is NOT to be changed
            // NOTE: Setting unscaled drawing of this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel()
            // is NOT necessary since width and height correspond to control size
            this.numberOfBoxMoveSteps = 0;
            this.boxMoveChangeInfo = new SimulationBoxChangeInfo();
            
            this.slicerPanel = aCustomPanelSlicer;
            // Set visibility
            this.slicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(false);
            this.slicerPanel.getBoxSettingsPanel().setVisible(false);
            this.slicerPanel.getFogSettingsPanel().setVisible(false);
            this.slicerPanel.getZoomPanel().setVisible(false);
            this.setParticleShiftRemoveButtonVisibility();
            
            this.imageFileType = anImageFileType;
            this.graphicalParticlePositionInfo = aGraphicalParticlePositionInfo;
            this.isMoleculeSelection = this.graphicalParticlePositionInfo.isMoleculeSelection();
            // Set value item container for molecule/particles display settings if available
            if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasMoleculeDisplaySettingsValueItemContainer()) {
                this.graphicalParticlePositionInfo.getGraphicalParticleInfo().setExclusionsColorsAndPreferences(Preferences.getInstance().getSimulationMovieSlicerConfiguration().getMoleculeDisplaySettingsValueItemContainer());
            }
            // Set exclusion box size info if available
            if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasExclusionBoxSizeInfo()) {
                this.graphicalParticlePositionInfo.setExclusionBoxSizeInfo(Preferences.getInstance().getSimulationMovieSlicerConfiguration().getExclusionBoxSizeInfo());
            }
            // Disable molecule selection if necessary
            if (this.isMoleculeSelection) {
                this.slicerPanel.getSelectionRadioButton().setVisible(true);
                // Set molecule selection manager ...
                if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasMoleculeSelectionManager()) {
                    this.graphicalParticlePositionInfo.setMoleculeSelectionManager(Preferences.getInstance().getSimulationMovieSlicerConfiguration().getMoleculeSelectionManager());
                }
                // ... and corresponding display
                this.setMoleculeSelectionDisplay();
            } else {
                this.slicerPanel.getSelectionRadioButton().setVisible(false);
            }
            // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
            this.enlargedBoxSizeInfo = this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
            // No measurement
            // NOTE: Exclusion box size info above is NOT changed but there is no measurement!
            this.removeMeasurementAndSetZoomPanel();
            this.firstZoomPoint = null;
            this.secondZoomPoint = null;
            // Set view full box button and general info label invisible
            this.slicerPanel.getSimulationBoxPanel().getViewFullBoxButton().setVisible(false);
            // Set virtualSlider BEFORE setting this.setThirdDimensionSlider()
            this.virtualSlider = new VirtualSlider(this.slicerPanel.getSimulationBoxPanel().getThirdDimensionSlider());
            // Set ThirdDimensionSlider AFTER setting virtual slider
            this.setThirdDimensionSlider();
            // NOTE: this.setBoxView() sets this.currentBoxView
            this.setBoxView(Preferences.getInstance().getBoxViewDisplay());
            switch (this.currentBoxView) {
                case XZ_FRONT:
                    this.slicerPanel.getXzFrontRadioButton().setSelected(true);
                    break;
                case XZ_BACK:
                    this.slicerPanel.getXzBackRadioButton().setSelected(true);
                    break;
                case YZ_LEFT:
                    this.slicerPanel.getYzLeftRadioButton().setSelected(true);
                    break;
                case YZ_RIGHT:
                    this.slicerPanel.getYzRightRadioButton().setSelected(true);
                    break;
                case XY_TOP:
                    this.slicerPanel.getXyTopRadioButton().setSelected(true);
                    break;
                case XY_BOTTOM:
                    this.slicerPanel.getXyBottomRadioButton().setSelected(true);
                    break;
            }
            // Update tool tip texts
            this.slicerPanel.getBoxViewImagePanel().setToolTipText(GuiMessage.get("CustomPanelSlicer.boxViewImagePanel.extendedToolTipText"));
            this.slicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.coordinatesLabel.extentedToolTipText"));
            this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.infoLabel.extendedToolTipText"));
            // Set slicer start settings
            this.setSlicerStartSettings();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Animation settings">
            this.isAnimationPlayingForwards = false;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            // <editor-fold defaultstate="collapsed" desc="- Listeners for graphics panel">
            this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().addMouseMotionListener(new MouseMotionAdapter() {

                public void mouseDragged(MouseEvent e) {
                    try {
                        if (!CustomPanelSlicerController.this.isAnimationPlaying()) {
                            if (e.isShiftDown() && !e.isControlDown() && !e.isAltDown() && SwingUtilities.isLeftMouseButton(e)) { // Shift + left mouse button pressed
                                // <editor-fold defaultstate="collapsed" desc="Magnification">
                                Preferences.getInstance().setSimulationBoxMagnificationPercentage(
                                        CustomPanelSlicerController.this.mousePressedMagnificationPercentage - (e.getY() - CustomPanelSlicerController.this.mousePressedY) / 4);
                                CustomPanelSlicerController.this.showMagnification();
                                // </editor-fold>
                            } else if (!e.isShiftDown() && !e.isControlDown() && !e.isAltDown() && SwingUtilities.isRightMouseButton(e)) { // Only right mouse button pressed
                                // <editor-fold defaultstate="collapsed" desc="Rotation around perpendicular axis">
                                switch (CustomPanelSlicerController.this.currentBoxView) {
                                    case XZ_FRONT:
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedYRotationAngle - (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XZ_BACK:
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedYRotationAngle + (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case YZ_LEFT:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedXRotationAngle + (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case YZ_RIGHT:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedXRotationAngle - (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XY_TOP:
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedZRotationAngle - (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XY_BOTTOM:
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    default:
                                        CustomPanelSlicerController.this.setPlaneCoordinates(e);
                                        CustomPanelSlicerController.this.showFullCoordinates();
                                        return;
                                }
                                CustomPanelSlicerController.this.showRotation();
                                // </editor-fold>
                            } else if (!e.isShiftDown() && !e.isControlDown() && !e.isAltDown() && SwingUtilities.isLeftMouseButton(e)) { // Only left mouse button pressed
                                // <editor-fold defaultstate="collapsed" desc="Rotation around display axes">
                                switch (CustomPanelSlicerController.this.currentBoxView) {
                                    case XZ_FRONT:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedXRotationAngle + (e.getY() - CustomPanelSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XZ_BACK:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedXRotationAngle - (e.getY() - CustomPanelSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case YZ_LEFT:
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedYRotationAngle + (e.getY() - CustomPanelSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case YZ_RIGHT:
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedYRotationAngle - (e.getY() - CustomPanelSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XY_TOP:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedXRotationAngle + (e.getY() - CustomPanelSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedYRotationAngle - (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XY_BOTTOM:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedXRotationAngle + (e.getY() - CustomPanelSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSlicerController.this.mousePressedYRotationAngle + (e.getX() - CustomPanelSlicerController.this.mousePressedX) / 4);
                                        break;
                                    default:
                                        CustomPanelSlicerController.this.setPlaneCoordinates(e);
                                        CustomPanelSlicerController.this.showFullCoordinates();
                                        return;
                                }
                                CustomPanelSlicerController.this.showRotation();
                                // </editor-fold>
                            } else if(!e.isShiftDown() && e.isControlDown() && !e.isAltDown() && SwingUtilities.isLeftMouseButton(e)) {
                                // <editor-fold defaultstate="collapsed" desc="Shift">
                                Preferences.getInstance().setXshiftInPixelSlicer(CustomPanelSlicerController.this.mousePressedXshift + e.getX() - CustomPanelSlicerController.this.mousePressedX);
                                Preferences.getInstance().setYshiftInPixelSlicer(-(CustomPanelSlicerController.this.mousePressedYshift + e.getY() - CustomPanelSlicerController.this.mousePressedY));
                                CustomPanelSlicerController.this.showShift();
                                // </editor-fold>
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseDragged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseMoved(MouseEvent e) {
                    try {
                        if (!CustomPanelSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSlicerController.this.setPlaneCoordinates(e);
                            CustomPanelSlicerController.this.showFullCoordinates();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseMoved()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    try {
                        if (!CustomPanelSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSlicerController.this.mousePressedX = e.getX();
                            CustomPanelSlicerController.this.mousePressedY = e.getY();

                            CustomPanelSlicerController.this.mousePressedXshift = Preferences.getInstance().getXshiftInPixelSlicer();
                            // IMPORTANT: y-direction on screen runs in opposite direction
                            CustomPanelSlicerController.this.mousePressedYshift = -Preferences.getInstance().getYshiftInPixelSlicer();
                            
                            CustomPanelSlicerController.this.mousePressedXRotationAngle = Preferences.getInstance().getRotationAroundXaxisAngle();
                            CustomPanelSlicerController.this.mousePressedYRotationAngle = Preferences.getInstance().getRotationAroundYaxisAngle();
                            CustomPanelSlicerController.this.mousePressedZRotationAngle = Preferences.getInstance().getRotationAroundZaxisAngle();

                            CustomPanelSlicerController.this.mousePressedMagnificationPercentage = Preferences.getInstance().getSimulationBoxMagnificationPercentage();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mousePressed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    try {
                        if (!CustomPanelSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSlicerController.this.setPlaneCoordinates(e);
                            CustomPanelSlicerController.this.showFullCoordinates();
                            if (Preferences.getInstance().getXshiftInPixelSlicer() != CustomPanelSlicerController.this.mousePressedXshift
                                || Preferences.getInstance().getYshiftInPixelSlicer() != CustomPanelSlicerController.this.mousePressedYshift
                                || Preferences.getInstance().getRotationAroundXaxisAngle() != CustomPanelSlicerController.this.mousePressedXRotationAngle
                                || Preferences.getInstance().getRotationAroundYaxisAngle() != CustomPanelSlicerController.this.mousePressedYRotationAngle
                                || Preferences.getInstance().getRotationAroundZaxisAngle() != CustomPanelSlicerController.this.mousePressedZRotationAngle
                                || Preferences.getInstance().getSimulationBoxMagnificationPercentage() != CustomPanelSlicerController.this.mousePressedMagnificationPercentage
                               ) 
                            {
                                CustomPanelSlicerController.this.createSlices();
                            } else {
                                // Rotation or magnification did NOT change: Repaint image again by removing box edge points
                                CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().removeBoxEdgePoints();
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseReleased()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    try {
                        if (!CustomPanelSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSlicerController.this.setPlaneCoordinates(e);
                            CustomPanelSlicerController.this.showFullCoordinates();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseEntered()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseExited(MouseEvent e) {
                    try {
                        if (!CustomPanelSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSlicerController.this.showThirdDimensionCoordinates(Preferences.getInstance().getFirstSliceIndex());
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseExited()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseClicked(MouseEvent e) {
                    // System.out.println(String.valueOf(debugCounter++) + " mouseClicked");
                    try {
                        if (!CustomPanelSlicerController.this.isAnimationPlaying()) {
                            if (e.isControlDown() && e.isAltDown() && !e.isShiftDown() && !CustomPanelSlicerController.this.isMeasurement()) {
                                // System.out.println(String.valueOf(debugCounter++) + " control/alt\n");
                                if (SwingUtilities.isLeftMouseButton(e)) {
                                    CustomPanelSlicerController.this.selectAllMolecules();
                                }
                                if (SwingUtilities.isRightMouseButton(e)) {
                                    CustomPanelSlicerController.this.deselectAllMolecules();
                                }
                            } else if (e.isAltDown() && !e.isShiftDown() && !e.isControlDown() && !CustomPanelSlicerController.this.isMeasurement()) {
                                // System.out.println(String.valueOf(debugCounter++) + " alt\n");
                                if (SwingUtilities.isLeftMouseButton(e)) {
                                    CustomPanelSlicerController.this.selectMolecule();
                                }
                                if (SwingUtilities.isRightMouseButton(e)) {
                                    CustomPanelSlicerController.this.deselectMolecule();
                                }
                            } else if (e.isControlDown() && e.isShiftDown() && !e.isAltDown()) {
                                // System.out.println(String.valueOf(debugCounter++) + " control/shift\n");
                                CustomPanelSlicerController.this.setPlaneCoordinates(e);
                                CustomPanelSlicerController.this.showFullCoordinates();
                                if (SwingUtilities.isRightMouseButton(e)) {
                                    CustomPanelSlicerController.this.removeMeasurementAndSetZoomPanel();
                                } if (SwingUtilities.isLeftMouseButton(e)) {
                                    CustomPanelSlicerController.this.measureDistance(e);
                                }
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseClicked()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }
            });
            this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().addMouseWheelListener(new MouseWheelListener() {

                public void mouseWheelMoved(MouseWheelEvent e) {
                    try {
                        if (!CustomPanelSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSlicerController.this.setThirdDimensionIncrement(e.getWheelRotation());
                            CustomPanelSlicerController.this.showFullCoordinates();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseWheelMoved()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getSaveGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        String tmpFilePathname = GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.selectGraphicsFileSaveChooser"), GuiDefinitions.IMAGE_BMP_FILE_EXTENSION);
                        if (tmpFilePathname != null) {
                            try {
                                GraphicsUtils.writeImageToFile(CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getImage(),
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
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getCopyGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        if (!ImageSelection.copyImageToClipboard(CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getImage())) {
                            // <editor-fold defaultstate="collapsed" desc="Show error message">
                            JOptionPane.showMessageDialog(null, GuiMessage.get("Error.CopyOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                    JOptionPane.ERROR_MESSAGE);

                            // </editor-fold>
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getRedrawButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.createSlices();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getCreateMovieButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.createMovie();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getEditMoveAndSpinSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.editMoveAndSpinSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getBoxWaitButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.startBoxWaitDialog();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getBoxMoveButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.startBoxMoveDialog();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getFlyButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.startFlyDialog();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getSpinHorizontalButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.startSpinAroundHorizontalAxisDialog();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getSpinVerticalButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.startSpinAroundVerticalAxisDialog();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSimulationBoxPanel().getPlayAnimationButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelSlicerController.this.playAnimation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listeners for box view panel">
            this.slicerPanel.getXzFrontRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getXzFrontRadioButton().isSelected()) {
                            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                            CustomPanelSlicerController.this.stopAnimation();
                            // </editor-fold>
                            Preferences.getInstance().setBoxViewDisplay(SimulationBoxViewEnum.XZ_FRONT);
                            CustomPanelSlicerController.this.createSlices();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getXzBackRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getXzBackRadioButton().isSelected()) {
                            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                            CustomPanelSlicerController.this.stopAnimation();
                            // </editor-fold>
                            Preferences.getInstance().setBoxViewDisplay(SimulationBoxViewEnum.XZ_BACK);
                            CustomPanelSlicerController.this.createSlices();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getYzLeftRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getYzLeftRadioButton().isSelected()) {
                            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                            CustomPanelSlicerController.this.stopAnimation();
                            // </editor-fold>
                            Preferences.getInstance().setBoxViewDisplay(SimulationBoxViewEnum.YZ_LEFT);
                            CustomPanelSlicerController.this.createSlices();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getYzRightRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getYzRightRadioButton().isSelected()) {
                            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                            CustomPanelSlicerController.this.stopAnimation();
                            // </editor-fold>
                            Preferences.getInstance().setBoxViewDisplay(SimulationBoxViewEnum.YZ_RIGHT);
                            CustomPanelSlicerController.this.createSlices();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getXyTopRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getXyTopRadioButton().isSelected()) {
                            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                            CustomPanelSlicerController.this.stopAnimation();
                            // </editor-fold>
                            Preferences.getInstance().setBoxViewDisplay(SimulationBoxViewEnum.XY_TOP);
                            CustomPanelSlicerController.this.createSlices();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getXyBottomRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getXyBottomRadioButton().isSelected()) {
                            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                            CustomPanelSlicerController.this.stopAnimation();
                            // </editor-fold>
                            Preferences.getInstance().setBoxViewDisplay(SimulationBoxViewEnum.XY_BOTTOM);
                            CustomPanelSlicerController.this.createSlices();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listener particle shift remove button">
            this.slicerPanel.getParticleShiftRemoveButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.removeParticleShift();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(
                            null, 
                            String.format(
                                GuiMessage.get("Error.CommandExecutionFailed"), 
                                "actionPerformed()", 
                                "CustomPanelSlicerController"
                            ),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listeners for quick settings panel">
            this.slicerPanel.getSetFirstSliceButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setFirstSlice();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getNoRotationAndShiftButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setDefaultRotationAndShiftQuick();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getFitMagnificationButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.fitSimulationBoxMagnificationPercentage();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getNoMagnificationButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setDefaultMagnification();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getOriginalDisplayButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setOriginalDisplayOfSimulationBox();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getBoxFrameButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        if (Preferences.getInstance().isFrameDisplaySlicer()) {
                            CustomPanelSlicerController.this.removeSimulationBoxFrame();
                        } else {
                            CustomPanelSlicerController.this.setSimulationBoxFrame();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listeners for fog settings panel">
            this.slicerPanel.getFog0Button().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setFogOfSimulationBox(0.0);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getFog1Button().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setFogOfSimulationBox(1.0);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getFog2Button().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setFogOfSimulationBox(2.0);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getFog3Button().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setFogOfSimulationBox(3.0);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getFog4Button().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setFogOfSimulationBox(4.0);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getFog5Button().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setFogOfSimulationBox(5.0);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listeners for zoom panel">
            this.slicerPanel.getSetZoomButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.setZoomPoints();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getZoomInBoxVolumeShapeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.zoomIn(true);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getZoomInEllipsoidVolumeShapeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.zoomIn(false);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getZoomOutButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.zoomOut();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getZoomFrequencyDistributionsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.showZoomStatistics();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getVolumeBinsSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSlicerController.this.stopAnimation();
                        CustomPanelSlicerController.this.editVolumeSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>            
            // <editor-fold defaultstate="collapsed" desc="- Listeners for settings panels">
            this.slicerPanel.getRotationAndShiftRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getRotationAndShiftRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.defaultRotationAndShiftButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.defaultRotationAndShiftButton.text"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.configureRotationAndShiftButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.configureRotationAndShiftButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getMoleculesRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getMoleculesRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.defaultMoleculesButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.defaultMoleculesButton.text"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.configureMoleculesButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.configureMoleculesButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getGraphicsRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getGraphicsRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.defaultGraphicsButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.defaultGraphicsButton.text"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.configureGraphicsButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.configureGraphicsButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getAnimationRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.defaultAnimationButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.defaultAnimationButton.text"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.configureAnimationButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.configureAnimationButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getMovieRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.makeMovieButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.makeMovieButton.text"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.configureMovieButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.configureMovieButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSelectionRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelSlicerController.this.slicerPanel.getSelectionRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setVisible(CustomPanelSlicerController.this.graphicalParticlePositionInfo.isMoleculeSelected());
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.clearMoleculeSelectionButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.clearMoleculeSelectionButton.text"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.configureMoleculeSelectionButton.toolTipText"));
                            CustomPanelSlicerController.this.slicerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelSlicer.configureMoleculeSelectionButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getFirstSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelSlicerController.this.stopAnimation();

                        // </editor-fold>
                        if (CustomPanelSlicerController.this.slicerPanel.getRotationAndShiftRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.setDefaultRotationAndShiftSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getMoleculesRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.setDefaultMoleculeSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getGraphicsRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.setDefaultGraphicsSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.setDefaultAnimationSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.startMovieCreation();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getSelectionRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.clearMoleculeSelection();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.slicerPanel.getSecondSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelSlicerController.this.stopAnimation();

                        // </editor-fold>
                        if (CustomPanelSlicerController.this.slicerPanel.getRotationAndShiftRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.configureRotationAndShiftSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getMoleculesRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.configureMoleculeSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getGraphicsRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.configureGraphicsSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.configureAnimationSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.configureMovieSettings();
                        }
                        if (CustomPanelSlicerController.this.slicerPanel.getSelectionRadioButton().isSelected()) {
                            CustomPanelSlicerController.this.configureMoleculeSelections();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listener for info label">
            this.slicerPanel.getSimulationBoxPanel().getInfoLabel().addMouseListener(new MouseAdapter() {
                
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        CustomPanelSlicerController.this.showMouseManipulationInfo();
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add this instance as change listener to third dimension slider">
            // Add this as change listener to third dimension slider
            this.slicerPanel.getSimulationBoxPanel().getThirdDimensionSlider().addChangeListener(this);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Select rotation radio button">
            this.slicerPanel.getRotationAndShiftRadioButton().setSelected(true);
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelSlicerController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
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
        if (this.slicerPanel.getSimulationBoxPanel().getRedrawButton().isVisible() != aValue) {
            this.slicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(aValue);
            this.showMeasurement();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- getImage">
    /**
     * Returns slice image
     *
     * @param anIndex Index
     * @return Slice image or null if image is not available
     */
    public BufferedImage getImage(int anIndex) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anIndex < 0 || anIndex > Preferences.getInstance().getNumberOfSlicesPerView() - 1) {
            return null;
        }
        // </editor-fold>
        try {
            return this.simulationBoxViewSlicer.getSliceImage(anIndex);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "getImage()", "CustomPanelSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- createSlices">
    /**
     * Creates slices
     */
    public void createSlices() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set wait cursor">
            MouseCursorManagement.getInstance().setWaitCursor();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Remove measurement">
            CustomPanelSlicerController.this.removeMeasurement();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
            this.stopAnimation();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set slicer start settings and third dimension slider">
            this.setSlicerStartSettings();
            this.setThirdDimensionSlider();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Remove measurement points">
            // Do NOT call removeMeasurement() method since this method initiates show() methods
            this.firstPointForMeasurement = null;
            this.firstSliceIndexForMeasurement = -1;
            this.secondPointForMeasurement = null;
            this.secondSliceIndexForMeasurement = -1;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create slices">
            // Create new SimulationBoxViewSlicer instances with possible exclusions and modified colors
            String tmpDestinationPath = this.fileUtilityMethods.getUniqueTemporaryDirectoryPath();
            // Set new colors and scaled radii to graphical particles
            this.graphicalParticlePositionInfo.getGraphicalParticleInfo().setCurrentColorAndScaledRadiusOfParticles();
            // Set current graphical particle positions according to settings (colors, exclusions, rotations etc.)
            // Parameter true: Zoom statistics are calculated if possible
            this.graphicalParticlePositionInfo.setCurrentGraphicalParticlePositions(true);
            // Get new enlarged box size info AFTER setting current graphical particle positions
            // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
            this.enlargedBoxSizeInfo = this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
            // Set and show third dimension coordinates
            this.setThirdDimension();
            // Set slicer image panel ratio of height to width (may have changed due to changed box size info)
            this.setSlicerImagePanelRatioOfHeightToWidth();
            // Release memory of old slicers
            this.releaseMemoryOfSlicer();
            this.currentBoxView = Preferences.getInstance().getBoxViewDisplay();
            this.setSimulationBoxViewImage();
            // Do NOT clone this.graphicalParticlePositionInfo.getCurrentGraphicalParticlePositionArrayList()
            this.simulationBoxViewSlicer = 
                new SimulationBoxViewSlicer(
                    this.currentBoxView, 
                    tmpDestinationPath, 
                    this.enlargedBoxSizeInfo,
                    this.graphicalParticlePositionInfo.getCurrentGraphicalParticlePositionArrayList(), 
                    this.imageFileType, 
                    this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getWidth(),
                    this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getHeight()
                );
            if (!this.simulationBoxViewSlicer.createSlices()) {
                // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                JOptionPane.showMessageDialog(null, 
                    String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Method this.simulationBoxViewSlicer.createSlices() failed", "CustomPanelSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), 
                    JOptionPane.ERROR_MESSAGE
                );
                // </editor-fold>
            } else {
                this.updateDisplay();
            }
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Set default cursor">
            MouseCursorManagement.getInstance().setDefaultCursor();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "startSlicer()", "CustomPanelSlicerController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            // <editor-fold defaultstate="collapsed" desc="Set default cursor">
            MouseCursorManagement.getInstance().setDefaultCursor();
            // </editor-fold>
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- kill">
    /**
     * Kills all ongoing operations of controller
     */
    public void kill() {
        this.stopAnimation();
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
    @Override
    public ImageFileType getImageFileType() {
        return this.imageFileType;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- NumberOfImages">
    /**
     * Returns number of images
     *
     * @return Number of images
     */
    @Override
    public int getNumberOfImages() {
        return Preferences.getInstance().getNumberOfSlicesPerView();
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public events">
    // <editor-fold defaultstate="collapsed" desc="- ChangeListener stateChanged">
    /**
     * ChangeListener state changed for virtual slider
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
            Preferences.getInstance().setFirstSliceIndex(this.virtualSlider.getValue() / this.virtualSlider.getMinorTickSpacing());
            this.setThirdDimension();
            this.setSliceImage(Preferences.getInstance().getFirstSliceIndex());
            this.setFirstSliceButtonAndOriginalButtonVisibility();
            this.showThirdDimensionCoordinates(Preferences.getInstance().getFirstSliceIndex());
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "CustomPanelSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Show methods">
    /**
     * Shows no coordinates
     * 
     * @param aSliceIndex Slice index
     */
    private void showThirdDimensionCoordinates(int aSliceIndex) {
        String tmpThirdDimensionString = "";
        switch (this.currentBoxView) {
            case XZ_FRONT:
            case XZ_BACK:
                tmpThirdDimensionString =
                    String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionY"),
                        this.stringUtilityMethods.formatDoubleValue(this.currentY * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                        )
                    );
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                tmpThirdDimensionString = 
                    String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionX"),
                        this.stringUtilityMethods.formatDoubleValue(this.currentX * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                        )
                    );
                break;
            case XY_TOP:
            case XY_BOTTOM:
                tmpThirdDimensionString = 
                    String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionZ"),
                        this.stringUtilityMethods.formatDoubleValue(this.currentZ * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                        )
                    );
                break;
        }
        this.slicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionCoordinateSlicerPlusImageSize"), this.currentBoxView.toRepresentation(), // current_box_view
                String.valueOf(aSliceIndex), // slice_index
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getWidth(), // image_width
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getHeight(), // image_height
                tmpThirdDimensionString // coordinate
            )
        );
    }

    /**
     * Shows full coordinates
     */
    private void showFullCoordinates() {
        this.slicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(
            String.format(
                GuiMessage.get("CompartmentGraphics.FullCoordinatesSlicerPlusImageSize"), 
                this.currentBoxView.toRepresentation(),
                String.valueOf(Preferences.getInstance().getFirstSliceIndex()),
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getWidth(),
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getHeight(),
                this.stringUtilityMethods.formatDoubleValue(
                    this.currentX * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                ), // x-coordinate
                this.stringUtilityMethods.formatDoubleValue(
                    this.currentY * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                ), // y-coordinate
                this.stringUtilityMethods.formatDoubleValue(
                    this.currentZ * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                ) // z-coordinate
            )
        );
    }

    /**
     * Shows rotation
     */
    private void showRotation() {
        this.slicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("SimulationBoxSlicer.Rotation"),
                    String.valueOf(Preferences.getInstance().getRotationAroundXaxisAngle()),
                    String.valueOf(Preferences.getInstance().getRotationAroundYaxisAngle()),
                    String.valueOf(Preferences.getInstance().getRotationAroundZaxisAngle())
                )
            );

        PointInSpace[] tmpBoxEdgePoints3D = this.enlargedBoxSizeInfo.getBoxEdgePointArray();
        this.graphicsUtilityMethods.rotatePoints(tmpBoxEdgePoints3D,
                Preferences.getInstance().getRotationAroundXaxisAngle(),
                Preferences.getInstance().getRotationAroundYaxisAngle(),
                Preferences.getInstance().getRotationAroundZaxisAngle(),
                this.enlargedBoxSizeInfo.getBoxMidPoint()
        );
        // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
        BoxSizeInfo tmpMagnifiedBoxSizeInfo = this.enlargedBoxSizeInfo.getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
        PointInPlane[] tmpBoxEdgePoints2D = 
            this.graphicsUtilityMethods.getMapped2dPointsForGraphicsPanel(
                tmpBoxEdgePoints3D,
                tmpMagnifiedBoxSizeInfo,
                this.currentBoxView,
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize()
            );
        this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBoxEdgePoints(tmpBoxEdgePoints2D);
    }
    
    /**
     * Shows shift
     */
    private void showShift() {
        this.slicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("SimulationBoxSlicer.Shift"),
                String.valueOf(Preferences.getInstance().getXshiftInPixelSlicer()),
                String.valueOf(Preferences.getInstance().getYshiftInPixelSlicer())
            )
        );
        
        PointInSpace[] tmpBoxEdgePoints3D = this.enlargedBoxSizeInfo.getBoxEdgePointArray();
        this.graphicsUtilityMethods.rotatePoints(tmpBoxEdgePoints3D,
                Preferences.getInstance().getRotationAroundXaxisAngle(),
                Preferences.getInstance().getRotationAroundYaxisAngle(),
                Preferences.getInstance().getRotationAroundZaxisAngle(),
                this.enlargedBoxSizeInfo.getBoxMidPoint()
        );
        // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
        BoxSizeInfo tmpMagnifiedBoxSizeInfo = this.enlargedBoxSizeInfo.getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
        // NOTE: Preferences.getInstance().getXshiftInPixelSlicer() and 
        // Preferences.getInstance().getYshiftInPixelSlicer() are taken in 
        // TargetCoordinatesAndSize into account:
        // this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize()
        PointInPlane[] tmpBoxEdgePoints2D = 
            this.graphicsUtilityMethods.getMapped2dPointsForGraphicsPanel(
                tmpBoxEdgePoints3D,
                tmpMagnifiedBoxSizeInfo,
                this.currentBoxView,
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize()
            );
        this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBoxEdgePoints(tmpBoxEdgePoints2D);
    }

    /**
     * Shows simulation box magnification
     */
    private void showMagnification() {
        this.slicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("CompartmentGraphics.Magnification"),
                        String.valueOf(Preferences.getInstance().getSimulationBoxMagnificationPercentage())));

        PointInSpace[] tmpBoxEdgePoints3D = this.enlargedBoxSizeInfo.getBoxEdgePointArray();
        this.graphicsUtilityMethods.rotatePoints(tmpBoxEdgePoints3D,
                Preferences.getInstance().getRotationAroundXaxisAngle(),
                Preferences.getInstance().getRotationAroundYaxisAngle(),
                Preferences.getInstance().getRotationAroundZaxisAngle(),
                this.enlargedBoxSizeInfo.getBoxMidPoint());

        // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
        BoxSizeInfo tmpMagnifiedBoxSizeInfo = this.enlargedBoxSizeInfo.getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
        PointInPlane[] tmpBoxEdgePoints2D = this.graphicsUtilityMethods.getMapped2dPointsForGraphicsPanel(
            tmpBoxEdgePoints3D,
            tmpMagnifiedBoxSizeInfo,
            this.currentBoxView,
            this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize()
        );
        this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBoxEdgePoints(tmpBoxEdgePoints2D);
    }

    /**
     * Shows measurement
     */
    private void showMeasurement() {
        if (this.slicerPanel.getSimulationBoxPanel().getRedrawButton().isVisible()) {
            this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.RedrawInfo"));
        } else {
            if (this.firstPointForMeasurement == null) {
                // <editor-fold defaultstate="collapsed" desc="No measurement">
                if (this.isMoleculeSelection) {
                    this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.MouseManipulationInfoWithSelection"));
                } else {
                    this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.MouseManipulationInfoWithoutSelection"));
                }
                // </editor-fold>
            } else if (this.secondPointForMeasurement == null) {
                // <editor-fold defaultstate="collapsed" desc="First point only">
                this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.FirstPointForMeasurement"));
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Measurement">
                this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(String.format(GuiMessage.get("SimulationBoxSlicer.DistanceMeasurement"),
                        this.stringUtilityMethods.formatDoubleValue(this.graphicsUtilityMethods.getConvertedDistanceInSpace(
                                this.firstPointForMeasurement, 
                                this.secondPointForMeasurement,
                                this.graphicalParticlePositionInfo.getLengthConversionFactor()
                            ), 
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                        )
                    )
                );
                // </editor-fold>
            }
        }
    }
    
    /**
     * Shows mouse manipulation info
     */
    private void showMouseManipulationInfo() {
        String tmpMessage = null;
        if (this.isMoleculeSelection) {
            tmpMessage = GuiMessage.get("MouseManipulationInfoWithSelection.Message");
        } else {
            tmpMessage = GuiMessage.get("MouseManipulationInfoWithoutSelection.Message");
        }
        JOptionPane.showMessageDialog(
            null, 
            tmpMessage, 
            GuiMessage.get("MouseManipulationInfo.Title"),
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Shows selected molecule info
     * 
     * @param aParticleIndex Particle index
     */
    private void showSelectedMoleculeInfo(int aParticleIndex) {
        String tmpSelectedMoleculeName = this.graphicalParticlePositionInfo.getSelectedMoleculeName(aParticleIndex);
        int tmpSelectedMoleculeIndex = this.graphicalParticlePositionInfo.getSelectedMoleculeIndex(aParticleIndex);
        if (tmpSelectedMoleculeName != null && tmpSelectedMoleculeIndex >= 0) {
            this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(String.format(GuiMessage.get("SimulationBoxSlicer.SelectedMoleculeInfoFormat"),
                    tmpSelectedMoleculeName,
                    tmpSelectedMoleculeIndex
                )
            );
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Set methods">
    /**
     * Set box view
     *
     * @param aBoxView Box view
     */
    private void setBoxView(SimulationBoxViewEnum aBoxView) {
        // <editor-fold defaultstate="collapsed" desc="Remove measurement">
        this.removeMeasurement();
        // </editor-fold>
        this.currentBoxView = aBoxView;
        this.setSlicerImagePanelRatioOfHeightToWidth();
        // Set third dimension slider to 0
        this.virtualSlider.setValue(0);
        this.setThirdDimension();
        this.showThirdDimensionCoordinates(Preferences.getInstance().getFirstSliceIndex());
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
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        / this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength());
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        / this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength());
                break;
            case XY_TOP:
            case XY_BOTTOM:
                this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength()
                        / this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength());
                break;
        }
    }

    /**
     * Sets appropriate plane coordinates
     *
     * @param aMouseEvent Mouse event
     */
    private void setPlaneCoordinates(MouseEvent aMouseEvent) {
        double tmpCorrectedMouseX;
        double tmpCorrectedMouseY;
        if (this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getWidth() > this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth()) {
            tmpCorrectedMouseX = 
                (double) (aMouseEvent.getX() - this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getXcoordinateWithXshift(Preferences.getInstance().getXshiftInPixelSlicer()));
            tmpCorrectedMouseY = (double) aMouseEvent.getY() + Preferences.getInstance().getYshiftInPixelSlicer();
        } else if (this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getHeight() > this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight()) {
            tmpCorrectedMouseX = (double) aMouseEvent.getX() + Preferences.getInstance().getXshiftInPixelSlicer();
            tmpCorrectedMouseY = 
                (double) (aMouseEvent.getY() - this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getYcoordinateWithYshift(Preferences.getInstance().getYshiftInPixelSlicer()));
        } else {
            tmpCorrectedMouseX = (double) aMouseEvent.getX() + Preferences.getInstance().getXshiftInPixelSlicer();;
            tmpCorrectedMouseY = (double) aMouseEvent.getY() + Preferences.getInstance().getYshiftInPixelSlicer();;
        }
        switch (this.currentBoxView) {
            case XZ_FRONT:
                this.currentX = this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength() * tmpCorrectedMouseX
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth()
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameXMin();
                this.currentZ = this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        * ((double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() - tmpCorrectedMouseY)
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() + this.enlargedBoxSizeInfo.getRotationDisplayFrameZMin();
                break;
            case XZ_BACK:
                this.currentX = this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength()
                        * (1.0 - tmpCorrectedMouseX / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth())
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameXMin();
                this.currentZ = this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        * ((double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() - tmpCorrectedMouseY)
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() + this.enlargedBoxSizeInfo.getRotationDisplayFrameZMin();
                break;
            case YZ_LEFT:
                this.currentY = this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength()
                        * (1.0 - tmpCorrectedMouseX / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth())
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameYMin();
                this.currentZ = this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        * ((double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() - tmpCorrectedMouseY)
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() + this.enlargedBoxSizeInfo.getRotationDisplayFrameZMin();
                break;
            case YZ_RIGHT:
                this.currentY = this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength() * tmpCorrectedMouseX
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth()
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameYMin();
                this.currentZ = this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        * ((double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() - tmpCorrectedMouseY)
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() + this.enlargedBoxSizeInfo.getRotationDisplayFrameZMin();
                break;
            case XY_TOP:
                this.currentX = this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength() * tmpCorrectedMouseX
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth()
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameXMin();
                this.currentY = this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength()
                        * (1.0 - tmpCorrectedMouseY / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth())
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameYMin();
                break;
            case XY_BOTTOM:
                this.currentX = this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength() * tmpCorrectedMouseX
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth()
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameXMin();
                this.currentY = this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength() * tmpCorrectedMouseY
                        / (double) this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth()
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameYMin();
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
        // NOTE: Do not use tmpCurrentSliderValue since this.virtualSlider may have changed this value
        Preferences.getInstance().setFirstSliceIndex(this.virtualSlider.getValue() / this.virtualSlider.getMinorTickSpacing());
        this.setThirdDimension();
        this.setSliceImage(Preferences.getInstance().getFirstSliceIndex());
        this.setFirstSliceButtonAndOriginalButtonVisibility();
    }

    /**
     * Sets third dimension
     */
    private void setThirdDimension() {
        int tmpCurrentSliceIndex = this.virtualSlider.getValue() / this.virtualSlider.getMinorTickSpacing();
        switch (this.currentBoxView) {
            case XZ_FRONT:
            case XZ_BACK:
                if (this.simulationBoxViewSlicer != null) {
                    double tmpCurrentY = this.simulationBoxViewSlicer.getSliceEndValue(tmpCurrentSliceIndex);
                    if (!Double.isNaN(tmpCurrentY)) {
                        this.currentY = tmpCurrentY;
                    }
                }
                break;
            case XY_BOTTOM:
            case XY_TOP:
                if (this.simulationBoxViewSlicer != null) {
                    double tmpCurrentZ = this.simulationBoxViewSlicer.getSliceEndValue(tmpCurrentSliceIndex);
                    if (!Double.isNaN(tmpCurrentZ)) {
                        this.currentZ = tmpCurrentZ;
                    }
                }
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                if (this.simulationBoxViewSlicer != null) {
                    double tmpCurrentX = this.simulationBoxViewSlicer.getSliceEndValue(tmpCurrentSliceIndex);
                    if (!Double.isNaN(tmpCurrentX)) {
                        this.currentX = tmpCurrentX;
                    }
                }
                break;
        }
    }

    /**
     * Sets settings at slicer start
     */
    private void setSlicerStartSettings() {
        // Set slice image creation information
        this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.CreatingSliceImages"));
        // Disable 3rd dimension scrolling
        this.isThirdDimensionScroll = false;
    }

    /**
     * Sets slice image with specified index
     *
     * @param aSliceIndex Slice index
     * @return True: Slice image could be set, false: Slice image was not
     * available
     */
    private boolean setSliceImage(int aSliceIndex) {
        BufferedImage tmpImage = this.simulationBoxViewSlicer.getSliceImage(aSliceIndex);
        if (tmpImage == null) {
            return false;
        } else {
            this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBasicImage(tmpImage);
            return true;
        }
    }

    /**
     * Sets third dimension slider
     */
    private void setThirdDimensionSlider() {
        this.virtualSlider.setSliderParameters(Preferences.getInstance().getNumberOfSlicesPerView());
        this.virtualSlider.setValue(0);
    }

    /**
     * Set box settings panel
     */
    private void setBoxSettingsPanel() {
        // <editor-fold defaultstate="collapsed" desc="First slice button and original display button">
        this.setFirstSliceButtonAndOriginalButtonVisibility();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="No rotation button">
        this.slicerPanel.getNoRotationAndShiftButton().setVisible(!this.isDefaultRotationAndShift());
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="No magnification button">
        this.slicerPanel.getNoMagnificationButton().setVisible(Preferences.getInstance().getSimulationBoxMagnificationPercentage() != Preferences.getInstance().getDefaultSimulationBoxMagnificationPercentage());
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Fit magnification button">
        this.slicerPanel.getFitMagnificationButton().setVisible(
            Preferences.getInstance().getSimulationBoxMagnificationPercentage() != this.getMaximumSimulationBoxMagnificationPercentage()
        );
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Box frame button">
        if (Preferences.getInstance().isFrameDisplaySlicer()) {
            this.slicerPanel.getBoxFrameButton().setText(GuiMessage.get("CustomPanelSlicer.boxFrameButton.strip.text"));
            this.slicerPanel.getBoxFrameButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.boxFrameButton.strip.toolTipText"));
        } else {
            this.slicerPanel.getBoxFrameButton().setText(GuiMessage.get("CustomPanelSlicer.boxFrameButton.text"));
            this.slicerPanel.getBoxFrameButton().setToolTipText(GuiMessage.get("CustomPanelSlicer.boxFrameButton.toolTipText"));
        }
        // </editor-fold>
    }

    /**
     * Sets original button visibility
     */
    private void setOriginalButtonVisibility() {
        boolean tmpIsOriginalDisplayButtonVisible = false;
        if (Preferences.getInstance().getFirstSliceIndex() != Preferences.getInstance().getDefaultFirstSliceIndex()) {
            tmpIsOriginalDisplayButtonVisible = true;
        }
        if (!this.isDefaultRotationAndShift()) {
            tmpIsOriginalDisplayButtonVisible = true;
        }
        if (Preferences.getInstance().getSimulationBoxMagnificationPercentage() != this.getMaximumSimulationBoxMagnificationPercentage()) {
            tmpIsOriginalDisplayButtonVisible = true;
        }
        this.slicerPanel.getOriginalDisplayButton().setVisible(tmpIsOriginalDisplayButtonVisible);
    }
    
    /**
     * Sets particle shift remove button visibility
     */
    private void setParticleShiftRemoveButtonVisibility() {
        this.slicerPanel.getParticleShiftRemoveButton().setVisible(this.isParticleShiftDefined());
    }
    
    /**
     * Sets first slice button visibility
     */
    private void setFirstSliceButtonVisibility() {
        this.slicerPanel.getSetFirstSliceButton().setVisible(Preferences.getInstance().getFirstSliceIndex() != Preferences.getInstance().getDefaultFirstSliceIndex());
    }

    /**
     * Sets first slice button and original button visibility
     */
    private void setFirstSliceButtonAndOriginalButtonVisibility() {
        this.setFirstSliceButtonVisibility();
        this.setOriginalButtonVisibility();
    }
    
    /**
     * Set fog settings panel
     */
    private void setFogSettingsPanel() {
        // <editor-fold defaultstate="collapsed" desc="Fog buttons">        
        this.slicerPanel.getFog0Button().setVisible(Preferences.getInstance().getDepthAttenuationSlicer() != 0.0);
        this.slicerPanel.getFog1Button().setVisible(Preferences.getInstance().getDepthAttenuationSlicer() != 1.0);
        this.slicerPanel.getFog2Button().setVisible(Preferences.getInstance().getDepthAttenuationSlicer() != 2.0);
        this.slicerPanel.getFog3Button().setVisible(Preferences.getInstance().getDepthAttenuationSlicer() != 3.0);
        this.slicerPanel.getFog4Button().setVisible(Preferences.getInstance().getDepthAttenuationSlicer() != 4.0);
        this.slicerPanel.getFog5Button().setVisible(Preferences.getInstance().getDepthAttenuationSlicer() != 5.0);
        // </editor-fold>
    }

    /**
     * Set zoom panel
     */
    private void setZoomPanel() {
        this.slicerPanel.getZoomInBoxVolumeShapeButton().setVisible(this.isZoomInPossibleWithoutMeasurement() || this.isZoomInPossible());
        this.slicerPanel.getZoomInEllipsoidVolumeShapeButton().setVisible(this.isZoomInPossibleWithoutMeasurement() || this.isZoomInPossible());
        this.slicerPanel.getZoomOutButton().setVisible(this.graphicalParticlePositionInfo.hasExclusionBoxSizeInfo());
        this.slicerPanel.getVolumeBinsSettingsButton().setVisible(this.graphicalParticlePositionInfo.hasZoomStatisticsValueItemContainer());
        this.slicerPanel.getZoomFrequencyDistributionsButton().setVisible(this.graphicalParticlePositionInfo.hasZoomStatisticsValueItemContainer());
    }
    
    /**
     * Sets original display of simulation box
     */
    private void setOriginalDisplayOfSimulationBox() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();
        // </editor-fold>
        if (!this.isDefaultRotationAndShift()) {
            this.setDefaultRotationAndShiftQuick();
        }
        int tmpMaximumSimulationBoxMagnificationPercentage = this.getMaximumSimulationBoxMagnificationPercentage();
        if (tmpMaximumSimulationBoxMagnificationPercentage != Preferences.getInstance().getSimulationBoxMagnificationPercentage()) {
            Preferences.getInstance().setSimulationBoxMagnificationPercentage(tmpMaximumSimulationBoxMagnificationPercentage);
        }
        // Set first slice (index 0)
        Preferences.getInstance().setFirstSliceIndex(Preferences.getInstance().getDefaultFirstSliceIndex());
        this.createSlices();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Configure/default graphics settings">
    /**
     * Configures slicer graphics settings
     */
    private void configureGraphicsSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();

        // </editor-fold>
        ValueItemContainer tmpSlicerSettingsValueItemContainer = Preferences.getInstance().getSlicerEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesSlicerGraphicsSettingsDialog.title"), tmpSlicerSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpSlicerSettingsValueItemContainer);
            this.startSlicerWithDefinedBoxView();
        }
    }

    /**
     * Sets default slicer graphics settings
     */
    private void setDefaultGraphicsSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();

        // </editor-fold>
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setSimulationBoxBackgroundColorSlicer(Preferences.getInstance().getDefaultSimulationBoxBackgroundColorSlicer().toString());
            Preferences.getInstance().setImageStorageMode(Preferences.getInstance().getDefaultImageStorageMode());
            Preferences.getInstance().setNumberOfSlicesPerView(Preferences.getInstance().getDefaultNumberOfSlicesPerView());
            Preferences.getInstance().setSingleSliceDisplay(Preferences.getInstance().getDefaultSingleSliceDisplay());
            Preferences.getInstance().setSimulationBoxMagnificationPercentage(Preferences.getInstance().getDefaultSimulationBoxMagnificationPercentage());
            Preferences.getInstance().setSpecularWhiteAttenuationSlicer(Preferences.getInstance().getDefaultSpecularWhiteAttenuationSlicer());
            Preferences.getInstance().setDepthAttenuationSlicer(Preferences.getInstance().getDefaultDepthAttenuationSlicer());
            Preferences.getInstance().setColorGradientAttenuationSlicer(Preferences.getInstance().getDefaultColorGradientAttenuationSlicer());
            Preferences.getInstance().setBoxViewDisplay(Preferences.getInstance().getDefaultBoxViewDisplay());
            Preferences.getInstance().setRadialGradientPaintFocusFactorX(Preferences.getInstance().getDefaultRadialGradientPaintFocusFactorX());
            Preferences.getInstance().setRadialGradientPaintFocusFactorY(Preferences.getInstance().getDefaultRadialGradientPaintFocusFactorY());
            this.startSlicerWithDefinedBoxView();
        }
    }

    /**
     * Sets default magnification
     */
    private void setDefaultMagnification() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();

        // </editor-fold>
        if (Preferences.getInstance().getSimulationBoxMagnificationPercentage() != Preferences.getInstance().getDefaultSimulationBoxMagnificationPercentage()) {
            Preferences.getInstance().setSimulationBoxMagnificationPercentage(Preferences.getInstance().getDefaultSimulationBoxMagnificationPercentage());
            this.startSlicerWithDefinedBoxView();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Configure/default molecule settings">
    /**
     * Configures bulk/molecule settings
     */
    private void configureMoleculeSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        try {
            // First check Preferences if value item container for molecule/particles display settings is already defined
            ValueItemContainer tmpMoleculeDisplaySettingsValueItemContainer = null;
            if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasMoleculeDisplaySettingsValueItemContainer()) {
                tmpMoleculeDisplaySettingsValueItemContainer = Preferences.getInstance().getSimulationMovieSlicerConfiguration().getMoleculeDisplaySettingsValueItemContainer();;
            } else {
                tmpMoleculeDisplaySettingsValueItemContainer = this.graphicalParticlePositionInfo.getGraphicalParticleInfo().getMoleculeDisplaySettingsValueItemContainer();
            }
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesSlicerMoleculesSettingsDialog.title"), tmpMoleculeDisplaySettingsValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                this.graphicalParticlePositionInfo.getGraphicalParticleInfo().setExclusionsColorsAndPreferences(tmpMoleculeDisplaySettingsValueItemContainer);
                // Set new value item container for molecule/particles display settings to Preferences
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().setMoleculeDisplaySettingsValueItemContainer(tmpMoleculeDisplaySettingsValueItemContainer);
                MouseCursorManagement.getInstance().setDefaultCursor();
                this.createSlices();
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Sets default molecule settings
     */
    private void setDefaultMoleculeSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setParticleColorDisplayMode(Preferences.getInstance().getDefaultParticleColorDisplayMode());
            this.graphicalParticlePositionInfo.getGraphicalParticleInfo().clear();
            // Clear value item container for molecule/particles display settings
            Preferences.getInstance().getSimulationMovieSlicerConfiguration().clearMoleculeDisplaySettingsValueItemContainer();
            this.createSlices();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Configure/default rotation/shift settings">
    /**
     * Configures rotation and shift settings
     */
    private void configureRotationAndShiftSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();
        // </editor-fold>
        ValueItemContainer tmpRotationAndShiftSettingsValueItemContainer = Preferences.getInstance().getRotationAndShiftEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesRotationAndShiftSettingsDialog.title"), tmpRotationAndShiftSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpRotationAndShiftSettingsValueItemContainer);
            this.createSlices();
        }
    }

    /**
     * Sets default rotation and shift settings
     */
    private void setDefaultRotationAndShiftSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();

        // </editor-fold>
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setXshiftInPixelSlicer(Preferences.getInstance().getDefaultXshiftInPixelSlicer());
            Preferences.getInstance().setYshiftInPixelSlicer(Preferences.getInstance().getDefaultYshiftInPixelSlicer());
            Preferences.getInstance().setRotationAroundXaxisAngle(Preferences.getInstance().getDefaultRotationAroundXaxisAngle());
            Preferences.getInstance().setRotationAroundYaxisAngle(Preferences.getInstance().getDefaultRotationAroundYaxisAngle());
            Preferences.getInstance().setRotationAroundZaxisAngle(Preferences.getInstance().getDefaultRotationAroundZaxisAngle());
            this.createSlices();
        }
    }

    /**
     * Sets default rotation and shift quick
     */
    private void setDefaultRotationAndShiftQuick() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();
        // </editor-fold>
        if (!this.isDefaultRotationAndShift()) {
            Preferences.getInstance().setXshiftInPixelSlicer(Preferences.getInstance().getDefaultXshiftInPixelSlicer());
            Preferences.getInstance().setYshiftInPixelSlicer(Preferences.getInstance().getDefaultYshiftInPixelSlicer());
            Preferences.getInstance().setRotationAroundXaxisAngle(Preferences.getInstance().getDefaultRotationAroundXaxisAngle());
            Preferences.getInstance().setRotationAroundYaxisAngle(Preferences.getInstance().getDefaultRotationAroundYaxisAngle());
            Preferences.getInstance().setRotationAroundZaxisAngle(Preferences.getInstance().getDefaultRotationAroundZaxisAngle());
            this.createSlices();
        }
    }
    
    /**
     * Tests default rotation and shift
     * 
     * @return True: Default rotation and shift, false: Otherwise
     */
    private boolean isDefaultRotationAndShift() {
        return 
            Preferences.getInstance().getXshiftInPixelSlicer() == Preferences.getInstance().getDefaultXshiftInPixelSlicer()
            && Preferences.getInstance().getYshiftInPixelSlicer() == Preferences.getInstance().getDefaultYshiftInPixelSlicer()
            && Preferences.getInstance().getRotationAroundXaxisAngle() == Preferences.getInstance().getDefaultRotationAroundXaxisAngle()
            && Preferences.getInstance().getRotationAroundYaxisAngle() == Preferences.getInstance().getDefaultRotationAroundYaxisAngle()
            && Preferences.getInstance().getRotationAroundZaxisAngle() == Preferences.getInstance().getDefaultRotationAroundZaxisAngle();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Zoom in/out and zoom-volume statistics">
    /**
     * Zoom in
     * 
     * @param anIsBoxVolumeShapeForZoom Flag for shape of zoom volume: True: Box volume shape, false: Ellipsoid volume shape.
     */
    private void zoomIn(boolean anIsBoxVolumeShapeForZoom) {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();
        // </editor-fold>
        if (this.firstZoomPoint != null && this.secondZoomPoint != null) {
            Preferences.getInstance().setBoxVolumeShapeForZoom(anIsBoxVolumeShapeForZoom);
            BoxSizeInfo tmpExclusionBoxSizeInfo = new BoxSizeInfo(this.firstZoomPoint, this.secondZoomPoint);
            this.graphicalParticlePositionInfo.setExclusionBoxSizeInfo(tmpExclusionBoxSizeInfo);
            Preferences.getInstance().getSimulationMovieSlicerConfiguration().setExclusionBoxSizeInfo(tmpExclusionBoxSizeInfo);
            if (this.graphicalParticlePositionInfo.hasExclusionBoxSizeInfo()) {
                // Set first slice (0)
                Preferences.getInstance().setFirstSliceIndex(Preferences.getInstance().getDefaultFirstSliceIndex());
                this.createSlices();
            }
        } else {
            PointInSpace tmpCorrectedFirstPointForMeasurement = null;
            if (this.isZoomInPossibleWithoutMeasurement()) {
                // <editor-fold defaultstate="collapsed" desc="Zoom-in WITHOUT measurement">
                switch(this.currentBoxView) {
                    case XZ_FRONT:
                        tmpCorrectedFirstPointForMeasurement = 
                            new PointInSpace(
                                0.0,
                                this.simulationBoxViewSlicer.getSliceStartValue(Preferences.getInstance().getFirstSliceIndex()),
                                0.0
                            );
                        this.secondPointForMeasurement = 
                            new PointInSpace(
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength(),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength(),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength()
                            );
                        break;
                    case YZ_LEFT:
                        tmpCorrectedFirstPointForMeasurement = 
                            new PointInSpace(
                                this.simulationBoxViewSlicer.getSliceStartValue(Preferences.getInstance().getFirstSliceIndex()),
                                0.0,
                                0.0
                            );
                        this.secondPointForMeasurement = 
                            new PointInSpace(
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength(),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength(),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength()
                            );
                        break;
                    case XY_BOTTOM:
                        tmpCorrectedFirstPointForMeasurement = 
                            new PointInSpace(
                                0.0,
                                0.0,
                                this.simulationBoxViewSlicer.getSliceStartValue(Preferences.getInstance().getFirstSliceIndex())
                            );
                        this.secondPointForMeasurement = 
                            new PointInSpace(
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength(),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength(),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength()
                            );
                        break;
                    case XZ_BACK:
                        tmpCorrectedFirstPointForMeasurement = 
                            new PointInSpace(
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength(),
                                this.simulationBoxViewSlicer.getSliceStartValue(Preferences.getInstance().getFirstSliceIndex()),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength()
                            );
                        this.secondPointForMeasurement = 
                            new PointInSpace(
                                0.0,
                                0.0,
                                0.0
                            );
                        break;
                    case YZ_RIGHT:
                        tmpCorrectedFirstPointForMeasurement = 
                            new PointInSpace(
                                this.simulationBoxViewSlicer.getSliceStartValue(Preferences.getInstance().getFirstSliceIndex()),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength(),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength()
                            );
                        this.secondPointForMeasurement = 
                            new PointInSpace(
                                0.0,
                                0.0,
                                0.0
                            );
                        break;
                    case XY_TOP:
                        tmpCorrectedFirstPointForMeasurement = 
                            new PointInSpace(
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength(),
                                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength(),
                                this.simulationBoxViewSlicer.getSliceStartValue(Preferences.getInstance().getFirstSliceIndex())
                            );
                        this.secondPointForMeasurement = 
                            new PointInSpace(
                                0.0,
                                0.0,
                                0.0
                            );
                        break;
                }
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Zoom-in WITH measurement">
                // Zoom points for measurement are in correct order
                // IMPORTANT: Use corrected first point for measurement for zoom
                tmpCorrectedFirstPointForMeasurement = this.getCorrectedFirstPointForMeasurementForZoom();
                // </editor-fold>
            }
            Preferences.getInstance().setBoxVolumeShapeForZoom(anIsBoxVolumeShapeForZoom);
            BoxSizeInfo tmpExclusionBoxSizeInfo = new BoxSizeInfo(tmpCorrectedFirstPointForMeasurement, this.secondPointForMeasurement);
            this.graphicalParticlePositionInfo.setExclusionBoxSizeInfo(tmpExclusionBoxSizeInfo);
            Preferences.getInstance().getSimulationMovieSlicerConfiguration().setExclusionBoxSizeInfo(tmpExclusionBoxSizeInfo);
            this.enlargedBoxSizeInfo = this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
            this.firstZoomPoint = tmpCorrectedFirstPointForMeasurement;
            this.secondZoomPoint = this.secondPointForMeasurement.getClone();
            this.setOriginalDisplayOfSimulationBox();
        }
    }

    /**
     * Zoom out
     */
    private void zoomOut() {
        if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasExclusionBoxSizeInfo()) {
            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
            CustomPanelSlicerController.this.stopAnimation();
            // </editor-fold>
            this.graphicalParticlePositionInfo.removeExclusionBoxSizeInfo();
            Preferences.getInstance().getSimulationMovieSlicerConfiguration().removeExclusionBoxSizeInfo();
            this.enlargedBoxSizeInfo = this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
            this.firstZoomPoint = null;
            this.secondZoomPoint = null;
            this.setOriginalDisplayOfSimulationBox();
        }
    }

    /**
     * True: Zoom in is possible, false: Otherwise
     *
     * @return True: Zoom in is possible, false: Otherwise
     */
    private boolean isZoomInPossible() {
        return this.firstPointForMeasurement != null
            && this.secondPointForMeasurement != null
            && !GraphicsUtils.isInPlane(this.firstPointForMeasurement, this.secondPointForMeasurement)
            && this.isDefaultRotationAndShift();
    }

    /**
     * True: Zoom in is possible without measurement, false: Otherwise
     *
     * @return True: Zoom in is possible without measurement, false: Otherwise
     */
    private boolean isZoomInPossibleWithoutMeasurement() {
        return 
            this.firstPointForMeasurement == null
            && this.secondPointForMeasurement == null
            && this.isDefaultRotationAndShift()
            && !this.graphicalParticlePositionInfo.hasExclusionBoxSizeInfo();
    }

    /**
     * Show zoom/exclusion statistics
     */
    private void showZoomStatistics() {
        DialogValueItemShow.show(GuiMessage.get("ZoomStatisticsDialog.Title"), this.graphicalParticlePositionInfo.getZoomStatisticsValueItemContainer(), true, true);
    }

    /**
     * Sets zoom points
     */
    private void setZoomPoints() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set zoomPointsValueItemContainer">
        ValueItemContainer tmpZoomPointsValueItemContainer = new ValueItemContainer(null);
        int tmpVerticalPosition = 0;
        String[] tmpRootNodeNames = new String[]{ModelMessage.get("SimulationBoxZoomPoints.Root")};
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpRootNodeNames);
        tmpValueItem.setName("SIMULATION_BOX_ZOOM_POINTS");
        tmpValueItem.setDisplayName(ModelMessage.get("SimulationBoxZoomPoints.DisplayName"));
        tmpValueItem.setDescription(ModelMessage.get("SimulationBoxZoomPoints.Description"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("SimulationBoxZoomPoints.X"),
            ModelMessage.get("SimulationBoxZoomPoints.Y"),
            ModelMessage.get("SimulationBoxZoomPoints.Z")});
        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_100, // X
            ModelDefinitions.CELL_WIDTH_NUMERIC_100, // Y            
            ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // Z
        if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasExclusionBoxSizeInfo()) {
            this.firstZoomPoint = Preferences.getInstance().getSimulationMovieSlicerConfiguration().getExclusionBoxSizeInfo().getFirstPoint();
            this.secondZoomPoint = Preferences.getInstance().getSimulationMovieSlicerConfiguration().getExclusionBoxSizeInfo().getSecondPoint();
        }
        // Set matrix
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[2][];
        // <editor-fold defaultstate="collapsed" desc="- Set first zoom point">
        tmpMatrix[0] = new ValueItemMatrixElement[3];
        // IMPORTANT: Use getInitialBoxSizeInfo() for boundaries
        tmpMatrix[0][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                "0.0",
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        tmpMatrix[0][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                "0.0",
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        tmpMatrix[0][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                "0.0",
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        // IMPORTANT: Use corrected first point for measurement for zoom
        PointInSpace tmpCorrectedFirstPointForMeasurement = this.getCorrectedFirstPointForMeasurementForZoom();
        if (tmpCorrectedFirstPointForMeasurement != null) {
            if (tmpCorrectedFirstPointForMeasurement.getX() < 0.0 || tmpCorrectedFirstPointForMeasurement.getX() > this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getXLength()) {
                tmpMatrix[0][0].setValue("0.0");
            } else {
                tmpMatrix[0][0].setValue(String.valueOf(tmpCorrectedFirstPointForMeasurement.getX() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            }
            if (tmpCorrectedFirstPointForMeasurement.getY() < 0.0 || tmpCorrectedFirstPointForMeasurement.getY() > this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getYLength()) {
                tmpMatrix[0][1].setValue("0.0");
            } else {
                tmpMatrix[0][1].setValue(String.valueOf(tmpCorrectedFirstPointForMeasurement.getY() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            }
            if (tmpCorrectedFirstPointForMeasurement.getZ() < 0.0 || tmpCorrectedFirstPointForMeasurement.getZ() > this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getZLength()) {
                tmpMatrix[0][2].setValue("0.0");
            } else {
                tmpMatrix[0][2].setValue(String.valueOf(tmpCorrectedFirstPointForMeasurement.getZ() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            }
        } else if (this.firstZoomPoint != null) {
            tmpMatrix[0][0].setValue(String.valueOf(this.firstZoomPoint.getX() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[0][1].setValue(String.valueOf(this.firstZoomPoint.getY() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[0][2].setValue(String.valueOf(this.firstZoomPoint.getZ() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        } else {
            tmpMatrix[0][0].setValue("0.0");
            tmpMatrix[0][1].setValue("0.0");
            tmpMatrix[0][2].setValue("0.0");
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Set second zoom point">
        tmpMatrix[1] = new ValueItemMatrixElement[3];
        // IMPORTANT: Use getInitialBoxSizeInfo() for boundaries
        tmpMatrix[1][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        tmpMatrix[1][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        tmpMatrix[1][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        if (this.secondPointForMeasurement != null) {
            if (this.secondPointForMeasurement.getX() < 0.0 || this.secondPointForMeasurement.getX() > this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getXLength()) {
                tmpMatrix[1][0].setValue(String.valueOf(this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            } else {
                tmpMatrix[1][0].setValue(String.valueOf(this.secondPointForMeasurement.getX() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            }
            if (this.secondPointForMeasurement.getY() < 0.0 || this.secondPointForMeasurement.getY() > this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getYLength()) {
                tmpMatrix[1][1].setValue(String.valueOf(this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            } else {
                tmpMatrix[1][1].setValue(String.valueOf(this.secondPointForMeasurement.getY() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            }
            if (this.secondPointForMeasurement.getZ() < 0.0 || this.secondPointForMeasurement.getZ() > this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getZLength()) {
                tmpMatrix[1][2].setValue(String.valueOf(this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            } else {
                tmpMatrix[1][2].setValue(String.valueOf(this.secondPointForMeasurement.getZ() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            }
        } else if (this.secondZoomPoint != null) {
            tmpMatrix[1][0].setValue(String.valueOf(this.secondZoomPoint.getX() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[1][1].setValue(String.valueOf(this.secondZoomPoint.getY() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[1][2].setValue(String.valueOf(this.secondZoomPoint.getZ() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        } else {
            tmpMatrix[1][0].setValue(String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[1][1].setValue(String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[1][2].setValue(String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        }
        tmpValueItem.setMatrix(tmpMatrix);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition);
        tmpZoomPointsValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // </editor-fold>
        if (DialogValueItemEdit.hasChanged(ModelMessage.get("SimulationBoxZoomPoints.DialogTitle"), tmpZoomPointsValueItemContainer)) {
            // <editor-fold defaultstate="collapsed" desc="Set zoom and start slicers">
            this.firstZoomPoint = new PointInSpace(
                    tmpValueItem.getValueAsDouble(0, 0) / this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    tmpValueItem.getValueAsDouble(0, 1) / this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    tmpValueItem.getValueAsDouble(0, 2) / this.graphicalParticlePositionInfo.getLengthConversionFactor());
            this.secondZoomPoint = new PointInSpace(
                    tmpValueItem.getValueAsDouble(1, 0) / this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    tmpValueItem.getValueAsDouble(1, 1) / this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    tmpValueItem.getValueAsDouble(1, 2) / this.graphicalParticlePositionInfo.getLengthConversionFactor());
            // Zoom points must define a box and NOT a plane
            if (this.firstZoomPoint.getX() == this.secondZoomPoint.getX() || this.firstZoomPoint.getY() == this.secondZoomPoint.getY() || this.firstZoomPoint.getZ() == this.secondZoomPoint.getZ()) {
                JOptionPane.showMessageDialog(null, 
                    GuiMessage.get("ZoomPointsNotAllowedToBeInPlane.Message"), 
                    GuiMessage.get("ZoomPointsNotAllowedToBeInPlane.Title"),
                    JOptionPane.INFORMATION_MESSAGE
                );
                this.firstZoomPoint = null;
                this.secondZoomPoint = null;
            } else {
                this.slicerPanel.getZoomInBoxVolumeShapeButton().setVisible(true);
                this.slicerPanel.getZoomInEllipsoidVolumeShapeButton().setVisible(true);
            }
            // </editor-fold>
        }
    }

    /**
     * Edits zoom-volume settings like number of volume bins
     */
    private void editVolumeSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();
        // </editor-fold>
        ValueItemContainer tmpVolumeSettingsValueItemContainer = Preferences.getInstance().getVolumeSettingsValueItemContainer();
        if (DialogValueItemEdit.hasChanged(ModelMessage.get("SimulationBoxVolumeSettings.DialogTitle"), tmpVolumeSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpVolumeSettingsValueItemContainer);
            if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasExclusionBoxSizeInfo()) {
                this.createSlices();
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Configure/default animation settings">
    /**
     * Configures animation settings
     */
    private void configureAnimationSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();

        // </editor-fold>
        ValueItemContainer tmpAnimationSettingsValueItemContainer = Preferences.getInstance().getAnimationEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesAnimationSettingsDialog.title"), tmpAnimationSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpAnimationSettingsValueItemContainer);
            // Do NOT call this.createSlices() since changed animation settings work with current slices
        }
    }

    /**
     * Sets default animation settings
     */
    private void setDefaultAnimationSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();

        // </editor-fold>
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setAnimationSpeed(Preferences.getInstance().getDefaultAnimationSpeed());
            // Do NOT call this.createSlices() since changed animation settings work with current slices
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Movie related methods">
    /**
     * Configures movie settings
     */
    private void configureMovieSettings() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelSlicerController.this.stopAnimation();

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
        // Parameter true: Supply images backwards in addition
        GuiUtils.createMovieImages(this, true, Preferences.getInstance().getImageDirectoryPathForSimulationMovies());
    }

    /**
     * Creates movie
     */
    private void createMovie() {
        if (!GuiUtils.canCreateMovie(Preferences.getInstance().getSimulationMovieImagePath())) {
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Movie.CreationNotPossible"), 
                GuiMessage.get("Movie.CreationNotPossible.Title"),
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            if (GuiUtils.getYesNoDecision(GuiMessage.get("Movie.CreationDesired.Title"), GuiMessage.get("Movie.CreationDesired"))) {
                GuiUtils.createMovie(Preferences.getInstance().getSimulationMovieImagePath(), 
                    Preferences.getInstance().getImageDirectoryPathForSimulationMovies(), 
                    Preferences.getInstance().getMovieDirectoryPathForSimulationMovies()    
                );
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Animation related methods">
    /**
     * Plays animation
     */
    private void playAnimation() {
        if (!this.isAnimationPlaying()) {
            int tmpDelay = 1000 / Preferences.getInstance().getAnimationSpeed();
            this.isAnimationPlayingForwards = true;
            this.animationTimer = new Timer(tmpDelay, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    int tmpSliceIndex = CustomPanelSlicerController.this.virtualSlider.getValue() / CustomPanelSlicerController.this.virtualSlider.getMinorTickSpacing();
                    if (tmpSliceIndex == Preferences.getInstance().getNumberOfSlicesPerView() - 1) {
                        CustomPanelSlicerController.this.isAnimationPlayingForwards = false;
                    }
                    if (tmpSliceIndex == 0) {
                        CustomPanelSlicerController.this.isAnimationPlayingForwards = true;
                    }
                    if (CustomPanelSlicerController.this.isAnimationPlayingForwards) {
                        CustomPanelSlicerController.this.virtualSlider.setValueWithoutStateChangedEvent(CustomPanelSlicerController.this.virtualSlider.getValue()
                                + CustomPanelSlicerController.this.virtualSlider.getMinorTickSpacing());
                    } else {
                        CustomPanelSlicerController.this.virtualSlider.setValueWithoutStateChangedEvent(CustomPanelSlicerController.this.virtualSlider.getValue()
                                - CustomPanelSlicerController.this.virtualSlider.getMinorTickSpacing());
                    }
                    CustomPanelSlicerController.this.setThirdDimension();
                    CustomPanelSlicerController.this.setSliceImage(tmpSliceIndex);
                    CustomPanelSlicerController.this.showThirdDimensionCoordinates(tmpSliceIndex);
                }

            });
            this.animationTimer.start();
            this.slicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textStop"));
            this.slicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText2"));
        } else {
            this.stopAnimation();
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

    /**
     * Stops animation
     */
    private void stopAnimation() {
        if (this.isAnimationPlaying()) {
            this.animationTimer.stop();
            int tmpSliceIndex = this.virtualSlider.getValue() / this.virtualSlider.getMinorTickSpacing();
            Preferences.getInstance().setFirstSliceIndex(tmpSliceIndex);
            CustomPanelSlicerController.this.setSliceImage(tmpSliceIndex);
            CustomPanelSlicerController.this.showThirdDimensionCoordinates(tmpSliceIndex);
            this.slicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textPlay"));
            this.slicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText"));
            this.setFirstSliceButtonAndOriginalButtonVisibility();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    /**
     * Updates display
     */
    private void updateDisplay() {
        this.slicerPanel.getBoxSettingsPanel().setVisible(true);
        this.slicerPanel.getFogSettingsPanel().setVisible(true);
        this.slicerPanel.getZoomPanel().setVisible(true);
        this.isThirdDimensionScroll = true;
        // Set virtual slider with default slice index
        this.virtualSlider.setValue(Preferences.getInstance().getFirstSliceIndex() * this.virtualSlider.getMinorTickSpacing());
        Preferences.getInstance().setFirstSliceIndex(this.virtualSlider.getValue() / this.virtualSlider.getMinorTickSpacing());
        this.setThirdDimension();
        if (this.setSliceImage(Preferences.getInstance().getFirstSliceIndex())) {
            if (this.isMoleculeSelection) {
                this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.MouseManipulationInfoWithSelection"));
            } else {
                this.slicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.MouseManipulationInfoWithoutSelection"));
            }
        }
        this.slicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(false);
        this.showThirdDimensionCoordinates(Preferences.getInstance().getFirstSliceIndex());
        this.setParticleShiftRemoveButtonVisibility();
        this.setBoxSettingsPanel();
        this.setFogSettingsPanel();
        this.setZoomPanel();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Measurement related methods">
    /**
     * Measure distance
     */
    private void measureDistance(MouseEvent aMouseEvent) {
        if (this.firstPointForMeasurement == null) {
            // <editor-fold defaultstate="collapsed" desc="Set measurement points and line">
            this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setPoint1((double) aMouseEvent.getX(), (double) aMouseEvent.getY());
            // </editor-fold>
            this.setFirstPointForMeasurement();
        } else {
            // <editor-fold defaultstate="collapsed" desc="Set measurement points and line">
            this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setPoint2((double) aMouseEvent.getX(), (double) aMouseEvent.getY());
            // </editor-fold>
            this.setSecondPointForMeasurement();
            switch (this.currentBoxView) {
                case XZ_FRONT:
                    if (this.firstPointForMeasurement.getY() != this.secondPointForMeasurement.getY()) {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case XZ_BACK:
                    if (this.firstPointForMeasurement.getY() != this.secondPointForMeasurement.getY()) {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case YZ_LEFT:
                    if (this.firstPointForMeasurement.getX() != this.secondPointForMeasurement.getX()) {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case YZ_RIGHT:
                    if (this.firstPointForMeasurement.getX() != this.secondPointForMeasurement.getX()) {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case XY_TOP:
                    if (this.firstPointForMeasurement.getZ() != this.secondPointForMeasurement.getZ()) {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case XY_BOTTOM:
                    if (this.firstPointForMeasurement.getZ() != this.secondPointForMeasurement.getZ()) {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        CustomPanelSlicerController.this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
            }
        }
        this.showMeasurement();
        this.setZoomPanel();
    }

    /**
     * Returns if measurement process is active
     * @return True: Measurement process is active, false: Otherwise
     */
    private boolean isMeasurement() {
        return this.firstPointForMeasurement != null;
    }
    
    /**
     * Removes measurement information
     */
    private void removeMeasurement() {
        if (this.firstPointForMeasurement != null || this.secondPointForMeasurement != null) {
            // <editor-fold defaultstate="collapsed" desc="Clear measurement points and line">
            this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().clearPoints();
            // </editor-fold>
            this.firstPointForMeasurement = null;
            this.firstSliceIndexForMeasurement = -1;
            this.secondPointForMeasurement = null;
            this.secondSliceIndexForMeasurement = -1;
            this.showMeasurement();
        }
    }

    /**
     * Removes measurement information and shows zoom
     */
    private void removeMeasurementAndSetZoomPanel() {
        this.removeMeasurement();
        this.setZoomPanel();
    }
    
    /**
     * Sets first point for measurement
     */
    private void setFirstPointForMeasurement() {
        this.firstPointForMeasurement = new PointInSpace(this.currentX, this.currentY, this.currentZ);
        this.firstSliceIndexForMeasurement = Preferences.getInstance().getFirstSliceIndex();
    }
    
    /**
     * Sets second point for measurement
     */
    private void setSecondPointForMeasurement() {
        this.secondPointForMeasurement = new PointInSpace(this.currentX, this.currentY, this.currentZ);
        this.secondSliceIndexForMeasurement = Preferences.getInstance().getFirstSliceIndex();
        // Swap points and slice indices for measurement if necessary
        if (this.secondSliceIndexForMeasurement < this.firstSliceIndexForMeasurement) {
            PointInSpace tmpPointInSpace = this.secondPointForMeasurement;
            this.secondPointForMeasurement = this.firstPointForMeasurement;
            this.firstPointForMeasurement = tmpPointInSpace;
            int tmpSliceIndex = this.secondSliceIndexForMeasurement;
            this.secondSliceIndexForMeasurement = this.firstSliceIndexForMeasurement;
            this.firstSliceIndexForMeasurement = tmpSliceIndex;
        }
    }
    
    /**
     * Corrects first point for measurement for zoom
     */
    private PointInSpace getCorrectedFirstPointForMeasurementForZoom() {
        if (this.firstPointForMeasurement != null) {
            return this.getSliceCorrectedPoint(this.firstPointForMeasurement, this.firstSliceIndexForMeasurement);
        } else {
            return null;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Resource image related methods">
    /**
     * Sets compartments image
     */
    private void setSimulationBoxViewImage() {
        switch (this.currentBoxView) {
            case XZ_FRONT:
                GuiUtils.setResourceImage(this.slicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_FRONT_IMAGE_FILENAME);
                break;
            case XZ_BACK:
                GuiUtils.setResourceImage(this.slicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_BACK_IMAGE_FILENAME);
                break;
            case YZ_LEFT:
                GuiUtils.setResourceImage(this.slicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_LEFT_IMAGE_FILENAME);
                break;
            case YZ_RIGHT:
                GuiUtils.setResourceImage(this.slicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_RIGHT_IMAGE_FILENAME);
                break;
            case XY_TOP:
                GuiUtils.setResourceImage(this.slicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_TOP_IMAGE_FILENAME);
                break;
            case XY_BOTTOM:
                GuiUtils.setResourceImage(this.slicerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_BOTTOM_IMAGE_FILENAME);
                break;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Simulation box magnification percentage fit related methods">
    /**
     * Fits simulation box magnification percentage so that box with current
     * rotation is displayed with maximum size
     */
    private void fitSimulationBoxMagnificationPercentage() {
        int tmpMaximumSimulationBoxMagnificationPercentage = this.getMaximumSimulationBoxMagnificationPercentage();
        if (tmpMaximumSimulationBoxMagnificationPercentage != Preferences.getInstance().getSimulationBoxMagnificationPercentage()) {
            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
            CustomPanelSlicerController.this.stopAnimation();
            // </editor-fold>
            Preferences.getInstance().setSimulationBoxMagnificationPercentage(tmpMaximumSimulationBoxMagnificationPercentage);
            this.createSlices();
        }
    }

    /**
     * Returns maximum simulation box magnification percentage so that box with
     * current rotation is displayed with maximum size
     *
     * @return Maximum simulation box magnification percentage so that box with
     * current rotation is displayed with maximum size
     */
    private int getMaximumSimulationBoxMagnificationPercentage() {
        PointInSpace[] tmpBoxEdgePoints3D = this.enlargedBoxSizeInfo.getBoxEdgePointArray();
        this.graphicsUtilityMethods.rotatePoints(tmpBoxEdgePoints3D,
                Preferences.getInstance().getRotationAroundXaxisAngle(),
                Preferences.getInstance().getRotationAroundYaxisAngle(),
                Preferences.getInstance().getRotationAroundZaxisAngle(),
                this.enlargedBoxSizeInfo.getBoxMidPoint());
        int tmpMaximumSimulationBoxMagnificationPercentage = 0;
        for (int tmpSimulationBoxMagnificationPercentage = 1; tmpSimulationBoxMagnificationPercentage <= ModelDefinitions.MAXIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE; tmpSimulationBoxMagnificationPercentage++) {
            // NOTE: tmpSimulationBoxMagnificationPercentage means magnification so enlarge with corresponding negative value which means box size reduction
            BoxSizeInfo tmpMagnifiedBoxSizeInfo = this.enlargedBoxSizeInfo.getEnlargedBoxSizeInfo(-tmpSimulationBoxMagnificationPercentage);
            // New code for "above/below" detection
            if (this.graphicsUtilityMethods.isMapped2dPointForGraphicsPanelAboveOrBelowDrawingArea(
                    tmpBoxEdgePoints3D,
                    tmpMagnifiedBoxSizeInfo,
                    this.currentBoxView,
                    this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize())) {
                break;
            }
            // Old code for "outside" detection:
            // if (this.graphicsUtilityMethods.isMapped2dPointForGraphicsPanelOutsideDrawingArea(
            //         tmpBoxEdgePoints3D,
            //         tmpMagnifiedBoxSizeInfo,
            //         this.currentBoxView,
            //         this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize())) {
            //     break;
            // }
            tmpMaximumSimulationBoxMagnificationPercentage = tmpSimulationBoxMagnificationPercentage;
        }
        // NOTE: Offset "-3" is subtracted for better simulation box display with "nice distance" to display boundary
        return tmpMaximumSimulationBoxMagnificationPercentage - 3;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle shift related methods">
    /**
     * Removes particle shift
     */
    private void removeParticleShift() {
        if (this.isParticleShiftDefined()) {
            Preferences.getInstance().setParticleShiftX(0);
            Preferences.getInstance().setParticleShiftY(0);
            Preferences.getInstance().setParticleShiftZ(0);
            this.createSlices();
        }
    }

    /**
     * Returns if particle shift is defined
     * 
     * @return True: Particle shift is defined, false: Otherwise
     */    
    private boolean isParticleShiftDefined() {
        return Preferences.getInstance().getParticleShiftX() != 0 || 
            Preferences.getInstance().getParticleShiftY() != 0 || 
            Preferences.getInstance().getParticleShiftZ() != 0;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Fog related methods">
    /**
     * Set fog value
     *
     * @param aValue Fog value
     */
    private void setFogOfSimulationBox(double aValue) {
        if (Preferences.getInstance().setDepthAttenuationSlicer(aValue)) {
            this.createSlices();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Simulation box frame change related methods">
    /**
     * Set simulation box frame
     */
    private void setSimulationBoxFrame() {
        if (!Preferences.getInstance().isFrameDisplaySlicer()) {
            Preferences.getInstance().setFrameDisplaySlicer(true);
            this.createSlices();
        }
    }

    /**
     * Remove simulation box frame
     */
    private void removeSimulationBoxFrame() {
        if (Preferences.getInstance().isFrameDisplaySlicer()) {
            Preferences.getInstance().setFrameDisplaySlicer(false);
            this.createSlices();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Slice index related methods">
    /**
     * Set first slice (0)
     */
    private void setFirstSlice() {
        // Set first slice (0)
        Preferences.getInstance().setFirstSliceIndex(Preferences.getInstance().getDefaultFirstSliceIndex());
        this.virtualSlider.setValue(Preferences.getInstance().getFirstSliceIndex() * this.virtualSlider.getMinorTickSpacing());
        Preferences.getInstance().setFirstSliceIndex(this.virtualSlider.getValue() / this.virtualSlider.getMinorTickSpacing());
        this.setThirdDimension();
        this.setSliceImage(Preferences.getInstance().getFirstSliceIndex());
        this.showThirdDimensionCoordinates(Preferences.getInstance().getFirstSliceIndex());
        this.setFirstSliceButtonAndOriginalButtonVisibility();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Slicer related methods">
    private void startSlicerWithDefinedBoxView() {
        switch(Preferences.getInstance().getBoxViewDisplay()) {
            case XZ_FRONT:
                if (this.slicerPanel.getXzFrontRadioButton().isSelected()) {
                    this.createSlices();
                } else {
                    this.slicerPanel.getXzFrontRadioButton().setSelected(true);
                }
                break;
            case XZ_BACK:
                if (this.slicerPanel.getXzBackRadioButton().isSelected()) {
                    this.createSlices();
                } else {
                    this.slicerPanel.getXzBackRadioButton().setSelected(true);
                }
                break;
            case YZ_LEFT:
                if (this.slicerPanel.getYzLeftRadioButton().isSelected()) {
                    this.createSlices();
                } else {
                    this.slicerPanel.getYzLeftRadioButton().setSelected(true);
                }
                break;
            case YZ_RIGHT:
                if (this.slicerPanel.getYzRightRadioButton().isSelected()) {
                    this.createSlices();
                } else {
                    this.slicerPanel.getYzRightRadioButton().setSelected(true);
                }
                break;
            case XY_TOP:
                if (this.slicerPanel.getXyTopRadioButton().isSelected()) {
                    this.createSlices();
                } else {
                    this.slicerPanel.getXyTopRadioButton().setSelected(true);
                }
                break;
            case XY_BOTTOM:
                if (this.slicerPanel.getXyBottomRadioButton().isSelected()) {
                    this.createSlices();
                } else {
                    this.slicerPanel.getXyBottomRadioButton().setSelected(true);
                }
                break;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Molecule selection related methods">
    /**
     * Selects molecule
     */
    private void selectMolecule() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int tmpNearestParticleIndex = this.simulationBoxViewSlicer.getNearestParticleIndex(Preferences.getInstance().getFirstSliceIndex(), 
                    this.getSelectionPoint()
                );
            if (tmpNearestParticleIndex >= 0) {
                if (this.graphicalParticlePositionInfo.selectMolecule(tmpNearestParticleIndex)) {
                    this.startSlicersAfterMoleculeSelectionChange();
                }
                this.showSelectedMoleculeInfo(tmpNearestParticleIndex);
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Selects all molecules of type
     */
    private void selectAllMolecules() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            LinkedList<Integer> tmpParticleIndexList = 
                this.simulationBoxViewSlicer.getParticleIndicesOfAllMolecules(Preferences.getInstance().getFirstSliceIndex(), 
                    this.getSelectionPoint()
                );
            if (tmpParticleIndexList != null) {
                if (tmpParticleIndexList.size() > Preferences.getInstance().getMaxSelectedMoleculeNumberSlicer()) {
                    if (
                            !GuiUtils.getYesNoDecision(GuiMessage.get("MoleculeSelection.Title"), 
                                String.format(GuiMessage.get("MoleculeSelection.MaxNumberOfSelectedMoleculesExceededFormat"),
                                    tmpParticleIndexList.size(),
                                    Preferences.getInstance().getMaxSelectedMoleculeNumberSlicer()
                                )
                            )
                        ) {
                        return;
                    }
                }
                boolean tmpIsMoleculeSelectionChanged = false;
                for (int tmpParticleIndex : tmpParticleIndexList) {
                    if (this.graphicalParticlePositionInfo.selectMolecule(tmpParticleIndex)) {
                        tmpIsMoleculeSelectionChanged = true;
                    }
                }
                if (tmpIsMoleculeSelectionChanged) {
                    this.startSlicersAfterMoleculeSelectionChange();
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Deselects molecule
     */
    private void deselectMolecule() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int tmpNearestParticleIndex = this.simulationBoxViewSlicer.getNearestParticleIndex(Preferences.getInstance().getFirstSliceIndex(), 
                    this.getSelectionPoint()
                );
            if (tmpNearestParticleIndex >= 0) {
                if (this.graphicalParticlePositionInfo.deselectMolecule(tmpNearestParticleIndex)) {
                    this.startSlicersAfterMoleculeSelectionChange();
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Deselects all molecule of type
     */
    private void deselectAllMolecules() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            LinkedList<Integer> tmpParticleIndexList = this.simulationBoxViewSlicer.getParticleIndicesOfAllMolecules(Preferences.getInstance().getFirstSliceIndex(), 
                    this.getSelectionPoint()
                );
            if (tmpParticleIndexList != null) {
                boolean tmpIsMoleculeSelectionChanged = false;
                for (int tmpParticleIndex : tmpParticleIndexList) {
                    if (this.graphicalParticlePositionInfo.deselectMolecule(tmpParticleIndex)) {
                        tmpIsMoleculeSelectionChanged = true;
                    }
                }
                if (tmpIsMoleculeSelectionChanged) {
                    this.startSlicersAfterMoleculeSelectionChange();
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    
    /**
     * Clears molecule selection
     */
    private void clearMoleculeSelection() {
        if (this.graphicalParticlePositionInfo.isMoleculeSelected()) {
            // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
            this.stopAnimation();
            // </editor-fold>
            if (GuiUtils.getYesNoDecision(GuiMessage.get("ClearSelection.Title"), GuiMessage.get("ClearSelection"))) {
                this.graphicalParticlePositionInfo.clearSelection();
                this.startSlicersAfterMoleculeSelectionChange();
            }
        }
    }
    
    /**
     * Configures molecule selections
     */
    private void configureMoleculeSelections() {
        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        this.stopAnimation();
        // </editor-fold>
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            ValueItemContainer tmpMoleculeSelectionValueItemContainer = this.graphicalParticlePositionInfo.getMoleculeSelectionValueItemContainer();
            if (tmpMoleculeSelectionValueItemContainer == null) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                JOptionPane.showMessageDialog(
                    null, 
                    GuiMessage.get("MoleculeSelection.NoMoleculeSelectionDialog"),
                    GuiMessage.get("MoleculeSelection.Title"),
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                MouseCursorManagement.getInstance().setDefaultCursor();
                if (DialogValueItemEdit.hasChanged(GuiMessage.get("MoleculeSelection.Title"), tmpMoleculeSelectionValueItemContainer)) {
                    MouseCursorManagement.getInstance().setWaitCursor();
                    this.graphicalParticlePositionInfo.setMoleculeSelectionValueItemContainer(tmpMoleculeSelectionValueItemContainer);
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    this.startSlicersAfterMoleculeSelectionChange();
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    
    /**
     * Returns selection point
     * 
     * @return Selection point
     */
    private IPointInSpace getSelectionPoint() {
        PointInSpace tmpNonCorrectedSelectionPoint = new PointInSpace(this.currentX, this.currentY, this.currentZ);
        return this.getSliceCorrectedPoint(tmpNonCorrectedSelectionPoint, Preferences.getInstance().getFirstSliceIndex());
    }

    /**
     * Set molecule selection display
     */
    private void setMoleculeSelectionDisplay() {
        if (this.graphicalParticlePositionInfo.getNumberOfSelectedMolecules() == 0) {
            this.slicerPanel.getSelectionRadioButton().setText(GuiMessage.get("CustomPanelSlicer.selectionRadioButton.text"));
        } else {
            this.slicerPanel.getSelectionRadioButton().setText(String.format(GuiMessage.get("CustomPanelSlicer.selectionRadioButton.formatString"), 
                        this.graphicalParticlePositionInfo.getNumberOfSelectedMolecules()
                    )
                );
        }
        if (this.slicerPanel.getSelectionRadioButton().isSelected()) {
            this.slicerPanel.getFirstSettingsButton().setVisible(this.graphicalParticlePositionInfo.isMoleculeSelected());
        }
    }
    
    /**
     * Starts slicers after molecule selection change
     */
    private void startSlicersAfterMoleculeSelectionChange() {
        Preferences.getInstance().getSimulationMovieSlicerConfiguration().setMoleculeSelectionManager(this.graphicalParticlePositionInfo.getMoleculeSelectionManager());
        this.setMoleculeSelectionDisplay();
        this.createSlices();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Spin and box wait/move dialogs related methods">
    /**
     * Start dialog for flying through simulation box
     */
    private void startFlyDialog() {
        switch (this.currentBoxView) {
            case XZ_FRONT:
                this.numberOfBoxMoveSteps = Math.abs(Preferences.getInstance().getNumberOfSpinSteps());
                if (Preferences.getInstance().getNumberOfSpinSteps() > 0) {
                    this.boxMoveChangeInfo.setParticleShiftChangeY(-100);
                } else {
                    this.boxMoveChangeInfo.setParticleShiftChangeY(100);
                }
                break;
            case XZ_BACK:
                this.numberOfBoxMoveSteps = Math.abs(Preferences.getInstance().getNumberOfSpinSteps());
                if (Preferences.getInstance().getNumberOfSpinSteps() > 0) {
                    this.boxMoveChangeInfo.setParticleShiftChangeY(100);
                } else {
                    this.boxMoveChangeInfo.setParticleShiftChangeY(-100);
                }
                break;
            case YZ_LEFT:
                this.numberOfBoxMoveSteps = Math.abs(Preferences.getInstance().getNumberOfSpinSteps());
                if (Preferences.getInstance().getNumberOfSpinSteps() > 0) {
                    this.boxMoveChangeInfo.setParticleShiftChangeX(-100);
                } else {
                    this.boxMoveChangeInfo.setParticleShiftChangeX(100);
                }
                break;
            case YZ_RIGHT:
                this.numberOfBoxMoveSteps = Math.abs(Preferences.getInstance().getNumberOfSpinSteps());
                if (Preferences.getInstance().getNumberOfSpinSteps() > 0) {
                    this.boxMoveChangeInfo.setParticleShiftChangeX(100);
                } else {
                    this.boxMoveChangeInfo.setParticleShiftChangeX(-100);
                }
                Preferences.getInstance().setNumberOfSpinSteps(-Preferences.getInstance().getNumberOfSpinSteps());
                break;
            case XY_TOP:
                this.numberOfBoxMoveSteps = Math.abs(Preferences.getInstance().getNumberOfSpinSteps());
                if (Preferences.getInstance().getNumberOfSpinSteps() > 0) {
                    this.boxMoveChangeInfo.setParticleShiftChangeZ(100);
                } else {
                    this.boxMoveChangeInfo.setParticleShiftChangeZ(-100);
                }
                break;
            case XY_BOTTOM:
                this.numberOfBoxMoveSteps = Math.abs(Preferences.getInstance().getNumberOfSpinSteps());
                if (Preferences.getInstance().getNumberOfSpinSteps() > 0) {
                    this.boxMoveChangeInfo.setParticleShiftChangeZ(-100);
                } else {
                    this.boxMoveChangeInfo.setParticleShiftChangeZ(100);
                }
                break;
        }
        this.startBoxMoveDialog();
    }

    /**
     * Start dialog for spinning around horizontal axis
     */
    private void startSpinAroundHorizontalAxisDialog() {
        SpinAxisEnum tmpSpinAxes = null;
        switch (this.currentBoxView) {
            case XZ_FRONT:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_X;
                break;
            case XZ_BACK:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_X;
                Preferences.getInstance().setNumberOfSpinSteps(-Preferences.getInstance().getNumberOfSpinSteps());
                break;
            case YZ_LEFT:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_Y;
                break;
            case YZ_RIGHT:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_Y;
                Preferences.getInstance().setNumberOfSpinSteps(-Preferences.getInstance().getNumberOfSpinSteps());
                break;
            case XY_TOP:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_X;
                break;
            case XY_BOTTOM:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_X;
                break;
        }
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(Preferences.getInstance().getNumberOfParallelSlicers());
        DialogSpinStepSlicerShow.show(tmpExecutorService,
            GuiMessage.get("DialogSpinStepSlicerHorizontal.title"), 
            this.graphicalParticlePositionInfo, 
            tmpSpinAxes, 
            this.enlargedBoxSizeInfo
        );
        try {
            tmpExecutorService.shutdown();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        } finally {
            if (this.currentBoxView == SimulationBoxViewEnum.XZ_BACK || this.currentBoxView == SimulationBoxViewEnum.YZ_RIGHT) {
                Preferences.getInstance().setNumberOfSpinSteps(-Preferences.getInstance().getNumberOfSpinSteps());
            }
        }
    }

    /**
     * Start dialog for spinning around vertical axis
     */
    private void startSpinAroundVerticalAxisDialog() {
        SpinAxisEnum tmpSpinAxes = null;
        switch (this.currentBoxView) {
            case XZ_FRONT:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_Z;
                break;
            case XZ_BACK:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_Z;
                break;
            case YZ_LEFT:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_Z;
                break;
            case YZ_RIGHT:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_Z;
                break;
            case XY_TOP:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_Y;
                Preferences.getInstance().setNumberOfSpinSteps(-Preferences.getInstance().getNumberOfSpinSteps());
                break;
            case XY_BOTTOM:
                tmpSpinAxes = SpinAxisEnum.SPIN_AROUND_Y;
                break;
        }
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(Preferences.getInstance().getNumberOfParallelSlicers());
        DialogSpinStepSlicerShow.show(tmpExecutorService,
            GuiMessage.get("DialogSpinStepSlicerVertical.title"), 
            this.graphicalParticlePositionInfo, 
            tmpSpinAxes, 
            this.enlargedBoxSizeInfo
        );
        try {
            tmpExecutorService.shutdown();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        } finally {
            if (this.currentBoxView == SimulationBoxViewEnum.XY_TOP) {
                Preferences.getInstance().setNumberOfSpinSteps(-Preferences.getInstance().getNumberOfSpinSteps());
            }
        }
    }

    /**
     * Start dialog for box wait
     */
    private void startBoxWaitDialog() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (Preferences.getInstance().getNumberOfBoxWaitSteps() == 0) {
            JOptionPane.showMessageDialog(null, GuiMessage.get("NoBoxWaitStepsDefined.Message"), GuiMessage.get("NoBoxWaitStepsDefined.Title"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;

        }
        // </editor-fold>
        // IMPORTANT: Set this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo(), otherwise Wait will not have the same zoom state
        this.boxMoveChangeInfo.setTargetExclusionBoxSizeInfo(this.graphicalParticlePositionInfo.getExclusionBoxSizeInfo());
        this.numberOfBoxMoveSteps = Preferences.getInstance().getNumberOfBoxWaitSteps();
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(Preferences.getInstance().getNumberOfParallelSlicers());
        DialogMoveStepSlicerShow.show(tmpExecutorService,
            GuiMessage.get("DialogMoveStepSlicer.title"),
            this.graphicalParticlePositionInfo,
            this.enlargedBoxSizeInfo,
            this.numberOfBoxMoveSteps,
            this.boxMoveChangeInfo
        );
        try {
            tmpExecutorService.shutdown();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
        this.numberOfBoxMoveSteps = 0;
    }

    /**
     * Start dialog for box move
     */
    private void startBoxMoveDialog() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.numberOfBoxMoveSteps == 0) {
            JOptionPane.showMessageDialog(
                null, 
                GuiMessage.get("NoBoxMoveStepsDefined.Message"), 
                GuiMessage.get("NoBoxMoveStepsDefined.Title"),
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        // </editor-fold>
        ExecutorService tmpExecutorService = Executors.newFixedThreadPool(Preferences.getInstance().getNumberOfParallelSlicers());
        DialogMoveStepSlicerShow.show(tmpExecutorService,
            GuiMessage.get("DialogMoveStepSlicer.title"),
            this.graphicalParticlePositionInfo,
            this.enlargedBoxSizeInfo,
            this.numberOfBoxMoveSteps,
            this.boxMoveChangeInfo
        );
        try {
            tmpExecutorService.shutdown();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
        }
        if (this.boxMoveChangeInfo.getRotationChangeAroundXaxisAngle() != 0
            || this.boxMoveChangeInfo.getRotationChangeAroundYaxisAngle() != 0
            || this.boxMoveChangeInfo.getRotationChangeAroundZaxisAngle() != 0
            || this.boxMoveChangeInfo.getXshiftChange() != 0
            || this.boxMoveChangeInfo.getYshiftChange() != 0
            || this.boxMoveChangeInfo.getParticleShiftChangeX() != 0
            || this.boxMoveChangeInfo.getParticleShiftChangeY() != 0
            || this.boxMoveChangeInfo.getParticleShiftChangeZ() != 0
            || this.boxMoveChangeInfo.getSimulationBoxMagnificationPercentageChange() != 0
            || this.boxMoveChangeInfo.getDepthAttenuationChangeSlicer() != 0.0
            || this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo() != null
            || this.boxMoveChangeInfo.getFirstSliceIndexChange() != 0
            ) {
            // <editor-fold defaultstate="collapsed" desc="Rotation">
            Preferences.getInstance().setRotationAroundXaxisAngle(((int) (Preferences.getInstance().getRotationAroundXaxisAngle() + this.boxMoveChangeInfo.getRotationChangeAroundXaxisAngle())) % 360);
            Preferences.getInstance().setRotationAroundYaxisAngle(((int) (Preferences.getInstance().getRotationAroundYaxisAngle() + this.boxMoveChangeInfo.getRotationChangeAroundYaxisAngle())) % 360);
            Preferences.getInstance().setRotationAroundZaxisAngle(((int) (Preferences.getInstance().getRotationAroundZaxisAngle() + this.boxMoveChangeInfo.getRotationChangeAroundZaxisAngle())) % 360);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Shift">
            Preferences.getInstance().setXshiftInPixelSlicer((int) (Preferences.getInstance().getXshiftInPixelSlicer() + this.boxMoveChangeInfo.getXshiftChange()));
            Preferences.getInstance().setYshiftInPixelSlicer((int) (Preferences.getInstance().getYshiftInPixelSlicer() + this.boxMoveChangeInfo.getYshiftChange()));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Particle shift">
            Preferences.getInstance().setParticleShiftX(((int) (Preferences.getInstance().getParticleShiftX()+ this.boxMoveChangeInfo.getParticleShiftChangeX())) % 100);
            Preferences.getInstance().setParticleShiftY(((int) (Preferences.getInstance().getParticleShiftY()+ this.boxMoveChangeInfo.getParticleShiftChangeY())) % 100);
            Preferences.getInstance().setParticleShiftZ(((int) (Preferences.getInstance().getParticleShiftZ()+ this.boxMoveChangeInfo.getParticleShiftChangeZ())) % 100);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="SimulationBoxMagnificationPercentage">
            double tmpCorrectedValue = ModelUtils.correctDoubleValue(Preferences.getInstance().getSimulationBoxMagnificationPercentage() + this.boxMoveChangeInfo.getSimulationBoxMagnificationPercentageChange(),
                    ModelDefinitions.MINIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE,
                    ModelDefinitions.MAXIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE
            );
            Preferences.getInstance().setSimulationBoxMagnificationPercentage((int) tmpCorrectedValue);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="DepthAttenuationSlicer">
            tmpCorrectedValue = ModelUtils.correctDoubleValue(Preferences.getInstance().getDepthAttenuationSlicer() + this.boxMoveChangeInfo.getDepthAttenuationChangeSlicer(),
                    ModelDefinitions.DEPTH_ATTENUATION_SLICER_MINIMUM,
                    ModelDefinitions.DEPTH_ATTENUATION_SLICER_MAXIMUM
            );
            Preferences.getInstance().setDepthAttenuationSlicer(tmpCorrectedValue);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Zoom">
            this.graphicalParticlePositionInfo.setExclusionBoxSizeInfo(this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo());
            Preferences.getInstance().getSimulationMovieSlicerConfiguration().setExclusionBoxSizeInfo(this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="FirstSliceIndex">
            tmpCorrectedValue = ModelUtils.correctDoubleValue(ModelUtils.roundDoubleValue(Preferences.getInstance().getFirstSliceIndex() + this.boxMoveChangeInfo.getFirstSliceIndexChange(), 0),
                    Preferences.getInstance().getDefaultFirstSliceIndex(),
                    Preferences.getInstance().getNumberOfSlicesPerView()
            );
            Preferences.getInstance().setFirstSliceIndex((int) tmpCorrectedValue);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Clear box move settings">
            // Do NOT change this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo()
            this.boxMoveChangeInfo.clearWithoutTargetExclusionBoxSizeInfo();
            // </editor-fold>
            this.createSlices();
        }
        this.numberOfBoxMoveSteps = 0;
    }

    /**
     * Edit spin settings
     */
    private void editMoveAndSpinSettings() {
        // <editor-fold defaultstate="collapsed" desc="Spin steps settings">
        ValueItemContainer tmpSpinAndMovePreferencesValueItemContainer = Preferences.getInstance().getSlicerSpinStepsEditablePrefencesValueItemContainer();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Wait steps settings">
        int tmpVerticalPosition = tmpSpinAndMovePreferencesValueItemContainer.getMaximumVerticalPosition() + 1;
        String[] tmpNodeNames = 
            new String[] { 
                ModelMessage.get("Preferences.Root"), ModelMessage.get("Preferences.SimulationBox"),
                ModelMessage.get("Preferences.BoxWaitSteps")
            };
        // <editor-fold defaultstate="collapsed" desc="- Number of wait steps value item">
        ValueItem tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName("CUSTOM_PANEL_SLICER_NUMBER_OF_BOX_WAIT_STEPS");
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("0.0", 0, 0.0, Double.POSITIVE_INFINITY));
        tmpValueItem.setDescription(ModelMessage.get("Preferences.BoxWaitSteps.NumberOfBoxWaitSteps.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.BoxWaitSteps.NumberOfBoxWaitSteps"));
        tmpValueItem.setValue(String.valueOf(Preferences.getInstance().getNumberOfBoxWaitSteps()));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpSpinAndMovePreferencesValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Move steps settings">
        tmpNodeNames = 
            new String[] { 
                ModelMessage.get("Preferences.Root"), 
                ModelMessage.get("Preferences.SimulationBox"),
                ModelMessage.get("Preferences.BoxMoveSteps")
            };
        // <editor-fold defaultstate="collapsed" desc="- Number of move steps value item">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName("CUSTOM_PANEL_SLICER_NUMBER_OF_BOX_MOVE_STEPS");
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("0.0", 0, 0.0, Double.POSITIVE_INFINITY));
        tmpValueItem.setDescription(ModelMessage.get("Preferences.BoxMoveSteps.NumberOfBoxMoveSteps.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.BoxMoveSteps.NumberOfBoxMoveSteps"));
        tmpValueItem.setValue(String.valueOf(this.numberOfBoxMoveSteps));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpSpinAndMovePreferencesValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Rotation change value item for box move">
        GuiUtils.addRotationChangeValueItem(tmpSpinAndMovePreferencesValueItemContainer, 
            tmpVerticalPosition, 
            tmpNodeNames, 
            ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME,
            this.boxMoveChangeInfo
        );
        tmpVerticalPosition++;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Shift change value item for box move">
        GuiUtils.addShiftChangeValueItem(tmpSpinAndMovePreferencesValueItemContainer, 
            tmpVerticalPosition, 
            tmpNodeNames, 
            ModelDefinitions.SHIFT_CHANGE_VALUE_ITEM_NAME,
            this.boxMoveChangeInfo
        );
        tmpVerticalPosition++;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Particle shift change value item for box move">
        GuiUtils.addParticleShiftChangeValueItem(
            tmpSpinAndMovePreferencesValueItemContainer, 
            tmpVerticalPosition, 
            tmpNodeNames, 
            ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME,
            this.boxMoveChangeInfo
        );
        tmpVerticalPosition++;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Simulation box magnification percentage">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        int tmpMaximumDifferenceMagnification = ModelDefinitions.MAXIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE - ModelDefinitions.MINIMUM_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE;
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("0.0", 0, -tmpMaximumDifferenceMagnification, tmpMaximumDifferenceMagnification));
        tmpValueItem.setName("CUSTOM_PANEL_SLICER_BOX_MOVE_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE");
        tmpValueItem.setDescription(ModelMessage.get("Preferences.BoxMoveSteps.SimulationBoxMagnificationPercentage.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.BoxMoveSteps.SimulationBoxMagnificationPercentage"));
        tmpValueItem.setValue(String.valueOf(this.boxMoveChangeInfo.getSimulationBoxMagnificationPercentageChange()));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpSpinAndMovePreferencesValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Depth attenuation slicer">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        double tmpMaximumDifferenceDepthAttenuation = ModelDefinitions.DEPTH_ATTENUATION_SLICER_MAXIMUM - ModelDefinitions.DEPTH_ATTENUATION_SLICER_MINIMUM;
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("0.0", 2, -tmpMaximumDifferenceDepthAttenuation, tmpMaximumDifferenceDepthAttenuation));
        tmpValueItem.setName("CUSTOM_PANEL_SLICER_BOX_MOVE_DEPTH_ATTENUATION_SLICER");
        tmpValueItem.setDescription(ModelMessage.get("Preferences.BoxMoveSteps.DepthAttenuationSlicer.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.BoxMoveSteps.DepthAttenuationSlicer"));
        tmpValueItem.setValue(String.valueOf(this.boxMoveChangeInfo.getDepthAttenuationChangeSlicer()));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpSpinAndMovePreferencesValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Zoom points">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setName("SIMULATION_BOX_ZOOM_POINTS");
        tmpValueItem.setDisplayName(ModelMessage.get("SimulationBoxZoomPoints.DisplayName"));
        tmpValueItem.setDescription(ModelMessage.get("SimulationBoxZoomPoints.Description"));
        tmpValueItem.setBasicType(ValueItemEnumBasicType.MATRIX);
        tmpValueItem.setMatrixColumnNames(new String[]{ModelMessage.get("SimulationBoxZoomPoints.X"),
            ModelMessage.get("SimulationBoxZoomPoints.Y"),
            ModelMessage.get("SimulationBoxZoomPoints.Z")});
        tmpValueItem.setMatrixColumnWidths(new String[]{ModelDefinitions.CELL_WIDTH_NUMERIC_100, // X
            ModelDefinitions.CELL_WIDTH_NUMERIC_100, // Y            
            ModelDefinitions.CELL_WIDTH_NUMERIC_100}); // Z
        // Set matrix
        ValueItemMatrixElement[][] tmpMatrix = new ValueItemMatrixElement[2][];
        // First zoom point
        tmpMatrix[0] = new ValueItemMatrixElement[3];
        tmpMatrix[0][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                "0.0",
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()
        ));
        tmpMatrix[0][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                "0.0",
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()
        ));
        tmpMatrix[0][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                "0.0",
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()
        ));
        if (this.graphicalParticlePositionInfo.hasExclusionBoxSizeInfo()) {
            this.boxMoveChangeInfo.setTargetExclusionBoxSizeInfo(this.graphicalParticlePositionInfo.getExclusionBoxSizeInfo());
        } else {
            this.boxMoveChangeInfo.setTargetExclusionBoxSizeInfo(null);
        }
        if (this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo() != null) {
            tmpMatrix[0][0].setValue(String.valueOf(this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo().getXMin() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[0][1].setValue(String.valueOf(this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo().getYMin() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[0][2].setValue(String.valueOf(this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo().getZMin() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        } else {
            tmpMatrix[0][0].setValue("0.0");
            tmpMatrix[0][1].setValue("0.0");
            tmpMatrix[0][2].setValue("0.0");
        }
        // Set second zoom point
        tmpMatrix[1] = new ValueItemMatrixElement[3];
        tmpMatrix[1][0] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()
        ));
        tmpMatrix[1][1] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()
        ));
        tmpMatrix[1][2] = new ValueItemMatrixElement(new ValueItemDataTypeFormat(
                String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()),
                ModelDefinitions.BOX_SIZE_NUMBER_OF_DECIMALS,
                0.0,
                this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()
        ));
        if (this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo() != null) {
            tmpMatrix[1][0].setValue(String.valueOf(this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo().getXMax() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[1][1].setValue(String.valueOf(this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo().getYMax() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[1][2].setValue(String.valueOf(this.boxMoveChangeInfo.getTargetExclusionBoxSizeInfo().getZMax() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        } else {
            tmpMatrix[1][0].setValue(String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getXLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[1][1].setValue(String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getYLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
            tmpMatrix[1][2].setValue(String.valueOf(this.graphicalParticlePositionInfo.getInitialBoxSizeInfo().getZLength() * this.graphicalParticlePositionInfo.getLengthConversionFactor()));
        }
        tmpValueItem.setMatrix(tmpMatrix);
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpSpinAndMovePreferencesValueItemContainer.addValueItem(tmpValueItem);
        // IMPORTANT: Initialize change detection for target exclusion box size info value item AFTER adding to container
        tmpValueItem.initializeChangeDetection();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- First slice index">
        tmpValueItem = new ValueItem();
        tmpValueItem.setNodeNames(tmpNodeNames);
        tmpValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat("0.0", 0, -Preferences.getInstance().getNumberOfSlicesPerView(), Preferences.getInstance().getNumberOfSlicesPerView()));
        tmpValueItem.setName("CUSTOM_PANEL_SLICER_BOX_MOVE_FIRST_SLICE_INDEX");
        tmpValueItem.setDescription(ModelMessage.get("Preferences.BoxMoveSteps.FirstSliceIndex.Description"));
        tmpValueItem.setDisplayName(ModelMessage.get("Preferences.BoxMoveSteps.FirstSliceIndex"));
        tmpValueItem.setValue(String.valueOf(this.boxMoveChangeInfo.getFirstSliceIndexChange()));
        tmpValueItem.setVerticalPosition(tmpVerticalPosition++);
        tmpSpinAndMovePreferencesValueItemContainer.addValueItem(tmpValueItem);
        // </editor-fold>
        // </editor-fold>
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesSlicerSpinStepsSettingsDialog.title"), tmpSpinAndMovePreferencesValueItemContainer)) {
            // <editor-fold defaultstate="collapsed" desc="Set changes">
            Preferences.getInstance().setEditablePreferences(tmpSpinAndMovePreferencesValueItemContainer);

            Preferences.getInstance().setNumberOfBoxWaitSteps(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem("CUSTOM_PANEL_SLICER_NUMBER_OF_BOX_WAIT_STEPS").getValueAsInt());

            this.numberOfBoxMoveSteps = tmpSpinAndMovePreferencesValueItemContainer.getValueItem("CUSTOM_PANEL_SLICER_NUMBER_OF_BOX_MOVE_STEPS").getValueAsInt();

            this.boxMoveChangeInfo.setRotationChangeAroundXaxisAngle(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem(ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 0));
            this.boxMoveChangeInfo.setRotationChangeAroundYaxisAngle(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem(ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 1));
            this.boxMoveChangeInfo.setRotationChangeAroundZaxisAngle(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem(ModelDefinitions.ROTATION_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 2));

            this.boxMoveChangeInfo.setXshiftChange(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem(ModelDefinitions.SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 0));
            this.boxMoveChangeInfo.setYshiftChange(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem(ModelDefinitions.SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 1));

            this.boxMoveChangeInfo.setParticleShiftChangeX(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem(ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 0));
            this.boxMoveChangeInfo.setParticleShiftChangeY(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem(ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 1));
            this.boxMoveChangeInfo.setParticleShiftChangeZ(
                tmpSpinAndMovePreferencesValueItemContainer.getValueItem(ModelDefinitions.PARTICLE_SHIFT_CHANGE_VALUE_ITEM_NAME).getValueAsInt(0, 2));
            
            this.boxMoveChangeInfo.setSimulationBoxMagnificationPercentageChange(tmpSpinAndMovePreferencesValueItemContainer.getValueItem("CUSTOM_PANEL_SLICER_BOX_MOVE_SIMULATION_BOX_MAGNIFICATION_PERCENTAGE").getValueAsInt());

            this.boxMoveChangeInfo.setDepthAttenuationChangeSlicer(tmpSpinAndMovePreferencesValueItemContainer.getValueItem("CUSTOM_PANEL_SLICER_BOX_MOVE_DEPTH_ATTENUATION_SLICER").getValueAsDouble());

            // <editor-fold defaultstate="collapsed" desc="Zoom points">
            ValueItem tmpZoomPointsValueItem = tmpSpinAndMovePreferencesValueItemContainer.getValueItem("SIMULATION_BOX_ZOOM_POINTS");
            PointInSpace tmpFirstZoomPoint = new PointInSpace(
                    tmpZoomPointsValueItem.getValueAsDouble(0, 0) / this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    tmpZoomPointsValueItem.getValueAsDouble(0, 1) / this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    tmpZoomPointsValueItem.getValueAsDouble(0, 2) / this.graphicalParticlePositionInfo.getLengthConversionFactor());
            PointInSpace tmpSecondZoomPoint = new PointInSpace(
                    tmpZoomPointsValueItem.getValueAsDouble(1, 0) / this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    tmpZoomPointsValueItem.getValueAsDouble(1, 1) / this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    tmpZoomPointsValueItem.getValueAsDouble(1, 2) / this.graphicalParticlePositionInfo.getLengthConversionFactor());
            // Zoom points must define a box and NOT a plane
            if (tmpFirstZoomPoint.getX() == tmpSecondZoomPoint.getX() || tmpFirstZoomPoint.getY() == tmpSecondZoomPoint.getY() || tmpFirstZoomPoint.getZ() == tmpSecondZoomPoint.getZ()) {
                this.boxMoveChangeInfo.setTargetExclusionBoxSizeInfo(null);
                JOptionPane.showMessageDialog(null, GuiMessage.get("ZoomPointsNotAllowedToBeInPlane.Message"), GuiMessage.get("ZoomPointsNotAllowedToBeInPlane.Title"),
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (tmpZoomPointsValueItem.hasChangeDetected()) {
                this.boxMoveChangeInfo.setTargetExclusionBoxSizeInfo(new BoxSizeInfo(tmpFirstZoomPoint, tmpSecondZoomPoint));
            }
            // </editor-fold>

            this.boxMoveChangeInfo.setFirstSliceIndexChange(tmpSpinAndMovePreferencesValueItemContainer.getValueItem("CUSTOM_PANEL_SLICER_BOX_MOVE_FIRST_SLICE_INDEX").getValueAsInt());
            // </editor-fold>
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Corrects point for slice start value (instead of slice end value)
     * 
     * @param aPointToBeCorrected Point to be corrected
     * @param aSliceIndex Slice index
     * @return Corrected point
     */
    private PointInSpace getSliceCorrectedPoint(PointInSpace aPointToBeCorrected, int aSliceIndex) {
        if (aPointToBeCorrected != null) {
            switch (this.currentBoxView) {
                case XZ_FRONT:
                case XZ_BACK:
                    if (this.simulationBoxViewSlicer != null) {
                        // NaN-Check for method getSliceStartValue() is NOT necessary here
                        return new PointInSpace(
                            aPointToBeCorrected.getX(), 
                            this.simulationBoxViewSlicer.getSliceStartValue(aSliceIndex), 
                            aPointToBeCorrected.getZ()
                        );
                    } else {
                        return aPointToBeCorrected;
                    }
                case XY_BOTTOM:
                case XY_TOP:
                    if (this.simulationBoxViewSlicer != null) {
                        // NaN-Check for method getSliceStartValue() is NOT necessary here
                        return new PointInSpace(
                            aPointToBeCorrected.getX(), 
                            aPointToBeCorrected.getY(), 
                            this.simulationBoxViewSlicer.getSliceStartValue(aSliceIndex)
                        );
                    } else {
                        return aPointToBeCorrected;
                    }
                case YZ_LEFT:
                case YZ_RIGHT:
                    if (this.simulationBoxViewSlicer != null) {
                        // NaN-Check for method getSliceStartValue() is NOT necessary here
                        return new PointInSpace(
                            this.simulationBoxViewSlicer.getSliceStartValue(aSliceIndex), 
                            aPointToBeCorrected.getY(), 
                            aPointToBeCorrected.getZ()
                        );
                    } else {
                        return aPointToBeCorrected;
                    }
            }                
        }
        return null;
    }
    
    /**
     * Releases memory of slicer
     */
    private void releaseMemoryOfSlicer() {
        if (this.simulationBoxViewSlicer != null) {
            this.simulationBoxViewSlicer.releaseMemory();
        }
    }
    // </editor-fold>
    // </editor-fold>

}
