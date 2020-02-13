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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.valueItem.ValueItem;

/**
 * Item state listener for value item selection texts
 *
 * @author Achim Zielesny
 */
public class ValueItemSelectionItemListener implements ItemListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Text field
     */
    private JComboBox comboBox;
    /**
     * ValueItemEnumDataType format
     */
    private ValueItem valueItem;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aComboBox Combo box
     * @param aValueItem Value item
     */
    public ValueItemSelectionItemListener(JComboBox aComboBox, ValueItem aValueItem) {
        this.setComboBoxAndValueItem(aComboBox, aValueItem);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Constructor
     *
     * @param aComboBox Combo box
     * @param aValueItem Value item
     */
    public void setComboBoxAndValueItem(JComboBox aComboBox, ValueItem aValueItem) {
        this.comboBox = aComboBox;
        this.valueItem = aValueItem;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public events">
    /**
     * Item state changed
     *
     * @param anItemEvent Item event
     */
    public void itemStateChanged(ItemEvent anItemEvent) {
        try {
            // Set mouse wait cursor since value item update cascade may take its time
            MouseCursorManagement.getInstance().setWaitCursor();
            // NOTE: The this.valueItem.setValue() method sets value at the correct matrix position due to setting in
            // ValueItemTableModelMatrixEdit.isCellEditable() method. This is necessary since otherwise edit information may get lost due to a focus change.
            if (this.valueItem.setValue(this.comboBox.getSelectedItem().toString())) {
                this.valueItem.notifyDependentValueItemsForUpdate();
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">

            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "itemStateChanged()",
                    "ValueItemSelectionItemListener"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
}
