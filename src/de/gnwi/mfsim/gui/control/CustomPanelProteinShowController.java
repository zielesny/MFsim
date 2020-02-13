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
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.dialog.DialogValueItemShow;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.jmolViewer.Jmol3dPeptideController;
import de.gnwi.mfsim.model.peptide.PdbToDpd;
import de.gnwi.mfsim.model.peptide.PdbToDpdPool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import de.gnwi.mfsim.model.util.ModelUtils;

/**
 * Controller class for CustomPanelProteinShow
 *
 * @author Achim Zielesny
 */
public class CustomPanelProteinShowController extends ChangeNotifier {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * String utility methods
     */
    private final StringUtilityMethods stringUtilityMethods = new StringUtilityMethods();

    /**
     * Utility misc methods
     */
    private MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

    /**
     * Panel this controller is made for
     */
    private CustomPanelProteinShow customPanelProteinShow;

    /**
     * PDB-to-DPD conversion
     */
    private PdbToDpd pdbToDPD;

    /**
     * Change information
     */
    private ChangeInformation changeInformation;

    /**
     * Controller for
     * this.customPanelProteinShow.getProperty3dStructureJmol3dPanel()
     */
    private Jmol3dPeptideController jmol3dPeptideController;

    /**
     * Last defined pH value
     */
    private String lastPhValue;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelProteinShow Panel this controller is made for
     * @param aProteinData Protein data
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelProteinShowController(CustomPanelProteinShow aCustomPanelProteinShow, String aProteinData) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelProteinShow == null) {
            throw new IllegalArgumentException("aCustomPanelProteinShow is null.");
        }
        if (!StandardParticleInteractionData.getInstance().hasAminoAcidDefinitions()) {
            throw new IllegalArgumentException("Amino acids are not defined.");
        }

        // </editor-fold>
        try {
            // Amino acids MUST already be initialized
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.customPanelProteinShow = aCustomPanelProteinShow;
            this.changeInformation = new ChangeInformation();
            this.jmol3dPeptideController = new Jmol3dPeptideController(this.customPanelProteinShow.getProperty3dStructureJmol3dPanel());
            this.jmol3dPeptideController.setBackgroundColor(Preferences.getInstance().getProteinViewerBackgroundColor());

            // Initialize PDB-to-DPD conversion
            if (aProteinData == null || aProteinData.isEmpty()) {
                this.pdbToDPD = null;
                this.lastPhValue = null;
                this.customPanelProteinShow.getProteinDefinitionInfoLabel().setText(GuiMessage.get("Protein.NoDefinition"));
                this.customPanelProteinShow.getProteinPropertyPanel().setVisible(false);
            } else {
                this.pdbToDPD = PdbToDpdPool.getInstance().getPdbToDpd(aProteinData);
                if (this.pdbToDPD.getPhValue() == null) {
                    this.lastPhValue = null;
                } else {
                    this.lastPhValue = String.valueOf(this.pdbToDPD.getPhValue());
                }
                this.customPanelProteinShow.getBioAssemblyInfoLabel().setText(this.pdbToDPD.getBiologicalAssembly());
                this.showProteinData();
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            this.customPanelProteinShow.getGraphicsPreferencesButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinShowController.this.setGraphicsPreferences();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinShow.getShowProteinRotationButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinShowController.this.showProteinRotation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinShow.getShowChainButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinShowController.this.showChain();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinShow.getShowPhButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinShowController.this.showPhValue();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinShow.getShowProteinPropertiesButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinShowController.this.showProperties();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinShow.getShowMutantButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinShowController.this.showMutant();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelProteinShowController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    /**
     * Shows protein data
     */
    private void showProteinData() {
        try {
            this.customPanelProteinShow.getProteinDefinitionInfoLabel().setText(this.pdbToDPD.getTitle());
            this.customPanelProteinShow.getProteinDefinitionInfoLabel().setToolTipText(this.pdbToDPD.getTitle());

            this.customPanelProteinShow.getPropertySequencesTextArea().setText(this.pdbToDPD.getSequences());
            this.customPanelProteinShow.getPropertySequencesTextArea().setCaretPosition(0);

            this.customPanelProteinShow.getPropertySpicesTextArea().setText(this.pdbToDPD.getSpices());
            this.customPanelProteinShow.getPropertySpicesTextArea().setCaretPosition(0);

            this.customPanelProteinShow.getPropertyPdbFileContentTextArea().setText(this.pdbToDPD.getPdb());
            this.customPanelProteinShow.getPropertyPdbFileContentTextArea().setCaretPosition(0);

            this.jmol3dPeptideController.showProteinFromMasterdata(this.pdbToDPD.getMasterdata());

            this.customPanelProteinShow.getProteinPropertyPanel().setVisible(true);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showProteinData", "CustomPanelProteinShowController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Sets protein viewer graphics preferences
     */
    private void setGraphicsPreferences() {
        ValueItemContainer tmpProteinViewerEditablePreferencesValueItemContainer = Preferences.getInstance().getProteinViewerEditablePreferencesValueItemContainer();
        if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesProteinViewerGraphicsSettingsDialog.title"), tmpProteinViewerEditablePreferencesValueItemContainer)) {
            Preferences.getInstance().setEditablePreferences(tmpProteinViewerEditablePreferencesValueItemContainer);
            this.jmol3dPeptideController.setBackgroundColor(Preferences.getInstance().getProteinViewerBackgroundColor());
        }
    }

    /**
     * Show protein rotation
     */
    private void showProteinRotation() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set property value item container">
            ValueItemContainer tmpProteinRotationValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set protein rotation value item">
            String[] tmpNodeNames = new String[]{GuiMessage.get("proteinPropertySettings.Nodename")};
            ValueItem tmpProteinRotationValueItem = GuiUtils.getProteinRotationValueItem(this.pdbToDPD);
            if (tmpProteinRotationValueItem == null) {
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setProteinRotation()", "CustomPanelProteinEditController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                // </editor-fold>
                return;
            }
            tmpProteinRotationValueItem.setNodeNames(tmpNodeNames);
            tmpProteinRotationValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpProteinRotationValueItemContainer.addValueItem(tmpProteinRotationValueItem);
            // </editor-fold>
            DialogValueItemShow.show(GuiMessage.get("proteinPropertySettings.dialogTitle"), tmpProteinRotationValueItemContainer, false, false);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showProteinRotation()", "CustomPanelProteinShowController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    
    /**
     * Show chain related properties
     */
    private void showChain() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set property value item container">
            ValueItemContainer tmpChainValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set chain related value items">
            String[] tmpNodeNames = new String[]{GuiMessage.get("chainSettings.Nodename")};
            // <editor-fold defaultstate="collapsed" desc="- Set active protein chains value item">
            ValueItem tmpActiveProteinChainsValueItem = GuiUtils.getActiveProteinChainsValueItem(this.pdbToDPD);
            if (tmpActiveProteinChainsValueItem == null) {
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showChain()", "CustomPanelProteinShowController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpActiveProteinChainsValueItem.setNodeNames(tmpNodeNames);
            tmpActiveProteinChainsValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpChainValueItemContainer.addValueItem(tmpActiveProteinChainsValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set chain segment assignment value item">
            ValueItem tmpChainSegmentAssignmentValueItem = GuiUtils.getChainSegmentAssignmentValueItem(this.pdbToDPD);
            tmpChainSegmentAssignmentValueItem.setNodeNames(tmpNodeNames);
            tmpChainSegmentAssignmentValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpChainValueItemContainer.addValueItem(tmpChainSegmentAssignmentValueItem);
            // </editor-fold>
            // </editor-fold>
            DialogValueItemShow.show(GuiMessage.get("chainSettings.dialogTitle"), tmpChainValueItemContainer, false, false);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showChain()", "CustomPanelProteinShowController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Show pH value
     */
    private void showPhValue() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set pH value item container">
            ValueItemContainer tmpPhValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set pH value item">
            String[] tmpNodeNames = new String[]{GuiMessage.get("proteinPhSettings.Nodename")};
            ValueItem tmpPhValueItem = GuiUtils.getPhValueItem(this.lastPhValue);
            if (tmpPhValueItem == null) {
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showPhValue()", "CustomPanelProteinShowController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpPhValueItem.setNodeNames(tmpNodeNames);
            tmpPhValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpPhValueItemContainer.addValueItem(tmpPhValueItem);

            // </editor-fold>
            DialogValueItemShow.show(GuiMessage.get("proteinPhSettings.dialogTitle"), tmpPhValueItemContainer, false, false);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showPhValue()", "CustomPanelProteinShowController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Show protein properties
     */
    private void showProperties() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set property value item container">
            ValueItemContainer tmpPropertyValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set property value items">
            String[] tmpNodeNames = new String[]{GuiMessage.get("proteinPropertySettings.Nodename")};
            // <editor-fold defaultstate="collapsed" desc="- Set protein backbone probe particles">
            ValueItem tmpProteinBackboneProbesValueItem = GuiUtils.getProteinBackboneProbesValueItem(this.pdbToDPD);
            if (tmpProteinBackboneProbesValueItem != null) {
                tmpProteinBackboneProbesValueItem.setNodeNames(tmpNodeNames);
                tmpProteinBackboneProbesValueItem.setVerticalPosition(tmpVerticalPosition++);
                tmpPropertyValueItemContainer.addValueItem(tmpProteinBackboneProbesValueItem);
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set protein backbone status and segments">
            ValueItem tmpProteinBackboneStatusValueItem = GuiUtils.getProteinBackboneStatusValueItem(this.pdbToDPD);
            if (tmpProteinBackboneStatusValueItem != null) {
                tmpProteinBackboneStatusValueItem.setNodeNames(tmpNodeNames);
                tmpProteinBackboneStatusValueItem.setVerticalPosition(tmpVerticalPosition++);
                tmpPropertyValueItemContainer.addValueItem(tmpProteinBackboneStatusValueItem);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set sequence loop">
            ValueItem tmpSequenceLoopValueItem = GuiUtils.getSequenceLoopValueItem(this.pdbToDPD);
            if (tmpSequenceLoopValueItem == null) {
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showProperties()", "CustomPanelProteinShowController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpSequenceLoopValueItem.setNodeNames(tmpNodeNames);
            tmpSequenceLoopValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpPropertyValueItemContainer.addValueItem(tmpSequenceLoopValueItem);
            // </editor-fold>
            // </editor-fold>
            DialogValueItemShow.show(GuiMessage.get("proteinPropertySettings.dialogTitle"), tmpPropertyValueItemContainer, false, false);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showProperties()", "CustomPanelProteinShowController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Show mutant
     */
    private void showMutant() {
        try {
            // <editor-fold defaultstate="collapsed" desc="Set mutant value item container">
            ValueItemContainer tmpMutantValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set mutant value item">
            String[] tmpNodeNames = new String[]{GuiMessage.get("proteinMutant.Nodename")};
            ValueItem tmpMutantValueItem = GuiUtils.getMutantValueItem(this.pdbToDPD);
            if (tmpMutantValueItem == null) {
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showMutant()", "CustomPanelProteinShowController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpMutantValueItem.setNodeNames(tmpNodeNames);
            tmpMutantValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpMutantValueItemContainer.addValueItem(tmpMutantValueItem);
            // </editor-fold>
            DialogValueItemShow.show(GuiMessage.get("proteinPropertySettings.dialogTitle"), tmpMutantValueItemContainer, false, false);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showMutant()", "CustomPanelProteinShowController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
}
