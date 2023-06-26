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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Panel for slicer graphics
 *
 * @author Achim Zielesny
 */
public class CustomPanelSingleSlicer extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JLabel generalInfoLabel;
    /**
     * GUI element
     */
    private CustomPanelSimulationBoxSlicer simulationBoxPanel;
    /**
     * GUI element
     */
    private SpringLayout springLayout;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000048L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     */
    public CustomPanelSingleSlicer() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);
        // <editor-fold defaultstate="collapsed" desc="simulationBoxPanel">
        {
            this.simulationBoxPanel = new CustomPanelSimulationBoxSlicer();
            this.add(this.simulationBoxPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.simulationBoxPanel, -5, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.simulationBoxPanel, 5, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.simulationBoxPanel, -5, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.simulationBoxPanel, 5, SpringLayout.NORTH, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="generalInfoLabel">
        {
            this.generalInfoLabel = new JLabel();
            this.generalInfoLabel.setText(GuiMessage.get("CustomPanelSlicer.generalInfoLabel.text")); 
            this.generalInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.generalInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
            add(this.generalInfoLabel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.generalInfoLabel, -10, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.generalInfoLabel, 10, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.generalInfoLabel, -10, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.generalInfoLabel, -35, SpringLayout.SOUTH, this);
        }
        // </editor-fold>
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * SimulationBoxPanel
     * 
     * @return SimulationBoxPanel
     */
    public CustomPanelSimulationBoxSlicer getSimulationBoxPanel() {
        return simulationBoxPanel;
    }
    // </editor-fold>
}
