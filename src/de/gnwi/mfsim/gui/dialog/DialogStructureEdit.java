/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2021  Achim Zielesny (achim.zielesny@googlemail.com)
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
import de.gnwi.mfsim.gui.control.CustomPanelProteinEdit;
import de.gnwi.mfsim.gui.control.CustomPanelProteinEditController;
import de.gnwi.mfsim.gui.control.CustomPanelStructureEdit;
import de.gnwi.mfsim.gui.control.CustomPanelStructureEditController;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
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
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Molecular structure edit dialog
 *
 * @author Achim Zielesny
 */
public class DialogStructureEdit extends CustomDialogApplyCancelSize implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelProteinEdit customPanelProteinEdit;
    private JTabbedPane selectStructureTypeTabbedPanel;
    private CustomPanelStructureEdit customPanelStructureEdit;
    private JPanel mainPanel;

    /**
     * True: A molecular structure change occurred, false: Otherwise
     */
    private boolean hasMolecularStructureChanged;

    /**
     * True: Protein definition change occurred, false: Otherwise
     */
    private boolean hasProteinDefinitionChanged;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private static class variables">
    /**
     * True: A change occurred, false: Otherwise. NOTE: Static variable is
     * necessary for dialog result since result is disposed.
     */
    private static boolean resultHasChanged;
    /**
     * True: Dialog is in molecular-structure edit mode, false: Dialog is in
     * protein edit mode
     */
    private static boolean isMolecularStructureEditMode;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000020L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the dialog
     */
    public DialogStructureEdit() {
        super();
        // <editor-fold defaultstate="collapsed" desc="Initialisation code">
        this.hasMolecularStructureChanged = false;
        this.hasProteinDefinitionChanged = false;
        // </editor-fold>
        this.setName(GuiMessage.get("DialogStructureEdit.Structure.name")); 
        this.setModal(true);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_DIALOG_WIDTH, ModelDefinitions.MINIMUM_DIALOG_HEIGHT));
        // <editor-fold defaultstate="collapsed" desc="mainPanel">
        {
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new BorderLayout());
            this.getContentPane().add(this.mainPanel, BorderLayout.CENTER);
            // <editor-fold defaultstate="collapsed" desc="selectStructureTypeTabbedPanel">
            {
                this.selectStructureTypeTabbedPanel = new JTabbedPane();
                this.mainPanel.add(this.selectStructureTypeTabbedPanel, BorderLayout.CENTER);
                // <editor-fold defaultstate="collapsed" desc="customPanelStructureEdit">
                {
                    this.customPanelStructureEdit = new CustomPanelStructureEdit();
                    this.selectStructureTypeTabbedPanel.addTab(GuiMessage.get("DialogStructureEdit.customPanelStructureEdit.title"), null, this.customPanelStructureEdit,
                            GuiMessage.get("DialogStructureEdit.customPanelStructureEdit.tooltip"));  //$NON-NLS-2$
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="proteinEditPanel">
                {
                    this.customPanelProteinEdit = new CustomPanelProteinEdit();
                    this.selectStructureTypeTabbedPanel.addTab(GuiMessage.get("DialogStructureEdit.customPanelProteinEdit.title"), null, this.customPanelProteinEdit,
                            GuiMessage.get("DialogStructureEdit.customPanelProteinEdit.tooltip"));  //$NON-NLS-2$
                }
                // </editor-fold>
            }
            // </editor-fold>
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * CustomPanelStructureEdit
     * 
     * @return CustomPanelStructureEdit
     */
    public CustomPanelStructureEdit getCustomPanelStructureEdit() {
        return customPanelStructureEdit;
    }

    /**
     * CustomPanelProteinEdit
     * 
     * @return CustomPanelProteinEdit
     */
    public CustomPanelProteinEdit getCustomPanelProteinEdit() {
        return customPanelProteinEdit;
    }

    /**
     * SelectStructureTypeTabbedPanel
     * 
     * @return SelectStructureTypeTabbedPanel
     */
    public JTabbedPane getSelectStructureTypeTabbedPanel() {
        return selectStructureTypeTabbedPanel;
    }

    /**
     * ProteinEditPanel
     * 
     * @return ProteinEditPanel
     */
    public JPanel getProteinEditPanel() {
        return customPanelProteinEdit;
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
        // <editor-fold defaultstate="collapsed" desc="Molecular structure change info">
        if (aChangeNotifier instanceof CustomPanelStructureEditController) {
            switch (aChangeInfo.getChangeType()) {
                case STRUCTURE_SYNTAX_CORRECT_CHANGE:
                    this.hasMolecularStructureChanged = true;
                    if (!this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(true);
                    }
                    break;
                case STRUCTURE_SYNTAX_ERROR_CHANGE:
                    this.hasMolecularStructureChanged = true;
                    if (this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(false);
                    }
                    break;
                case STRUCTURE_SYNTAX_NO_CHANGE:
                    this.hasMolecularStructureChanged = false;
                    if (this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(false);
                    }
                    break;
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Protein definition change info">
        if (aChangeNotifier instanceof CustomPanelProteinEditController) {
            switch (aChangeInfo.getChangeType()) {
                case PROTEIN_DEFINITION_CORRECT_CHANGE:
                    this.hasProteinDefinitionChanged = true;
                    if (!this.getApplyButton().isVisible()) {
                        this.getApplyButton().setVisible(true);
                    }
                    break;
            }
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public static edit() method">
    /**
     * Edit structure
     *
     * @param aValueItem Value item (may be changed)
     * @param anAvailableParticles Available particles (is NOT allowed to be
     * null)
     * @param anAvailableMonomers Available monomers (may be null)
     * @return True: Value item changed value, false: Otherwise
     */
    public static boolean edit(
        ValueItem aValueItem, 
        HashMap<String, String> anAvailableParticles, 
        HashMap<String, String> anAvailableMonomers
    ) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            return false;
        }
        if (anAvailableParticles == null) {
            return false;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set final variables">
        // Value item
        final ValueItem tmpValueItem = aValueItem;
        // Save initial monomer/molecular structure and initial protein data
        final String tmpInitialStructure = tmpValueItem.getValue();
        final String tmpInitialProteinData = tmpValueItem.getValueItemMatrixElement().getProteinData();
        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Dialog setup and show">
            // <editor-fold defaultstate="collapsed" desc="- Instantiate dialog">
            // <editor-fold defaultstate="collapsed" desc="-- Instantiate tmpDialogStructureEdit">
            final DialogStructureEdit tmpDialogStructureEdit = new DialogStructureEdit();
            MouseCursorManagement.getInstance().pushMouseCursorComponent(tmpDialogStructureEdit);
            // Set dialog icon
            tmpDialogStructureEdit.setIconImage(GuiUtils.getImageOfResource(GuiDefinitions.ICON_IMAGE_FILENAME));
            // Set dialog title
            tmpDialogStructureEdit.setTitle(GuiMessage.get("DialogStructureEdit.Structure.title"));
            // Make apply-button invisible
            tmpDialogStructureEdit.getApplyButton().setVisible(false);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="-- Set tabs and instantiate controllers">
            CustomPanelStructureEditController tmpCustomPanelStructureEditControllerNonFinal = null;
            CustomPanelProteinEditController tmpCustomPanelProteinEditControllerNonFinal = null;
            if (StandardParticleInteractionData.getInstance().hasAminoAcidDefinitions()) {
                if (tmpValueItem.getValueItemMatrixElement().hasProteinData()) {
                    // Set protein edit tab (index 1) and set protein edit mode
                    tmpDialogStructureEdit.getSelectStructureTypeTabbedPanel().setSelectedIndex(1);
                    DialogStructureEdit.isMolecularStructureEditMode = false;
                    // Instantiate CustomPanelStructureEditController, Parameter false: Molecular structure (no monomer)
                    tmpCustomPanelStructureEditControllerNonFinal = 
                        new CustomPanelStructureEditController(
                            tmpDialogStructureEdit.getCustomPanelStructureEdit(), 
                            false,
                            anAvailableParticles, 
                            anAvailableMonomers, 
                            StandardParticleInteractionData.getInstance().getDefaultWaterParticle()
                        );
                } else {
                    // Set molecular structure edit tab (index 0) and set molecular structure edit mode
                    tmpDialogStructureEdit.getSelectStructureTypeTabbedPanel().setSelectedIndex(0);
                    DialogStructureEdit.isMolecularStructureEditMode = true;
                    // Instantiate CustomPanelStructureEditController, Parameter false: Molecular structure (no monomer)
                    tmpCustomPanelStructureEditControllerNonFinal = 
                        new CustomPanelStructureEditController(
                            tmpDialogStructureEdit.getCustomPanelStructureEdit(), 
                            false,
                            anAvailableParticles, 
                            anAvailableMonomers, 
                            tmpInitialStructure
                        );
                }
                // Add tmpDialogStructureEdit as change receiver
                tmpCustomPanelStructureEditControllerNonFinal.addChangeReceiver(tmpDialogStructureEdit);
                // Instantiate CustomPanelStructureEditController, Parameter false: Molecular structure (no monomer)
                tmpCustomPanelProteinEditControllerNonFinal = 
                    new CustomPanelProteinEditController(
                        tmpDialogStructureEdit.getCustomPanelProteinEdit(),
                        tmpInitialProteinData
                    );
                // Add tmpDialogStructureEdit as change receiver
                tmpCustomPanelProteinEditControllerNonFinal.addChangeReceiver(tmpDialogStructureEdit);
            } else {
                // Disable protein edit
                tmpDialogStructureEdit.getSelectStructureTypeTabbedPanel().setEnabledAt(1, false);
                // Set molecular structure edit tab (index 0) and set molecular structure edit mode
                tmpDialogStructureEdit.getSelectStructureTypeTabbedPanel().setSelectedIndex(0);
                DialogStructureEdit.isMolecularStructureEditMode = true;
                // Instantiate CustomPanelStructureEditController, Parameter false: Molecular structure (no monomer)
                tmpCustomPanelStructureEditControllerNonFinal =
                    new CustomPanelStructureEditController(
                        tmpDialogStructureEdit.getCustomPanelStructureEdit(), 
                        false,
                        anAvailableParticles, 
                        anAvailableMonomers, 
                        tmpInitialStructure
                    );
                // Add tmpDialogStructureEdit as change receiver
                tmpCustomPanelStructureEditControllerNonFinal.addChangeReceiver(tmpDialogStructureEdit);
            }
            final CustomPanelStructureEditController tmpCustomPanelStructureEditController = tmpCustomPanelStructureEditControllerNonFinal;
            final CustomPanelProteinEditController tmpCustomPanelProteinEditController = tmpCustomPanelProteinEditControllerNonFinal;
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Add listeners">
            tmpDialogStructureEdit.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent e) {
                    try {
                        if (tmpDialogStructureEdit.getApplyButton().isVisible()
                            && !GuiUtils.getYesNoDecision(GuiMessage.get("DialogStructureEdit.Cancel.Title"), GuiMessage.get("DialogStructureEdit.Cancel"))) {
                            return;
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogStructureEditHeightWidth(tmpDialogStructureEdit.getHeight(), tmpDialogStructureEdit.getWidth());
                        // Set result before dispose()
                        DialogStructureEdit.resultHasChanged = false;
                        tmpDialogStructureEdit.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "windowClosing()", "DialogStructureEdit"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureEdit.getSelectStructureTypeTabbedPanel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent e) {
                    try {
                        DialogStructureEdit.isMolecularStructureEditMode = tmpDialogStructureEdit.getSelectStructureTypeTabbedPanel().getSelectedIndex() == 0;
                        if (DialogStructureEdit.isMolecularStructureEditMode) {
                            tmpDialogStructureEdit.hasMolecularStructureChanged = !tmpInitialStructure.equals(tmpDialogStructureEdit.getCustomPanelStructureEdit().getStructureTextArea().getText());
                            tmpDialogStructureEdit.getApplyButton().setVisible(tmpDialogStructureEdit.hasMolecularStructureChanged);
                        } else {
                            tmpDialogStructureEdit.getApplyButton().setVisible(tmpDialogStructureEdit.hasProteinDefinitionChanged);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "DialogStructureEdit"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureEdit.getApplyButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        try {
                            if (DialogStructureEdit.isMolecularStructureEditMode) {
                                // <editor-fold defaultstate="collapsed" desc="Molecular structure edit mode">
                                tmpValueItem.getValueItemMatrixElement().removeProteinData();
                                if (tmpValueItem.setValue(tmpDialogStructureEdit.getCustomPanelStructureEdit().getStructureTextArea().getText())) {
                                    tmpValueItem.notifyDependentValueItemsForUpdate();
                                }
                                // Set Spices for reuse
                                SpicesGraphics tmpLastValidSpices = tmpCustomPanelStructureEditController.getLastValidSpices();
                                if (tmpLastValidSpices != null) {
                                    SpicesPool.getInstance().setSpicesForReuse(tmpLastValidSpices);
                                }
                                // Set previous structure definition
                                Preferences.getInstance().addPreviousStructure(tmpValueItem.getValue());
                                // </editor-fold>
                            } else {
                                // <editor-fold defaultstate="collapsed" desc="Protein edit mode">
                                String tmpProteinData = tmpCustomPanelProteinEditController.getProteinData();
                                String tmpSpicesString = tmpCustomPanelProteinEditController.getProteinSpices();
                                boolean tmpIsInitialProteinDataChanged = tmpValueItem.getValueItemMatrixElement().setProteinData(tmpProteinData);
                                if (tmpValueItem.setValue(tmpSpicesString) || tmpIsInitialProteinDataChanged) {
                                    tmpValueItem.notifyDependentValueItemsForUpdate();
                                }
                                // Set Spices for reuse
                                SpicesGraphics tmpLastValidSpices = tmpCustomPanelProteinEditController.getLastValidSpices();
                                if (tmpLastValidSpices != null) {
                                    SpicesPool.getInstance().setSpicesForReuse(tmpLastValidSpices);
                                }
                                // Do NOT set previous structure definition for protein
                                // </editor-fold>
                            }
                        } catch (Exception anException) {
                            ModelUtils.appendToLogfile(true, anException);
                            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                            JOptionPane.showMessageDialog(null, 
                                String.format(GuiMessage.get("Error.CommandExecutionFailed"), "edit()", "DialogStructureEdit"), 
                                GuiMessage.get("Error.ErrorNotificationTitle"), 
                                JOptionPane.ERROR_MESSAGE
                            );
                            // </editor-fold>
                            // Restore
                            boolean tmpIsInitialStructureChanged = tmpValueItem.setValue(tmpInitialStructure);
                            boolean tmpIsInitialProteinDataChanged = tmpValueItem.getValueItemMatrixElement().setProteinData(tmpInitialProteinData);
                            if (tmpIsInitialStructureChanged || tmpIsInitialProteinDataChanged) {
                                tmpValueItem.notifyDependentValueItemsForUpdate();
                            }
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogStructureEditHeightWidth(tmpDialogStructureEdit.getHeight(), tmpDialogStructureEdit.getWidth());
                        // <editor-fold defaultstate="collapsed" desc="Set result before dispose()">
                        if (DialogStructureEdit.isMolecularStructureEditMode) {
                            DialogStructureEdit.resultHasChanged = tmpDialogStructureEdit.hasMolecularStructureChanged;
                        } else {
                            DialogStructureEdit.resultHasChanged = tmpDialogStructureEdit.hasProteinDefinitionChanged;
                        }
                        // </editor-fold>
                        tmpDialogStructureEdit.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureEdit"), 
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureEdit.getCancelButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (tmpDialogStructureEdit.getApplyButton().isVisible()
                            && !GuiUtils.getYesNoDecision(GuiMessage.get("DialogStructureEdit.Cancel.Title"), GuiMessage.get("DialogStructureEdit.Cancel"))) {
                            return;
                        }
                        // Set dialog size in BasicPreferences
                        Preferences.getInstance().setDialogStructureEditHeightWidth(tmpDialogStructureEdit.getHeight(), tmpDialogStructureEdit.getWidth());
                        // Set result before dispose()
                        DialogStructureEdit.resultHasChanged = false;
                        tmpDialogStructureEdit.dispose();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureEdit"),
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureEdit.getMinimizeDialogSizeButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.minimizeDialogSize(tmpDialogStructureEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogStructureEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureEdit"), 
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureEdit.getMaximizeDialogSizeButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.maximizeDialogSize(tmpDialogStructureEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogStructureEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureEdit"), 
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureEdit.getCenterDialogButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.centerDialogOnScreen(tmpDialogStructureEdit);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureEdit"), 
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureEdit.getCustomDialogSizeButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (GuiUtils.setCustomDialogSize(tmpDialogStructureEdit)) {
                            GuiUtils.centerDialogOnScreen(tmpDialogStructureEdit);
                        }
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureEdit"), 
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            tmpDialogStructureEdit.getCustomDialogPreferencesButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        GuiUtils.setCustomDialogSizePreferences(tmpDialogStructureEdit);
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, 
                            String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "DialogStructureEdit"), 
                            GuiMessage.get("Error.ErrorNotificationTitle"), 
                            JOptionPane.ERROR_MESSAGE
                        );
                        // </editor-fold>
                    }
                }
            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Show centered dialog">
            // Important: Pack dialog first ...
            tmpDialogStructureEdit.pack();
            // ... then setSize() ...
            tmpDialogStructureEdit.setSize(Preferences.getInstance().getDialogStructureEditWidth(), Preferences.getInstance().getDialogStructureEditHeight());
            GuiUtils.checkDialogSize(tmpDialogStructureEdit);
            GuiUtils.centerDialogOnScreen(tmpDialogStructureEdit);
            // ... then request focus:
            tmpDialogStructureEdit.getCustomPanelStructureEdit().getStructureTextArea().requestFocusInWindow();
            tmpDialogStructureEdit.setVisible(true);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Return dialog result">
            return DialogStructureEdit.resultHasChanged;
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "edit()", "DialogStructureEdit"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Restore">
            boolean tmpIsInitialStructureChanged = tmpValueItem.setValue(tmpInitialStructure);
            boolean tmpIsInitialProteinDataChanged = tmpValueItem.getValueItemMatrixElement().setProteinData(tmpInitialProteinData);
            if (tmpIsInitialStructureChanged || tmpIsInitialProteinDataChanged) {
                tmpValueItem.notifyDependentValueItemsForUpdate();
            }
            // </editor-fold>
            return false;
        } finally {
            MouseCursorManagement.getInstance().popMouseCursorComponent();
        }
    }
    // </editor-fold>

}
