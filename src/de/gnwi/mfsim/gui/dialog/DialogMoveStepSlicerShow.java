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
import de.gnwi.mfsim.gui.control.CustomPanelMoveStepSlicerController;
import de.gnwi.mfsim.gui.control.CustomPanelStepSlicer;
import de.gnwi.mfsim.model.graphics.BoxSizeInfo;
import de.gnwi.mfsim.model.graphics.particle.GraphicalParticlePositionInfo;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.SimulationBoxChangeInfo;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Simulation box move step slicer show dialog
 *
 * @author Achim Zielesny
 */
public class DialogMoveStepSlicerShow extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private CustomPanelStepSlicer customStepSlicerPanel;
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
    static final long serialVersionUID = 1000000000000000066L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogMoveStepSlicerShow() {
        super(false, true);
        this.setName(GuiMessage.get("DialogMoveStepSlicerShow.name")); 
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

            // <editor-fold defaultstate="collapsed" desc="customSlicerPanel">
            {
                this.customStepSlicerPanel = new CustomPanelStepSlicer();
                this.mainPanel.add(this.customStepSlicerPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.customStepSlicerPanel, -10, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.customStepSlicerPanel, 10, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.customStepSlicerPanel, -10, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.customStepSlicerPanel, 10, SpringLayout.NORTH, this.mainPanel);
            }

            // </editor-fold>
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CustomStepSlicerPanel
     * 
     * @return CustomStepSlicerPanel
     */
    public CustomPanelStepSlicer getCustomStepSlicerPanel() {
        return customStepSlicerPanel;
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
        if (aChangeNotifier instanceof CustomPanelMoveStepSlicerController) {
            if (aChangeInfo.getChangeType() == ChangeTypeEnum.INTERNAL_ERROR) {
                DialogMoveStepSlicerShow.resulthasFailedInternally = true;
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
     * @param anExecutorService Executor service
     * @param aTitle Title of dialog
     * @param aGraphicalParticlePositionInfo GraphicalParticlePositionInfo
     * @param anEnlargedBoxSizeInfo Enlarged box size instance
     * @param aNumberOfBoxMoveSteps Number of box move steps
     * @param aBoxMoveChangeInfo Box move info
     */
    public static void show(
        ExecutorService anExecutorService,
        String aTitle,
        GraphicalParticlePositionInfo aGraphicalParticlePositionInfo,
        BoxSizeInfo anEnlargedBoxSizeInfo,
        int aNumberOfBoxMoveSteps,
        SimulationBoxChangeInfo aBoxMoveChangeInfo
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anExecutorService == null) {
            return;
        }
        if (aTitle == null || aTitle.isEmpty()) {
            return;
        }
        if (aGraphicalParticlePositionInfo == null || anEnlargedBoxSizeInfo == null) {
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogMoveStepSlicerShow"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">
            // <editor-fold defaultstate="collapsed" desc="- Set static and final variables">
            DialogMoveStepSlicerShow.resulthasFailedInternally = false;
            DialogMoveStepSlicerShow.isResize = true;

            final DialogMoveStepSlicerShow tmpMoveStepSlicerShowDialog = new DialogMoveStepSlicerShow();
            tmpMoveStepSlicerShowDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpMoveStepSlicerShowDialog);
            // Set wait cursor
            MouseCursorManagement.getInstance().setWaitCursor();
            // Set dialog title
            tmpMoveStepSlicerShowDialog.setTitle(aTitle);
            // Instantiate CustomPanelMoveStepSlicerController
            final CustomPanelMoveStepSlicerController tmpCustomPanelMoveStepSlicerController = 
                new CustomPanelMoveStepSlicerController(
                    anExecutorService,
                    tmpMoveStepSlicerShowDialog.getCustomStepSlicerPanel(),
                    aGraphicalParticlePositionInfo,
                    anEnlargedBoxSizeInfo,
                    aNumberOfBoxMoveSteps,
                    aBoxMoveChangeInfo,
                    ImageFileType.JPG
                );
            // Add tmpSlicerShowDialog as change receiver
            tmpCustomPanelMoveStepSlicerController.addChangeReceiver(tmpMoveStepSlicerShowDialog);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpMoveStepSlicerShowDialog.addWindowListener(new WindowAdapter() {
                public void windowOpened(final WindowEvent e) {
                    tmpCustomPanelMoveStepSlicerController.startSlicers();
                }

                public void windowClosing(final WindowEvent e) {
                    try {
                        // Kill all ongoing operations of tmpCustomPanelMoveStepSlicerController
                        tmpCustomPanelMoveStepSlicerController.kill();
                        tmpMoveStepSlicerShowDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpMoveStepSlicerShowDialog.addComponentListener(new ComponentAdapter() {
                public void componentResized(final ComponentEvent e) {
                    try {
                        if (DialogMoveStepSlicerShow.isResize) {
                            DialogMoveStepSlicerShow.isResize = false;
                            tmpCustomPanelMoveStepSlicerController.startSlicers();
                        } else {
                            tmpCustomPanelMoveStepSlicerController.setVisibilityOfRedrawButton(true);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "componentResized()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpMoveStepSlicerShowDialog.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Kill all ongoing operations of tmpCustomPanelMoveStepSlicerController
                        tmpCustomPanelMoveStepSlicerController.kill();
                        // Set dialog size in BasicPreferences
                        tmpMoveStepSlicerShowDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpMoveStepSlicerShowDialog.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        DialogMoveStepSlicerShow.isResize = true;
                        if (GuiUtils.minimizeDialogSize(tmpMoveStepSlicerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpMoveStepSlicerShowDialog);
                        } else {
                            DialogMoveStepSlicerShow.isResize = false;
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpMoveStepSlicerShowDialog.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        DialogMoveStepSlicerShow.isResize = true;
                        if (GuiUtils.maximizeDialogSize(tmpMoveStepSlicerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpMoveStepSlicerShowDialog);
                        } else {
                            DialogMoveStepSlicerShow.isResize = false;
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpMoveStepSlicerShowDialog.getCenterDialogButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpMoveStepSlicerShowDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpMoveStepSlicerShowDialog.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        DialogMoveStepSlicerShow.isResize = true;
                        if (GuiUtils.setCustomDialogSize(tmpMoveStepSlicerShowDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpMoveStepSlicerShowDialog);
                        } else {
                            DialogMoveStepSlicerShow.isResize = false;
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpMoveStepSlicerShowDialog.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        DialogMoveStepSlicerShow.isResize = GuiUtils.setCustomDialogSizePreferences(tmpMoveStepSlicerShowDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">
            GuiUtils.checkDialogSize(tmpMoveStepSlicerShowDialog);
            GuiUtils.centerDialogOnScreen(tmpMoveStepSlicerShowDialog);
            MouseCursorManagement.getInstance().setDefaultCursor();

            // Show dialog - Wait
            tmpMoveStepSlicerShowDialog.setVisible(true);

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Treatment of dialog result">
            if (DialogMoveStepSlicerShow.resulthasFailedInternally) {

                // <editor-fold defaultstate="collapsed" desc="Message that internal error occurred">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.InternalError"), "show()", "DialogMoveStepSlicerShow"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
            }

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogMoveStepSlicerShow"),
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
