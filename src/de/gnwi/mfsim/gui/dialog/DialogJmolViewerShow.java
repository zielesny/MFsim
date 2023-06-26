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
package de.gnwi.mfsim.gui.dialog;

import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.control.CustomPanelJmolViewer;
import de.gnwi.mfsim.gui.control.CustomPanelJmolViewerController;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Jmol viewer show dialog
 *
 * @author Achim Zielesny
 */
public class DialogJmolViewerShow extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private CustomPanelJmolViewer customJmolViewerPanel;
    /**
     * GUI element
     */
    private SpringLayout mainPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel mainPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    
    /**
     * True: Jmol viewer failed internally, false: Otherwise. NOTE: Static variable is necessary for treatment of dialog results since result is disposed.
     */
    private static boolean resulthasFailedInternally;
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000055L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogJmolViewerShow() {
        super(false, true);
        this.setName(GuiMessage.get("DialogJmolViewerShow.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogSlicerShowWidth(), Preferences.getInstance().getDialogSlicerShowHeight());
        // <editor-fold defaultstate="collapsed" desc="mainPanel">

        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);

            // <editor-fold defaultstate="collapsed" desc="customJmolViewerPanel">

            {
                this.customJmolViewerPanel = new CustomPanelJmolViewer();
                this.mainPanel.add(this.customJmolViewerPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.customJmolViewerPanel, -10, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.customJmolViewerPanel, 10, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.customJmolViewerPanel, -10, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.customJmolViewerPanel, 10, SpringLayout.NORTH, this.mainPanel);
            }

            // </editor-fold>

        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CustomJmolViewerPanel
     * 
     * @return CustomJmolViewerPanel
     */
    public CustomPanelJmolViewer getCustomJmolViewerPanel() {
        return customJmolViewerPanel;
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
        if (aChangeNotifier instanceof CustomPanelJmolViewerController) {
            if (aChangeInfo.getChangeType() == ChangeTypeEnum.INTERNAL_ERROR) {
                DialogJmolViewerShow.resulthasFailedInternally = true;
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
     * @param aGraphicalParticlePositionInfo GraphicalParticlePositionInfo instance
     */
    public static void show(String aTitle, GraphicalParticlePositionInfo aGraphicalParticlePositionInfo) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aTitle == null || aTitle.isEmpty()) {
            return;
        }
        if (aGraphicalParticlePositionInfo == null) {

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">

            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogJmolViewerShow"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>

            return;
        }

        // </editor-fold>

        try {

            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">

            // <editor-fold defaultstate="collapsed" desc="- Set static and final variables">

            DialogJmolViewerShow.resulthasFailedInternally = false;

            final DialogJmolViewerShow tmpJmolViewerShowDialog = new DialogJmolViewerShow();
            tmpJmolViewerShowDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpJmolViewerShowDialog);
            // Set wait cursor
            MouseCursorManagement.getInstance().setWaitCursor();
            // Set dialog title
            tmpJmolViewerShowDialog.setTitle(aTitle);
            // Instantiate CustomPanelJmolViewerController
            final CustomPanelJmolViewerController tmpCustomPanelJmolViewerController = new CustomPanelJmolViewerController(tmpJmolViewerShowDialog.getCustomJmolViewerPanel(),
                    aGraphicalParticlePositionInfo, ImageFileType.JPG);
            // Add tmpJmolViewerShowDialog as change receiver
            tmpCustomPanelJmolViewerController.addChangeReceiver(tmpJmolViewerShowDialog);

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpJmolViewerShowDialog.addWindowListener(new WindowAdapter() {
                public void windowOpened(final WindowEvent e) {
                    // Do nothing!
                }

                public void windowClosing(final WindowEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogSlicerShowHeightWidth(tmpJmolViewerShowDialog.getHeight(), tmpJmolViewerShowDialog.getWidth());
                        tmpJmolViewerShowDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogJmolViewerShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpJmolViewerShowDialog.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogSlicerShowHeightWidth(tmpJmolViewerShowDialog.getHeight(), tmpJmolViewerShowDialog.getWidth());
                        tmpJmolViewerShowDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogJmolViewerShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpJmolViewerShowDialog.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpJmolViewerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpJmolViewerShowDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogJmolViewerShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpJmolViewerShowDialog.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpJmolViewerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpJmolViewerShowDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogJmolViewerShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpJmolViewerShowDialog.getCenterDialogButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpJmolViewerShowDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogJmolViewerShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpJmolViewerShowDialog.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpJmolViewerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpJmolViewerShowDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogJmolViewerShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpJmolViewerShowDialog.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpJmolViewerShowDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogJmolViewerShow"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">

            GuiUtils.checkDialogSize(tmpJmolViewerShowDialog);
            GuiUtils.centerDialogOnScreen(tmpJmolViewerShowDialog);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // Show dialog - Wait
            tmpJmolViewerShowDialog.setVisible(true);

            // </editor-fold>

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Treatment of dialog result">

            if (DialogJmolViewerShow.resulthasFailedInternally) {

                // <editor-fold defaultstate="collapsed" desc="Message that internal error occurred">

                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.InternalError"), "show()", "DialogJmolViewerShow"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>

            }

            // </editor-fold>

        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">

            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogJmolViewerShow"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>

            return;
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>

}
