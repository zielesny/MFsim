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

import de.gnwi.mfsim.gui.message.GuiMessage;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;

/**
 * Custom panel for edit of compartments
 *
 * @author Achim Zielesny
 */
public class CustomPanelCompartmentEdit extends JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    private JButton editCompartmentNameButton;
    private JButton addCompartmentXyLayerButton;
    private JButton addCompartmentSphereButton;
    private JButton removeCompartmentButton;
    private CustomPanelValueItemEdit customValueItemEditPanel;
    private SpringLayout springLayout;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable class. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. If no
     * match is found, then an InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000032L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Create the panel
     */
    public CustomPanelCompartmentEdit() {
        super();
        this.springLayout = new SpringLayout();
        setLayout(this.springLayout);

        // <editor-fold defaultstate="collapsed" desc="customPanelValueItemEdit">

        {
            this.customValueItemEditPanel = new CustomPanelValueItemEdit();
            final TitledBorder selectedFeaturePanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage
                    .get("CustomPanelCompartmentEdit.selectedFeaturePanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, 
                    GuiDefinitions.PANEL_TITLE_COLOR);
            this.customValueItemEditPanel.getSelectedFeaturePanel().setBorder(selectedFeaturePanelTitledBorder);
            final TitledBorder featureOverviewPanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage
                    .get("CustomPanelCompartmentEdit.featureOverviewPanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, 
                    GuiDefinitions.PANEL_TITLE_COLOR);
            this.customValueItemEditPanel.getFeatureOverviewPanel().setBorder(featureOverviewPanelTitledBorder);
            add(this.customValueItemEditPanel);
            this.springLayout.putConstraint(SpringLayout.EAST, this.customValueItemEditPanel, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.customValueItemEditPanel, 0, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.customValueItemEditPanel, -40, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.customValueItemEditPanel, 0, SpringLayout.NORTH, this);
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="editCompartmentNameButton">
        {
            this.editCompartmentNameButton = new JButton();
            this.editCompartmentNameButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentEdit.editCompartmentNameButton.toolTipText")); 
            this.editCompartmentNameButton.setText(GuiMessage.get("CustomPanelCompartmentEdit.editCompartmentNameButton.text")); 
            add(this.editCompartmentNameButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.editCompartmentNameButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.editCompartmentNameButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.editCompartmentNameButton, 80, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.editCompartmentNameButton, 0, SpringLayout.WEST, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="addCompartmentSphereButton">
        {
            this.addCompartmentSphereButton = new JButton();
            this.addCompartmentSphereButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentEdit.addCompartmentSphereButton.toolTipText")); 
            this.addCompartmentSphereButton.setText(GuiMessage.get("CustomPanelCompartmentEdit.addCompartmentSphereButton.text")); 
            add(this.addCompartmentSphereButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.addCompartmentSphereButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.addCompartmentSphereButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.addCompartmentSphereButton, 280, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.addCompartmentSphereButton, 80, SpringLayout.WEST, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="addCompartmentXyLayerButton">
        {
            this.addCompartmentXyLayerButton = new JButton();
            this.addCompartmentXyLayerButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentEdit.addCompartmentXyLayerButton.toolTipText")); 
            this.addCompartmentXyLayerButton.setText(GuiMessage.get("CustomPanelCompartmentEdit.addCompartmentXyLayerButton.text")); 
            add(this.addCompartmentXyLayerButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.addCompartmentXyLayerButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.addCompartmentXyLayerButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.addCompartmentXyLayerButton, 480, SpringLayout.WEST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.addCompartmentXyLayerButton, 280, SpringLayout.WEST, this);
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="removeCompartmentButton">
        {
            this.removeCompartmentButton = new JButton();
            this.removeCompartmentButton.setToolTipText(GuiMessage.get("CustomPanelCompartmentEdit.removeCompartmentButton.toolTipText")); 
            this.removeCompartmentButton.setText(GuiMessage.get("CustomPanelCompartmentEdit.removeCompartmentButton.text")); 
            add(this.removeCompartmentButton);
            this.springLayout.putConstraint(SpringLayout.SOUTH, this.removeCompartmentButton, 0, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.NORTH, this.removeCompartmentButton, -35, SpringLayout.SOUTH, this);
            this.springLayout.putConstraint(SpringLayout.EAST, this.removeCompartmentButton, 0, SpringLayout.EAST, this);
            this.springLayout.putConstraint(SpringLayout.WEST, this.removeCompartmentButton, -80, SpringLayout.EAST, this);
        }
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    public JButton getEditCompartmentNameButton() {
        return editCompartmentNameButton;
    }

    public JButton getAddCompartmentSphereButton() {
        return addCompartmentSphereButton;
    }

    public JButton getAddCompartmentXyLayerButton() {
        return addCompartmentXyLayerButton;
    }

    public JButton getRemoveCompartmentButton() {
        return removeCompartmentButton;
    }

    public CustomPanelValueItemEdit getCustomValueItemEditPanel() {
        return this.customValueItemEditPanel;
    }
    // </editor-fold>

}
