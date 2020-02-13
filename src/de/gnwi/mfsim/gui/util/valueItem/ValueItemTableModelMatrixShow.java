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
package de.gnwi.mfsim.gui.util.valueItem;

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import javax.swing.table.AbstractTableModel;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * This class implements a table model for show of a matrix of a value item
 *
 * @author Achim Zielesny
 */
public class ValueItemTableModelMatrixShow extends AbstractTableModel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
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
    static final long serialVersionUID = 1000000000000000026L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aValueItem Value item
     * @throws IllegalArgumentException Thrown if argument is illegal
     */
    public ValueItemTableModelMatrixShow(ValueItem aValueItem) throws IllegalArgumentException {
        try {
            this.setValueItem(aValueItem);
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
        boolean tmpIsCellEditable = tmpDataType == ValueItemEnumDataType.MOLECULAR_STRUCTURE || tmpDataType == ValueItemEnumDataType.MONOMER_STRUCTURE;
        if (tmpIsCellEditable) {
            // NOTE: This setting is necessary for correct value setting in subsequent methods
            this.valueItem.setCurrentMatrixPosition(aRow, aColumn);
        }
        return tmpIsCellEditable;
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
            return this.valueItem.getValue(aRow, aColumn);
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
    }
    // </editor-fold>

}
