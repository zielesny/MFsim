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

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.body.BodyInterface;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Controller class for CustomPanelCompartmentGraphics
 *
 * @author Achim Zielesny
 */
public class CustomPanelCompartmentGraphicsController implements ChangeListener, ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Compartment graphics panel
     */
    private CustomPanelCompartmentGraphics compartmentGraphicsPanel;

    /**
     * Info about coordinate
     */
    private String coordinatesInfo;

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
     * Info about overlap
     */
    private String overlapInfo;

    /**
     * Info about selected value item
     */
    private String selectionInfo;

    /**
     * Info about geometry of compartment
     */
    private String compartmentGeometryInfo;

    /**
     * Compartment container
     */
    private CompartmentContainer compartmentContainer;

    /**
     * True: Mouse is over compartment graphics panel, false: Otherwise
     */
    private boolean isMouseOverCompartmentGraphicsPanel;

    /**
     * x-value of mouse if left mouse-button is pressed
     */
    private int mousePressedX;

    /**
     * y-value of mouse if left mouse-button is pressed
     */
    private int mousePressedY;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelCompartmentGraphics Panel this controller is made for
     * @param aCompartmentContainer Compartment container
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelCompartmentGraphicsController(
        CustomPanelCompartmentGraphics aCustomPanelCompartmentGraphics, 
        CompartmentContainer aCompartmentContainer
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelCompartmentGraphics == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aCompartmentContainer == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            // NOTE: Command sequence must NOT be changed
            this.compartmentGraphicsPanel = aCustomPanelCompartmentGraphics;
            this.compartmentContainer = aCompartmentContainer;
            this.selectionInfo = GuiMessage.get("CompartmentGraphics.NoSelection");
            this.compartmentGeometryInfo = GuiMessage.get("CompartmentGraphics.NoCompartmentGeometryInfo");
            this.overlapInfo = GuiMessage.get("CompartmentGraphics.NoOverlap");
            this.coordinatesInfo = "";
            this.isMouseOverCompartmentGraphicsPanel = false;
            // ThirdDimensionSlider
            int tmpThirdDimensionSliderMaximum = (int) Math.pow(10.0, ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES);
            this.compartmentGraphicsPanel.getThirdDimensionSlider().setMinimum(0);
            this.compartmentGraphicsPanel.getThirdDimensionSlider().setMaximum(tmpThirdDimensionSliderMaximum);
            this.compartmentGraphicsPanel.getThirdDimensionSlider().setMajorTickSpacing(tmpThirdDimensionSliderMaximum / 10);
            this.compartmentGraphicsPanel.getThirdDimensionSlider().setMinorTickSpacing(tmpThirdDimensionSliderMaximum / 50);
            this.compartmentGraphicsPanel.getThirdDimensionSlider().setValue(0);
            // Set Body attenuation option
            this.compartmentGraphicsPanel.getBodyAttenuationRadioButton().setSelected(true);
            // Value slider
            int tmpSliderMaximum = (int) Math.pow(10.0, ModelDefinitions.NUMBER_OF_DECIMALS_FOR_COLOR_PROPERTIES);
            this.compartmentGraphicsPanel.getValueSlider().setMinimum(0);
            this.compartmentGraphicsPanel.getValueSlider().setMaximum(tmpSliderMaximum);
            this.compartmentGraphicsPanel.getValueSlider().setMajorTickSpacing(tmpSliderMaximum / 5);
            this.compartmentGraphicsPanel.getValueSlider().setMinorTickSpacing(tmpSliderMaximum / 10);
            this.compartmentGraphicsPanel.getValueSlider().setValue((int) Math.floor(Preferences.getInstance().getColorGradientAttenuationCompartment() / ModelDefinitions.COLOR_GRADIENT_ATTENUATION_MAXIMUM
                            * (double) this.compartmentGraphicsPanel.getValueSlider().getMaximum()));
            // Set SimulationBoxViewEnum.XZ_FRONT
            this.compartmentGraphicsPanel.getXzFrontRadioButton().setSelected(true);
            this.updateBoxView(SimulationBoxViewEnum.XZ_FRONT);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Initialize compartment graphics panel">
            this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().setCompartmentBox(this.compartmentContainer.getCompartmentBox());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    try {
                        CustomPanelCompartmentGraphicsController.this.mousePressedX = e.getX();
                        CustomPanelCompartmentGraphicsController.this.mousePressedY = e.getY();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mousePressed()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    try {
                        CustomPanelCompartmentGraphicsController.this.isMouseOverCompartmentGraphicsPanel = true;
                        CustomPanelCompartmentGraphicsController.this.setPlaneCoordinates(e);
                        CustomPanelCompartmentGraphicsController.this.showFullCoordinates();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseEntered()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseExited(MouseEvent e) {
                    try {
                        CustomPanelCompartmentGraphicsController.this.isMouseOverCompartmentGraphicsPanel = false;
                        CustomPanelCompartmentGraphicsController.this.showThirdDimensionCoordinates();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String
                                .format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseExited()", "CustomPanelCompartmentGraphicsController"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseClicked(MouseEvent e) {
                    try {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            // Right-Button click
                            CustomPanelCompartmentGraphicsController.this.setPlaneCoordinates(e);
                            CustomPanelCompartmentGraphicsController.this.deselectBody();
                            // NOTE: showFullCoordinates() method is invoked by notifyChange() method
                        } else {
                            if (e.getClickCount() == 1) {
                                // Double click
                                CustomPanelCompartmentGraphicsController.this.setPlaneCoordinates(e);
                                CustomPanelCompartmentGraphicsController.this.selectBody();
                                // NOTE: showFullCoordinates() method is invoked by notifyChange() method
                            }
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseClicked()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().addMouseMotionListener(new MouseMotionAdapter() {

                public void mouseDragged(MouseEvent e) {
                    try {
                        if (e.isControlDown() && SwingUtilities.isLeftMouseButton(e)) { // Control + left mouse button pressed (only x-direction is evaluated)
                            CustomPanelCompartmentGraphicsController.this.setPlaneCoordinates(e);
                            int tmpDriftX = (e.getX() - CustomPanelCompartmentGraphicsController.this.mousePressedX) / 4;
                            if (Math.abs(tmpDriftX) > 0) {
                                CustomPanelCompartmentGraphicsController.this.mousePressedX = e.getX();
                            }
                            int tmpDriftY = 0;
                            CustomPanelCompartmentGraphicsController.this.magnifyBody(tmpDriftX, tmpDriftY);
                            CustomPanelCompartmentGraphicsController.this.showFullCoordinates();
                        } else if (e.isShiftDown() && SwingUtilities.isLeftMouseButton(e)) { // Shift + left mouse button pressed (only y-direction is evaluated)
                            CustomPanelCompartmentGraphicsController.this.setPlaneCoordinates(e);
                            int tmpDriftX = 0;
                            int tmpDriftY = -(e.getY() - CustomPanelCompartmentGraphicsController.this.mousePressedY) / 4;
                            if (Math.abs(tmpDriftY) > 0) {
                                CustomPanelCompartmentGraphicsController.this.mousePressedY = e.getY();
                            }
                            CustomPanelCompartmentGraphicsController.this.magnifyBody(tmpDriftX, tmpDriftY);
                            CustomPanelCompartmentGraphicsController.this.showFullCoordinates();
                        } else if (SwingUtilities.isLeftMouseButton(e)) { // Only Left mouse button pressed
                            CustomPanelCompartmentGraphicsController.this.setPlaneCoordinates(e);
                            CustomPanelCompartmentGraphicsController.this.dragBody();
                            CustomPanelCompartmentGraphicsController.this.showFullCoordinates();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseDragged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void mouseMoved(MouseEvent e) {
                    try {
                        CustomPanelCompartmentGraphicsController.this.setPlaneCoordinates(e);
                        CustomPanelCompartmentGraphicsController.this.showFullCoordinates();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null,
                                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseMoved()", "CustomPanelCompartmentGraphicsController"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().addMouseWheelListener(new MouseWheelListener() {

                public void mouseWheelMoved(MouseWheelEvent e) {
                    CustomPanelCompartmentGraphicsController.this.setThirdDimensionIncrement(e.getWheelRotation());
                    CustomPanelCompartmentGraphicsController.this.showFullCoordinates();
                }

            });
            this.compartmentGraphicsPanel.getXzFrontRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getXzFrontRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.updateBoxView(SimulationBoxViewEnum.XZ_FRONT);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseWheelMoved()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getXzBackRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getXzBackRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.updateBoxView(SimulationBoxViewEnum.XZ_BACK);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getYzLeftRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getYzLeftRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.updateBoxView(SimulationBoxViewEnum.YZ_LEFT);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getYzRightRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getYzRightRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.updateBoxView(SimulationBoxViewEnum.YZ_RIGHT);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getXyTopRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getXyTopRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.updateBoxView(SimulationBoxViewEnum.XY_TOP);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getXyBottomRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getXyBottomRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.updateBoxView(SimulationBoxViewEnum.XY_BOTTOM);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getBodyAttenuationRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getBodyAttenuationRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.setValueSlider();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getFogRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getFogRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.setValueSlider();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getTransparencyRadioButton().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        if (CustomPanelCompartmentGraphicsController.this.compartmentGraphicsPanel.getTransparencyRadioButton().isSelected()) {
                            CustomPanelCompartmentGraphicsController.this.setValueSlider();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getSettingsPanel().addMouseWheelListener(new MouseWheelListener() {

                public void mouseWheelMoved(MouseWheelEvent e) {
                    try {
                        CustomPanelCompartmentGraphicsController.this.setValueSliderIncrement(e.getWheelRotation());
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "mouseWheelMoved()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getValueSlider().addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    try {
                        CustomPanelCompartmentGraphicsController.this.setValueSlider(((JSlider) e.getSource()).getValue());
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getConfigureSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelCompartmentGraphicsController.this.configureSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.compartmentGraphicsPanel.getDefaultSettingsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelCompartmentGraphicsController.this.setDefaultSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelCompartmentGraphicsController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // Add this as change listener to third dimension slider
            this.compartmentGraphicsPanel.getThirdDimensionSlider().addChangeListener(this);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add this as change receiver">
            this.compartmentContainer.getValueItemContainer().addChangeReceiver(this);

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor()", "CustomPanelCompartmentGraphicsController"),
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a changeInformation receiver
     *
     * @param aChangeNotifier Object that notifies changeInformation
     * @param aChangeInfo Change information
     */
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        try {
            if (aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_SELECTED_CHANGE || aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_DESELECTED_CHANGE) {
                this.showInformation();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "notifyChange()", "CustomPanelCompartmentGraphicsController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Shows box view, coordinates and selection information
     */
    public void showInformation() {
        try {
            if (this.compartmentContainer.getCompartmentBox().getSelectedBody() != null) {
                // <editor-fold defaultstate="collapsed" desc="Selected body exists">
                String tmpSelectedBody = String.format(GuiMessage.get("CompartmentGraphics.Selection"), this.compartmentContainer.getCompartmentBox().getSelectedBody()
                        .getGeometryDataValueItem().getNodeNames()[2]);
                String tmpSelectedCoordinates = String.format(GuiMessage.get("CompartmentGraphics.SelectionCoordinates"), // Coordinates
this.stringUtilityMethods.formatDoubleValue(this.compartmentContainer.getCompartmentBox().getSelectedBody().getBodyCenter().getX()
                                * this.compartmentContainer.getLengthConversionFactor(), ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES), // X
this.stringUtilityMethods.formatDoubleValue(this.compartmentContainer.getCompartmentBox().getSelectedBody().getBodyCenter().getY()
                                * this.compartmentContainer.getLengthConversionFactor(), ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES), // Y
this.stringUtilityMethods.formatDoubleValue(this.compartmentContainer.getCompartmentBox().getSelectedBody().getBodyCenter().getZ()
                                * this.compartmentContainer.getLengthConversionFactor(), ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES)); // Z
                this.selectionInfo = String.format(GuiMessage.get("CompartmentGraphics.SelectionInfo"), tmpSelectedBody, tmpSelectedCoordinates);

                switch (this.compartmentContainer.getCompartmentBox().getSelectedBody().getBodyType()) {
                    // <editor-fold defaultstate="collapsed" desc="SPHERE">
                    case SPHERE:
                        String tmpRadiusString = this.compartmentContainer.getCompartmentBox().getSelectedBody().getGeometryDataValueItem().getDisplayValueItem().getValue(0, 3);
                        this.compartmentGeometryInfo = String.format(GuiMessage.get("CompartmentGraphics.SphereCompartmentGeometryInfo"), tmpRadiusString);
                        break;

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="XY_LAYER">
                    case XY_LAYER:
                        String tmpXLengthString = this.compartmentContainer.getCompartmentBox().getSelectedBody().getGeometryDataValueItem().getDisplayValueItem().getValue(0, 3);
                        String tmpYLengthString = this.compartmentContainer.getCompartmentBox().getSelectedBody().getGeometryDataValueItem().getDisplayValueItem().getValue(0, 4);
                        String tmpZLengthString = this.compartmentContainer.getCompartmentBox().getSelectedBody().getGeometryDataValueItem().getDisplayValueItem().getValue(0, 5);
                        this.compartmentGeometryInfo = String.format(GuiMessage.get("CompartmentGraphics.XyLayerCompartmentGeometryInfo"),
                                tmpXLengthString, tmpYLengthString, tmpZLengthString);
                        break;

                    // </editor-fold>
                    default:
                        throw new IllegalArgumentException("Body type is unknown.");
                }

                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="No selected body">
                this.selectionInfo = GuiMessage.get("CompartmentGraphics.NoSelection");
                this.compartmentGeometryInfo = GuiMessage.get("CompartmentGraphics.NoCompartmentGeometryInfo");

                // </editor-fold>
            }
            this.compartmentGraphicsPanel.getCoordinatesLabel().setText(this.coordinatesInfo);
             if (this.compartmentContainer.getCompartmentBox().hasOverlap()) {
                 this.overlapInfo = GuiMessage.get("CompartmentGraphics.Overlap");
             } else {
                 this.overlapInfo = GuiMessage.get("CompartmentGraphics.NoOverlap");
             }
             this.compartmentGraphicsPanel.getInfoLabel().setText(String.format(GuiMessage.get("CompartmentGraphics.InformationWithOverlap"), this.selectionInfo, this.overlapInfo));
            // Alternative without overlap info:
            // this.compartmentGraphicsPanel.getInfoLabel().setText(this.selectionInfo);
            this.compartmentGraphicsPanel.getGeometryInfoLabel().setText(this.compartmentGeometryInfo);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showInformation()", "CustomPanelCompartmentGraphicsController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- ChangeListener stateChanged">
    public void stateChanged(ChangeEvent e) {
        this.setThirdDimension(((JSlider) e.getSource()).getValue());
        this.showThirdDimensionCoordinates();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Show methods">
    /**
     * Shows no coordinates
     */
    private void showThirdDimensionCoordinates() {
        String tmpThirdDimensionString = "";
        switch (this.compartmentContainer.getCompartmentBox().getBoxView()) {
            case XZ_FRONT:
            case XZ_BACK:
                tmpThirdDimensionString = 
                    String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionY"),
                        this.stringUtilityMethods.formatDoubleValue(this.currentY * this.compartmentContainer.getLengthConversionFactor(), 
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                        )
                    );
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                tmpThirdDimensionString = 
                    String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionX"),
                        this.stringUtilityMethods.formatDoubleValue(this.currentX * this.compartmentContainer.getLengthConversionFactor(), 
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                        )
                    );
                break;
            case XY_TOP:
            case XY_BOTTOM:
                tmpThirdDimensionString = 
                    String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionZ"),
                        this.stringUtilityMethods.formatDoubleValue(this.currentZ * this.compartmentContainer.getLengthConversionFactor(), 
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES
                        )
                    );
                break;
        }
        this.coordinatesInfo = 
            String.format(GuiMessage.get("CompartmentGraphics.ThirdDimensionCoordinateCompartment"),
                this.compartmentContainer.getCompartmentBox().getBoxView().toRepresentation(), 
                tmpThirdDimensionString
            );
        this.showInformation();
    }

    /**
     * Shows full coordinates
     */
    private void showFullCoordinates() {

        if (this.isMouseOverCompartmentGraphicsPanel) {
            // <editor-fold defaultstate="collapsed" desc="Mouse is over compartment graphics panel">
            this.coordinatesInfo = String.format(GuiMessage.get("CompartmentGraphics.FullCoordinatesCompartments"), this.compartmentContainer.getCompartmentBox().getBoxView()
                    .toRepresentation(), // Coordinates
this.stringUtilityMethods.formatDoubleValue(this.currentX * this.compartmentContainer.getLengthConversionFactor(),
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES), // x
this.stringUtilityMethods.formatDoubleValue(this.currentY * this.compartmentContainer.getLengthConversionFactor(),
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES), // y
this.stringUtilityMethods.formatDoubleValue(this.currentZ * this.compartmentContainer.getLengthConversionFactor(),
                            ModelDefinitions.NUMBER_OF_DECIMALS_FOR_GRAPHICS_COORDINATES)); // z
            this.showInformation();

            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Mouse is elsewhere">
            this.showThirdDimensionCoordinates();

            // </editor-fold>
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Set/configure methods">
    /**
     * Sets appropriate plane coordinates
     *
     * @param aMouseEvent Mouse event
     */
    private void setPlaneCoordinates(MouseEvent aMouseEvent) {
        switch (this.compartmentContainer.getCompartmentBox().getBoxView()) {
            case XZ_FRONT:
                this.currentX = this.compartmentContainer.getCompartmentBox().getXLength() * (double) aMouseEvent.getX()
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getWidth();
                this.currentZ = this.compartmentContainer.getCompartmentBox().getZLength()
                        * ((double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getHeight() - (double) aMouseEvent.getY())
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getHeight();
                break;
            case XZ_BACK:
                this.currentX = this.compartmentContainer.getCompartmentBox().getXLength()
                        * (1.0 - (double) aMouseEvent.getX() / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getWidth());
                this.currentZ = this.compartmentContainer.getCompartmentBox().getZLength()
                        * ((double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getHeight() - (double) aMouseEvent.getY())
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getHeight();
                break;
            case YZ_LEFT:
                this.currentY = this.compartmentContainer.getCompartmentBox().getYLength()
                        * (1.0 - (double) aMouseEvent.getX() / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getWidth());
                this.currentZ = this.compartmentContainer.getCompartmentBox().getZLength()
                        * ((double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getHeight() - (double) aMouseEvent.getY())
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getHeight();
                break;
            case YZ_RIGHT:
                this.currentY = this.compartmentContainer.getCompartmentBox().getYLength() * (double) aMouseEvent.getX()
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getWidth();
                this.currentZ = this.compartmentContainer.getCompartmentBox().getZLength()
                        * ((double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getHeight() - (double) aMouseEvent.getY())
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getHeight();
                break;
            case XY_TOP:
                this.currentX = this.compartmentContainer.getCompartmentBox().getXLength() * (double) aMouseEvent.getX()
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getWidth();
                this.currentY = this.compartmentContainer.getCompartmentBox().getYLength()
                        * (1.0 - (double) aMouseEvent.getY() / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getWidth());
                break;
            case XY_BOTTOM:
                this.currentX = this.compartmentContainer.getCompartmentBox().getXLength() * (double) aMouseEvent.getX()
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getWidth();
                this.currentY = this.compartmentContainer.getCompartmentBox().getYLength() * (double) aMouseEvent.getY()
                        / (double) this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().getWidth();
                break;
        }
    }

    /**
     * Sets third dimension
     *
     * @param anIncrementItemNumber Number of increment items
     */
    private void setThirdDimensionIncrement(int anIncrementItemNumber) {
        int tmpCurrentSliderValue = this.compartmentGraphicsPanel.getThirdDimensionSlider().getValue();
        // NOTE: The "-" is IMPORTANT for "intuitive" scrolling with mouse wheel
        tmpCurrentSliderValue -= anIncrementItemNumber * this.compartmentGraphicsPanel.getThirdDimensionSlider().getMinorTickSpacing();
        if (tmpCurrentSliderValue < 0) {
            tmpCurrentSliderValue = 0;
        } else if (tmpCurrentSliderValue > this.compartmentGraphicsPanel.getThirdDimensionSlider().getMaximum()) {
            tmpCurrentSliderValue = this.compartmentGraphicsPanel.getThirdDimensionSlider().getMaximum();
        }
        this.compartmentGraphicsPanel.getThirdDimensionSlider().removeChangeListener(this);
        this.compartmentGraphicsPanel.getThirdDimensionSlider().setValue(tmpCurrentSliderValue);
        this.setThirdDimension(tmpCurrentSliderValue);
        this.compartmentGraphicsPanel.getThirdDimensionSlider().addChangeListener(this);
    }

    /**
     * Sets value slider increment
     *
     * @param anIncrementItemNumber Number of increment items
     */
    private void setValueSliderIncrement(int anIncrementItemNumber) {
        int tmpCurrentSliderValue = this.compartmentGraphicsPanel.getValueSlider().getValue();
        // NOTE: The "-" is IMPORTANT for "intuitive" scrolling with mouse wheel
        tmpCurrentSliderValue -= anIncrementItemNumber * this.compartmentGraphicsPanel.getValueSlider().getMinorTickSpacing();
        if (tmpCurrentSliderValue < 0) {
            tmpCurrentSliderValue = 0;
        } else if (tmpCurrentSliderValue > this.compartmentGraphicsPanel.getValueSlider().getMaximum()) {
            tmpCurrentSliderValue = this.compartmentGraphicsPanel.getValueSlider().getMaximum();
        }
        this.compartmentGraphicsPanel.getValueSlider().removeChangeListener(this);
        this.compartmentGraphicsPanel.getValueSlider().setValue(tmpCurrentSliderValue);
        this.setValueSlider(tmpCurrentSliderValue);
        this.compartmentGraphicsPanel.getValueSlider().addChangeListener(this);
    }

    /**
     * Sets third dimension
     *
     * @param aSliderValue Slider value
     */
    private void setThirdDimension(int aSliderValue) {
        double tmpFraction = (double) aSliderValue / (double) this.compartmentGraphicsPanel.getThirdDimensionSlider().getMaximum();
        switch (this.compartmentContainer.getCompartmentBox().getBoxView()) {
            case XZ_FRONT:
                this.currentY = this.compartmentContainer.getCompartmentBox().getYLength() * tmpFraction;
                this.compartmentContainer.getCompartmentBox().setThirdDimensionValue(this.currentY);
                break;
            case XZ_BACK:
                this.currentY = this.compartmentContainer.getCompartmentBox().getYLength() * (1.0 - tmpFraction);
                this.compartmentContainer.getCompartmentBox().setThirdDimensionValue(this.currentY);
                break;
            case YZ_LEFT:
                this.currentX = this.compartmentContainer.getCompartmentBox().getXLength() * tmpFraction;
                this.compartmentContainer.getCompartmentBox().setThirdDimensionValue(this.currentX);
                break;
            case YZ_RIGHT:
                this.currentX = this.compartmentContainer.getCompartmentBox().getXLength() * (1.0 - tmpFraction);
                this.compartmentContainer.getCompartmentBox().setThirdDimensionValue(this.currentX);
                break;
            case XY_TOP:
                this.currentZ = this.compartmentContainer.getCompartmentBox().getZLength() * (1.0 - tmpFraction);
                this.compartmentContainer.getCompartmentBox().setThirdDimensionValue(this.currentZ);
                break;
            case XY_BOTTOM:
                this.currentZ = this.compartmentContainer.getCompartmentBox().getZLength() * tmpFraction;
                this.compartmentContainer.getCompartmentBox().setThirdDimensionValue(this.currentZ);
                break;
        }
    }

    /**
     * Sets value slider
     */
    private void setValueSlider() {

        // <editor-fold defaultstate="collapsed" desc="getBodyAttenuationRadioButton()">
        if (this.compartmentGraphicsPanel.getBodyAttenuationRadioButton().isSelected()) {
            this.compartmentGraphicsPanel.getValueSlider().setValue((int) Math.floor(Preferences.getInstance().getColorGradientAttenuationCompartment() / ModelDefinitions.COLOR_GRADIENT_ATTENUATION_MAXIMUM
                            * (double) this.compartmentGraphicsPanel.getValueSlider().getMaximum()));
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="getFogRadioButton()">
        if (this.compartmentGraphicsPanel.getFogRadioButton().isSelected()) {
            this.compartmentGraphicsPanel.getValueSlider().setValue((int) Math.floor(Preferences.getInstance().getDepthAttenuationCompartment() / ModelDefinitions.COLOR_SHAPE_ATTENUATION_COMPARTMENT_MAXIMUM
                            * (double) this.compartmentGraphicsPanel.getValueSlider().getMaximum()));
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="getTransparencyRadioButton()">
        if (this.compartmentGraphicsPanel.getTransparencyRadioButton().isSelected()) {
            this.compartmentGraphicsPanel.getValueSlider().setValue((int) Math.floor((double) Preferences.getInstance().getColorTransparencyCompartment() / (double) ModelDefinitions.COLOR_TRANSPARENCY_MAXIMUM
                            * (double) this.compartmentGraphicsPanel.getValueSlider().getMaximum()));
        }

        // </editor-fold>
    }

    /**
     * Sets value slider
     *
     * @param aSliderValue Slider value
     */
    private void setValueSlider(int aSliderValue) {
        double tmpFraction = (double) aSliderValue / (double) this.compartmentGraphicsPanel.getValueSlider().getMaximum();
        // <editor-fold defaultstate="collapsed" desc="getBodyAttenuationRadioButton()">
        if (this.compartmentGraphicsPanel.getBodyAttenuationRadioButton().isSelected()) {
            Preferences.getInstance().setColorGradientAttenuationCompartment(tmpFraction * ModelDefinitions.COLOR_GRADIENT_ATTENUATION_MAXIMUM);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="getFogRadioButton()">
        if (this.compartmentGraphicsPanel.getFogRadioButton().isSelected()) {
            Preferences.getInstance().setDepthAttenuationCompartment(tmpFraction * ModelDefinitions.COLOR_SHAPE_ATTENUATION_COMPARTMENT_MAXIMUM);
            this.compartmentContainer.getCompartmentBox().setColorShapeAttenuation();
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="getTransparencyRadioButton()">
        if (this.compartmentGraphicsPanel.getTransparencyRadioButton().isSelected()) {
            Preferences.getInstance().setColorTransparencyCompartment((float) tmpFraction * ModelDefinitions.COLOR_TRANSPARENCY_MAXIMUM);
        }
        // </editor-fold>
        this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().repaint();
    }

    /**
     * Sets graphics parameters
     */
    private void setGraphicsParameters() {
        switch (this.compartmentContainer.getCompartmentBox().getBoxView()) {
            case XZ_FRONT:
            case XZ_BACK:
                this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().setRatioOfHeightToWidth(
                        this.compartmentContainer.getCompartmentBox().getZLength() / this.compartmentContainer.getCompartmentBox().getXLength());
                break;
            case YZ_LEFT:
            case YZ_RIGHT:
                this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().setRatioOfHeightToWidth(
                        this.compartmentContainer.getCompartmentBox().getZLength() / this.compartmentContainer.getCompartmentBox().getYLength());
                break;
            case XY_TOP:
            case XY_BOTTOM:
                this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().setRatioOfHeightToWidth(
                        this.compartmentContainer.getCompartmentBox().getYLength() / this.compartmentContainer.getCompartmentBox().getXLength());
                break;
        }
    }

    /**
     * Sets default compartment graphics settings
     */
    private void setDefaultSettings() {
        if (GuiUtils.getYesNoDecision(GuiMessage.get("Preferences.Default.Title"), GuiMessage.get("Preferences.Default"))) {
            Preferences.getInstance().setDepthAttenuationCompartment(Preferences.getInstance().getDefaultDepthAttenuationCompartment());
            Preferences.getInstance().setColorGradientAttenuationCompartment(Preferences.getInstance().getDefaultColorGradientAttenuationCompartment());
            Preferences.getInstance().setColorTransparencyCompartment(Preferences.getInstance().getDefaultColorTransparencyCompartment());
            // <editor-fold defaultstate="collapsed" desc="Set slider">
            this.setValueSlider();

            // </editor-fold>
            this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().repaint();
        }
    }

    /**
     * Configures compartment graphics settings
     */
    private void configureSettings() {
        ValueItemContainer tmpCompartmentGraphicsSettingsValueItemContainer = Preferences.getInstance().getCompartmentGraphicsSettingsEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesCompartmentGraphicsSettingsDialog.title"), tmpCompartmentGraphicsSettingsValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpCompartmentGraphicsSettingsValueItemContainer);

            // <editor-fold defaultstate="collapsed" desc="Set slider">
            this.setValueSlider();

            // </editor-fold>
            this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().repaint();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Selection related methods">
    /**
     * Deselects body
     */
    private void deselectBody() {
        this.compartmentContainer.getCompartmentBox().deselectBody();
    }

    /**
     * Selects body
     */
    private void selectBody() {
        this.compartmentContainer.getCompartmentBox().selectBody(this.currentX, this.currentY, this.currentZ);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Drag and magnify body methods">
    /**
     * Drags body
     */
    private void dragBody() {
        BodyInterface tmpBody = CustomPanelCompartmentGraphicsController.this.compartmentContainer.getCompartmentBox().getSelectedBody();
        if (tmpBody != null) {
            switch (this.compartmentContainer.getCompartmentBox().getBoxView()) {
                case XZ_FRONT:
                case XZ_BACK:
                    this.compartmentContainer.getCompartmentBox().correctOutOfBox(this.currentX, tmpBody.getBodyCenter().getY(), this.currentZ, tmpBody);
                    break;
                case YZ_LEFT:
                case YZ_RIGHT:
                    this.compartmentContainer.getCompartmentBox().correctOutOfBox(tmpBody.getBodyCenter().getX(), this.currentY, this.currentZ, tmpBody);
                    break;
                case XY_TOP:
                case XY_BOTTOM:
                    this.compartmentContainer.getCompartmentBox().correctOutOfBox(this.currentX, this.currentY, tmpBody.getBodyCenter().getZ(), tmpBody);
                    break;
            }
            this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().repaint();
        }
    }

    /**
     * Magnifies body
     *
     * @param aXPercentage Magnification percentage in X direction
     * @param aYPercentage Magnification percentage in Y direction
     */
    private void magnifyBody(int aXPercentage, int aYPercentage) {
        double tmpOffsetX;
        double tmpOffsetY;
        BodyInterface tmpBody = CustomPanelCompartmentGraphicsController.this.compartmentContainer.getCompartmentBox().getSelectedBody();
        if (tmpBody != null) {
            switch (this.compartmentContainer.getCompartmentBox().getBoxView()) {
                case XZ_FRONT:
                case XZ_BACK:
                    tmpOffsetX = this.compartmentContainer.getCompartmentBox().getBoxSizeInfo().getXLength() * (double) aXPercentage * Preferences.getInstance().getCompartmentBodyChangeResponseFactor();
                    tmpOffsetY = this.compartmentContainer.getCompartmentBox().getBoxSizeInfo().getZLength() * (double) aYPercentage * Preferences.getInstance().getCompartmentBodyChangeResponseFactor();
                    this.compartmentContainer.getCompartmentBox().magnifyBody(tmpBody, tmpOffsetX, 0.0, tmpOffsetY, tmpOffsetY);
                    break;
                case YZ_LEFT:
                case YZ_RIGHT:
                    tmpOffsetX = this.compartmentContainer.getCompartmentBox().getBoxSizeInfo().getYLength() * (double) aXPercentage * Preferences.getInstance().getCompartmentBodyChangeResponseFactor();
                    tmpOffsetY = this.compartmentContainer.getCompartmentBox().getBoxSizeInfo().getZLength() * (double) aYPercentage * Preferences.getInstance().getCompartmentBodyChangeResponseFactor();
                    this.compartmentContainer.getCompartmentBox().magnifyBody(tmpBody, 0.0, tmpOffsetX, tmpOffsetY, tmpOffsetY);
                    break;
                case XY_TOP:
                case XY_BOTTOM:
                    tmpOffsetX = this.compartmentContainer.getCompartmentBox().getBoxSizeInfo().getXLength() * (double) aXPercentage * Preferences.getInstance().getCompartmentBodyChangeResponseFactor();
                    tmpOffsetY = this.compartmentContainer.getCompartmentBox().getBoxSizeInfo().getYLength() * (double) aYPercentage * Preferences.getInstance().getCompartmentBodyChangeResponseFactor();
                    this.compartmentContainer.getCompartmentBox().magnifyBody(tmpBody, tmpOffsetX, tmpOffsetY, 0.0, tmpOffsetY);
                    break;
            }
            this.compartmentGraphicsPanel.getSimulationBoxDrawPanel().getGraphicsPanel().repaint();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update methods">
    /**
     * Updates with new box view
     *
     * @param aBoxView Box view
     */
    private void updateBoxView(SimulationBoxViewEnum aBoxView) {
        this.compartmentContainer.getCompartmentBox().setBoxView(aBoxView);
        this.setGraphicsParameters();
        this.setThirdDimension(this.compartmentGraphicsPanel.getThirdDimensionSlider().getValue());
        this.showThirdDimensionCoordinates();
        this.setSimulationBoxViewImage();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Resource image related methods">
    /**
     * Sets compartments image
     */
    private void setSimulationBoxViewImage() {
        switch (this.compartmentContainer.getCompartmentBox().getBoxView()) {
            case XZ_FRONT:
                GuiUtils.setResourceImage(this.compartmentGraphicsPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_FRONT_IMAGE_FILENAME);
                break;
            case XZ_BACK:
                GuiUtils.setResourceImage(this.compartmentGraphicsPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_BACK_IMAGE_FILENAME);
                break;
            case YZ_LEFT:
                GuiUtils.setResourceImage(this.compartmentGraphicsPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_LEFT_IMAGE_FILENAME);
                break;
            case YZ_RIGHT:
                GuiUtils.setResourceImage(this.compartmentGraphicsPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_RIGHT_IMAGE_FILENAME);
                break;
            case XY_TOP:
                GuiUtils.setResourceImage(this.compartmentGraphicsPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_TOP_IMAGE_FILENAME);
                break;
            case XY_BOTTOM:
                GuiUtils.setResourceImage(this.compartmentGraphicsPanel.getBoxViewImagePanel(), GuiDefinitions.BOX_VIEW_BOTTOM_IMAGE_FILENAME);
                break;
        }
    }
    // </editor-fold>
    // </editor-fold>

}
