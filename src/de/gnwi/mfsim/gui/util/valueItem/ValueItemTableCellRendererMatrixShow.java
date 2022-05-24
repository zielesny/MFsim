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
package de.gnwi.mfsim.gui.util.valueItem;

import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Table cell renderer for value item show matrix
 *
 * @author Achim Zielesny
 */
public class ValueItemTableCellRendererMatrixShow extends DefaultTableCellRenderer {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Value item instance
     */
    private ValueItem valueItem;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000028L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aValueItem Value item (not allowed to be null)
     * @throws IllegalArgumentException Thrown if aValueItem or anInfoLabel is null
     */
    public ValueItemTableCellRendererMatrixShow(ValueItem aValueItem) throws IllegalArgumentException {
        this.setValueItem(aValueItem);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Value item
     *
     * @param aValueItem Value item (not allowed to be null)
     * @throws IllegalArgumentException Thrown if aValueItem is null
     */
    public void setValueItem(ValueItem aValueItem) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aValueItem == null) {
            throw new IllegalArgumentException("aValueItem is null.");
        }

        // </editor-fold>
        this.valueItem = aValueItem;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Standard method
     *
     * @param aTable Table
     * @param aColor Color
     * @param anIsSelected Selection flag
     * @param aHasFocus Focus flag
     * @param aRow Row
     * @param aColumn Column
     * @return Renderer component
     */
    @Override
    public Component getTableCellRendererComponent(JTable aTable, Object aColor, boolean anIsSelected, boolean aHasFocus, int aRow, int aColumn) {
        // <editor-fold defaultstate="collapsed" desc="Set cell colors">
        // Matrix in show table is NOT editable
        ValueItemEnumDataType tmpDataType = this.valueItem.getTypeFormat(aRow, aColumn).getDataType();
        if (tmpDataType == ValueItemEnumDataType.MOLECULAR_STRUCTURE || tmpDataType == ValueItemEnumDataType.MONOMER_STRUCTURE) {
            this.setBackground(GuiDefinitions.TABLE_CELL_NON_EDITABLE_MONOMER_STRUCTURE_BACKGROUND_COLOR);
        } else {
            this.setBackground(GuiDefinitions.TABLE_CELL_NON_EDITABLE_BACKGROUND_COLOR);
        }
        this.setForeground(GuiDefinitions.TABLE_CELL_NON_EDITABLE_FOREGROUND_COLOR);
        // If cell is editable and highlighted set highlight background color
        if (this.valueItem.getTypeFormat(aRow, aColumn).isHighlighted()) {
            this.setForeground(GuiDefinitions.TABLE_CELL_HIGHLIGHT_FOREGROUND_COLOR);
            this.setBackground(GuiDefinitions.TABLE_CELL_HIGHLIGHT_BACKGROUND_COLOR);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Set text alignment">
        if (this.valueItem.getTypeFormat(aRow, aColumn).getDataType() == ValueItemEnumDataType.NUMERIC) {
            this.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            this.setHorizontalAlignment(SwingConstants.CENTER);
        }
        // </editor-fold>
        return super.getTableCellRendererComponent(aTable, aColor, anIsSelected, aHasFocus, aRow, aColumn);
    }
    // </editor-fold>

}
