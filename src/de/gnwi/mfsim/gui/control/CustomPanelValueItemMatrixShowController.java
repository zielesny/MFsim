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

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemCellEditorShow;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemTableCellRendererMatrixShow;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemTableModelMatrixShow;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import de.gnwi.mfsim.model.util.ModelUtils;

/**
 * Controller for class CustomPanelValueItemMatrixShow
 *
 * @author Achim Zielesny
 */
public class CustomPanelValueItemMatrixShowController {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * CustomPanelValueItemFlexibleMatrix instance
     */
    private CustomPanelValueItemMatrixShow matrixShowPanel;
    /**
     * Table model of matrix of value item
     */
    private ValueItemTableModelMatrixShow matrixTableModel;
    /**
     * Table cell renderer for matrix table
     */
    private ValueItemTableCellRendererMatrixShow tableCellRenderer;
    /**
     * Value item instance
     */
    private ValueItem valueItem;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aCustomValueItemFlexibleMatrixPanel
     * CustomPanelValueItemFlexibleMatrix instance (not allowed to be null)
     * @param aValueItem Value item (not allowed to be null)
     * @throws IllegalArgumentException Thrown if an argument is null or illegal
     */
    public CustomPanelValueItemMatrixShowController(CustomPanelValueItemMatrixShow aCustomValueItemFlexibleMatrixPanel, ValueItem aValueItem)
            throws IllegalArgumentException {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomValueItemFlexibleMatrixPanel == null) {
            throw new IllegalArgumentException("aCustomValueItemFlexibleMatrixPanel is null.");
        }

        // </editor-fold>
        try {
            // IMPORTANT: Set this.matrixShowPanel FIRST
            this.matrixShowPanel = aCustomValueItemFlexibleMatrixPanel;
            // IMPORTANT: Value item MUST be set AFTER this.matrixShowPanel
            this.setValueItem(aValueItem);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor()",
                    "CustomPanelValueItemMatrixShowController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Value item
     *
     * @param aValueItem Value item
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
        try {
            this.valueItem = aValueItem;

            // <editor-fold defaultstate="collapsed" desc="Set table related objects">
            if (this.matrixTableModel == null) {
                this.matrixTableModel = new ValueItemTableModelMatrixShow(this.valueItem);
                this.matrixShowPanel.getMatrixTable().setModel(this.matrixTableModel);

                ValueItemCellEditorShow tmpValueItemCellEditorShow = new ValueItemCellEditorShow();
                this.matrixShowPanel.getMatrixTable().setDefaultEditor(String.class, tmpValueItemCellEditorShow);

                this.tableCellRenderer = new ValueItemTableCellRendererMatrixShow(this.valueItem);
                this.matrixShowPanel.getMatrixTable().setDefaultRenderer(String.class, this.tableCellRenderer);
            } else {
                this.tableCellRenderer.setValueItem(this.valueItem);
                this.matrixTableModel.setValueItem(this.valueItem);
            }

            // </editor-fold>
            this.updateDisplay();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setValueItem()",
                    "CustomPanelValueItemMatrixShowController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Update display">
    /**
     * Updates display
     */
    private void updateDisplay() {
        // <editor-fold defaultstate="collapsed" desc="Set column width">
        if (this.valueItem.hasMatrixColumnWidths()) {

            // <editor-fold defaultstate="collapsed" desc="Column widths are defined">
            this.matrixShowPanel.getMatrixTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            int tmpNumberOfColumns = this.matrixShowPanel.getMatrixTable().getColumnModel().getColumnCount();
            for (int i = 0; i < tmpNumberOfColumns; i++) {
                TableColumn tmpColumn = this.matrixShowPanel.getMatrixTable().getColumnModel().getColumn(i);
                tmpColumn.setPreferredWidth(this.valueItem.getMatrixColumnWidthAsInt(i));
            }

            // </editor-fold>
        } else {

            // <editor-fold defaultstate="collapsed" desc="Column widths are not defined">
            this.matrixShowPanel.getMatrixTable().setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

            // </editor-fold>
        }

        // </editor-fold>
    }
    // </editor-fold>
    // </editor-fold>

}
