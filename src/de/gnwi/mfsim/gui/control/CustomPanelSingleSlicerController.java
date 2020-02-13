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

import de.gnwi.mfsim.model.util.ImageSelection;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.util.FileUtilityMethods;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.graphics.point.PointInPlane;
import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.slice.SimulationBoxViewSlicer;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.GraphicsUtilityMethods;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.spices.PointInSpace;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.gui.util.VirtualSlider;
import de.gnwi.mfsim.model.util.ModelUtils;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
public class CustomPanelSingleSlicerController extends ChangeNotifier implements IImageProvider, ChangeListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility graphics methods
     */
    private GraphicsUtilityMethods graphicsUtilityMethods = new GraphicsUtilityMethods();

    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Utility for files
     */
    private final FileUtilityMethods fileUtilityMethods = new FileUtilityMethods();

    /**
     * Simulation box slicer panel
     */
    private CustomPanelSingleSlicer singleSlicerPanel;

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
     * Second point for measurement
     */
    private PointInSpace secondPointForMeasurement;

    /**
     * GraphicalParticlePositionInfo instance
     */
    private GraphicalParticlePositionInfo graphicalParticlePositionInfo;

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
     * Simulation box view slicer
     */
    private SimulationBoxViewSlicer simulationBoxViewSlicer;

    /**
     * Index of first slice to be shown
     */
    private int firstSlideIndex;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelSingleSlicer Panel this controller is made for
     * @param aGraphicalParticlePositionInfo GraphicalParticlePositionInfo
 instance
     * @param anImageFileType File type of the images to be created
     * @param aBoxView Simulation box view
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelSingleSlicerController(CustomPanelSingleSlicer aCustomPanelSingleSlicer, GraphicalParticlePositionInfo aGraphicalParticlePositionInfo, ImageFileType anImageFileType,
            SimulationBoxViewEnum aBoxView) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelSingleSlicer == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aGraphicalParticlePositionInfo == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            // NOTE: Command sequence is NOT to be changed
            // NOTE: Setting unscaled drawing of this.slicerPanel.getSlicerImagePanel().getDrawPanel() is NOT necessary since width and height correspond to control size
            this.singleSlicerPanel = aCustomPanelSingleSlicer;
            this.singleSlicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(false);
            this.singleSlicerPanel.getSimulationBoxPanel().getCreateMovieButton().setVisible(false);
            this.singleSlicerPanel.getSimulationBoxPanel().getEditMoveAndSpinSettingsButton().setVisible(false);
            this.singleSlicerPanel.getSimulationBoxPanel().getBoxWaitButton().setVisible(false);
            this.singleSlicerPanel.getSimulationBoxPanel().getBoxMoveButton().setVisible(false);
            this.singleSlicerPanel.getSimulationBoxPanel().getSpinAroundXButton().setVisible(false);
            this.singleSlicerPanel.getSimulationBoxPanel().getSpinAroundYButton().setVisible(false);
            this.singleSlicerPanel.getSimulationBoxPanel().getSpinAroundZButton().setVisible(false);
            this.imageFileType = anImageFileType;
            this.graphicalParticlePositionInfo = aGraphicalParticlePositionInfo;
            // Set value item container for molecule/particles display settings if available
            if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasMoleculeDisplaySettingsValueItemContainer()) {
                this.graphicalParticlePositionInfo.getGraphicalParticleInfo().setExclusionsColorsAndPreferences(Preferences.getInstance().getSimulationMovieSlicerConfiguration().getMoleculeDisplaySettingsValueItemContainer());
            }
            // Set exclusion box size info if available
            if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasExclusionBoxSizeInfo()) {
                this.graphicalParticlePositionInfo.setExclusionBoxSizeInfo(Preferences.getInstance().getSimulationMovieSlicerConfiguration().getExclusionBoxSizeInfo());
            }
            // Set molecule selection manager
            if (Preferences.getInstance().getSimulationMovieSlicerConfiguration().hasMoleculeSelectionManager()) {
                this.graphicalParticlePositionInfo.setMoleculeSelectionManager(Preferences.getInstance().getSimulationMovieSlicerConfiguration().getMoleculeSelectionManager());
            }
            this.firstSlideIndex = Preferences.getInstance().getFirstSliceIndex();
            // Set box frame
            // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
            this.enlargedBoxSizeInfo = this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
            // No measurement
            this.removeMeasurement();
            // Set view full box button invisible
            this.singleSlicerPanel.getSimulationBoxPanel().getViewFullBoxButton().setVisible(false);
            // Set virtualSlider BEFORE setting this.setThirdDimensionSlider()
            this.virtualSlider = new VirtualSlider(this.singleSlicerPanel.getSimulationBoxPanel().getThirdDimensionSlider());
            // Set ThirdDimensionSlider AFTER setting virtual slider
            this.setThirdDimensionSlider();
            this.updateBoxView(aBoxView);
            // Set slicer start settings
            this.setSlicerStartSettings();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Animation settings">
            this.isAnimationPlayingForwards = false;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            // <editor-fold defaultstate="collapsed" desc="- Listeners for graphics panel">
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().addMouseMotionListener(new MouseMotionAdapter() {

                public void mouseDragged(MouseEvent e) {
                    try {
                        if (!CustomPanelSingleSlicerController.this.isAnimationPlaying()) {
                            if (e.isShiftDown() && !e.isControlDown() && !e.isAltDown() && SwingUtilities.isLeftMouseButton(e)) { // Shift + left mouse button pressed
                                // <editor-fold defaultstate="collapsed" desc="Magnification">
                                Preferences.getInstance().setSimulationBoxMagnificationPercentage(
                                        CustomPanelSingleSlicerController.this.mousePressedMagnificationPercentage - (e.getY() - CustomPanelSingleSlicerController.this.mousePressedY) / 4);
                                CustomPanelSingleSlicerController.this.showMagnification();
                                // </editor-fold>
                            } else if (!e.isShiftDown() && !e.isControlDown() && !e.isAltDown() && SwingUtilities.isRightMouseButton(e)) { // Only right mouse button pressed
                                // <editor-fold defaultstate="collapsed" desc="Rotation around perpendicular axes">
                                switch (CustomPanelSingleSlicerController.this.currentBoxView) {
                                    case XZ_FRONT:
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedYRotationAngle - (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XZ_BACK:
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedYRotationAngle + (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case YZ_LEFT:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedXRotationAngle + (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case YZ_RIGHT:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedXRotationAngle - (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XY_TOP:
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedZRotationAngle - (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XY_BOTTOM:
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    default:
                                        CustomPanelSingleSlicerController.this.setPlaneCoordinates(e);
                                        CustomPanelSingleSlicerController.this.showFullCoordinates();
                                        return;
                                }
                                CustomPanelSingleSlicerController.this.showRotation();
                                // </editor-fold>
                            } else if (!e.isShiftDown() && !e.isControlDown() && !e.isAltDown() && SwingUtilities.isLeftMouseButton(e)) { // Only Left mouse button pressed
                                // <editor-fold defaultstate="collapsed" desc="Rotation around display axes">
                                switch (CustomPanelSingleSlicerController.this.currentBoxView) {
                                    case XZ_FRONT:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedXRotationAngle + (e.getY() - CustomPanelSingleSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XZ_BACK:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedXRotationAngle - (e.getY() - CustomPanelSingleSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case YZ_LEFT:
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedYRotationAngle + (e.getY() - CustomPanelSingleSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case YZ_RIGHT:
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedYRotationAngle - (e.getY() - CustomPanelSingleSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundZaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedZRotationAngle + (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XY_TOP:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedXRotationAngle + (e.getY() - CustomPanelSingleSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedYRotationAngle - (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    case XY_BOTTOM:
                                        Preferences.getInstance().setRotationAroundXaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedXRotationAngle + (e.getY() - CustomPanelSingleSlicerController.this.mousePressedY) / 4);
                                        Preferences.getInstance().setRotationAroundYaxisAngle(
                                                CustomPanelSingleSlicerController.this.mousePressedYRotationAngle + (e.getX() - CustomPanelSingleSlicerController.this.mousePressedX) / 4);
                                        break;
                                    default:
                                        CustomPanelSingleSlicerController.this.setPlaneCoordinates(e);
                                        CustomPanelSingleSlicerController.this.showFullCoordinates();
                                        return;
                                }
                                CustomPanelSingleSlicerController.this.showRotation();
                                // </editor-fold>
                            } else if(!e.isShiftDown() && e.isControlDown() && !e.isAltDown() && SwingUtilities.isLeftMouseButton(e)) {
                                // <editor-fold defaultstate="collapsed" desc="Shift">
                                Preferences.getInstance().setXshiftInPixelSlicer(CustomPanelSingleSlicerController.this.mousePressedXshift + e.getX() - CustomPanelSingleSlicerController.this.mousePressedX);
                                Preferences.getInstance().setYshiftInPixelSlicer(-(CustomPanelSingleSlicerController.this.mousePressedYshift + e.getY() - CustomPanelSingleSlicerController.this.mousePressedY));
                                CustomPanelSingleSlicerController.this.showShift();
                                // </editor-fold>
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseDragged()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseMoved(MouseEvent e) {
                    try {
                        if (!CustomPanelSingleSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSingleSlicerController.this.setPlaneCoordinates(e);
                            CustomPanelSingleSlicerController.this.showFullCoordinates();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseMoved()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    try {
                        if (!CustomPanelSingleSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSingleSlicerController.this.mousePressedX = e.getX();
                            CustomPanelSingleSlicerController.this.mousePressedY = e.getY();

                            CustomPanelSingleSlicerController.this.mousePressedXshift = Preferences.getInstance().getXshiftInPixelSlicer();
                            // IMPORTANT: y-direction on screen runs in opposite direction
                            CustomPanelSingleSlicerController.this.mousePressedYshift = -Preferences.getInstance().getYshiftInPixelSlicer();

                            CustomPanelSingleSlicerController.this.mousePressedXRotationAngle = Preferences.getInstance().getRotationAroundXaxisAngle();
                            CustomPanelSingleSlicerController.this.mousePressedYRotationAngle = Preferences.getInstance().getRotationAroundYaxisAngle();
                            CustomPanelSingleSlicerController.this.mousePressedZRotationAngle = Preferences.getInstance().getRotationAroundZaxisAngle();

                            CustomPanelSingleSlicerController.this.mousePressedMagnificationPercentage = Preferences.getInstance().getSimulationBoxMagnificationPercentage();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mousePressed()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    try {
                        if (!CustomPanelSingleSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSingleSlicerController.this.setPlaneCoordinates(e);
                            CustomPanelSingleSlicerController.this.showFullCoordinates();
                            if (Preferences.getInstance().getXshiftInPixelSlicer() != CustomPanelSingleSlicerController.this.mousePressedXshift
                                || Preferences.getInstance().getYshiftInPixelSlicer() != CustomPanelSingleSlicerController.this.mousePressedYshift
                                || Preferences.getInstance().getRotationAroundXaxisAngle() != CustomPanelSingleSlicerController.this.mousePressedXRotationAngle
                                || Preferences.getInstance().getRotationAroundYaxisAngle() != CustomPanelSingleSlicerController.this.mousePressedYRotationAngle
                                || Preferences.getInstance().getRotationAroundZaxisAngle() != CustomPanelSingleSlicerController.this.mousePressedZRotationAngle
                                || Preferences.getInstance().getSimulationBoxMagnificationPercentage() != CustomPanelSingleSlicerController.this.mousePressedMagnificationPercentage) 
                            {
                                CustomPanelSingleSlicerController.this.createSlices();
                            } else {
                                // Rotation or magnification did NOT change: Repaint image again by removing box edge points
                                CustomPanelSingleSlicerController.this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().removeBoxEdgePoints();
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseReleased()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    try {
                        if (!CustomPanelSingleSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSingleSlicerController.this.setPlaneCoordinates(e);
                            CustomPanelSingleSlicerController.this.showFullCoordinates();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseEntered()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

                public void mouseExited(MouseEvent e) {
                    try {
                        if (!CustomPanelSingleSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSingleSlicerController.this.showThirdDimensionCoordinates();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseExited()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseClicked(MouseEvent e) {
                    try {
                        if (!CustomPanelSingleSlicerController.this.isAnimationPlaying()) {
                            if (e.isControlDown()) {
                                CustomPanelSingleSlicerController.this.setPlaneCoordinates(e);
                                CustomPanelSingleSlicerController.this.showFullCoordinates();
                                if (SwingUtilities.isRightMouseButton(e)) {
                                    // Right-Button click
                                    CustomPanelSingleSlicerController.this.removeMeasurement();
                                } else {
                                    CustomPanelSingleSlicerController.this.measureDistance(e);
                                }
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseClicked()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().addMouseWheelListener(new MouseWheelListener() {

                public void mouseWheelMoved(MouseWheelEvent e) {
                    try {
                        if (!CustomPanelSingleSlicerController.this.isAnimationPlaying()) {
                            CustomPanelSingleSlicerController.this.setThirdDimensionIncrement(e.getWheelRotation());
                            CustomPanelSingleSlicerController.this.showFullCoordinates();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseWheelMoved()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.singleSlicerPanel.getSimulationBoxPanel().getSaveGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSingleSlicerController.this.stopAnimation();
                        String tmpFilePathname = GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.selectGraphicsFileSaveChooser"), GuiDefinitions.IMAGE_BMP_FILE_EXTENSION);
                        if (tmpFilePathname != null) {
                            try {
                                GraphicsUtils.writeImageToFile(CustomPanelSingleSlicerController.this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getImage(),
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
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.singleSlicerPanel.getSimulationBoxPanel().getCopyGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelSingleSlicerController.this.stopAnimation();
                        if (!ImageSelection.copyImageToClipboard(CustomPanelSingleSlicerController.this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().getImage())) {

                            // <editor-fold defaultstate="collapsed" desc="Show error message">
                            JOptionPane.showMessageDialog(null, GuiMessage.get("Error.CopyOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                    JOptionPane.ERROR_MESSAGE);

                            // </editor-fold>
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.singleSlicerPanel.getSimulationBoxPanel().getRedrawButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelSingleSlicerController.this.singleSlicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(false);
                        // Stop animation if necessary
                        CustomPanelSingleSlicerController.this.stopAnimation();
                        CustomPanelSingleSlicerController.this.createSlices();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.singleSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelSingleSlicerController.this.playAnimation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelSingleSlicerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listener for info label">
            this.singleSlicerPanel.getSimulationBoxPanel().getInfoLabel().addMouseListener(new MouseAdapter() {
                
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        CustomPanelSingleSlicerController.this.showMouseManipulationInfo();
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- This instance as change listener for third dimension slider">
            // Add this as change listener to third dimension slider
            this.singleSlicerPanel.getSimulationBoxPanel().getThirdDimensionSlider().addChangeListener(this);
            // </editor-fold>
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelSingleSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
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
        if (this.singleSlicerPanel.getSimulationBoxPanel().getRedrawButton().isVisible() != aValue) {
            this.singleSlicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(aValue);
            this.showMeasurement();
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
        if (anIndex < 0 || anIndex > Preferences.getInstance().getNumberOfSlicesPerView() - 1) {
            return null;
        }

        // </editor-fold>
        try {
            return this.simulationBoxViewSlicer.getSliceImage(anIndex);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "getSliceImage()", "CustomPanelSingleSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return null;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- createSlices">
    /**
     * Creates slices
     */
    public void createSlices() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set wait cursor">
            MouseCursorManagement.getInstance().setWaitCursor();
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
            this.secondPointForMeasurement = null;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Create slicer">
            // Create new SimulationBoxViewSlicer instances with possible exclusions and modified colors
            String tmpDestinationPath = this.fileUtilityMethods.getUniqueTemporaryDirectoryPath();
            // Set new colors and scaled radii to graphical particles
            this.graphicalParticlePositionInfo.getGraphicalParticleInfo().setCurrentColorAndScaledRadiusOfParticles();
            // Set current graphical particle positions according to settings (colors, exclusions, rotations)
            // Parameter false: Zoom statistics are NOT calculated
            this.graphicalParticlePositionInfo.setCurrentGraphicalParticlePositions(false);
            // Get new enlarged box size info AFTER setting current graphical particle positions
            // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
            this.enlargedBoxSizeInfo = this.graphicalParticlePositionInfo.getCurrentBoxSizeInfo().getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
            // Set and show third dimension coordinates
            this.setThirdDimension();
            // Set slicer image panel ratio of height to width (may have changed due to changed box size info)
            this.setSlicerImagePanelRatioOfHeightToWidth();
            // Do NOT clone this.graphicalParticlePositionInfo.getCurrentGraphicalParticlePositionArrayList()
            this.simulationBoxViewSlicer = 
                new SimulationBoxViewSlicer(
                    this.currentBoxView, 
                    tmpDestinationPath, 
                    this.enlargedBoxSizeInfo,
                    this.graphicalParticlePositionInfo.getCurrentGraphicalParticlePositionArrayList(), 
                    this.imageFileType, 
                    this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getWidth(),
                    this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getHeight()
                );
            if (!this.simulationBoxViewSlicer.createSlices()) {
                // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                JOptionPane.showMessageDialog(null, 
                    String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Method this.simulationBoxViewSlicer.createSlices() failed", "CustomPanelSingleSlicerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), 
                    JOptionPane.ERROR_MESSAGE
                );
                // </editor-fold>
            } else {
                this.updateDisplay();
            }
            // <editor-fold defaultstate="collapsed" desc="Set default cursor">
            MouseCursorManagement.getInstance().setDefaultCursor();
            // </editor-fold>
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Set default cursor">
            MouseCursorManagement.getInstance().setDefaultCursor();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "startSlicer()", "CustomPanelSingleSlicerController"), 
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
    // <editor-fold defaultstate="collapsed" desc="- NumberOfImages">
    /**
     * Returns number of images
     *
     * @return Number of images
     */
    public int getNumberOfImages() {
        return Preferences.getInstance().getNumberOfSlicesPerView();
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
    @Override
    public void stateChanged(ChangeEvent e) {
        try {
            // <editor-fold defaultstate="collapsed" desc="Check if 3rd dimension scroll is allowed">
            if (!this.isThirdDimensionScroll) {
                this.virtualSlider.setValue(0);
                return;
            }
            // </editor-fold>
            this.virtualSlider.setValueFromSlider();
            this.setThirdDimension();
            this.showThirdDimensionCoordinates();
            this.setSliceImage();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "CustomPanelSingleSlicerController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
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
     */
    private void showThirdDimensionCoordinates() {
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
        this.singleSlicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionCoordinateSlicer"), 
                this.currentBoxView.toRepresentation(), // current_box_view
                String.valueOf(this.firstSlideIndex), // current_slice_index
                tmpThirdDimensionString // coordinate
            )
        ); 
    }

    /**
     * Shows full coordinates
     */
    private void showFullCoordinates() {
        this.singleSlicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("CompartmentGraphics.FullCoordinatesSlicer"), 
                this.currentBoxView.toRepresentation(), // current_box_view
                String.valueOf(this.firstSlideIndex),   // current_slice_index
this.stringUtilityMethods.formatDoubleValue(this.currentX * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES), // x-coordinate
this.stringUtilityMethods.formatDoubleValue(this.currentY * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES), // y-coordinate
this.stringUtilityMethods.formatDoubleValue(this.currentZ * this.graphicalParticlePositionInfo.getLengthConversionFactor(),
                    ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES)  // z-coordinate
            )
        );
    }

    /**
     * Shows shift
     */
    private void showShift() {
        this.singleSlicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("SimulationBoxSlicer.Shift"),
                String.valueOf(Preferences.getInstance().getXshiftInPixelSlicer()),
                String.valueOf(Preferences.getInstance().getYshiftInPixelSlicer())
            )
        );

        PointInSpace[] tmpBoxEdgePoints3D = this.enlargedBoxSizeInfo.getBoxEdgePointArray();
        this.graphicsUtilityMethods.rotatePoints(tmpBoxEdgePoints3D,
                Preferences.getInstance().getRotationAroundXaxisAngle(),
                Preferences.getInstance().getRotationAroundYaxisAngle(),
                Preferences.getInstance().getRotationAroundZaxisAngle(),
                this.enlargedBoxSizeInfo.getBoxMidPoint());

        // NOTE: Preferences.getInstance().getSimulationBoxMagnificationPercentage() means magnification so enlarge with corresponding negative value which means box size reduction
        BoxSizeInfo tmpMagnifiedBoxSizeInfo = this.enlargedBoxSizeInfo.getEnlargedBoxSizeInfo(-Preferences.getInstance().getSimulationBoxMagnificationPercentage());
        // NOTE: Preferences.getInstance().getXshiftInPixelSlicer() and 
        // Preferences.getInstance().getYshiftInPixelSlicer() are taken in 
        // TargetCoordinatesAndSize into account:
        // this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize()
        PointInPlane[] tmpBoxEdgePoints2D = this.graphicsUtilityMethods.getMapped2dPointsForGraphicsPanel(
            tmpBoxEdgePoints3D,
            tmpMagnifiedBoxSizeInfo,
            this.currentBoxView,
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize()
        );
        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBoxEdgePoints(tmpBoxEdgePoints2D);
    }

    /**
     * Shows rotation
     */
    private void showRotation() {
        this.singleSlicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("SimulationBoxSlicer.Rotation"),
                        String.valueOf(Preferences.getInstance().getRotationAroundXaxisAngle()),
                        String.valueOf(Preferences.getInstance().getRotationAroundYaxisAngle()),
                        String.valueOf(Preferences.getInstance().getRotationAroundZaxisAngle())));

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
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize()
        );
        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBoxEdgePoints(tmpBoxEdgePoints2D);
    }

    /**
     * Shows simulation box magnification
     */
    private void showMagnification() {
        this.singleSlicerPanel.getSimulationBoxPanel().getCoordinatesLabel().setText(String.format(GuiMessage.get("CompartmentGraphics.Magnification"),
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
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize()
        );
        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBoxEdgePoints(tmpBoxEdgePoints2D);
    }

    /**
     * Shows measurement
     */
    private void showMeasurement() {
        if (this.singleSlicerPanel.getSimulationBoxPanel().getRedrawButton().isVisible()) {
            this.singleSlicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.RedrawInfo"));
        } else {
            if (this.firstPointForMeasurement == null) {
                // <editor-fold defaultstate="collapsed" desc="No measurement">
                this.singleSlicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.MouseManipulationInfoWithoutSelection"));
                // </editor-fold>
            } else {
                if (this.secondPointForMeasurement == null) {
                    // <editor-fold defaultstate="collapsed" desc="First point only">
                    this.singleSlicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.FirstPointForMeasurement"));
                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Measurement">
                    this.singleSlicerPanel.getSimulationBoxPanel().getInfoLabel().setText(String.format(GuiMessage.get("SimulationBoxSlicer.DistanceMeasurement"), this.stringUtilityMethods.formatDoubleValue(this.graphicsUtilityMethods.getConvertedDistanceInSpace(this.firstPointForMeasurement, this.secondPointForMeasurement, this.graphicalParticlePositionInfo.getLengthConversionFactor()),
                                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES)));
                    // </editor-fold>
                }
            }
        }
    }
    
    /**
     * Shows mouse manipulation info
     */
    private void showMouseManipulationInfo() {
        JOptionPane.showMessageDialog(null, 
            GuiMessage.get("MouseManipulationInfoWithoutSelection.Message"), 
            GuiMessage.get("MouseManipulationInfo.Title"),
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Set methods">
    /**
     * Sets this.slicerPanel.getSimulationBoxPanel().getSlicerImagePanel() with
     * ratio of height to width according to this.currentBoxView and
     * this.enlargedBoxSizeInfo
     */
    private void setSlicerImagePanelRatioOfHeightToWidth() {
        switch (this.currentBoxView) {
            case XZ_FRONT:
            case XZ_BACK:
                this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength() / this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength());
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength() / this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength());
                break;
            case XY_TOP:
            case XY_BOTTOM:
                this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().setRatioOfHeightToWidth(this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength() / this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength());
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
        if (this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getWidth() > this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth()) {
            tmpCorrectedMouseX = 
                (double) (aMouseEvent.getX() - this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getXcoordinateWithXshift(Preferences.getInstance().getXshiftInPixelSlicer()));
            tmpCorrectedMouseY = (double) aMouseEvent.getY() + Preferences.getInstance().getYshiftInPixelSlicer();
        } else if (this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getHeight() > this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight()) {
            tmpCorrectedMouseX = (double) aMouseEvent.getX() + Preferences.getInstance().getXshiftInPixelSlicer();
            tmpCorrectedMouseY = 
                (double) (aMouseEvent.getY() - this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getYcoordinateWithYshift(Preferences.getInstance().getYshiftInPixelSlicer()));
        } else {
            tmpCorrectedMouseX = (double) aMouseEvent.getX() + Preferences.getInstance().getXshiftInPixelSlicer();
            tmpCorrectedMouseY = (double) aMouseEvent.getY() + Preferences.getInstance().getYshiftInPixelSlicer();
        }
        switch (this.currentBoxView) {
            case XZ_FRONT:
                this.currentX = this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength() * tmpCorrectedMouseX
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth() + this.enlargedBoxSizeInfo.getRotationDisplayFrameXMin();
                this.currentZ = this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        * ((double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() - tmpCorrectedMouseY)
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() + this.enlargedBoxSizeInfo.getRotationDisplayFrameZMin();
                break;
            case XZ_BACK:
                this.currentX = this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength()
                        * (1.0 - tmpCorrectedMouseX / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth())
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameXMin();
                this.currentZ = this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        * ((double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() - tmpCorrectedMouseY)
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() + this.enlargedBoxSizeInfo.getRotationDisplayFrameZMin();
                break;
            case YZ_LEFT:
                this.currentY = this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength()
                        * (1.0 - tmpCorrectedMouseX / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth())
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameYMin();
                this.currentZ = this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        * ((double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() - tmpCorrectedMouseY)
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() + this.enlargedBoxSizeInfo.getRotationDisplayFrameZMin();
                break;
            case YZ_RIGHT:
                this.currentY = this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength() * tmpCorrectedMouseX
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth() + this.enlargedBoxSizeInfo.getRotationDisplayFrameYMin();
                this.currentZ = this.enlargedBoxSizeInfo.getRotationDisplayFrameZLength()
                        * ((double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() - tmpCorrectedMouseY)
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getHeight() + this.enlargedBoxSizeInfo.getRotationDisplayFrameZMin();
                break;
            case XY_TOP:
                this.currentX = this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength() * tmpCorrectedMouseX
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth() + this.enlargedBoxSizeInfo.getRotationDisplayFrameXMin();
                this.currentY = this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength()
                        * (1.0 - tmpCorrectedMouseY / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth())
                        + this.enlargedBoxSizeInfo.getRotationDisplayFrameYMin();
                break;
            case XY_BOTTOM:
                this.currentX = this.enlargedBoxSizeInfo.getRotationDisplayFrameXLength() * tmpCorrectedMouseX
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth() + this.enlargedBoxSizeInfo.getRotationDisplayFrameXMin();
                this.currentY = this.enlargedBoxSizeInfo.getRotationDisplayFrameYLength() * tmpCorrectedMouseY
                        / (double) this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanelCoordinatesAndSize().getWidth() + this.enlargedBoxSizeInfo.getRotationDisplayFrameYMin();
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
        this.setThirdDimension();
        this.setSliceImage();
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
        this.singleSlicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.CreatingSliceImages"));
        // Disable 3rd dimension scrolling
        this.isThirdDimensionScroll = false;
        // Disable visibility of simulation box elements
        this.singleSlicerPanel.getSimulationBoxPanel().getSaveGraphicsButton().setVisible(false);
        this.singleSlicerPanel.getSimulationBoxPanel().getCopyGraphicsButton().setVisible(false);
        this.singleSlicerPanel.getSimulationBoxPanel().getRedrawButton().setVisible(false);
        this.singleSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setVisible(false);
        this.singleSlicerPanel.getSimulationBoxPanel().getThirdDimensionSlider().setVisible(false);
        // Set wait cursor
        MouseCursorManagement.getInstance().setWaitCursor();
    }

    /**
     * Sets slice image
     *
     * @return True: Slice image could be set, false: Slice image was not
     * available
     */
    private boolean setSliceImage() {
        this.firstSlideIndex = this.virtualSlider.getValue() / this.virtualSlider.getMinorTickSpacing();
        BufferedImage tmpImage = this.simulationBoxViewSlicer.getSliceImage(this.firstSlideIndex);
        if (tmpImage == null) {
            return false;
        } else {
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setBasicImage(tmpImage);
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

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Animation related methods">
    /**
     * Plays animation
     */
    private void playAnimation() {
        if (!this.isAnimationPlaying()) {
            int tmpDelay = 1000 / Preferences.getInstance().getAnimationSpeed();
            this.isAnimationPlayingForwards = true;
            this.animationTimer = new Timer(tmpDelay, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    int tmpSliceIndex = CustomPanelSingleSlicerController.this.virtualSlider.getValue() / CustomPanelSingleSlicerController.this.virtualSlider.getMinorTickSpacing();
                    if (tmpSliceIndex == Preferences.getInstance().getNumberOfSlicesPerView() - 1) {
                        CustomPanelSingleSlicerController.this.isAnimationPlayingForwards = false;
                    }
                    if (tmpSliceIndex == 0) {
                        CustomPanelSingleSlicerController.this.isAnimationPlayingForwards = true;
                    }
                    if (CustomPanelSingleSlicerController.this.isAnimationPlayingForwards) {
                        CustomPanelSingleSlicerController.this.virtualSlider.setValueWithoutStateChangedEvent(CustomPanelSingleSlicerController.this.virtualSlider.getValue()
                                + CustomPanelSingleSlicerController.this.virtualSlider.getMinorTickSpacing());
                    } else {
                        CustomPanelSingleSlicerController.this.virtualSlider.setValueWithoutStateChangedEvent(CustomPanelSingleSlicerController.this.virtualSlider.getValue()
                                - CustomPanelSingleSlicerController.this.virtualSlider.getMinorTickSpacing());
                    }
                    CustomPanelSingleSlicerController.this.setThirdDimension();
                    CustomPanelSingleSlicerController.this.showThirdDimensionCoordinates();
                    CustomPanelSingleSlicerController.this.setSliceImage();
                }

            });
            this.animationTimer.start();
            this.singleSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textStop"));
            this.singleSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText2"));
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
            this.singleSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textPlay"));
            this.singleSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText"));
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
     * Updates with new box view
     *
     * @param aBoxView Box view
     */
    private void updateBoxView(SimulationBoxViewEnum aBoxView) {
        this.currentBoxView = aBoxView;
        this.setSlicerImagePanelRatioOfHeightToWidth();
        // Set third dimension slider to 0
        this.virtualSlider.setValue(0);
        this.setThirdDimension();
        this.showThirdDimensionCoordinates();
    }

    /**
     * Updates display
     */
    private void updateDisplay() {
        // <editor-fold defaultstate="collapsed" desc="Set default cursor">
        MouseCursorManagement.getInstance().setDefaultCursor();
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set third dimension scroll">
        this.isThirdDimensionScroll = true;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set slice image and GUI elements">
        // <editor-fold defaultstate="collapsed" desc="- Set virtual slider with default slice index">
        this.virtualSlider.setValue(this.firstSlideIndex * this.virtualSlider.getMinorTickSpacing());
        this.setThirdDimension();
        this.showThirdDimensionCoordinates();
        // </editor-fold>
        if (this.setSliceImage()) {
            this.singleSlicerPanel.getSimulationBoxPanel().getInfoLabel().setText(GuiMessage.get("SimulationBoxSlicer.MouseManipulationInfoWithoutSelection"));
            // Enable visibility of simulation box elements
            this.singleSlicerPanel.getSimulationBoxPanel().getSaveGraphicsButton().setVisible(true);
            this.singleSlicerPanel.getSimulationBoxPanel().getCopyGraphicsButton().setVisible(true);
            this.singleSlicerPanel.getSimulationBoxPanel().getPlayAnimationButton().setVisible(true);
            this.singleSlicerPanel.getSimulationBoxPanel().getThirdDimensionSlider().setVisible(true);
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Measurement related methods">
    /**
     * Measure distance
     */
    private void measureDistance(MouseEvent aMouseEvent) {
        if (this.firstPointForMeasurement == null) {
            // <editor-fold defaultstate="collapsed" desc="Set measurement points and line">
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setPoint1((double) aMouseEvent.getX(), (double) aMouseEvent.getY());
            // </editor-fold>
            this.firstPointForMeasurement = new PointInSpace(this.currentX, this.currentY, this.currentZ);
        } else {
            // <editor-fold defaultstate="collapsed" desc="Set measurement points and line">
            this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setPoint2((double) aMouseEvent.getX(), (double) aMouseEvent.getY());
            // </editor-fold>
            this.secondPointForMeasurement = new PointInSpace(this.currentX, this.currentY, this.currentZ);
            switch (this.currentBoxView) {
                case XZ_FRONT:
                    if (this.firstPointForMeasurement.getY() != this.secondPointForMeasurement.getY()) {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case XZ_BACK:
                    if (this.firstPointForMeasurement.getY() != this.secondPointForMeasurement.getY()) {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case YZ_LEFT:
                    if (this.firstPointForMeasurement.getX() != this.secondPointForMeasurement.getX()) {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case YZ_RIGHT:
                    if (this.firstPointForMeasurement.getX() != this.secondPointForMeasurement.getX()) {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case XY_TOP:
                    if (this.firstPointForMeasurement.getZ() != this.secondPointForMeasurement.getZ()) {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
                case XY_BOTTOM:
                    if (this.firstPointForMeasurement.getZ() != this.secondPointForMeasurement.getZ()) {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(true);
                    } else {
                        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().setLineDashed(false);
                    }
                    break;
            }
        }
        this.showMeasurement();
    }

    /**
     * Removes measurement information
     */
    private void removeMeasurement() {

        // <editor-fold defaultstate="collapsed" desc="Clear measurement points and line">
        this.singleSlicerPanel.getSimulationBoxPanel().getSlicerImagePanel().getDrawPanel().clearPoints();

        // </editor-fold>
        this.firstPointForMeasurement = null;
        this.secondPointForMeasurement = null;
        this.showMeasurement();
    }
    // </editor-fold>
    // </editor-fold>

}
