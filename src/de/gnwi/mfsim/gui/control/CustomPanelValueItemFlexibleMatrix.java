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
package de.gnwi.mfsim.gui.control;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import de.gnwi.mfsim.gui.message.GuiMessage;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Table panel for flexible value item based matrix
 *
 * @author Achim Zielesny
 */
public class CustomPanelValueItemFlexibleMatrix extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JScrollPane matrixTableScrollPanel;

    private SpringLayout springLayout;

    private JTable matrixTable;

    private JPanel infoPanel;

    private JLabel infoLabel;

    private JPanel actionsPanel;

    private JPanel setColumnsPanel;

    private JButton copyRowButton;

    private JButton insertRowButton;

    private JButton removeRowButton;

    private JButton setColumnsButton;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000018L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public CustomPanelValueItemFlexibleMatrix() {
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
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.matrixTableScrollPanel, -55, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.matrixTableScrollPanel, 10, SpringLayout.NORTH, this);
            {
                this.matrixTable = new JTable();

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
                // IMPORTANT: setSurrendersFocusOnKeystroke allows cell editor
                // activation!
                this.matrixTable.setSurrendersFocusOnKeystroke(true);
                this.matrixTable.setCellSelectionEnabled(true);
                this.matrixTable.setColumnSelectionAllowed(true);
                this.matrixTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                this.matrixTableScrollPanel.setViewportView(this.matrixTable);
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="infoPanel">
        {
            this.infoPanel = new JPanel();
            final BorderLayout infoPanelBorderLayout = new BorderLayout();
            infoPanelBorderLayout.setHgap(5);
            this.infoPanel.setLayout(infoPanelBorderLayout);
            this.add(this.infoPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.infoPanel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.infoPanel, 12, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.infoPanel, -5, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.infoPanel, -40, SpringLayout.SOUTH, this);

            // <editor-fold defaultstate="collapsed" desc="setColumnsPanel">
            {
                this.setColumnsPanel = new JPanel();
                final FlowLayout setPanelFlowLayout = new FlowLayout();
                setPanelFlowLayout.setHgap(0);
                setPanelFlowLayout.setVgap(0);
                this.setColumnsPanel.setLayout(setPanelFlowLayout);
                this.infoPanel.add(this.setColumnsPanel, BorderLayout.LINE_START);

                // <editor-fold defaultstate="collapsed" desc="setColumnsButton">
                {
                    this.setColumnsButton = new JButton();
                    this.setColumnsButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixSetColumnsButton.toolTipText")); 
                    this.setColumnsButton.setMaximumSize(new Dimension(80, 35));
                    this.setColumnsButton.setMinimumSize(new Dimension(80, 35));
                    this.setColumnsButton.setPreferredSize(new Dimension(80, 35));
                    this.setColumnsButton.setText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixSetColumnsButton.text")); 
                    this.setColumnsPanel.add(this.setColumnsButton);
                }

                // </editor-fold>
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="infoLabel">
            {
                this.infoLabel = new JLabel();
                this.infoLabel.setToolTipText(GuiMessage.get("CustomPanelValueItemFlexibleMatrix.infoLabel.toolTipText")); 
                this.infoLabel.setOpaque(true);
                this.infoLabel.setHorizontalTextPosition(SwingConstants.LEFT);
                this.infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                this.infoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                this.infoLabel.setText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixInfoLabel.text")); 
                this.infoPanel.add(this.infoLabel, BorderLayout.CENTER);
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="actionsPanel">
            {
                this.actionsPanel = new JPanel();
                final FlowLayout actionsPanelFlowLayout = new FlowLayout();
                actionsPanelFlowLayout.setHgap(0);
                actionsPanelFlowLayout.setVgap(0);
                this.actionsPanel.setLayout(actionsPanelFlowLayout);
                this.infoPanel.add(this.actionsPanel, BorderLayout.LINE_END);

                // <editor-fold defaultstate="collapsed" desc="removeRowButton">
                {
                    this.removeRowButton = new JButton();
                    this.removeRowButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixRemoveRowButton.toolTipText")); 
                    this.removeRowButton.setMaximumSize(new Dimension(80, 35));
                    this.removeRowButton.setMinimumSize(new Dimension(80, 35));
                    this.removeRowButton.setPreferredSize(new Dimension(80, 35));
                    this.removeRowButton.setText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixRemoveRowButton.text")); 
                    this.actionsPanel.add(this.removeRowButton);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="insertRowButton">
                {
                    this.insertRowButton = new JButton();
                    this.insertRowButton.setToolTipText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixInsertRowButton.toolTipText")); 
                    this.insertRowButton.setMaximumSize(new Dimension(80, 35));
                    this.insertRowButton.setMinimumSize(new Dimension(80, 35));
                    this.insertRowButton.setPreferredSize(new Dimension(80, 35));
                    this.insertRowButton.setText(GuiMessage.get("CustomPanelValueItemEdit.selectedFeatureMatrixInsertRowButton.text")); 
                    this.actionsPanel.add(this.insertRowButton);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="copyRowButton">
                {
                    this.copyRowButton = new JButton();
                    this.copyRowButton.setToolTipText(GuiMessage.get("CustomPanelValueItemFlexibleMatrix.copyRowButton.toolTipText")); 
                    this.copyRowButton.setMaximumSize(new Dimension(80, 35));
                    this.copyRowButton.setMinimumSize(new Dimension(80, 35));
                    this.copyRowButton.setPreferredSize(new Dimension(80, 35));
                    this.copyRowButton.setText(GuiMessage.get("CustomPanelValueItemFlexibleMatrix.copyRowButton.text")); 
                    this.actionsPanel.add(this.copyRowButton);
                }

                // </editor-fold>
            }

            // </editor-fold>
        }

        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public JButton getInsertRowButton() {
        return insertRowButton;
    }

    public JButton getRemoveRowButton() {
        return removeRowButton;
    }

    public JButton getSetColumnsButton() {
        return setColumnsButton;
    }

    public JLabel getInfoLabel() {
        return infoLabel;
    }

    public JTable getMatrixTable() {
        return matrixTable;
    }

    public JPanel getActionsPanel() {
        return actionsPanel;
    }

    public JPanel getSetColumnsPanel() {
        return setColumnsPanel;
    }

    public JScrollPane getMatrixTableScrollPanel() {
        return matrixTableScrollPanel;
    }
    
	public JButton getCopyRowButton() {
		return copyRowButton;
	}
    // </editor-fold>

}
