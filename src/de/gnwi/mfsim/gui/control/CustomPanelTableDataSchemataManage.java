/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2021  Achim Zielesny (achim.zielesny@googlemail.com)
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

import javax.swing.JButton;
import de.gnwi.mfsim.gui.message.GuiMessage;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

/**
 * Custom table-data schemata management panel
 *
 * @author Achim Zielesny
 */
public class CustomPanelTableDataSchemataManage extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JButton mergeSchemataButton;
    private JButton clearSchemataButton;
    private JButton removeSchemataButton;
    private JButton saveSchemataButton;
    private JButton loadSchemataButton;
    private JButton editSchemaButton;
    private JButton viewSchemaButton;
    private JScrollPane scrollPanel;
    private SpringLayout springLayout;
    private JList tableDataSchemataList;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000064L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelTableDataSchemataManage() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        {
            this.scrollPanel = new JScrollPane();
            this.scrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.add(this.scrollPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.scrollPanel, -40, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.scrollPanel, 0, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.scrollPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.scrollPanel, 0, SpringLayout.WEST, this);
            {
                this.tableDataSchemataList = new JList();
                this.tableDataSchemataList.setToolTipText(GuiMessage.get("CustomPanelTableDataSchemataManage.tableDataSchemataList.toolTipText")); 
                this.tableDataSchemataList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                this.scrollPanel.setViewportView(this.tableDataSchemataList);
            }
        }
        {
            this.viewSchemaButton = new JButton();
            this.viewSchemaButton.setToolTipText(GuiMessage.get("CustomPanelTableDataSchemataManage.viewSchemaButton.toolTipText")); 
            this.viewSchemaButton.setText(GuiMessage.get("CustomPanelTableDataSchemataManage.viewSchemaButton.text")); 
            this.add(this.viewSchemaButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.viewSchemaButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.viewSchemaButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.viewSchemaButton, 80, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.viewSchemaButton, 0, SpringLayout.WEST, this);
        }
        {
            this.editSchemaButton = new JButton();
            this.editSchemaButton.setToolTipText(GuiMessage.get("CustomPanelTableDataSchemataManage.editSchemaButton.toolTipText")); 
            this.editSchemaButton.setText(GuiMessage.get("CustomPanelTableDataSchemataManage.editSchemaButton.text")); 
            this.add(this.editSchemaButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.editSchemaButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.editSchemaButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.editSchemaButton, 160, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.editSchemaButton, 80, SpringLayout.WEST, this);
        }
        {
            this.loadSchemataButton = new JButton();
            this.loadSchemataButton.setToolTipText(GuiMessage.get("CustomPanelTableDataSchemataManage.loadSchemataButton.toolTipText")); 
            this.loadSchemataButton.setText(GuiMessage.get("CustomPanelTableDataSchemataManage.loadSchemataButton.text")); 
            this.add(this.loadSchemataButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.loadSchemataButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.loadSchemataButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.loadSchemataButton, 260, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.loadSchemataButton, 180, SpringLayout.WEST, this);
        }
        {
            this.saveSchemataButton = new JButton();
            this.saveSchemataButton.setToolTipText(GuiMessage.get("CustomPanelTableDataSchemataManage.saveSchemataButton.toolTipText")); 
            this.saveSchemataButton.setText(GuiMessage.get("CustomPanelTableDataSchemataManage.saveSchemataButton.text")); 
            this.add(this.saveSchemataButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.saveSchemataButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.saveSchemataButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.saveSchemataButton, 340, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.saveSchemataButton, 260, SpringLayout.WEST, this);
        }
        {
            this.mergeSchemataButton = new JButton();
            this.mergeSchemataButton.setToolTipText(GuiMessage.get("CustomPanelTableDataSchemataManage.mergeSchemataButton.toolTipText")); 
            this.mergeSchemataButton.setText(GuiMessage.get("CustomPanelTableDataSchemataManage.mergeSchemataButton.text")); 
            this.add(this.mergeSchemataButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.mergeSchemataButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.mergeSchemataButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.mergeSchemataButton, 420, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.mergeSchemataButton, 340, SpringLayout.WEST, this);
        }
        {
            this.clearSchemataButton = new JButton();
            this.clearSchemataButton.setToolTipText(GuiMessage.get("CustomPanelTableDataSchemataManage.clearSchemataButton.toolTipText")); 
            this.clearSchemataButton.setText(GuiMessage.get("CustomPanelTableDataSchemataManage.clearSchemataButton.text")); 
            this.add(this.clearSchemataButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.clearSchemataButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.clearSchemataButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.clearSchemataButton, -80, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.clearSchemataButton, -160, SpringLayout.EAST, this);
        }
        {
            this.removeSchemataButton = new JButton();
            this.removeSchemataButton.setToolTipText(GuiMessage.get("CustomPanelTableDataSchemataManage.removeSchemataButton.toolTipText")); 
            this.removeSchemataButton.setText(GuiMessage.get("CustomPanelTableDataSchemataManage.removeSchemataButton.text")); 
            this.add(this.removeSchemataButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.removeSchemataButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.removeSchemataButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.removeSchemataButton, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.removeSchemataButton, -80, SpringLayout.EAST, this);
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * TableDataSchemataList
     * 
     * @return TableDataSchemataList
     */
    public JList getTableDataSchemataList() {
        return tableDataSchemataList;
    }

    /**
     * ViewSchemaButton
     * 
     * @return ViewSchemaButton
     */
    public JButton getViewSchemaButton() {
        return viewSchemaButton;
    }

    /**
     * EditSchemaButton
     * 
     * @return EditSchemaButton
     */
    public JButton getEditSchemaButton() {
        return editSchemaButton;
    }

    /**
     * LoadSchemataButton
     * 
     * @return LoadSchemataButton
     */
    public JButton getLoadSchemataButton() {
        return loadSchemataButton;
    }

    /**
     * SaveSchemataButton
     * 
     * @return SaveSchemataButton
     */
    public JButton getSaveSchemataButton() {
        return saveSchemataButton;
    }

    /**
     * MergeSchemataButton
     * 
     * @return MergeSchemataButton
     */
    public JButton getMergeSchemataButton() {
        return mergeSchemataButton;
    }

    /**
     * ClearSchemataButton
     * 
     * @return ClearSchemataButton
     */
    public JButton getClearSchemataButton() {
        return clearSchemataButton;
    }

    /**
     * RemoveSchemataButton
     * 
     * @return RemoveSchemataButton
     */
    public JButton getRemoveSchemataButton() {
        return removeSchemataButton;
    }
    // </editor-fold>
}
