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

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Document listener for value item based documents in text components
 *
 * @author Achim Zielesny
 */
public class ValueItemDocumentListener implements DocumentListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Value item instance
     */
    private ValueItem valueItem;
    /**
     * Text field
     */
    private JTextField textField;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aValueItem Value item (not allowed to be null)
     * @param aTextField Text field (not allowed to be null)
     * @throws IllegalArgumentException Thrown if aValueItem or aTextField is null
     */
    public ValueItemDocumentListener(ValueItem aValueItem, JTextField aTextField) throws IllegalArgumentException {
        this.setValueItemAndTextField(aValueItem, aTextField);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Value item
     *
     * @param aValueItem Value item (not allowed to be null)
     * @param aTextField Text field (not allowed to be null)
     * @throws IllegalArgumentException Thrown if aValueItem or aTextField is null
     */
    public void setValueItemAndTextField(ValueItem aValueItem, JTextField aTextField) throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">

        if (aValueItem == null) {
            throw new IllegalArgumentException("aValueItem is null.");
        }
        if (aTextField == null) {
            throw new IllegalArgumentException("aTextField is null.");
        }

        // </editor-fold>

        this.valueItem = aValueItem;
        this.textField = aTextField;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Standard interface method
     *
     * @param anDocumentEvent Document event
     */
    public void insertUpdate(DocumentEvent anDocumentEvent) {
        this.performSettings();
    }

    /**
     * Standard interface method
     *
     * @param anDocumentEvent Document event
     */
    public void removeUpdate(DocumentEvent anDocumentEvent) {
        this.performSettings();
    }

    /**
     * Standard interface method
     *
     * @param anDocumentEvent Document event
     */
    public void changedUpdate(DocumentEvent anDocumentEvent) {
        // NOTE: Plain text components don't fire these events, so do nothing
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Sets value item and background of text field
     */
    private void performSettings() {
        String tmpValue = this.textField.getText();
        if (this.valueItem.getTypeFormat().isValueAllowed(tmpValue)) {
            this.textField.setBackground(GuiDefinitions.TEXT_FIELD_BACKGROUND_CORRECT_COLOR);
            this.textField.setForeground(GuiDefinitions.TEXT_FIELD_FOREGROUND_CORRECT_COLOR);
            // NOTE: If this.valueItem is an update notifier performance problems could occur since every single change in this.textField would be followed by a
            // full update process. Therefore NO this.valueItem.setValue(tmpValue) is performed.
        } else {
            this.textField.setBackground(GuiDefinitions.TEXT_FIELD_BACKGROUND_WRONG_COLOR);
            this.textField.setForeground(GuiDefinitions.TEXT_FIELD_FOREGROUND_WRONG_COLOR);
        }
    }
    // </editor-fold>
}
