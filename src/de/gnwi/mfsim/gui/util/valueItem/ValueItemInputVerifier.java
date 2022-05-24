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

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Input verifier for value item related text fields. NOTE: Method shouldYieldFocus() sets value of value item if focus is lost.
 *
 * @author Achim Zielesny
 */
public class ValueItemInputVerifier extends InputVerifier {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Value item
     */
    private ValueItem valueItem;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructor
     *
     * @param aValueItem Value item
     */
    public ValueItemInputVerifier(ValueItem aValueItem) {
        super();
        this.setValueItem(aValueItem);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Value item
     *
     * @param aValueItem Value item
     */
    public void setValueItem(ValueItem aValueItem) {
        this.valueItem = aValueItem;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Should yield focus
     *
     * @param anInput Input (source)
     * @param aTarget Target (not used)
     * @return True: anInput is correct so yield focus, false: Otherwise
     */
    @Override
    public boolean shouldYieldFocus(JComponent anInput, JComponent aTarget) {
        try {
            if (this.verify(anInput) && this.valueItem != null) {
                // Set mouse wait cursor since value item update cascade may take its time
                MouseCursorManagement.getInstance().setWaitCursor();
                // NOTE: If input verifier is used within a table the this.valueItem.setValue() method sets value at the correct matrix position due to setting
                // in ValueItemTableModelMatrixEdit.isCellEditable() method. This is necessary since otherwise edit information may get lost due to a focus
                // change.
                if (this.valueItem.setValue(((JTextField) anInput).getText())) {
                    this.valueItem.notifyDependentValueItemsForUpdate();
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            MouseCursorManagement.getInstance().setDefaultCursor();
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "shouldYieldFocus()",
                    "ValueItemInputVerifier"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
            // </editor-fold>
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
        return true;
    }

    /**
     * Verify
     *
     * @param anInput An input
     * @return True: anInput is correct, false: Otherwise
     */
    @Override
    public boolean verify(JComponent anInput) {
        if (this.valueItem.getTypeFormat() != null && anInput != null) {
            return this.valueItem.getTypeFormat().isValueAllowed(((JTextField) anInput).getText());
        } else {
            return true;
        }
    }
    // </editor-fold>
}
