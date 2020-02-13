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
package de.gnwi.mfsim.gui.dialog;

import de.gnwi.mfsim.model.util.ModelUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.control.CustomPanelSingleSlicer;
import de.gnwi.mfsim.gui.control.CustomPanelSingleSlicerController;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.graphics.SimulationBoxViewEnum;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Simulation box slicer show dialog
 *
 * @author Achim Zielesny
 */
public class DialogSingleSlicerShow extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelSingleSlicer customSingleSlicerPanel;
    private SpringLayout mainPanelSpringLayout;
    private JPanel mainPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    
    /**
     * True: Slicer failed internally, false: Otherwise. NOTE: Static variable
     * is necessary for treatment of dialog results since result is disposed.
     */
    private static boolean resulthasFailedInternally;
    /**
     * True: Initial resize of dialog, false: Dialog was already resized
     */
    private static boolean isResize;
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000049L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogSingleSlicerShow() {
        super(false, true);
        this.setName(GuiMessage.get("DialogSlicerShow.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogSingleSlicerShowWidth(), Preferences.getInstance().getDialogSingleSlicerShowHeight());
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);

            // <editor-fold defaultstate="collapsed" desc="customSingleSlicerPanel">
            {
                this.customSingleSlicerPanel = new CustomPanelSingleSlicer();
                this.mainPanel.add(this.customSingleSlicerPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.customSingleSlicerPanel, 0, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.customSingleSlicerPanel, 0, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.customSingleSlicerPanel, 0, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.customSingleSlicerPanel, 0, SpringLayout.NORTH, this.mainPanel);
            }

            // </editor-fold>
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public CustomPanelSingleSlicer getCustomSingleSlicerPanel() {
        return customSingleSlicerPanel;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="ChangeReceiverInterface notifyChange method">
    
    /**
     * Notify method for this instance as a change receiver
     *
     * @param aChangeInfo Change information
     * @param aChangeNotifier Object that notifies change
     */
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        if (aChangeNotifier instanceof CustomPanelSingleSlicerController) {
            if (aChangeInfo.getChangeType() == ChangeTypeEnum.INTERNAL_ERROR) {
                DialogSingleSlicerShow.resulthasFailedInternally = true;
                this.dispose();
            }
        }
    }

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static show() method">
    
    /**
     * Show
     *
     * @param aTitle Title of dialog
     * @param aGraphicalParticlePositionInfo GraphicalParticlePositionInfo
 instance
     * @param aBoxView Simulation box view
     */
    public static void show(String aTitle, GraphicalParticlePositionInfo aGraphicalParticlePositionInfo, SimulationBoxViewEnum aBoxView) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTitle == null || aTitle.isEmpty()) {
            return;
        }
        if (aGraphicalParticlePositionInfo == null) {

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogSingleSlicerShow"), GuiMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">
            // <editor-fold defaultstate="collapsed" desc="- Set static and final variables">
            DialogSingleSlicerShow.resulthasFailedInternally = false;
            DialogSingleSlicerShow.isResize = true;

            final DialogSingleSlicerShow tmpSingleSlicerShowDialog = new DialogSingleSlicerShow();
            tmpSingleSlicerShowDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpSingleSlicerShowDialog);
            // Set wait cursor
            MouseCursorManagement.getInstance().setWaitCursor();
            // Set dialog title
            tmpSingleSlicerShowDialog.setTitle(aTitle);
            // Instantiate CustomPanelSlicerController
            final CustomPanelSingleSlicerController tmpCustomPanelSingleSlicerController = new CustomPanelSingleSlicerController(tmpSingleSlicerShowDialog.getCustomSingleSlicerPanel(),
                    aGraphicalParticlePositionInfo, ImageFileType.JPG, aBoxView);
            // Add tmpSlicerShowDialog as change receiver
            tmpCustomPanelSingleSlicerController.addChangeReceiver(tmpSingleSlicerShowDialog);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpSingleSlicerShowDialog.addWindowListener(new WindowAdapter() {
                public void windowOpened(final WindowEvent e) {
                    // Do nothing!
                }

                public void windowClosing(final WindowEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogSingleSlicerShowHeightWidth(tmpSingleSlicerShowDialog.getHeight(), tmpSingleSlicerShowDialog.getWidth());
                        tmpSingleSlicerShowDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogSingleSlicerShow"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpSingleSlicerShowDialog.addComponentListener(new ComponentAdapter() {
                public void componentResized(final ComponentEvent e) {
                    try {
                        if (DialogSingleSlicerShow.isResize) {
                            DialogSingleSlicerShow.isResize = false;
                            tmpCustomPanelSingleSlicerController.createSlices();
                        } else {
                            tmpCustomPanelSingleSlicerController.setVisibilityOfRedrawButton(true);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "componentResized()", "DialogSingleSlicerShow"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpSingleSlicerShowDialog.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogSingleSlicerShowHeightWidth(tmpSingleSlicerShowDialog.getHeight(), tmpSingleSlicerShowDialog.getWidth());
                        tmpSingleSlicerShowDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogSingleSlicerShow"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpSingleSlicerShowDialog.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        DialogSingleSlicerShow.isResize = true;
                        if (GuiUtils.minimizeDialogSize(tmpSingleSlicerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpSingleSlicerShowDialog);
                        } else {
                            DialogSingleSlicerShow.isResize = false;
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogSingleSlicerShow"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpSingleSlicerShowDialog.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        DialogSingleSlicerShow.isResize = true;
                        if (GuiUtils.maximizeDialogSize(tmpSingleSlicerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpSingleSlicerShowDialog);
                        } else {
                            DialogSingleSlicerShow.isResize = false;
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogSingleSlicerShow"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpSingleSlicerShowDialog.getCenterDialogButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpSingleSlicerShowDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogSingleSlicerShow"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpSingleSlicerShowDialog.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        DialogSingleSlicerShow.isResize = true;
                        if (GuiUtils.setCustomDialogSize(tmpSingleSlicerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpSingleSlicerShowDialog);
                        } else {
                            DialogSingleSlicerShow.isResize = false;
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogSingleSlicerShow"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpSingleSlicerShowDialog.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        DialogSingleSlicerShow.isResize = GuiUtils.setCustomDialogSizePreferences(tmpSingleSlicerShowDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogSingleSlicerShow"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">
            GuiUtils.checkDialogSize(tmpSingleSlicerShowDialog);
            GuiUtils.centerDialogOnScreen(tmpSingleSlicerShowDialog);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // Show dialog - Wait
            tmpSingleSlicerShowDialog.setVisible(true);

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Treatment of dialog result">
            if (DialogSingleSlicerShow.resulthasFailedInternally) {

                // <editor-fold defaultstate="collapsed" desc="Message that internal error occurred">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.InternalError"), "show()", "DialogSingleSlicerShow"), GuiMessage
                        .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
            }

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogSingleSlicerShow"), GuiMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return;
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>

}
