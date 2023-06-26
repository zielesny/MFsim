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

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.control.CustomPanelCompartment;
import de.gnwi.mfsim.gui.control.CustomPanelCompartmentController;
import de.gnwi.mfsim.model.graphics.compartment.CompartmentContainer;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Value item edit dialog
 *
 * @author Achim Zielesny
 */
public class DialogCompartmentEdit extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private CustomPanelCompartment compartmentPanel;
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
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    
    /**
     * True: A change occurred, false: Otherwise
     */
    private boolean hasChanged;

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    
    /**
     * True: Ok-button was used, false: Otherwise. NOTE: Static variable is necessary for dialog result since result is disposed.
     */
    private static boolean resultIsOk;

    /**
     * True: A change occurred, false: Otherwise. NOTE: Static variable is necessary for dialog result since result is disposed.
     */
    private static boolean resultHasChanged;

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000031L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogCompartmentEdit() {
        super();
        // <editor-fold defaultstate="collapsed" desc="Initialisation code">
        
        this.hasChanged = false;

        
        // </editor-fold>
        this.setName(GuiMessage.get("DialogCompartmentEdit.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogCompartmentEditWidth(), Preferences.getInstance().getDialogCompartmentEditHeight());

        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);
            {
                this.compartmentPanel = new CustomPanelCompartment();
                this.mainPanel.add(this.compartmentPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentPanel, -10, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentPanel, 10, SpringLayout.NORTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentPanel, -10, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentPanel, 10, SpringLayout.WEST, this.mainPanel);
            }

            // <editor-fold defaultstate="collapsed" desc="mainTabbedPanel">
            // </editor-fold>
        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CompartmentPanel
     * 
     * @return CompartmentPanel
     */
    public CustomPanelCompartment getCompartmentPanel() {
        return compartmentPanel;
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
        if (aChangeNotifier instanceof CompartmentContainer) {
            this.hasChanged = true;
            if (!this.getApplyButton().isVisible()) {
                this.getApplyButton().setVisible(true);
            }
        }
    }

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    
    /**
     * True: A change occurred, false: Otherwise
     *
     * @return True: A change occurred, false: Otherwise
     */
    public boolean hasChanged() {
        return this.hasChanged;
    }

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static hasChanged() method">
    
    /**
     * Returns if dialog has changed value items
     *
     * @param aTitle Title for dialog
     * @param aCompartmentContainer Compartment container
     * @return true: Value items are changed, false: Otherwise
     */
    public static boolean hasChanged(String aTitle, CompartmentContainer aCompartmentContainer) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aTitle == null || aTitle.isEmpty() || aCompartmentContainer == null) {

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "hasChanged()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return false;
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">
            // <editor-fold defaultstate="collapsed" desc="- Set final variables">
            final DialogCompartmentEdit tmpCompartmentEditDialog = new DialogCompartmentEdit();
            tmpCompartmentEditDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpCompartmentEditDialog);
            final CompartmentContainer tmpCompartmentContainer = aCompartmentContainer;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add tmpCompartmentEditDialog as change receiver">
            aCompartmentContainer.addChangeReceiver(tmpCompartmentEditDialog);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Configure dialog">
            // Set title
            tmpCompartmentEditDialog.setTitle(aTitle);
            // Set Ok-button invisible
            tmpCompartmentEditDialog.getApplyButton().setVisible(false);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Instantiate controller">
            new CustomPanelCompartmentController(tmpCompartmentEditDialog.getCompartmentPanel(), aCompartmentContainer);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpCompartmentEditDialog.addWindowListener(new WindowAdapter() {

                public void windowClosing(final WindowEvent e) {
                    try {
                        if (tmpCompartmentEditDialog.hasChanged()
                                && !GuiUtils.getYesNoDecision(GuiMessage.get("DialogCompartmentEdit.Cancel.Title"), GuiMessage.get("DialogCompartmentEdit.Cancel"))) {
                            return;
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogCompartmentEditHeightWidth(tmpCompartmentEditDialog.getHeight(), tmpCompartmentEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        DialogCompartmentEdit.resultIsOk = false;
                        DialogCompartmentEdit.resultHasChanged = tmpCompartmentEditDialog.hasChanged();

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Remove tmpCompartmentEditDialog as change">
                        // receiver
                        tmpCompartmentContainer.removeSingleChangeReceiver(tmpCompartmentEditDialog);

                        // </editor-fold>
                        tmpCompartmentEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpCompartmentEditDialog.getApplyButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogCompartmentEditHeightWidth(tmpCompartmentEditDialog.getHeight(), tmpCompartmentEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        DialogCompartmentEdit.resultIsOk = true;
                        DialogCompartmentEdit.resultHasChanged = tmpCompartmentEditDialog.hasChanged();

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Remove tmpCompartmentEditDialog as">
                        // change receiver
                        tmpCompartmentContainer.removeSingleChangeReceiver(tmpCompartmentEditDialog);

                        // </editor-fold>
                        tmpCompartmentEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpCompartmentEditDialog.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (tmpCompartmentEditDialog.hasChanged()
                                && !GuiUtils.getYesNoDecision(GuiMessage.get("DialogCompartmentEdit.Cancel.Title"), GuiMessage.get("DialogCompartmentEdit.Cancel"))) {
                            return;
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogCompartmentEditHeightWidth(tmpCompartmentEditDialog.getHeight(), tmpCompartmentEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        DialogCompartmentEdit.resultIsOk = false;
                        DialogCompartmentEdit.resultHasChanged = tmpCompartmentEditDialog.hasChanged();

                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Remove tmpCompartmentEditDialog as">
                        // change receiver
                        tmpCompartmentContainer.removeSingleChangeReceiver(tmpCompartmentEditDialog);

                        // </editor-fold>
                        tmpCompartmentEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpCompartmentEditDialog.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpCompartmentEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpCompartmentEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpCompartmentEditDialog.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpCompartmentEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpCompartmentEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpCompartmentEditDialog.getCenterDialogButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpCompartmentEditDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            tmpCompartmentEditDialog.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpCompartmentEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpCompartmentEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpCompartmentEditDialog.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpCompartmentEditDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">
            GuiUtils.checkDialogSize(tmpCompartmentEditDialog);
            GuiUtils.centerDialogOnScreen(tmpCompartmentEditDialog);
            // Show dialog - Wait
            tmpCompartmentEditDialog.setVisible(true);

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Treatment of dialog result">
            if (!DialogCompartmentEdit.resultIsOk) {

                // <editor-fold defaultstate="collapsed" desc="Cancelled/Closed">
                return false;

                // </editor-fold>
            } else {

                // <editor-fold defaultstate="collapsed" desc="Ok - return if changes occurred">
                return DialogCompartmentEdit.resultHasChanged;

                // </editor-fold>
            }

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "hasChanged()", "DialogCompartmentEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
            return false;
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>

}
