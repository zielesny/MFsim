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

import de.gnwi.mfsim.gui.message.GuiMessage;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Custom description panel
 *
 * @author Achim Zielesny
 */
public class CustomPanelDescription extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JLabel nameLabel;
    /**
     * GUI element
     */
    private JLabel nameInfoLabel;
    /**
     * GUI element
     */
    private JTextArea textArea;
    /**
     * GUI element
     */
    private JScrollPane scrollPanel;
    /**
     * GUI element
     */
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
    static final long serialVersionUID = 1000000000000000027L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelDescription() {
        super();
        this.springLayout = new SpringLayout();
        this.setLayout(this.springLayout);
        {
            this.nameInfoLabel = new JLabel();
            this.nameInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
            this.nameInfoLabel.setText(GuiMessage.get("CustomPanelDescription.nameInfoLabel.text")); 
            this.add(this.nameInfoLabel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.nameInfoLabel, 50, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.nameInfoLabel, 12, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.nameInfoLabel, 45, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.nameInfoLabel, 10, SpringLayout.NORTH, this);
        }
        {
            this.nameLabel = new JLabel();
            this.nameLabel.setText(GuiMessage.get("CustomPanelDescription.nameLabel.text")); 
            this.add(this.nameLabel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.nameLabel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.nameLabel, 60, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.nameLabel, 45, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.nameLabel, 10, SpringLayout.NORTH, this);
        }
        {
            this.scrollPanel = new JScrollPane();
            this.add(this.scrollPanel);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.scrollPanel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.scrollPanel, 50, SpringLayout.NORTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.scrollPanel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.scrollPanel, 10, SpringLayout.WEST, this);
            {
                this.textArea = new JTextArea();
                this.textArea.setWrapStyleWord(true);
                this.textArea.setLineWrap(true);
                this.textArea.setOpaque(false);
                this.textArea.setEditable(false);
                this.scrollPanel.setViewportView(this.textArea);
            }
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * TextArea
     * 
     * @return TextArea
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /**
     * NameLabel
     * 
     * @return NameLabel
     */
    public JLabel getNameLabel() {
        return nameLabel;
    }
    // </editor-fold>
}
