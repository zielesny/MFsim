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
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.util.FastListModel;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.util.MiscUtilityMethods;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.peptide.PeptideToSpices;
import java.awt.event.*;
import java.util.Stack;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.event.*;
import org.apache.commons.lang3.StringUtils;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Controller class for CustomPanelPeptideEdit
 *
 * @author Achim Zielesny
 */
public class CustomPanelPeptideEditController extends ChangeNotifier {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Utility misc methods
     */
    private MiscUtilityMethods miscUtilityMethods = new MiscUtilityMethods();

    /**
     * Panel this controller is made for
     */
    CustomPanelPeptideEdit customPanelPeptide;

    /**
     * PeptideToFSmile instance
     */
    PeptideToSpices peptideToSpices;

    /**
     * True: Current peptide is correct, false: Otherwise
     */
    private boolean isCorrectPeptide;

    /**
     * Stack for undo operations
     */
    private Stack<String> undoStack;

    /**
     * Change information
     */
    private ChangeInformation changeInformation;

    /**
     * Old image resource filename
     */
    private String oldImageResourceFilename;

    /**
     * pH information string
     */
    private String pHInfo;

    /**
     * Last defined pH value
     */
    private String lastPhValue;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private regex patterns">
    /**
     * Forbidden characters for peptide definition (without amino acid
     * one-letter codes)
     */
    private Pattern peptideForbiddenCharacters = Pattern.compile("[^0-9NCS\\[\\]\\{\\}\\-\\+\\*]");
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelPeptide Panel this controller is made for
     * @param aPeptideToSpices PeptideToFSmile instance
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelPeptideEditController(CustomPanelPeptideEdit aCustomPanelPeptide, PeptideToSpices aPeptideToSpices) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelPeptide == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aPeptideToSpices == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.customPanelPeptide = aCustomPanelPeptide;
            this.peptideToSpices = aPeptideToSpices;
            this.isCorrectPeptide = false;
            this.undoStack = new Stack<String>();
            this.changeInformation = new ChangeInformation();
            this.oldImageResourceFilename = null;
            this.pHInfo = null;
            this.lastPhValue = null;
            // Amino acids MUST already be initialized

            // <editor-fold defaultstate="collapsed" desc="- Peptide settings">
            if (!Preferences.getInstance().hasPreviousPeptides()) {
                // Disable previous definitions tab if previous definitions do not exist
                this.customPanelPeptide.getInfoTabbedPanel().setEnabledAt(2, false);
            }
            this.customPanelPeptide.getPeptideTextArea().setToolTipText(GuiMessage.get("CustomPanelPeptideEdit.inputTextField.toolTipText"));

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set description for peptide syntax">
            this.customPanelPeptide.getDescriptionTextArea().setText(ModelMessage.get("Peptide.DescriptionOneLetter"));
            this.customPanelPeptide.getDescriptionTextArea().setCaretPosition(0);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set symbols and amino acids">
            // Since list starts with symbol set symbol image
            GuiUtils.setResourceImage(this.customPanelPeptide.getAminoAcidImagePanel(), GuiDefinitions.AMINO_ACID_SYMBOL_FILENAME);

            FastListModel tmpAminoAcidsModel = new FastListModel();
            tmpAminoAcidsModel.setListenersEnabled(false);
            this.customPanelPeptide.getAminoAcidsInfoList().setModel(tmpAminoAcidsModel);
            int tmpCount = 0;
            // Amino acids
            String[] tmpAminoAcidNames = StandardParticleInteractionData.getInstance().getSortedAminoAcidNames();
            for (int i = 0; i < tmpAminoAcidNames.length; i++) {
                tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"),
                        StandardParticleInteractionData.getInstance().getAminoAcidDescriptionForName(tmpAminoAcidNames[i]).getOneLetterCode(), tmpAminoAcidNames[i]));
                tmpCount++;
            }
            tmpAminoAcidsModel.addElement(" ");
            tmpCount++;
            // Symbols
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.SSConnectionOpenSymbol"),
                    GuiMessage.get("DialogPeptideEdit.SSConnectionOpenText")));
            tmpCount++;
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.SSConnectionCloseSymbol"),
                    GuiMessage.get("DialogPeptideEdit.SSConnectionCloseText")));
            tmpCount++;
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.ChargeOpenSymbol"),
                    GuiMessage.get("DialogPeptideEdit.ChargeOpenText")));
            tmpCount++;
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.ChargeCloseSymbol"),
                    GuiMessage.get("DialogPeptideEdit.ChargeCloseText")));
            tmpCount++;
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.ChargedAminoSymbol"),
                    GuiMessage.get("DialogPeptideEdit.ChargedAminoText")));
            tmpCount++;
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.ChargedCarboxylicAcidSymbol"),
                    GuiMessage.get("DialogPeptideEdit.ChargedCarboxylicAcidText")));
            tmpCount++;
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.ChargedPositiveSidechainSymbol"),
                    GuiMessage.get("DialogPeptideEdit.ChargedPositiveSidechainText")));
            tmpCount++;
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.ChargedNegativeSidechainSymbol"),
                    GuiMessage.get("DialogPeptideEdit.ChargedNegativeSidechainText")));
            tmpCount++;
            tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), GuiMessage.get("DialogPeptideEdit.PeptideLoop"),
                    GuiMessage.get("DialogPeptideEdit.PeptideSequenceLoopText")));
            tmpCount++;
            // Digits
            tmpAminoAcidsModel.addElement(" ");
            tmpCount++;
            for (int i = 0; i < 10; i++) {
                tmpAminoAcidsModel.addElement(String.format(GuiMessage.get("DialogPeptideEdit.aminoAcidInfoString"), String.valueOf(i).trim(),
                        GuiMessage.get("DialogPeptideEdit.FrequencyDigit")));
                tmpCount++;
            }
            tmpAminoAcidsModel.setListenersEnabled(true);
            tmpAminoAcidsModel.fireIntervalAdded(tmpAminoAcidsModel, 0, tmpCount);
            this.customPanelPeptide.getAminoAcidsInfoList().setSelectedIndex(0);

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            this.customPanelPeptide.getPeptideTextArea().addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent aKeyEvent) {
                    String tmpKeyCharacter = String.valueOf(aKeyEvent.getKeyChar());
                    if (CustomPanelPeptideEditController.this.peptideForbiddenCharacters.matcher(tmpKeyCharacter).matches()
                            && !CustomPanelPeptideEditController.this.isAminoAcid(String.valueOf(aKeyEvent.getKeyChar()))) {
                        aKeyEvent.consume();
                    } else {
                        // Set undo stack
                        CustomPanelPeptideEditController.this.undoStack.push(CustomPanelPeptideEditController.this.customPanelPeptide.getPeptideTextArea().getText());
                    }
                }

                @Override
                public void keyReleased(KeyEvent aKeyEvent) {
                    // Do nothing
                }

                @Override
                public void keyPressed(KeyEvent aKeyEvent) {
                    // Suppress tab key
                    if(aKeyEvent.getKeyChar() == '\t'){
                        aKeyEvent.consume();
                    }                    
                    if (Character.isISOControl(aKeyEvent.getKeyChar())) {
                        // Control character like backspace, delete etc.
                        // Set undo stack
                        CustomPanelPeptideEditController.this.undoStack.push(CustomPanelPeptideEditController.this.customPanelPeptide.getPeptideTextArea().getText());
                    }
                }

            });
            this.customPanelPeptide.getPeptideTextArea().getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.performSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "insertUpdate()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void removeUpdate(DocumentEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.performSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "removeUpdate()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                public void changedUpdate(DocumentEvent e) {
                    // NOTE: Plain text components don't fire these events, so do nothing
                }

            });
            this.customPanelPeptide.getInfoTabbedPanel().addChangeListener(new ChangeListener() {

                public void stateChanged(final ChangeEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.setPreviousDefinitions();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getUsePreviousDefinitionButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.usePreviousDefinition();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getRemovePreviousDefinitionButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.removePreviousDefinition();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getClearPreviousDefinitionsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.clearPreviousDefinitions();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getAppendAminoAcidButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.appendToPeptide();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getUndoButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.undoLastOperation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getPhButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.setPhValue();

                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getClearPeptideButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.clearPeptide();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getDischargeButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelPeptideEditController.this.discharge();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelPeptideEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelPeptide.getAminoAcidsInfoList().addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        // Show amino acid image
                        CustomPanelPeptideEditController.this.showAminoAcidImage();
                    }
                    if (e.getClickCount() == 2) {
                        // Show amino acid image
                        CustomPanelPeptideEditController.this.showAminoAcidImage();
                        // Double click: Append symbol or amino acid to peptide
                        CustomPanelPeptideEditController.this.appendToPeptide();
                    }
                }

            });
            this.customPanelPeptide.getAminoAcidsInfoList().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    // Show amino acid image
                    CustomPanelPeptideEditController.this.showAminoAcidImage();
                }

            });
            this.customPanelPeptide.getPreviousDefinitionsList().addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        // Double click: Use previous definition
                        CustomPanelPeptideEditController.this.usePreviousDefinition();
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Clear peptide text areas">
            this.customPanelPeptide.getPeptideTextArea().setText("");
            this.customPanelPeptide.getThreeLetterCodeTextArea().setText("");
            this.customPanelPeptide.getPeptideTextArea().requestFocus();
            this.performSettings();
            this.showAminoAcidImage();

            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelPeptideEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Displays three-letter code of peptide
     */
    private void displayThreeLetterCode() {
        String tmpPeptide = this.customPanelPeptide.getPeptideTextArea().getText();
        if (tmpPeptide == null || tmpPeptide.isEmpty()) {
            this.customPanelPeptide.getThreeLetterCodeTextArea().setText("");
        } else {
            if (this.isCorrectPeptide) {
                try {
                    MouseCursorManagement.getInstance().setWaitCursor();
                    this.customPanelPeptide.getThreeLetterCodeTextArea().setText(this.peptideToSpices.convertOneToThreeLetterDefinition(this.customPanelPeptide.getPeptideTextArea().getText()));
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    this.customPanelPeptide.getThreeLetterCodeTextArea().setText("");
                } finally {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                }
            } else {
                this.customPanelPeptide.getThreeLetterCodeTextArea().setText("");
            }
        }
    }

    /**
     * Performs settings
     */
    private void performSettings() {
        // Remove all whitespace characters
        // Old code:
        // String tmpPeptide = this.customPanelPeptide.getPeptideTextArea().getText().replaceAll("\\s+", "");
        String tmpPeptide = StringUtils.deleteWhitespace(this.customPanelPeptide.getPeptideTextArea().getText());
        if (tmpPeptide == null || tmpPeptide.isEmpty()) {
            this.customPanelPeptide.getPeptideInfoLabel().setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
            this.customPanelPeptide.getPeptideInfoLabel().setText(GuiMessage.get("CustomPanelPeptideEdit.peptideInfoLabel.text"));
            // Notify change receiver to disable Apply-Button
            this.changeInformation.setChangeType(ChangeTypeEnum.PEPTIDE_SYNTAX_NO_CHANGE);
            super.notifyChangeReceiver(this, this.changeInformation);
        } else {
            String tmpErrorString = this.peptideToSpices.checkPeptideDefinitonOneLetterCode(tmpPeptide);
            if (tmpErrorString.isEmpty()) {
                // <editor-fold defaultstate="collapsed" desc="Peptide is valid">
                // NOTE: Correct peptide: Apply-button is made visible in document listener
                this.customPanelPeptide.getPeptideInfoLabel().setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
                if (this.pHInfo == null || this.pHInfo.isEmpty()) {
                    this.customPanelPeptide.getPeptideInfoLabel().setText(GuiMessage.get("DialogPeptideEdit.definitionInfoLabel.text"));
                } else {
                    this.customPanelPeptide.getPeptideInfoLabel().setText(String.format(GuiMessage.get("DialogPeptideEdit.definitionInfoLabel.textPlusPhValueFormat"), this.pHInfo));
                    this.pHInfo = null;
                    this.lastPhValue = null;
                }
                this.isCorrectPeptide = true;

                // Notify change receiver
                if (this.customPanelPeptide.getPeptideTextArea().getText().isEmpty()) {
                    this.changeInformation.setChangeType(ChangeTypeEnum.PEPTIDE_SYNTAX_NO_CHANGE);
                    super.notifyChangeReceiver(this, this.changeInformation);
                } else {
                    this.changeInformation.setChangeType(ChangeTypeEnum.PEPTIDE_SYNTAX_CORRECT_CHANGE);
                    super.notifyChangeReceiver(this, this.changeInformation);
                }

                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Peptide has failure">
                // Invalid peptide/monomer: Make apply-button invisible
                this.customPanelPeptide.getPeptideInfoLabel().setForeground(GuiDefinitions.PANEL_INFO_WRONG_COLOR);
                this.customPanelPeptide.getPeptideInfoLabel().setText(String.format(GuiMessage.get("Peptide.Error"), tmpErrorString));
                this.isCorrectPeptide = false;
                // Notify change receiver to disable Apply-Button
                this.changeInformation.setChangeType(ChangeTypeEnum.PEPTIDE_SYNTAX_ERROR_CHANGE);
                super.notifyChangeReceiver(this, this.changeInformation);

                // </editor-fold>
            }
        }
        this.displayThreeLetterCode();
        this.setButtonVisibility();
    }

    /**
     * Appends selected symbol or amino acid to peptide
     */
    private void appendToPeptide() {
        String tmpSelectedItem = this.customPanelPeptide.getAminoAcidsInfoList().getSelectedValue().toString();
        if (!tmpSelectedItem.trim().isEmpty()) {
            String tmpAppendItem = tmpSelectedItem.substring(0, tmpSelectedItem.indexOf(" ")).trim();
            if (this.customPanelPeptide.getPeptideTextArea().getText() == null || this.customPanelPeptide.getPeptideTextArea().getText().isEmpty()) {
                // Set undo stack
                this.undoStack.push("");
                this.customPanelPeptide.getPeptideTextArea().setText(tmpAppendItem);
            } else {
                // Set undo stack
                this.undoStack.push(this.customPanelPeptide.getPeptideTextArea().getText());
                this.customPanelPeptide.getPeptideTextArea().setText(this.peptideToSpices.removeOneLetterCharges(this.customPanelPeptide.getPeptideTextArea().getText()) + tmpAppendItem);
            }
            this.customPanelPeptide.getPeptideTextArea().setCaretPosition(this.customPanelPeptide.getPeptideTextArea().getText().length());
            this.customPanelPeptide.getPeptideTextArea().requestFocus();
        }
    }

    /**
     * Undo last operations
     */
    private void undoLastOperation() {
        if (this.undoStack.size() > 0) {
            this.customPanelPeptide.getPeptideTextArea().setText(this.undoStack.pop());
        }
    }

    /**
     * Shows amino acid image
     */
    private void showAminoAcidImage() {
        String tmpSelectedItem = this.customPanelPeptide.getAminoAcidsInfoList().getSelectedValue().toString();
        if (!tmpSelectedItem.trim().isEmpty()) {
            String tmpShowItem = tmpSelectedItem.substring(0, tmpSelectedItem.indexOf(" ")).trim();
            if (this.isAminoAcid(tmpShowItem)) {
                String tmpImageResourceFilename = GuiDefinitions.AMINO_ACID_IMAGE_FILENAME_PREFIX + tmpShowItem + GuiDefinitions.RESOURCE_IMAGE_ENDING;
                if (!tmpImageResourceFilename.equals(this.oldImageResourceFilename)) {
                    if (GuiUtils.hasImageOfResource(tmpImageResourceFilename)) {
                        GuiUtils.setResourceImage(this.customPanelPeptide.getAminoAcidImagePanel(), tmpImageResourceFilename);
                        this.oldImageResourceFilename = tmpImageResourceFilename;
                    } else {
                        GuiUtils.setResourceImage(this.customPanelPeptide.getAminoAcidImagePanel(), GuiDefinitions.AMINO_ACID_AMINO_ACID_FILENAME);
                        this.oldImageResourceFilename = null;
                    }
                }
            } else {
                if (!GuiDefinitions.AMINO_ACID_SYMBOL_FILENAME.equals(this.oldImageResourceFilename)) {
                    GuiUtils.setResourceImage(this.customPanelPeptide.getAminoAcidImagePanel(), GuiDefinitions.AMINO_ACID_SYMBOL_FILENAME);
                    this.oldImageResourceFilename = GuiDefinitions.AMINO_ACID_SYMBOL_FILENAME;
                }
            }
        } else {
            if (!GuiDefinitions.AMINO_ACID_EMPTY_FILENAME.equals(this.oldImageResourceFilename)) {
                GuiUtils.setResourceImage(this.customPanelPeptide.getAminoAcidImagePanel(), GuiDefinitions.AMINO_ACID_EMPTY_FILENAME);
                this.oldImageResourceFilename = GuiDefinitions.AMINO_ACID_EMPTY_FILENAME;
            }
        }
    }

    /**
     * Clears peptide
     */
    private void clearPeptide() {
        // Set undo stack
        this.undoStack.push(this.customPanelPeptide.getPeptideTextArea().getText());
        this.customPanelPeptide.getPeptideTextArea().setText("");
        this.customPanelPeptide.getPeptideTextArea().requestFocus();
    }

    /**
     * Removes any charges from peptide definition
     */
    private void discharge() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isCorrectPeptide) {
            return;
        }
        String tmpPeptide = this.customPanelPeptide.getPeptideTextArea().getText();
        if (tmpPeptide == null || tmpPeptide.isEmpty()) {
            return;
        }

        // </editor-fold>
        this.lastPhValue = null;
        this.customPanelPeptide.getPeptideTextArea().setText(this.peptideToSpices.removeOneLetterCharges(this.customPanelPeptide.getPeptideTextArea().getText()));
        this.customPanelPeptide.getPeptideTextArea().setCaretPosition(this.customPanelPeptide.getPeptideTextArea().getText().length());
        this.customPanelPeptide.getPeptideTextArea().requestFocus();
    }

    /**
     * Sets pH value
     */
    private void setPhValue() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (!this.isCorrectPeptide) {
            return;
        }
        String tmpPeptide = this.customPanelPeptide.getPeptideTextArea().getText();
        if (tmpPeptide == null || tmpPeptide.isEmpty()) {
            return;
        }

        // </editor-fold>
        try {

            // <editor-fold defaultstate="collapsed" desc="Set pH-ValueItem and container">
            ValueItem tmpPhValueItem = new ValueItem();
            tmpPhValueItem.setNodeNames(new String[]{GuiMessage.get("pH.Nodename")});
            String tmpPhValueItemName = "PH_VALUE";
            tmpPhValueItem.setName(tmpPhValueItemName);
            tmpPhValueItem.setDisplayName(GuiMessage.get("pH.DisplayName"));
            // Set editable and numeric-null allowed
            tmpPhValueItem.setDefaultTypeFormat(new ValueItemDataTypeFormat(1, 0.0, 14.0, true, true));
            if (this.lastPhValue == null || this.lastPhValue.isEmpty()) {
                // Set to numeric-null
                tmpPhValueItem.setValue(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"));
            } else {
                // Set to last pH value
                tmpPhValueItem.setValue(this.lastPhValue);
            }
            tmpPhValueItem.setVerticalPosition(1);
            tmpPhValueItem.setDescription(GuiMessage.get("pH.Description"));

            ValueItemContainer tmpPhValueItemContainer = new ValueItemContainer(null);
            tmpPhValueItemContainer.addValueItem(tmpPhValueItem);

            // </editor-fold>
            if (DialogValueItemEdit.hasChanged(GuiMessage.get("pH.dialogTitle"), tmpPhValueItemContainer)) {
                // Check that not numeric-null
                if (tmpPhValueItemContainer.getValueItem(tmpPhValueItemName).getValue().equals(ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString"))) {
                    this.lastPhValue = null;
                    // Set undo stack
                    this.undoStack.push(this.customPanelPeptide.getPeptideTextArea().getText());
                    this.customPanelPeptide.getPeptideTextArea().setText(this.peptideToSpices.removeOneLetterCharges(this.customPanelPeptide.getPeptideTextArea().getText()));
                    this.customPanelPeptide.getPeptideTextArea().setCaretPosition(this.customPanelPeptide.getPeptideTextArea().getText().length());
                    this.customPanelPeptide.getPeptideTextArea().requestFocus();
                } else {
                    this.pHInfo = String.format(GuiMessage.get("DialogPeptideEdit.pHInfoFormat"), tmpPhValueItemContainer.getValueItem(tmpPhValueItemName).getValue());
                    this.lastPhValue = tmpPhValueItemContainer.getValueItem(tmpPhValueItemName).getValue();
                    double tmpPhValue = tmpPhValueItemContainer.getValueItem(tmpPhValueItemName).getValueAsDouble();
                    // Set undo stack
                    this.undoStack.push(this.customPanelPeptide.getPeptideTextArea().getText());
                    this.customPanelPeptide.getPeptideTextArea().setText(this.peptideToSpices.chargeOneLetterPeptide(this.customPanelPeptide.getPeptideTextArea().getText(), tmpPhValue));
                    this.customPanelPeptide.getPeptideTextArea().setCaretPosition(this.customPanelPeptide.getPeptideTextArea().getText().length());
                    this.customPanelPeptide.getPeptideTextArea().requestFocus();
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message that method failed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setPhValue()", "CustomPanelPeptideEditController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    /**
     * Checks if string may represent a valid amino acid
     *
     * @param aOneLetterCode String with one-letter code of amino acid
     * @return True: String may represent a valid amino acid, false: Otherwise
     */
    private boolean isAminoAcid(String aOneLetterCode) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aOneLetterCode == null || aOneLetterCode.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return StandardParticleInteractionData.getInstance().hasAminoAcidDescription(aOneLetterCode);
    }

    /**
     * Sets visibility of buttons
     */
    private void setButtonVisibility() {
        this.customPanelPeptide.getAppendAminoAcidButton().setVisible(!this.customPanelPeptide.getAminoAcidsInfoList().isSelectionEmpty());
        this.customPanelPeptide.getUndoButton().setVisible(this.undoStack.size() > 0);
        String tmpPeptide = this.customPanelPeptide.getPeptideTextArea().getText();
        this.customPanelPeptide.getPhButton().setVisible(this.isCorrectPeptide && tmpPeptide != null && !tmpPeptide.isEmpty());
        this.customPanelPeptide.getDischargeButton().setVisible(this.isCorrectPeptide && tmpPeptide != null && !tmpPeptide.isEmpty() && this.peptideToSpices.hasOneLetterCharge(tmpPeptide));
        this.customPanelPeptide.getClearPeptideButton().setVisible(tmpPeptide != null && !tmpPeptide.isEmpty());
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Previous definitions related methods">
    /**
     * Clears previous definitions
     */
    private void clearPreviousDefinitions() {
        if (GuiUtils.getYesNoDecision(GuiMessage.get("ClearPreviousDefinition.FrameTitle"), GuiMessage.get("ClearPreviousDefinition.Message"))) {
            Preferences.getInstance().clearPreviousPeptides();
            // Select three-letter code tab
            this.customPanelPeptide.getInfoTabbedPanel().setSelectedIndex(0);
            // Disable previous definitions tab since previous definitions do not exist
            this.customPanelPeptide.getInfoTabbedPanel().setEnabledAt(2, false);
        }
    }

    /**
     * Removes selected previous definition
     */
    private void removePreviousDefinition() {
        if (GuiUtils.getYesNoDecision(GuiMessage.get("RemovePreviousDefinition.FrameTitle"), GuiMessage.get("RemovePreviousDefinition.Message"))) {
            int tmpSelectedIndex = this.customPanelPeptide.getPreviousDefinitionsList().getSelectedIndex();
            this.customPanelPeptide.getPreviousDefinitionsList().remove(tmpSelectedIndex);
            String tmpDefinitionToBeRemoved = this.customPanelPeptide.getPreviousDefinitionsList().getSelectedValue().toString();
            boolean tmpHasDefinitions = false;
            Preferences.getInstance().removePreviousPeptide(tmpDefinitionToBeRemoved);
            tmpHasDefinitions = Preferences.getInstance().hasPreviousPeptides();
            if (tmpHasDefinitions) {
                if (tmpSelectedIndex == this.customPanelPeptide.getPreviousDefinitionsList().getModel().getSize()) {
                    this.customPanelPeptide.getPreviousDefinitionsList().setSelectedIndex(tmpSelectedIndex - 1);
                } else {
                    this.customPanelPeptide.getPreviousDefinitionsList().setSelectedIndex(tmpSelectedIndex);
                }
            } else {
                // Select graphics tab
                this.customPanelPeptide.getInfoTabbedPanel().setSelectedIndex(0);
                // Disable previous definitions tab since previous definitions do not exist
                this.customPanelPeptide.getInfoTabbedPanel().setEnabledAt(2, false);
            }
        }
    }

    /**
     * Sets previous definitions
     */
    private void setPreviousDefinitions() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.customPanelPeptide.getInfoTabbedPanel().getSelectedIndex() != 2) {
            return;
        }
        if (this.customPanelPeptide.getPreviousDefinitionsList().getModel().getSize() > 0) {
            return;
        }

        // </editor-fold>
        MouseCursorManagement.getInstance().setWaitCursor();
        FastListModel tmpPreviousDefinitionsModel = new FastListModel();
        this.customPanelPeptide.getPreviousDefinitionsList().setModel(tmpPreviousDefinitionsModel);
        tmpPreviousDefinitionsModel.setListenersEnabled(false);
        for (String tmpPreviousPeptide : Preferences.getInstance().getPreviousPeptides()) {
            tmpPreviousDefinitionsModel.addElement(tmpPreviousPeptide);
        }
        tmpPreviousDefinitionsModel.setListenersEnabled(true);
        tmpPreviousDefinitionsModel.fireIntervalAdded(tmpPreviousDefinitionsModel, 0, Preferences.getInstance().getPreviousPeptides().size());
        this.customPanelPeptide.getPreviousDefinitionsList().setSelectedIndex(0);
        MouseCursorManagement.getInstance().setDefaultCursor();
    }

    /**
     * Uses previous definition
     */
    private void usePreviousDefinition() {
        // Set undo stack
        this.undoStack.push(this.customPanelPeptide.getPeptideTextArea().getText());
        this.customPanelPeptide.getPeptideTextArea().setText(this.customPanelPeptide.getPreviousDefinitionsList().getSelectedValue().toString());
        this.customPanelPeptide.getPeptideTextArea().setCaretPosition(0);
        // Switch to graphics panel: Graphics will automatically be redrawn by text field event above
        this.customPanelPeptide.getInfoTabbedPanel().setSelectedIndex(0);
    }
    // </editor-fold>
    // </editor-fold>

}
