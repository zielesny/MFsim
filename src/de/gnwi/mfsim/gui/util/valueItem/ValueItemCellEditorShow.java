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

import de.gnwi.mfsim.gui.dialog.DialogStructureShow;
import de.gnwi.mfsim.gui.dialog.DialogProteinShow;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.util.ModelUtils;
import de.gnwi.mfsim.model.particle.StandardParticleInteractionData;
import de.gnwi.mfsim.model.valueItem.ValueItem;
import de.gnwi.mfsim.model.valueItem.ValueItemDataTypeFormat;
import de.gnwi.mfsim.model.valueItem.ValueItemEnumDataType;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

/**
 * Cell editor for showing matrix value items
 *
 * @author Achim Zielesny
 */
public class ValueItemCellEditorShow extends AbstractCellEditor implements TableCellEditor, FocusListener, MouseListener {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Viewer start label
     */
    private JLabel viewerStartLabel;
    /**
     * standard label
     */
    private JLabel standardLabel;
    /**
     * Current table
     */
    private JTable table;
    /**
     * Current value item
     */
    private ValueItem valueItem;
    /**
     * Current value item type;
     */
    private ValueItemEnumDataType valueItemType;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000059L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public ValueItemCellEditorShow() {
        this.standardLabel = new JLabel();

        // <editor-fold defaultstate="collapsed" desc="this.editorStartLabel">

        this.viewerStartLabel = new JLabel();
        this.viewerStartLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.viewerStartLabel.addFocusListener(this);
        this.viewerStartLabel.addMouseListener(this);
        this.viewerStartLabel.setText(GuiMessage.get("Information.ShowStructure"));

        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns value of cell editor
     *
     * @return Value of cell editor
     */
    public Object getCellEditorValue() {
        switch (this.valueItemType) {
            case MOLECULAR_STRUCTURE:
            case MONOMER_STRUCTURE:

                // <editor-fold defaultstate="collapsed" desc="Molecular structure">

                return this.valueItem.getValue();

            // </editor-fold>

            default:

                // <editor-fold defaultstate="collapsed" desc="Default (this.standardLabel.getText())">

                return this.standardLabel.getText();

            // </editor-fold>

        }
    }

    /**
     * Returns cell editor component
     *
     * @param aTable Table
     * @param aValue Value
     * @param anIsSelected Flag
     * @param aRowIndex Row index
     * @param aColumnIndex Column index
     * @return Cell editor component or null if not possible
     */
    public Component getTableCellEditorComponent(JTable aTable, Object aValue, boolean anIsSelected, int aRowIndex, int aColumnIndex) {

        // <editor-fold defaultstate="collapsed" desc="Get value item and column type format of matrix">

        this.valueItem = null;
        ValueItemDataTypeFormat tmpTypeFormat = null;
        try {
            this.valueItem = ((ValueItemTableModelMatrixShow) aTable.getModel()).getValueItem();
            tmpTypeFormat = this.valueItem.getTypeFormat(aRowIndex, aColumnIndex);
        } catch (Exception anException) {
            ModelUtils.appendToLogfile(true, anException);
            return null;
        }
        if (tmpTypeFormat == null) {
            return null;
        } else {
            this.valueItemType = tmpTypeFormat.getDataType();
        }

        // </editor-fold>

        switch (this.valueItemType) {
            case MOLECULAR_STRUCTURE:
            case MONOMER_STRUCTURE:

                // <editor-fold defaultstate="collapsed" desc="Structure (return this.editorStartLabel)">

                // NOTE: Value item has already aValue
                this.table = aTable;
                return this.viewerStartLabel;

            // </editor-fold>

            default:

                // <editor-fold defaultstate="collapsed" desc="Default (return this.standardLabel)">

                // NOTE: Value item has already aValue
                this.standardLabel.setText(aValue.toString());
                return this.standardLabel;

            // </editor-fold>

        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public events for this.editorStartLabel">
    // <editor-fold defaultstate="collapsed" desc="FocusListener events for this.editorStartLabel">
    /**
     * Standard focus listener method
     *
     * @param aFocusEvent Event
     * @throws IllegalArgumentException Thrown if value item type is illegal
     */
    public void focusGained(FocusEvent aFocusEvent) throws IllegalArgumentException {
        this.startViewer();
    }

    /**
     * Standard focus listener method
     *
     * @param aFocusEvent Event
     */
    public void focusLost(FocusEvent aFocusEvent) {
        // Do nothing
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="MouseListener events for this.editorStartLabel">
    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mouseClicked(MouseEvent aMouseEvent) {
        this.startViewer();
    }

    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mouseReleased(MouseEvent aMouseEvent) {
        // Do nothing
    }

    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mouseEntered(MouseEvent aMouseEvent) {
        // Do nothing
    }

    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mousePressed(MouseEvent aMouseEvent) {
        // Do nothing
    }

    /**
     * Standard mouse listener method
     *
     * @param aMouseEvent Event
     */
    public void mouseExited(MouseEvent aMouseEvent) {
        // Do nothing
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Starts viewer
     */
    private void startViewer() {
        // Set editorStartLabel text: Edit in progress
        this.viewerStartLabel.setText(GuiMessage.get("Information.ShowStructureInProgress"));

        // Get monomers
        HashMap<String, String> tmpAvailableMonomers = null;
        ValueItem tmpMonomerTableValueItem = this.valueItem.getValueItemContainer().getValueItem("MonomerTable");
        if (tmpMonomerTableValueItem != null && tmpMonomerTableValueItem.isActive()) {
            tmpAvailableMonomers = new HashMap<String, String>(tmpMonomerTableValueItem.getMatrixRowCount());
            for (int k = 0; k < tmpMonomerTableValueItem.getMatrixRowCount(); k++) {
                // Monomers in molecular structures start with a # character
                tmpAvailableMonomers.put("#" + tmpMonomerTableValueItem.getValue(k, 0), tmpMonomerTableValueItem.getValue(k, 1));
            }
        }

        switch (this.valueItemType) {
            case MOLECULAR_STRUCTURE:
                // Show protein/molecular structure show dialog (Parameter false)
                if (this.valueItem.getValueItemMatrixElement().hasProteinData()) {
                    DialogProteinShow.show(this.valueItem.getValueItemMatrixElement().getProteinData());
                } else {
                    DialogStructureShow.show(this.valueItem, false, StandardParticleInteractionData.getInstance().getAvailableParticles(), tmpAvailableMonomers);
                }
                break;
            case MONOMER_STRUCTURE:
                // Show monomer show dialog (Parameter true)
                DialogStructureShow.show(this.valueItem, true, StandardParticleInteractionData.getInstance().getAvailableParticles(), null);
                break;
            default:
                throw new IllegalArgumentException("Illegal value item type: " + this.valueItemType.name());
        }
        // Set viewerStartLabel text: Viewer
        this.viewerStartLabel.setText(GuiMessage.get("Information.ShowStructure"));
        // IMPORTANT: Request focus on table again
        this.table.requestFocusInWindow();
        // IMPORTANT: Stop renderer
        fireEditingStopped();
    }
    // </editor-fold>
}