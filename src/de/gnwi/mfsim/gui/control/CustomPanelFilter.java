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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Input panel for paths with browse and input button
 *
 * @author Achim Zielesny
 */
public class CustomPanelFilter extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JPanel infoPanel;
    private JPanel actionsPanel;
    private JButton clearButton;
    private JButton editButton;
    private JLabel filterInformationLabel;
    private SpringLayout springLayout;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000006L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="CustomPanelFilter constructor">
    /**
     * Create the panel
     */
    public CustomPanelFilter() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);
        final TitledBorder titledBorder = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), GuiMessage.get("CustomPanelFilter.titledBorder.title"), 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR);
        setBorder(titledBorder);

        {
            this.infoPanel = new JPanel();
            final BorderLayout infoPanelBorderLayout = new BorderLayout();
            infoPanelBorderLayout.setHgap(5);
            this.infoPanel.setLayout(infoPanelBorderLayout);
            this.add(this.infoPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.infoPanel, 0, SpringLayout.SOUTH, this);
            springLayout.putConstraint(SpringLayout.NORTH, infoPanel, 0, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.infoPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.infoPanel, 5, SpringLayout.WEST, this);

            // <editor-fold defaultstate="collapsed" desc="filterInformationLabel">

            {
                this.filterInformationLabel = new JLabel();
                this.infoPanel.add(this.filterInformationLabel);
                this.filterInformationLabel.setText(GuiMessage.get("CustomPanelFilter.filterInformationLabel.text")); 
            }

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="actionsPanel">

            {
                this.actionsPanel = new JPanel();
                final BorderLayout actionsPanelBorderLayout = new BorderLayout();
                this.actionsPanel.setLayout(actionsPanelBorderLayout);
                this.infoPanel.add(this.actionsPanel, BorderLayout.EAST);

                // <editor-fold defaultstate="collapsed" desc="clearButton">

                {
                    this.clearButton = new JButton();
                    clearButton.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent arg0) {
                        }
                    });
                    this.clearButton.setText(GuiMessage.get("CustomPanelFilter.clearButton.text")); 
                    this.clearButton.setMaximumSize(new Dimension(80, 35));
                    this.clearButton.setMinimumSize(new Dimension(80, 35));
                    this.clearButton.setPreferredSize(new Dimension(80, 35));
                    this.actionsPanel.add(this.clearButton);
                }

                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="editButton">

                {
                    this.editButton = new JButton();
                    this.editButton.setText(GuiMessage.get("CustomPanelFilter.editButton.text")); 
                    this.editButton.setMaximumSize(new Dimension(80, 35));
                    this.editButton.setMinimumSize(new Dimension(80, 35));
                    this.editButton.setPreferredSize(new Dimension(80, 35));
                    this.actionsPanel.add(this.editButton, BorderLayout.EAST);
                }

                // </editor-fold>

            }

            // </editor-fold>

        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public JLabel getFilterInformationLabel() {
        return this.filterInformationLabel;
    }

    public JButton getEditButton() {
        return this.editButton;
    }

    public JButton getClearButton() {
        return this.clearButton;
    }
    // </editor-fold>
}
