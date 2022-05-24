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
package de.gnwi.mfsim.gui.util;

import de.gnwi.mfsim.model.util.StandardColorEnum;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Color cell renderer for combo box
 *
 * @author Achim Zielesny
 */
public class ComboBoxColorCellRenderer extends DefaultListCellRenderer {

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public ComboBoxColorCellRenderer() {
        super();
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Standard method
     *
     * @param aList Table
     * @param aValue Value
     * @param anIndex Index
     * @param anIsSelected Selection flag
     * @param aHasFocus Focus flag
     * @return Renderer component
     */
    @Override
    public Component getListCellRendererComponent(JList aList, Object aValue, int anIndex, boolean anIsSelected, boolean aHasFocus) {
        JLabel tmpLabel = null;
        if (anIsSelected && aValue != null && StandardColorEnum.isStandardColor(aValue.toString())) {
            Component tmpComponent = super.getListCellRendererComponent(aList, aValue, anIndex, anIsSelected, aHasFocus);
            if (tmpComponent instanceof JLabel) {
                tmpLabel = (JLabel) super.getListCellRendererComponent(aList, aValue, anIndex, anIsSelected, aHasFocus);
                tmpLabel.setBackground(StandardColorEnum.toStandardColor(aValue.toString()).toColor());
                tmpLabel.setForeground(StandardColorEnum.toStandardColor(aValue.toString()).toForegroundColor());
            }
        }
        if (tmpLabel != null) {
            return tmpLabel;
        } else {
            return super.getListCellRendererComponent(aList, aValue, anIndex, anIsSelected, aHasFocus);
        }
    }
    // </editor-fold>

}
