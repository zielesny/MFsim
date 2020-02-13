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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.util.ProgressTaskInterface;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Progress dialog
 *
 * @author Achim Zielesny
 */
public class DialogProgress extends CustomDialogApplyCancelSize {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JProgressBar progressBar;

    private SpringLayout mainPanelSpringLayout;

    private JPanel mainPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * True: Cancel-button was used or dialog was closed, false: Otherwise. NOTE: Static variable is necessary for dialog result since result is disposed.
     */
    private static boolean resultIsCancelled;

    /**
     * True: Progress process failed internally, false: Otherwise. NOTE: Static variable is necessary for treatment of dialog results since result is disposed.
     */
    private static boolean resulthasFailedInternally;
    
    /**
     * Executor service for progress tasks
     */
    private static ExecutorService progressExecutorService;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">

    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000042L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogProgress() {
        super(false, false);
        this.setName(GuiMessage.get("DialogProgress.name")); 
        this.setModal(true);
        this.setResizable(false);
        this.setSize(500, 150);
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);
            {
                this.progressBar = new JProgressBar();
                this.progressBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
                this.progressBar.setStringPainted(true);
                this.mainPanel.add(this.progressBar);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.progressBar, -10, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.progressBar, 10, SpringLayout.NORTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.progressBar, -10, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.progressBar, 10, SpringLayout.WEST, this.mainPanel);
            }
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public JProgressBar getProgressBar() {
        return progressBar;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static hasCanceled() method">
    /**
     * Returns if dialog was cancelled
     *
     * @param aTitle Title for dialog
     * @param aProgressTask Task that allows progress control
     * @return true: Dialog was cancelled, false: Otherwise
     */
    public static synchronized boolean hasCanceled(String aTitle, ProgressTaskInterface aProgressTask) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTitle == null || aTitle.isEmpty() || aProgressTask == null) {

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "hasCanceled()",
                    "DialogProgress"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return false;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">
            // <editor-fold defaultstate="collapsed" desc="- Set final variables">
            final DialogProgress tmpProgressDialog = new DialogProgress();
            tmpProgressDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpProgressDialog);
            final ProgressTaskInterface tmpProgressTask = aProgressTask;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Configure dialog">
            // Set title
            tmpProgressDialog.setTitle(aTitle);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpProgressDialog.addWindowListener(new WindowAdapter() {

                public void windowClosing(final WindowEvent e) {
                    try {
                        // <editor-fold defaultstate="collapsed" desc="Stop progress">
                        tmpProgressTask.stop();

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        DialogProgress.resultIsCancelled = true;
                        DialogProgress.resulthasFailedInternally = false;

                        // </editor-fold>
                        tmpProgressDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()",
                                "DialogProgress"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpProgressDialog.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // <editor-fold defaultstate="collapsed" desc="Stop progress">
                        tmpProgressTask.stop();

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        DialogProgress.resultIsCancelled = true;
                        DialogProgress.resulthasFailedInternally = false;

                        // </editor-fold>
                        tmpProgressDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"),
                                "actionPerformed()", "DialogProgress"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpProgressTask.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        // <editor-fold defaultstate="collapsed" desc="Property change due to progress">
                        if (evt.getPropertyName() == ModelDefinitions.PROPERTY_CHANGE_PROGRESS) {
                            int tmpProgressValue = (Integer) evt.getNewValue();
                            tmpProgressDialog.getProgressBar().setValue(tmpProgressValue);

                            // <editor-fold defaultstate="collapsed" desc="Progress 100%">
                            if (tmpProgressValue == 100) {
                                // Remove this instance as propertyChangeListener
                                ProgressTaskInterface tmpProgressTask = (ProgressTaskInterface) evt.getSource();
                                tmpProgressTask.removePropertyChangeListener(this);

                                // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                                DialogProgress.resultIsCancelled = false;
                                DialogProgress.resulthasFailedInternally = false;

                                // </editor-fold>
                                tmpProgressDialog.dispose();
                            }

                            // </editor-fold>
                        }

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Property change due to internal error">
                        if (evt.getPropertyName() == ModelDefinitions.PROPERTY_CHANGE_ERROR) {
                            // Remove this instance as propertyChangeListener
                            ProgressTaskInterface tmpProgressTask = (ProgressTaskInterface) evt.getSource();
                            tmpProgressTask.removePropertyChangeListener(this);

                            // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                            DialogProgress.resultIsCancelled = true;
                            DialogProgress.resulthasFailedInternally = true;

                            // </editor-fold>
                            tmpProgressDialog.dispose();
                        }

                        // </editor-fold>
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "propertyChange()",
                                "DialogProgress"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Start task">
            if (DialogProgress.progressExecutorService == null) {
                DialogProgress.progressExecutorService = Executors.newSingleThreadExecutor();
            }
            DialogProgress.progressExecutorService.submit(tmpProgressTask);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">
            GuiUtils.centerDialogOnScreen(tmpProgressDialog);
            // Show dialog - Wait
            tmpProgressDialog.setVisible(true);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Treatment of dialog result">
            if (DialogProgress.resulthasFailedInternally) {
                // <editor-fold defaultstate="collapsed" desc="Message that progress process failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.InternalError"), "hasCanceled()", "DialogProgress"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
            }
            // Return result
            return DialogProgress.resultIsCancelled;
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "hasCanceled()",
                    "DialogProgress"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return true;
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    // </editor-fold>

}
