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
import de.gnwi.mfsim.gui.control.CustomPanelStructureEdit;
import de.gnwi.mfsim.gui.control.CustomPanelStructureEditController;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Monomer structure edit dialog
 *
 * @author Achim Zielesny
 */
public class DialogMonomerEdit extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private CustomPanelStructureEdit customPanelStructureEdit;
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
    static final long serialVersionUID = 1000000000000000020L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogMonomerEdit() {
        super();
        // <editor-fold defaultstate="collapsed" desc="Initialisation code">
        

        this.hasChanged = false;

        
        // </editor-fold>
        this.setName(GuiMessage.get("DialogMonomerEdit.Structure.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        this.setSize(Preferences.getInstance().getDialogStructureEditWidth(), Preferences.getInstance().getDialogStructureEditHeight());
        // <editor-fold defaultstate="collapsed" desc="mainPanel">

        {
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new BorderLayout());
            getContentPane().add(this.mainPanel, BorderLayout.CENTER);

            // <editor-fold defaultstate="collapsed" desc="customPanelStructureEdit">

            {
                this.customPanelStructureEdit = new CustomPanelStructureEdit();
                this.mainPanel.add(this.customPanelStructureEdit, BorderLayout.CENTER);
            }

            // </editor-fold>

        }

        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CustomPanelStructureEdit_GS
     * 
     * @return CustomPanelStructureEdit_GS
     */
    public CustomPanelStructureEdit getCustomPanelStructureEdit_GS() {
        return customPanelStructureEdit;
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
        if (aChangeNotifier instanceof CustomPanelStructureEditController) {
            switch (aChangeInfo.getChangeType()) {
                case STRUCTURE_SYNTAX_CORRECT_CHANGE:
                    this.hasChanged = true;
                    if (!this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(true);
                    }
                    break;
                case STRUCTURE_SYNTAX_ERROR_CHANGE:
                    this.hasChanged = true;
                    if (this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(false);
                    }
                    break;
                case STRUCTURE_SYNTAX_NO_CHANGE:
                    this.hasChanged = false;
                    if (this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(false);
                    }
                    break;
            }
        }
    }

    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static edit() method">
    
    /**
     * Edit structure
     *
     * @param aValueItem Value item (may be changed)
     * @param anAvailableParticles Available particles (is NOT allowed to be null)
     * @return True: Value item changed value, false: Otherwise
     */
    public static boolean edit(ValueItem aValueItem, HashMap<String, String> anAvailableParticles) {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aValueItem == null) {
            return false;
        }
        if (anAvailableParticles == null) {
            return false;
        }

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Set static variables">

        DialogMonomerEdit.resultHasChanged = false;

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Set final variables">

        // Value item
        final ValueItem tmpValueItem = aValueItem;
        // Save initial monomer/molecular structure
        final String tmpInitialStructure = aValueItem.getValue();

        // </editor-fold>

        try {

            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">

            // <editor-fold defaultstate="collapsed" desc="- Instantiate dialog">

            final DialogMonomerEdit tmpDialogMonomerEdit = new DialogMonomerEdit();
            tmpDialogMonomerEdit.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpDialogMonomerEdit);
            // Make apply-button invisible
            tmpDialogMonomerEdit.getApplyButton().setVisible(false);
            // Set dialog title
            tmpDialogMonomerEdit.setTitle(GuiMessage.get("DialogMonomerEdit.Monomer.title"));
            // Instantiate CustomPanelStructureEditController: Parameter true: anIsMonomer, Parameter null: anAvailableMonomers
            final CustomPanelStructureEditController tmpCustomPanelStructureEditController_GS = new CustomPanelStructureEditController(tmpDialogMonomerEdit.getCustomPanelStructureEdit_GS(), true,
                    anAvailableParticles, null, tmpInitialStructure);
            // Add tmpDialogMonomerEdit as change receiver
            tmpCustomPanelStructureEditController_GS.addChangeReceiver(tmpDialogMonomerEdit);

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpDialogMonomerEdit.addWindowListener(new WindowAdapter() {
                public void windowClosing(final WindowEvent e) {
                    try {
                        // Confirm cancel operation if structure changed
                        if (tmpDialogMonomerEdit.getCustomPanelStructureEdit_GS().getStructureTextArea().getText().equals(tmpValueItem.getValue())
                                || GuiUtils.getYesNoDecision(GuiMessage.get("DialogMonomerEdit.Cancel.Title"), GuiMessage.get("DialogMonomerEdit.Cancel"))) {
                            // Set dialog size in BasicPreferences
                            Preferences.getInstance().setDialogStructureEditHeightWidth(tmpDialogMonomerEdit.getHeight(), tmpDialogMonomerEdit.getWidth());
                            // Set result before dispose()
                            DialogMonomerEdit.resultHasChanged = false;
                            tmpDialogMonomerEdit.dispose();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogMonomerEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogMonomerEdit.getApplyButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        try {
                            if (tmpValueItem.setValue(tmpDialogMonomerEdit.getCustomPanelStructureEdit_GS().getStructureTextArea().getText())) {
                                tmpValueItem.notifyDependentValueItemsForUpdate();
                            }
                        } catch (Exception anException) {
                            ModelUtils.appendToLogfile(true, anException);

                            // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "edit()", "DialogMonomerEdit"),
                                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                            // </editor-fold>

                            if (tmpValueItem.setValue(tmpInitialStructure)) {
                                tmpValueItem.notifyDependentValueItemsForUpdate();
                            }
                        }
                        // Set previous definition
                        Preferences.getInstance().addPreviousMonomer(tmpValueItem.getValue());
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogStructureEditHeightWidth(tmpDialogMonomerEdit.getHeight(), tmpDialogMonomerEdit.getWidth());

                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">

                        DialogMonomerEdit.resultHasChanged = tmpDialogMonomerEdit.hasChanged;

                        // </editor-fold>

                        tmpDialogMonomerEdit.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMonomerEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogMonomerEdit.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Confirm cancel operation if structure changed
                        if (tmpDialogMonomerEdit.getCustomPanelStructureEdit_GS().getStructureTextArea().getText().equals(tmpValueItem.getValue())
                                || GuiUtils.getYesNoDecision(GuiMessage.get("DialogMonomerEdit.Cancel.Title"), GuiMessage.get("DialogMonomerEdit.Cancel"))) {
                            // Set dialog size in BasicPreferences
                            Preferences.getInstance().setDialogStructureEditHeightWidth(tmpDialogMonomerEdit.getHeight(), tmpDialogMonomerEdit.getWidth());
                            // Set result before dispose()
                            DialogMonomerEdit.resultHasChanged = false;
                            tmpDialogMonomerEdit.dispose();
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMonomerEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogMonomerEdit.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpDialogMonomerEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogMonomerEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMonomerEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogMonomerEdit.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpDialogMonomerEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogMonomerEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMonomerEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogMonomerEdit.getCenterDialogButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpDialogMonomerEdit);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMonomerEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>

                    }
                }
            });
            tmpDialogMonomerEdit.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpDialogMonomerEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogMonomerEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMonomerEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            tmpDialogMonomerEdit.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpDialogMonomerEdit);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">

                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogMonomerEdit"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">

            GuiUtils.checkDialogSize(tmpDialogMonomerEdit);
            GuiUtils.centerDialogOnScreen(tmpDialogMonomerEdit);
            tmpDialogMonomerEdit.setVisible(true);

            // </editor-fold>

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Return dialog result">

            return DialogMonomerEdit.resultHasChanged;

            // </editor-fold>

        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">

            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "edit()", "DialogMonomerEdit"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>

            if (aValueItem.setValue(tmpInitialStructure)) {
                aValueItem.notifyDependentValueItemsForUpdate();
            }
            return false;
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    
    // </editor-fold>

}
