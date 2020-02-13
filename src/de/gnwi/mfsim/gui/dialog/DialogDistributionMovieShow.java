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

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.control.CustomPanelDistributionMovie;
import de.gnwi.mfsim.gui.control.CustomPanelDistributionMovieController;
import de.gnwi.mfsim.model.job.TimeStepInfo;
import de.gnwi.mfsim.model.job.JobResult;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.util.VolumeFrequency;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Dialog to show distribution movies
 * 
 * @author Jan-Mathis Hein
 */
public class DialogDistributionMovieShow extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JPanel mainPanel;
    
    private SpringLayout mainPanelSpringLayout;
    
    private CustomPanelDistributionMovie customPanelDistributionMovie;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * True: The progress of creating the animation failed internally, 
     * false: Otherwise
     */
    private static boolean resultHasFailedInternally;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000067L;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogDistributionMovieShow() {
        // Apply button is not visible, size related buttons are visible
        super(false, true);
        this.setName(GuiMessage.get("DialogDistributionMovieShow.name"));
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        // NOTE: All charting related dialogs share the same preferences for their height and width
        this.setSize(Preferences.getInstance().getDialogValueItemMatrixDiagramWidth(),
            Preferences.getInstance().getDialogValueItemMatrixDiagramHeight()
        );
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            this.getContentPane().add(this.mainPanel, BorderLayout.CENTER);
            {
                this.customPanelDistributionMovie = new CustomPanelDistributionMovie();
                this.mainPanel.add(this.customPanelDistributionMovie);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.customPanelDistributionMovie, -10, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.customPanelDistributionMovie, 10, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.customPanelDistributionMovie, -10, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.customPanelDistributionMovie, 10, SpringLayout.NORTH, this.mainPanel);
            }
        }
        // </editor-fold>
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
    @Override
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        if (aChangeNotifier instanceof CustomPanelDistributionMovieController) {
            if (aChangeInfo.getChangeType() == ChangeTypeEnum.INTERNAL_ERROR) {
                DialogDistributionMovieShow.resultHasFailedInternally = true;
                this.dispose();
            }
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public CustomPanelDistributionMovie getCustomPanelDistributionMovie(){
        return this.customPanelDistributionMovie;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static show() method">
    /**
     * Shows the dialog. Returns if there is an error or a parameter is not valid
     *
     * @param anExecutorService ExecutorService that controls the tasks
     * @param aTitle Title for the dialog
     * @param aTimeStepInfoArray Array with information for the simulation steps
     * @param aJobInputValueItemContainer Value item container with the job 
     * input
     * @param anVolumeAxis Axis along which the distribution is calculated
     * @param aParticleType Type of particle that is examined
     * @param aParticleTypeDescriptionString String describing the particle that
     * is examined
     */
    public static void show(
        ExecutorService anExecutorService, 
        String aTitle, 
        TimeStepInfo[] aTimeStepInfoArray, 
        ValueItemContainer aJobInputValueItemContainer, 
        VolumeFrequency.VolumeAxis anVolumeAxis, 
        JobResult.ParticleType aParticleType, 
        String aParticleTypeDescriptionString
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (
            anExecutorService == null || aTitle == null || aTitle.isEmpty() || aTimeStepInfoArray == null || 
            aJobInputValueItemContainer == null || anVolumeAxis == null || aParticleType == null || 
            aParticleTypeDescriptionString == null || aParticleTypeDescriptionString.isEmpty()
        ) {
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null,
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogDistributionMovieShow"),
                GuiMessage.get("Error.ErrorNotificationTitle"),
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
            return;
        }
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup">
            // <editor-fold defaultstate="collapsed" desc="- Set variables">
            final DialogDistributionMovieShow tmpDialogDistributionMovieShow = new DialogDistributionMovieShow();
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpDialogDistributionMovieShow);
            MouseCursorManagement.getInstance().setWaitCursor();
            DialogDistributionMovieShow.resultHasFailedInternally = false;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set icon image">
            tmpDialogDistributionMovieShow.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set title">
            tmpDialogDistributionMovieShow.setTitle(aTitle);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Initiate CustomPanelDistributionMovieController">
            final CustomPanelDistributionMovieController tmpCustomPanelDistributionMovieController =
                new CustomPanelDistributionMovieController(
                    tmpDialogDistributionMovieShow.getCustomPanelDistributionMovie(),
                    anExecutorService,
                    ImageFileType.JPG,
                    aTimeStepInfoArray, 
                    aJobInputValueItemContainer,
                    anVolumeAxis,
                    aParticleType,
                    aParticleTypeDescriptionString
                );
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpDialogDistributionMovieShow.addWindowListener(new WindowAdapter() {

                public void windowClosing(final WindowEvent e) {
                    try {
                        // Kill all ongoing operations of tmpCustomPanelDistributionMovieController
                        tmpCustomPanelDistributionMovieController.kill();
                        // NOTE: All charting related dialogs share the same preferences for their height and width
                        // Set dialog size in Preferences
                        Preferences.getInstance().setDialogValueItemMatrixDiagramHeightWidth(
                            tmpDialogDistributionMovieShow.getCustomPanelDistributionMovie().getHeight(), 
                            tmpDialogDistributionMovieShow.getCustomPanelDistributionMovie().getWidth()
                        );
                        tmpDialogDistributionMovieShow.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogDistributionMovieShow"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpDialogDistributionMovieShow.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Kill all ongoing operations of tmpCustomPanelDistributionMovieController
                        tmpCustomPanelDistributionMovieController.kill();
                        // NOTE: All charting related dialogs share the same preferences for their height and width
                        // Set dialog size in Preferences
                        Preferences.getInstance().setDialogValueItemMatrixDiagramHeightWidth(
                            tmpDialogDistributionMovieShow.getCustomPanelDistributionMovie().getHeight(), 
                            tmpDialogDistributionMovieShow.getCustomPanelDistributionMovie().getWidth()
                        );
                        tmpDialogDistributionMovieShow.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogDistributionMovieShow"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpDialogDistributionMovieShow.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpDialogDistributionMovieShow)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogDistributionMovieShow);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogDistributionMovieShow"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpDialogDistributionMovieShow.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpDialogDistributionMovieShow)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogDistributionMovieShow);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogDistributionMovieShow"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpDialogDistributionMovieShow.getCenterDialogButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpDialogDistributionMovieShow);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogDistributionMovieShow"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }

            });
            tmpDialogDistributionMovieShow.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpDialogDistributionMovieShow)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogDistributionMovieShow);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogDistributionMovieShow"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            tmpDialogDistributionMovieShow.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpDialogDistributionMovieShow);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogDistributionMovieShow"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
                
            });
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Treatment of dialog result">
            if (DialogDistributionMovieShow.resultHasFailedInternally) {
                // <editor-fold defaultstate="collapsed" desc="Message that internal error occurred">
                JOptionPane.showMessageDialog(null, 
                    String.format(GuiMessage.get("Error.InternalError"), "show()", "DialogDistributionMovieShow"), 
                    GuiMessage.get("Error.ErrorNotificationTitle"), 
                    JOptionPane.ERROR_MESSAGE
                );
                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Show">
            // Check dialog size and center
            GuiUtils.checkDialogSize(tmpDialogDistributionMovieShow);
            GuiUtils.centerDialogOnScreen(tmpDialogDistributionMovieShow);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // Show dialog - Wait
            tmpDialogDistributionMovieShow.setVisible(true);
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, 
                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "show()", "DialogDistributionMovieShow"), 
                GuiMessage.get("Error.ErrorNotificationTitle"), 
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        } finally {
            anExecutorService.shutdownNow();
            MouseCursorManagement.getInstance().setDefaultCursor();
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    // </editor-fold>

}
