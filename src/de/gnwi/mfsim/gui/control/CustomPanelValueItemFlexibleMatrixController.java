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

import de.gnwi.mfsim.model.changeNotification.ChangeInformation;
import de.gnwi.mfsim.model.changeNotification.ChangeReceiverInterface;
import de.gnwi.mfsim.model.changeNotification.ChangeTypeEnum;
import de.gnwi.mfsim.gui.dialog.DialogValueItemEdit;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemCellEditorEdit;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemTableCellRendererMatrixEdit;
import de.gnwi.mfsim.gui.util.valueItem.ValueItemTableModelMatrixEdit;
import de.gnwi.mfsim.model.job.JobUtilityMethods;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.util.MouseCursorManagement;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemContainer;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumBasicType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumStatus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import de.gnwi.mfsim.model.preference.ModelDefinitions;
import de.gnwi.mfsim.model.preference.Preferences;
import de.gnwi.spices.SpicesConstants;

/**
 * Controller for class CustomPanelValueItemFlexibleMatrix
 *
 * @author Achim Zielesny
 */
public class CustomPanelValueItemFlexibleMatrixController implements ChangeReceiverInterface {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Job utility methods
     */
    private final JobUtilityMethods jobUtilityMethods = new JobUtilityMethods();

    /**
     * CustomPanelValueItemFlexibleMatrix instance
     */
    private CustomPanelValueItemFlexibleMatrix flexibleMatrixPanel;

    /**
     * Table model of matrix of value item
     */
    private ValueItemTableModelMatrixEdit matrixTableModel;

    /**
     * Table cell renderer for matrix table
     */
    private ValueItemTableCellRendererMatrixEdit tableCellRenderer;

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
    public CustomPanelValueItemFlexibleMatrixController(
        CustomPanelValueItemFlexibleMatrix aCustomValueItemFlexibleMatrixPanel, 
        ValueItem aValueItem
    ) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aCustomValueItemFlexibleMatrixPanel == null) {
            throw new IllegalArgumentException("aCustomValueItemFlexibleMatrixPanel is null.");
        }
        // </editor-fold>
        try {
            // IMPORTANT: Set this.flexibleMatrixPanel FIRST
            this.flexibleMatrixPanel = aCustomValueItemFlexibleMatrixPanel;
            // IMPORTANT: Value item MUST be set AFTER this.flexibleMatrixPanel
            this.setValueItem(aValueItem);
            // <editor-fold defaultstate="collapsed" desc="Add listener">
            this.flexibleMatrixPanel.getSetColumnsButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemFlexibleMatrixController.this.setColumnsWithValues();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelValueItemFlexibleMatrixController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);
                        // </editor-fold>
                    }
                }
            });
            this.flexibleMatrixPanel.getCopyRowButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemFlexibleMatrixController.this.copyRow();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelValueItemFlexibleMatrixController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.flexibleMatrixPanel.getInsertRowButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemFlexibleMatrixController.this.insertRow();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelValueItemFlexibleMatrixController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            this.flexibleMatrixPanel.getRemoveRowButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try {
                        CustomPanelValueItemFlexibleMatrixController.this.removeRow();
                    } catch (Exception anException) {
                        ModelUtils.appendToLogfile(true, anException);
                        // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
                        JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "actionPerformed()",
                                "CustomPanelValueItemFlexibleMatrixController"), GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

                        // </editor-fold>
                    }
                }

            });
            // </editor-fold>
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "Constructor()", "CustomPanelValueItemFlexibleMatrixController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Inserts row under selected row in flexible matrix
     */
    public void insertRow() {
        int tmpSelectedRow;
        if (this.matrixTableModel.getRowCount() == 1) {
            tmpSelectedRow = 0;
        } else {
            tmpSelectedRow = this.flexibleMatrixPanel.getMatrixTable().getSelectedRow();
            if (tmpSelectedRow == -1) {
                // Select last row if no row is selected
                tmpSelectedRow = this.matrixTableModel.getRowCount() - 1;
            }
        }
        // Cancel cell editing FIRST
        if (this.flexibleMatrixPanel.getMatrixTable().getCellEditor() != null) {
            if (!this.flexibleMatrixPanel.getMatrixTable().getCellEditor().stopCellEditing()) {
                this.flexibleMatrixPanel.getMatrixTable().getCellEditor().cancelCellEditing();
            }
        }
        this.matrixTableModel.insertRow(tmpSelectedRow + 1);
        if (this.flexibleMatrixPanel.getMatrixTable().requestFocusInWindow()) {
            int tmpFirstEditableColumn = this.matrixTableModel.getFirstEditableColumn();
            if (tmpFirstEditableColumn > -1) {
                this.flexibleMatrixPanel.getMatrixTable().changeSelection(tmpSelectedRow + 1, tmpFirstEditableColumn, false, false);
                // NOTE: Do NOT use editCellAt()-method because of strange effects with next TAB-key hit
            }
        }
        this.updateDisplay();
    }

    /**
     * Removes selected row from flexible matrix
     */
    public void removeRow() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.matrixTableModel.getRowCount() == 1) {
            return;
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Remove row">
        try {
            MouseCursorManagement.getInstance().setWaitCursor();
            int tmpSelectedRow = this.flexibleMatrixPanel.getMatrixTable().getSelectedRow();
            if (tmpSelectedRow == -1) {
                // Select last row if no row is selected
                tmpSelectedRow = this.matrixTableModel.getRowCount() - 1;
            }
            // Cancel cell editing FIRST
            if (this.flexibleMatrixPanel.getMatrixTable().getCellEditor() != null) {
                this.flexibleMatrixPanel.getMatrixTable().getCellEditor().cancelCellEditing();
            }
            this.matrixTableModel.removeRow(tmpSelectedRow);
            if (this.flexibleMatrixPanel.getMatrixTable().requestFocusInWindow()) {
                int tmpFirstEditableColumn = this.matrixTableModel.getFirstEditableColumn();
                if (tmpFirstEditableColumn > -1) {
                    if (tmpSelectedRow == this.matrixTableModel.getRowCount()) {
                        tmpSelectedRow = tmpSelectedRow - 1;
                    }
                    this.flexibleMatrixPanel.getMatrixTable().changeSelection(tmpSelectedRow, tmpFirstEditableColumn, false, false);
                    // NOTE: Do NOT use editCellAt()-method because of strange effects with next TAB-key hit
                }
            }
            this.updateDisplay();
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }

        // </editor-fold>
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
            // Set actions panel
            this.flexibleMatrixPanel.getActionsPanel().setVisible(this.valueItem.getBasicType() == ValueItemEnumBasicType.FLEXIBLE_MATRIX);
            this.flexibleMatrixPanel.getSetColumnsPanel().setVisible(this.valueItem.isColumnValueSet());
            // IMPORTANT: Clear selection before changing table model
            this.flexibleMatrixPanel.getMatrixTable().clearSelection();
            if (this.matrixTableModel == null) {
                this.matrixTableModel = new ValueItemTableModelMatrixEdit(this.valueItem, this.flexibleMatrixPanel.getInfoLabel());
                this.flexibleMatrixPanel.getMatrixTable().setModel(this.matrixTableModel);
                ValueItemCellEditorEdit tmpValueItemCellEditorEdit = new ValueItemCellEditorEdit();
                this.flexibleMatrixPanel.getMatrixTable().setDefaultEditor(String.class, tmpValueItemCellEditorEdit);
                this.tableCellRenderer = new ValueItemTableCellRendererMatrixEdit(this.valueItem, this.flexibleMatrixPanel.getInfoLabel());
                this.flexibleMatrixPanel.getMatrixTable().setDefaultRenderer(String.class, this.tableCellRenderer);
            } else {
                this.tableCellRenderer.setValueItem(this.valueItem);
                this.matrixTableModel.setValueItem(this.valueItem);
            }

            // </editor-fold>
            this.updateDisplay();
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);

            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "setValueItem()", "CustomPanelValueItemFlexibleMatrixController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
   // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public notifyChange() method">
    /**
     * Notify method for this instance as a change receiver
     *
     * @param aChangeInfo Change information
     * @param aChangeNotifier Object that notifies change
     */
    @Override
    public void notifyChange(Object aChangeNotifier, ChangeInformation aChangeInfo) {
        try {
            if (aChangeNotifier == this.valueItem) {
                if (aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_VALUE_CHANGE || aChangeInfo.getChangeType() == ChangeTypeEnum.VALUE_ITEM_MATRIX_CHANGE) {
                    this.flexibleMatrixPanel.getMatrixTable().repaint();
                }
            }
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            // <editor-fold defaultstate="collapsed" desc="Message CommandExecutionFailed">
            JOptionPane.showMessageDialog(null, String.format(GuiMessage.get("Error.CommandExecutionFailed"), "notifyChange()", "CustomPanelValueItemFlexibleMatrixController"),
                    GuiMessage.get("Error.ErrorNotificationTitle"), JOptionPane.ERROR_MESSAGE);

            // </editor-fold>
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Actions">
    /**
     * Copies selected row in flexible matrix to the end
     */
    private void copyRow() {
        int tmpSelectedRow;
        if (this.matrixTableModel.getRowCount() == 1) {
            tmpSelectedRow = 0;
        } else {
            tmpSelectedRow = this.flexibleMatrixPanel.getMatrixTable().getSelectedRow();
            if (tmpSelectedRow == -1) {
                // Select last row if no row is selected
                tmpSelectedRow = this.matrixTableModel.getRowCount() - 1;
            }
        }
        // Cancel cell editing FIRST
        if (this.flexibleMatrixPanel.getMatrixTable().getCellEditor() != null) {
            if (!this.flexibleMatrixPanel.getMatrixTable().getCellEditor().stopCellEditing()) {
                this.flexibleMatrixPanel.getMatrixTable().getCellEditor().cancelCellEditing();
            }
        }
        this.matrixTableModel.copyRow(tmpSelectedRow);
        if (this.flexibleMatrixPanel.getMatrixTable().requestFocusInWindow()) {
            int tmpFirstEditableColumn = this.matrixTableModel.getFirstEditableColumn();
            if (tmpFirstEditableColumn > -1) {
                this.flexibleMatrixPanel.getMatrixTable().changeSelection(tmpSelectedRow + 1, tmpFirstEditableColumn, false, false);
                // NOTE: Do NOT use editCellAt()-method because of strange effects with next TAB-key hit
            }
        }
        this.updateDisplay();
    }

    /**
     * Sets columns with specified values
     */
    private void setColumnsWithValues() {
        try {
            if (this.valueItem.isColumnValueSet()) {
                MouseCursorManagement.getInstance().setWaitCursor();
                ValueItemContainer tmpColumnsValueItemContainer = new ValueItemContainer(null);
                String[] tmpNodeNames = new String[]{GuiMessage.get("columnValueSettings.Nodename")};
                // <editor-fold defaultstate="collapsed" desc="Set column value items">
                for (int i = 0; i < this.valueItem.getMatrixColumnCount(); i++) {
                    if (this.valueItem.isColumnValueSet(i)) {
                        String tmpColumnName = this.valueItem.getMatrixColumnName(i);
                        ValueItemDataTypeFormat tmpValueItemDataTypeFormat = this.valueItem.getValueItemMatrixElement(0, i).getTypeFormat().getClone();
                        // Convert data type NUMERIC to data type NUMERIC_NULL to allow for setting the default value (see below)
                        tmpValueItemDataTypeFormat.convertToNumericNull();
                        // If data type format of at least first row is selection text then combine all possible selection texts
                        // NOTE: The data type format of later rows may NOT be selection text
                        if (tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.SELECTION_TEXT) {
                            tmpValueItemDataTypeFormat = this.valueItem.getCombinedSelectionTextColumnDataTypeFormat(i);
                            if (tmpValueItemDataTypeFormat == null) {
                                JOptionPane.showMessageDialog(null, 
                                    GuiMessage.get("Information.NoSelectionTextCombination"), 
                                    GuiMessage.get("Information.NoSelectionTextCombinationTitle"),
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                                return;
                            }
                        }
                        // <editor-fold defaultstate="collapsed" desc="Set column value item">
                        ValueItemDataTypeFormat tmpFilterDataTypeFormat = 
                            new ValueItemDataTypeFormat(
                                GuiMessage.get("columnValueSettings.Filter.Off"), 
                                new String[]{
                                    GuiMessage.get("columnValueSettings.Filter.On"),
                                    GuiMessage.get("columnValueSettings.Filter.Off")
                                }
                            );
                        LinkedList<String> tmpMatrixColumnNameList = new LinkedList<>();
                        tmpMatrixColumnNameList.add(tmpColumnName);
                        LinkedList<String> tmpMatrixColumnWidthList = new LinkedList<>();
                        tmpMatrixColumnWidthList.add(ModelDefinitions.CELL_WIDTH_TEXT_150);
                        LinkedList<ValueItemDataTypeFormat> tmpDefaultDataTypeFormatList = new LinkedList<>();
                        tmpDefaultDataTypeFormatList.add(tmpValueItemDataTypeFormat);
                        LinkedList<String> tmpSupplementaryInformationList = new LinkedList<>();
                        tmpSupplementaryInformationList.add(String.valueOf(-1));
                        boolean tmpHasFilter = false;
                        int tmpFilterIndex = 1;
                        for (int k = 0; k < this.valueItem.getMatrixColumnCount(); k++) {
                            ValueItemDataTypeFormat tmpCombinedTextColumnDataTypeFormat = this.valueItem.getCombinedTextColumnDataTypeFormat(k);
                            if (tmpCombinedTextColumnDataTypeFormat != null) {
                                tmpHasFilter = true;
                                // Filter
                                tmpMatrixColumnNameList.add(String.format(GuiMessage.get("columnValueSettings.FilterFormat"),
                                        GuiMessage.get("columnValueSettings.Filter"),
                                        tmpFilterIndex
                                    )
                                );
                                tmpMatrixColumnWidthList.add(ModelDefinitions.CELL_WIDTH_NUMERIC_80);
                                tmpDefaultDataTypeFormatList.add(tmpFilterDataTypeFormat);
                                tmpSupplementaryInformationList.add(String.valueOf(k));
                                // Entries
                                if (k == i) {
                                    tmpMatrixColumnNameList.add(String.format(GuiMessage.get("columnValueSettings.FilterCriterionFormat"),
                                            tmpFilterIndex,
                                            GuiMessage.get("columnValueSettings.Filter.OldEntry")
                                        )
                                    );
                                } else {
                                    tmpMatrixColumnNameList.add(String.format(GuiMessage.get("columnValueSettings.FilterCriterionFormat"),
                                            tmpFilterIndex,
                                            this.valueItem.getMatrixColumnName(k)
                                        )
                                    );
                                }
                                tmpMatrixColumnWidthList.add(ModelDefinitions.CELL_WIDTH_TEXT_150);
                                tmpDefaultDataTypeFormatList.add(tmpCombinedTextColumnDataTypeFormat);
                                tmpSupplementaryInformationList.add(String.valueOf(k));
                                tmpFilterIndex++;
                            }
                        }
                        if (tmpHasFilter) {
                            // <editor-fold defaultstate="collapsed" desc="With filters">
                            ValueItem tmpColumnValueItem = new ValueItem();
                            tmpColumnValueItem.setNodeNames(tmpNodeNames);
                            tmpColumnValueItem.setName("COLUMN_" + String.valueOf(i));
                            tmpColumnValueItem.setDisplayName(tmpColumnName);
                            tmpColumnValueItem.setDescription(String.format(GuiMessage.get("columnValueSettings.Description"), tmpColumnName));
                            tmpColumnValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
                            tmpColumnValueItem.setMatrixColumnNames(tmpMatrixColumnNameList.toArray(new String[0]));
                            tmpColumnValueItem.setMatrixColumnWidths(tmpMatrixColumnWidthList.toArray(new String[0]));
                            tmpColumnValueItem.setDefaultTypeFormats(tmpDefaultDataTypeFormatList.toArray(new ValueItemDataTypeFormat[0]));
                            tmpColumnValueItem.setSupplementaryData(tmpSupplementaryInformationList.toArray(new String[0]));
                            // NOTE: Set essential to false to avoid disappearance of activity check box in GUI after activity is set to true!
                            tmpColumnValueItem.setEssential(false);
                            tmpColumnValueItem.setActivity(false);
                            // This is important: Vertical position = Column index
                            tmpColumnValueItem.setVerticalPosition(i);
                            tmpColumnsValueItemContainer.addValueItem(tmpColumnValueItem);
                            // </editor-fold>
                        } else {
                            // <editor-fold defaultstate="collapsed" desc="Without filters">
                            ValueItem tmpColumnValueItem = new ValueItem();
                            tmpColumnValueItem.setNodeNames(tmpNodeNames);
                            tmpColumnValueItem.setName("COLUMN_" + String.valueOf(i));
                            tmpColumnValueItem.setDisplayName(tmpColumnName);
                            tmpColumnValueItem.setDescription(String.format(GuiMessage.get("columnValueSettings.Description"), tmpColumnName));
                            tmpColumnValueItem.setDefaultTypeFormat(tmpValueItemDataTypeFormat);
                            // NOTE: Set essential to false to avoid disappearance of activity check box in GUI after activity is set to true!
                            tmpColumnValueItem.setEssential(false);
                            tmpColumnValueItem.setActivity(false);
                            tmpColumnValueItem.setVerticalPosition(i);
                            tmpColumnsValueItemContainer.addValueItem(tmpColumnValueItem);
                            // </editor-fold>
                        }
                        // </editor-fold>
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set line range value item">
                ValueItem tmpLineRangeValueItem = new ValueItem();
                tmpLineRangeValueItem.setNodeNames(tmpNodeNames);
                tmpLineRangeValueItem.setName("LINE_RANGE");
                tmpLineRangeValueItem.setDisplayName(GuiMessage.get("columnValueSettings.LineRange"));
                tmpLineRangeValueItem.setDescription(GuiMessage.get("columnValueSettings.LineRange.Description"));
                tmpLineRangeValueItem.setBasicType(ValueItemEnumBasicType.VECTOR);
                tmpLineRangeValueItem.setMatrixColumnNames(new String[]{
                    GuiMessage.get("columnValueSettings.LineRange.StartLine"),
                    GuiMessage.get("columnValueSettings.LineRange.EndLine")});
                tmpLineRangeValueItem.setMatrixColumnWidths(new String[]{
                    ModelDefinitions.CELL_WIDTH_TEXT_150, // StartLine
                    ModelDefinitions.CELL_WIDTH_TEXT_150}); // EndLine
                String[] tmpLines = ModelUtils.getNumberStringsForInterval(1, this.valueItem.getMatrixRowCount());
                tmpLineRangeValueItem.setDefaultTypeFormats(new ValueItemDataTypeFormat[]{
                    new ValueItemDataTypeFormat(tmpLines[0], tmpLines),
                    new ValueItemDataTypeFormat(tmpLines[tmpLines.length - 1], tmpLines)
                });
                // NOTE: Set essential to false to avoid disappearance of activity check box in GUI after activity is set to true!
                tmpLineRangeValueItem.setEssential(false);
                tmpLineRangeValueItem.setActivity(false);
                tmpLineRangeValueItem.setVerticalPosition(this.valueItem.getMatrixColumnCount());
                tmpColumnsValueItemContainer.addValueItem(tmpLineRangeValueItem);
                // </editor-fold>
                MouseCursorManagement.getInstance().setDefaultCursor();
                if (DialogValueItemEdit.hasChanged(GuiMessage.get("columnValueSettings.dialogTitle"), tmpColumnsValueItemContainer)) {
                    MouseCursorManagement.getInstance().setWaitCursor();
                    // <editor-fold defaultstate="collapsed" desc="Apply changes">
                    LinkedList<ValueItem> tmpValueItemList = tmpColumnsValueItemContainer.getSortedValueItemsWithStatus(ValueItemEnumStatus.ACTIVE);
                    if (!(tmpValueItemList == null || tmpValueItemList.isEmpty())) {
                        int tmpStartRow = 0;
                        int tmpEndRow = this.valueItem.getMatrixRowCount() - 1;
                        // Evaluate LINE_RANGE value item if necessary
                        LinkedList<ValueItem> tmpColumnsValueItemList = new LinkedList<>();
                        for (ValueItem tmpValueItem : tmpValueItemList) {
                            if (tmpValueItem.getName().equals("LINE_RANGE")) {
                                tmpStartRow = Math.min(tmpValueItem.getValueAsInt(0, 0) - 1, tmpValueItem.getValueAsInt(0, 1) - 1);
                                tmpEndRow = Math.max(tmpValueItem.getValueAsInt(0, 0) - 1, tmpValueItem.getValueAsInt(0, 1) - 1);
                            } else {
                                tmpColumnsValueItemList.add(tmpValueItem);
                            }
                        }
                        if (!tmpColumnsValueItemList.isEmpty()) {
                            boolean tmpIsChanged = false;
                            for (ValueItem tmpActiveValueItem : tmpColumnsValueItemList) {
                                int tmpColumn = tmpActiveValueItem.getVerticalPosition();
                                // NOTE: Index (0, 0) is used if tmpActiveValueItem is of basic type SCALAR or VECTOR
                                String tmpDefaultValue = tmpActiveValueItem.getValue(0, 0);
                                boolean tmpIsDefaultValueNumericNull = tmpActiveValueItem.hasNumericNullValue(0, 0);
                                LinkedList<Integer> tmpRowList = new LinkedList<>();
                                for (int i = tmpStartRow; i <= tmpEndRow; i++) {
                                    tmpRowList.add(i);
                                }
                                boolean tmpHasFilter = tmpActiveValueItem.getBasicType() == ValueItemEnumBasicType.VECTOR;
                                if (tmpHasFilter) {
                                    // <editor-fold defaultstate="collapsed" desc="Filter rows">
                                    int tmpNumberOfFilters = (tmpActiveValueItem.getMatrixColumnCount() - 1) / 2;
                                    // Determine rows for set operation
                                    Iterator<Integer> tmpIterator = tmpRowList.iterator();
                                    while (tmpIterator.hasNext()) {
                                        int tmpRow = tmpIterator.next();
                                        for (int i = 0; i < tmpNumberOfFilters; i++) {
                                            int tmpColumnIndexOfFilter = 2 * i + 1;
                                            if (tmpActiveValueItem.getValue(0, tmpColumnIndexOfFilter).equals(GuiMessage.get("columnValueSettings.Filter.On"))) {
                                                int tmpOriginalColumnForFilter = Integer.valueOf(tmpActiveValueItem.getSupplementaryData()[tmpColumnIndexOfFilter]);
                                                // Check if *PD1, *PD2 etc. protein backbone probe particle
                                                if (tmpActiveValueItem.getValue(0, tmpColumnIndexOfFilter + 1).startsWith(ModelDefinitions.GENERAL_PROTEIN_BACKBONE_PROBE_START)) {
                                                    String tmpProteinBackboneProbeEnding = 
                                                        tmpActiveValueItem.getValue(0, tmpColumnIndexOfFilter + 1).substring(ModelDefinitions.GENERAL_PROTEIN_BACKBONE_PROBE_START.length());
                                                    if (!this.valueItem.getValue(tmpRow, tmpOriginalColumnForFilter).endsWith(tmpProteinBackboneProbeEnding)) {
                                                        tmpIterator.remove();
                                                        break;
                                                    }
                                                } else {
                                                    if (!tmpActiveValueItem.getValue(0, tmpColumnIndexOfFilter + 1).equals(this.valueItem.getValue(tmpRow, tmpOriginalColumnForFilter))) {
                                                        tmpIterator.remove();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // </editor-fold>
                                }
                                for (int tmpRow : tmpRowList) {
                                    ValueItemDataTypeFormat tmpValueItemDataTypeFormat = this.valueItem.getValueItemMatrixElement(tmpRow, tmpColumn).getTypeFormat();
                                    if (tmpValueItemDataTypeFormat.isEditable()) {
                                        if (tmpValueItemDataTypeFormat.getDataType() == ValueItemEnumDataType.SELECTION_TEXT) {
                                            if (tmpValueItemDataTypeFormat.hasSelectionText(tmpDefaultValue)) {
                                                if (this.valueItem.setValue(tmpDefaultValue, tmpRow, tmpColumn)) {
                                                    this.valueItem.notifyDependentValueItemsForUpdate();
                                                    tmpIsChanged = true;
                                                }
                                            } else {
                                                // Check if tmpDefaultValue = *PD1, *PD2 etc. protein backbone probe particle
                                                String tmpMatchingSelectionText = tmpValueItemDataTypeFormat.getMatchingProteinBackboneProbeParticleSelectionText(tmpDefaultValue);
                                                if (tmpMatchingSelectionText != null) {
                                                    if (this.valueItem.setValue(tmpMatchingSelectionText, tmpRow, tmpColumn)) {
                                                        this.valueItem.notifyDependentValueItemsForUpdate();
                                                        tmpIsChanged = true;
                                                    }
                                                }
                                            }
                                        } else {
                                            if (tmpIsDefaultValueNumericNull && this.valueItem.getTypeFormat(tmpRow, tmpColumn).getDataType() == ValueItemEnumDataType.NUMERIC) {
                                                // Numeric-null value for data type NUMERIC means setting the default value
                                                // Bonds12Table value item needs special treatment for bond length (column 1)
                                                if (this.valueItem.getName().equals("Bonds12Table") && tmpColumn == 1) {
                                                    if (this.updateVolumeBasedBondLengthsInBonds12(this.valueItem, tmpRow)) {
                                                        this.valueItem.notifyDependentValueItemsForUpdate();
                                                        tmpIsChanged = true;
                                                    }
                                                } else {
                                                    if (this.valueItem.setDefaultValue(tmpRow, tmpColumn)) {
                                                        this.valueItem.notifyDependentValueItemsForUpdate();
                                                        tmpIsChanged = true;
                                                    }
                                                }
                                            } else {
                                                if (this.valueItem.setValue(tmpDefaultValue, tmpRow, tmpColumn)) {
                                                    this.valueItem.notifyDependentValueItemsForUpdate();
                                                    tmpIsChanged = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (tmpIsChanged) {
                                this.flexibleMatrixPanel.getMatrixTable().repaint();
                            }
                        }
                    }
                    // </editor-fold>
                    MouseCursorManagement.getInstance().setDefaultCursor();
                }
            }
        } finally {
            MouseCursorManagement.getInstance().setDefaultCursor();
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Update display">
    /**
     * Updates display
     */
    private void updateDisplay() {
        // <editor-fold defaultstate="collapsed" desc="Set column width">
        if (this.valueItem.hasMatrixColumnWidths()) {
            // <editor-fold defaultstate="collapsed" desc="Column widths are defined">
            this.flexibleMatrixPanel.getMatrixTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            int tmpNumberOfColumns = this.flexibleMatrixPanel.getMatrixTable().getColumnModel().getColumnCount();
            for (int i = 0; i < tmpNumberOfColumns; i++) {
                TableColumn tmpColumn = this.flexibleMatrixPanel.getMatrixTable().getColumnModel().getColumn(i);
                tmpColumn.setPreferredWidth(this.valueItem.getMatrixColumnWidthAsInt(i));
            }

            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Column widths are not defined">
            this.flexibleMatrixPanel.getMatrixTable().setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

            // </editor-fold>
        }
        // </editor-fold>
        this.flexibleMatrixPanel.getInsertRowButton().setVisible(this.valueItem.canInsertMatrixRow());
        this.flexibleMatrixPanel.getCopyRowButton().setVisible(this.valueItem.canInsertMatrixRow());
        this.flexibleMatrixPanel.getRemoveRowButton().setVisible(this.matrixTableModel.getRowCount() > 1);
        if (this.valueItem.hasEditableColumn()) {
            this.flexibleMatrixPanel.getInfoLabel().setText(GuiMessage.get("Information.MatrixEditDefaultHint"));
        } else {
            this.flexibleMatrixPanel.getInfoLabel().setText(GuiMessage.get("Information.MatrixNoEditHint"));
        }
        // Set vertical scroll bar to top
        this.flexibleMatrixPanel.getMatrixTableScrollPanel().getVerticalScrollBar().setValue(0);
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Updates volume-based bond length defaults in Bonds12Table value item
     * 
     * @param aBonds12TableValueItem Bonds12Table value item (may be changed)
     * @param aRow Row to be updated
     * @return True: aBonds12TableValueItem was changed, false: Otherwise
     */
    private boolean updateVolumeBasedBondLengthsInBonds12(ValueItem aBonds12TableValueItem, int aRow) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aBonds12TableValueItem == null || !aBonds12TableValueItem.getName().equals("Bonds12Table")) {
            return false;
        }
        if (aRow < 0 || aRow > aBonds12TableValueItem.getMatrixRowCount()) {
            return false;
        }
        // </editor-fold>
        double tmpLengthConversionFactor = this.jobUtilityMethods.getLengthConversionFactorFromDpdToPhysicalLength(aBonds12TableValueItem.getValueItemContainer());
        if (tmpLengthConversionFactor == -1.0) {
            return false;
        }
        String[] tmpParticles = SpicesConstants.PARTICLE_SEPARATOR_PATTERN.split(aBonds12TableValueItem.getValue(aRow, 0));
        double tmpVolumeBasedBondLengthInAngstrom = StandardParticleInteractionData.getInstance().getParticlePairVolumeBasedBondLength(tmpParticles[0], tmpParticles[1]);
        double tmpVolumeBasedBondLengthInDpd = tmpVolumeBasedBondLengthInAngstrom / tmpLengthConversionFactor;
        if (tmpVolumeBasedBondLengthInDpd < Preferences.getInstance().getMinimumBondLengthDpd()) {
            if (aBonds12TableValueItem.getValueAsDouble(aRow, 1) != Preferences.getInstance().getMinimumBondLengthDpd()) {
                // Set bond length to minimum bond length in DPD units
                aBonds12TableValueItem.setValue(String.valueOf(Preferences.getInstance().getMinimumBondLengthDpd()), aRow, 1);
                aBonds12TableValueItem.getTypeFormat(aRow, 1).setDefaultValue(String.valueOf(Preferences.getInstance().getMinimumBondLengthDpd()));
                return true;
            } else {
                return false;
            }
        } else {
            if (aBonds12TableValueItem.getValueAsDouble(aRow, 1) != tmpVolumeBasedBondLengthInDpd) {
                // Set bond length to volume-based bond length
                aBonds12TableValueItem.setValue(String.valueOf(tmpVolumeBasedBondLengthInDpd), aRow, 1);
                aBonds12TableValueItem.getTypeFormat(aRow, 1).setDefaultValue(String.valueOf(tmpVolumeBasedBondLengthInDpd));
                return true;
            } else {
                return false;
            }
        }
    }
    // </editor-fold>
    // </editor-fold>

}
