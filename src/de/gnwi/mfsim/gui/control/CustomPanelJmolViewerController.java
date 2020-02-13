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

import de.gnwi.mfsim.gui.util.VirtualSlider;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.model.util.ImageSelection;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.jmolViewer.Jmol3dBoxController;
import de.gnwi.mfsim.model.jmolViewer.setting.JmolGraphicsSettings;
import de.gnwi.mfsim.model.jmolViewer.setting.JmolSlabSettings;
import de.gnwi.mfsim.model.jmolViewer.command.JmolDpdBoxCommands;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.graphics.IImageProvider;

/**
 * Controller class for CustomPanelJmolViewer
 *
 * @author Achim Zielesny
 */
public class CustomPanelJmolViewerController extends ChangeNotifier implements IImageProvider, ChangeListener, PropertyChangeListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * File type of images
     */
    private ImageFileType imageFileType;

    /**
     * Jmol viewer panel
     */
    private CustomPanelJmolViewer jmolViewerPanel;

    /**
     * Jmol viewer panel controller
     */
    private Jmol3dBoxController jmolViewerController;

    /**
     * JMol slab settings
     */
    private JmolSlabSettings jmolSlabSettings;

    /**
     * JMol graphics settings
     */
    private JmolGraphicsSettings jmolGraphicsSettings;

    /**
     * Maximum number of slabs
     */
    private int maximumNumberOfSlabs;

    /**
     * Current box view
     */
    private SimulationBoxViewEnum currentBoxView;

    /**
     * GraphicalParticlePositionInfo instance
     */
    private GraphicalParticlePositionInfo graphicalParticlePositionInfo;

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

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelJmolViewer Panel this controller is made for
     * @param aGraphicalParticlePositionInfo GraphicalParticlePositionInfo instance
     * @param anImageFileType Image file type
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelJmolViewerController(CustomPanelJmolViewer aCustomPanelJmolViewer, GraphicalParticlePositionInfo aGraphicalParticlePositionInfo, ImageFileType anImageFileType)
            throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelJmolViewer == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aGraphicalParticlePositionInfo == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            // NOTE: Command sequence is NOT to be changed
            this.jmolViewerPanel = aCustomPanelJmolViewer;
            this.jmolViewerController = new Jmol3dBoxController(this.jmolViewerPanel.getJmolSimulationBoxPanel().getJmol3dPanel());
            this.imageFileType = anImageFileType;
            this.maximumNumberOfSlabs = 100;
            this.graphicalParticlePositionInfo = aGraphicalParticlePositionInfo;
            // Set virtualSlider BEFORE setting this.setThirdDimensionSlider()
            this.virtualSlider = new VirtualSlider(this.jmolViewerPanel.getJmolSimulationBoxPanel().getThirdDimensionSlider());
            // Set ThirdDimensionSlider AFTER setting virtual slider and this.maximumNumberOfSlabs
            this.setThirdDimensionSlider();
            // Set SimulationBoxViewEnum.XZ_FRONT
            this.updateBoxView(SimulationBoxViewEnum.XZ_FRONT);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Animation settings">
            this.isAnimationPlayingForwards = false;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            // <editor-fold defaultstate="collapsed" desc="- Listeners for graphics panel">
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getSaveGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Stop animation if necessary
                        CustomPanelJmolViewerController.this.stopAnimation();
                        String tmpFilePathname = GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.selectGraphicsFileSaveChooser"), GuiDefinitions.IMAGE_BMP_FILE_EXTENSION);
                        if (tmpFilePathname != null) {
                            try {
                                MouseCursorManagement.getInstance().setWaitCursor();
                                BufferedImage tmpJMolViewerImage = CustomPanelJmolViewerController.this.jmolViewerController.getScreenImage();
                                GraphicsUtils.writeImageToFile(tmpJMolViewerImage, ImageFileType.BMP, new File(tmpFilePathname));
                                MouseCursorManagement.getInstance().setDefaultCursor();
                            } catch (Exception anExeption) {
                                MouseCursorManagement.getInstance().setDefaultCursor();
                                // <editor-fold defaultstate="collapsed" desc="Show error message">
                                JOptionPane.showMessageDialog(null, GuiMessage.get("Error.SaveOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                        JOptionPane.ERROR_MESSAGE);

                                // </editor-fold>
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getCopyGraphicsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        MouseCursorManagement.getInstance().setWaitCursor();
                        // Stop animation if necessary
                        CustomPanelJmolViewerController.this.stopAnimation();
                        BufferedImage tmpJMolViewerImage = CustomPanelJmolViewerController.this.jmolViewerController.getScreenImage();
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        if (!ImageSelection.copyImageToClipboard(tmpJMolViewerImage)) {

                            // <editor-fold defaultstate="collapsed" desc="Show error message">
                            JOptionPane.showMessageDialog(null, GuiMessage.get("Error.CopyOperationFailed"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                    JOptionPane.ERROR_MESSAGE);

                            // </editor-fold>
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        MouseCursorManagement.getInstance().setDefaultCursor();

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getPlayAnimationButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelJmolViewerController.this.playAnimation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getSpinCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        CustomPanelJmolViewerController.this.setSpinningStateOfSimulationBox();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getFrameCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        CustomPanelJmolViewerController.this.setFrameOfSimulationBox();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getJmolSimulationBoxPanel().addMouseWheelListener(new MouseWheelListener() {

                public void mouseWheelMoved(MouseWheelEvent e) {
                    try {
                        if (!CustomPanelJmolViewerController.this.isAnimationPlaying()) {
                            CustomPanelJmolViewerController.this.setThirdDimensionIncrement(e.getWheelRotation());
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseWheelMoved()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listeners for box view panel">
            this.jmolViewerPanel.getXzFrontButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelJmolViewerController.this.stopAnimation();

                        // </editor-fold>
                        CustomPanelJmolViewerController.this.updateBoxView(SimulationBoxViewEnum.XZ_FRONT);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getXzBackButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelJmolViewerController.this.stopAnimation();

                        // </editor-fold>
                        CustomPanelJmolViewerController.this.updateBoxView(SimulationBoxViewEnum.XZ_BACK);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getYzLeftButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelJmolViewerController.this.stopAnimation();

                        // </editor-fold>
                        CustomPanelJmolViewerController.this.updateBoxView(SimulationBoxViewEnum.YZ_LEFT);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getYzRightButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelJmolViewerController.this.stopAnimation();

                        // </editor-fold>
                        CustomPanelJmolViewerController.this.updateBoxView(SimulationBoxViewEnum.YZ_RIGHT);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getXyTopButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelJmolViewerController.this.stopAnimation();

                        // </editor-fold>
                        CustomPanelJmolViewerController.this.updateBoxView(SimulationBoxViewEnum.XY_TOP);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getXyBottomButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelJmolViewerController.this.stopAnimation();

                        // </editor-fold>
                        CustomPanelJmolViewerController.this.updateBoxView(SimulationBoxViewEnum.XY_BOTTOM);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Listeners for settings panel">
            this.jmolViewerPanel.getMoleculesRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getMoleculesRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelJmolViewer.defaultMoleculesButton.toolTipText"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelJmolViewer.defaultMoleculesButton.text"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelJmolViewer.configureMoleculesButton.toolTipText"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelJmolViewer.configureMoleculesButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getGraphicsRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getGraphicsRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelJmolViewer.defaultGraphicsButton.toolTipText"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelJmolViewer.defaultGraphicsButton.text"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelJmolViewer.configureGraphicsButton.toolTipText"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelJmolViewer.configureGraphicsButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getAnimationRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelJmolViewer.defaultAnimationButton.toolTipText"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelJmolViewer.defaultAnimationButton.text"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelJmolViewer.configureAnimationButton.toolTipText"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelJmolViewer.configureAnimationButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getMovieRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setVisible(true);
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setToolTipText(GuiMessage.get("CustomPanelJmolViewer.makeMovieButton.toolTipText"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getFirstSettingsButton().setText(GuiMessage.get("CustomPanelJmolViewer.makeMovieButton.text"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setToolTipText(GuiMessage.get("CustomPanelJmolViewer.configureMovieButton.toolTipText"));
                            CustomPanelJmolViewerController.this.jmolViewerPanel.getSecondSettingsButton().setText(GuiMessage.get("CustomPanelJmolViewer.configureMovieButton.text"));
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getFirstSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelJmolViewerController.this.stopAnimation();

                        // </editor-fold>
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getMoleculesRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.setDefaultMoleculeSettings();
                        }
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getGraphicsRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.setDefaultGraphicsSettings();
                        }
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.setDefaultAnimationSettings();
                        }
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.startMovieCreation();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.jmolViewerPanel.getSecondSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {

                        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
                        CustomPanelJmolViewerController.this.stopAnimation();

                        // </editor-fold>
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getMoleculesRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.configureMoleculeSettings();
                        }
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getGraphicsRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.configureGraphicsSettings();
                        }
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getAnimationRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.configureAnimationSettings();
                        }
                        if (CustomPanelJmolViewerController.this.jmolViewerPanel.getMovieRadioButton().isSelected()) {
                            CustomPanelJmolViewerController.this.configureMovieSettings();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelJmolViewerController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add this instance as change listener to third dimension slider">
            // Add this as change listener to third dimension slider
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getThirdDimensionSlider().addChangeListener(this);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Select molecules radio button">
            this.jmolViewerPanel.getMoleculesRadioButton().setSelected(true);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Jmol viewer initialisation">
            MouseCursorManagement.getInstance().setWaitCursor();

            this.jmolSlabSettings = new JmolSlabSettings();
            this.jmolSlabSettings.setZShadePower(Preferences.getInstance().getJmolShadePower());
            this.jmolViewerController.setSlabSettings(this.jmolSlabSettings);

            this.jmolGraphicsSettings = new JmolGraphicsSettings();
            this.jmolGraphicsSettings.setBackgroundColor(Preferences.getInstance().getJmolSimulationBoxBackgroundColor());
            this.jmolGraphicsSettings.setAmbientPercent(Preferences.getInstance().getJmolAmbientLightPercentage());
            this.jmolGraphicsSettings.setDiffusePercent(Preferences.getInstance().getJmolDiffuseLightPercentage());
            this.jmolGraphicsSettings.setSpecularExponent(Preferences.getInstance().getJmolSpecularReflectionExponent());
            this.jmolGraphicsSettings.setSpecularPercent(Preferences.getInstance().getJmolSpecularReflectionPercentage());
            this.jmolGraphicsSettings.setSpecularPower(Preferences.getInstance().getJmolSpecularReflectionPower());
            this.jmolViewerController.setGraphicSettings(this.jmolGraphicsSettings);

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
            // Set current colors and scaled radii of graphical particles
            this.graphicalParticlePositionInfo.getGraphicalParticleInfo().setCurrentColorAndScaledRadiusOfParticles();
            // Set current graphical particle positions according to settings (colors, exclusions, rotations)
            // Parameter false: Zoom statistics are NOT calculated
            this.graphicalParticlePositionInfo.setCurrentGraphicalParticlePositions(false);
            this.jmolViewerController.showSingleStepBox(this.graphicalParticlePositionInfo.getCurrentGraphicalParticlePositionArrayList().getSizedGraphicalParticlePositions());
            MouseCursorManagement.getInstance().setDefaultCursor();
            // Do NOT Show frame of simulation box
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getFrameCheckBox().setSelected(false);
            // Do NOT spin simulation box
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getSpinCheckBox().setSelected(false);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add this instance as property change listener of Jmol viewer controller">
            this.jmolViewerController.addPropertyChangeListener(this);
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelJmolViewerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- getImage">
    /**
     * Returns image for slab index
     *
     * @param aSlabIndex Slab index
     * @return Image for slab index or null if image is not available
     */
    public BufferedImage getImage(int aSlabIndex) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aSlabIndex < 0 || aSlabIndex > this.maximumNumberOfSlabs) {
            return null;
        }

        // </editor-fold>
        try {
            this.jmolSlabSettings.setSlab(aSlabIndex);
            return this.jmolViewerController.getSlabImageQuiet(this.jmolSlabSettings);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "getImage()", "CustomPanelJmolViewerController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return null;
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
        return this.maximumNumberOfSlabs;
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
            this.virtualSlider.setValueFromSlider();
            this.jmolSlabSettings.setSlab(this.virtualSlider.getValue());
            this.jmolViewerController.setSlabSettings(this.jmolSlabSettings);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "CustomPanelJmolViewerController"),
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
     * @param anEvent PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent anEvent) {
        // <editor-fold defaultstate="collapsed" desc="- Property SLAB_CHANGED">
        if (anEvent.getPropertyName().equals(Jmol3dBoxController.SLAB_CHANGED)) {
            // int tmpOldSlab = ((Integer) anEvent.getOldValue()).intValue();
            int tmpNewSlab = ((Integer) anEvent.getNewValue()).intValue();
            // IMPORTANT: Set value WITHOUT state changed event!
            this.virtualSlider.setValueWithoutStateChangedEvent(tmpNewSlab);
        }
        // </editor-fold>
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Set methods">

    /**
     * Sets spinning state of simulation box according to check box selection
     * status
     */
    private void setSpinningStateOfSimulationBox() {
        if (this.jmolViewerPanel.getJmolSimulationBoxPanel().getSpinCheckBox().isSelected()) {
            this.jmolViewerController.executeCommand(JmolDpdBoxCommands.SpinOn);
        } else {
            this.jmolViewerController.executeCommand(JmolDpdBoxCommands.SpinOff);
        }
    }

    /**
     * Sets frame of simulation box according to check box selection status
     */
    private void setFrameOfSimulationBox() {
        if (this.jmolViewerPanel.getJmolSimulationBoxPanel().getFrameCheckBox().isSelected()) {
            this.jmolViewerController.executeCommand(JmolDpdBoxCommands.ShowBoundbox);
        } else {
            this.jmolViewerController.executeCommand(JmolDpdBoxCommands.HideBoundbox);
        }
    }

    /**
     * Sets third dimension
     *
     * @param anIncrementItemNumber Number of increment items
     */
    private void setThirdDimensionIncrement(int anIncrementItemNumber) {
        int tmpCurrentSliderValue = this.virtualSlider.getValue();
        // NOTE: The "-" is IMPORTANT for "intuitive" scrolling with mouse wheel
        tmpCurrentSliderValue -= anIncrementItemNumber * this.virtualSlider.getMinorTickSpacing();
        this.virtualSlider.setValueWithoutStateChangedEvent(tmpCurrentSliderValue);
        // NOTE: Do not use tmpCurrentSliderValue since this.virtualSlider may have changed this value
        this.jmolSlabSettings.setSlab(this.virtualSlider.getValue());
        this.jmolViewerController.setSlabSettings(this.jmolSlabSettings);
    }

    /**
     * Sets third dimension slider
     */
    private void setThirdDimensionSlider() {
        // JMol allows only 100 slab settings
        this.virtualSlider.setSliderParameters(this.maximumNumberOfSlabs);
        this.virtualSlider.setValue(0);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Configure/default graphics settings">
    /**
     * Configures Jmol viewer graphics settings
     */
    private void configureGraphicsSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelJmolViewerController.this.stopAnimation();

        // </editor-fold>
        ValueItemContainer tmpJmolViewerSettingsValueItemContainer = Preferences.getInstance().getJmolViewerEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesJmolViewerGraphicsSettingsDialog.title"), tmpJmolViewerSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpJmolViewerSettingsValueItemContainer);

            this.jmolSlabSettings.setZShadePower(Preferences.getInstance().getJmolShadePower());
            this.jmolViewerController.setSlabSettings(this.jmolSlabSettings);

            this.jmolGraphicsSettings.setBackgroundColor(Preferences.getInstance().getJmolSimulationBoxBackgroundColor());
            this.jmolGraphicsSettings.setAmbientPercent(Preferences.getInstance().getJmolAmbientLightPercentage());
            this.jmolGraphicsSettings.setDiffusePercent(Preferences.getInstance().getJmolDiffuseLightPercentage());
            this.jmolGraphicsSettings.setSpecularExponent(Preferences.getInstance().getJmolSpecularReflectionExponent());
            this.jmolGraphicsSettings.setSpecularPercent(Preferences.getInstance().getJmolSpecularReflectionPercentage());
            this.jmolGraphicsSettings.setSpecularPower(Preferences.getInstance().getJmolSpecularReflectionPower());
            this.jmolViewerController.setGraphicSettings(this.jmolGraphicsSettings);
            this.setSpinningStateOfSimulationBox();
            this.setFrameOfSimulationBox();
        }
    }

    /**
     * Sets default Jmol viewer graphics settings
     */
    private void setDefaultGraphicsSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelJmolViewerController.this.stopAnimation();

        // </editor-fold>
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setJmolShadePower(Preferences.getInstance().getDefaultJmolShadePower());
            this.jmolSlabSettings.setZShadePower(Preferences.getInstance().getJmolShadePower());
            this.jmolViewerController.setSlabSettings(this.jmolSlabSettings);

            Preferences.getInstance().setJmolSimulationBoxBackgroundColor(Preferences.getInstance().getDefaultJmolSimulationBoxBackgroundColor().toString());
            this.jmolGraphicsSettings.setBackgroundColor(Preferences.getInstance().getJmolSimulationBoxBackgroundColor());
            Preferences.getInstance().setJmolAmbientLightPercentage(Preferences.getInstance().getDefaultJmolAmbientLightPercentage());
            this.jmolGraphicsSettings.setAmbientPercent(Preferences.getInstance().getJmolAmbientLightPercentage());
            Preferences.getInstance().setJmolDiffuseLightPercentage(Preferences.getInstance().getDefaultJmolDiffuseLightPercentage());
            this.jmolGraphicsSettings.setDiffusePercent(Preferences.getInstance().getJmolDiffuseLightPercentage());
            Preferences.getInstance().setJmolSpecularReflectionExponent(Preferences.getInstance().getDefaultJmolSpecularReflectionExponent());
            this.jmolGraphicsSettings.setSpecularExponent(Preferences.getInstance().getJmolSpecularReflectionExponent());
            Preferences.getInstance().setJmolSpecularReflectionPercentage(Preferences.getInstance().getDefaultJmolSpecularReflectionPercentage());
            this.jmolGraphicsSettings.setSpecularPercent(Preferences.getInstance().getJmolSpecularReflectionPercentage());
            Preferences.getInstance().setJmolSpecularReflectionPower(Preferences.getInstance().getDefaultJmolSpecularReflectionPower());
            this.jmolGraphicsSettings.setSpecularPower(Preferences.getInstance().getJmolSpecularReflectionPower());
            this.jmolViewerController.setGraphicSettings(this.jmolGraphicsSettings);
            this.setSpinningStateOfSimulationBox();
            this.setFrameOfSimulationBox();
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Configure/default molecule settings">
    /**
     * Configures bulk/molecule settings
     */
    private void configureMoleculeSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelJmolViewerController.this.stopAnimation();

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
                // Set new colors and scaled radii of graphical particles
                this.graphicalParticlePositionInfo.getGraphicalParticleInfo().setCurrentColorAndScaledRadiusOfParticles();
                // Set current graphical particle positions according to settings (colors, exclusions, rotations)
                // Parameter false: Zoom statistics are NOT calculated
                this.graphicalParticlePositionInfo.setCurrentGraphicalParticlePositions(false);
                this.jmolViewerController.showSingleStepBox(this.graphicalParticlePositionInfo.getCurrentGraphicalParticlePositionArrayList().getSizedGraphicalParticlePositions());
                this.setSpinningStateOfSimulationBox();
                this.setFrameOfSimulationBox();
                MouseCursorManagement.getInstance().setDefaultCursor();
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
        CustomPanelJmolViewerController.this.stopAnimation();

        // </editor-fold>
        try {
            if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
                MouseCursorManagement.getInstance().setWaitCursor();
                Preferences.getInstance().setParticleColorDisplayMode(Preferences.getInstance().getDefaultParticleColorDisplayMode());
                this.graphicalParticlePositionInfo.getGraphicalParticleInfo().clear();
                // Clear value item container for molecule/particles display settings
                Preferences.getInstance().getSimulationMovieSlicerConfiguration().clearMoleculeDisplaySettingsValueItemContainer();
                // Set current colors and scaled radii of graphical particles
                this.graphicalParticlePositionInfo.getGraphicalParticleInfo().setCurrentColorAndScaledRadiusOfParticles();
                // Set current graphical particle positions according to settings (colors, exclusions, rotations)
                // Parameter false: Zoom statistics are NOT calculated
                this.graphicalParticlePositionInfo.setCurrentGraphicalParticlePositions(false);
                this.jmolViewerController.showSingleStepBox(this.graphicalParticlePositionInfo.getCurrentGraphicalParticlePositionArrayList().getSizedGraphicalParticlePositions());
                this.setSpinningStateOfSimulationBox();
                this.setFrameOfSimulationBox();
                MouseCursorManagement.getInstance().setDefaultCursor();
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Configure/default animation settings">
    /**
     * Configures animation settings
     */
    private void configureAnimationSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelJmolViewerController.this.stopAnimation();

        // </editor-fold>
        ValueItemContainer tmpAnimationSettingsValueItemContainer = Preferences.getInstance().getAnimationEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesAnimationSettingsDialog.title"), tmpAnimationSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpAnimationSettingsValueItemContainer);
        }
    }

    /**
     * Sets default animation settings
     */
    private void setDefaultAnimationSettings() {

        // <editor-fold defaultstate="collapsed" desc="Stop animation if necessary">
        CustomPanelJmolViewerController.this.stopAnimation();

        // </editor-fold>
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setAnimationSpeed(Preferences.getInstance().getDefaultAnimationSpeed());
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
        CustomPanelJmolViewerController.this.stopAnimation();

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
        // Suppress spinning of simulation box        
        if (this.jmolViewerPanel.getJmolSimulationBoxPanel().getSpinCheckBox().isSelected()) {
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getSpinCheckBox().setSelected(false);
        }
        // Parameter true: Supply images backwards in addition
        GuiUtils.createMovieImages(this, true, Preferences.getInstance().getImageDirectoryPathForSimulationMovies());
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
                    int tmpSliceIndex = CustomPanelJmolViewerController.this.virtualSlider.getValue() / CustomPanelJmolViewerController.this.virtualSlider.getMinorTickSpacing();
                    if (tmpSliceIndex == CustomPanelJmolViewerController.this.maximumNumberOfSlabs - 1) {
                        CustomPanelJmolViewerController.this.isAnimationPlayingForwards = false;
                    }
                    if (tmpSliceIndex == 0) {
                        CustomPanelJmolViewerController.this.isAnimationPlayingForwards = true;
                    }
                    if (CustomPanelJmolViewerController.this.isAnimationPlayingForwards) {
                        CustomPanelJmolViewerController.this.virtualSlider.setValueWithoutStateChangedEvent(CustomPanelJmolViewerController.this.virtualSlider.getValue()
                                + CustomPanelJmolViewerController.this.virtualSlider.getMinorTickSpacing());
                    } else {
                        CustomPanelJmolViewerController.this.virtualSlider.setValueWithoutStateChangedEvent(CustomPanelJmolViewerController.this.virtualSlider.getValue()
                                - CustomPanelJmolViewerController.this.virtualSlider.getMinorTickSpacing());
                    }
                    CustomPanelJmolViewerController.this.jmolSlabSettings.setSlab(CustomPanelJmolViewerController.this.virtualSlider.getValue());
                    CustomPanelJmolViewerController.this.jmolViewerController.setSlabSettings(CustomPanelJmolViewerController.this.jmolSlabSettings);
                }

            });
            this.animationTimer.start();
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getPlayAnimationButton().setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textStop"));
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText2"));
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
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getPlayAnimationButton().setText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.textPlay"));
            this.jmolViewerPanel.getJmolSimulationBoxPanel().getPlayAnimationButton().setToolTipText(GuiMessage.get("CustomPanelSimulationBoxSlicer.playAnimationButton.toolTipText"));
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
        // Set third dimension slider to 0
        this.virtualSlider.setValue(0);
        this.setSimulationBoxViewImage();
        switch (this.currentBoxView) {
            case XZ_FRONT:
                this.jmolViewerController.executeCommand(JmolDpdBoxCommands.MoveToXZFront);
                break;
            case XZ_BACK:
                this.jmolViewerController.executeCommand(JmolDpdBoxCommands.MoveToXZBack);
                break;
            case YZ_LEFT:
                this.jmolViewerController.executeCommand(JmolDpdBoxCommands.MoveToYZLeft);
                break;
            case YZ_RIGHT:
                this.jmolViewerController.executeCommand(JmolDpdBoxCommands.MoveToYZRight);
                break;
            case XY_TOP:
                this.jmolViewerController.executeCommand(JmolDpdBoxCommands.MoveToXYTop);
                break;
            case XY_BOTTOM:
                this.jmolViewerController.executeCommand(JmolDpdBoxCommands.MoveToXYBottom);
                break;
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
                GuiUtils.setResourceImage(this.jmolViewerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_FRONT_IMAGE_FILENAME);
                break;
            case XZ_BACK:
                GuiUtils.setResourceImage(this.jmolViewerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_BACK_IMAGE_FILENAME);
                break;
            case YZ_LEFT:
                GuiUtils.setResourceImage(this.jmolViewerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_LEFT_IMAGE_FILENAME);
                break;
            case YZ_RIGHT:
                GuiUtils.setResourceImage(this.jmolViewerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_RIGHT_IMAGE_FILENAME);
                break;
            case XY_TOP:
                GuiUtils.setResourceImage(this.jmolViewerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_TOP_IMAGE_FILENAME);
                break;
            case XY_BOTTOM:
                GuiUtils.setResourceImage(this.jmolViewerPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_BOTTOM_IMAGE_FILENAME);
                break;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
