/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2022  Achim Zielesny (achim.zielesny@googlemail.com)
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
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.util.ExtensionFileFilter;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.util.StringUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.jmolViewer.Jmol3dPeptideController;
import de.gnwi.mfsim.model.peptide.base.AminoAcid;
import de.gnwi.mfsim.model.peptide.base.AminoAcids;
import de.gnwi.mfsim.model.peptide.PdbToDpd;
import de.gnwi.mfsim.model.peptide.PdbToDpdPool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.vecmath.Vector3d;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.valueItem.ValueItemMatrixElement;
import de.gnwi.mfsim.model.valueItem.ValueItemUpdateNotifierInterface;
import javax.swing.ComboBoxModel;

/**
 * Controller class for CustomPanelProteinEdit
 *
 * @author Achim Zielesny
 */
public class CustomPanelProteinEditController extends ChangeNotifier implements ValueItemUpdateNotifierInterface {

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
    private CustomPanelProteinEdit customPanelProteinEdit;

    /**
     * PDB-to-DPD conversion
     */
    private PdbToDpd pdbToDpd;

    /**
     * Change information
     */
    private ChangeInformation changeInformation;

    /**
     * Last defined pH value
     */
    private String lastPhValue;

    /**
     * Controller for
     * this.customPanelProteinEdit.getProperty3dStructureJmol3dPanel()
     */
    private Jmol3dPeptideController jmol3dPeptideController;
    
    /**
     * Last valid Spices
     */
    private SpicesGraphics lastValidSpices;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">

    /**
     * Constructor
     *
     * @param aCustomPanelProteinEdit Panel this controller is made for
     * @param aProteinData Protein data
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelProteinEditController(CustomPanelProteinEdit aCustomPanelProteinEdit, String aProteinData) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelProteinEdit == null) {
            throw new IllegalArgumentException("aCustomPanelProteinEdit is null.");
        }
        if (!StandardParticleInteractionData.getInstance().hasAminoAcidDefinitions()) {
            throw new IllegalArgumentException("Amino acids are not defined.");
        }
        // </editor-fold>
        try {
            // Amino acids MUST already be initialized
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.customPanelProteinEdit = aCustomPanelProteinEdit;
            this.changeInformation = new ChangeInformation();
            this.jmol3dPeptideController = new Jmol3dPeptideController(this.customPanelProteinEdit.getProperty3dStructureJmol3dPanel());
            this.jmol3dPeptideController.setBackgroundColor(Preferences.getInstance().getProteinViewerBackgroundColor());
            // Initialize PDB-to-DPD conversion
            if (aProteinData == null || aProteinData.isEmpty()) {
                this.pdbToDpd = null;
                this.lastValidSpices = null;
                this.lastPhValue = null;
                this.customPanelProteinEdit.getProteinDefinitionInfoLabel().setText(GuiMessage.get("Protein.NoDefinition"));
                this.setFeatureWidgetVisibility(false);
                this.customPanelProteinEdit.getProteinPropertyPanel().setVisible(false);
            } else {
                this.pdbToDpd = PdbToDpdPool.getInstance().getPdbToDpd(aProteinData);
                String tmpMolecularStructureString = this.pdbToDpd.getSpices();
                this.lastValidSpices = new SpicesGraphics(tmpMolecularStructureString);
                if (this.pdbToDpd.getPhValue() == null) {
                    this.lastPhValue = null;
                } else {
                    this.lastPhValue = String.valueOf(this.pdbToDpd.getPhValue());
                }
                this.customPanelProteinEdit.getBioAssemblyComboBox().setModel(new DefaultComboBoxModel(this.pdbToDpd.getBiologicalAssemblies()));
                this.customPanelProteinEdit.getBioAssemblyComboBox().setSelectedItem(this.pdbToDpd.getBiologicalAssembly());
                this.showProteinData();
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            this.customPanelProteinEdit.getLoadPdbFileButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.loadPdbFile();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getSetChainButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.setChain();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getSetPhButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.setPhValue();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getSetProteinPropertiesButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.setProperties();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getSetMutantButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.setMutant();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getClearSettingsButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.clearSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getSetProteinRotationButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.setProteinRotation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getSetCurrentRotationButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.setCurrentProteinRotation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getGraphicsPreferencesButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelProteinEditController.this.setGraphicsPreferences();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelProteinEdit.getBioAssemblyComboBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        CustomPanelProteinEditController.this.setBioAssembly();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()", "CustomPanelProteinEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns last valid Spices (which may be null)
     * @return Last valid Spices (which may be null)
     */
    public SpicesGraphics getLastValidSpices() {
        return this.lastValidSpices;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="notifyDependentValueItemsForUpdate() method">
    /**
     * Notify dependent value items of container for update
     *
     * @param anUpdateNotifierValueItem Value item that notifies update
     */
    @Override
    public void notifyDependentValueItemsForUpdate(ValueItem anUpdateNotifierValueItem) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anUpdateNotifierValueItem == null) {
            return;
        }
        if (anUpdateNotifierValueItem.getValueItemContainer() == null) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Update notifier PROTEIN_MUTANT">
        if (anUpdateNotifierValueItem.getName().equals("PROTEIN_MUTANT")) {
            ValueItemMatrixElement[][] tmpMatrix = anUpdateNotifierValueItem.getMatrix();
            for(int i = 0; i < tmpMatrix.length; i++) {
                // Highlight replacement if different to original amino acid
                tmpMatrix[i][3].getTypeFormat().setHightlight(!tmpMatrix[i][2].getValue().equals(tmpMatrix[i][3].getValue()));
            }
        }
        // </editor-fold>
    }    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get)">
    /**
     * Protein data XML string from PDB-to-DPD master data
     *
     * @return Protein data XML string from PDB-to-DPD master data or empty
     * string if protein data is not available
     */
    public String getProteinData() {
        try {
            if (this.pdbToDpd == null || this.pdbToDpd.getMasterdata() == null) {
                return "";
            } else {
                String tmpProteinData = this.pdbToDpd.getProteinData();
                PdbToDpdPool.getInstance().setPdbToDpdForReuse(this.pdbToDpd, tmpProteinData);
                return tmpProteinData;
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return "";
        }
    }

    /**
     * Spices of protein
     *
     * @return Spices of protein or null if Spices of protein is not available
     */
    public String getProteinSpices() {
        try {
            if (this.pdbToDpd == null) {
                return null;
            } else {
                return this.pdbToDpd.getSpices();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private Methods">
    /**
     * Loads PDB file
     */
    private void loadPdbFile() {
        if (this.pdbToDpd != null) {
            if (!GuiUtils.getYesNoDecision(GuiMessage.get("Protein.AskForLoad.Title"), GuiMessage.get("Protein.AskForLoad"))) {
                return;
            }
        }
        try {
            // Select PDB file
            String tmpPdbFilePathname = GuiUtils.selectSingleFile(Preferences.getInstance().getLastSelectedPath(), GuiMessage.get("Protein.SelectPdbFileForLoad"),
                    new ExtensionFileFilter(new String[]{GuiDefinitions.PDB_FILE_EXTENSION}));
            if (tmpPdbFilePathname != null && !tmpPdbFilePathname.isEmpty() && (new File(tmpPdbFilePathname)).isFile()) {
                // Read PDB file
                MouseCursorManagement.getInstance().setWaitCursor();
                PdbToDpd tmpOldPdbToDPD = this.pdbToDpd;
                this.pdbToDpd = new PdbToDpd();
                if (!this.pdbToDpd.readPdb(new File(tmpPdbFilePathname))) {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    this.pdbToDpd = tmpOldPdbToDPD;
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Protein.NoPdbFile"), GuiMessage.get("Error.ErrorNotificationTitle"),
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    this.lastPhValue = null;
                    String tmpMolecularStructureString = this.pdbToDpd.getSpices();
                    SpicesGraphics tmpSpices = new SpicesGraphics(tmpMolecularStructureString);
                    if (tmpSpices.isValid()) {
                        // <editor-fold defaultstate="collapsed" desc="Generated Spices is valid">
                        this.lastValidSpices = tmpSpices;
                        // <editor-fold defaultstate="collapsed" desc="Notify change receiver">
                        this.changeInformation.setChangeType(ChangeTypeEnum.PROTEIN_DEFINITION_CORRECT_CHANGE);
                        super.notifyChangeReceiver(this, this.changeInformation);
                        // </editor-fold>
                        this.customPanelProteinEdit.getBioAssemblyComboBox().setModel(new DefaultComboBoxModel(this.pdbToDpd.getBiologicalAssemblies()));
                        this.customPanelProteinEdit.getBioAssemblyComboBox().setSelectedItem(this.pdbToDpd.getBiologicalAssembly());
                        this.showProteinData();
                        // </editor-fold>
                    } else {
                        // <editor-fold defaultstate="collapsed" desc="Generated Spices is invalid">
                        this.pdbToDpd = tmpOldPdbToDPD;
                        JOptionPane.showMessageDialog(null, GuiMessage.get("Protein.NoValidSpicesFromPdbFile"), GuiMessage.get("Error.ErrorNotificationTitle"),
                                JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "loadPdbFile", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Sets biological assembly
     */
    private void setBioAssembly() {
        String tmpSelectedBioAssembly = this.customPanelProteinEdit.getBioAssemblyComboBox().getSelectedItem().toString();
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // NOTE: This setting removes every other setting in this.pdbToDPD
            this.pdbToDpd.setBiologicalAssembly(tmpSelectedBioAssembly);
            this.lastPhValue = null;
            this.showProteinData();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setBioAssembly()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Set chain related properties
     */
    private void setChain() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Set property value item container">
            ValueItemContainer tmpChainValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set chain related value items">
            String[] tmpNodeNames = new String[]{GuiMessage.get("chainSettings.Nodename")};
            // <editor-fold defaultstate="collapsed" desc="- Set active protein chains value item">
            ValueItem tmpActiveProteinChainsValueItem = GuiUtils.getActiveProteinChainsValueItem(this.pdbToDpd);
            if (tmpActiveProteinChainsValueItem == null) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setChain()", "CustomPanelProteinEditController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpActiveProteinChainsValueItem.setNodeNames(tmpNodeNames);
            tmpActiveProteinChainsValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpChainValueItemContainer.addValueItem(tmpActiveProteinChainsValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set chain segment assignment value item">
            ValueItem tmpChainSegmentAssignmentValueItem = GuiUtils.getChainSegmentAssignmentValueItem(this.pdbToDpd);
            if (tmpChainSegmentAssignmentValueItem == null) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setChain()", "CustomPanelProteinEditController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpChainSegmentAssignmentValueItem.setNodeNames(tmpNodeNames);
            tmpChainSegmentAssignmentValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpChainValueItemContainer.addValueItem(tmpChainSegmentAssignmentValueItem);
            // </editor-fold>
            // </editor-fold>
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("chainSettings.dialogTitle"), tmpChainValueItemContainer)) {
                // <editor-fold defaultstate="collapsed" desc="Set changes">
                // <editor-fold defaultstate="collapsed" desc="- Set active protein chains">
                ArrayList<String> tmpActiveChainIdList = new ArrayList<String>();
                HashMap<String, String> tmpChainNameToIdMap = this.pdbToDpd.getNameChainIDMap();
                for (int i = 0; i < tmpActiveProteinChainsValueItem.getMatrixRowCount(); i++) {
                    if (tmpActiveProteinChainsValueItem.getValue(i, 1).equals(GuiMessage.get("ActiveProteinChains.Used"))) {
                        tmpActiveChainIdList.add(tmpChainNameToIdMap.get(tmpActiveProteinChainsValueItem.getValue(i, 0)));
                    }
                }
                if (tmpActiveChainIdList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, GuiMessage.get("ActiveProteinChains.CompleteExclusion"),
                            GuiMessage.get("ActiveProteinChains.CompleteExclusion.Title"), JOptionPane.INFORMATION_MESSAGE);
                    for (String tmpChainId : tmpChainNameToIdMap.values()) {
                        tmpActiveChainIdList.add(tmpChainId);
                    }
                }
                // FIRST check if clear is necessary ...
                boolean tmpIsClearNecessary = false;
                if (this.lastPhValue != null) {
                    this.lastPhValue = null;
                    this.pdbToDpd.setPhValue(null);
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.hasProbes()) {
                    this.pdbToDpd.clearProbes();
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.hasStatus()) {
                    this.pdbToDpd.clearStatus();
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.hasSegments()) {
                    this.pdbToDpd.clearSegments();
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.hasChangedAminoAcidSequence()) {
                    this.pdbToDpd.restoreOriginalAminoAcidSequence();
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.isCircular()) {
                    this.pdbToDpd.setCircular(false);
                    tmpIsClearNecessary = true;
                }
                // ... THEN set active chains!
                this.pdbToDpd.setActiveChains(tmpActiveChainIdList);
                if (tmpIsClearNecessary) {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Information.ProteinChainChange"), GuiMessage.get("Information.NotificationTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                    MouseCursorManagement.getInstance().setWaitCursor();
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Set chain segment assignment">
                if (tmpChainSegmentAssignmentValueItem.getValue().equals(GuiMessage.get("ChainSegmentsAssignment.True"))) {
                    this.pdbToDpd.setBackboneParticleSegmentArray(this.pdbToDpd.getBackboneParticleSegmentsChainsApplied());
                }
                // </editor-fold>
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Notify change receiver">
                this.changeInformation.setChangeType(ChangeTypeEnum.PROTEIN_DEFINITION_CORRECT_CHANGE);
                super.notifyChangeReceiver(this, this.changeInformation);
                // </editor-fold>
                this.showProteinData();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setChain()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Set pH value
     */
    private void setPhValue() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Set pH value item container">
            ValueItemContainer tmpPhValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set pH value item">
            String[] tmpNodeNames = new String[]{GuiMessage.get("proteinPhSettings.Nodename")};
            ValueItem tmpPhValueItem = GuiUtils.getPhValueItem(this.lastPhValue);
            if (tmpPhValueItem == null) {
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setPhValue()", "CustomPanelProteinEditController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpPhValueItem.setNodeNames(tmpNodeNames);
            tmpPhValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpPhValueItemContainer.addValueItem(tmpPhValueItem);

            // </editor-fold>
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("proteinPhSettings.dialogTitle"), tmpPhValueItemContainer)) {
                // <editor-fold defaultstate="collapsed" desc="Set property pH Value">
                // Check that not numeric-null
                boolean tmpIsPhValueChanged = false;
                if (tmpPhValueItem.getValue().equals(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"))) {
                    if (this.lastPhValue != null) {
                        this.lastPhValue = null;
                        this.pdbToDpd.setPhValue(null);
                        tmpIsPhValueChanged = true;
                    }
                } else {
                    if (this.lastPhValue == null || !this.lastPhValue.equals(tmpPhValueItem.getValue())) {
                        this.lastPhValue = tmpPhValueItem.getValue();
                        this.pdbToDpd.setPhValue(tmpPhValueItem.getValueAsDouble());
                        tmpIsPhValueChanged = true;
                    }
                }
                if (tmpIsPhValueChanged) {
                    if (this.pdbToDpd.hasProbes()) {
                        this.pdbToDpd.clearProbes();
                        MouseCursorManagement.getInstance().setDefaultCursor();
                        JOptionPane.showMessageDialog(null, GuiMessage.get("Information.ProteinPhValueChange"), GuiMessage.get("Information.NotificationTitle"),
                                JOptionPane.INFORMATION_MESSAGE);
                        MouseCursorManagement.getInstance().setWaitCursor();
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Notify change receiver">
                this.changeInformation.setChangeType(ChangeTypeEnum.PROTEIN_DEFINITION_CORRECT_CHANGE);
                super.notifyChangeReceiver(this, this.changeInformation);
                // </editor-fold>
                this.showProteinData();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setPhValue()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Set properties
     */
    private void setProperties() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Set property value item container">
            ValueItemContainer tmpPropertyValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set property value items">
            String[] tmpNodeNames = new String[]{GuiMessage.get("proteinPropertySettings.Nodename")};
            // <editor-fold defaultstate="collapsed" desc="- Set protein backbone probe particles">
            ValueItem tmpProteinBackboneProbesValueItem = GuiUtils.getProteinBackboneProbesValueItem(this.pdbToDpd);
            if (tmpProteinBackboneProbesValueItem == null) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setProperties()", "CustomPanelProteinEditController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpProteinBackboneProbesValueItem.setNodeNames(tmpNodeNames);
            tmpProteinBackboneProbesValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpPropertyValueItemContainer.addValueItem(tmpProteinBackboneProbesValueItem);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set protein backbone status and segments">
            ValueItem tmpProteinBackboneStatusValueItem = GuiUtils.getProteinBackboneStatusValueItem(this.pdbToDpd);
            if (tmpProteinBackboneStatusValueItem == null) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setProperties()", "CustomPanelProteinEditController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpProteinBackboneStatusValueItem.setNodeNames(tmpNodeNames);
            tmpProteinBackboneStatusValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpPropertyValueItemContainer.addValueItem(tmpProteinBackboneStatusValueItem);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set sequence loop">
            ValueItem tmpSequenceLoopValueItem = GuiUtils.getSequenceLoopValueItem(this.pdbToDpd);
            if (tmpSequenceLoopValueItem == null) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setProperties()", "CustomPanelProteinEditController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpSequenceLoopValueItem.setNodeNames(tmpNodeNames);
            tmpSequenceLoopValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpPropertyValueItemContainer.addValueItem(tmpSequenceLoopValueItem);
            // </editor-fold>
            // </editor-fold>
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("proteinPropertySettings.dialogTitle"), tmpPropertyValueItemContainer)) {
                // <editor-fold defaultstate="collapsed" desc="Set changed properties">
                // <editor-fold defaultstate="collapsed" desc="- Set protein backbone probe particles">
                HashMap<String, String> tmpProteinBackboneProbeParticlesMap = new HashMap<String, String>(tmpProteinBackboneProbesValueItem.getMatrixRowCount());
                for (int i = 0; i < tmpProteinBackboneProbesValueItem.getMatrixRowCount(); i++) {
                    if (!tmpProteinBackboneProbesValueItem.getValue(i, 3).equals(GuiMessage.get("ProteinBackboneProbeParticles.NoProbe"))) {
                        tmpProteinBackboneProbeParticlesMap.put(tmpProteinBackboneProbesValueItem.getValue(i, 2), tmpProteinBackboneProbesValueItem.getValue(i, 3));
                    }
                }
                this.pdbToDpd.setProbes(tmpProteinBackboneProbeParticlesMap);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Set protein backbone status and segments">
                boolean[] tmpBackboneParticleStatusArray = this.pdbToDpd.getBackboneParticleStatusArray();
                int[] tmpBackboneParticleSegmentArray = this.pdbToDpd.getBackboneParticleSegmentArray();
                for (int i = 0; i < tmpProteinBackboneStatusValueItem.getMatrixRowCount(); i++) {
                    tmpBackboneParticleStatusArray[i] = tmpProteinBackboneStatusValueItem.getValue(i, 3).equals(GuiMessage.get("ProteinBackboneStatus.Status.On"));
                    tmpBackboneParticleSegmentArray[i] = tmpProteinBackboneStatusValueItem.getValueAsInt(i, 4);
                }
                this.pdbToDpd.setBackboneParticleStatusArray(tmpBackboneParticleStatusArray);
                this.pdbToDpd.setBackboneParticleSegmentArray(tmpBackboneParticleSegmentArray);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="- Set sequence loop">
                // toLowerCase() is inserted for compatibility with earlier versions which used capitalized messages
                if (tmpSequenceLoopValueItem.getValue().toLowerCase().equals(GuiMessage.get("SequenceLoop.HasClosedLoop").toLowerCase())) {
                    this.pdbToDpd.setCircular(true);
                } else {
                    this.pdbToDpd.setCircular(false);
                }

                // </editor-fold>
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Notify change receiver">
                this.changeInformation.setChangeType(ChangeTypeEnum.PROTEIN_DEFINITION_CORRECT_CHANGE);
                super.notifyChangeReceiver(this, this.changeInformation);
                // </editor-fold>
                this.showProteinData();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setProperties()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Set mutant
     */
    private void setMutant() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Set mutant value item container">
            ValueItemContainer tmpMutantValueItemContainer = new ValueItemContainer(this);
            int tmpVerticalPosition = 1;
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set mutant value item">
            String[] tmpNodeNames = new String[]{GuiMessage.get("proteinMutant.Nodename")};
            ValueItem tmpMutantValueItem = GuiUtils.getMutantValueItemForEdit(this.pdbToDpd);
            if (tmpMutantValueItem == null) {
                MouseCursorManagement.getInstance().setDefaultCursor();
                // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setMutant()", "CustomPanelProteinEditController"),
                        GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                // </editor-fold>
                return;
            }
            tmpMutantValueItem.setNodeNames(tmpNodeNames);
            tmpMutantValueItem.setVerticalPosition(tmpVerticalPosition++);
            tmpMutantValueItemContainer.addValueItem(tmpMutantValueItem);
            // </editor-fold>
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("ProteinMutant.dialogTitle"), tmpMutantValueItemContainer)) {
                // <editor-fold defaultstate="collapsed" desc="Set mutant">
                HashMap<String, String> tmpChainNameToIdMap = this.pdbToDpd.getNameChainIDMap();
                HashMap<String, AminoAcid[]> tmpCurrentAminoAcidSequenceMap = this.pdbToDpd.getCurrentAminoAcidSequence();
                for (int i = 0; i < tmpMutantValueItem.getMatrixRowCount(); i++) {
                    String tmpId = tmpChainNameToIdMap.get(tmpMutantValueItem.getValue(i, 0));
                    AminoAcid[] tmpCurrentAminoAcids = tmpCurrentAminoAcidSequenceMap.get(tmpId);
                    // Get replaced amino acid index (NOTE: Starts with index = 1)
                    int tmpReplacedAminoAcidIndexInChain = tmpMutantValueItem.getValueAsInt(i, 1) - 1;
                    if (!tmpCurrentAminoAcids[tmpReplacedAminoAcidIndexInChain].getName().equals(tmpMutantValueItem.getValue(i, 3))) {
                        AminoAcid tmpReplacedAminoAcid = AminoAcids.getInstance().getAminoAcidsUtility().getAminoAcidFromName(tmpMutantValueItem.getValue(i, 3));
                        tmpCurrentAminoAcids[tmpReplacedAminoAcidIndexInChain] = tmpReplacedAminoAcid;
                    }
                }
                for (String tmpId : tmpCurrentAminoAcidSequenceMap.keySet()) {
                    AminoAcid[] tmpReplacedAminoAcids = tmpCurrentAminoAcidSequenceMap.get(tmpId);
                    this.pdbToDpd.setAminoAcidSequence(tmpId, tmpReplacedAminoAcids);
                }
                boolean tmpIsClearNecessary = false;
                if (this.lastPhValue != null) {
                    this.lastPhValue = null;
                    this.pdbToDpd.setPhValue(null);
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.hasProbes()) {
                    this.pdbToDpd.clearProbes();
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.hasStatus()) {
                    this.pdbToDpd.clearStatus();
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.hasSegments()) {
                    this.pdbToDpd.clearSegments();
                    tmpIsClearNecessary = true;
                }
                if (this.pdbToDpd.isCircular()) {
                    this.pdbToDpd.setCircular(false);
                    tmpIsClearNecessary = true;
                }
                if (tmpIsClearNecessary) {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    JOptionPane.showMessageDialog(null, GuiMessage.get("Information.ProteinMutantChange"), GuiMessage.get("Information.NotificationTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                    MouseCursorManagement.getInstance().setWaitCursor();
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Notify change receiver">
                this.changeInformation.setChangeType(ChangeTypeEnum.PROTEIN_DEFINITION_CORRECT_CHANGE);
                super.notifyChangeReceiver(this, this.changeInformation);
                // </editor-fold>
                this.showProteinData();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setMutant()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Set protein rotation
     */
    private void setProteinRotation() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            // <editor-fold defaultstate="collapsed" desc="Set property value item container">
            ValueItemContainer tmpProteinRotationValueItemContainer = new ValueItemContainer(null);
            int tmpVerticalPosition = 1;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set protein rotation value item">
            String[] tmpNodeNames = new String[]{GuiMessage.get("proteinPropertySettings.Nodename")};
            this.jmol3dPeptideController.updateRotationInMasterdata(this.pdbToDpd.getMasterdata());
            ValueItem tmpProteinRotationValueItem = GuiUtils.getProteinRotationValueItem(this.pdbToDpd);
            if (tmpProteinRotationValueItem == null) {
                MouseCursorManagement.getInstance().setDefaultCursor();
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
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("proteinPropertySettings.dialogTitle"), tmpProteinRotationValueItemContainer)) {
                // <editor-fold defaultstate="collapsed" desc="Set protein rotation">
                Vector3d tmpNewRotationInRadians = new Vector3d(
                        tmpProteinRotationValueItem.getValueAsDouble(0, 0) * Math.PI / 180.0,
                        tmpProteinRotationValueItem.getValueAsDouble(0, 1) * Math.PI / 180.0,
                        tmpProteinRotationValueItem.getValueAsDouble(0, 2) * Math.PI / 180.0);
                this.pdbToDpd.setRotation(tmpNewRotationInRadians.x, tmpNewRotationInRadians.y, tmpNewRotationInRadians.z);
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Notify change receiver">
                this.changeInformation.setChangeType(ChangeTypeEnum.PROTEIN_DEFINITION_CORRECT_CHANGE);
                super.notifyChangeReceiver(this, this.changeInformation);
                // </editor-fold>
                this.showProteinData();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setProteinRotation()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Set current protein rotation
     */
    private void setCurrentProteinRotation() {
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            this.jmol3dPeptideController.updateRotationInMasterdata(this.pdbToDpd.getMasterdata());
            this.pdbToDpd.getMasterdata().copyRotationToDefault();
            // <editor-fold defaultstate="collapsed" desc="Notify change receiver">
            this.changeInformation.setChangeType(ChangeTypeEnum.PROTEIN_DEFINITION_CORRECT_CHANGE);
            super.notifyChangeReceiver(this, this.changeInformation);
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setCurrentProteinRotation()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Shows protein data
     */
    private void showProteinData() {
        try {
            this.customPanelProteinEdit.getProteinDefinitionInfoLabel().setText(this.pdbToDpd.getTitle());
            this.customPanelProteinEdit.getProteinDefinitionInfoLabel().setToolTipText(this.pdbToDpd.getTitle());

            this.setFeatureWidgetVisibility(true);

            this.customPanelProteinEdit.getPropertySequencesTextArea().setText(this.pdbToDpd.getSequences());
            this.customPanelProteinEdit.getPropertySequencesTextArea().setCaretPosition(0);

            this.customPanelProteinEdit.getPropertySpicesTextArea().setText(this.pdbToDpd.getSpices());
            this.customPanelProteinEdit.getPropertySpicesTextArea().setCaretPosition(0);

            this.customPanelProteinEdit.getPropertyPdbFileContentTextArea().setText(this.pdbToDpd.getPdb());
            this.customPanelProteinEdit.getPropertyPdbFileContentTextArea().setCaretPosition(0);

            this.jmol3dPeptideController.showProteinFromMasterdata(this.pdbToDpd.getMasterdata());
            this.jmol3dPeptideController.updateRotationInMasterdata(this.pdbToDpd.getMasterdata());
            this.pdbToDpd.getMasterdata().copyRotationToDefault();

            this.customPanelProteinEdit.getProteinPropertyPanel().setVisible(true);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "showProteinData", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Clears all settings
     */
    private void clearSettings() {
        if (!GuiUtils.getYesNoDecision(GuiMessage.get("Protein.AskForClearSettings.Title"), GuiMessage.get("Protein.AskForClearSettings"))) {
            return;
        }
        try {
            // Delete mutants
            this.pdbToDpd.restoreOriginalAminoAcidSequence();
            // Set all chains
            HashMap<String, String> tmpCompoundToChainIdMap = this.pdbToDpd.getNameChainIDMap();
            ArrayList<String> tmpActiveChainIdList = new ArrayList<String>();
            for (String tmpChainId : tmpCompoundToChainIdMap.values()) {
                tmpActiveChainIdList.add(tmpChainId);
            }
            this.pdbToDpd.setActiveChains(tmpActiveChainIdList);
            // Clear probes, status and segments
            this.pdbToDpd.clearProbes();
            this.pdbToDpd.clearStatus();
            this.pdbToDpd.clearSegments();
            // pH value
            this.pdbToDpd.setPhValue(null);
            this.lastPhValue = null;
            // <editor-fold defaultstate="collapsed" desc="Notify change receiver">
            this.changeInformation.setChangeType(ChangeTypeEnum.PROTEIN_DEFINITION_CORRECT_CHANGE);
            super.notifyChangeReceiver(this, this.changeInformation);
            // </editor-fold>
            this.showProteinData();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "clearSettings()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Sets protein viewer graphics preferences
     */
    private void setGraphicsPreferences() {
        try {
            ValueItemContainer tmpProteinViewerEditablePreferencesValueItemContainer = Preferences.getInstance().getProteinViewerEditablePreferencesValueItemContainer();
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("PreferencesProteinViewerGraphicsSettingsDialog.title"), tmpProteinViewerEditablePreferencesValueItemContainer)) {
                MouseCursorManagement.getInstance().setWaitCursor();
                Preferences.getInstance().setEditablePreferences(tmpProteinViewerEditablePreferencesValueItemContainer);
                this.jmol3dPeptideController.setBackgroundColor(Preferences.getInstance().getProteinViewerBackgroundColor());
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setGraphicsPreferences()", "CustomPanelProteinEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }

    /**
     * Sets feature button visibility
     *
     * @param aValue True: Feature buttons are visible, false: Otherwise
     */
    private void setFeatureWidgetVisibility(boolean aValue) {
        this.customPanelProteinEdit.getBioAssemblyComboBox().setVisible(aValue);
        this.customPanelProteinEdit.getSetChainButton().setVisible(aValue);
        this.customPanelProteinEdit.getSetPhButton().setVisible(aValue);
        this.customPanelProteinEdit.getSetProteinPropertiesButton().setVisible(aValue);
        this.customPanelProteinEdit.getSetMutantButton().setVisible(aValue);
        this.customPanelProteinEdit.getClearSettingsButton().setVisible(aValue);
    }
    // </editor-fold>

}
