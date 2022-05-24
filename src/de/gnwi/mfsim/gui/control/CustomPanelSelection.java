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

import javax.swing.*;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

/**
 * Custom panel for selection
 *
 * @author Achim Zielesny
 */
public class CustomPanelSelection extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JList list;
    private JScrollPane scrollPanel;
    private CustomPanelFilter filterPanel;
    private SpringLayout springLayout;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000022L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelSelection() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        // <editor-fold defaultstate="collapsed" desc="filterPanel">
        {
            this.filterPanel = new CustomPanelFilter();
            this.add(this.filterPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.filterPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.filterPanel, 0, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.filterPanel, 65, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.filterPanel, 0, SpringLayout.NORTH, this);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="scrollPanel & list">
        {
            this.scrollPanel = new JScrollPane();
            this.scrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.add(this.scrollPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.scrollPanel, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.scrollPanel, 70, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.scrollPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.scrollPanel, 0, SpringLayout.WEST, this);
            {
                this.list = new JList();
                this.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                this.scrollPanel.setViewportView(this.list);
            }
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * FilterPanel
     * 
     * @return FilterPanel
     */
    public CustomPanelFilter getFilterPanel() {
        return filterPanel;
    }

    /**
     * List
     * 
     * @return List
     */
    public JList getList() {
        return list;
    }
    // </editor-fold>
    
}
