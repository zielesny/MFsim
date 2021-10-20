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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

/**
 * Custom panel for compartments
 *
 * @author Achim Zielesny
 */
public class CustomPanelCompartment extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private CustomPanelCompartmentGraphics compartmentGraphicsPanel;

    private CustomPanelCompartmentEdit compartmentEditPanel;

    private SpringLayout graphicsPanelSpringLayout;

    private SpringLayout parameterPanelSpringLayout;

    private JPanel graphicsPanel;

    private JPanel parameterPanel;

    private JTabbedPane mainTabbedPanel;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000036L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the panel
     */
    public CustomPanelCompartment() {
        super();
        setLayout(new BorderLayout());
        {
            this.mainTabbedPanel = new JTabbedPane();
            add(this.mainTabbedPanel, BorderLayout.CENTER);
            {
                this.parameterPanel = new JPanel();
                this.parameterPanelSpringLayout = new SpringLayout();
                this.parameterPanel.setLayout(this.parameterPanelSpringLayout);
                this.mainTabbedPanel.addTab("Compartments", null, this.parameterPanel, "Edit Compartments");
                {
                    this.compartmentEditPanel = new CustomPanelCompartmentEdit();
                    this.parameterPanel.add(this.compartmentEditPanel);
                    this.parameterPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentEditPanel, -10,
                            SpringLayout.EAST, this.parameterPanel);
                    this.parameterPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentEditPanel, 10,
                            SpringLayout.WEST, this.parameterPanel);
                    this.parameterPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentEditPanel, -10,
                            SpringLayout.SOUTH, this.parameterPanel);
                    this.parameterPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentEditPanel, 10,
                            SpringLayout.NORTH, this.parameterPanel);
                }
            }
            {
                this.graphicsPanel = new JPanel();
                this.graphicsPanelSpringLayout = new SpringLayout();
                this.graphicsPanel.setLayout(this.graphicsPanelSpringLayout);
                this.mainTabbedPanel.addTab("Geometry", null, this.graphicsPanel, "Edit Compartment Geometry");
                {
                    this.compartmentGraphicsPanel = new CustomPanelCompartmentGraphics();
                    this.graphicsPanel.add(this.compartmentGraphicsPanel);
                    this.graphicsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.compartmentGraphicsPanel,
                            0, SpringLayout.SOUTH, this.graphicsPanel);
                    this.graphicsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.compartmentGraphicsPanel,
                            0, SpringLayout.NORTH, this.graphicsPanel);
                    this.graphicsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.compartmentGraphicsPanel,
                            0, SpringLayout.EAST, this.graphicsPanel);
                    this.graphicsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.compartmentGraphicsPanel, 0,
                            SpringLayout.WEST, this.graphicsPanel);
                }
            }
        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * MainTabbedPanel
     * 
     * @return MainTabbedPanel
     */
    public JTabbedPane getMainTabbedPanel() {
        return mainTabbedPanel;
    }

    /**
     * CompartmentGraphicsPanel
     * 
     * @return CompartmentGraphicsPanel
     */
    public CustomPanelCompartmentGraphics getCompartmentGraphicsPanel() {
        return compartmentGraphicsPanel;
    }

    /**
     * CompartmentEditPanel
     * 
     * @return CompartmentEditPanel
     */
    public CustomPanelCompartmentEdit getCompartmentEditPanel() {
        return compartmentEditPanel;
    }
    // </editor-fold>

}
