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

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * This class implements a table model for edit of a matrix of a value item
 *
 * @author Achim Zielesny
 */
public class ValueItemTableModelMatrixEdit extends AbstractTableModel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Label for information display
     */
    private JLabel infoLabel;

    /**
     * Value item
     */
    private ValueItem valueItem;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000009L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aValueItem Value item
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemTableModelMatrixEdit(ValueItem aValueItem) throws IllegalArgumentException {
        try {
            this.setValueItem(aValueItem);
            this.setInfoLabel(null);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new IllegalArgumentException("Argument is illegal.", anException);
        }
    }

    /**
     * Constructor
     *
     * @param aValueItem Value item
     * @param anInfoLabel Information label
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemTableModelMatrixEdit(ValueItem aValueItem, JLabel anInfoLabel) throws IllegalArgumentException {
        try {
            this.setValueItem(aValueItem);
            this.setInfoLabel(anInfoLabel);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            throw new IllegalArgumentException("Argument is illegal.", anException);
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Value item
     *
     * @return Value item
     */
    public ValueItem getValueItem() {
        return this.valueItem;
    }

    /**
     * Value item
     *
     * @param aValueItem Value item (not allowed to be null)
     * @throws IllegalArgumentException Thrown if aValueItem is null or illegal
     */
    public void setValueItem(ValueItem aValueItem) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            throw new IllegalArgumentException("Value item is null.");
        }
        if (aValueItem.getBasicType() == ValueItemEnumBasicType.SCALAR) {
            throw new IllegalArgumentException("Type of value item is invalid.");
        }
        // </editor-fold>
        this.valueItem = aValueItem;
        // Indicate that change has happened
        this.fireTableStructureChanged();
    }

    /**
     * Information label
     *
     * @param anInfoLabel Information label (may be null)
     */
    public void setInfoLabel(JLabel anInfoLabel) {
        this.infoLabel = anInfoLabel;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Column name
     *
     * @param aColumn Column
     * @return Column name
     */
    @Override
    public Class<?> getColumnClass(int aColumn) {
        return this.getValueAt(0, aColumn).getClass();
    }

    /**
     * Column name
     *
     * @param aColumn Column
     * @return Column name
     */
    @Override
    public String getColumnName(int aColumn) {
        return this.valueItem.getMatrixColumnName(aColumn);
    }

    /**
     * Column count
     *
     * @return Column count
     */
    @Override
    public int getColumnCount() {
        return this.valueItem.getMatrixColumnCount();
    }

    /**
     * Returns first editable column or -1 if there is no editable column
     *
     * @return First editable column or -1 if there is no editable column
     */
    public int getFirstEditableColumn() {
        return this.valueItem.getFirstEditableColumn();
    }

    /**
     * Row count
     *
     * @return Row count
     */
    @Override
    public int getRowCount() {
        return this.valueItem.getMatrixRowCount();
    }

    /**
     * Cell editable (always returns true)
     *
     * @param aRow Row
     * @param aColumn Column
     * @return True: Cell is editable, false: Otherwise
     */
    @Override
    public boolean isCellEditable(int aRow, int aColumn) {
        ValueItemEnumDataType tmpDataType = this.valueItem.getTypeFormat(aRow, aColumn).getDataType();
        boolean tmpIsCellViewable = tmpDataType == ValueItemEnumDataType.MOLECULAR_STRUCTURE || tmpDataType == ValueItemEnumDataType.MONOMER_STRUCTURE;
        if (!this.valueItem.getTypeFormat(aRow, aColumn).isEditable() && tmpIsCellViewable) {

            // <editor-fold defaultstate="collapsed" desc="Viewable cell with monomer/molecular structure">
            // NOTE: This setting is necessary for correct value setting in subsequent methods
            this.valueItem.setCurrentMatrixPosition(aRow, aColumn);
            return true;

            // </editor-fold>
        } else if (this.valueItem.getTypeFormat(aRow, aColumn).isEditable()) {

            // <editor-fold defaultstate="collapsed" desc="Editable cell">
            // NOTE: This setting is necessary for correct value setting in method ValueItemDocumentListener.performSettings() or ValueItemInputVerifier.shouldYieldFocus()
            this.valueItem.setCurrentMatrixPosition(aRow, aColumn);
            // Set info label
            switch (this.valueItem.getTypeFormat(aRow, aColumn).getDataType()) {
                case NUMERIC:
                    this.displayOnInfoLabel(String.format(GuiMessage.get("Information.MinDefaultMax"), this.valueItem
                            .getTypeFormat(aRow, aColumn).getMinimumValueRepresentation(), this.valueItem.getTypeFormat(aRow, aColumn).getDefaultValue(),
                            this.valueItem.getTypeFormat(aRow, aColumn).getMaximumValueRepresentation()));
                    break;
                case NUMERIC_NULL:
                    this.displayOnInfoLabel(String.format(GuiMessage.get("Information.MinDefaultMaxOrNull"), this.valueItem.getTypeFormat(aRow,
                            aColumn).getMinimumValueRepresentation(), this.valueItem.getTypeFormat(aRow, aColumn).getDefaultValue(), this.valueItem.getTypeFormat(
                                    aRow, aColumn).getMaximumValueRepresentation(), ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString")));
                    break;
                case SELECTION_TEXT:
                    this.displayOnInfoLabel(String.format(GuiMessage.get("Information.SelectItem"), this.valueItem.getTypeFormat(aRow, aColumn)
                            .getDefaultValue()));
                    break;
                default:
                    this.displayOnInfoLabel(GuiMessage.get("Information.EditCell"));
                    break;
            }
            return true;

            // </editor-fold>
        } else {

            // <editor-fold defaultstate="collapsed" desc="Non-editable cell">
            return false;

            // </editor-fold>
        }

    }

    /**
     * Insert row at specified position
     *
     * @param aRow Row position
     */
    public void insertRow(int aRow) {
        if (this.valueItem.insertMatrixRow(aRow)) {
            // Table data changed
            this.fireTableDataChanged();
            try {
                // Set mouse wait cursor since value item update cascade may take its time
                MouseCursorManagement.getInstance().setWaitCursor();
                this.valueItem.notifyDependentValueItemsForUpdate();
            } finally {
                MouseCursorManagement.getInstance().setDefaultCursor();
            }
        }
    }

    /**
     * Copies row at specified position to end
     *
     * @param aRowToBeCopied Row position of row to be copied
     */
    public void copyRow(int aRowToBeCopied) {
        if (this.valueItem.copyMatrixRow(aRowToBeCopied)) {
            // Table data changed
            this.fireTableDataChanged();
            try {
                // Set mouse wait cursor since value item update cascade may take its time
                MouseCursorManagement.getInstance().setWaitCursor();
                this.valueItem.notifyDependentValueItemsForUpdate();
            } finally {
                MouseCursorManagement.getInstance().setDefaultCursor();
            }
        }
    }

    /**
     * Value at matrix position
     *
     * @param aRow Row
     * @param aColumn Column
     * @return Value at matrix position
     */
    @Override
    public Object getValueAt(int aRow, int aColumn) {
        ValueItemEnumDataType tmpDataType = this.valueItem.getTypeFormat(aRow, aColumn).getDataType();
        if (tmpDataType == ValueItemEnumDataType.MOLECULAR_STRUCTURE || tmpDataType == ValueItemEnumDataType.MONOMER_STRUCTURE) {
            // <editor-fold defaultstate="collapsed" desc="Monomer/molecular structure">
            if (this.valueItem.getTypeFormat(aRow, aColumn).isEditable()) {
                // <editor-fold defaultstate="collapsed" desc="Editable">
                if (this.valueItem.getValue(aRow, aColumn).length() > ModelDefinitions.STRUCTURE_LENGTH_FOR_DISPLAY) {
                    String tmpInitialStructure = this.valueItem.getValue(aRow, aColumn).substring(0,
                            ModelDefinitions.STRUCTURE_LENGTH_FOR_DISPLAY - GuiMessage.get("StructureTooComplexEnding").length());
                    return String.format(GuiMessage.get("StructureTooComplexFormat"), tmpInitialStructure, GuiMessage.get("StructureTooComplexEnding"));
                    // return GuiMessage.getString("StructureTooComplexEnding");
                } else {
                    return this.valueItem.getValue(aRow, aColumn);
                }

                // </editor-fold>
            } else {
                // <editor-fold defaultstate="collapsed" desc="Non-editable (view only)">
                if (this.valueItem.getValue(aRow, aColumn).length() > ModelDefinitions.STRUCTURE_LENGTH_FOR_DISPLAY) {
                    String tmpInitialStructure = this.valueItem.getValue(aRow, aColumn).substring(0,
                            ModelDefinitions.STRUCTURE_LENGTH_FOR_DISPLAY - GuiMessage.get("StructureTooComplexEnding").length());
                    return String.format(GuiMessage.get("StructureTooComplexFormat"), tmpInitialStructure, GuiMessage.get("StructureTooComplexEnding"));
                    // return GuiMessage.getString("StructureTooComplexEnding");
                } else {
                    return this.valueItem.getValue(aRow, aColumn);
                }

                // </editor-fold>
            }

            // </editor-fold>
        } else {
            return this.valueItem.getValue(aRow, aColumn);
        }
    }

    /**
     * Remove specified row
     *
     * @param aRow Row position
     */
    public void removeRow(int aRow) {
        if (this.valueItem.removeMatrixRow(aRow)) {
            // Table data changed
            this.fireTableDataChanged();
            try {
                // Set mouse wait cursor since value item update cascade may take its time
                MouseCursorManagement.getInstance().setWaitCursor();
                this.valueItem.notifyDependentValueItemsForUpdate();
            } finally {
                MouseCursorManagement.getInstance().setDefaultCursor();
            }
        }
    }

    /**
     * Value at matrix position
     *
     * @param aValue Value at matrix position
     * @param aRow Row
     * @param aColumn Column
     */
    @Override
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        // NOTE: Value of value item was already set, e.g. in ValueItemDocumentListener.performSettings() or ValueItemInputVerifier.shouldYieldFocus() or a dialog like DialogStructureEdit!
        // Set info label
        this.displayOnInfoLabel(GuiMessage.get("Information.MatrixCellEditable"));
        // Indicate that change has happened
        this.fireTableCellUpdated(aRow, aColumn);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Displays text on info label if available
     *
     * @param aDisplayText Text to display
     */
    private void displayOnInfoLabel(String aDisplayText) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.infoLabel == null || aDisplayText == null || aDisplayText.isEmpty()) {
            return;
        }

        // </editor-fold>
        this.infoLabel.setText(aDisplayText);
    }
    // </editor-fold>

}
