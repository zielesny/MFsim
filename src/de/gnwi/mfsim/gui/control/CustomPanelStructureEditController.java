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

import de.gnwi.mfsim.model.util.FastListModel;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeNotifier;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.spices.SpicesInner;
import de.gnwi.spices.Spices;
import de.gnwi.mfsim.gui.dialog.DialogPeptideEdit;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.util.ImageSelection;
import de.gnwi.spicestographstream.SpicesToGraphStream;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.*;
import org.graphstream.ui.graphicGraph.GraphicElement;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Controller class for CustomPanelStructureEdit
 *
 * @author Achim Zielesny
 */
public class CustomPanelStructureEditController extends ChangeNotifier {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Panel this controller is made for
     */
    private CustomPanelStructureEdit customPanelStructureEdit;

    /**
     * True: Monomer is to be edit, false: Structure is to be edit
     */
    boolean isMonomer;

    /**
     * Available particles
     */
    private HashMap<String, String> availableParticles;

    /**
     * Available monomers
     */
    private HashMap<String, String> availableMonomers;

    /**
     * True: Current structure is correct, false: Otherwise
     */
    private boolean isCorrectStructure;

    /**
     * True: Current structure can be displayed graphically, false: Otherwise
     */
    private boolean isDisplayStructure;

    /**
     * Stack for undo operations
     */
    private Stack<String> undoStack;

    /**
     * Change information
     */
    private ChangeInformation changeInformation;

    /**
     * Initial structure (monomer or molecular structure)
     */
    private String initialStructure;

    /**
     * Hashmap for part to structure mapping
     */
    private HashMap<String, String> partToStructureMap;

    /**
     * Current part structure
     */
    private String currentPartStructure;

    /**
     * Old image resource filename
     */
    private String oldImageResourceFilename;
    
    /**
     * Last valid Spices
     */
    private SpicesGraphics lastValidSpices;

    /**
     * SpicesToGraphStream for Spices conversion to GraphStream graph
     */
    private SpicesToGraphStream spicesToGraphStream;
    
    /**
     * Spices for display
     */
    private Spices spicesForDisplay;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private regex patterns">
    /**
     * Pattern for particle identification
     */
    private Pattern particlePattern = Pattern.compile(ModelDefinitions.PARTICLE_REGEX_PATTERN_STRING);

    /**
     * Pattern for allowed characters in a molecular structure
     */
    private Pattern structureAllowedCharactersPattern = Pattern.compile("[a-zA-Z0-9\\<\\>\\(\\)\\[\\]\\-\\#\\']");

    /**
     * Pattern for allowed characters in a monomer structure
     */
    private Pattern monomerAllowedCharactersPattern = Pattern.compile("[A-Z0-9\\(\\)\\[\\]\\{\\}\\-]");

    /**
     * Pattern for opening characters in a molecular structure
     */
    private Pattern structureOpeningCharactersPattern = Pattern.compile("[\\<\\(\\[\\-]");

    /**
     * Pattern for opening characters in a monomer
     */
    private Pattern monomerOpeningCharactersPattern = Pattern.compile("[\\(\\[\\{\\-]");

    /**
     * Pattern for ending with particle
     */
    private Pattern particleEndingPattern = Pattern.compile(".*[a-zA-Z]+[0-9]+$");

    /**
     * Pattern for ending with frequency number
     */
    private Pattern frequencyNumberEndingPattern = Pattern.compile("^[0-9]+|.*[^a-zA-Z][0-9]+$");

    /**
     * Pattern for characters in a molecular structure with no previous bonding
     */
    private Pattern structureNoPreviousBondingCharactersPattern = Pattern.compile("[\\<\\>\\(\\)\\[\\]\\-\\']");

    /**
     * Pattern for backbone attribute
     */
    private Pattern backboneAttributePattern = Pattern.compile("\\'[0-9]+\\'");

    /**
     * Pattern for ending with backbone symbol but NOT backbone attribute
     */
    private Pattern backboneSymbolEndingPattern = Pattern.compile(".*[^\\'][^0-9]+\\'$");
    
    /**
     * Pattern for characters in a monomer with no previous bonding
     */
    private Pattern monomerNoPreviousBondingCharactersPattern = Pattern.compile("[\\(\\)\\[\\]\\}\\-]");

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelStructureEdit Panel this controller is made for
     * @param anIsMonomer True: Monomer is to be edit, false: Structure is to be
     * edit
     * @param anAvailableParticles Available particles (is NOT allowed to be
     * null)
     * @param anAvailableMonomers Available monomers (may be null)
     * @param anInitialStructure Initial structure (monomer or molecular
     * structure, is NOT allowed to be null)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelStructureEditController(
        CustomPanelStructureEdit aCustomPanelStructureEdit, 
        boolean anIsMonomer, 
        HashMap<String, String> anAvailableParticles,
        HashMap<String, String> anAvailableMonomers, 
        String anInitialStructure
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelStructureEdit == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (anAvailableParticles == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (anInitialStructure == null || anInitialStructure.isEmpty()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.customPanelStructureEdit = aCustomPanelStructureEdit;
            this.isMonomer = anIsMonomer;
            this.availableParticles = anAvailableParticles;
            this.availableMonomers = anAvailableMonomers;
            this.initialStructure = anInitialStructure;
            this.currentPartStructure = null;
            this.partToStructureMap = null;
            this.oldImageResourceFilename = null;
            this.lastValidSpices = null;
            this.spicesForDisplay = null;
            this.spicesToGraphStream = new SpicesToGraphStream();

            this.isCorrectStructure = false;
            this.isDisplayStructure = false;

            this.undoStack = new Stack<String>();

            this.changeInformation = new ChangeInformation();

            // <editor-fold defaultstate="collapsed" desc="- Monomer and molecule settings">
            if (this.isMonomer) {

                // <editor-fold defaultstate="collapsed" desc="Monomer settings">
                if (!Preferences.getInstance().hasPreviousMonomers()) {
                    // Disable previous definitions tab if previous definitions do not exist
                    this.customPanelStructureEdit.getInfoTabbedPanel().setEnabledAt(2, false);
                }
                this.customPanelStructureEdit.getStructureTextArea().setToolTipText(GuiMessage.get("CustomPanelStructureEdit.Monomer.inputTextField.toolTipText"));

                // </editor-fold>
            } else {

                // <editor-fold defaultstate="collapsed" desc="Molecular structure settings">
                if (!Preferences.getInstance().hasPreviousStructures()) {
                    // Disable previous definitions tab if previous definitions do not exist
                    this.customPanelStructureEdit.getInfoTabbedPanel().setEnabledAt(2, false);
                }
                this.customPanelStructureEdit.getStructureTextArea().setToolTipText(GuiMessage.get("CustomPanelStructureEdit.Structure.inputTextField.toolTipText"));

                // </editor-fold>
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set description for structure syntax">
            this.customPanelStructureEdit.getDescriptionTextArea().setText(GuiMessage.get("CustomPanelStructureEdit.Structure.descriptionTextArea.text"));
            this.customPanelStructureEdit.getDescriptionTextArea().setCaretPosition(0);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Hide structure-too-complex info label and select-part combo box">
            this.customPanelStructureEdit.getStructureTooComplexLabel().setVisible(false);
            this.customPanelStructureEdit.getSelectPartComboBox().setVisible(false);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set symbols and particles">
            // Since list starts with symbol set symbol image
            GuiUtils.setResourceImage(this.customPanelStructureEdit.getParticleImagePanel(), GuiDefinitions.PARTICLE_SYMBOL_FILENAME);

            FastListModel tmpParticlesModel = new FastListModel();
            tmpParticlesModel.setListenersEnabled(false);
            this.customPanelStructureEdit.getParticlesInfoList().setModel(tmpParticlesModel);
            int tmpCount = 0;
            // Symbols
            tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"), GuiMessage.get("DialogStructureEdit.Structure.BondingSymbol"),
                    GuiMessage.get("DialogStructureEdit.Structure.BondingText")));
            tmpCount++;
            tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                    GuiMessage.get("DialogStructureEdit.Structure.PartOpenSymbol"), GuiMessage.get("DialogStructureEdit.Structure.PartOpenText")));
            tmpCount++;
            tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                    GuiMessage.get("DialogStructureEdit.Structure.PartCloseSymbol"), GuiMessage.get("DialogStructureEdit.Structure.PartCloseText")));
            tmpCount++;
            tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                    GuiMessage.get("DialogStructureEdit.Structure.BranchOpenSymbol"), GuiMessage.get("DialogStructureEdit.Structure.BranchOpenText")));
            tmpCount++;
            tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                    GuiMessage.get("DialogStructureEdit.Structure.BranchCloseSymbol"), GuiMessage.get("DialogStructureEdit.Structure.BranchCloseText")));
            tmpCount++;
            tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                    GuiMessage.get("DialogStructureEdit.Structure.ConnectionOpenSymbol"), GuiMessage.get("DialogStructureEdit.Structure.ConnectionOpenText")));
            tmpCount++;
            tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                    GuiMessage.get("DialogStructureEdit.Structure.ConnectionCloseSymbol"), GuiMessage.get("DialogStructureEdit.Structure.ConnectionCloseText")));
            tmpCount++;
            if (!anIsMonomer) {
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                        GuiMessage.get("DialogStructureEdit.Structure.ParticleStartSymbol"), GuiMessage.get("DialogStructureEdit.Structure.ParticleStartText")));
                tmpCount++;
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                        GuiMessage.get("DialogStructureEdit.Structure.ParticleEndSymbol"), GuiMessage.get("DialogStructureEdit.Structure.ParticleEndText")));
                tmpCount++;
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                        GuiMessage.get("DialogStructureEdit.Structure.BackboneAttributeStartEndSymbol"), GuiMessage.get("DialogStructureEdit.Structure.BackboneAttributeStartEndText")));
                tmpCount++;
                tmpParticlesModel.addElement(" ");
                tmpCount++;
                for (int i = 1; i < 10; i++) {
                    tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                            String.format(GuiMessage.get("DialogStructureEdit.Structure.BackboneAttributeFormat"), String.valueOf(i).trim()),
                            GuiMessage.get("DialogStructureEdit.Structure.BackboneAttribute")));
                    tmpCount++;
                }
            }
            // Symbols for monomers
            if (anIsMonomer) {
                tmpParticlesModel.addElement(" ");
                tmpCount++;
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                        GuiMessage.get("DialogStructureEdit.Structure.MonomerStartSymbol"), GuiMessage.get("DialogStructureEdit.Structure.MonomerStartText")));
                tmpCount++;
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                        GuiMessage.get("DialogStructureEdit.Structure.MonomerEndSymbol"), GuiMessage.get("DialogStructureEdit.Structure.MonomerEndText")));
                tmpCount++;
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                        GuiMessage.get("DialogStructureEdit.Structure.MonomerHeadSymbol"), GuiMessage.get("DialogStructureEdit.Structure.MonomerHeadText")));
                tmpCount++;
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"),
                        GuiMessage.get("DialogStructureEdit.Structure.MonomerTailSymbol"), GuiMessage.get("DialogStructureEdit.Structure.MonomerTailText")));
                tmpCount++;
            }
            // Digits
            tmpParticlesModel.addElement(" ");
            tmpCount++;
            for (int i = 0; i < 10; i++) {
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"), String.valueOf(i).trim(),
                        GuiMessage.get("DialogStructureEdit.Structure.FrequencyDigit")));
                tmpCount++;
            }
            // Available monomers
            if (!anIsMonomer && this.availableMonomers != null && this.availableMonomers.size() > 0) {
                tmpParticlesModel.addElement(" ");
                tmpCount++;
                String[] tmpMonomers = this.availableMonomers.keySet().toArray(new String[0]);
                Arrays.sort(tmpMonomers);
                for (int i = 0; i < tmpMonomers.length; i++) {
                    tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"), tmpMonomers[i], this.availableMonomers.get(tmpMonomers[i])));
                    tmpCount++;
                }
            }
            // Particles
            tmpParticlesModel.addElement(" ");
            tmpCount++;
            String[] tmpParticles = this.availableParticles.keySet().toArray(new String[0]);
            Arrays.sort(tmpParticles);
            for (int i = 0; i < tmpParticles.length; i++) {
                tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"), tmpParticles[i], this.availableParticles.get(tmpParticles[i])));
                tmpCount++;
            }
            tmpParticlesModel.setListenersEnabled(true);
            tmpParticlesModel.fireIntervalAdded(tmpParticlesModel, 0, tmpCount);
            this.customPanelStructureEdit.getParticlesInfoList().setSelectedIndex(0);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set frames and reduced-particle-display check boxes checked">
            this.customPanelStructureEdit.getReducedCheckBox().setSelected(true);

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set peptides button">
            this.customPanelStructureEdit.getPeptidesButton().setVisible(StandardParticleInteractionData.getInstance().hasAminoAcidDefinitions());

            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            this.customPanelStructureEdit.getStructureTextArea().addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent aKeyEvent) {
                    if (CustomPanelStructureEditController.this.isMonomer) {
                        if (!CustomPanelStructureEditController.this.monomerAllowedCharactersPattern.matcher(String.valueOf(aKeyEvent.getKeyChar())).matches()) {
                            aKeyEvent.consume();
                        } else {
                            // Set undo stack
                            CustomPanelStructureEditController.this.undoStack.push(CustomPanelStructureEditController.this.customPanelStructureEdit.getStructureTextArea().getText());
                        }
                    } else if (!CustomPanelStructureEditController.this.structureAllowedCharactersPattern.matcher(String.valueOf(aKeyEvent.getKeyChar())).matches()) {
                        aKeyEvent.consume();
                    } else {
                        // Set undo stack
                        CustomPanelStructureEditController.this.undoStack.push(CustomPanelStructureEditController.this.customPanelStructureEdit.getStructureTextArea().getText());
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
                        CustomPanelStructureEditController.this.undoStack.push(CustomPanelStructureEditController.this.customPanelStructureEdit.getStructureTextArea().getText());
                    }
                }

            });
            this.customPanelStructureEdit.getStructureTextArea().getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    try {
                        CustomPanelStructureEditController.this.performSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "insertUpdate()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    try {
                        CustomPanelStructureEditController.this.performSettings();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "removeUpdate()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // NOTE: Plain text components don't fire these events, so do nothing
                }

            });
            this.customPanelStructureEdit.getGraphStreamViewPanel().addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent aMouseEvent) {
                    // Consume mouse pressed if not node or sprite (otherwise GraphStream panel selects an area)
                    GraphicElement tmpElement = 
                        CustomPanelStructureEditController.this.customPanelStructureEdit.getGraphStreamViewPanel().findNodeOrSpriteAt(
                            aMouseEvent.getX(), 
                            aMouseEvent.getY()
                        );
                    if (tmpElement == null) {
                        CustomPanelStructureEditController.this.customPanelStructureEdit.getGraphStreamViewPanel().endSelectionAt(aMouseEvent.getX(), aMouseEvent.getY());
                    }
                }

            });
            this.customPanelStructureEdit.getGraphicsSaveButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.saveGraphics();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getGraphicsCopyButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.copyGraphics();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getGraphicsResetButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.displayGraph();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getInfoTabbedPanel().addChangeListener(new ChangeListener() {

                public void stateChanged(final ChangeEvent e) {
                    try {
                        CustomPanelStructureEditController.this.setPreviousDefinitions();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "stateChanged()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getUsePreviousDefinitionButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.usePreviousDefinition();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getRemovePreviousDefinitionButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.removePreviousDefinition();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getClearPreviousDefinitionsButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.clearPreviousDefinitions();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getAppendParticleButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.appendToStructure();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getUndoButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.undoLastOperation();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getClearStructureButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.clearStructure();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getPeptidesButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureEditController.this.getPeptideSpices();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getReducedCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        CustomPanelStructureEditController.this.displayGraph();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureEditController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureEdit.getParticlesInfoList().addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        // Show particle image
                        CustomPanelStructureEditController.this.showParticleImage();
                    }
                    if (e.getClickCount() == 2) {
                        // Show particle image
                        CustomPanelStructureEditController.this.showParticleImage();
                        // Double click: Append symbol or particle to structure
                        CustomPanelStructureEditController.this.appendToStructure();
                    }
                }

            });
            this.customPanelStructureEdit.getParticlesInfoList().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    CustomPanelStructureEditController.this.showParticleImage();
                }

            });
            this.customPanelStructureEdit.getPreviousDefinitionsList().addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        // Double click: Use previous definition
                        CustomPanelStructureEditController.this.usePreviousDefinition();
                    }
                }

            });
            this.customPanelStructureEdit.getSelectPartComboBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        CustomPanelStructureEditController.this.setSelectedPartStructure();
                    } catch (Exception anExcpetion) {

                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                                "CustomPanelStructureEditController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set monomer/molecular structure">
            // NOTE: Set structure AFTER connection to document listener
            // Set undo stack
            this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
            this.customPanelStructureEdit.getStructureTextArea().setText(this.initialStructure);
            this.customPanelStructureEdit.getStructureTextArea().setCaretPosition(this.customPanelStructureEdit.getStructureTextArea().getText().length());
            this.performSettings();
            this.showParticleImage();
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelStructureEditController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

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
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Save graph/graphics to files
     */
    private void saveGraphics() {
        BufferedImage tmpImage = 
            new BufferedImage(
                this.customPanelStructureEdit.getGraphStreamViewPanel().getWidth(),
                this.customPanelStructureEdit.getGraphStreamViewPanel().getHeight(),
                BufferedImage.TYPE_INT_RGB
            );
        this.customPanelStructureEdit.getGraphStreamViewPanel().paint(tmpImage.getGraphics());
        String tmpFilePathname = 
            GuiUtils.selectSingleFileForSave(GuiMessage.get("Chooser.selectGraphicsFileSaveChooser"),
                GuiDefinitions.IMAGE_BMP_FILE_EXTENSION
            );
        if (tmpFilePathname != null) {
            try {
                GraphicsUtils.writeImageToFile(
                    tmpImage, 
                    ImageFileType.BMP, 
                    new File(tmpFilePathname)
                );
            } catch (Exception anExeption) {
                // <editor-fold defaultstate="collapsed" desc="Show error message">
                JOptionPane.showMessageDialog(null, 
                    GuiMessage.get("Error.SaveOperationFailed"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), 
                    JOptionPane.ERROR_MESSAGE
                );
                // </editor-fold>
            }
        }
    }

    /**
     * Copy graph/graphics
     */
    private void copyGraphics() {
        BufferedImage tmpImage = 
            new BufferedImage(
                this.customPanelStructureEdit.getGraphStreamViewPanel().getWidth(),
                this.customPanelStructureEdit.getGraphStreamViewPanel().getHeight(),
                BufferedImage.TYPE_INT_RGB
            );
        this.customPanelStructureEdit.getGraphStreamViewPanel().paint(tmpImage.getGraphics());
        if (!ImageSelection.copyImageToClipboard(tmpImage)) {
            // <editor-fold defaultstate="collapsed" desc="Show error message">
            JOptionPane.showMessageDialog(null, 
                GuiMessage.get("Error.CopyOperationFailed"), 
                GuiMessage.get("Error.ErrorNotificationTitle"),
                JOptionPane.ERROR_MESSAGE
            );
            // </editor-fold>
        }
    }
    
    /**
     * Displays graph
     */
    private void displayGraph() {
        if (this.isCorrectStructure) {
            if (this.isDisplayStructure) {
                try {
                    MouseCursorManagement.getInstance().setWaitCursor();
                    this.spicesToGraphStream.setFullParticleNameDisplay(!this.customPanelStructureEdit.getReducedCheckBox().isSelected());
                    if (this.spicesForDisplay == null) {
                        this.spicesForDisplay = new Spices(this.currentPartStructure.replaceAll("\\s+", ""));
                    } else {
                        this.spicesForDisplay.setInputStructure(this.currentPartStructure.replaceAll("\\s+", ""));
                    }
                    this.spicesToGraphStream.updateSpicesGraph(this.customPanelStructureEdit.getGraphStreamGraph(), this.spicesForDisplay);
                    this.setGuiElements(true, false);
                } catch (Exception anException) {
                    ModelUtils.appendToLogfile(true, anException);
                    MouseCursorManagement.getInstance().setDefaultCursor();
                    this.setGuiElements(false, false);
                } finally {
                    MouseCursorManagement.getInstance().setDefaultCursor();
                }
            } else {
                // Show structure-too-complex info label: Parameter true
                this.setGuiElements(false, true);
            }
        } else {
            this.setGuiElements(false, false);
        }
    }

    /**
     * Sets GUI elements
     *
     * @param anIsVisible True: Corresponding GUI elements are visible, false:
     * Otherwise
     * @param anIsStructureTooComplexVisible True: Structure-too-complex label
     * is visible, false: Otherwise
     */
    private void setGuiElements(boolean anIsVisible, boolean anIsStructureTooComplexVisible) {
        this.customPanelStructureEdit.getGraphicsSaveButton().setVisible(anIsVisible);
        this.customPanelStructureEdit.getGraphicsCopyButton().setVisible(anIsVisible);
        this.customPanelStructureEdit.getGraphicsResetButton().setVisible(anIsVisible);
        if (anIsVisible) {
            this.customPanelStructureEdit.getSelectPartComboBox().setVisible(this.customPanelStructureEdit.getSelectPartComboBox().getItemCount() > 1);
        } else {
            this.customPanelStructureEdit.getSelectPartComboBox().setVisible(anIsVisible);
        }
        this.customPanelStructureEdit.getReducedCheckBox().setVisible(anIsVisible);
        this.customPanelStructureEdit.getStructureTooComplexLabel().setVisible(anIsStructureTooComplexVisible);
        this.customPanelStructureEdit.getGraphStreamViewPanel().setVisible(!anIsStructureTooComplexVisible);
    }

    /**
     * Performs settings
     */
    private void performSettings() {
        String tmpStructure = this.customPanelStructureEdit.getStructureTextArea().getText().trim();
        SpicesGraphics tmpSpices = new SpicesGraphics(tmpStructure, this.availableParticles, this.isMonomer);
        String tmpErrorString = tmpSpices.getErrorMessage();
        if (tmpErrorString == null) {
            // <editor-fold defaultstate="collapsed" desc="Structure/monomer is valid">
            // NOTE: Correct structure/monomer: Apply-button is made visible in document listener
            if (!this.isMonomer) {
                this.lastValidSpices = tmpSpices;
            }
            SpicesInner[] tmpPartSpicesArray = tmpSpices.getPartsOfSpices();
            if (tmpPartSpicesArray == null) {
                this.partToStructureMap = null;
                this.currentPartStructure = tmpSpices.getInputStructure();
                this.customPanelStructureEdit.getSelectPartComboBox().setVisible(false);
            } else if (tmpPartSpicesArray.length == 1) {
                this.partToStructureMap = null;
                this.currentPartStructure = tmpPartSpicesArray[0].getInputStructure();
                this.customPanelStructureEdit.getSelectPartComboBox().setVisible(false);
            } else {
                this.partToStructureMap = new HashMap<String, String>(tmpPartSpicesArray.length);
                String[] tmpPartArray = new String[tmpPartSpicesArray.length];
                for (int i = 0; i < tmpPartSpicesArray.length; i++) {
                    String tmpPart = String.format(GuiMessage.get("Structure.PartFormat"), String.valueOf(i + 1));
                    tmpPartArray[i] = tmpPart;
                    this.partToStructureMap.put(tmpPart, tmpPartSpicesArray[i].getInputStructure());
                }
                this.customPanelStructureEdit.getSelectPartComboBox().setModel(new DefaultComboBoxModel(tmpPartArray));
                this.customPanelStructureEdit.getSelectPartComboBox().setSelectedItem(tmpPartArray[0]);
                this.currentPartStructure = this.partToStructureMap.get(tmpPartArray[0]);
                this.customPanelStructureEdit.getSelectPartComboBox().setVisible(true);
            }
            this.customPanelStructureEdit.getStructureInfoLabel().setForeground(GuiDefinitions.PANEL_INFO_CORRECT_COLOR);
            if (this.isMonomer) {
                this.customPanelStructureEdit.getStructureInfoLabel().setText(GuiMessage.get("DialogStructureEdit.Monomer.definitionInfoLabel.text"));
            } else {
                this.customPanelStructureEdit.getStructureInfoLabel().setText(GuiMessage.get("DialogStructureEdit.Structure.definitionInfoLabel.text"));
            }
            this.isCorrectStructure = true;
            int tmpNumberOfDisplayParticles = tmpSpices.getNumberOfDisplayParticles();
            this.isDisplayStructure = tmpNumberOfDisplayParticles <= Preferences.getInstance().getMaximumNumberOfParticlesForGraphicalDisplay();
            // Notify change receiver
            if (this.customPanelStructureEdit.getStructureTextArea().getText().trim().equals(this.initialStructure)) {
                this.changeInformation.setChangeType(ChangeTypeEnum.STRUCTURE_SYNTAX_NO_CHANGE);
                super.notifyChangeReceiver(this, this.changeInformation);
            } else {
                this.changeInformation.setChangeType(ChangeTypeEnum.STRUCTURE_SYNTAX_CORRECT_CHANGE);
                super.notifyChangeReceiver(this, this.changeInformation);
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Structure/monomer has failure">
            // Invalid structure/monomer: Make apply-button invisible
            this.customPanelStructureEdit.getStructureInfoLabel().setForeground(GuiDefinitions.PANEL_INFO_WRONG_COLOR);
            this.customPanelStructureEdit.getStructureInfoLabel().setText(String.format(GuiMessage.get("Structure.Error"), tmpErrorString));
            this.isCorrectStructure = false;
            this.isDisplayStructure = false;
            this.currentPartStructure = null;
            this.partToStructureMap = null;
            this.lastValidSpices = null;
            // Notify change receiver
            this.changeInformation.setChangeType(ChangeTypeEnum.STRUCTURE_SYNTAX_ERROR_CHANGE);
            super.notifyChangeReceiver(this, this.changeInformation);
            // </editor-fold>
        }
        this.displayGraph();
        this.setButtonVisibility();
    }

    /**
     * Appends selected symbol or particle to structure
     */
    private void appendToStructure() {
        String tmpSelectedItem = this.customPanelStructureEdit.getParticlesInfoList().getSelectedValue().toString();
        if (!tmpSelectedItem.trim().isEmpty()) {
            String tmpAppendItem = tmpSelectedItem.substring(0, tmpSelectedItem.indexOf(" ")).trim();
            if (this.customPanelStructureEdit.getStructureTextArea().getText() == null || this.customPanelStructureEdit.getStructureTextArea().getText().trim().isEmpty()) {
                this.customPanelStructureEdit.getStructureTextArea().setText(tmpAppendItem);
            } else {
                String tmpLastStructureCharacter = this.customPanelStructureEdit.getStructureTextArea().getText().trim().substring(this.customPanelStructureEdit.getStructureTextArea().getText().trim().length() - 1);
                String tmpBond = GuiMessage.get("DialogStructureEdit.Structure.BondingSymbol");
                if (this.isMonomer) {
                    // <editor-fold defaultstate="collapsed" desc="Monomer">
                    if (this.monomerOpeningCharactersPattern.matcher(tmpLastStructureCharacter).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpAppendItem);
                    } else if (particleEndingPattern.matcher(this.customPanelStructureEdit.getStructureTextArea().getText().trim()).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpBond + tmpAppendItem);
                    } else if (frequencyNumberEndingPattern.matcher(this.customPanelStructureEdit.getStructureTextArea().getText().trim()).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpAppendItem);
                    } else if (this.monomerNoPreviousBondingCharactersPattern.matcher(tmpAppendItem).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpAppendItem);
                    } else {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpBond + tmpAppendItem);
                    }

                    // </editor-fold>
                } else {
                    // <editor-fold defaultstate="collapsed" desc="Molecular structure">
                    if (this.structureOpeningCharactersPattern.matcher(tmpLastStructureCharacter).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpAppendItem);
                    } else if (particleEndingPattern.matcher(this.customPanelStructureEdit.getStructureTextArea().getText().trim()).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        if (this.structureOpeningCharactersPattern.matcher(tmpAppendItem).matches()
                                || this.backboneAttributePattern.matcher(tmpAppendItem).matches()
                                || tmpAppendItem.equals(GuiMessage.get("DialogStructureEdit.Structure.BackboneAttributeStartEndSymbol"))) {
                            this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpAppendItem);
                        } else {
                            this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpBond + tmpAppendItem);
                        }
                    } else if (frequencyNumberEndingPattern.matcher(this.customPanelStructureEdit.getStructureTextArea().getText().trim()).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpAppendItem);
                    } else if (this.structureNoPreviousBondingCharactersPattern.matcher(tmpAppendItem).matches()
                            || tmpAppendItem.equals(GuiMessage.get("DialogStructureEdit.Structure.ParticleStartSymbol"))
                            || tmpAppendItem.equals(GuiMessage.get("DialogStructureEdit.Structure.ParticleEndSymbol"))
                            || tmpAppendItem.equals(GuiMessage.get("DialogStructureEdit.Structure.BackboneAttributeStartEndSymbol"))
                            || this.backboneAttributePattern.matcher(tmpAppendItem).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpAppendItem);
                    } else if(backboneSymbolEndingPattern.matcher(this.customPanelStructureEdit.getStructureTextArea().getText().trim()).matches()) {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpAppendItem);
                    } else {
                        // Set undo stack
                        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
                        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getStructureTextArea().getText() + tmpBond + tmpAppendItem);
                    } 
                    // </editor-fold>
                }
            }
            this.customPanelStructureEdit.getStructureTextArea().setCaretPosition(this.customPanelStructureEdit.getStructureTextArea().getText().length());
            this.customPanelStructureEdit.getStructureTextArea().requestFocus();
        }
    }

    /**
     * Undo last operations
     */
    private void undoLastOperation() {
        if (this.undoStack.size() > 1) {
            this.customPanelStructureEdit.getStructureTextArea().setText(this.undoStack.pop());
            this.customPanelStructureEdit.getStructureTextArea().requestFocus();
        }
    }

    /**
     * Show particle image
     */
    private void showParticleImage() {
        String tmpSelectedItem = this.customPanelStructureEdit.getParticlesInfoList().getSelectedValue().toString();
        if (!tmpSelectedItem.trim().isEmpty()) {
            String tmpShowItem = tmpSelectedItem.substring(0, tmpSelectedItem.indexOf(" ")).trim();
            if (this.isParticle(tmpShowItem)) {
                String tmpImageResourceFilename = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + tmpShowItem + GuiDefinitions.RESOURCE_IMAGE_ENDING;
                if (!tmpImageResourceFilename.equals(this.oldImageResourceFilename)) {
                    if (GuiUtils.hasImageOfResource(tmpImageResourceFilename)) {
                        GuiUtils.setResourceImage(this.customPanelStructureEdit.getParticleImagePanel(), tmpImageResourceFilename);
                        this.oldImageResourceFilename = tmpImageResourceFilename;
                    } else {
                        GuiUtils.setResourceImage(this.customPanelStructureEdit.getParticleImagePanel(), GuiDefinitions.PARTICLE_PARTICLE_FILENAME);
                        this.oldImageResourceFilename = null;
                    }
                }
            } else if (tmpShowItem.startsWith("#")) {
                if (!GuiDefinitions.PARTICLE_MONOMER_FILENAME.equals(this.oldImageResourceFilename)) {
                    GuiUtils.setResourceImage(this.customPanelStructureEdit.getParticleImagePanel(), GuiDefinitions.PARTICLE_MONOMER_FILENAME);
                    this.oldImageResourceFilename = GuiDefinitions.PARTICLE_MONOMER_FILENAME;
                }
            } else if (!GuiDefinitions.PARTICLE_SYMBOL_FILENAME.equals(this.oldImageResourceFilename)) {
                GuiUtils.setResourceImage(this.customPanelStructureEdit.getParticleImagePanel(), GuiDefinitions.PARTICLE_SYMBOL_FILENAME);
                this.oldImageResourceFilename = GuiDefinitions.PARTICLE_SYMBOL_FILENAME;
            }
        } else if (!GuiDefinitions.PARTICLE_EMPTY_FILENAME.equals(this.oldImageResourceFilename)) {
            GuiUtils.setResourceImage(this.customPanelStructureEdit.getParticleImagePanel(), GuiDefinitions.PARTICLE_EMPTY_FILENAME);
            this.oldImageResourceFilename = GuiDefinitions.PARTICLE_EMPTY_FILENAME;
        }
    }

    /**
     * Clears structure
     */
    private void clearStructure() {
        // Set undo stack
        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
        this.customPanelStructureEdit.getStructureTextArea().setText("");
        this.customPanelStructureEdit.getStructureTextArea().requestFocus();
    }

    /**
     * Gets peptide Spices
     */
    private void getPeptideSpices() {
        String tmpPeptideSpices = DialogPeptideEdit.getPeptideSpices();
        if (tmpPeptideSpices != null && !tmpPeptideSpices.isEmpty()) {
            // Set undo stack
            this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
            this.customPanelStructureEdit.getStructureTextArea().setText(tmpPeptideSpices);
            this.customPanelStructureEdit.getStructureTextArea().requestFocus();
        }
    }

    /**
     * Checks if string may represent a valid particle
     *
     * @param aString String
     * @return True: String may represent a valid particle, false: Otherwise
     */
    private boolean isParticle(String aString) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.isEmpty()) {
            return false;
        }

        // </editor-fold>
        return this.particlePattern.matcher(aString).matches();
    }

    /**
     * Sets visibility of buttons
     */
    private void setButtonVisibility() {
        this.customPanelStructureEdit.getAppendParticleButton().setVisible(!this.customPanelStructureEdit.getParticlesInfoList().isSelectionEmpty());
        this.customPanelStructureEdit.getUndoButton().setVisible(this.undoStack.size() > 1);
        String tmpStructure = this.customPanelStructureEdit.getStructureTextArea().getText().trim();
        this.customPanelStructureEdit.getClearStructureButton().setVisible(tmpStructure != null && !tmpStructure.isEmpty());
    }

    /**
     * Sets selected part structure
     */
    private void setSelectedPartStructure() {
        String tmpSelectedPart = this.customPanelStructureEdit.getSelectPartComboBox().getSelectedItem().toString();
        this.currentPartStructure = this.partToStructureMap.get(tmpSelectedPart);
        this.displayGraph();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Previous definitions related methods">
    /**
     * Clears previous definitions
     */
    private void clearPreviousDefinitions() {
        if (GuiUtils.getYesNoDecision(GuiMessage.get("ClearPreviousDefinition.FrameTitle"), GuiMessage.get("ClearPreviousDefinition.Message"))) {
            if (this.isMonomer) {
                Preferences.getInstance().clearPreviousMonomers();
            } else {
                Preferences.getInstance().clearPreviousStructures();
            }
            // Select graphics tab
            this.customPanelStructureEdit.getInfoTabbedPanel().setSelectedIndex(0);
            // Disable previous definitions tab since previous definitions do not exist
            this.customPanelStructureEdit.getInfoTabbedPanel().setEnabledAt(2, false);
        }
    }

    /**
     * Removes selected previous definition
     */
    private void removePreviousDefinition() {
        if (GuiUtils.getYesNoDecision(GuiMessage.get("RemovePreviousDefinition.FrameTitle"), GuiMessage.get("RemovePreviousDefinition.Message"))) {
            int tmpSelectedIndex = this.customPanelStructureEdit.getPreviousDefinitionsList().getSelectedIndex();
            this.customPanelStructureEdit.getPreviousDefinitionsList().remove(tmpSelectedIndex);
            String tmpDefinitionToBeRemoved = this.customPanelStructureEdit.getPreviousDefinitionsList().getSelectedValue().toString();
            boolean tmpHasDefinitions = false;
            if (this.isMonomer) {
                Preferences.getInstance().removePreviousMonomer(tmpDefinitionToBeRemoved);
                tmpHasDefinitions = Preferences.getInstance().hasPreviousMonomers();
            } else {
                Preferences.getInstance().removePreviousStructure(tmpDefinitionToBeRemoved);
                tmpHasDefinitions = Preferences.getInstance().hasPreviousStructures();
            }
            if (tmpHasDefinitions) {
                if (tmpSelectedIndex == this.customPanelStructureEdit.getPreviousDefinitionsList().getModel().getSize()) {
                    this.customPanelStructureEdit.getPreviousDefinitionsList().setSelectedIndex(tmpSelectedIndex - 1);
                } else {
                    this.customPanelStructureEdit.getPreviousDefinitionsList().setSelectedIndex(tmpSelectedIndex);
                }
            } else {
                // Select graphics tab
                this.customPanelStructureEdit.getInfoTabbedPanel().setSelectedIndex(0);
                // Disable previous definitions tab since previous definitions do not exist
                this.customPanelStructureEdit.getInfoTabbedPanel().setEnabledAt(2, false);
            }
        }
    }

    /**
     * Sets previous definitions
     */
    private void setPreviousDefinitions() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.customPanelStructureEdit.getInfoTabbedPanel().getSelectedIndex() != 2) {
            return;
        }
        if (this.customPanelStructureEdit.getPreviousDefinitionsList().getModel().getSize() > 0) {
            return;
        }

        // </editor-fold>
        MouseCursorManagement.getInstance().setWaitCursor();
        FastListModel tmpPreviousDefinitionsModel = new FastListModel();
        this.customPanelStructureEdit.getPreviousDefinitionsList().setModel(tmpPreviousDefinitionsModel);
        tmpPreviousDefinitionsModel.setListenersEnabled(false);
        if (this.isMonomer) {
            for (String tmpPreviousMonomer : Preferences.getInstance().getPreviousMonomers()) {
                tmpPreviousDefinitionsModel.addElement(tmpPreviousMonomer);
            }
            tmpPreviousDefinitionsModel.setListenersEnabled(true);
            tmpPreviousDefinitionsModel.fireIntervalAdded(tmpPreviousDefinitionsModel, 0, Preferences.getInstance().getPreviousMonomers().size());
        } else {
            for (String tmpPreviousStructure : Preferences.getInstance().getPreviousStructures()) {
                tmpPreviousDefinitionsModel.addElement(tmpPreviousStructure);
            }
            tmpPreviousDefinitionsModel.setListenersEnabled(true);
            tmpPreviousDefinitionsModel.fireIntervalAdded(tmpPreviousDefinitionsModel, 0, Preferences.getInstance().getPreviousStructures().size());
        }
        this.customPanelStructureEdit.getPreviousDefinitionsList().setSelectedIndex(0);
        MouseCursorManagement.getInstance().setDefaultCursor();
    }

    /**
     * Uses previous definition
     */
    private void usePreviousDefinition() {
        // Set undo stack
        this.undoStack.push(this.customPanelStructureEdit.getStructureTextArea().getText());
        this.customPanelStructureEdit.getStructureTextArea().setText(this.customPanelStructureEdit.getPreviousDefinitionsList().getSelectedValue().toString());
        this.customPanelStructureEdit.getStructureTextArea().setCaretPosition(0);
        // Switch to graphics panel: Graphics will automatically be redrawn by text field event above
        this.customPanelStructureEdit.getInfoTabbedPanel().setSelectedIndex(0);
    }
    // </editor-fold>
    // </editor-fold>

}
