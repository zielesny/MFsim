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
import de.gnwi.mfsim.gui.control.CustomDialogApplyCancelSize;
import de.gnwi.mfsim.gui.control.CustomPanelValueItemEdit;
import de.gnwi.mfsim.gui.control.CustomPanelValueItemEditController;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Value item edit dialog
 *
 * @author Achim Zielesny
 */
public class DialogValueItemEdit extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private CustomPanelValueItemEdit customValueItemEditPanel;
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
    static final long serialVersionUID = 1000000000000000015L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogValueItemEdit() {
        super();
        // <editor-fold defaultstate="collapsed" desc="Initialisation code">
        

        this.hasChanged = false;

        
        // </editor-fold>
        this.setName(GuiMessage.get("DialogValueItemEdit.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogValueItemEditWidth(), Preferences.getInstance().getDialogValueItemEditHeight());
        // <editor-fold defaultstate="collapsed" desc="mainPanel">

        {
            this.mainPanel = new JPanel();
            this.mainPanelSpringLayout = new SpringLayout();
            this.mainPanel.setLayout(this.mainPanelSpringLayout);
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);

            // <editor-fold defaultstate="collapsed" desc="customValueItemEditPanel">

            {
                this.customValueItemEditPanel = new CustomPanelValueItemEdit();
                final TitledBorder selectedFeaturePanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage
                        .get("DialogValueItemEdit.selectedFeaturePanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null,
                        GuiDefinitions.PANEL_TITLE_COLOR);
                this.customValueItemEditPanel.getSelectedFeaturePanel().setBorder(selectedFeaturePanelTitledBorder); 
                final TitledBorder featureOverviewPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage
                        .get("DialogValueItemEdit.featureOverviewPanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null,
                        GuiDefinitions.PANEL_TITLE_COLOR); 
                this.customValueItemEditPanel.getFeatureOverviewPanel().setBorder(featureOverviewPanelTitledBorder);
                this.mainPanel.add(this.customValueItemEditPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.EAST, this.customValueItemEditPanel, -10, SpringLayout.EAST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.WEST, this.customValueItemEditPanel, 10, SpringLayout.WEST, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.customValueItemEditPanel, -10, SpringLayout.SOUTH, this.mainPanel);
                this.mainPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.customValueItemEditPanel, 10, SpringLayout.NORTH, this.mainPanel);
            }

            // </editor-fold>

        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CustomValueItemEditPanel
     * 
     * @return CustomValueItemEditPanel
     */
    public CustomPanelValueItemEdit getCustomValueItemEditPanel() {
        return customValueItemEditPanel;
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
        switch (aChangeInfo.getChangeType()) {
            case VALUE_ITEM_ACTIVITY_CHANGE:
            case VALUE_ITEM_ERROR_CHANGE:
            case VALUE_ITEM_HINT_CHANGE:
            case VALUE_ITEM_VALUE_CHANGE:
            case VALUE_ITEM_MATRIX_CHANGE:
            case VALUE_ITEM_LOCK_STATUS_CHANGE:
            case VALUE_ITEM_COMPARTMENT_CHANGE:
                this.hasChanged = true;
                if (!this.getApplyButton().isVisible()) {
                    this.getApplyButton().setVisible(true);
                }
                break;
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
    // <editor-fold defaultstate="collapsed" desc="Public static hasChanged() methods">
    
    /**
     * Returns if dialog with hidden value item status selection and removed hint and error tabs has changed value items
     *
     * @param aTitle Title for dialog
     * @param aValueItemContainer Value item container
     * @return true: Value items are changed, false: Otherwise
     */
    public static boolean hasChanged(String aTitle, ValueItemContainer aValueItemContainer) {
        return DialogValueItemEdit.hasChanged(aTitle, aValueItemContainer, false, true, true, true);
    }

    /**
     * Returns if dialog with hidden value item status selection and removed hint and error tabs has changed value items
     *
     * @param aTitle Title for dialog
     * @param aValueItemContainer Value item container
     * @param anIsSchemaManagement True: Schema management is available, false: Schema management not shown/available
     * @return true: Value items are changed, false: Otherwise
     */
    public static boolean hasChanged(String aTitle, ValueItemContainer aValueItemContainer, boolean anIsSchemaManagement) {
        return DialogValueItemEdit.hasChanged(aTitle, aValueItemContainer, false, true, true, anIsSchemaManagement);
    }

    /**
     * Returns if dialog has changed value items
     *
     * @param aTitle Title for dialog
     * @param aValueItemContainer Value item container
     * @param anIsValueItemStatusSelectionShown True: Value items status selections are shown, false: Otherwise
     * @param anIsHintTabRemoved True: Hint tab is removed, false: Hint tab is shown
     * @param anIsErrorTabRemoved True: Error tab is removed, false: Error tab is shown
     * @param anIsSchemaManagement True: Schema management is available, false: Schema management not shown/available
     * @return true: Value items are changed, false: Otherwise
     */
    public static boolean hasChanged(String aTitle, ValueItemContainer aValueItemContainer, boolean anIsValueItemStatusSelectionShown, boolean anIsHintTabRemoved,
            boolean anIsErrorTabRemoved, boolean anIsSchemaManagement) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aTitle == null || aTitle.isEmpty() || aValueItemContainer == null) {

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">

            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "hasChanged()", "DialogValueItemEdit"), GuiMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>

            return false;
        }

        // </editor-fold>

        try {

            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">

            // <editor-fold defaultstate="collapsed" desc="- Set final variables">

            final DialogValueItemEdit tmpValueItemEditDialog = new DialogValueItemEdit();
            tmpValueItemEditDialog.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpValueItemEditDialog);
            final ValueItemContainer tmpValueItemContainer = aValueItemContainer;

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Add tmpValueItemEditDialog as change receiver">

            aValueItemContainer.addChangeReceiver(tmpValueItemEditDialog);

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Configure dialog">

            // Set title
            tmpValueItemEditDialog.setTitle(aTitle);
            // Configure CustomPanelValueItemEdit
            GuiUtils.configureCustomPanelValueItemEdit(tmpValueItemEditDialog.getCustomValueItemEditPanel(), anIsValueItemStatusSelectionShown, anIsErrorTabRemoved, anIsHintTabRemoved,
                    anIsSchemaManagement);
            // Set Apply-button invisible
            tmpValueItemEditDialog.getApplyButton().setVisible(false);

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Instantiate CustomPanelValueItemEditController">

            new CustomPanelValueItemEditController(tmpValueItemEditDialog.getCustomValueItemEditPanel(), aValueItemContainer);

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpValueItemEditDialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(final WindowEvent e) {
                    try {
                        if (tmpValueItemEditDialog.hasChanged()
                                && !GuiUtils.getYesNoDecision(GuiMessage.get("DialogValueItemEdit.Cancel.Title"), GuiMessage.get("DialogValueItemEdit.Cancel"))) {
                            return;
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogValueItemEditHeightWidth(tmpValueItemEditDialog.getHeight(), tmpValueItemEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">

                        DialogValueItemEdit.resultIsOk = false;
                        DialogValueItemEdit.resultHasChanged = tmpValueItemEditDialog.hasChanged();

                        // </editor-fold>

                        // <editor-fold defaultstate="collapsed" desc="Remove tmpValueItemEditDialog as change receiver">

                        tmpValueItemContainer.removeSingleChangeReceiver(tmpValueItemEditDialog);

                        // </editor-fold>

                        tmpValueItemEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogValueItemEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpValueItemEditDialog.getApplyButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogValueItemEditHeightWidth(tmpValueItemEditDialog.getHeight(), tmpValueItemEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">

                        DialogValueItemEdit.resultIsOk = true;
                        DialogValueItemEdit.resultHasChanged = tmpValueItemEditDialog.hasChanged();

                        // </editor-fold>

                        // <editor-fold defaultstate="collapsed" desc="Remove tmpValueItemEditDialog as change">
                        // receiver

                        tmpValueItemContainer.removeSingleChangeReceiver(tmpValueItemEditDialog);

                        // </editor-fold>

                        tmpValueItemEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpValueItemEditDialog.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (tmpValueItemEditDialog.hasChanged()
                                && !GuiUtils.getYesNoDecision(GuiMessage.get("DialogValueItemEdit.Cancel.Title"), GuiMessage.get("DialogValueItemEdit.Cancel"))) {
                            return;
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogValueItemEditHeightWidth(tmpValueItemEditDialog.getHeight(), tmpValueItemEditDialog.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">

                        DialogValueItemEdit.resultIsOk = false;
                        DialogValueItemEdit.resultHasChanged = tmpValueItemEditDialog.hasChanged();

                        // </editor-fold>

                        // <editor-fold defaultstate="collapsed" desc="Remove tmpValueItemEditDialog as change">
                        // receiver

                        tmpValueItemContainer.removeSingleChangeReceiver(tmpValueItemEditDialog);

                        // </editor-fold>

                        tmpValueItemEditDialog.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpValueItemEditDialog.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpValueItemEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpValueItemEditDialog.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpValueItemEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpValueItemEditDialog.getCenterDialogButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpValueItemEditDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpValueItemEditDialog.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpValueItemEditDialog)) {
                            GuiUtils.centerDialogOnScreen(tmpValueItemEditDialog);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }
            });
            tmpValueItemEditDialog.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpValueItemEditDialog);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogValueItemEdit"), GuiMessage
                                .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">

            GuiUtils.checkDialogSize(tmpValueItemEditDialog);
            GuiUtils.centerDialogOnScreen(tmpValueItemEditDialog);
            // Show dialog - Wait
            tmpValueItemEditDialog.setVisible(true);

            // </editor-fold>

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Treatment of dialog result">

            if (!DialogValueItemEdit.resultIsOk) {

                // <editor-fold defaultstate="collapsed" desc="Canceled/Closed">

                return false;

                // </editor-fold>

            } else {

                // <editor-fold defaultstate="collapsed" desc="Ok - return if changes occurred">

                return DialogValueItemEdit.resultHasChanged;

                // </editor-fold>

            }

            // </editor-fold>

        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">

            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "hasChanged()", "DialogValueItemEdit"), GuiMessage
                    .get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>

            return false;
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>

}
