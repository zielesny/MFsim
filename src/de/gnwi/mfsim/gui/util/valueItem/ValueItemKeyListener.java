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

import de.gnwi.mfsim.model.message.ModelMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;

/**
 * Key listener for value item text fields
 *
 * @author Achim Zielesny
 */
public class ValueItemKeyListener implements KeyListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Text field
     */
    private JTextField textField;
    /**
     * ValueItemEnumDataType format
     */
    private ValueItemDataTypeFormat typeFormat;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aTextField Text field
     * @param aTypeFormat ValueItemEnumDataType format
     */
    public ValueItemKeyListener(JTextField aTextField, ValueItemDataTypeFormat aTypeFormat) {
        this.setTextFieldAndTypeFormat(aTextField, aTypeFormat);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Constructor
     *
     * @param aTextField Text field
     * @param aTypeFormat ValueItemEnumDataType format
     */
    public void setTextFieldAndTypeFormat(JTextField aTextField, ValueItemDataTypeFormat aTypeFormat) {

        this.textField = aTextField;
        this.typeFormat = aTypeFormat;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public events">
    /**
     * Key typed
     *
     * @param aKeyEvent Key event
     */
    public void keyTyped(final KeyEvent aKeyEvent) {
        if (this.typeFormat != null && this.textField != null) {
            switch (this.typeFormat.getDataType()) {

                case NUMERIC:
                case NUMERIC_NULL:

                    // <editor-fold defaultstate="collapsed" desc="NUMERIC or NUMERIC_NULL">

                    switch (aKeyEvent.getKeyChar()) {

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':

                            // <editor-fold defaultstate="collapsed" desc="Digits">

                            if (this.typeFormat.getMaximumValue() < 0 && !this.textField.getText().startsWith("-")) {
                                aKeyEvent.consume();
                                return;
                            }
                            if (this.typeFormat.getNumberOfDecimals() > 0
                                    && this.typeFormat.getNumberOfDecimals() <= ModelUtils.getNumberOfDecimals(this.textField.getText())
                                    && this.textField.getSelectionStart() > ModelUtils.getPositionOfDecimalSeparator(this.textField.getText())
                                    && this.textField.getSelectionStart() == this.textField.getSelectionEnd()) {
                                aKeyEvent.consume();
                                return;
                            }
                            return;

                        // </editor-fold>

                        case ',':

                            // <editor-fold defaultstate="collapsed" desc="Decimal separator ','">

                            // Change comma to decimal point for convenience
                            aKeyEvent.setKeyChar('.');

                        // </editor-fold>

                        case '.':

                            // <editor-fold defaultstate="collapsed" desc="Decimal separator '.'">

                            if (this.textField.getText().isEmpty() || this.textField.getText().indexOf(".") != -1 || this.typeFormat.getNumberOfDecimals() == 0) {
                                aKeyEvent.consume();
                                return;
                            } else {
                                return;
                            }

                        // </editor-fold>

                        case '-':

                            // <editor-fold defaultstate="collapsed" desc="Minus sign">

                            if (this.typeFormat.getMinimumValue() >= 0 || this.textField.getSelectionStart() > 0) {
                                aKeyEvent.consume();
                                return;
                            } else {
                                return;
                            }

                        // </editor-fold>

                        default:

                            // <editor-fold defaultstate="collapsed" desc="Default">

                            if (this.typeFormat.getDataType() == ValueItemEnumDataType.NUMERIC) {

                                // <editor-fold defaultstate="collapsed" desc="NUMERIC">
                                aKeyEvent.consume();
                                return;
                                // </editor-fold>

                            } else {

                                // <editor-fold defaultstate="collapsed" desc="NUMERIC_NULL">

                                char[] tmpChars = ModelMessage.get("ValueItemDataTypeFormat.NumericNullValueString").toCharArray();
                                boolean tmpIsMatch = false;
                                for (char tmpSingleChar : tmpChars) {
                                    if (aKeyEvent.getKeyChar() == tmpSingleChar) {
                                        tmpIsMatch = true;
                                        break;
                                    }
                                }
                                if (!tmpIsMatch) {
                                    aKeyEvent.consume();
                                }
                                return;

                                // </editor-fold>

                            }

                        // </editor-fold>
                        // </editor-fold>

                    }

                // </editor-fold>

                default:

                    // <editor-fold defaultstate="collapsed" desc="Others formats">

                    if (!this.typeFormat.isCharacterAllowed(aKeyEvent.getKeyChar())) {
                        aKeyEvent.consume();
                    }
                    return;

                // </editor-fold>

            }
        }
    }

    /**
     * Key released
     *
     * @param aKeyEvent Key event
     */
    public void keyReleased(final KeyEvent aKeyEvent) {
        // Do nothing
    }

    /**
     * Key pressed
     *
     * @param aKeyEvent Key event
     */
    public void keyPressed(final KeyEvent aKeyEvent) {
        // Do nothing
    }
    // </editor-fold>
}
