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
package de.gnwi.mfsim.gui.util.valueItem;

import de.gnwi.mfsim.gui.dialog.DialogStructureShow;
import de.gnwi.mfsim.gui.dialog.DialogStructureEdit;
import de.gnwi.mfsim.gui.dialog.DialogProteinShow;
import de.gnwi.mfsim.gui.dialog.DialogMonomerEdit;
import de.gnwi.mfsim.gui.util.ComboBoxColorCellRenderer;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.text.PlainDocument;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Cell editor for editing matrix value items
 *
 * @author Achim Zielesny
 */
public class ValueItemCellEditorEdit extends AbstractCellEditor implements TableCellEditor, FocusListener, MouseListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Editor start label
     */
    private JLabel editorStartLabel;
    /**
     * Combo box
     */
    private JComboBox comboBox;
    /**
     * Current table
     */
    private JTable table;
    /**
     * Text field
     */
    private JTextField textField;
    /**
     * Current value item
     */
    private ValueItem valueItem;
    /**
     * Key listener for item of matrix value item
     */
    private ValueItemKeyListener valueItemKeyListener;
    /**
     * Value item document listener for value item related text fields
     */
    private ValueItemDocumentListener valueItemDocumentListener;
    /**
     * Value item input verifier for value item related text fields
     */
    private ValueItemInputVerifier valueItemInputVerifier;
    /**
     * Value item item listener
     */
    private ValueItemSelectionItemListener valueItemSelectionItemListener;
    /**
     * Current value item type;
     */
    private ValueItemEnumDataType valueItemType;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000010L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public ValueItemCellEditorEdit() {
        this.textField = new JTextField();
        this.comboBox = new JComboBox();
        this.comboBox.setRenderer(new ComboBoxColorCellRenderer());
        // <editor-fold defaultstate="collapsed" desc="this.editorStartLabel">
        this.editorStartLabel = new JLabel();
        this.editorStartLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.editorStartLabel.addFocusListener(this);
        this.editorStartLabel.addMouseListener(this);
        // NOTE:
        // Do NOT set this.editorStartLabel.setText(...) since this is performed 
        // in method getTableCellEditorComponent() in dependence of edit/view of 
        // monomer/molecular structure
        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns value of cell editor
     *
     * @return Value of cell editor
     */
    @Override
    public Object getCellEditorValue() {
        switch (this.valueItemType) {
            case SELECTION_TEXT:
                // <editor-fold defaultstate="collapsed" desc="Selection text (this.comboBox.getSelectedItem())">
                return this.comboBox.getSelectedItem();
                // </editor-fold>
            case MOLECULAR_STRUCTURE:
            case MONOMER_STRUCTURE:
                // <editor-fold defaultstate="collapsed" desc="Molecular structure">
                return this.valueItem.getValue();
                // </editor-fold>
            default:
                // <editor-fold defaultstate="collapsed" desc="Default (this.textField.getText())">
                return this.textField.getText();
                // </editor-fold>
        }
    }

    /**
     * Returns cell editor component
     *
     * @param aTable Table
     * @param aValue Value
     * @param anIsSelected Flag
     * @param aRowIndex Row index
     * @param aColumnIndex Column index
     * @return Cell editor component or null if not possible
     */
    @Override
    public Component getTableCellEditorComponent(JTable aTable, Object aValue, boolean anIsSelected, int aRowIndex, int aColumnIndex) {
        // <editor-fold defaultstate="collapsed" desc="Get value item and column type format of matrix">
        this.valueItem = null;
        ValueItemDataTypeFormat tmpTypeFormat = null;
        try {
            this.valueItem = ((ValueItemTableModelMatrixEdit) aTable.getModel()).getValueItem();
            tmpTypeFormat = this.valueItem.getTypeFormat(aRowIndex, aColumnIndex);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
        if (tmpTypeFormat == null) {
            return null;
        } else {
            this.valueItemType = tmpTypeFormat.getDataType();
        }
        // </editor-fold>
        switch (this.valueItemType) {
            case SELECTION_TEXT:
                // <editor-fold defaultstate="collapsed" desc="Set this.valueItemSelectionItemListener">
                if (this.valueItemSelectionItemListener == null) {
                    this.valueItemSelectionItemListener = new ValueItemSelectionItemListener(this.comboBox, this.valueItem);
                    this.comboBox.addItemListener(this.valueItemSelectionItemListener);
                } else {
                    this.valueItemSelectionItemListener.setComboBoxAndValueItem(this.comboBox, this.valueItem);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set this.comboBox">
                this.comboBox.setModel(new DefaultComboBoxModel(tmpTypeFormat.getSelectionTexts()));
                this.comboBox.setSelectedItem(aValue.toString());
                // </editor-fold>
                return this.comboBox;
            case MOLECULAR_STRUCTURE:
            case MONOMER_STRUCTURE:
                // NOTE: Value item has already aValue
                this.table = aTable;
                if (tmpTypeFormat.isEditable()) {
                    this.editorStartLabel.setText(GuiMessage.get("Information.EditStructure"));
                } else {
                    this.editorStartLabel.setText(GuiMessage.get("Information.ShowStructure"));
                }
                return this.editorStartLabel;
            default:
                // <editor-fold defaultstate="collapsed" desc="Set this.valueItemKeyListener">
                if (this.valueItemKeyListener == null) {
                    this.valueItemKeyListener = new ValueItemKeyListener(this.textField, tmpTypeFormat);
                    this.textField.addKeyListener(this.valueItemKeyListener);
                } else {
                    this.valueItemKeyListener.setTextFieldAndTypeFormat(this.textField, tmpTypeFormat);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set this.valueItemDocumentListener">
                if (this.valueItemDocumentListener == null) {
                    PlainDocument tmpDocument = (PlainDocument) this.textField.getDocument();
                    this.valueItemDocumentListener = new ValueItemDocumentListener(this.valueItem, this.textField);
                    tmpDocument.addDocumentListener(this.valueItemDocumentListener);
                } else {
                    this.valueItemDocumentListener.setValueItemAndTextField(this.valueItem, this.textField);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set this.valueItemInputVerifier">
                if (this.valueItemInputVerifier == null) {
                    this.valueItemInputVerifier = new ValueItemInputVerifier(this.valueItem);
                    this.textField.setInputVerifier(this.valueItemInputVerifier);
                } else {
                    this.valueItemInputVerifier.setValueItem(this.valueItem);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set this.textField">
                this.textField.setForeground(GuiDefinitions.TEXT_FIELD_FOREGROUND_CORRECT_COLOR);
                this.textField.setText(aValue.toString());
                // Select complete text of text field
                this.textField.selectAll();

                // </editor-fold>
                return this.textField;
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public events for this.editorStartLabel">
    // <editor-fold defaultstate="collapsed" desc="FocusListener events for this.editorStartLabel">
    /**
     * Standard focus listener method
     *
     * @param aFocusEvent Event
     * @throws IllegalArgumentException Thrown if value item type is illegal
     */
    public void focusGained(FocusEvent aFocusEvent) throws IllegalArgumentException {
        this.startEditor();
    }

    /**
     * Standard focus listener method
     *
     * @param aFocusEvent Event
     */
    public void focusLost(FocusEvent aFocusEvent) {
        // Do nothing
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="MouseListener events for this.editorStartLabel">
    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mouseClicked(MouseEvent aMouseEvent) {
        this.startEditor();
    }

    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mouseReleased(MouseEvent aMouseEvent) {
        // Do nothing
    }

    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mouseEntered(MouseEvent aMouseEvent) {
        // Do nothing
    }

    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mousePressed(MouseEvent aMouseEvent) {
        // Do nothing
    }

    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mouseExited(MouseEvent aMouseEvent) {
        // Do nothing
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Starts editor
     */
    private void startEditor() {
        if (this.valueItem.getTypeFormat().isEditable()) {
            // <editor-fold defaultstate="collapsed" desc="Editor">
            // Set editorStartLabel text: Edit in progress
            this.editorStartLabel.setText(GuiMessage.get("Information.EditStructureInProgress"));

            switch (this.valueItemType) {
                case MOLECULAR_STRUCTURE:
                    // Show molecular structure edit dialog (Parameter false)
                    DialogStructureEdit.edit(this.valueItem, StandardParticleInteractionData.getInstance().getAvailableParticles(), this.getAvailableMonomers());
                    break;
                case MONOMER_STRUCTURE:
                    // Show monomer edit dialog
                    DialogMonomerEdit.edit(this.valueItem, StandardParticleInteractionData.getInstance().getAvailableParticles());
                    break;
                default:
                    throw new IllegalArgumentException("Illegal value item type: " + this.valueItemType.name());
            }
            // Set editorStartLabel text: Editor
            this.editorStartLabel.setText(GuiMessage.get("Information.EditStructure"));

            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Viewer">
            // Set editorStartLabel text: Edit in progress
            this.editorStartLabel.setText(GuiMessage.get("Information.ShowStructureInProgress"));

            switch (this.valueItemType) {
                case MOLECULAR_STRUCTURE:
                    // Show protein/molecular structure show dialog (Parameter false)
                    if (this.valueItem.getValueItemMatrixElement().hasProteinData()) {
                        DialogProteinShow.show(this.valueItem.getValueItemMatrixElement().getProteinData());
                    } else {
                        DialogStructureShow.show(this.valueItem, false, StandardParticleInteractionData.getInstance().getAvailableParticles(), this.getAvailableMonomers());
                    }
                    break;
                case MONOMER_STRUCTURE:
                    // Show monomer show dialog (Parameter true)
                    DialogStructureShow.show(this.valueItem, true, StandardParticleInteractionData.getInstance().getAvailableParticles(), null);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal value item type: " + this.valueItemType.name());
            }
            // Set editorStartLabel text: Viewer
            this.editorStartLabel.setText(GuiMessage.get("Information.ShowStructure"));

            // </editor-fold>
        }
        // IMPORTANT: Request focus on table again
        this.table.requestFocusInWindow();
        // IMPORTANT: Stop renderer
        fireEditingStopped();
    }

    /**
     * Returns available monomers
     *
     * @return Available monomers or null if none are available
     */
    private HashMap<String, String> getAvailableMonomers() {
        HashMap<String, String> tmpAvailableMonomers = null;
        ValueItem tmpMonomerTableValueItem = this.valueItem.getValueItemContainer().getValueItem("MonomerTable");
        if (tmpMonomerTableValueItem != null && tmpMonomerTableValueItem.isActive()) {
            tmpAvailableMonomers = new HashMap<String, String>(tmpMonomerTableValueItem.getMatrixRowCount());
            for (int k = 0; k < tmpMonomerTableValueItem.getMatrixRowCount(); k++) {
                // Monomers in molecular structures start with a # character
                tmpAvailableMonomers.put("#" + tmpMonomerTableValueItem.getValue(k, 0), tmpMonomerTableValueItem.getValue(k, 1));
            }
        }
        return tmpAvailableMonomers;
    }
    // </editor-fold>
}
