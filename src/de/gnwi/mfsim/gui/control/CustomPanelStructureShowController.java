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
import de.gnwi.mfsim.model.util.ImageSelection;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.mfsim.model.graphics.SpicesGraphics;
import de.gnwi.mfsim.model.particleStructure.SpicesPool;
import de.gnwi.spices.SpicesInner;
import de.gnwi.mfsim.model.graphics.ImageFileType;
import de.gnwi.mfsim.model.graphics.GraphicsUtils;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.GuiUtils;
import de.gnwi.spices.Spices;
import de.gnwi.spicestographstream.SpicesToGraphStream;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.*;
import org.graphstream.ui.graphicGraph.GraphicElement;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Controller class for CustomPanelStructureShow
 *
 * @author Achim Zielesny
 */
public class CustomPanelStructureShowController {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Panel this controller is made for
     */
    CustomPanelStructureShow customPanelStructureShow;

    /**
     * True: Monomer is to be edit, false: Structure is to be edit
     */
    boolean isMonomer;

    /**
     * Available particles
     */
    HashMap<String, String> availableParticles;

    /**
     * Available monomers
     */
    HashMap<String, String> availableMonomers;

    /**
     * True: Current structure can be displayed graphically, false: Otherwise
     */
    private boolean isDisplayStructure;

    /**
     * Initial structure (monomer or molecular structure)
     */
    private String initialStructure;

    /**
     * Old image resource filename
     */
    private String oldImageResourceFilename;

    /**
     * Hashmap for part to structure mapping
     */
    private HashMap<String, String> partToStructureMap;

    /**
     * Current part structure
     */
    private String currentPartStructure;

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
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomPanelStructureShow Panel this controller is made for
     * @param anIsMonomer True: Monomer is to shown, false: Structure is to be
     * shown
     * @param anAvailableParticles Available particles (is NOT allowed to be
     * null)
     * @param anAvailableMonomers Available monomers (may be null)
     * @param aStructure Structure (monomer or molecular structure, is NOT
     * allowed to be null)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public CustomPanelStructureShowController(
        CustomPanelStructureShow aCustomPanelStructureShow, 
        boolean anIsMonomer, 
        HashMap<String, String> anAvailableParticles,
        HashMap<String, String> anAvailableMonomers, 
        String aStructure
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomPanelStructureShow == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (anAvailableParticles == null) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        if (aStructure == null || aStructure.isEmpty()) {
            throw new IllegalArgumentException("An argument is illegal.");
        }

        // </editor-fold>
        try {
            // <editor-fold defaultstate="collapsed" desc="Initialize class variables">
            this.customPanelStructureShow = aCustomPanelStructureShow;
            this.availableParticles = anAvailableParticles;
            this.availableMonomers = anAvailableMonomers;
            this.initialStructure = aStructure;
            this.oldImageResourceFilename = null;
            this.spicesForDisplay = null;
            this.spicesToGraphStream = new SpicesToGraphStream();

            // <editor-fold defaultstate="collapsed" desc="- Set Spices">
            // NOTE: aStructure is already guaranteed to be a valid Spices
            SpicesGraphics tmpSpices = SpicesPool.getInstance().getSpices(aStructure);
            SpicesInner[] tmpPartSpicesArray = tmpSpices.getPartsOfSpices();
            if (tmpPartSpicesArray == null) {
                this.partToStructureMap = null;
                this.currentPartStructure = tmpSpices.getInputStructure();
                this.customPanelStructureShow.getSelectPartComboBox().setVisible(false);
            } else if (tmpPartSpicesArray.length == 1) {
                this.partToStructureMap = null;
                this.currentPartStructure = tmpPartSpicesArray[0].getInputStructure();
                this.customPanelStructureShow.getSelectPartComboBox().setVisible(false);
            } else {
                this.partToStructureMap = new HashMap<String, String>(tmpPartSpicesArray.length);
                String[] tmpPartArray = new String[tmpPartSpicesArray.length];
                for (int i = 0; i < tmpPartSpicesArray.length; i++) {
                    String tmpPart = String.format(GuiMessage.get("Structure.PartFormat"), String.valueOf(i + 1));
                    tmpPartArray[i] = tmpPart;
                    this.partToStructureMap.put(tmpPart, tmpPartSpicesArray[i].getInputStructure());
                }
                this.customPanelStructureShow.getSelectPartComboBox().setModel(new DefaultComboBoxModel(tmpPartArray));
                this.customPanelStructureShow.getSelectPartComboBox().setSelectedItem(tmpPartArray[0]);
                this.currentPartStructure = this.partToStructureMap.get(tmpPartArray[0]);
                this.customPanelStructureShow.getSelectPartComboBox().setVisible(true);
            }
            int tmpNumberOfDisplayParticles = tmpSpices.getNumberOfDisplayParticles();
            this.isDisplayStructure = tmpNumberOfDisplayParticles <= Preferences.getInstance().getMaximumNumberOfParticlesForGraphicalDisplay();
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Monomer and molecule settings">
            if (this.isMonomer) {
                // <editor-fold defaultstate="collapsed" desc="Monomer settings">
                this.customPanelStructureShow.getStructureTextArea().setToolTipText(GuiMessage.get("CustomPanelStructureShow.Monomer.inputTextField.toolTipText"));
                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Molecular structure settings">
                this.customPanelStructureShow.getStructureTextArea().setToolTipText(GuiMessage.get("CustomPanelStructureShow.Structure.inputTextField.toolTipText"));
                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Hide structure-too-complex info label">
            this.customPanelStructureShow.getStructureTooComplexLabel().setVisible(false);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set symbols and particles">
            // Since list starts with symbol set symbol image
            GuiUtils.setResourceImage(this.customPanelStructureShow.getParticleImagePanel(), GuiDefinitions.PARTICLE_SYMBOL_FILENAME);
            FastListModel tmpParticlesModel = new FastListModel();
            tmpParticlesModel.setListenersEnabled(false);
            this.customPanelStructureShow.getParticlesInfoList().setModel(tmpParticlesModel);
            int tmpCount = 0;
            // Symbols
            tmpParticlesModel.addElement(String.format(GuiMessage.get("DialogStructureEdit.Structure.particleInfoString"), GuiMessage.get("DialogStructureEdit.Structure.BondingSymbol"),
                    GuiMessage.get("DialogStructureEdit.Structure.BondingText")));
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
            this.customPanelStructureShow.getParticlesInfoList().setSelectedIndex(0);
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="- Set frames and reduced-particle-display check boxes checked">
            this.customPanelStructureShow.getReducedCheckBox().setSelected(true);
            // </editor-fold>
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Add listeners">
            this.customPanelStructureShow.getGraphStreamViewPanel().addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent aMouseEvent) {
                    // Consume mouse pressed if not node or sprite (otherwise GraphStream panel selects an area)
                    GraphicElement tmpElement = 
                        CustomPanelStructureShowController.this.customPanelStructureShow.getGraphStreamViewPanel().findNodeOrSpriteAt(
                            aMouseEvent.getX(), 
                            aMouseEvent.getY()
                        );
                    if (tmpElement == null) {
                        CustomPanelStructureShowController.this.customPanelStructureShow.getGraphStreamViewPanel().endSelectionAt(aMouseEvent.getX(), aMouseEvent.getY());
                    }
                }

            });
            this.customPanelStructureShow.getGraphicsSaveButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureShowController.this.saveGraphics();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureShow.getGraphicsCopyButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureShowController.this.copyGraphics();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureShow.getGraphicsResetButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelStructureShowController.this.displayGraph();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureShow.getReducedCheckBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    try {
                        CustomPanelStructureShowController.this.displayGraph();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);

                        // <editor-fold defaultstate="collapsed" desc="Message that method failed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()", "CustomPanelStructureShowController"),
                                GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.customPanelStructureShow.getParticlesInfoList().addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    // Show particle image
                    CustomPanelStructureShowController.this.showParticleImage();
                }

            });
            this.customPanelStructureShow.getParticlesInfoList().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    CustomPanelStructureShowController.this.showParticleImage();
                }

            });
            this.customPanelStructureShow.getSelectPartComboBox().addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    CustomPanelStructureShowController.this.setSelectedPartStructure();
                }

            });
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Set monomer/molecular structure">
            this.customPanelStructureShow.getStructureTextArea().setText(this.initialStructure);
            this.customPanelStructureShow.getStructureTextArea().setCaretPosition(this.customPanelStructureShow.getStructureTextArea().getText().length());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Display graph">
            this.displayGraph();
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor", "CustomPanelStructureShowController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Save graph/graphics to files
     */
    private void saveGraphics() {
        BufferedImage tmpImage = 
            new BufferedImage(
                this.customPanelStructureShow.getGraphStreamViewPanel().getWidth(),
                this.customPanelStructureShow.getGraphStreamViewPanel().getHeight(),
                BufferedImage.TYPE_INT_RGB
            );
        this.customPanelStructureShow.getGraphStreamViewPanel().paint(tmpImage.getGraphics());
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
                this.customPanelStructureShow.getGraphStreamViewPanel().getWidth(),
                this.customPanelStructureShow.getGraphStreamViewPanel().getHeight(),
                BufferedImage.TYPE_INT_RGB
            );
        this.customPanelStructureShow.getGraphStreamViewPanel().paint(tmpImage.getGraphics());
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
        if (this.isDisplayStructure) {
            try {
                MouseCursorManagement.getInstance().setWaitCursor();
                this.spicesToGraphStream.setFullParticleNameDisplay(!this.customPanelStructureShow.getReducedCheckBox().isSelected());
                // Hide structure-too-complex info label
                this.customPanelStructureShow.getStructureTooComplexLabel().setVisible(false);
                this.customPanelStructureShow.getGraphStreamViewPanel().setVisible(true);
                if (this.spicesForDisplay == null) {
                    this.spicesForDisplay = new Spices(this.currentPartStructure.replaceAll("\\s+", ""));
                } else {
                    this.spicesForDisplay.setInputStructure(this.currentPartStructure.replaceAll("\\s+", ""));
                }
                this.spicesToGraphStream.updateSpicesGraph(this.customPanelStructureShow.getGraphStreamGraph(), this.spicesForDisplay);
                this.customPanelStructureShow.getGraphicsSaveButton().setVisible(true);
                this.customPanelStructureShow.getGraphicsCopyButton().setVisible(true);
                this.customPanelStructureShow.getGraphicsResetButton().setVisible(true);
                this.customPanelStructureShow.getReducedCheckBox().setVisible(true);
            } catch (Exception anException) {
                ModelUtils.appendToLogfile(true, anException);
                MouseCursorManagement.getInstance().setDefaultCursor();
                this.customPanelStructureShow.getGraphicsSaveButton().setVisible(false);
                this.customPanelStructureShow.getGraphicsCopyButton().setVisible(false);
                this.customPanelStructureShow.getGraphicsResetButton().setVisible(false);
                this.customPanelStructureShow.getReducedCheckBox().setVisible(false);
            } finally {
                MouseCursorManagement.getInstance().setDefaultCursor();
            }
        } else {
            // Show structure-too-complex info label
            this.customPanelStructureShow.getGraphStreamViewPanel().setVisible(false);
            this.customPanelStructureShow.getGraphicsCopyButton().setVisible(false);
            this.customPanelStructureShow.getGraphicsSaveButton().setVisible(false);
            this.customPanelStructureShow.getGraphicsResetButton().setVisible(false);
            this.customPanelStructureShow.getReducedCheckBox().setVisible(false);
            this.customPanelStructureShow.getStructureTooComplexLabel().setVisible(true);
        }
    }

    /**
     * Show particle image
     */
    private void showParticleImage() {
        String tmpSelectedItem = this.customPanelStructureShow.getParticlesInfoList().getSelectedValue().toString();
        if (!tmpSelectedItem.trim().isEmpty()) {
            String tmpShowItem = tmpSelectedItem.substring(0, tmpSelectedItem.indexOf(" ")).trim();
            if (this.isParticle(tmpShowItem)) {
                String tmpImageResourceFilename = GuiDefinitions.PARTICLE_IMAGE_FILENAME_PREFIX + tmpShowItem + GuiDefinitions.RESOURCE_IMAGE_ENDING;
                if (!tmpImageResourceFilename.equals(this.oldImageResourceFilename)) {
                    if (GuiUtils.hasImageOfResource(tmpImageResourceFilename)) {
                        GuiUtils.setResourceImage(this.customPanelStructureShow.getParticleImagePanel(), tmpImageResourceFilename);
                        this.oldImageResourceFilename = tmpImageResourceFilename;
                    } else {
                        GuiUtils.setResourceImage(this.customPanelStructureShow.getParticleImagePanel(), GuiDefinitions.PARTICLE_PARTICLE_FILENAME);
                        this.oldImageResourceFilename = null;
                    }
                }
            } else if (tmpShowItem.startsWith("#")) {
                if (!GuiDefinitions.PARTICLE_MONOMER_FILENAME.equals(this.oldImageResourceFilename)) {
                    GuiUtils.setResourceImage(this.customPanelStructureShow.getParticleImagePanel(), GuiDefinitions.PARTICLE_MONOMER_FILENAME);
                    this.oldImageResourceFilename = GuiDefinitions.PARTICLE_MONOMER_FILENAME;
                }
            } else if (!GuiDefinitions.PARTICLE_SYMBOL_FILENAME.equals(this.oldImageResourceFilename)) {
                GuiUtils.setResourceImage(this.customPanelStructureShow.getParticleImagePanel(), GuiDefinitions.PARTICLE_SYMBOL_FILENAME);
                this.oldImageResourceFilename = GuiDefinitions.PARTICLE_SYMBOL_FILENAME;
            }
        } else if (!GuiDefinitions.PARTICLE_EMPTY_FILENAME.equals(this.oldImageResourceFilename)) {
            GuiUtils.setResourceImage(this.customPanelStructureShow.getParticleImagePanel(), GuiDefinitions.PARTICLE_EMPTY_FILENAME);
            this.oldImageResourceFilename = GuiDefinitions.PARTICLE_EMPTY_FILENAME;
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
     * Sets selected part structure
     */
    private void setSelectedPartStructure() {
        String tmpSelectedPart = this.customPanelStructureShow.getSelectPartComboBox().getSelectedItem().toString();
        this.currentPartStructure = this.partToStructureMap.get(tmpSelectedPart);
        this.displayGraph();
    }
    // </editor-fold>

}
