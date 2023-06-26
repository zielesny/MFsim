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
package de.gnwi.mfsim.gui.control;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Table panel for show of value item based matrix
 *
 * @author Achim Zielesny
 */
public class CustomPanelValueItemMatrixShow extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JScrollPane matrixTableScrollPanel;
    /**
     * GUI element
     */
    private SpringLayout springLayout;
    /**
     * GUI element
     */
    private JTable matrixTable;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000024L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelValueItemMatrixShow() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="matrixTableScrollPanel & matrixTable">
        {
            this.matrixTableScrollPanel = new JScrollPane();
            this.matrixTableScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.add(this.matrixTableScrollPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.matrixTableScrollPanel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.matrixTableScrollPanel, 10, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.matrixTableScrollPanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.matrixTableScrollPanel, 10, SpringLayout.NORTH, this);
            {
                this.matrixTable = new JTable();
                this.matrixTable.setRowSelectionAllowed(false);

                // <editor-fold defaultstate="collapsed" desc="Set row margin/height">
                
                this.matrixTable.setRowMargin(GuiDefinitions.TABLE_ROW_MARGIN);
                this.matrixTable.setRowHeight(GuiDefinitions.TABLE_ROW_HEIGHT);

                
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set header height">
                
                ((JLabel) this.matrixTable.getTableHeader().getDefaultRenderer()).setPreferredSize(new Dimension(0, GuiDefinitions.TABLE_HEADER_HEIGHT));

                
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Set header properties">
                
                this.matrixTable.getTableHeader().setReorderingAllowed(false);
                ((JLabel) this.matrixTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

                
                // </editor-fold>
                this.matrixTable.setCellSelectionEnabled(true);
                this.matrixTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                this.matrixTableScrollPanel.setViewportView(this.matrixTable);
            }
        }

        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * MatrixTable
     * 
     * @return MatrixTable
     */
    public JTable getMatrixTable() {
        return matrixTable;
    }
    // </editor-fold>

}
